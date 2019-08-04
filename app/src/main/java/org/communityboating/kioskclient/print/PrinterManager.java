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

public class PrinterManager implements IConnectionCallback {

    private StarIoExtManager extManager;

    //private static PrinterManager Instance;

    private Queue<SendCommandsThread> pendingCommands;

    private long printIndexCounter;

    //Lock for when we are modifying command buffer objects
    final private Object pendingLock;

    private boolean printerConnected;

    private PrinterConnectionAttempt connectionAttempt;

    private boolean sendingCommands;

    private boolean unrecoverableError;

    private PrinterManagerSettings printerSettings;

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
        pendingCommands = new LinkedList<>();
        printIndexCounter = 0;
        printerConnected = false;
        sendingCommands = false;
        unrecoverableError = false;
        pendingLock = new Object();
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

    protected void sendICommands(ICommandBuilder builder, final SendCommandsCallback callback) {
        final byte[] commands = builder.getCommands();
        synchronized (pendingLock) {
            SendCommandsThread sendCommandsThread = new SendCommandsThread(builder.getCommands(), callback, printIndexCounter++);
            pendingCommands.add(sendCommandsThread);
        }
        attemptSendCommands();
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
        this.connectionAttempt = new PrinterConnectionAttempt();
        attemptPrinterReconnection();
    }

    public void attemptPrinterReconnection(){
        if(connectionAttempt.startConnectionAttemptIfRequired())
            this.extManager.connect(this);
    }

    //public void connectToPrinter(){
    //    this.extManager.connect(this);
    //}

    private void notifyPendingCommandsOfFailure(){
        synchronized (PrinterManager.this.pendingLock){
            while(!pendingCommands.isEmpty()){
                SendCommandsThread thread = pendingCommands.poll();
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
        if(!connectionAttempt.isComplete()){
            Log.d("printer", "printer still connecting, waiting to send commands");
            return;
        }
        if(!connectionAttempt.isPrinterConnected()){
            Log.d("printer", "attempting to connect to printer");
            //TODO autoConnectOnPrint
            connectToPrinter();
            return;
        }
        synchronized (pendingLock) {
            SendCommandsThread currentThread = pendingCommands.poll();
            currentThread.start();
        }
    }

    final static long PRINT_CONNECT_MAX_WAIT=100000;

    @Override
    public void onConnected(ConnectResult connectResult) {
        synchronized (this.connectionAttempt) {
            connectionAttempt.handleConnectionResult(connectResult);
            int connectionAttempts = connectionAttempt.previousAttempts;
            if(connectionAttempt.isComplete){
                if (connectionAttempt.printerConnectionFailed) {
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_FAILURE_FINAL);
                    CBIAPPEventManager.dispatchEvent(event);
                    notifyPendingCommandsOfFailure();
                }else if(connectionAttempt.printerConnected){
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_SUCCESSFUL);
                    CBIAPPEventManager.dispatchEvent(event);
                    attemptSendCommands();
                }else{
                    throw new RuntimeException("Invalid connection attempt, did not fail or succeed, bad logic somewhere");
                }
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

    @Override
    public void onDisconnected() {
        Log.d("printer", "disconnected");
        //printerConnected = false;
        connectionAttempt.disconnect();
        PrinterManagerEvent event = new PrinterManagerStarIOExtDisconnectEvent();
        CBIAPPEventManager.dispatchEvent(event);
        //errorHandler.handleDisconnect();
    }

    public static interface SendCommandsCallback{
        public void handleSuccess();
        public void handleError(Exception e);
        //public boolean shouldContinue(StarIOPortException e, int attempts);
    }

    public class SendCommandsThread extends Thread {

        public SendCommandsThread(byte[] commands, SendCommandsCallback callback, long printIndex){
            this.commands = commands;
            this.callback = callback;
            this.printIndex = printIndex;
        }

        long printIndex;

        public byte[] commands;

        SendCommandsCallback callback;

        @Override
        public void run() {
            synchronized (PrinterManager.this.extManager) {
                PrinterManager.this.sendingCommands=true;
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
                PrinterManager.this.sendingCommands=false;
            }
            PrinterManager.this.attemptSendCommands();
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
            long timeNow = System.currentTimeMillis();
            StarIoExtManager.PrinterStatus printerStatus = extManager.getPrinterOnlineStatus();
            if(printerStatus != StarIoExtManager.PrinterStatus.Online && printerStatus != StarIoExtManager.PrinterStatus.Offline)
                throw new FatalPrintException("Printer status incorrect : " + printerStatus.name());
            StarIOPort port = PrinterManager.this.extManager.getPort();

            if (port == null)
                throw new FatalPrintException("Port invalid or device not connected");
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
            if(exception != null)
                throw exception;
        }
    }

    public void destroy(){
        extManager.disconnect(this);
    }

    private class PrinterConnectionAttempt{
        public boolean printerConnected;
        public boolean printerConnectionFailed;
        public boolean isComplete;
        public boolean isConnecting;
        public int previousAttempts;
        public PrinterConnectionAttempt(){
            printerConnected=false;
            printerConnectionFailed=false;
            isComplete=false;
            isConnecting=false;
            previousAttempts = 0;
        }
        public boolean startConnectionAttemptIfRequired(){
            synchronized (this) {
                if (isConnecting || isComplete)
                    return false;
                isConnecting=true;
                return true;
            }
        }
        public void startConnectionAttempt(){
            synchronized (this) {
                isConnecting = true;
            }
        }

        public boolean isConnecting(){
            synchronized (this){
                return isConnecting;
            }
        }

        public boolean isComplete(){
            synchronized (this){
                return isComplete;
            }
        }

        public void disconnect(){
            synchronized (this){
                printerConnected=false;
            }
        }

        public boolean isPrinterConnected(){
            synchronized (this){
                return printerConnected;
            }
        }

        public boolean isPrinterConnectionFailed(){
            synchronized (this){
                return printerConnectionFailed;
            }
        }

        public int getConnectionAttempts(){
            synchronized (this){
                return previousAttempts;
            }
        }

        public void handleConnectionResult(ConnectResult result){
            Log.d("derpderp", "derpderp : " + result);
            if(result == ConnectResult.Success){
                printerConnected=true;
                printerConnectionFailed=false;
                isComplete=true;
            }else if(++previousAttempts >= printerSettings.maxExtConnectAttempts || result == ConnectResult.AlreadyConnected){
                    printerConnected=false;
                    printerConnectionFailed=true;
                    isComplete=true;
            }
            isConnecting=false;

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
