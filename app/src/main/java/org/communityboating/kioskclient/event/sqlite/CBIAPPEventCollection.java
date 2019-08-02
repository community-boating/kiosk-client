package org.communityboating.kioskclient.event.sqlite;

import android.util.Log;

import org.communityboating.kioskclient.activity.admingui.EventCollectionAdapter;
import org.communityboating.kioskclient.event.handler.CBIAPPEventCollectionUpdateHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CBIAPPEventCollection {

    public CBIAPPEventCollection(EventDBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    CBIAPPEventCollectionUpdateHandler collectionUpdateHandler;

    EventDBHelper dbHelper;

    CBIAPPEventSelection selection;

    int selectionSize;

    int numberOfPages;

    int pageEntrySize = 50;

    int maxPagesPopulated = 5;

    int populationCounter;

    //Map<Integer, Integer> indexPageMapping;
    List<CBIAPPEventCollectionPage> collectionPages;
    //Map<Integer, CBIAPPEventCollectionPage> collectionPagesNormalSizes;
    //Map<Integer, CBIAPPEventCollectionPage> collectionPagesUnusualSizes;

    Object updateSizeLock = new Object();
    boolean isUpdatingSize=false;

    public CBIAPPEventSelection getSelection() {
        return selection;
    }

    private CBIAPPEventCollectionPage getPageFromPageNumber(int pageNumber){
        return collectionPages.get(pageNumber);
    }

    private CBIAPPEventCollectionPage getPageFromIndex(int index){
        if(index >= selectionSize)
            index = selectionSize - 1;
        if(index < 0)
            index = 0;
        int maxPageIndex=collectionPages.size()-1;
        int highIndex=maxPageIndex;
        int lowIndex=0;
        CBIAPPEventCollectionPage foundPage = null;
        while(true){
            int middleIndex = (highIndex + lowIndex) / 2;
            CBIAPPEventCollectionPage middlePage = getPageFromPageNumber(middleIndex);
            if(index < middlePage.getPageFirstIndex()){
                if(middleIndex-1 < 0){
                    foundPage=middlePage;
                    break;
                }else {
                    highIndex = middleIndex - 1;
                    continue;
                }
            }else{
                if(middleIndex + 1 > maxPageIndex){
                    foundPage=middlePage;
                    break;
                }else{
                    CBIAPPEventCollectionPage pageNext = getPageFromPageNumber(middleIndex + 1);
                    if(index < pageNext.getPageFirstIndex()){
                        foundPage=middlePage;
                        break;
                    }else{
                        lowIndex=middleIndex+1;
                        continue;
                    }
                }
            }
        }
        if(foundPage==null)
            throw new RuntimeException("Could not find page from index : " + index);
        return foundPage;
    }

    private void splitPageIfNeeded(CBIAPPEventCollectionPage page){
        if(page.getPageSize() >= pageEntrySize * 2) {
            splitPage(page, pageEntrySize);
        }
    }

    private void splitPage(CBIAPPEventCollectionPage page, int splitIndex){
        List<SQLiteEvent> sqLiteEvents = page.getSqLiteEvents();
        List<SQLiteEvent> newPageLowerSQLiteEvents = sqLiteEvents.subList(0, splitIndex);
        List<SQLiteEvent> newPageUpperSQLiteEvents = sqLiteEvents.subList(splitIndex, sqLiteEvents.size());
        page.sqLiteEvents = newPageLowerSQLiteEvents;
        page.pageSize = newPageLowerSQLiteEvents.size();
        int secondPageOffset = page.getPageNumber() + 1;
        int secondPageIndex = page.getPageFirstIndex() + newPageLowerSQLiteEvents.size();
        CBIAPPEventCollectionPage upperPage = new CBIAPPEventCollectionPage(secondPageOffset, newPageUpperSQLiteEvents.size(), secondPageIndex);
        collectionPages.add(secondPageOffset, upperPage);
        for(int i = secondPageOffset + 1; i < collectionPages.size(); i++){
            CBIAPPEventCollectionPage pageAbove = collectionPages.get(i);
            pageAbove.pageNumber = i;
        }
    }

    public void unpopulatePage(CBIAPPEventCollectionPage page){
        page.p
    }

    public void insertEvent(SQLiteEvent event){
        Comparator<SQLiteEvent> comparator = getSelection().getSelectionComparator();
        CBIAPPEventCollectionPage foundPage = null;
        CBIAPPEventCollectionPage closestPopulated=null;
        for(CBIAPPEventCollectionPage page : collectionPages){
            if(page.isPagePopulated()){
                SQLiteEvent eventFirst = page.getFirstEvent();
                SQLiteEvent eventFinal = page.getFinalEvent();
                if(comparator.compare(event, eventFirst) < 0){
                    if(closestPopulated==null || closestPopulated.getPageNumber() > page.getPageNumber()) {
                        closestPopulated = page;
                        continue;
                    }
                }else if(comparator.compare(event, eventFinal) <= 0 || page.getPageNumber() + 1 >= collectionPages.size()){
                    foundPage=page;
                    break;
                }
            }
        }
        CBIAPPEventCollectionPage increaseAfterPage=null;
        if(foundPage != null) {
            increaseAfterPage = foundPage;
            foundPage.addEvent(event, getSelection());
        }else {
            if(closestPopulated != null)
                increaseAfterPage = getCollectionPageBefore(closestPopulated.getPageNumber());
            else
                increaseAfterPage=getPageFromPageNumber(collectionPages.size() - 1);
            increaseAfterPage.increasePageSizeBy(1);
        }
        for(int i = increaseAfterPage.getPageNumber() + 1; i < collectionPages.size(); i++){
            CBIAPPEventCollectionPage pageAfterFoundPage=getPageFromPageNumber(i);
            pageAfterFoundPage.increaseFirstIndexBy(1);
        }
        if(foundPage != null){
            splitPageIfNeeded(foundPage);
        }
        selectionSize++;
    }

    public void updateSelectionSize(){
        synchronized (updateSizeLock) {
            isUpdatingSize = true;
        }
        selectionSize = dbHelper.getSelectionSize(this.selection);
        numberOfPages = (selectionSize + pageEntrySize - 1) / pageEntrySize;
        collectionPages = new ArrayList<>(numberOfPages);
        populationCounter = 0;
        if(numberOfPages == 0)
            collectionPages.add(new CBIAPPEventCollectionPage(0, 0, 0));
        for(int i = 0; i < numberOfPages; i++){
            int pageSize = pageEntrySize;
            if(i + 1 >= numberOfPages)
                pageSize=(selectionSize%pageEntrySize==0&&selectionSize>0)?pageEntrySize:(selectionSize%pageEntrySize);
            CBIAPPEventCollectionPage page = new CBIAPPEventCollectionPage(i, pageSize, i*pageEntrySize);
            collectionPages.add(page);
        }
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

    private void populateEventPageAsync(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection){
        synchronized (asyncPopulatingPages){
            if(asyncPopulatingPages.containsKey(page.getPageNumber()))
                return; //Page already loading
        }
        PopulateEventPageAsyncThread thread = new PopulateEventPageAsyncThread(page, pageBefore, pageAfter, selection);
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
        private PopulateEventPageAsyncThread(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection){
            this.page=page;
            this.pageBefore=pageBefore;
            this.pageAfter=pageAfter;
            this.selection=selection;
        }
        @Override
        public void run(){
            populateEventPage(page, pageBefore, pageAfter, selection);
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
        CBIAPPEventCollectionPage eventPage = getPageFromIndex(index);
        int pageOffset = eventPage.getPageOffset(index);
        int page=eventPage.getPageNumber();
        CBIAPPEventCollectionPage pageBefore = getCollectionPageBefore(page);
        CBIAPPEventCollectionPage pageAfter = getCollectionPageAfter(page);
        if(!eventPage.isPagePopulated()){
            if(isPageAsyncPopulating(eventPage))
                waitForPageAsyncPopulating(eventPage);
            else
                populateEventPage(eventPage, pageBefore, pageAfter, selection);

        }
        if(pageBefore!=null&&!pageBefore.isPagePopulated()){
            CBIAPPEventCollectionPage pageBeforePageBefore = getCollectionPageBefore(page - 1);
            populateEventPageAsync(pageBefore, pageBeforePageBefore, eventPage, selection);
        }
        if(pageAfter!=null&&!pageAfter.isPagePopulated()){
            CBIAPPEventCollectionPage pageAfterPageAfter = getCollectionPageAfter(page + 1);
            populateEventPageAsync(pageAfter, eventPage, pageAfterPageAfter, selection);
        }
        return eventPage.getSQLiteEvent(pageOffset);
    }

    private void populateEventPage(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection){
        dbHelper.populateEventPage(page, pageBefore, pageAfter, selection, populationCounter++);
    }

    public void setCollectionUpdateHandler(CBIAPPEventCollectionUpdateHandler collectionUpdateHandler){
        this.collectionUpdateHandler = collectionUpdateHandler;
    }

    public int getPageEntrySize(){
        return pageEntrySize;
    }

    private CBIAPPEventCollectionPage getCollectionPageBefore(int page){
        if(page > 0 && page - 1 < numberOfPages)
            return getPageFromPageNumber(page - 1);
        else
            return null;
    }

    private CBIAPPEventCollectionPage getCollectionPageAfter(int page){
        if(page + 1 < numberOfPages && page + 1 > 0)
            return getPageFromPageNumber(page + 1);
        else
            return null;
    }

    /*public CBIAPPEventCollectionPage getCollectionPage(int page){
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
    }*/

    /*public List<SQLiteEvent> getSQLiteEvents() {
        return SQLiteEvents;
    }*/

    public void updateSelection(CBIAPPEventSelection selection) {
        this.selection = selection;
        updateSelectionSize();
        collectionUpdateHandler.handleCollectionUpdate();
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }

    public int getSelectionSize(){
        return selectionSize;
    }

    /*public void setSQLiteEvents(List<SQLiteEvent> SQLiteEvents) {
        this.SQLiteEvents = SQLiteEvents;
    }*/

}
