package com.example.listendog.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.listendog.MainActivity;
import com.example.listendog.R;
import com.example.listendog.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class CallLogCheckSerivce extends BaseService{

    private static final String TAG = "CallLogCheckSerivce";

    private boolean isFirstRun = true;

    private Date lastQueryTime;

    private Date nextQueryTime;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Enter the CallLogCheckSerivce...");
        if(isFirstRun)
            nextQueryTime = DateUtil.getNextQueryTime(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.CHECK_PERIOD));

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastQueryTime = new Date();
                MainActivity.getInstance().setListView(isFirstRun, nextQueryTime, lastQueryTime);
            }
        });
        AlarmManager callLogAlarmManger= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, CallLogCheckSerivce.class);
        PendingIntent pi = PendingIntent.getService(this,0, i ,0);
        long triggerAtTime = SystemClock.elapsedRealtime()
                + (isFirstRun ? nextQueryTime.getTime() - System.currentTimeMillis() : SHARED_PREFERENCES_UTIL.RUN_DURATION * 60 * 1000);
        callLogAlarmManger.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        isFirstRun = false;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
