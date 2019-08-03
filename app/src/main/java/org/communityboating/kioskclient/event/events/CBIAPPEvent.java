package org.communityboating.kioskclient.event.events;

import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;

import java.util.Date;

public abstract class CBIAPPEvent {
    public abstract String getEventMessage();
    public CBIAPPEvent(Long eventTimeStamp, String eventTitle, CBIAPPEventType eventType){
        this.eventTimeStamp = eventTimeStamp;
        this.eventTitle = eventTitle;
        this.eventType = eventType;
    }
    public CBIAPPEvent(String eventTitle, CBIAPPEventType eventType){
        this.eventTimeStamp = CBIAPPEventManager.getCurrentTime();
        this.eventTitle = eventTitle;
        this.eventType = eventType;
    }
    CBIAPPEventType eventType;
    String eventTitle;
    Long eventTimeStamp;
    public String getEventTitle(){
        return eventTitle;
    }
    public CBIAPPEventType getEventType(){
        return eventType;
    }
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
