package com.example.listendog.service;

import android.content.Intent;
import android.util.Log;

import com.example.listendog.MainActivity;

public class CallLogCheckSerivce extends BaseService{

    private static final String TAG = "CallLogCheckSerivce";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Enter the CallLogCheckSerivce...");
        MainActivity.getInstance().setListView();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: The CallLogCheckSerivce is destroyed...");
        super.onDestroy();
    }
}
