package com.example.listendog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.listendog.util.SharedPreferencesUtil;

public class BaseService extends Service {

    //protected static final AppConfig APP_CONFIG = AppConfig.INSTANCE;

    protected SharedPreferencesUtil SHARED_PREFERENCES_UTIL;

    @Override
    public void onCreate() {
        super.onCreate();
        SHARED_PREFERENCES_UTIL = SharedPreferencesUtil.getInstance(BaseService.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
