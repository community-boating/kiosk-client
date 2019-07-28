package org.communityboating.kioskclient.event.handler;

import org.communityboating.kioskclient.event.events.CBIAPPEvent;

public interface CBIAPPEventHandler {
    abstract void handleEvent(CBIAPPEvent event);
}
