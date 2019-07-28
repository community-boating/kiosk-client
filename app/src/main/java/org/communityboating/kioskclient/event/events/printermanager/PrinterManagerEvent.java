package org.communityboating.kioskclient.event.events.printermanager;

public abstract class PrinterManagerEvent implements Comparable<PrinterManagerEvent>{

    public long timeStamp;
    public String eventType;

    public abstract String getEventMessage();

    public PrinterManagerEvent(String eventType){
        this.eventType = eventType;
    }

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(PrinterManagerEvent event){
        return event.timeStamp > this.timeStamp ? 1 : event.timeStamp < this.timeStamp ? -1 : 0;
    }

}
