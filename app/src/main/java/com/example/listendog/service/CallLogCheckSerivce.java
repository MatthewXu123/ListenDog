package com.example.listendog.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.listendog.MainActivity;

public class CallLogCheckSerivce extends BaseService{

    private static final String TAG = "CallLogCheckSerivce";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Enter the CallLogCheckSerivce...");
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getInstance().setListView();
            }
        });
        AlarmManager callLogAlarmManger= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, CallLogCheckSerivce.class);
        PendingIntent pi = PendingIntent.getService(this,0, i ,0);
        long triggerAtTime = SystemClock.elapsedRealtime() + SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.RUN_DURATION) * 60 * 1000;
        callLogAlarmManger.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
