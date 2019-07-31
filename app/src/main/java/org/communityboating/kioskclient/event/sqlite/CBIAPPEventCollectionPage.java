package org.communityboating.kioskclient.event.sqlite;

import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CBIAPPEventCollectionPage {
    List<SQLiteEvent> sqLiteEvents;
    boolean pagePopulated;
    int pageNumber;
    int pageSize;
    int pageFirstIndex;
    public CBIAPPEventCollectionPage(int pageNumber, int pageSize, int pageFirstIndex){
        pagePopulated=false;
        this.pageNumber=pageNumber;
        this.pageSize=pageSize;
        this.pageFirstIndex=pageFirstIndex;
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
    public int getPageFirstIndex(){ return pageFirstIndex; }
    public SQLiteEvent getFinalEvent(){
        if(sqLiteEvents.size()==0)
            return null;
        return sqLiteEvents.get(sqLiteEvents.size()-1);
    }
    public SQLiteEvent getFirstEvent(){
        if(sqLiteEvents.size()==0)
            return null;
        return sqLiteEvents.get(0);
    }
    public void addEvent(SQLiteEvent event, CBIAPPEventSelection selection){
        sqLiteEvents.add(event);
        Log.d("derpderp", "event added!");
        Collections.sort(sqLiteEvents, selection.getSelectionComparator());
    }
    public void increaseFirstIndexBy(int amount){
        this.pageFirstIndex += amount;
    }
    public int getPageOffset(int index){
        return index-getPageFirstIndex();
    }
    public void increasePageSizeBy(int amount){
        pageSize+=amount;
    }
}
