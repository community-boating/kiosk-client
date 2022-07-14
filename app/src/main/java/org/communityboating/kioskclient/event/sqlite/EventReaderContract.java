package org.communityboating.kioskclient.event.sqlite;

import android.provider.BaseColumns;

public final class EventReaderContract {

    private EventReaderContract(){
    }

    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME="events";
        public static final String COLUMN_NAME_ID="id";
        public static final String COLUMN_NAME_EVENT_TIMESTAMP="event_timestamp";
        public static final String COLUMN_NAME_EVENT_TYPE="event_type";
        public static final String COLUMN_NAME_EVENT_TITLE="event_title";
        public static final String COLUMN_NAME_EVENT_MESSAGE="event_message";

    }

}
