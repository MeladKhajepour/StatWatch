package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.EventListAdapter;
import com.example.android.eventtimer.utils.EventsManager;
import com.example.android.eventtimer.utils.StatsManager;
import com.example.android.eventtimer.utils.UpdateUIListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;


public class EventListFragment extends Fragment {

    private ListView eventsListView;
    private List<Event> removedEvents;
    private int numRemovedEvents;

    private EventListAdapter eventListAdapter;
    private UpdateUIListener mainActivityListener;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityListener = (UpdateUIListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ListFragmentInterface");
        }

        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void init(MainActivity app) {
        eventsListView = app.findViewById(R.id.events_list);

        eventListAdapter = new EventListAdapter(app);
        eventsListView.setAdapter(eventListAdapter);
        eventsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        eventsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = eventsListView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                String event = " event";

                if(checkedCount != 1) {
                    event = " events";
                }

                mode.setTitle(checkedCount + event + " selected");
                // Calls toggleSelection method from ListViewAdapter Class
                eventListAdapter.toggleSelection(position);
            }

            @Override //When user selects an action from the action bar after selecting events in list
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_delete_selected_events:
                        removeSelectedEvents(getSelectedEvents());

                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contexual_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                eventListAdapter.clearSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });
    }

    public void refreshEventList() {
        eventListAdapter.notifyDataSetChanged();
    }

    public void removeAllEvents() {
        List<Event> eventList = EventsManager.getAllEvents(prefs);

        removeSelectedEvents(eventList);
    }

    private void removeSelectedEvents(List<Event> selectedEvents) {
        removedEvents = new ArrayList<>(selectedEvents);
        numRemovedEvents = removedEvents.size();

        EventsManager.removeSelectedEvents(prefs, removedEvents);
        StatsManager.updateEventsRemoved(prefs, removedEvents);
        refreshEventList();
        mainActivityListener.updateStatsFragment();

        showUndoSnackbar(eventListAdapter.getSelectedIds());

        eventListAdapter.clearSelection();
    }

    private List<Event> getSelectedEvents() {
        SparseBooleanArray selected = eventListAdapter.getSelectedIds();
        List<Event> selectedEventsList = new ArrayList<>();

        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                selectedEventsList.add(eventListAdapter.getItem(selected.keyAt(i)));
            }
        }

        return selectedEventsList;
    }

    private void showUndoSnackbar(SparseBooleanArray selectedEventIndices) { //todo show it in main activity
        final SparseBooleanArray indices = selectedEventIndices.clone();

        String text = numRemovedEvents + " events removed";

        if(numRemovedEvents == 1) {
            text = numRemovedEvents + " event removed";
        }

        Snackbar snackbar = Snackbar.make(eventsListView, text, Snackbar.LENGTH_LONG);

        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoRemoveEvents(indices);
            }
        });
        snackbar.show();
    }

    private void undoRemoveEvents(SparseBooleanArray removedEventIndices) {
        EventsManager.undoRemoveEvents(prefs, removedEvents, removedEventIndices);
        StatsManager.undoRemoveEvents(prefs);
        refreshEventList();
        mainActivityListener.undo();
    }
}