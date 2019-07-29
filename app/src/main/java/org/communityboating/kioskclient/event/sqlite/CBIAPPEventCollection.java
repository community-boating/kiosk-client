package org.communityboating.kioskclient.event.sqlite;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CBIAPPEventCollection {



    public CBIAPPEventCollection(EventDBHelper dbHelper){
        this.dbHelper = dbHelper;
        this.collectionPages = new TreeMap<>();
    }

    EventDBHelper dbHelper;

    CBIAPPEventSelection selection;

    int selectionSize;

    int numberOfPages;

    int pageEntrySize = 50;

    Map<Integer, CBIAPPEventCollectionPage> collectionPages;

    public CBIAPPEventSelection getSelection() {
        return selection;
    }

    private int getPageFromIndex(int index){
        if(index >= selectionSize)
            index = selectionSize - 1;
        if(index < 0)
            index = 0;
        return index/pageEntrySize;
    }

    private int getPageOffsetFromIndex(int index){
        return index%pageEntrySize;
    }

    public void updateSelectionSize(){
        selectionSize = dbHelper.getSelectionSize(this.selection);
        numberOfPages = (selectionSize+pageEntrySize-1)/pageEntrySize;
    }

    public SQLiteEvent getEvent(int index){
        int page = getPageFromIndex(index);
        int pageOffset = getPageOffsetFromIndex(index);
        CBIAPPEventCollectionPage eventPage = getCollectionPage(page);
        CBIAPPEventCollectionPage pageBefore = getCollectionPageBefore(page);
        CBIAPPEventCollectionPage pageAfter = getCollectionPageAfter(page);
        if(!eventPage.isPagePopulated()){
            dbHelper.populateEventPage(eventPage, pageBefore, pageAfter, selection, getPageEntrySize());
        }
        if(pageBefore!=null&&!pageBefore.isPagePopulated()){
            CBIAPPEventCollectionPage pageBeforePageBefore = getCollectionPageBefore(page - 1);
            dbHelper.populateEventPage(pageBefore, pageBeforePageBefore, eventPage, selection, getPageEntrySize());
        }
        if(pageAfter!=null&&!pageAfter.isPagePopulated()){
            CBIAPPEventCollectionPage pageAfterPageAfter = getCollectionPageAfter(page + 1);
            dbHelper.populateEventPage(pageAfter, eventPage, pageAfterPageAfter, selection, getPageEntrySize());
        }
        return eventPage.getSQLiteEvent(pageOffset);
    }

    public int getPageEntrySize(){
        return pageEntrySize;
    }

    private CBIAPPEventCollectionPage getCollectionPageBefore(int page){
        if(page > 0 && page - 1 < numberOfPages)
            return getCollectionPage(page - 1);
        else
            return null;
    }

    private CBIAPPEventCollectionPage getCollectionPageAfter(int page){
        if(page + 1 < numberOfPages && page + 1 > 0)
            return getCollectionPage(page + 1);
        else
            return null;
    }

    public CBIAPPEventCollectionPage getCollectionPage(int page){
        if(collectionPages.containsKey(page)){
            return collectionPages.get(page);
        }else{
            int pageSize = pageEntrySize;
            if(page + 1 >= numberOfPages && selectionSize%pageSize!=0)
                pageSize = selectionSize % pageEntrySize;
            CBIAPPEventCollectionPage collectionPage = new CBIAPPEventCollectionPage(page, pageSize);
            collectionPages.put(page, collectionPage);
            return collectionPage;
        }
    }

    /*public List<SQLiteEvent> getSQLiteEvents() {
        return SQLiteEvents;
    }*/

    public void updateSelection(CBIAPPEventSelection selection) {
        this.selection = selection;
        updateSelectionSize();
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }

    /*public void setSQLiteEvents(List<SQLiteEvent> SQLiteEvents) {
        this.SQLiteEvents = SQLiteEvents;
    }*/

}
