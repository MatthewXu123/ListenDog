package com.example.listendog.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.CallLog;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.listendog.MainActivity;
import com.example.listendog.R;
import com.example.listendog.util.DateUtil;
import com.example.listendog.util.PropertiesUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final String[] COLUMNS = {CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE};
    private static final int ONE_MINIUTE = 60*1000*3;
    private static final int PENDING_REQUEST = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Enter...");
        Properties properties = PropertiesUtil.getProperties(getApplicationContext());
        MainActivity.getINSTANCE().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getINSTANCE().setListView();
            }
        });

        //通过AlarmManager定时启动广播
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + Long.valueOf(properties.getProperty(PropertiesUtil.RUN_DURATION));//从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内
        Intent i=new Intent(this, AlarmReceive.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}