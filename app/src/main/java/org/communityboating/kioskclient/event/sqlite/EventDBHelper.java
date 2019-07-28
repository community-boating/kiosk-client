package org.communityboating.kioskclient.event.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;

import org.communityboating.kioskclient.event.events.CBIAPPEventType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private List<String> getSelectionStringFromKnownPage(CBIAPPEventCollectionPage lastPage, CBIAPPEventSelection selection, StringBuilder builder, boolean reversed){
        SQLiteEvent finalEvent = lastPage.getFinalEvent();
        List<String> selectionArgs = new ArrayList<>();
        String firstColumnName=selection.getFirstSortColumn();
        int firstColumnType=selection.getFirstSortColumnOrder(reversed);
        String firstColumnValue=finalEvent.getValue(firstColumnName);
        String secondColumnName=selection.getSecondSortColumn();
        int secondColumnType=selection.getSecondSortColumnOrder(reversed);
        String secondColumnValue=finalEvent.getValue(secondColumnName);
        String thirdColumnName=selection.getThirdSortColumn();
        int thirdColumnType=selection.getThirdSortColumnOrder(reversed);
        String thirdColumnValue=finalEvent.getValue(thirdColumnName);
        if(selection.getEventType()!=null){
            builder.append(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE);
            builder.append(" = ? AND ");
            selectionArgs.add(Integer.toString(selection.getEventType().getEventTypeValue()));
        }
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
            if(thirdColumnName==null){
                builder.append(firstColumnName);
                if(firstColumnType==CBIAPPEventSelection.ASC)
                    builder.append(" >= ?");
                if(firstColumnType==CBIAPPEventSelection.DESC)
                    builder.append(" <= ?");
                builder.append(" AND NOT (");
                builder.append(firstColumnName);
                builder.append(" = ? AND ");
                builder.append(secondColumnName);
                if(secondColumnType==CBIAPPEventSelection.ASC)
                    builder.append(" <= ?");
                if(secondColumnType==CBIAPPEventSelection.DESC)
                    builder.append(" >= ?");
            }
        }
        return null;

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
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, null);
        page.pagePopulated=true;
        page.sqLiteEvents=getEventsFromCursor(cursor);
    }

    public void populateEventPageFromEnd(CBIAPPEventCollectionPage page, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();
        String orderByString = getOrderStringFromSelection(selection, true);
        String limitString = "" + page.getPageSize();
        Cursor cursor = db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.pagePopulated=true;
        page.sqLiteEvents=getEventsFromCursor(cursor);
    }

    public void populateEventPageFromMiddle(CBIAPPEventCollectionPage page, CBIAPPEventSelection selection, int pageSize){
        int offset = page.getPageNumber() * pageSize;
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        String[] selectionArgs = addClausesFromSelection(selection, builder);
        String selectionString = builder.toString();
        String orderByString = getOrderStringFromSelection(selection, false);
        String limitString = offset + ", " + page.getPageSize();
        Cursor cursor=db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
        page.pagePopulated=true;
        page.sqLiteEvents=getEventsFromCursor(cursor);
    }

    private void populateEventPageFromKnownBefore(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventSelection selection){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder builder = new StringBuilder();
        List<String> selectionArgsList = addClausesFromSelection(selection, builder, true, false);
        String selectionString = builder.toString();
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        String orderByString = getOrderStringFromSelection(selection, false);
        String limitString = "LIMIT " + page.getPageSize();
        db.query(EventReaderContract.EventEntry.TABLE_NAME, null, selectionString, selectionArgs, null, null, orderByString, limitString);
    }

    public void populateEventPage(CBIAPPEventCollectionPage page, CBIAPPEventCollectionPage pageBefore, CBIAPPEventCollectionPage pageAfter, CBIAPPEventSelection selection){
        if(pageBefore==null&&pageAfter!=null){
            //Load from start
        }else if(pageAfter==null&&pageBefore!=null){
            //Load from end
        }else if(pageAfter!=null&&pageBefore!=null){
            if(pageBefore.isPagePopulated()){

            }else if(pageAfter.isPagePopulated()){

            }else{

            }
        }else{
            throw new RuntimeException("Page before and after were null, this should never happen");
        }
    }

    public SQLiteEvent insertEvent(SQLiteEvent event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE, event.getEventType().getEventTypeValue());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP, event.getEventTimestamp());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TITLE, event.getEventTitle());
        contentValues.put(EventReaderContract.EventEntry.COLUMN_NAME_EVENT_MESSAGE, event.getEventMessage());
        long id = db.insert(EventReaderContract.EventEntry.TABLE_NAME, null, contentValues);
        event.setEventID(id);
        return event;
    }

    public List<SQLiteEvent> getEventsFromCursor(Cursor cursor){
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
            sqLiteEvents.add(event);
        }
        cursor.close();
        return sqLiteEvents;
    }

    public List<SQLiteEvent> getEventsFromSelection(CBIAPPEventSelection selection){
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
        Log.d("derpderp", "herper : " + cursor.getCount());
        return getEventsFromCursor(cursor);
    };

    private String getSortOrder(CBIAPPEventSelection selection){
        //if(selection.getSortType()==CBIAPPEventSelection.SORT_TYPE_EVENT_TYPE)
        //    return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TYPE + " DESC";
        return EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP + "DESC";
    }

    private String[] addClausesFromSelection(CBIAPPEventSelection selection, StringBuilder builder){
        return addClausesFromSelection(selection, builder, false, false).toArray(new String[0]);
    }

    private List<String> addClausesFromSelection(CBIAPPEventSelection selection, StringBuilder builder, boolean finalAnd, boolean addWhere){
        boolean hasLast=false;
        List<String> stringList = new ArrayList<>();
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
        if(finalAnd && hasLast)
            builder.append("AND ");
        return stringList;
    }

}
