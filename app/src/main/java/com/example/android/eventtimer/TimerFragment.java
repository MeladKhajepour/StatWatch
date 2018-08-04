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
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Timer;
import com.example.android.eventtimer.utils.TimerService;

import static com.example.android.eventtimer.utils.Constants.HIDE_BUTTONS_DURATION;
import static com.example.android.eventtimer.utils.Constants.IS_PAUSED;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.SHOW_BUTTONS_DURATION;
import static com.example.android.eventtimer.utils.Constants.TIMER_FRAGMENT_RECEIVER;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;
import static com.example.android.eventtimer.utils.Constants.TV_TIME;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;

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
        public void onServiceConnected(ComponentName className, IBinder service) {
            ts = ((TimerService.TimerBinder)service).getService();

            switch (prefs.getString(TIMER_STATE, IS_READY)) {

                case IS_TIMING:
                    continueTiming();
                    break;

                case IS_PAUSED:
                    reloadPausedState();
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
        setupHandlers();
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

    public void clearTimer() {
        ts.resetIndex();
        onTimerReset();
    }

    public void undoResetIndex() {
        ts.undoResetIndex();
    }

    private void setupViews() {
        MainActivity app = (MainActivity) requireContext();

        timerTv = app.findViewById(R.id.timer_textview);
        buttonBar = app.findViewById(R.id.timer_buttons);
        resetBtn = app.findViewById(R.id.reset_button);
        startBtn = app.findViewById(R.id.start_pause_button);
        addBtn = app.findViewById(R.id.add_event_button);
    }

    private void setupHandlers() {
        resetBtn.setOnClickListener(btnListener);
        startBtn.setOnClickListener(btnListener);
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
        ts.createEvent();
        ((MainActivity) requireContext()).onEventAdded();
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

    private void onTimerStart() {
        startBtn.setBackground(getResources().getDrawable(R.drawable.red_to_grey_transition));
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.startTransition(SHOW_BUTTONS_DURATION);

        showPauseBtn();
        showResetBtn();
    }

    private void onTimerPause() {
        startBtn.setBackground(getResources().getDrawable(R.drawable.grey_to_background_transition));
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.startTransition(SHOW_BUTTONS_DURATION);
        showAddBtn();
        showStartBtn();
        showDivider();
    }

    private void onTimerResume() {
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.reverseTransition(SHOW_BUTTONS_DURATION);
        hideAddBtn();
        showPauseBtn();
        hideDivider();
    }

    private void onTimerReset() {
        if(addBtn.getVisibility() == View.VISIBLE) {
            startBtn.setBackground(getResources().getDrawable(R.drawable.background_to_red_transition));
            TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
            transition.startTransition(SHOW_BUTTONS_DURATION);

        } else if(addBtn.getVisibility() == View.INVISIBLE) {
            startBtn.setBackground(getResources().getDrawable(R.drawable.grey_to_red_transition));
            TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
            transition.startTransition(SHOW_BUTTONS_DURATION);
        }

        hideResetBtn();
        hideAddBtn();
        showStartBtn();
        hideDivider();
    }

    private void onEventAdded() {
        startBtn.setBackground(getResources().getDrawable(R.drawable.background_to_red_transition));
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.startTransition(SHOW_BUTTONS_DURATION);
        hideResetBtn();
        hideAddBtn();
        showStartBtn();
    }

    private void showResetBtn() {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(SHOW_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        resetBtn.setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private void showPauseBtn() {
        ((ImageView)startBtn.getChildAt(1)).setImageResource(R.drawable.pause_icon);
    }

    private void showAddBtn() {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(SHOW_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        addBtn.setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private void showStartBtn() {
        ((ImageView)startBtn.getChildAt(1)).setImageResource(R.drawable.start_icon);
    }

    private void showDivider() {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(SHOW_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        startBtn.getChildAt(0).setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private void hideResetBtn() {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        resetBtn.setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    private void hideAddBtn() {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        addBtn.setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    private void hideDivider() {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        startBtn.getChildAt(0).setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    private BroadcastReceiver updateTime = new BroadcastReceiver() { // from
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(TV_TIME, 0);
            timerTv.setText(Timer.formatDuration(time));
        }
    };
}