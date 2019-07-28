package org.communityboating.kioskclient.print;

import android.content.Context;
import android.util.Log;

import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerEvent;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerEventHandler;
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
import java.util.List;
import java.util.Stack;

public class PrinterManager implements IConnectionCallback {

    private PrinterErrorHandler errorHandler = new PrinterErrorHandler() {
        @Override
        public void handlePrintingError(StarIOPortException portException, boolean isFinal) {
        }

        @Override
        public void handleFatalPrinterError(Throwable cause) {
        }

        @Override
        public void handleConnectResult(ConnectResult result) {

        }

        @Override
        public void handleDisconnect() {

        }
    };

    private StarIoExtManager extManager;

    //private static PrinterManager Instance;

    private List<SendCommandsThread> pendingCommands;

    //Lock for when we are modifying command buffer objects
    final private Object pendingLock;

    private boolean printerConnected;

    private PrinterConnectionAttempt connectionAttempt;

    private boolean sendingCommands;

    private boolean unrecoverableError;

    private PrinterManagerEventHandler eventHandler;

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

    public PrinterManager(Context context){
        loadConfigSettings(context);
        eventHandler = new PrinterManagerEventHandler();
        //PrinterSettings settings = printerSettings;
        pendingCommands = new Stack<>();
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
        PrinterManager.this.extManager.setListener(new StarIoExtManagerListener() {
            @Override
            public void onPrinterImpossible() {
                super.onPrinterImpossible();
                Log.d("printer", "printer impossible");
                printerOnline=false;
            }

            @Override
            public void onPrinterOnline() {
                super.onPrinterOnline();
                Log.d("printer", "printer online");
                printerOnline=true;
            }

            @Override
            public void onPrinterOffline() {
                super.onPrinterOffline();
                printerOnline=false;
                Log.d("printer", "printer offline");
            }

            @Override
            public void onPrinterPaperReady() {
                super.onPrinterPaperReady();
                Log.d("printer", "paper ready");
            }

            @Override
            public void onPrinterPaperNearEmpty() {
                super.onPrinterPaperNearEmpty();
                Log.d("printer", "paper empty near");
            }

            @Override
            public void onPrinterPaperEmpty() {
                super.onPrinterPaperEmpty();
                Log.d("printer", "paper empty");
            }

            @Override
            public void onPrinterCoverOpen() {
                super.onPrinterCoverOpen();
                Log.d("printer", "cover open");
            }

            @Override
            public void onPrinterCoverClose() {
                super.onPrinterCoverClose();
                Log.d("printer", "cover close");
            }

            @Override
            public void onCashDrawerOpen() {
                super.onCashDrawerOpen();
                Log.d("printer", "cash open");
            }

            @Override
            public void onCashDrawerClose() {
                super.onCashDrawerClose();
                Log.d("printer", "cash close");
            }

            @Override
            public void onBarcodeReaderImpossible() {
                super.onBarcodeReaderImpossible();
                Log.d("printer", "barcode impossible");
            }

            @Override
            public void onBarcodeReaderConnect() {
                super.onBarcodeReaderConnect();
                Log.d("printer", "barcode connect");
            }

            @Override
            public void onBarcodeReaderDisconnect() {
                super.onBarcodeReaderDisconnect();
                Log.d("printer", "barcode disconnect");
            }

            @Override
            public void onBarcodeDataReceive(byte[] bytes) {
                super.onBarcodeDataReceive(bytes);
                Log.d("printer", "barcode rec");
            }

            @Override
            public void onAccessoryConnectSuccess() {
                super.onAccessoryConnectSuccess();
                Log.d("printer", "accessory connect success");
            }

            @Override
            public void onAccessoryConnectFailure() {
                super.onAccessoryConnectFailure();
                Log.d("printer", "accessory connect failure");
            }

            @Override
            public void onAccessoryDisconnect() {
                super.onAccessoryDisconnect();
                Log.d("printer", "accessory disconnect");
            }

            @Override
            public void onStatusUpdate(String s) {
                super.onStatusUpdate(s);
                Log.d("printer", "status update : " + s);
            }
        });
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

    public void setPrinterErrorHandler(PrinterErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    protected void sendICommands(ICommandBuilder builder, final SendCommandsCallback callback) {
        final byte[] commands = builder.getCommands();
        SendCommandsThread sendCommandsThread = new SendCommandsThread(builder.getCommands(), callback);
        synchronized (pendingLock) {
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

    private void attemptSendCommands() {
        Log.d("printer", "printerprinter" + printerConnected);
        synchronized (PrinterManager.this.pendingLock) {
            if (pendingCommands.isEmpty() || sendingCommands)
                return;
        }
        if(!hasConnectedPrinter()){
            Log.d("printer", "attempting to connect to printer");
            connectToPrinter();
            return;
        }
        synchronized (pendingLock) {
            SendCommandsThread currentThread = pendingCommands.get(0);
            pendingCommands.remove(0);
            currentThread.start();
        }
    }

    final static long PRINT_CONNECT_MAX_WAIT=100000;

    @Override
    public void onConnected(ConnectResult connectResult) {
        synchronized (this.connectionAttempt) {
            connectionAttempt.handleConnectionResult(connectResult);
            int connectionAttempts = connectionAttempt.connectionAttempts;
            if (connectionAttempt.printerConnectionFailed) {
                if (connectionAttempts < printerSettings.maxExtConnectAttempts) {
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_FAILED);
                    eventHandler.dispatchEvent(event);
                    attemptPrinterReconnection();
                } else {
                    PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_FAILURE_FINAL);
                    eventHandler.dispatchEvent(event);
                }
            }else if(connectionAttempt.printerConnected){
                PrinterManagerEvent event = new PrinterManagerStarIOExtConnectionEvent(connectionAttempts, PrinterManagerStarIOExtConnectionEvent.EVENT_RESULT_SUCCESSFUL);
                eventHandler.dispatchEvent(event);
                attemptSendCommands();
            }else{
                throw new RuntimeException("Invalid connection attempt, did not fail or succeed, bad logic somewhere");
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
        eventHandler.dispatchEvent(event);
        //errorHandler.handleDisconnect();
    }

    public static interface SendCommandsCallback{
        public void handleSuccess();
        public void handleError(StarIOPortException e);
        //public boolean shouldContinue(StarIOPortException e, int attempts);
    }

    public class SendCommandsThread extends Thread {

        public SendCommandsThread(byte[] commands, SendCommandsCallback callback){
            this.commands = commands;
            this.callback = callback;
        }

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
                        break;
                    } catch (StarIOPortException e) {
                        Log.d("printer", "error printing...");
                        e.printStackTrace();
                        if (attempts >= printerSettings.maxPrintAttempts) {
                            callback.handleError(e);
                            errorHandler.handlePrintingError(e, true);
                            break;
                        }else{
                            errorHandler.handlePrintingError(e, false);
                        }
                    }
                }
                PrinterManager.this.sendingCommands=false;
            }
            PrinterManager.this.attemptSendCommands();
        }

        private void attemptPrint() throws StarIOPortException {
            long waitStart = System.currentTimeMillis();
            StarIOPort port = PrinterManager.this.extManager.getPort();
            Log.d("printer", "started printing : " + PrinterManager.this.extManager.getPrinterOnlineStatus());
            Log.d("printer", "started port attempt");
            if (port == null)
                throw new StarIOPortException("Port invalid or device not connected");
            StarPrinterStatus status = port.beginCheckedBlock();
            if (status.offline)
                throw new StarIOPortException("Printer is offline");
            Log.d("printer", "printer connected");

            port.writePort(commands, 0, commands.length);

            port.setEndCheckedBlockTimeoutMillis(printerSettings.checkedBlockTimeout);

            Log.d("printer", "wrote data to printer");

            status = port.endCheckedBlock();

            Log.d("printer", "done sending commands");

            if (status.coverOpen) {
                throw new StarIOPortException("Printer cover is open");
            } else if (status.receiptPaperEmpty) {
                throw new StarIOPortException("Receipt paper is empty");
            } else if (status.offline) {
                throw new StarIOPortException("Printer is offline");
            }
        }
    }

