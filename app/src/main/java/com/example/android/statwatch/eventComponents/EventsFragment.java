package com.example.android.statwatch.eventComponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.statwatch.utils.Constants.PREFS;


public class EventsFragment extends Fragment {
    private ListView listView;
    private EventListAdapter eventListAdapter;
    private SharedPreferences prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity app = (MainActivity) context;

        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        eventListAdapter = new EventListAdapter(context, prefs);
        initList(app);
    }

    /*
     * Start of public methods
     */

    public void refresh() {
        eventListAdapter.notifyDataSetChanged();
    }

    public void deleteEvents() {
        List<Event> selectedEvents = new ArrayList<>(EventsManager.getEvents(prefs));

        deleteSelectedEvents(selectedEvents);
    }

    /*
     * End of public methods
     */

    private void initList(MainActivity app) {
        listView = app.findViewById(R.id.events_list);
        listView.setAdapter(eventListAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listView.getCheckedItemCount();
                String title;

                if(checkedCount > 1) {
                    title = checkedCount + " events selected";
                } else {
                    title = checkedCount + " event selected";
                }

                mode.setTitle(title);
                eventListAdapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_delete_selected_events:
                        deleteSelectedEvents(getSelectedEvents());

                        mode.finish();
                        return true;

//                    case R.id.action_calculate_selection: //todo? maybe?
//                        calculateSelection(getSelectedEvents());
//
//                        return true;

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

    private void deleteSelectedEvents(List<Event> selectedEvents) {
        EventsManager.removeSelectedEvents(prefs, selectedEvents);
        List<Integer> selectedPositions = new ArrayList<>(eventListAdapter.getSelectedPositions());
        eventListAdapter.clearSelection();

        if(selectedPositions.isEmpty()) {
            for(int i = 0; i < selectedEvents.size(); i++) {
                selectedPositions.add(i);
            }
        }

        showUndoSnackbar(selectedEvents, selectedPositions);

        ((MainActivity) requireContext()).removeEvent();
    }

    private List<Event> getSelectedEvents() {
        List<Integer> selectedPositions = eventListAdapter.getSelectedPositions();
        List<Event> selectedEvents = new ArrayList<>();

        for(Integer position : selectedPositions) {
            selectedEvents.add(eventListAdapter.getItem(position));
        }

        return selectedEvents;
    }

    private void showUndoSnackbar(final List<Event> selectedEvents, final List<Integer> selectedPositions) {
        int numRemovedEvents = selectedEvents.size();
        String text = numRemovedEvents == 1 ? " event deleted" : " events deleted";

        Snackbar snackbar = Snackbar.make(listView, numRemovedEvents + text, Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo(selectedEvents, selectedPositions);
            }
        });
        snackbar.show();
    }

    private void undo(List<Event> selectedEvents, List<Integer> selectedPositions) {
        EventsManager.undo(prefs, selectedEvents, selectedPositions);
        refresh();
        ((MainActivity) requireContext()).undo();
    }
}