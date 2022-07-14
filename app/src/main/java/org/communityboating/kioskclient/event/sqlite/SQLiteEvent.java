package org.communityboating.kioskclient.event.sqlite;

import android.database.sqlite.SQLiteDatabase;

import org.communityboating.kioskclient.event.events.CBIAPPEventType;

public class SQLiteEvent {

    Long eventID;
    CBIAPPEventType eventType;
    String eventTitle;
    String eventMessage;
    Long eventTimestamp;

    public Long getEventID() {
        return eventID;
    }

    public CBIAPPEventType getEventType() {
        return eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public void setEventType(CBIAPPEventType eventType) {
        this.eventType = eventType;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Number getValueNumber(String columnName){
        if(columnName==null)
            return null;
        if(columnName.equals(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP))
            return getEventTimestamp();
        if(columnName.equals(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE))
            return getEventType().getEventTypeValue();
        if(columnName.equals(EventReaderContract.EventEntry.COLUMN_NAME_ID))
            return getEventID();
        return null;
    }

    public String getValue(String columnName){
        Number value = getValueNumber(columnName);
        if(value==null)
            return null;
        return value.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof SQLiteEvent)
            return getEventID().equals(((SQLiteEvent) o).getEventID());
        return super.equals(o);
    }
}
