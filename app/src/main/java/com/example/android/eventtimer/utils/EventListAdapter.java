package com.example.android.eventtimer.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.eventtimer.MainActivity;
import com.example.android.eventtimer.R;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;


public class EventListAdapter extends ArrayAdapter<Event> {

    private SparseBooleanArray selectedEventIds;

    public EventListAdapter(MainActivity app) {
        super(app.getBaseContext(), R.layout.time_row, EventsManager.getAllEvents(app.getSharedPreferences(PREFS,Context.MODE_PRIVATE)));
        selectedEventIds = new SparseBooleanArray();
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

        viewHolder.label.setText(event.getLabelText());
        //TODO: add method to event obj to return formatted text of time instead of using timer (done)
        //TODO: think more about ^ after refactoring the counter into a linear layout with h:m:s
        viewHolder.time.setText(event.getFormattedDuration());

        return convertView;
    }

    public void toggleSelection(int position) {
        selectEvent(position, !selectedEventIds.get(position));
    }

    public void clearSelection() {
        selectedEventIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectEvent(int position, boolean selected) {
        if(selected) {
            selectedEventIds.put(position, true);
        } else {
            selectedEventIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedEventIds;
    }
}