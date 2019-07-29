package org.communityboating.kioskclient.event.events;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.EventLog;
import android.util.Log;

import org.communityboating.kioskclient.event.handler.CBIAPPEventHandler;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventCollection;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventCollectionPage;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventSelection;
import org.communityboating.kioskclient.event.sqlite.EventDBHelper;
import org.communityboating.kioskclient.event.sqlite.EventReaderContract;
import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CBIAPPEventManager {
    private static CBIAPPEventManager eventManagerInstance = null;
    public static void initiateIfRequired(Context context){
        if(eventManagerInstance == null){
            eventManagerInstance = new CBIAPPEventManager(context);
        }
    }

    private List<CBIAPPEventHandler> eventHandlers = new LinkedList<>();
    private Map<Integer, List<CBIAPPEventHandler>> eventHandlersTyped = new TreeMap<>();
    private List<CBIAPPEventCollection> activeCollections = new LinkedList<>();

    private EventDBHelper dbHelper;
    private CBIAPPEventManager(Context context){
        dbHelper = new EventDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long systemStart = System.currentTimeMillis();
        db.rawQuery("SELECT * FROM events WHERE event_timestamp > '100' GROUP BY event_timestamp, id", new String[0]).close();
        //long size1 = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + EventReaderContract.EventEntry.TABLE_NAME + " WHERE (" + EventReaderContract.EventEntry.COLUMN_NAME_EVENT_TIMESTAMP + ") >= (1000)", new String[0]);
        long afterFirst = System.currentTimeMillis();
        CBIAPPEventSelection selection = new CBIAPPEventSelection();
        selection.setEndTimeStamp(45000l);
        selection.setStartTimeStamp(5l);
        //selection.setEventType(CBIAPPEventType.EVENT_TYPE_PRINTER);
        selection.setSortType(CBIAPPEventSelection.SORT_TYPE_EVENT_TYPE);
        //selection.setEventType(CBIAPPEventType.EVENT_TYPE_PRINTER);
        //long size2 = dbHelper.getSelectionSize(selection);
        CBIAPPEventCollection collection = new CBIAPPEventCollection(dbHelper);
        collection.updateSelection(selection);
        int numPages = collection.getNumberOfPages();
        CBIAPPEventCollectionPage pageFirst = collection.getCollectionPage(0);
        CBIAPPEventCollectionPage pageSecondLast = collection.getCollectionPage(numPages - 2);
        CBIAPPEventCollectionPage pageLast = collection.getCollectionPage(numPages - 1);
        List<SQLiteEvent> sqLiteEvents = dbHelper.getEventsFromSelection(selection);
        boolean hasError = false;
        int counted = 0;
        for(; counted < sqLiteEvents.size(); counted++){
            SQLiteEvent event1 = sqLiteEvents.get(counted);
            SQLiteEvent event2 = collection.getEvent(counted);
            //Log.d("derpderp", "aaaa : " + event1.getEventType() + " : " + event2.getEventType());
            if(!event1.equals(event2)) {
                Log.d("derpderp", "issue what is this : " + counted + " : " + event1.getEventID() + " : " + event2.getEventID());
                hasError=true;
            }
        }
        if (hasError)
            Log.d("derpderp", "we had an error : " + counted);
        else
            Log.d("derpderp", "we have no error : " + counted);
        //dbHelper.populateEventPageFromMiddle(pageSecond, selection, 50);
        //int size2 = dbHelper.populateEventPageFromStart(page, selection);
        //Log.d("derpderpaherp", "time1 : " + (afterFirst-systemStart) + " time 2 : " + (afterSecond-afterFirst));
        //Log.d("derpderp", "derpderpaherp + : " + size1 + " : " + size2);
        Log.d("derpderp", "derp : " + sqLiteEvents.size() + " : " + pageFirst.getPageSize() + " : " + pageLast.getPageSize());
        //Log.d("derpderp", "derpderpaherp + ::: " + colPage.getPageSize() + " ::: " + numPages);
    }

    private void dispatchEvent(CBIAPPEvent event){
        SQLiteEvent sqLiteEvent = event.getSQLiteEvent();
        dbHelper.insertEvent(sqLiteEvent);
        for(CBIAPPEventHandler eventHandler : eventHandlers){
            eventHandler.handleEvent(event);
        }
        for(CBIAPPEventHandler eventHandler : eventHandlersTyped.get(event.getEventType().getEventTypeValue())){
            eventHandler.handleEvent(event);
        }
    }

    private void addEventHandlerI(CBIAPPEventType type, CBIAPPEventHandler handler){
        if(type == null){
            eventHandlers.add(handler);
        }else{
            int typeKey = type.getEventTypeValue();
            if(eventHandlersTyped.containsKey(typeKey)){
            }else{
                List<CBIAPPEventHandler> handlerList = new LinkedList<>();
                handlerList.add(handler);
                eventHandlersTyped.put(typeKey, handlerList);
            }
        }
    }

    public static void addEventHandler(CBIAPPEventType type, CBIAPPEventHandler handler){
        eventManagerInstance.addEventHandlerI(type, handler);
    }

    public static EventDBHelper getDBHelper(){
        return eventManagerInstance.dbHelper;
    }

    public static void onClose(){
        if(eventManagerInstance != null)
            eventManagerInstance.dbHelper.close();
    }

}
