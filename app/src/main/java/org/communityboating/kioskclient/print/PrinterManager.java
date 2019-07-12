package org.communityboating.kioskclient.print;

import android.content.Context;
import android.util.Log;
import android.util.Printer;

import org.communityboating.kioskclient.config.AdminConfigProperties;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ConnectionCallback;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.IConnectionCallback;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starioextension.StarIoExtManager;
import com.starmicronics.starprntsdk.Communication;
import com.starmicronics.starprntsdk.ModelCapability;
import com.starmicronics.starprntsdk.PrinterSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadPoolExecutor;

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

    //Lock for when we are using the StarIO Printer API
    final private Object sendingLock;

    private boolean printerConnected;

    private boolean sendingCommands;

    private boolean unrecoverableError;

    private static PrinterSettings printerSettings = new PrinterSettings(
            ModelCapability.TSP650II,
                    "BT:00:12:F3:24:FA:F2",
                    "",
                    "00:12:F3:24:FA:F2",
                    "Star Micronics",
                    true,
                    384
    );

    /*public static void init(Context context){
        if(Instance==null) {
            Instance = new PrinterManager(context);
            Instance.connectToPrinter();
        }
    }*/

    public PrinterManager(Context context){
        PrinterSettings settings = printerSettings;
        pendingCommands = new Stack<>();
        printerConnected = false;
        sendingCommands = false;
        unrecoverableError = false;
        pendingLock = new Object();
        sendingLock = new Object();
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
        extManager = new StarIoExtManager(StarIoExtManager.Type.Standard, info.getPortName(), settings.getPortSettings(), 2000, context);
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

    public void reconnectToPrinter(){
    }

    public void connectToPrinter(){
        this.extManager.connect(this);
    }

    private void attemptSendCommands() {
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
        Log.d("printer", "connected : " + connectResult.name());
            printerConnected = connectResult != ConnectResult.Failure;
        attemptSendCommands();
        errorHandler.handleConnectResult(connectResult);
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
        printerConnected = false;
        errorHandler.handleDisconnect();
    }

    public static interface SendCommandsCallback{
        public void handleSuccess();
        public void handleError(StarIOPortException e);
        public boolean shouldContinue(StarIOPortException e, int attempts);
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
            synchronized (PrinterManager.this.sendingLock) {
                PrinterManager.this.sendingCommands=true;
                long waitStart = System.currentTimeMillis();
                while(PrinterManager.this.extManager.getPrinterOnlineStatus()!=StarIoExtManager.PrinterStatus.Online && ((System.currentTimeMillis()-waitStart)<PRINT_CONNECT_MAX_WAIT)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
                        if (!callback.shouldContinue(e, attempts)) {
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
            Log.d("printer", "port port : " + PrinterManager.this.extManager.getPrinterOnlineStatus());
            StarIOPort port = PrinterManager.this.extManager.getPort();
            Log.d("printer", "started port attempt");
            if (port == null)
                throw new StarIOPortException("Port invalid or device not connected");
            StarPrinterStatus status = port.beginCheckedBlock();
            if (status.offline)
                throw new StarIOPortException("Printer is offline");
            Log.d("printer", "printer connected");

            port.setEndCheckedBlockTimeoutMillis(3000);

            port.writePort(commands, 0, commands.length);

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

    public static interface PrinterErrorHandler{
        public void handlePrintingError(StarIOPortException portException, boolean isFinal);
        public void handleFatalPrinterError(Throwable cause);
        public void handleConnectResult(ConnectResult result);
        public void handleDisconnect();
    }

}
