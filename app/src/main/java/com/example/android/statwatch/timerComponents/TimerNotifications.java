package com.example.android.statwatch.timerComponents;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

import java.util.Objects;

import static com.example.android.statwatch.utils.Constants.ADD_EVENT;
import static com.example.android.statwatch.utils.Constants.NOTIFICATION_DISMISSED;
import static com.example.android.statwatch.utils.Constants.PAUSED;
import static com.example.android.statwatch.utils.Constants.PAUSE_TIMER;
import static com.example.android.statwatch.utils.Constants.READY;
import static com.example.android.statwatch.utils.Constants.RESET_TIMER;
import static com.example.android.statwatch.utils.Constants.RESUME_TIMER;
import static com.example.android.statwatch.utils.Constants.START_TIMER;
import static com.example.android.statwatch.utils.Constants.TIMING;

class TimerNotifications {
    private TimerService timerService;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    TimerNotifications(TimerService timerService) {
        this.timerService = timerService;
        notificationManager = Objects.requireNonNull((NotificationManager) timerService.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    NotificationCompat.Builder getBuilder() {

        if(builder == null) {
            createBuilder();
        }
        return builder;
    }

    void postNotification(boolean eventAdded) {
        int notificationId = 1;
        updateBuilderContent(eventAdded);
        notificationManager.notify(notificationId, builder.build());
    }

    private void createBuilder() {
        String channelId = "channel-01";
        Intent intent = new Intent(timerService, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(timerService, 0, intent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelName = "StatWatch";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager  = (NotificationManager) timerService.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        builder = new NotificationCompat.Builder(timerService, channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);
    }

    private void updateBuilderContent(boolean eventAdded) {
        builder.setContentTitle(Timer.formatDuration(timerService.getTime(), true));
        builder.mActions.clear();

        switch (timerService.getTimerState()) {

            case TIMING:
                builder.setContentText("StatWatch is running. Tap to return or expand for actions");
                builder.addAction(R.drawable.icon_reset, "Reset", resetTimerAction());
                builder.addAction(R.drawable.icon_pause, "Pause", pauseTimerAction());
                builder.addAction(R.drawable.icon_add, "Add", addEventAction());
                break;

            case PAUSED:
                builder.setContentText("StatWatch is paused. Add event or swipe away to reset");
                builder.addAction(R.drawable.icon_reset, "Reset", resetTimerAction());
                builder.addAction(R.drawable.icon_start, "Resume", resumeTimerAction());
                builder.addAction(R.drawable.icon_add, "Add", addEventAction());
                builder.setDeleteIntent(PendingIntent.getBroadcast(timerService, 0, new Intent(NOTIFICATION_DISMISSED), 0));
                break;

            case READY:

                if(eventAdded) {
                    builder.setContentText("Event added");
                    builder.setContentTitle(Timer.formatDuration(timerService.lastAddedTime(), true));
                } else {
                    builder.setContentText("StatWatch reset");
                }

                builder.addAction(R.drawable.icon_start, "Start", startTimerAction());
                break;
        }
    }

    /*
     * broadcasts sent to TimerService
     */

    private PendingIntent resetTimerAction() {
        return PendingIntent.getBroadcast(timerService, 0, new Intent(RESET_TIMER), 0);
    }

    private PendingIntent pauseTimerAction() {
        return PendingIntent.getBroadcast(timerService, 0, new Intent(PAUSE_TIMER), 0);
    }

    private PendingIntent addEventAction() {
        return PendingIntent.getBroadcast(timerService, 0, new Intent(ADD_EVENT), 0);
    }

    private PendingIntent resumeTimerAction() {
        return PendingIntent.getBroadcast(timerService, 0, new Intent(RESUME_TIMER), 0);
    }

    private PendingIntent startTimerAction() {
        return PendingIntent.getBroadcast(timerService, 0, new Intent(START_TIMER), 0);
    }
}
