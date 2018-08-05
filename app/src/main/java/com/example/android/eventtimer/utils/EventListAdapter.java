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
import java.util.Objects;


public class EventListAdapter extends ArrayAdapter<Event> {

    private List<Integer> selectedEventIds;

    public EventListAdapter(Context context, SharedPreferences prefs) {
        super(context, R.layout.time_row, EventsManager.getAllEvents(prefs)); //todo improve time row layout
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

        viewHolder.label.setText(Objects.requireNonNull(event).getLabel());
        viewHolder.time.setText(Timer.formatDuration(event.getDurationMillis()));

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

    public List<Integer> getSelectedIds() {
        return selectedEventIds;
    }
}