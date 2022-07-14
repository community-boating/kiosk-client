package org.communityboating.kioskclient.event.events.printermanager;

public abstract class PrinterManagerPrintAttemptEvent extends PrinterManagerEvent {
    protected long printJobIndex;
    public PrinterManagerPrintAttemptEvent(String eventType, long printJobIndex) {
        super(eventType);
        this.printJobIndex = printJobIndex;
    }
}
