package com.example.android.statwatch.timerComponents;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.android.statwatch.eventComponents.EventsManager;

import java.util.Objects;

import static com.example.android.statwatch.utils.Constants.ADD_EVENT;
import static com.example.android.statwatch.utils.Constants.NOTIFICATION_DISMISSED;
import static com.example.android.statwatch.utils.Constants.PAUSE_TIMER;
import static com.example.android.statwatch.utils.Constants.PREFS;
import static com.example.android.statwatch.utils.Constants.REFRESH_RATE;
import static com.example.android.statwatch.utils.Constants.RESET_TIMER;
import static com.example.android.statwatch.utils.Constants.RESUME_TIMER;
import static com.example.android.statwatch.utils.Constants.START_TIMER;
import static com.example.android.statwatch.utils.Constants.TEXT_VIEW_RECEIVER;
import static com.example.android.statwatch.utils.Constants.TIMER_DURATION;
import static com.example.android.statwatch.utils.Constants.TIMING;

public class TimerService extends Service {
    private Timer timer;
    private TimerNotifications timerNotifications;
    private IBinder binder = new TimerBinder();
    private SharedPreferences prefs;
    private Handler handler = new Handler();
    private boolean isConnected;

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getBaseContext();
        prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        timer = new Timer(prefs);
        timerNotifications = new TimerNotifications(this);

        IntentFilter notificationFilters = new IntentFilter();
        notificationFilters.addAction(START_TIMER);
        notificationFilters.addAction(PAUSE_TIMER);
        notificationFilters.addAction(RESUME_TIMER);
        notificationFilters.addAction(RESET_TIMER);
        notificationFilters.addAction(ADD_EVENT);
        notificationFilters.addAction(NOTIFICATION_DISMISSED);
        context.registerReceiver(notificationReceivers, notificationFilters);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Context context = getBaseContext();
        context.unregisterReceiver(notificationReceivers);
    }

    /*
     * Start of public methods
     */

    void setConnection(boolean isConnected) {
        this.isConnected = isConnected;
    }

    void onStart() {
        timer.onStart();

        if(isConnected) {
            handler.post(updateFragmentRunnable);
        }
    }

    void onPause() {
        timer.onPause();

        if(isConnected) {
            handler.removeCallbacks(updateFragmentRunnable);
            updateFragment();
        }
    }

    void onResume() {
        timer.onResume();

        if(isConnected) {
            handler.post(updateFragmentRunnable);
        }
    }

    void onReset() {
        onReset(false);
    }

    void onReset(boolean clearIndex) {
        timer.onReset();

        if(clearIndex) {
            timer.resetTimerIndex();
        }

        if(isConnected) {
            handler.removeCallbacks(updateFragmentRunnable);
            updateFragment();
        }
    }

    void onUndo() {
        timer.onUndo();
    }

    void onAdd(long millis) {

        EventsManager.addEvent(prefs, timer.createEvent(millis));

        if(isConnected) {
            handler.removeCallbacks(updateFragmentRunnable);
            updateFragment();
        }
    }

    String getTimerState() {
        return timer.getState();
    }

    long getTime() {
        return timer.getTime();
    }

    long lastAddedTime() {
        return EventsManager.getLatestTime(prefs);
    }

    void loadPausedTime() {
        timer.loadPausedTime();
        updateFragment();
    }

    /*
     * Start of private methods
     */

    private Runnable updateFragmentRunnable = new Runnable() {
        public void run() {
            updateFragment();
            handler.postDelayed(this, REFRESH_RATE);
        }
    };

    private Runnable notificationsRunnable = new Runnable() {
        @Override
        public void run() {
            timerNotifications.postNotification(false);
            handler.postDelayed(this, (long) (REFRESH_RATE*9.99));
        }
    };

    // sends broadcast to TimerFragment to update TextView
    private void updateFragment() {
        Intent fragmentIntent = new Intent(TEXT_VIEW_RECEIVER);
        fragmentIntent.putExtra(TIMER_DURATION, timer.getTime());
        sendBroadcast(fragmentIntent);
    }

    /*
     * from TimerNotifications actions
     */
    private final BroadcastReceiver notificationReceivers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (Objects.requireNonNull(intent.getAction())) {

                case START_TIMER:
                    timer.onStart();
                    makeForegroundService();
                    break;

                case PAUSE_TIMER:
                    timer.onPause();
                    removeForegroundService(false);
                    break;

                case RESUME_TIMER:
                    timer.onResume();
                    makeForegroundService();
                    break;

                case RESET_TIMER:
                    timer.onReset();
                    removeForegroundService(false);
                    break;

                case ADD_EVENT:
                    onAdd(-1);
                    removeForegroundService(true);
                    break;

                case NOTIFICATION_DISMISSED:
                    onReset();
                    break;
            }
        }
    };

    private void makeForegroundService() {
        startForeground(1, timerNotifications.getBuilder().build());
        handler.post(notificationsRunnable);
    }

    private void removeForegroundService(boolean eventAdded) {
        stopForeground(false);
        handler.removeCallbacks(notificationsRunnable);
        timerNotifications.postNotification(eventAdded);
    }

    @Override
    public void onRebind(Intent intent) {// gets called AFTER ServiceConnection in TimerFragment
        handler.removeCallbacks(notificationsRunnable);
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(timer.getState().equals(TIMING)) {
            makeForegroundService();
            handler.removeCallbacks(updateFragmentRunnable);
        }
        return true;
    }

    class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
}