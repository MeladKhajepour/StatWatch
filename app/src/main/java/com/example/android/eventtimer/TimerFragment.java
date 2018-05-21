package com.example.android.eventtimer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Timer;
import com.example.android.eventtimer.utils.TimerService;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;

/*
*
* This fragment contains the UI components related to the timer and its functionality. This fragment
* controls TimerService based on user-selected actions for timer functionality, and sends events to
* MainActivity if users want to add the time.
*
 */

public class TimerFragment extends Fragment {
    public static final String TIMER_FRAGMENT_RECEIVER = "com.example.android.eventtimer.TimerFragment";
    public static final String START_TIMER_COMMAND = "start";
    public static final String STOP_TIMER_COMMAND = "stop";
    public static final String RESET_TIMER_COMMAND = "reset";
    public static final String ADD_EVENT_COMMAND = "addEventCommand";
    public static final String TIMER_STATE = "timerState";
    public static final String STATE_TIMING = "stateTiming";
    public static final String STATE_RESET = "stateReset";
    public static final String STATE_STOPPED = "stateStopped";
    public static final String TV_TIME = "tvTime";
    public static final String START_TIME_MILLIS = "startTimeMillis";

    private TimerService ts;
    private boolean bound = false;
    private Intent intent;
    private TextView timerTv;
    private FloatingActionButton timerBtn;
    private FloatingActionButton resetBtn;
    private TimerFragmentInterface mainActivityListener;
    private Context context;
    private SharedPreferences prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        intent = new Intent(context, TimerService.class);

        try {
            mainActivityListener = (TimerFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TimerFragmentInterface");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        context.registerReceiver(updateFragmentReceiver, new IntentFilter(TIMER_FRAGMENT_RECEIVER));
        context.startService(intent);

        if(!bound) {
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
            bound = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        context.unregisterReceiver(updateFragmentReceiver);

        if(bound) {
            context.unbindService(conn);
            bound = false;
        }
    }

    public void init(MainActivity app) {
        setupViews(app);
        setupHandlers();
    }

    private void setupViews(MainActivity app) {
        timerTv = app.findViewById(R.id.timer_textview);
        timerBtn = app.findViewById(R.id.timer_btn);
        resetBtn = app.findViewById(R.id.timer_reset_btn);
    }

    private void setupHandlers() {
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (prefs.getString(TIMER_STATE, STATE_RESET)) {

                    case STATE_RESET:
                        startTimerCommand();
                        break;

                    case STATE_TIMING:
                        stopTimerCommand();
                        break;

                    case STATE_STOPPED:
                        addEventCommand();
                        resetButtons();
                        break;
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimerCommand();
                resetButtons();
            }
        });
    }

    /*
     *
     * Timer methods
     *
     */

    private void startTimerCommand() {
        ts.startTimerCommand();

        changeTimerButton(R.color.stopButtonColour, R.drawable.stop_icon);
    }

    private void stopTimerCommand() {
        ts.stopTimerCommand();

        changeTimerButton(R.color.startButtonColour, R.drawable.add_icon);
        resetBtn.show();
    }

    private void addEventCommand() {
        ts.addEventCommand();
        mainActivityListener.updateFragments();
    }

    private void resetTimerCommand() {
        ts.resetTimerCommand();
    }

    public void resetTimerIndex() {
        ts.resetTimerIndexCommand();

        resetButtons();
    }

    private void loadStoppedState() {
        ts.reloadStoppedStateCommand();

        changeTimerButton(R.color.startButtonColour, R.drawable.add_icon);
        resetBtn.show();
    }

    private void resumeTimer() {
        ts.resumeTimerCommand();

        changeTimerButton(R.color.stopButtonColour, R.drawable.stop_icon);
    }

    /*
     *
     * UI methods
     *
     */

    private void resetButtons() {
        changeTimerButton(R.color.startButtonColour, R.drawable.start_icon);
        resetBtn.hide();
    }

    private void changeTimerButton(int colour, int icon) {
        timerBtn.setBackgroundTintList(getResources().getColorStateList(colour));
        timerBtn.setImageResource(icon);
    }

    private BroadcastReceiver updateFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(TV_TIME, 0);
            timerTv.setText(Timer.formatDuration(time));
        }
    };

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ts = ((TimerService.TimerBinder)service).getService();

            switch (prefs.getString(TIMER_STATE, STATE_RESET)) {
                case STATE_RESET:
                    break;

                case STATE_TIMING:
                    resumeTimer();
                    break;

                case STATE_STOPPED:
                    loadStoppedState();
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            ts = null;
        }
    };

    public interface TimerFragmentInterface {
        void updateFragments();
    }
}