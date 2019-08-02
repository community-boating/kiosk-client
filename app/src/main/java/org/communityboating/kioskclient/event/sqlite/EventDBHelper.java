package org.communityboating.kioskclient.event.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;

import org.communityboating.kioskclient.event.events.CBIAPPEventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EventDBHelper extends SQLiteOpenHelper {
    public EventDBHelper(Context context) {
        super(context, CBIAPPDBInfo.DB_NAME, null, CBIAPPDBInfo.DB_VERSION);
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + EventReaderContract.EventEntry.TABLE_NAME + " (" +
            EventReaderContract.EventEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE + " INTEGER," +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TITLE + " TEXT," +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_MESSAGE + " TEXT," +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP + " INTEGER)";

    private static final String SQL_CREATE_INDEXES = "CREATE INDEX IdxIDTYPETIMESTAMP ON " + EventReaderContract.EventEntry.TABLE_NAME + "(" +
            EventReaderContract.EventEntry.COLUMN_NAME_ID + ", " +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE + ", " +
            EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP + ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EventReaderContract.EventEntry.TABLE_NAME;

    private List<CBIAPPEventCollection> activeCollections = new ArrayList<>();

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_INDEXES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private Object insertingLock = new Object();
    private int insertingLockCount = 0;

    private Object fetchingLock = new Object();
    private int fetchingLockCount = 0;

    public CBIAPPEventCollection getCollection(){
        CBIAPPEventCollection newCollection = new CBIAPPEventCollection(this);
        activeCollections.add(newCollection);
        return newCollection;
    }

    public int getSelectionSize(CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(*) FROM ");
        builder.append(EventReaderContract.EventEntry.TABLE_NAME);
        builder.append(" WHERE ");
        String[] queryArgs = addClausesFromSelection(selection, builder);
        String query = builder.toString();
        Cursor cursor = db.rawQuery(query, queryArgs);
        cursor.moveToNext();
        return (int)DatabaseUtils.longForQuery(db, query, queryArgs);
    }

    private boolean addOrderFromSelection(StringBuilder builder, String column, int order, boolean hasLast){
        String DESC = " DESC";
        String ASC = " ASC";
        if(column == null)
            return hasLast;
        if(hasLast)
            builder.append(", ");
        builder.append(column);
        if(order == CBIAPPEventSelection.DESC)
            builder.append(DESC);
        if(order == CBIAPPEventSelection.ASC)
            builder.append(ASC);
        return true;
    }

    private String getOrderStringFromSelection(CBIAPPEventSelection selection, boolean reversed){
        StringBuilder builder = new StringBuilder();
        //builder.append("ORDER BY ");
        boolean hasLast = false;
        hasLast = addOrderFromSelection(builder, selection.getFirstSortColumn(), selection.getFirstSortColumnOrder(reversed), hasLast);
        hasLast = addOrderFromSelection(builder, selection.getSecondSortColumn(), selection.getSecondSortColumnOrder(reversed), hasLast);
        addOrderFromSelection(builder, selection.getThirdSortColumn(), selection.getThirdSortColumnOrder(reversed), hasLast);
        return builder.toString();
    }

    private List<String> getSelectionStringFromKnownPage(SQLiteEvent eventReference, CBIAPPEventSelection selection, StringBuilder builder, boolean reversed){
        List<String> selectionArgs = new ArrayList<>();
        String firstColumnName=selection.getFirstSortColumn();
        int firstColumnType=selection.getFirstSortColumnOrder(reversed);
        String firstColumnValue=eventReference.getValue(firstColumnName);
        String secondColumnName=selection.getSecondSortColumn();
        int secondColumnType=selection.getSecondSortColumnOrder(reversed);
        String secondColumnValue=eventReference.getValue(secondColumnName);
        String thirdColumnName=selection.getThirdSortColumn();
        int thirdColumnType=selection.getThirdSortColumnOrder(reversed);
        String thirdColumnValue=eventReference.getValue(thirdColumnName);
        addClausesFromSelection(selection, builder, selectionArgs, true, false);
        if(firstColumnName==null)
            return selectionArgs;
        if(secondColumnName==null){
            builder.append(firstColumnName);
            if(firstColumnType==CBIAPPEventSelection.ASC)
                builder.append(" > ?");
            if(firstColumnType==CBIAPPEventSelection.DESC)
                builder.append(" < ?");
            selectionArgs.add(firstColumnValue);
        }else{
            builder.append(firstColumnName);
            if(firstColumnType==CBIAPPEventSelection.ASC)
                builder.append(" >= ?");
            if(firstColumnType==CBIAPPEventSelection.DESC)
                builder.append(" <= ?");
            selectionArgs.add(firstColumnValue);
            if(thirdColumnName==null){
                builder.append(" AND NOT (");
                builder.append(firstColumnName);
                builder.append(" = ? AND ");
                selectionArgs.add(firstColumnValue);
                builder.append(secondColumnName);
                if(secondColumnType==CBIAPPEventSelection.ASC)
                    builder.append(" <= ?");
                if(secondColumnType==CBIAPPEventSelection.DESC)
                    builder.append(" >= ?");
                selectionArgs.add(secondColumnValue);
                builder.append(")");
            }else{
                builder.append(" AND NOT (");
                builder.append(firstColumnName);
                builder.append(" = ? AND ");
                selectionArgs.add(firstColumnValue);
                builder.append(secondColumnName);
                if(secondColumnType==CBIAPPEventSelection.ASC)
                    builder.append(" < ?");
                if(secondColumnType==CBIAPPEventSelection.DESC)
                    builder.append(" > ?");
                selectionArgs.add(secondColumnValue);
                builder.append(") AND NOT (");
                builder.append(firstColumnName);
                builder.append(" = ? AND ");
                selectionArgs.add(firstColumnValue);
                builder.append(secondColumnName);
                builder.append(" = ? AND ");
                selectionArgs.add(secondColumnValue);
                builder.append(thirdColumnName);
                if(thirdColumnType==CBIAPPEventSelection.ASC)
                    builder.append(" <= ?");
                if(thirdColumnType==CBIAPPEventSelection.DESC)
                    builder.append(" >= ?");
                selectionArgs.add(thirdColumnValue);
                builder.append(")");
            }
        }
        return selectionArgs;

    }

    //private String getOrderStringFromSelection(CBIAPPEventSelection selection){
    //    return "ORDER BY " + EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP;
    //}

    public void populateEventPageFromStart(CBIAPPEventCollectionPage page, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();
        String orderByString = getOrderStringFromSelection(selection, false);
        String limitString = "" + page.getPageSize();
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.sqLiteEvents=getEventsFromCursor(cursor, false);
        page.pagePopulated=true;
    }

    public void populateEventPageFromEnd(CBIAPPEventCollectionPage page, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();
        String orderByString = getOrderStringFromSelection(selection, true);
        String limitString = "" + page.getPageSize();
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.sqLiteEvents=getEventsFromCursor(cursor, true);
        page.pagePopulated=true;
    }

    public void populateEventPageFromMiddle(CBIAPPEventCollectionPage page, CBIAPPEventSelection selection){
        int offset = page.getPageFirstIndex();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();
        String orderByString = getOrderStringFromSelection(selection, false);
        String limitString = offset + ", " + page.getPageSize();
        Cursor cursor=db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.sqLiteEvents=getEventsFromCursor(cursor, false);
        page.pagePopulated=true;
    }

    public void populateEventPageFromKnownAfter(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        SQLiteEvent lastEventFromPage=pageAfter.getFirstEvent();
        List<String> selectionArgsList = getSelectionStringFromKnownPage(lastEventFromPage, selection, builder, true);
        String selectionString = builder.toString();
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        int ii=0;
        String orderByString = getOrderStringFromSelection(selection, true);
        String limitString = "" + page.getPageSize();
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.sqLiteEvents=getEventsFromCursor(cursor, true);
        page.pagePopulated=true;
    }

    public void populateEventPageFromKnownBefore(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        SQLiteEvent lastEventFromPage=pageBefore.getFinalEvent();
        List<String> selectionArgsList = getSelectionStringFromKnownPage(lastEventFromPage, selection, builder, false);
        String selectionString = builder.toString();
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        int ii=0;
        String orderByString = getOrderStringFromSelection(selection, false);
        String limitString = "" + page.getPageSize();
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.sqLiteEvents=getEventsFromCursor(cursor, false);
        page.pagePopulated=true;
    }

    private void startDBFetchLock(){
        synchronized (insertingLock){
            try{
                while(insertingLockCount > 0){
                    insertingLock.wait();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        synchronized (fetchingLock){
            fetchingLockCount+=1;
        }
    }

    private void stopDBFetchLock(){
        synchronized (fetchingLock){
            fetchingLockCount-=1;
            fetchingLock.notifyAll();
        }
    }

    public void populateEventPage(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection, int populationIndex){
        startDBFetchLock();
        if(pageBefore==null&&pageAfter!=null){
            //Load from start
            populateEventPageFromStart(page, selection);
        }else if(pageAfter==null&&pageBefore!=null){
            //Load from end
            populateEventPageFromEnd(page, selection);
        }else if(pageAfter!=null&&pageBefore!=null){
            if(pageBefore.isPagePopulated()){
                populateEventPageFromKnownBefore(page, pageBefore, selection);
            }else if(pageAfter.isPagePopulated()){
                populateEventPageFromKnownAfter(page, pageAfter, selection);
            }else{
                populateEventPageFromMiddle(page, selection);
            }
        }else{
            populateEventPageFromMiddle(page, selection);
        }
        page.populationIndex = populationIndex;
        stopDBFetchLock();
    }

    public void insertEventAsync(final SQLiteEvent event){
        Thread thread = new Thread(){
            @Override
            public void run(){
                insertEvent(event);
            }
        };
        thread.start();
    }

    public SQLiteEvent insertEvent(SQLiteEvent event){
        synchronized (fetchingLock){
            try {
                while (fetchingLockCount > 0) {
                    fetchingLock.wait();
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        synchronized (insertingLock){
            insertingLockCount+=1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE, event.getEventType().getEventTypeValue());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP, event.getEventTimestamp());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TITLE, event.getEventTitle());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_MESSAGE, event.getEventMessage());
        long id = db.insert(EventReaderContract.EventEntry.TABLE_NAME, null, contentValues);
        event.setEventID(id);
        for(final CBIAPPEventCollection activeCollection : activeCollections){
            if(activeCollection.getSelection()!=null&&activeCollection.getSelection().isEventInSelection(event)) {
                activeCollection.insertEvent(event);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(activeCollection.collectionUpdateHandler!=null)
                        activeCollection.collectionUpdateHandler.handleCollectionUpdate();
                    }
                });
            }
        }
        synchronized (insertingLock){
            insertingLockCount-=1;
            insertingLock.notifyAll();
        }
        return event;
    }

    public List<SQLiteEvent> getEventsFromCursor(Cursor cursor, boolean reversed){
        List<SQLiteEvent> sqLiteEvents = new ArrayList<>(cursor.getCount());
        int indexID = cursor.getColumnIndexOrThrow(EventReaderContract.EventEntry.COLUMN_NAME_ID);
        int indexEventType = cursor.getColumnIndexOrThrow(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE);
        int indexEventTitle = cursor.getColumnIndexOrThrow(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TITLE);
        int indexEventMessage = cursor.getColumnIndexOrThrow(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_MESSAGE);
        int indexEventTimeStamp = cursor.getColumnIndexOrThrow(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP);
        while(cursor.moveToNext()){
            SQLiteEvent event = new SQLiteEvent();
            event.setEventID(cursor.getLong(indexID));
            event.setEventType(CBIAPPEventType.getEventTypeByValue(cursor.getInt(indexEventType)));
            event.setEventTitle(cursor.getString(indexEventTitle));
            event.setEventMessage(cursor.getString(indexEventMessage));
            event.setEventTimestamp(cursor.getLong(indexEventTimeStamp));
            if(reversed)
                sqLiteEvents.add(0, event);
            else
                sqLiteEvents.add(event);
        }
        cursor.close();
        return sqLiteEvents;
    }

    public List<SQLiteEvent> getEventsFromSelection(CBIAPPEventSelection selection){
        startDBFetchLock();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();

        String sortOrder = getOrderStringFromSelection(selection, false);
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME,
                null,
                selectionString,
                selectionArgs,
                null,
                null,
                sortOrder);
        List<SQLiteEvent> events = getEventsFromCursor(cursor, false);
        stopDBFetchLock();
        return events;
    };

    private String getSortOrder(CBIAPPEventSelection selection){
        //if(selection.getSortType()==CBIAPPEventSelection.SORT_TYPE_EVENT_TYPE)
        //    return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE + " DESC";
        return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP + "DESC";
    }

    private String[] addClausesFromSelection(CBIAPPEventSelection selection, StringBuilder builder){
        List<String> stringList = new ArrayList<>();
        addClausesFromSelection(selection, builder, stringList, false, false);
        return stringList.toArray(new String[0]);
    }

    private void addClausesFromSelection(CBIAPPEventSelection selection, StringBuilder builder, List<String> stringList, boolean finalAnd, boolean addWhere){
        boolean hasLast=false;
        if(addWhere)
            builder.append("WHERE ");
        if(selection.getStartTimeStamp()!=null&&selection.getEndTimeStamp()!=null){
            builder.append(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP);
            builder.append(" BETWEEN ? AND ? ");
            stringList.add(selection.getStartTimeStamp().toString());
            stringList.add(selection.getEndTimeStamp().toString());
            hasLast=true;
        }else if(selection.getStartTimeStamp()!=null){
            builder.append(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP);
            builder.append(" >= ? ");
            stringList.add(selection.getStartTimeStamp().toString());
            hasLast=true;
        }else if(selection.getEndTimeStamp()!=null){
            builder.append(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP);
            builder.append(" <= ? ");
            stringList.add(selection.getEndTimeStamp().toString());
            hasLast=true;
        }
        if(selection.getEventType()!=null){
            if(hasLast)
                builder.append("AND ");
            builder.append(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE);
            builder.append(" = ? ");
            stringList.add(Integer.toString(selection.getEventType().getEventTypeValue()));
        }
        if(!hasLast)
            builder.append("1 ");
        if(finalAnd)
            builder.append("AND ");
    }

}
