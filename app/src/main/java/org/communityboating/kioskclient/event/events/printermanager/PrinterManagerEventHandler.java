package org.communityboating.kioskclient.event.events.printermanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class PrinterManagerEventHandler {

    List<PrinterManagerEvent> events;

    List<PrinterManagerEventListener> eventListeners;

    public PrinterManagerEventHandler(){
        events = new ArrayList<>();
        eventListeners = new LinkedList<>();
    }

    public void addEventListener(PrinterManagerEventListener eventListener){
        eventListeners.add(eventListener);
    }

    public void removeEventListener(PrinterManagerEventListener eventListener){
        eventListeners.remove(eventListener);
    }

    public void dispatchEvent(PrinterManagerEvent event){
        if(events.isEmpty()) {
            events.add(event);
        }else{
            ListIterator<PrinterManagerEvent> eventsIterator = events.listIterator(events.size());
            while(eventsIterator.hasPrevious() && eventsIterator.previous().compareTo(event) > 0){
            }
            eventsIterator.add(event);
        }
        dispatchEventNotifications(event);
    }

    private void dispatchEventNotifications(PrinterManagerEvent event){
        Iterator<PrinterManagerEventListener> listenerIterator = eventListeners.iterator();
        while(listenerIterator.hasNext()){
            PrinterManagerEventListener listener = listenerIterator.next();
            listener.onEvent(event);
        }
    }

    public int getEventCount(){
        return events.size();
    }

    public PrinterManagerEvent getEventAtIndex(int index){
        return events.get(index);
    }

}
