package org.communityboating.kioskclient.event.events;

import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;

public abstract class CBIAPPEvent {
    public abstract CBIAPPEventType getEventType();
    public abstract String getEventTitle();
    public abstract String getEventMessage();
    Long eventTimeStamp;
    public void setEventTimeStamp(Long eventTimeStamp){
        this.eventTimeStamp = eventTimeStamp;
    }
    public Long getEventTimeStamp(){
        return eventTimeStamp;
    }
    public SQLiteEvent getSQLiteEvent(){
        SQLiteEvent event = new SQLiteEvent();
        event.setEventID(null);
        event.setEventTitle(getEventTitle());
        event.setEventMessage(getEventMessage());
        event.setEventType(getEventType());
        event.setEventTimestamp(getEventTimeStamp());
        return event;
    }
}
