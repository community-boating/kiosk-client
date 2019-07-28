package org.communityboating.kioskclient.event.events.printermanager;

public class PrinterManagerPrintAttemptSuccessfulEvent extends PrinterManagerPrintAttemptEvent {
    public PrinterManagerPrintAttemptSuccessfulEvent(long printJobIndex) {
        super("Print Attempt Successful", printJobIndex);
    }

    @Override
    public String getEventMessage() {
        return "Print attempt completed successfully (Print Job : " + printJobIndex + ")";
    }
}
