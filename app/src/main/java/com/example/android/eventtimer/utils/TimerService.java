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

import static com.example.android.eventtimer.utils.Constants.ADD_EVENT_COMMAND;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_STOPPED;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.RESET_TIMER_COMMAND;
import static com.example.android.eventtimer.utils.Constants.START_TIMER_COMMAND;
import static com.example.android.eventtimer.utils.Constants.STOP_TIMER_COMMAND;
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
    private Intent fragmentIntent;
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
        fragmentIntent = new Intent(TIMER_FRAGMENT_RECEIVER);
        notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        IntentFilter intentFilters = new IntentFilter();
        intentFilters.addAction(START_TIMER_COMMAND);
        intentFilters.addAction(STOP_TIMER_COMMAND);
        intentFilters.addAction(RESET_TIMER_COMMAND);
        intentFilters.addAction(ADD_EVENT_COMMAND);
        context.registerReceiver(notificationReceivers, intentFilters);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    public void startTimerCommand() {
        timer.startTimer();
        handler.post(autoRefreshFragment);
    }

    public void continueTiming() {
        timer.resumeTimer();
        handler.post(autoRefreshFragment);
    }

    public void stopTimerCommand() {
        timer.stopTimer();
        handler.removeCallbacks(autoRefreshFragment);
        refreshFragmentTv();
    }

    public void reloadStoppedStateCommand() {
        timer.reloadStopState();
        refreshFragmentTv();
    }

    public void resetTimer() {
        timer.resetTimer();
        refreshFragmentTv();
    }

    public Event addEventCommand() {
        Event event = timer.createEvent();
        timer.resetTimer();
        refreshFragmentTv();

        return event;
    }

    public void resetIndex() {
        timer.resetTimerIndex();
        timer.resetTimer();
        handler.removeCallbacks(autoRefreshFragment);
        refreshFragmentTv();
    }

    public void undoResetIndex() {
        timer.undoResetTimerIndex();
    }

    private Runnable autoRefreshFragment = new Runnable() {
        public void run() {
            refreshFragmentTv();
            handler.postDelayed(this, 10);
        }
    };

    private void refreshFragmentTv() {
        fragmentIntent.putExtra(TV_TIME, timer.getElapsedTime());
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
        long timeMillis = timer.getElapsedTime();

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

        if(Timer.getTimerState(prefs).equals(IS_READY)) {
            mBuilder.addAction(R.drawable.start_icon, "Start timer", startTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_STOPPED).equals(IS_STOPPED)
                || prefs.getString(TIMER_STATE, IS_TIMING).equals(IS_TIMING)) {
            mBuilder.addAction(R.drawable.reset_icon, "Reset timer", resetTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_TIMING).equals(IS_TIMING)) {
            mBuilder.addAction(R.drawable.stop_icon, "Stop timer", stopTimerAction());
        }

        if(prefs.getString(TIMER_STATE, IS_STOPPED).equals(IS_STOPPED)) {
            mBuilder.addAction(R.drawable.add_icon, "Add event", addEventAction());
        }

        return mBuilder;
    }

    private PendingIntent startTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(START_TIMER_COMMAND), 0);
    }

    private PendingIntent stopTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(STOP_TIMER_COMMAND), 0);
    }

    private PendingIntent resetTimerAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(RESET_TIMER_COMMAND), 0);
    }

    private PendingIntent addEventAction() {
        return PendingIntent.getBroadcast(context, 0, new Intent(ADD_EVENT_COMMAND), 0);
    }

    //
    //From notification actions
    //
    private BroadcastReceiver notificationReceivers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case START_TIMER_COMMAND:
                    timer.startTimer();
                    handler.post(startNotifications);
                    break;

                case STOP_TIMER_COMMAND:
                    timer.stopTimer();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification().build());
                    break;

                case RESET_TIMER_COMMAND:
                    timer.resetTimer();
                    stopNotifications();
                    notificationManager.notify(notificationId, buildNotification().build());
                    break;

                case ADD_EVENT_COMMAND:
                    Timer.setTimerState(prefs, IS_READY);
                    notificationManager.notify(notificationId, buildNotification("Time of " +
                            Timer.formatDuration(timer.getElapsedTime()) + " has been added").build());

                    addEventCommand();// todo fix (doesnt work)
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
