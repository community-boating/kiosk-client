package org.communityboating.kioskclient.activity.admingui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;

import java.util.Date;

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
        Date eventDate = new Date(event.getEventTimestamp());
        String timeString = AdminGUIActivity.dateFormat.format(eventDate) + " " + AdminGUIActivity.timeFormat.format(eventDate);
        eventTimeStamp.setText(timeString);
        eventTitle.setText(event.getEventTitle());
        eventMessage.setText(event.getEventMessage());
        eventMessage.setVisibility(View.GONE);
    }
}
