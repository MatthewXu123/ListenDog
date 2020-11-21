package com.example.listendog.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.listendog.AppConfig;

public class BaseService extends Service {

    protected static final AppConfig APP_CONFIG = AppConfig.INSTANCE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
