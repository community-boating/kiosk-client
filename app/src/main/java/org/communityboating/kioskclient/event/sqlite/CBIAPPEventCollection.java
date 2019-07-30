package org.communityboating.kioskclient.event.sqlite;

import android.util.Log;

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

    int pageEntrySize = 100;

    Map<Integer, CBIAPPEventCollectionPage> collectionPages;

    Object updateSizeLock = new Object();
    boolean isUpdatingSize=false;

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
        synchronized (updateSizeLock) {
            isUpdatingSize = true;
        }
        selectionSize = dbHelper.getSelectionSize(this.selection);
        numberOfPages = (selectionSize + pageEntrySize - 1) / pageEntrySize;
        synchronized (updateSizeLock) {
            isUpdatingSize = false;
            updateSizeLock.notifyAll();
        }
    }

    Map<Integer, PopulateEventPageAsyncThread> asyncPopulatingPages = new TreeMap<>();

    private boolean isPageAsyncPopulating(CBIAPPEventCollectionPage page){
        synchronized (asyncPopulatingPages){
            return asyncPopulatingPages.containsKey(page.getPageNumber());
        }
    }

    private void waitForPageAsyncPopulating(CBIAPPEventCollectionPage page){
        PopulateEventPageAsyncThread thread;
        synchronized (asyncPopulatingPages){
            if(!asyncPopulatingPages.containsKey(page.getPageNumber()))
                return;
            thread=asyncPopulatingPages.get(page.getPageNumber());
        }
        synchronized (thread.asyncPopulationLock){
            try {
                thread.asyncPopulationLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateEventPageAsync(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection, int collectionPageSize){
        Log.d("derpderp", "page async load : " + page.getPageNumber());
        synchronized (asyncPopulatingPages){
            if(asyncPopulatingPages.containsKey(page.getPageNumber()))
                return; //Page already loading
        }
        PopulateEventPageAsyncThread thread = new PopulateEventPageAsyncThread(page, pageBefore, pageAfter, selection, collectionPageSize);
        synchronized (asyncPopulatingPages){
            asyncPopulatingPages.put(page.getPageNumber(), thread);
        }
        thread.start();
    }

    private void populateEventPageAsyncFinish(PopulateEventPageAsyncThread thread){
        synchronized (asyncPopulatingPages){
            asyncPopulatingPages.remove(thread.page.getPageNumber());
        }
        synchronized (thread.asyncPopulationLock){
            thread.asyncPopulationLock.notifyAll();
        }
    }

    private class PopulateEventPageAsyncThread extends Thread {
        Object asyncPopulationLock = new Object();
        CBIAPPEventCollectionPage page, pageBefore, pageAfter;
        CBIAPPEventSelection selection;
        int collectionPageSize;
        private PopulateEventPageAsyncThread(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection, int collectionPageSize){
            this.page=page;
            this.pageBefore=pageBefore;
            this.pageAfter=pageAfter;
            this.selection=selection;
            this.collectionPageSize=collectionPageSize;
        }
        @Override
        public void run(){
            dbHelper.populateEventPage(page, pageBefore, pageAfter, selection, collectionPageSize);
            populateEventPageAsyncFinish(this);
        }
    }

    public SQLiteEvent getEvent(int index){
        synchronized (updateSizeLock){
            if(isUpdatingSize) {
                try {
                    updateSizeLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        int page = getPageFromIndex(index);
        int pageOffset = getPageOffsetFromIndex(index);
        CBIAPPEventCollectionPage eventPage = getCollectionPage(page);
        CBIAPPEventCollectionPage pageBefore = getCollectionPageBefore(page);
        CBIAPPEventCollectionPage pageAfter = getCollectionPageAfter(page);
        if(!eventPage.isPagePopulated()){
            if(isPageAsyncPopulating(eventPage))
                waitForPageAsyncPopulating(eventPage);
            else
                dbHelper.populateEventPage(eventPage, pageBefore, pageAfter, selection, getPageEntrySize());

        }
        if(pageBefore!=null&&!pageBefore.isPagePopulated()){
            CBIAPPEventCollectionPage pageBeforePageBefore = getCollectionPageBefore(page - 1);
            populateEventPageAsync(pageBefore, pageBeforePageBefore, eventPage, selection, getPageEntrySize());
        }
        if(pageAfter!=null&&!pageAfter.isPagePopulated()){
            CBIAPPEventCollectionPage pageAfterPageAfter = getCollectionPageAfter(page + 1);
            populateEventPageAsync(pageAfter, eventPage, pageAfterPageAfter, selection, getPageEntrySize());
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
