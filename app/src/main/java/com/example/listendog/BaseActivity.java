package com.example.listendog;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.listendog.service.BaseService;
import com.example.listendog.util.SharedPreferencesUtil;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    //protected static final AppConfig APP_CONFIG = AppConfig.INSTANCE;

    protected SharedPreferencesUtil SHARED_PREFERENCES_UTIL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d(TAG, getClass().getSimpleName());
        SHARED_PREFERENCES_UTIL = SharedPreferencesUtil.getInstance(BaseActivity.this);
    }
}
