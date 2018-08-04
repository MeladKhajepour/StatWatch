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

import static com.example.android.eventtimer.utils.Constants.ADD_EVENT;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_STOPPED;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.RESET_TIMER;
import static com.example.android.eventtimer.utils.Constants.RESUME_TIMER;
import static com.example.android.eventtimer.utils.Constants.START_TIMER;
import static com.example.android.eventtimer.utils.Constants.PAUSE_TIMER;
import static com.example.android.eventtimer.utils.Constants.TIMER_FRAGMENT_RECEIVER;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;
import static com.example.android.eventtimer.utils.Constants.TV_TIME;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;

public class TimerService extends Service {
    private IBinder binder = new TimerBinder();
    private Timer timer;
    private SharedPreferences prefs;
    private Context context;
    private Handler handler = new Handler();
    private NotificationManager notificationManager;
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

        context = getBaseContext(); //base context for the fragment activity and not application context
        prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        timer = new Timer(prefs);
        notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        IntentFilter intentFilters = new IntentFilter();
        intentFilters.addAction(START_TIMER);
        intentFilters.addAction(PAUSE_TIMER);
        intentFilters.addAction(RESET_TIMER);
        intentFilters.addAction(ADD_EVENT);
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
        updateFragmentTime();
    }

    public void createEvent() {
        Event event = timer.createEvent();

        EventsManager.addToList(prefs, event);
        StatsManager.calculateStats(prefs, event);
        timer.reset();
        updateFragmentTime();
    }

    public void continueTiming() {
        handler.post(autoRefreshFragment);
    }

    public void reloadPausedState() {
        timer.reloadPausedState();
        updateFragmentTime();
    }

    public void resetIndex() {
        timer.resetTimerIndex();
        timer.reset();
        handler.removeCallbacks(autoRefreshFragment);
        updateFragmentTime();
    }

    public void undoResetIndex() {
        timer.undoResetTimerIndex();
    }

    private Runnable autoRefreshFragment = new Runnable() {
        public void run() {
            updateFragmentTime();
            handler.postDelayed(this, 10);
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
            if(inForeground) {
                notificationManager.notify(notificationId, buildNotification().build());
            } else {
                startForeground(notificationId, buildNotification().build());
                inForeground = true;
            }

            handler.postDelayed(this, 500);
        }
    };

    private NotificationCompat.Builder buildNotification() {
        return buildNotification("Tap to return or expand for more actions");
    }

    private NotificationCompat.Builder buildNotification(String text) {
        String channelId = "channel-01";
        long timeMillis = timer.getTime();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelName = "Event Timer";
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Timer.formatNotificationDuration(timeMillis))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        if(Timer.getState(prefs).equals(IS_READY)) {
            mBuilder.addAction(R.drawable.start_icon, "Start timer", startTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_STOPPED).equals(IS_STOPPED)
                || prefs.getString(TIMER_STATE, IS_TIMING).equals(IS_TIMING)) {
            mBuilder.addAction(R.drawable.reset_icon, "Reset timer", resumeTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_TIMING).equals(IS_TIMING)) {
            mBuilder.addAction(R.drawable.stop_icon, "Stop timer", pauseTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_STOPPED).equals(IS_STOPPED)) {
            mBuilder.addAction(R.drawable.add_icon, "Add event", addEvent());
        }

        return mBuilder;
    }

    private PendingIntent startTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(START_TIMER), 0);
    }

    private PendingIntent pauseTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(PAUSE_TIMER), 0);
    }

    private PendingIntent resumeTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(RESET_TIMER), 0);
    }

    private PendingIntent resetTeimer() {
        return PendingIntent.getBroadcast(context, 0, new Intent(RESET_TIMER), 0);
    }

    private PendingIntent addEvent() {
        return PendingIntent.getBroadcast(context, 0, new Intent(ADD_EVENT), 0);
    }

    //
    //From notification actions
    //
    private BroadcastReceiver notificationReceivers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case START_TIMER:
                    timer.start();
                    handler.post(startNotifications);
                    break;

                case PAUSE_TIMER:
                    timer.pause();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification("Timer paused. " +
                            "Add event or swipe away to reset.").build());
                    break;

                case RESUME_TIMER: // todo
                    timer.resume();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification().build());
                    break;

                case RESET_TIMER:
                    timer.reset();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification().build());
                    break;

                case ADD_EVENT:
                    Timer.setState(prefs, IS_READY);
                    notificationManager.notify(notificationId, buildNotification("Time of " +
                            Timer.formatDuration(timer.getTime()) + " has been added").build());

                    createEvent();
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

        if(prefs.getString(TIMER_STATE, IS_TIMING).equals(IS_TIMING)) {
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
