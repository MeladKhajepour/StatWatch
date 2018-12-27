package com.example.android.statwatch.timerComponents;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;

import com.example.android.statwatch.MainActivity;

import static com.example.android.statwatch.utils.Constants.TIMER_DURATION;
import static com.example.android.statwatch.utils.Constants.PAUSED;
import static com.example.android.statwatch.utils.Constants.READY;
import static com.example.android.statwatch.utils.Constants.TEXT_VIEW_RECEIVER;
import static com.example.android.statwatch.utils.Constants.TIMING;

public class TimerFragment extends Fragment { // 12/25/18 - "it cant get any simpler than this"
    private TimerService timerService;
    private TimerViews timerViews;
    private boolean bound = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        timerViews = new TimerViews(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity app = (MainActivity) requireContext();

        app.registerReceiver(textViewRefresher, new IntentFilter(TEXT_VIEW_RECEIVER));
        app.startService( new Intent(app, TimerService.class));

        if(!bound) {
            app.bindService(new Intent(app, TimerService.class), connection, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity app = (MainActivity) requireContext();

        app.unregisterReceiver(textViewRefresher);

        if(bound) {
            app.unbindService(connection);
            bound = false;
        }
    }

    /*
     * Start of public methods
     */

    public void clearTimer() { // called from MainActivity action menu
        timerService.onReset(true);
    }

    public void undo() { // called from EventsFragment snackbar action
        timerService.onUndo(); //just undos resetting the Timer index
    }

    void startTimer() {
        timerService.onStart();
    }

    void pauseTimer() {
        timerService.onPause();
    }

    void resumeTimer() {
        timerService.onResume();
    }

    void resetTimer() {
        timerService.onReset();
    }

    void addEvent() {
        timerService.onAdd();
        ((MainActivity) requireContext()).refreshComponents();
    }

    String getTimerState() {
        return timerService.getTimerState();
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) { // when reentering app
            timerService = ((TimerService.TimerBinder)service).getService();
            timerService.setConnection(true);

            switch (getTimerState()) { // state set in Timer class

                case TIMING:
                    resumeTimer();
                    break;

                case PAUSED:
                    timerService.loadPausedTime();
                    break;

                case READY:

                default:
                    resetTimer();
                    break;
            }

            timerViews.transitionViews(getTimerState());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg) {
            timerService.setConnection(false);
            timerService = null;
        }
    };

    // for updating the timer TextView from TimerService
    private BroadcastReceiver textViewRefresher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(TIMER_DURATION, 0);
            timerViews.updateTime(time);
        }
    };
}