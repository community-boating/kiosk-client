package org.communityboating.kioskclient.event.events.printermanager;

import org.communityboating.kioskclient.event.events.CBIAPPEvent;
import org.communityboating.kioskclient.event.events.CBIAPPEventType;

public abstract class PrinterManagerEvent extends CBIAPPEvent {

    public PrinterManagerEvent(String eventTitle){
        super(eventTitle, CBIAPPEventType.EVENT_TYPE_PRINTER);
    }

}
