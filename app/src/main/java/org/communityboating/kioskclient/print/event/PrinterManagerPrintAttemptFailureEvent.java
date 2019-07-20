package org.communityboating.kioskclient.print.event;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.StarIoExtManager;

public class PrinterManagerPrintAttemptFailureEvent extends PrinterManagerPrintAttemptEvent{

    private StarIOPortException portException;
    private StarIoExtManager.PrinterStatus printerStatus;
    private int printAttempt;
    boolean isFinalAttempt;

    public PrinterManagerPrintAttemptFailureEvent(long printJobIndex) {
        super("Print Attempt Event", printJobIndex);
    }

    @Override
    public String getEventMessage() {
        return null;
    }
}
