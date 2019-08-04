package org.communityboating.kioskclient.print;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.stario.StarPrinterStatus;

public class PrinterException extends Exception{

    StarPrinterStatus printerStatus;
    public PrinterException(String message, StarPrinterStatus printerStatus){
        super(message);
        this.printerStatus = printerStatus;
    }

    public static PrinterException getPrinterExceptionFromStatus(StarPrinterStatus status){
        if(status.blackMarkError)
            return new PrinterException("Black mark error", status);
        else if(status.coverOpen)
            return new PrinterException("Printer cover open", status);
        else if(status.cutterError)
            return new PrinterException("Cutter error", status);
        else if(status.headThermistorError)
            return new PrinterException("Head thermistor error", status);
        else if(status.headUpError)
            return new PrinterException("Head up error", status);
        else if(status.mechError)
            return new PrinterException("Mechanical error", status);
        else if(status.overTemp)
            return new PrinterException("Over temperature", status);
        else if(status.pageModeCmdError)
            return new PrinterException("Page mode command error", status);
        else if(status.presenterPaperJamError)
            return new PrinterException("Presenter paper jam error", status);
        else if(status.receiptPaperEmpty)
            return new PrinterException("Printer paper empty", status);
        else if(status.voltageError)
            return new PrinterException("Voltage error", status);
        else if(status.offline)
            return new PrinterException("Printer is offline", status);
        else if(status.unrecoverableError)
            return new PrinterException("Unrecoverable error", status);
        else
            return null;
    }

    public StarPrinterStatus getPrinterStatus(){
        return printerStatus;
    }

}
