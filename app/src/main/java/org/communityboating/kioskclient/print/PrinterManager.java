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
import java.util.List;

public class PrinterManager implements IConnectionCallback {

    private StarIoExtManager extManager;

    private static PrinterManager Instance;

    private static PrinterSettings printerSettings = new PrinterSettings(
            ModelCapability.TSP650II,
                    "BT:00:12:F3:24:FA:F2",
                    "",
                    "00:12:F3:24:FA:F2",
                    "Star Micronics",
                    true,
                    384
    );

    public static void init(Context context){
        if(Instance==null) {
            Instance = new PrinterManager(context);
            Instance.extManager.connect(Instance);
        }
    }

    public PrinterManager(Context context){
        PrinterSettings settings = printerSettings;
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
        extManager = new StarIoExtManager(StarIoExtManager.Type.Standard, info.getPortName(), settings.getPortSettings(), 20000, context);
    }

    public static ICommandBuilder getCommandBuilder(){
        return StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPosMobile);
    }

    public static void sendCommands(Context context, ICommandBuilder builder, final Communication.SendCallback mCallback) throws Throwable {
        init(context);
        Instance.sendICommands(context, builder, mCallback);
    }

    private void sendICommands(Context context, ICommandBuilder builder, final Communication.SendCallback mCallback) throws Throwable{
        //StarIoExt.Emulation emulation = ModelCapability.getEmulation(printerSettings.getModelIndex());
        //int paperSize = printerSettings.getPaperSize();
        List<PortInfo> portInfos = null;

        int count=0;

        do{
            portInfos = StarIOPort.searchPrinter("BT:", context);
            count++;
            if(count>1)
                Thread.sleep(500);
        }while(portInfos.isEmpty() && count < 10);

        //String bluetoothPort=AdminConfigProperties.getCBIPrinterBluetoothAddress();

        if(portInfos.isEmpty())
            throw new RuntimeException("No printer ports found");
        PortInfo info = portInfos.get(0);
        final String bluetoothPort=info.getPortName();
        final byte[] commands = builder.getCommands();
        final Context fContext = context;
        final Communication.SendCallback callback = new Communication.SendCallback() {
            int count=0;
            @Override
            public void onStatus(boolean result, Communication.Result communicateResult) {
                if(result) {
                    mCallback.onStatus(result, communicateResult);
                }else {
                    if(count<10) {
                        count++;
                        Communication.sendCommands(fContext, commands, bluetoothPort, printerSettings.getPortSettings(), 10000, fContext, this);
                    }else{
                        mCallback.onStatus(result, communicateResult);
                    }
                }
            }
        };
        SendCommandsThread sendCommandsThread = new SendCommandsThread(new String(), extManager.getPort(), builder.getCommands());
        sendCommandsThread.start();
        //Communication.sendCommands(context, builder.getCommands(), bluetoothPort, printerSettings.getPortSettings(), 10000, context, callback);     // 10000mS!!!
    }

    @Override
    public void onConnected(ConnectResult connectResult) {
        Log.d("printer", "connected : " + connectResult.name());
        Log.d("printer", "connected" + this.extManager.getPrinterOnlineStatus());
        try {
            printAllVariables(this.extManager.getPort().retreiveStatus());
        }catch(Throwable t){
            t.printStackTrace();
        }
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
        Log.d("printer", "connected");
    }

    public class SendCommandsThread extends Thread {

        public SendCommandsThread(Object lock, StarIOPort port, byte[] commands){
            this.lock = lock;
            this.port = port;
            this.commands = commands;
        }

        public Object lock;
        public StarIOPort port;
        public byte[] commands;

        @Override
        public void run() {
            //synchronized (lock) {
                try {
                    Log.d("printer", "started port attempt");
                    StarPrinterStatus status = port.beginCheckedBlock();
                    if (status.offline)
                        throw new StarIOPortException("Printer is offline");
                    Log.d("printer", "printer connected");

                    port.setEndCheckedBlockTimeoutMillis(30000);

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
                } catch (StarIOPortException e) {
                    e.printStackTrace();
                }
            }
        //}

    }

}
