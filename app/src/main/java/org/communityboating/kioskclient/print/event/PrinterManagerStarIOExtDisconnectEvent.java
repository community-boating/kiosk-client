package org.communityboating.kioskclient.print.event;

public class PrinterManagerStarIOExtDisconnectEvent extends PrinterManagerEvent {
    public PrinterManagerStarIOExtDisconnectEvent() {
        super("StarIO Extension Disconnected");
    }

    @Override
    public String getEventMessage() {
        return "StarIO extension manager disconnected";
    }
}
