package org.communityboating.kioskclient.print;

import android.content.Context;
import android.util.Printer;

import org.communityboating.kioskclient.config.AdminConfigProperties;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.Communication;
import com.starmicronics.starprntsdk.ModelCapability;
import com.starmicronics.starprntsdk.PrinterSettings;

import java.util.List;

public class PrinterManager {

    private static PrinterSettings printerSettings = new PrinterSettings(
            ModelCapability.TSP650II,
                    "BT:00:12:F3:24:FA:F2",
                    "",
                    "00:12:F3:24:FA:F2",
                    "Star Micronics",
                    true,
                    384
    );

    public static ICommandBuilder getCommandBuilder(){
        return StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPosMobile);
    }

    public static void sendCommands(Context context, ICommandBuilder builder, final Communication.SendCallback mCallback) throws Throwable{
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
        Communication.sendCommands(context, builder.getCommands(), bluetoothPort, printerSettings.getPortSettings(), 10000, context, callback);     // 10000mS!!!
    }

}
