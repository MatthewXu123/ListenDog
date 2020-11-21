package com.example.listendog;

import android.app.Application;
import android.util.Log;

import com.example.listendog.util.PropertiesUtil;

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: App");
        // Initialize the app configuration.
        //PropertiesUtil.initAppConfig();
    }
}
