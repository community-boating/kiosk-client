package org.communityboating.kioskclient.event.events;

import java.util.Map;
import java.util.TreeMap;

public enum CBIAPPEventType {
    EVENT_TYPE_PRINTER(0), EVENT_TYPE_NETWORK(1), EVENT_TYPE_SYSTEM_INT(2);

    static Map<Integer, CBIAPPEventType> eventTypeMapping = new TreeMap<>();

    static{
        for(CBIAPPEventType value : CBIAPPEventType.values()){
            eventTypeMapping.put(value.eventTypeValue, value);
        }
    }

    int eventTypeValue;
    CBIAPPEventType(int eventTypeValue){
        this.eventTypeValue = eventTypeValue;
    }
    public int getEventTypeValue(){
        return eventTypeValue;
    }

    public static CBIAPPEventType getEventTypeByValue(Integer value){
        return eventTypeMapping.get(value);
    }

}
