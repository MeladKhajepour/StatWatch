package com.example.android.eventtimer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.eventtimer.R;

import java.util.ArrayList;
import java.util.List;


public class EventListAdapter extends ArrayAdapter<Event> {

    private List<Integer> selectedEventIds;

    public EventListAdapter(Context context, SharedPreferences prefs) {
        super(context, R.layout.time_row, EventsManager.getAllEvents(prefs));
        selectedEventIds = new ArrayList<>();
    }

    private class ViewHolder {
        TextView label;
        TextView time;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.time_row, parent, false);

            viewHolder.label = convertView.findViewById(R.id.event_label);
            viewHolder.time = convertView.findViewById(R.id.event_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.label.setText(event.getLabel());
        viewHolder.time.setText(event.getFormattedDuration());

        return convertView;
    }

    public void toggleSelection(int position) {
        selectedEventIds.add(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedEventIds = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void selectAll() {
        for(int i = 0; i < getCount(); i++) {
            selectedEventIds.add(i);
        }
    }

    public List<Integer> getSelectedIds() {
        return selectedEventIds;
    }
}