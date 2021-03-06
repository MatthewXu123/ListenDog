package com.example.listendog.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.listendog.MainActivity;

public class CallLogCheckAlarmService extends BaseService {
    private static final String TAG = "AlarmService";
    private static final int PENDING_REQUEST = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Enter...");
/*
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getInstance().setListView();
            }
        });
*/

        MainActivity.getInstance().setListView();
        //通过AlarmManager定时启动广播
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.RUN_DURATION) * (60 * 1000);//从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内
        Intent i=new Intent(this, CallLogCheckAlarmReceiver.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}