package org.communityboating.kioskclient.event.admingui;

import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.View;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;

public class EventViewHolder extends RecyclerView.ViewHolder {

    TextView eventType;
    TextView eventTimeStamp;
    TextView eventTitle;
    TextView eventMessage;

    private static View.OnClickListener eventDetailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView eventMessage=view.findViewById(R.id.admin_gui_event_message);
            eventMessage.setVisibility(View.VISIBLE);
        }
    };

    public EventViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(eventDetailClickListener);
        eventType=itemView.findViewById(R.id.admin_gui_event_type);
        eventTimeStamp=itemView.findViewById(R.id.admin_gui_event_timestamp);
        eventTitle=itemView.findViewById(R.id.admin_gui_event_title);
        eventMessage=itemView.findViewById(R.id.admin_gui_event_message);
    }
    public void setEvent(SQLiteEvent event){
        eventType.setText(event.getEventType().name());
        eventTimeStamp.setText(event.getEventTimestamp().toString());
        eventTitle.setText(event.getEventTitle());
        eventMessage.setText(event.getEventMessage());
        eventMessage.setVisibility(View.GONE);
    }
}
