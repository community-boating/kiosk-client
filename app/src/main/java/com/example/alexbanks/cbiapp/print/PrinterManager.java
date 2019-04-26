package com.example.alexbanks.cbiapp.print;

import android.content.Context;
import android.util.Printer;

import com.example.alexbanks.cbiapp.config.AdminConfigProperties;
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

    public static void sendCommands(Context context, ICommandBuilder builder, Communication.SendCallback mCallback) throws Throwable{
        //StarIoExt.Emulation emulation = ModelCapability.getEmulation(printerSettings.getModelIndex());
        //int paperSize = printerSettings.getPaperSize();
        List<PortInfo> portInfos = StarIOPort.searchPrinter("BT:", context);

        //String bluetoothPort=AdminConfigProperties.getCBIPrinterBluetoothAddress();

        if(portInfos.isEmpty())
            throw new RuntimeException("No printer ports found");
        PortInfo info = portInfos.get(0);
        String bluetoothPort=info.getPortName();

        Communication.sendCommands(context, builder.getCommands(), bluetoothPort, printerSettings.getPortSettings(), 10000, context, mCallback);     // 10000mS!!!
    }

}
