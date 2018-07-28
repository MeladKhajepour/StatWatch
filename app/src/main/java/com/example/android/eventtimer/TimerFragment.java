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

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.Timer;
import com.example.android.eventtimer.utils.TimerService;

import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_STOPPED;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.TIMER_FRAGMENT_RECEIVER;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;
import static com.example.android.eventtimer.utils.Constants.TV_TIME;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;

public class TimerFragment extends Fragment {
    private SharedPreferences prefs;
    private TimerService ts;
    private boolean bound;
    private TextView timerTv;
    private FloatingActionButton mainBtn;
    private FloatingActionButton resetBtn;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ts = ((TimerService.TimerBinder)service).getService();

            switch (prefs.getString(TIMER_STATE, IS_READY)) {

                case IS_TIMING:
                    continueTiming();
                    break;

                case IS_STOPPED:
                    reloadStoppedTime();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg) {
            ts = null;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        setupViews();
        setupHandlers();
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity app = (MainActivity) requireContext();

        app.registerReceiver(updateFragmentReceiver, new IntentFilter(TIMER_FRAGMENT_RECEIVER));
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

        app.unregisterReceiver(updateFragmentReceiver);

        if(bound) {
            app.unbindService(connection);
            bound = false;
        }
    }

    public void clearTimer() {
        ts.resetIndex();
        resetButtons();
    }

    public void undoResetIndex() {
        ts.undoResetIndex();
    }

    private void setupViews() {
        MainActivity app = (MainActivity) requireContext();

        timerTv = app.findViewById(R.id.timer_textview);
        mainBtn = app.findViewById(R.id.timer_btn);
        resetBtn = app.findViewById(R.id.timer_reset_btn);
    }

    private void setupHandlers() {
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (prefs.getString(TIMER_STATE, IS_READY)) {

                    case IS_READY:
                        startTimer();
                        break;

                    case IS_TIMING:
                        stopTimer();
                        break;

                    case IS_STOPPED:
                        addEvent();
                        break;
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTime();
            }
        });
    }

    private void startTimer() {
        ts.startTimerCommand();
        changeTimerButton(R.color.colorAccent, R.drawable.stop_icon);
    }

    private void stopTimer() { //todo make it pause instead
        ts.stopTimerCommand();
        changeTimerButton(R.color.colorPrimary, R.drawable.add_icon);
        resetBtn.show();
    }

    private void addEvent() {
        Event event = ts.addEventCommand();
        ((MainActivity) requireContext()).eventAdded(event);
        resetButtons();
    }

    private void resetTime() {
        ts.resetTimer();
        resetButtons();
    }

    private void reloadStoppedTime() {
        ts.reloadStoppedStateCommand();
        changeTimerButton(R.color.colorPrimary, R.drawable.add_icon);
        resetBtn.show();
    }

    private void continueTiming() {
        ts.continueTiming();
        changeTimerButton(R.color.colorAccent, R.drawable.stop_icon);
    }

    private void resetButtons() {
        changeTimerButton(R.color.colorPrimary, R.drawable.start_icon);
        resetBtn.hide();
    }

    private void changeTimerButton(int colour, int icon) {
        mainBtn.setBackgroundTintList(getResources().getColorStateList(colour));
        mainBtn.setImageResource(icon);
    }

    private BroadcastReceiver updateFragmentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(TV_TIME, 0);
            timerTv.setText(Timer.formatDuration(time));
        }
    };
}