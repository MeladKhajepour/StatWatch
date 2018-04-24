package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.eventtimer.utils.EventListAdapter;
import com.example.android.eventtimer.utils.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.eventtimer.MainActivity.EVENTS;
import static com.example.android.eventtimer.MainActivity.PREFS;

public class EventListFragment extends Fragment {

    ListView eventListView;
    List<Event> eventList;
    EventListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_list_fragment, container, false);

        eventListView = view.findViewById(R.id.events_list);

        loadEvents();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHandlers();
    }

    private void setHandlers() {
        adapter = new EventListAdapter(getContext(), eventList);
        eventListView.setAdapter(adapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                final Event clickedEvent = (Event) parent.getItemAtPosition(position);

                view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        eventList.remove(clickedEvent);
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                    }
                });
            }
        });
    }

    //TODO: get event from main activity and update list
    public void addEvent(Event event) {
        eventList.add(event); //TODO try adapter.add(event)
        adapter.notifyDataSetChanged();
        saveEvents();
    }

    private void saveEvents() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(eventList);
        editor.putString(EVENTS, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    private void loadEvents() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(EVENTS, null);
        Type type = new TypeToken<ArrayList<Event>>() {}.getType();
        if(gson.fromJson(json, type) == null) {
            this.eventList = new ArrayList<>();
        } else {
            this.eventList = gson.fromJson(json, type);
        }
    }
}