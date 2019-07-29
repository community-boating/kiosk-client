package org.communityboating.kioskclient.event.sqlite;

import org.communityboating.kioskclient.event.events.CBIAPPEventType;

public class CBIAPPEventSelection {

    public static final int SORT_TYPE_EVENT_TYPE=0;
    public static final int SORT_TYPE_TIMESTAMP=1;
    public static final int SORT_TYPE_TIMESTAMP_REVERSE=2;

    public static final int ASC = 0;
    public static final int DESC = 1;

    //public Long startID;
    //public Long endID;
    public Long startTimeStamp;
    public Long endTimeStamp;
    public CBIAPPEventType eventType;
    public int sortType;

    public static int getASC(boolean reversed){
        return reversed ? DESC : ASC;
    }

    public static int getDESC(boolean reversed){
        return reversed ? ASC : DESC;
    }

    public Long getStartTimeStamp() {
        return startTimeStamp;
    }

    public Long getEndTimeStamp() {
        return endTimeStamp;
    }

    public CBIAPPEventType getEventType() {
        return eventType;
    }

    public int getSortType() {
        return sortType;
    }

    public void setStartTimeStamp(Long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void setEndTimeStamp(Long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public void setEventType(CBIAPPEventType eventType) {
        this.eventType = eventType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getFirstSortColumn(){
        if(eventType != null || sortType == SORT_TYPE_TIMESTAMP || sortType == SORT_TYPE_TIMESTAMP_REVERSE)
            return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP;
        if(sortType == SORT_TYPE_EVENT_TYPE)
            return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE;
        return null;
    }

    public int getFirstSortColumnOrder(boolean reversed){
        if(eventType != null || sortType == SORT_TYPE_TIMESTAMP || sortType == SORT_TYPE_EVENT_TYPE)
            return getASC(reversed);
        if(sortType == SORT_TYPE_TIMESTAMP_REVERSE)
            return getDESC(reversed);
        return DESC;
    }

    public String getSecondSortColumn(){
        if(eventType != null || sortType == SORT_TYPE_TIMESTAMP || sortType == SORT_TYPE_TIMESTAMP_REVERSE)
            return EventReaderContract.EventEntry.COLUMN_NAME_ID;
        if(sortType == SORT_TYPE_EVENT_TYPE)
            return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP;
        return null;
    }

    public int getSecondSortColumnOrder(boolean reversed){
        return getASC(reversed);
    }

    public String getThirdSortColumn(){
        if(eventType != null || sortType == SORT_TYPE_TIMESTAMP || sortType == SORT_TYPE_TIMESTAMP_REVERSE)
            return null;
        if(sortType == SORT_TYPE_EVENT_TYPE)
            return EventReaderContract.EventEntry.COLUMN_NAME_ID;
        return null;
    }

    public int getThirdSortColumnOrder(boolean reversed){
        return getASC(reversed);
    }

}
