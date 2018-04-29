package com.example.android.eventtimer.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class TimerService extends Service {

    @Override //started when an activity calls for this service to start
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service Started");

        //TODO make function to take time from intent and continue timing
        //TODO make function to make notification of time with buttons to stop, add and reset timer
        return super.onStartCommand(intent, flags, startId);
    }

    @Override //called when service is ending
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
