package com.example.android.eventtimer;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.eventtimer.utils.Adapter;
import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.TimerUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String TIMER_COUNT = "timerCount";
    public static final String TIMER_BTN = "timerBtn";
    public static final String ADD_BTN = "addBtn";
    public static final String TIME_LIST_VIEW = "timeListView";

    Adapter adapter;
    HashMap<String, View> viewsMap = new HashMap<>();
    SharedPreferences prefs;

    TimerUtils timer;

    boolean timerStarted = false;
    boolean timerPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewsMap.put(TIMER_COUNT, findViewById(R.id.timer_textview));
        viewsMap.put(TIMER_BTN, findViewById(R.id.timer_btn));
        viewsMap.put(ADD_BTN, findViewById(R.id.timer_add_btn));
        viewsMap.put(TIME_LIST_VIEW, findViewById(R.id.times_list));

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        timer = new TimerUtils(viewsMap, prefs);

        adapter = new Adapter(this, timer);

        ((ListView)viewsMap.get(TIME_LIST_VIEW)).setAdapter(adapter);

        viewsMap.get(TIMER_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerStarted && !timerPaused) {
                    timer.pauseTimer();
                    timerPaused = true;
                    timerStarted = true;

                } else if(timerStarted && timerPaused) {
                    timer.resetTimer();
                    timerPaused = false;
                    timerStarted = false;

                } else {
                    timer.startTimer();
                    timerStarted = true;
                    timerPaused = false;
                }
            }
        });

        viewsMap.get(ADD_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.addTime();
                timerPaused = false;
                timerStarted = false;
                adapter.notifyDataSetChanged();
            }
        });

        ((ListView)viewsMap.get(TIME_LIST_VIEW)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                final Event clickedEvent = (Event) parent.getItemAtPosition(position);

                view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        timer.removeEvent(clickedEvent);
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                    }
                });
            }

        });
    }
}