    public void destroy(){
        extManager.disconnect(this);
    }

    public interface PrinterErrorHandler{
        public void handlePrintingError(StarIOPortException portException, boolean isFinal);
        public void handleFatalPrinterError(Throwable cause);
        public void handleConnectResult(ConnectResult result);
        public void handleDisconnect();
    }

    private class PrinterConnectionAttempt{
        public boolean printerConnected;
        public boolean printerConnectionFailed;
        public boolean isConnecting;
        public int connectionAttempts;
        public PrinterConnectionAttempt(){
            printerConnected=false;
            printerConnectionFailed=false;
            isConnecting=false;
            connectionAttempts=0;
        }
        public boolean startConnectionAttemptIfRequired(){
            synchronized (this) {
                if (isConnecting || printerConnected)
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
                return connectionAttempts;
            }
        }

        public void handleConnectionResult(ConnectResult result){
            //synchronized (this) {
                if (result == ConnectResult.Failure) {
                    printerConnected = false;
                    printerConnectionFailed = true;
                } else {
                    printerConnected = true;
                    printerConnectionFailed = false;
                }
                isConnecting = false;
                connectionAttempts++;
            //}
        }
    }

    private class PrinterManagerSettings{
        private int maxPrintAttempts;
        private String portName;
        private String portSettings;
        private int portTimeout;
        private int checkedBlockTimeout;
        private int maxExtConnectAttempts;
    }

}
