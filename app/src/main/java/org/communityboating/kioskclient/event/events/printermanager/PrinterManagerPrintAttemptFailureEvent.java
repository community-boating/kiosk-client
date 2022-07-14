package org.communityboating.kioskclient.event.events.printermanager;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.StarIoExtManager;

public class PrinterManagerPrintAttemptFailureEvent extends PrinterManagerPrintAttemptEvent{

    private Exception portException;
    private int printAttempt;
    boolean isFinalAttempt;

    public PrinterManagerPrintAttemptFailureEvent(long printJobIndex, Exception portException, boolean isFinalAttempt, int printAttempt) {
        super("Print Attempt Event", printJobIndex);
        this.portException = portException;
        this.isFinalAttempt = isFinalAttempt;
        this.printAttempt = printAttempt;
    }

    @Override
    public String getEventMessage() {
        return "Print attempt failed, attempt: " + printAttempt + " final: " + isFinalAttempt + " message: " + portException.getMessage();
    }
}
