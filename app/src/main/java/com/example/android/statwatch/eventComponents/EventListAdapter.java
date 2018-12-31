package com.example.android.statwatch.eventComponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.statwatch.R;
import com.example.android.statwatch.timerComponents.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class EventListAdapter extends ArrayAdapter<Event> {

    private List<Integer> selectedEventIds;

    EventListAdapter(Context context, SharedPreferences prefs) {
        super(context, R.layout.time_row, EventsManager.getEvents(prefs)); //todo improve time row layout
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

    void toggleSelection(Integer position) {

        if(!selectedEventIds.contains(position)) {
            selectedEventIds.add(position);
        } else {
            selectedEventIds.remove(position);
        }

        notifyDataSetChanged();
    }

    void clearSelection() {
        selectedEventIds.clear();
        notifyDataSetChanged();
    }

    List<Integer> getSelectedPositions() {
        return selectedEventIds;
    }
}