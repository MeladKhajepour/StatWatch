package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
        eventListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        eventListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = eventListView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = adapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Event selecteditem = adapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                adapter.remove(selecteditem);
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.action_bar, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });

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