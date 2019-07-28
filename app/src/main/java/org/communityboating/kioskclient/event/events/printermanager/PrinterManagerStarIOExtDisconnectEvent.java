package org.communityboating.kioskclient.event.events.printermanager;

public class PrinterManagerStarIOExtDisconnectEvent extends PrinterManagerEvent {
    public PrinterManagerStarIOExtDisconnectEvent() {
        super("StarIO Extension Disconnected");
    }

    @Override
    public String getEventMessage() {
        return "StarIO extension manager disconnected";
    }
}
