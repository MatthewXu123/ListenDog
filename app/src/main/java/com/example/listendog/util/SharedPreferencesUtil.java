package com.example.listendog.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreferencesUtil {

    private static final String TAG = "SharedPreferences";

    public static final String CALL_NUMBER = "call_number";
    public static final String REQUIRED_NUMBER_GROUP = "required_number_group";
    public static final String DEFAULT_SIM = "default_sim";
    public static final String CHECK_PERIOD = "check_period";
    public static final String NUMBER_MISS_THRESHOLD = "number_miss_threshold";
    public static final String RUN_DURATION = "run_duration";

    private static SharedPreferences.Editor editor = null;

    private static SharedPreferences sharedPreferences = null;

    private static final SharedPreferencesUtil INSTANCE = new SharedPreferencesUtil();

    private SharedPreferencesUtil(){
    }

    public static SharedPreferencesUtil getInstance(Context context){
        sharedPreferences = context.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Map<String, ?> map = sharedPreferences.getAll();
        if(map == null || map.size() == 0){
            initAppConfig();
        }
        return INSTANCE;
    }

    private static void initAppConfig(){
        editor.putString(CALL_NUMBER, "15366203524");
        editor.putString(REQUIRED_NUMBER_GROUP, "051266628226,051266628227");
        editor.putInt(DEFAULT_SIM, 0);
        editor.putInt(CHECK_PERIOD, 15);
        editor.putInt(NUMBER_MISS_THRESHOLD, 2);
        editor.putInt(RUN_DURATION, 1);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, "");
    }

    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public void putString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    public void putInt(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }

    public void putStringMap(Map<String, String> map){
        for(Map.Entry<String, String> entry : map.entrySet()){
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public void putIntMap(Map<String, Integer> map){
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            editor.putInt(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

}

