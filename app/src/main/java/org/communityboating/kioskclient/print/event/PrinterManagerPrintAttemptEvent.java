package org.communityboating.kioskclient.print.event;

public abstract class PrinterManagerPrintAttemptEvent extends PrinterManagerEvent {
    protected long printJobIndex;
    public PrinterManagerPrintAttemptEvent(String eventType, long printJobIndex) {
        super(eventType);
        this.printJobIndex = printJobIndex;
    }
}
