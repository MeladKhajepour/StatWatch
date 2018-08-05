package com.example.android.eventtimer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Timer;
import com.example.android.eventtimer.utils.TimerService;
import com.example.android.eventtimer.utils.TransitionUtils;

import static com.example.android.eventtimer.utils.Constants.ANIMATION_DURATION;
import static com.example.android.eventtimer.utils.Constants.IS_PAUSED;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.TIMER_FRAGMENT_RECEIVER;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;
import static com.example.android.eventtimer.utils.Constants.TV_TIME;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;
import static com.example.android.eventtimer.utils.TransitionUtils.transitionStartBtn;

public class TimerFragment extends Fragment {
    private SharedPreferences prefs;
    private TimerService ts;
    private boolean bound;
    private TextView timerTv;
    private LinearLayout buttonBar;
    private FrameLayout resetBtn;
    private FrameLayout startBtn;
    private FrameLayout addBtn;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) { //entering app again when timer running already
            ts = ((TimerService.TimerBinder)service).getService();

            switch (prefs.getString(TIMER_STATE, IS_READY)) {

                case IS_TIMING:
                    continueTiming();
                    break;

                case IS_PAUSED:
                    reloadPausedState();
                    break;

                default:
                    readyTimer();
                    break; // todo fix the time showing after adding event from notification and entering app again (might not have to do after data binding)
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg) {
            ts = null;
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.reset_button:
                    resetTimer();
                    break;

                case R.id.start_pause_button:

                    switch (Timer.getState(prefs)) {
                        case IS_TIMING:
                            pauseTimer();
                            break;
                        case IS_PAUSED:
                            resumeTimer();
                            break;
                        default:
                            startTimer();
                            break;
                    }
                    break;

                case R.id.add_event_button:
                    addEvent();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        setupViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity app = (MainActivity) requireContext();

        app.registerReceiver(updateTime, new IntentFilter(TIMER_FRAGMENT_RECEIVER));
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

        app.unregisterReceiver(updateTime);

        if(bound) {
            app.unbindService(connection);
            bound = false;
        }
    }

    /*
     * Start of public methods
     */

    public void clearTimer() {
        ts.clearTimer();
        onTimerReset();
    }

    /*
     * End of public methods
     */

    private void setupViews() {
        MainActivity app = (MainActivity) requireContext();

        timerTv = app.findViewById(R.id.timer_textview);// todo data binding
        buttonBar = app.findViewById(R.id.button_bar);
        resetBtn = app.findViewById(R.id.reset_button);
        resetBtn.setOnClickListener(btnListener);

        startBtn = app.findViewById(R.id.start_pause_button);
        startBtn.setOnClickListener(btnListener);

        addBtn = app.findViewById(R.id.add_event_button);
        addBtn.setOnClickListener(btnListener);
    }

    private void startTimer() {
        ts.startTimer();
        onTimerStart();
    }

    private void pauseTimer() {
        ts.pauseTimer();
        onTimerPause();
    }

    private void resumeTimer() {
        ts.resumeTimer();
        onTimerResume();
    }

    private void resetTimer() {
        ts.resetTimer();
        onTimerReset();
    }

    private void addEvent() {
        long t = System.currentTimeMillis();
        ts.addEvent();
        onEventAdded();
        System.out.println("TF.addToList: "+(System.currentTimeMillis()-t)+"ms");
    }

    private void reloadPausedState() {
        ts.reloadPausedState();
        onTimerPause();
    }

    private void continueTiming() {
        ts.continueTiming();
        onTimerResume();
    }

    private void readyTimer() {
        timerTv.setText(Timer.formatDuration(0));

        transitionStartBtn(requireContext(), startBtn, R.drawable.grey_to_red_transition);
        TransitionUtils.onTimerReset(buttonBar, resetBtn, startBtn, addBtn);
    }

    private void onTimerStart() {
        transitionStartBtn(requireContext(), startBtn, R.drawable.red_to_grey_transition);

        TransitionUtils.onTimerStart(buttonBar, resetBtn, startBtn);
    }

    private void onTimerPause() {
        transitionStartBtn(requireContext(), startBtn, R.drawable.grey_to_background_transition);

        TransitionUtils.onTimerPause(buttonBar, startBtn, addBtn);
    }

    private void onTimerResume() {
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.reverseTransition(ANIMATION_DURATION);

        TransitionUtils.onTimerResume(buttonBar, startBtn, addBtn);
    }

    private void onTimerReset() {
        if(addBtn.getVisibility() == View.VISIBLE) {
            transitionStartBtn(requireContext(), startBtn, R.drawable.background_to_red_transition);

        } else if(addBtn.getVisibility() == View.INVISIBLE) {
            transitionStartBtn(requireContext(), startBtn, R.drawable.grey_to_red_transition);
        }

        TransitionUtils.onTimerReset(buttonBar, resetBtn, startBtn, addBtn);
    }

    private void onEventAdded() {
        transitionStartBtn(requireContext(), startBtn, R.drawable.background_to_red_transition);

        TransitionUtils.onEventAdded(buttonBar, resetBtn, startBtn, addBtn);
        ((MainActivity) requireContext()).onEventAdded();
    }

    private BroadcastReceiver updateTime = new BroadcastReceiver() { // from
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(TV_TIME, 0);
            timerTv.setText(Timer.formatDuration(time));
        }
    };
}