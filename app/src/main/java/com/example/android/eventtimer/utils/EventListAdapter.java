package com.example.android.eventtimer.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.eventtimer.R;

import java.util.List;


public class EventListAdapter extends ArrayAdapter<Event> {

    private List<Event> eventList;
    private SparseBooleanArray selectedItemsIds;

    public EventListAdapter(Context context, List<Event> eventList) {
        super(context, R.layout.time_row, eventList);
        this.eventList = eventList;
        selectedItemsIds = new SparseBooleanArray();
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

        String label = "Time " + String.valueOf(event.getLabel());

        viewHolder.label.setText(label);
        //TODO: add method to event obj to return formatted text of time instead of using timer
        //TODO: think more about ^ after refactoring the counter into a linear layout with h:m:s
        viewHolder.time.setText(Timer.formatDuration(event.getDurationMillis()));

        return convertView;
    }

    public void remove(Event event) {
        eventList.remove(event);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if(value) {
            selectedItemsIds.put(position, value);
        } else {
            selectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}