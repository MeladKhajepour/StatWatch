package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.example.android.eventtimer.utils.Timer;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;


public class EventsFragment extends Fragment {

    private ListView listView;
    private EventListAdapter eventListAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        init(context);
    }

    /*
     * Start of public methods
     */

    public void refreshList() {
        eventListAdapter.notifyDataSetChanged();
    }

    public void clearEvents() {
        List<Event> selectedEvents = new ArrayList<>(EventsManager.getAllEvents(prefs));

        removeSelectedEvents(selectedEvents);
    }

    /*
     * End of public methods
     */

    private void init(Context context) {
        MainActivity app = (MainActivity) context;
        eventListAdapter = new EventListAdapter(context, prefs);
        listView = app.findViewById(R.id.events_list);
        listView.setAdapter(eventListAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listView.getCheckedItemCount();
                StringBuilder sb = checkedCount > 1 ?
                        new StringBuilder(" events") : new StringBuilder(" event");

                sb.append(" selected");
                mode.setTitle(checkedCount + sb.toString());
                eventListAdapter.toggleSelection(position);
            }

            @Override
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

    private void removeSelectedEvents(List<Event> selectedEvents) {
        EventsManager.removeSelectedEvents(prefs, selectedEvents);
        StatsManager.updateEventsRemoved(prefs, selectedEvents);
        eventListAdapter.clearSelection();
        refreshList();

        showUndoSnackbar(selectedEvents);

        ((MainActivity) requireContext()).onEventsRemoved();
    }

    private List<Event> getSelectedEvents() {
        List<Integer> selectedEventIds = eventListAdapter.getSelectedIds();
        List<Event> selectedEvents = new ArrayList<>();

        for(Integer selectedEventId : selectedEventIds) {
            selectedEvents.add(eventListAdapter.getItem(selectedEventId));
        }

        return selectedEvents;
    }

    private void showUndoSnackbar(final List<Event> selectedEvents) {
        final List<Integer> selectedEventIds = eventListAdapter.getSelectedIds();
        int numRemovedEvents = selectedEvents.size();

        String text = numRemovedEvents == 1 ? " event removed" : " events removed";

        Snackbar snackbar = Snackbar.make(listView, numRemovedEvents + text, Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoRemoveEvents(selectedEventIds, selectedEvents);
            }
        });
        snackbar.show();
    }

    private void undoRemoveEvents(List<Integer> selectedEventIds, List<Event> selectedEvents) {
        EventsManager.undoRemoveEvents(prefs, selectedEvents, selectedEventIds);
        StatsManager.undoRemoveEvents(prefs);
        Timer.undoResetTimerIndex(prefs);
        refreshList();
        ((MainActivity) requireContext()).onUndo();
    }
}