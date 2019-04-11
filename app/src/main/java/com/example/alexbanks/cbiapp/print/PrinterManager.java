package com.example.alexbanks.cbiapp.print;

import android.content.Context;
import android.util.Printer;

import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.util.List;

public class PrinterManager {

    private static PrinterManager instance;

    private Context context;

    private PortInfo portInfo;

    private PrinterManager(Context context){
        this.context = context;
    }

    public static PrinterManager getInstance(Context context){
        if(instance == null)
            instance = new PrinterManager(context);
        return instance;
    }

    public boolean findAndLoadPort() throws Throwable{
        List<PortInfo> portInfoList = StarIOPort.searchPrinter("BT:", context);
        if(portInfoList.isEmpty())
            return false;
        portInfo = portInfoList.get(0);
        return true;
    }

    public boolean doPrintTest() throws Throwable{
        StarIOPort port = StarIOPort.getPort(portInfo.getPortName(), "", 10000, context);
        StarPrinterStatus status = port.beginCheckedBlock();
        ICommandBuilder commandBuilder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.StarPRNT);
        commandBuilder.beginDocument();
        commandBuilder.append("Hello World.\n".getBytes());
        commandBuilder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        commandBuilder.endDocument();
        byte[] commands = commandBuilder.getCommands();
        port.writePort(commands, 0, commands.length);
        status = port.endCheckedBlock();
        StarIOPort.releasePort(port);
        return !status.offline;
    }

}
