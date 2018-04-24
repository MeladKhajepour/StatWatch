package com.example.android.eventtimer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.Timer;

import static com.example.android.eventtimer.MainActivity.PREFS;

public class TimerFragment extends Fragment {

    private final String TIMER_READY_BUTTON_LABEL = "Start";
    private final String TIMER_STARTED_BUTTON_LABEL = "Stop";
    private final String TIMER_STOPPED_BUTTON_LABEL = "Reset";

    //TODO: change the textview into a linear layout containing the h:m:s
    private TextView textView;
    private Button timerBtn;
    private Button addBtn;
    private Timer timer;
    private OnEventAddedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnEventAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timer_fragment, container, false);

        textView = view.findViewById(R.id.timer_textview);
        timerBtn = view.findViewById(R.id.timer_btn);
        addBtn = view.findViewById(R.id.timer_add_btn);

        timer = new Timer(textView, getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHandlers(view);
    }

    private void setupHandlers(View view) {
        view.findViewById(R.id.timer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (timer.getTimerState()) {

                    case RESET:
                        timer.startTimer();
                        setMainButtonLabel(TIMER_STARTED_BUTTON_LABEL);
                        break;

                    case TIMING:
                        timer.stopTimer();
                        setMainButtonLabel(TIMER_STOPPED_BUTTON_LABEL);
                        showAddButton();
                        break;

                    case STOPPED:
                        timer.resetTimer();
                        setMainButtonLabel(TIMER_READY_BUTTON_LABEL);
                        hideAddButton();
                        break;
                }
            }
        });

        view.findViewById(R.id.timer_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEventToActivity();
            }
        });
    }

    //TODO: send Event to main activity
    private void sendEventToActivity() {
        setMainButtonLabel(TIMER_READY_BUTTON_LABEL);
        hideAddButton();

        listener.onEventAdded(timer.createEvent());
    }

    public interface OnEventAddedListener {
        void onEventAdded(Event event);
    }

    private void setMainButtonLabel(String text) {
        timerBtn.setText(text);
    }

    private void hideAddButton() {
        addBtnVisible(false);
    }

    private void showAddButton() {
        addBtnVisible(true);
    }

    private void addBtnVisible(boolean b) {
        float resetBtnWeight = b ? 5 : 1;
        float addBtnWeight = b ? 3 : 0;

        timerBtn.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                resetBtnWeight
        ));
        addBtn.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        addBtnWeight
                ));
    }
}