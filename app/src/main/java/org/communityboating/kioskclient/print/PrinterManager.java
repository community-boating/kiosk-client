package org.communityboating.kioskclient.print;

import android.content.Context;
import android.util.Log;

import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.event.events.CBIAPPEventManager;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerPrintAttemptFailureEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerPrintAttemptSuccessfulEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerPrinterStatusUpdateEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerStarIOExtConnectionEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerStarIOExtDisconnectEvent;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.IConnectionCallback;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExtManager;
import com.starmicronics.starioextension.StarIoExtManagerListener;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class PrinterManager implements IConnectionCallback {

    private StarIoExtManager extManager;

    //private static PrinterManager Instance;

    private LinkedBlockingQueue<SendCommandsTask> pendingCommands;

    private ExecutorService postConnectionExecutor;

    private long printIndexCounter;

    private PrinterManagerConnectingLock connectingLock;

    private boolean printerConnected;

    private PrinterConnectionAttempt connectionAttempt;

    private boolean sendingCommands;


    private PrinterManagerSettings printerSettings;

    private PrinterManagerTaskLooper printTaskLooper;

    /*private static PrinterSettings printerSettings = new PrinterSettings(
            ModelCapability.TSP650II,
                    "BT:00:12:F3:24:FA:F2",
                    "",
                    "00:12:F3:24:FA:F2",
                    "Star Micronics",
                    true,
                    384
    );*/

    /*public static void init(Context context){
        if(Instance==null) {
            Instance = new PrinterManager(context);
            Instance.connectToPrinter();
        }
    }*/

    private boolean printerOnline=false;

    private static final StarIoExtManagerListener eventExtManagerListener = new StarIoExtManagerListener() {

        private void sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType eventType){
            sendEvent(eventType, null);
        }

        private void sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType eventType, Object extraData){
            PrinterManagerPrinterStatusUpdateEvent event = new PrinterManagerPrinterStatusUpdateEvent(eventType, extraData);
            CBIAPPEventManager.dispatchEvent(event);
        }

        @Override
        public void onPrinterImpossible() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_IMPOSSIBLE);
        }

        @Override
        public void onPrinterOnline() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_ONLINE);
        }

        @Override
        public void onPrinterOffline() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_OFFLINE);
        }

        @Override
        public void onPrinterPaperReady() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_PAPER_READY);
        }

        @Override
        public void onPrinterPaperNearEmpty() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_PAPER_NEAR_EMPTY);
        }

        @Override
        public void onPrinterPaperEmpty() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_PAPER_EMPTY);
        }

        @Override
        public void onPrinterCoverOpen() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_COVER_OPEN);
        }

        @Override
        public void onPrinterCoverClose() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_COVER_CLOSED);
        }

        @Override
        public void onCashDrawerOpen() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.CASH_DRAWER_OPEN);
        }

        @Override
        public void onCashDrawerClose() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.CASH_DRAWER_CLOSED);
        }

        @Override
        public void onBarcodeReaderImpossible() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.BARCODE_READER_IMPOSSIBLE);
        }

        @Override
        public void onBarcodeReaderConnect() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.BARCODE_READER_CONNECT);
        }

        @Override
        public void onBarcodeReaderDisconnect() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.BARCODE_READER_DISCONNECT);
        }

        @Override
        public void onBarcodeDataReceive(byte[] bytes) {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.BARCODE_READER_RECEIVE_DATA, bytes);
        }

        @Override
        public void onAccessoryConnectSuccess() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.ACCESSORY_CONNECT_SUCCESSFUL);
        }

        @Override
        public void onAccessoryConnectFailure() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.ACCESSORY_CONNECT_FAILURE);
        }

        @Override
        public void onAccessoryDisconnect() {
            sendEvent(PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.ACCESSORY_DISCONNECT);
        }

        @Override
        public void onStatusUpdate(String s) {
            super.onStatusUpdate(s);
        }
    };

    public PrinterManager(Context context){
        loadConfigSettings(context);
        //PrinterSettings settings = printerSettings;
        pendingCommands = new LinkedBlockingQueue<>();
        printIndexCounter = 0;
        printerConnected = false;
        sendingCommands = false;
        connectingLock = new PrinterManagerConnectingLock();
        startPrintTaskLooper();
        postConnectionExecutor = Executors.newSingleThreadExecutor();
        PortInfo info = null;
        try {
            List<PortInfo> portInfos = StarIOPort.searchPrinter("BT:");
            if(portInfos.isEmpty()){
                Log.d("printer", "no bluetooth ports!");
                return;
            }else{
                info = portInfos.get(0);
                Log.d("printer", "found a port!" + info.getPortName());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        extManager = new StarIoExtManager(StarIoExtManager.Type.Standard, info.getPortName(), printerSettings.portSettings, printerSettings.portTimeout, context);
        PrinterManager.this.extManager.setListener(eventExtManagerListener);
    }

    private void startPrintTaskLooper(){
        printTaskLooper = new PrinterManagerTaskLooper();
        printTaskLooper.start();
    }

    private void loadConfigSettings(Context context){
        AdminConfigProperties.loadPropertiesIfRequired(context);
        printerSettings = new PrinterManagerSettings();
        printerSettings.maxPrintAttempts=AdminConfigProperties.getMaxPrintAttempts();
        printerSettings.portSettings=AdminConfigProperties.getStarIOPortSettings();
        printerSettings.portName=AdminConfigProperties.getStarIOPortName();
        printerSettings.portTimeout=AdminConfigProperties.getStarIOPortTimeout();
        printerSettings.checkedBlockTimeout=AdminConfigProperties.getCheckedBlockTimeout();
        printerSettings.maxExtConnectAttempts=AdminConfigProperties.getStarIOExtMaxConnectionAttempts();
    }

    public static ICommandBuilder getCommandBuilder(){
        return StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPosMobile);
    }

    //public static void sendCommands(Context context, ICommandBuilder builder, final SendCommandsCallback callback) throws Throwable {
        //init(context);
    //    Instance.sendICommands(context, builder, callback);
    //}

    protected void sendCommands(byte[] data, final SendCommandsCallback callback) {
        SendCommandsTask sendCommandsThread = new SendCommandsTask(data, callback, printIndexCounter++);
        try {
            pendingCommands.put(sendCommandsThread);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!printTaskLooper.isAlive())
            startPrintTaskLooper();
        //Communication.sendCommands(context, builder.getCommands(), bluetoothPort, printerSettings.getPortSettings(), 10000, context, callback);     // 10000mS!!!
    }

    public boolean hasConnectedPrinter(){
        return printerConnected;
    }

    public boolean hasFatalConnectionError(){
        StarIoExtManager.PrinterStatus status = this.extManager.getPrinterOnlineStatus();
        Log.d("printer", "connected : " + this.printerConnected);
        if(status == null)
            return false;
        else if(status==StarIoExtManager.PrinterStatus.Invalid || status==StarIoExtManager.PrinterStatus.Impossible)
            return true;
        return false;
    }

    public void connectToPrinter(){
        if(this.extManager == null){
            Log.e("Printer Error", "No port, or other fatal error");
            return;
        }
        this.connectionAttempt = new PrinterConnectionAttempt();
        connectingLock.setLocked(true);
        attemptPrinterReconnection();
    }

    public void attemptPrinterReconnection(){
        this.extManager.connect(this);
    }

    //public void connectToPrinter(){
    //    this.extManager.connect(this);
    //}

    /*private void notifyPendingCommandsOfFailure(){
        synchronized (PrinterManager.this.pendingLock){
            while(!pendingCommands.isEmpty()){
                SendCommandsTask thread = null;
                try {
                    thread = pendingCommands.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FatalPrintException exception = new FatalPrintException("Printer connection failure");
                thread.callback.handleError(exception);
                thread.notifyError(exception, true, 1);
            }
        }
    }

    private void attemptSendCommands() {
        Log.d("printer", "printerprinter" + printerConnected);
        synchronized (PrinterManager.this.pendingLock) {
            if (pendingCommands.isEmpty() || sendingCommands)
                return;
        }
        if(!connectionAttempt.isPrinterConnected()){
            Log.d("printer", "attempting to connect to printer");
            connectToPrinter();
            return;
        }
        synchronized (pendingLock) {
            SendCommandsTask currentThread = pendingCommands.poll();
            //currentThread.start();
        }
    }*/

    final static long PRINT_CONNECT_MAX_WAIT=100000;

    @Override
    public void onConnected(ConnectResult connectResult) {
        synchronized (this.connectionAttempt) {
            connectionAttempt.handleConnectionResult(connectResult);
            int connectionAttempts = connectionAttempt.previousAttempts;
            if(!connectionAttempt.isConnecting()){
                if (connectionAttempt.isPrinterFailed()) {
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_FAILURE_FINAL);
                    CBIAPPEventManager.dispatchEvent(event);
                }else if(connectionAttempt.isPrinterConnected()){
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_SUCCESSFUL);
                    CBIAPPEventManager.dispatchEvent(event);
                }else{
                    throw new RuntimeException("Invalid connection attempt, did not fail or succeed, bad logic somewhere");
                }
                connectingLock.setLocked(false);
            }else{
                PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_FAILED);
                CBIAPPEventManager.dispatchEvent(event);
                attemptPrinterReconnection();
            }
        }
        //Log.d("printer", "connected : " + connectResult.name());
        //    printerConnected = connectResult != ConnectResult.Failure;
        //attemptSendCommands();
        //errorHandler.handleConnectResult(connectResult);
    }

    public void printAllVariables(StarPrinterStatus status){
        Field[] fields = StarPrinterStatus.class.getDeclaredFields();
        for(Field f : fields){
            try {
                Log.d("printer", "connected : " + f.getName() + " : " + f.get(status));
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }

    public interface RetrievePortStatusAsyncCallback {
        public void handlePortStatusError(Exception e);
        public void handlePortStatus(StarIoExtManager.PrinterStatus status, StarIoExtManager.PrinterPaperStatus paperStatus, StarIoExtManager.PrinterCoverStatus coverStatus);
    }

    public class RetrievePortStatusTask implements Runnable{

        private RetrievePortStatusAsyncCallback callback;

        public RetrievePortStatusTask(RetrievePortStatusAsyncCallback callback){
            this.callback = callback;
        }

        @Override
        public void run() {
            connectingLock.waitOnLock();
            if(connectionAttempt == null){
                callback.handlePortStatusError(new FatalPrintException("ConnectionAttempt NULL"));
                return;
            }
            if(connectionAttempt.isPrinterFailed()){
                FatalPrintException exception = new FatalPrintException("Printer connection failed");
                callback.handlePortStatusError(exception);
                return;
            }
            synchronized (extManager) {
                try {
                    StarIoExtManager.PrinterStatus status = extManager.getPrinterOnlineStatus();
                    StarIoExtManager.PrinterPaperStatus paperStatus = extManager.getPrinterPaperReadyStatus();
                    StarIoExtManager.PrinterCoverStatus coverStatus = extManager.getPrinterCoverOpenStatus();
                    callback.handlePortStatus(status, paperStatus, coverStatus);
                } catch (Exception e) {
                    callback.handlePortStatusError(e);
                }
            }
        }
    }

    public void retrievePortStatusAsync(final RetrievePortStatusAsyncCallback callback){
        RetrievePortStatusTask task = new RetrievePortStatusTask(callback);
        postConnectionExecutor.execute(task);
    }

    @Override
    public void onDisconnected() {
        Log.d("printer", "disconnected");
        //printerConnected = false;
        connectionAttempt.disconnect();
        PrinterManagerEvent event = new PrinterManagerStarIOExtDisconnectEvent();
        CBIAPPEventManager.dispatchEvent(event);
        //errorHandler.handleDisconnect();
    }

    public interface SendCommandsCallback{
        public void handleSuccess();
        public void handleError(Exception e);
        //public boolean shouldContinue(StarIOPortException e, int attempts);
    }

    public class SendCommandsTask implements Runnable {
        public SendCommandsTask(byte[] commands, SendCommandsCallback callback, long printIndex){
            this.commands = commands;
            this.callback = callback;
            this.printIndex = printIndex;
        }

        long printIndex;

        public byte[] commands;

        SendCommandsCallback callback;

        @Override
        public void run() {
            /*long waitStart = System.currentTimeMillis();
            while(PrinterManager.this.extManager.getPrinterOnlineStatus()!=StarIoExtManager.PrinterStatus.Online && ((System.currentTimeMillis()-waitStart)<PRINT_CONNECT_MAX_WAIT)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            int attempts = 0;
            while (true) {
                attempts++;
                try {
                    attemptPrint();
                    callback.handleSuccess();
                    notifySuccess();
                    break;
                } catch (Exception e) {
                    Log.d("printer", "error printing...");
                    e.printStackTrace();
                    boolean isFinal = e instanceof FatalPrintException || attempts >= printerSettings.maxPrintAttempts;
                    notifyError(e, isFinal, attempts);
                    if (isFinal) {
                        callback.handleError(e);
                        break;
                    }else{
                        try {
                            //TODO add config for this variable
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }

        private void notifySuccess(){
            PrinterManagerPrintAttemptSuccessfulEvent event = new PrinterManagerPrintAttemptSuccessfulEvent(printIndex);
            CBIAPPEventManager.dispatchEvent(event);
        }

        private void notifyError(Exception e, boolean isFinal, int attempts){
            PrinterManagerPrintAttemptFailureEvent event = new PrinterManagerPrintAttemptFailureEvent(printIndex, e, isFinal, attempts);
            CBIAPPEventManager.dispatchEvent(event);
        }

        private void attemptPrint() throws Exception {
            synchronized (PrinterManager.this.extManager) {
                StarIOPort port = getPortOrThrow();
                StarPrinterStatus status;
                try {
                    status = port.beginCheckedBlock();
                } catch (StarIOPortException e) {
                    status = port.retreiveStatus();
                }

                PrinterException exception = PrinterException.getPrinterExceptionFromStatus(status);

                if (exception != null)
                    throw exception;

                port.writePort(commands, 0, commands.length);

                port.setEndCheckedBlockTimeoutMillis(printerSettings.checkedBlockTimeout);

                Log.d("printer", "wrote data to printer");

                status = port.endCheckedBlock();

                Log.d("printer", "done sending commands");
                exception = PrinterException.getPrinterExceptionFromStatus(status);
                if (exception != null)
                    throw exception;
            }
        }
    }

    private StarIOPort getPortOrThrow() throws Exception {
        StarIoExtManager.PrinterStatus printerStatus = extManager.getPrinterOnlineStatus();
        if(printerStatus != StarIoExtManager.PrinterStatus.Online && printerStatus != StarIoExtManager.PrinterStatus.Offline)
            throw new FatalPrintException("Printer status incorrect : " + printerStatus.name());
        StarIOPort port = PrinterManager.this.extManager.getPort();
        if (port == null)
            throw new FatalPrintException("Port invalid or device not connected");
        return port;
    }

    public void destroy(){
        extManager.disconnect(this);
    }

    public static final int CONNECTION_ATTEMPT_STATUS_CONNECTED=0,
    CONNECTION_ATTEMPT_STATUS_CONNECT_FAILED=1,
    CONNECTION_ATTEMPT_STATUS_CONNECTING=2,
    CONNECTION_ATTEMPT_STATUS_DISCONNECTED=3;

    private class PrinterConnectionAttempt{
        private int status;

        public int previousAttempts;
        public PrinterConnectionAttempt(){
            status = CONNECTION_ATTEMPT_STATUS_CONNECTING;
            previousAttempts = 0;
        }

        public boolean isConnecting(){
            return status == CONNECTION_ATTEMPT_STATUS_CONNECTING;
        }

        public void disconnect(){
            status = CONNECTION_ATTEMPT_STATUS_DISCONNECTED;
        }

        public boolean isPrinterConnected(){
            return status == CONNECTION_ATTEMPT_STATUS_CONNECTED;
        }

        public boolean isPrinterFailed(){
            return status == CONNECTION_ATTEMPT_STATUS_CONNECT_FAILED;
        }

        public int getConnectionAttempts(){
            return previousAttempts;
        }

        public void handleConnectionResult(ConnectResult result){
            Log.d("derpderp", "derpderp : " + result);
            if(result == ConnectResult.Success){
                status = CONNECTION_ATTEMPT_STATUS_CONNECTED;
            }else if(++previousAttempts >= printerSettings.maxExtConnectAttempts || result == ConnectResult.AlreadyConnected){
                    status = CONNECTION_ATTEMPT_STATUS_CONNECT_FAILED;
            }else{
                status = CONNECTION_ATTEMPT_STATUS_CONNECTING;
            }

            /*//synchronized (this) {
                if (result == ConnectResult.Failure) {
                    printerConnected = false;
                    printerConnectionFailed = true;
                } else {
                    printerConnected = true;
                    printerConnectionFailed = false;
                }
                isConnecting = false;
            //}*/
        }
    }

    private class PrinterManagerTaskLooper extends Thread {
        @Override
        public void run(){
            while(true) {
                connectingLock.waitOnLock();
                SendCommandsTask task = null;
                try {
                    task = pendingCommands.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                if(connectionAttempt.isPrinterFailed()){
                    FatalPrintException exception = new FatalPrintException("Printer connection failure");
                    task.callback.handleError(exception);
                    task.notifyError(exception, true, 1);
                    continue;
                }
                try {
                    task.run();
                }catch(Throwable t){
                    FatalPrintException exception = new FatalPrintException(t);
                    task.callback.handleError(exception);
                    task.notifyError(exception, true, 1);
                }
            }
        }
    }

    private class PrinterManagerConnectingLock{
        private Object lock;
        private boolean isLocked;
        public PrinterManagerConnectingLock(){
            this.lock=new Object();
            this.isLocked=false;
        }
        public void waitOnLock(){
            synchronized (lock){
                try {
                    while (isLocked) {
                        this.lock.wait();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        public void setLocked(boolean locked){
            synchronized (this.lock){
                this.isLocked=locked;
                if(!locked)
                    this.lock.notifyAll();
            }
        }
    }

    private class PrinterManagerSettings{
        private int maxPrintAttempts;
        private String portName;
        private String portSettings;
        private int portTimeout;
        private int checkedBlockTimeout;
        private int maxExtConnectAttempts;
        private boolean autoConnectOnPrint;
    }

}
