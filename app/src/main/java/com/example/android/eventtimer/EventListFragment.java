package com.example.android.eventtimer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.eventtimer.utils.EventListAdapter;
import com.example.android.eventtimer.utils.Event;


public class EventListFragment extends Fragment {

    private ListView eventListView;
    private EventListAdapter eventListAdapter;
    private RemoveEventListener mainActivityListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityListener = (RemoveEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RemoveEventListener");
        }
    }

    public void init(MainActivity app) {
        eventListView = app.findViewById(R.id.events_list);

        eventListAdapter = new EventListAdapter(app);
        eventListView.setAdapter(eventListAdapter);
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
                eventListAdapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete_selected_events:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = eventListAdapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Event selectedEvent = eventListAdapter
                                        .getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                removeEvent(selectedEvent);
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
                mode.getMenuInflater().inflate(R.menu.contexual_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                eventListAdapter.removeSelection();
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
                        removeEvent(clickedEvent);
                        view.setAlpha(1);
                    }
                });
            }
        });
    }

    public void addEvent(Event event) {
        eventListAdapter.add(event);
    }

    public void removeAllEvents() {
        eventListAdapter.removeAllEvents();
    }

    public interface RemoveEventListener {
        void onEventRemoved(long eventMillis);
    }

    private void removeEvent(Event event) {
        eventListAdapter.remove(event);
        mainActivityListener.onEventRemoved(event.getDurationMillis());
    }
}