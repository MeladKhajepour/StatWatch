package com.example.android.eventtimer;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.Timer;

import static com.example.android.eventtimer.utils.EventManager.PREFS;

public class TimerFragment extends Fragment {

    //TODO: change the textview into a linear layout containing the h:m:s
    private FloatingActionButton timerBtn;
    private FloatingActionButton resetBtn;
    private Timer timer;
    private AddEventListener mainActivityListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityListener = (AddEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddEventListener");
        }
    }

    public void init(MainActivity app) {
        setupViews(app);
        setupHandlers();
    }

    public void resetTimerLabel() {
        timer.resetLabel();
    }

    public interface AddEventListener {
        void onEventReceived(Event event);
    }

    private void setupViews(MainActivity app) {
        TextView textView = app.findViewById(R.id.timer_textview);
        timerBtn = app.findViewById(R.id.timer_btn);
        resetBtn = app.findViewById(R.id.timer_reset_btn);

        timer = new Timer(textView, app.getSharedPreferences(PREFS, Context.MODE_PRIVATE));
    }

    private void setupHandlers() {
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (timer.getTimerState()) {

                    case RESET:
                        timer.startTimer();
                        setMainButtonIcon(R.drawable.stop_icon);
                        changeButtonColour(R.color.stopButtonColour);
                        break;

                    case TIMING:
                        timer.stopTimer();
                        setMainButtonIcon(R.drawable.add_icon);
                        changeButtonColour(R.color.startButtonColour);
                        resetBtn.show();
                        break;

                    case STOPPED:
                        sendEventToActivity();
                        break;
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.resetTimer();
                resetButtons();
            }
        });
    }

    private void sendEventToActivity() {
        resetButtons();

        mainActivityListener.onEventReceived(timer.createEvent());
    }

    private void resetButtons() {
        setMainButtonIcon(R.drawable.start_icon);
        changeButtonColour(R.color.startButtonColour);
        resetBtn.hide();
    }

    private void changeButtonColour(int colour) {
        timerBtn.setBackgroundTintList(getResources().getColorStateList(colour));
    }

    private void setMainButtonIcon(int icon) {
        timerBtn.setImageResource(icon);
    }
}