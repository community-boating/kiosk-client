package org.communityboating.kioskclient.event.admingui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventCollection;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventCollectionPage;


public class EventCollectionAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private CBIAPPEventCollection collection;

    public EventCollectionAdapter(CBIAPPEventCollection collection){
        this.collection = collection;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup group, int index){
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.layout_admin_gui_main_event, group, false);
        EventViewHolder holder = new EventViewHolder(view);
        holder.setEvent(collection.getEvent(index));
        return holder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int index) {
        eventViewHolder.setEvent(collection.getEvent(index));
    }

    @Override
    public int getItemCount() {
        return collection.getSelectionSize();
    }
}
