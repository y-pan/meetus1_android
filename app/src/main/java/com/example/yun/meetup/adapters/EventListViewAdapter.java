package com.example.yun.meetup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.models.Event;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by danie on 12/6/2017.
 */

public class EventListViewAdapter extends BaseAdapter {

    private final List<Event> mEvents;
    private Context mContext;

    public class EventsListViewHolder {

        final TextView eventTitle;
        final TextView eventHost;
        final TextView eventDate;
        final TextView eventAddress;

        public EventsListViewHolder(View view) {
            eventTitle = (TextView) view.findViewById(R.id.lv_item_event_title);
            eventHost = (TextView) view.findViewById(R.id.lv_item_event_host);
            eventDate = (TextView) view.findViewById(R.id.lv_item_event_date);
            eventAddress = (TextView) view.findViewById(R.id.lv_item_event_address);
        }

    }

    public EventListViewAdapter(List<Event> events, Context context){
        mEvents = events;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return new BigInteger(mEvents.get(position).get_id(), 16).longValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        EventsListViewHolder holder;

        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.lv_event_item, parent, false);
            holder = new EventsListViewHolder(view);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (EventsListViewHolder) view.getTag();
        }

        Event event = mEvents.get(position);

        holder.eventTitle.setText(event.getTitle());
        holder.eventHost.setText(event.getUserInfo().getName());
        holder.eventDate.setText(event.getDate());
        holder.eventAddress.setText(event.getAddress());

        return view;
    }
}
