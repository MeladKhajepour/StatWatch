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

import static com.example.android.eventtimer.utils.DataManager.PREFS;


public class EventListAdapter extends ArrayAdapter<Event> {

    private SparseBooleanArray selectedItemsIds;

    public EventListAdapter(Context context) {
        super(context, R.layout.time_row, DataManager.loadEvents(context.getSharedPreferences(PREFS,Context.MODE_PRIVATE)));
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

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void remove(Event event) {
        DataManager.removeEvent(getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE), event);
        notifyDataSetChanged();
    }

    public void add(Event event) {
        DataManager.addEvent(getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE), event);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if(value) {
            selectedItemsIds.put(position, true);
        } else {
            selectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}