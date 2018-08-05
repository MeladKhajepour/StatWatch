package com.example.android.eventtimer.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;

import com.example.android.eventtimer.MainActivity;
import com.example.android.eventtimer.R;

import java.util.Objects;

import static com.example.android.eventtimer.utils.Constants.ADD_EVENT;
import static com.example.android.eventtimer.utils.Constants.FRAGMENT_REFRESH_RATE;
import static com.example.android.eventtimer.utils.Constants.IS_PAUSED;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.NOTIFICATION_DISMISSED;
import static com.example.android.eventtimer.utils.Constants.NOTIFICATION_REFRESH_RATE;
import static com.example.android.eventtimer.utils.Constants.PAUSE_TIMER;
import static com.example.android.eventtimer.utils.Constants.RESET_TIMER;
import static com.example.android.eventtimer.utils.Constants.RESUME_TIMER;
import static com.example.android.eventtimer.utils.Constants.START_TIMER;
import static com.example.android.eventtimer.utils.Constants.TIMER_FRAGMENT_RECEIVER;
import static com.example.android.eventtimer.utils.Constants.TV_TIME;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;

public class TimerService extends Service {
    private IBinder binder = new TimerBinder();
    private Timer timer;
    private SharedPreferences prefs;
    private Context context;
    private Handler handler = new Handler();
    private int notificationId = 1;
    private boolean inForeground = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(notificationReceivers);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getBaseContext();
        prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        timer = new Timer(prefs);

        IntentFilter intentFilters = new IntentFilter();
        intentFilters.addAction(START_TIMER);
        intentFilters.addAction(PAUSE_TIMER);
        intentFilters.addAction(RESET_TIMER);
        intentFilters.addAction(ADD_EVENT);
        intentFilters.addAction(NOTIFICATION_DISMISSED);
        context.registerReceiver(notificationReceivers, intentFilters);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void startTimer() {
        timer.start();
        handler.post(autoRefreshFragment);
    }

    public void pauseTimer() {
        timer.pause();
        handler.removeCallbacks(autoRefreshFragment);
        updateFragmentTime();
    }

    public void resumeTimer() {
        timer.resume();
        handler.post(autoRefreshFragment);
    }

    public void resetTimer() {
        timer.reset();

        handler.removeCallbacks(autoRefreshFragment);
        updateFragmentTime();
    }

    public void addEvent() {
        Event event = timer.addEvent();

        EventsManager.addToList(prefs, event);
        StatsManager.calculateStats(prefs, event);
        updateFragmentTime();
    }

    public void continueTiming() {
        handler.post(autoRefreshFragment);
    }

    public void reloadPausedState() {
        timer.reloadPausedState();
        updateFragmentTime();
    }

    public void clearTimer() {
        resetTimer();

        Timer.resetTimerIndex(prefs);
    }

    private Runnable autoRefreshFragment = new Runnable() {
        public void run() {
            updateFragmentTime();
            handler.postDelayed(this, FRAGMENT_REFRESH_RATE);
        }
    };

    private void updateFragmentTime() {
        Intent fragmentIntent = new Intent(TIMER_FRAGMENT_RECEIVER);
        fragmentIntent.putExtra(TV_TIME, timer.getTime());
        sendBroadcast(fragmentIntent);
    }

    private Runnable startNotifications = new Runnable() {
        @Override
        public void run() {
            NotificationManager notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(inForeground && notificationManager != null) {
                notificationManager.notify(notificationId, buildNotification("StatWatch is running. Tap to return or expand for actions").build());
            } else {
                startForeground(notificationId, buildNotification("StatWatch is running. Tap to return or expand for actions").build());
                inForeground = true;
            }

            handler.postDelayed(this, NOTIFICATION_REFRESH_RATE);
        }
    };

    private NotificationCompat.Builder buildNotification(String text) {
        String channelId = "channel-01";
        long timeMillis = timer.getTime();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelName = "StatWatch";
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

            NotificationManager notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Timer.formatNotificationDuration(timeMillis))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        return addActions(mBuilder);
    }

    private NotificationCompat.Builder addActions(NotificationCompat.Builder mBuilder) {

        switch (Timer.getState(prefs)) {

            case IS_TIMING:
                mBuilder.addAction(R.drawable.reset_icon, "Reset", resetTimerAction());
                mBuilder.addAction(R.drawable.pause_icon, "Pause", pauseTimerAction());
                mBuilder.addAction(R.drawable.add_icon, "Add", addEventAction());
                break;

            case IS_PAUSED:
                mBuilder.addAction(R.drawable.reset_icon, "Reset", resetTimerAction());
                mBuilder.addAction(R.drawable.start_icon, "Resume", resumeTimerAction());
                mBuilder.addAction(R.drawable.add_icon, "Add", addEventAction());

                mBuilder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, new Intent(NOTIFICATION_DISMISSED), 0));
                break;

            case IS_READY:
                mBuilder.addAction(R.drawable.start_icon, "Start", startTimerAction());

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((NotificationManager) Objects.requireNonNull(
//                                context.getSystemService(Context.NOTIFICATION_SERVICE))
//                        ).cancel(notificationId);
//                    }
//                }, 3000);
                break;
        }

        return mBuilder;
    }

    private PendingIntent resetTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(RESET_TIMER), 0);
    }

    private PendingIntent pauseTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(PAUSE_TIMER), 0);
    }

    private PendingIntent addEventAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(ADD_EVENT), 0);
    }

    private PendingIntent resumeTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(RESUME_TIMER), 0);
    }

    private PendingIntent startTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(START_TIMER), 0);
    }

    //
    //From notification actions
    //
    private final BroadcastReceiver notificationReceivers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Objects.requireNonNull(notificationManager);

            switch (Objects.requireNonNull(intent.getAction())) {
                case START_TIMER:
                    timer.start();
                    handler.post(startNotifications);
                    break;

                case PAUSE_TIMER:
                    timer.pause();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification("StatWatch is paused. " +
                            "Add event or swipe away to reset").build());
                    break;

                case RESUME_TIMER:
                    timer.resume();
                    handler.post(startNotifications);
                    notificationManager.notify(notificationId, buildNotification("StatWatch is running. " +
                            "Tap to return or expand for actions").build());
                    break;

                case RESET_TIMER:
                    timer.reset();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification("StatWatch reset").build());
                    break;

                case ADD_EVENT:
                    notificationManager.notify(notificationId, buildNotification("Time of " +
                            Timer.formatDuration(timer.getTime()) + " has been added").build());

                    stopNotifications();
                    addEvent();
                    break;

                case NOTIFICATION_DISMISSED:
                    resetTimer();
                    break;
            }
        }
    };

    private void stopNotifications() {
        handler.removeCallbacks(startNotifications);
        inForeground = false;
        stopForeground(false);
    }

    @Override
    public void onRebind(Intent intent) {
        handler.removeCallbacks(startNotifications);
        inForeground = false;
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(Timer.getState(prefs).equals(IS_TIMING)) {
            handler.post(startNotifications);
            handler.removeCallbacks(autoRefreshFragment);
        }
        return true;
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
