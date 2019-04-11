package com.example.alexbanks.cbiapp.print;

import android.content.Context;
import android.util.Printer;

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
            9,
                    "BT:00:15:0E:E6:BD:0D",
                    "mini",
                    "00:15:0E:E6:BD:0D",
                    "Star Micronics",
                    true,
                    384
    );

    public static ICommandBuilder getCommandBuilder(){
        return StarIoExt.createCommandBuilder(ModelCapability.getEmulation(printerSettings.getModelIndex()));
    }

    public static void sendCommands(Context context, ICommandBuilder builder, Communication.SendCallback mCallback) throws Throwable{
        //StarIoExt.Emulation emulation = ModelCapability.getEmulation(printerSettings.getModelIndex());
        //int paperSize = printerSettings.getPaperSize();

        Communication.sendCommands(context, builder.getCommands(), printerSettings.getPortName(), printerSettings.getPortSettings(), 10000, context, mCallback);     // 10000mS!!!
    }

}