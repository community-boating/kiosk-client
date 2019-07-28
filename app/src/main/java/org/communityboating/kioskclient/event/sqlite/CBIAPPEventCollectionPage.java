package org.communityboating.kioskclient.event.sqlite;

import java.util.LinkedList;
import java.util.List;

public class CBIAPPEventCollectionPage {
    List<SQLiteEvent> sqLiteEvents;
    boolean pagePopulated;
    int pageNumber;
    int pageSize;
    public CBIAPPEventCollectionPage(int pageNumber, int pageSize){
        pagePopulated=false;
        this.pageNumber=pageNumber;
        this.pageSize=pageSize;
        sqLiteEvents=new LinkedList<>();
    }
    public boolean isPagePopulated(){
        return pagePopulated;
    }
    public SQLiteEvent getSQLiteEvent(int offset){
        return sqLiteEvents.get(offset);
    }
    public int getPageNumber(){
        return pageNumber;
    }
    public int getPageSize(){
        return pageSize;
    }
    public SQLiteEvent getFinalEvent(){
        if(sqLiteEvents.size()==0)
            return null;
        return sqLiteEvents.get(sqLiteEvents.size()-1);
    }
}
