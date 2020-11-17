package com.example.listendog.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.listendog.service.AlarmService;

public class AlarmReceive extends BroadcastReceiver {
    private static final String TAG = "AlarmReceive";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        //循环启动Service
        Log.d(TAG, "onReceive: Enter...");
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);
    }
}