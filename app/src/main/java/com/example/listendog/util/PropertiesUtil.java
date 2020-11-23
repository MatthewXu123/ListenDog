package com.example.listendog.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.listendog.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    private static final String TAG = "PropertiesUtil";

    private static Properties props = new Properties();

    private static Properties initProperties(Context context){
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            InputStream in = context.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtil.class.getResourceAsStream("/assets/appConfig");
            props.load(in);
        } catch (Exception e1) {
            Log.e(TAG, e1.getMessage() );
        }
        return props;
    }

    public static void initAppConfig(Context context) {
        if(props.isEmpty())
            initProperties(context);
        AppConfig appConfig = AppConfig.INSTANCE;
        appConfig.setCallNumber(props.getProperty("call_number"));
        appConfig.setRequiredNumberGroup(props.getProperty("required_number_group"));
        appConfig.setCheckPeriod(Integer.valueOf(props.getProperty("check_period")));
        appConfig.setDefaultSim(Integer.valueOf(props.getProperty("default_sim")));
        appConfig.setNumberMissThreshold(Integer.valueOf(props.getProperty("number_miss_threshold")));
        appConfig.setRunDuration(Integer.valueOf(props.getProperty("run_duration")));
    }

    public static void outPut(Map<String, String> kvMap, Context context){
        try {
            for(Map.Entry<String, String> entry : kvMap.entrySet()){
                props.setProperty(entry.getKey(), entry.getValue());
            }
/*
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator
                    + "appConfig.properties");
*/
            FileOutputStream fos = context.openFileOutput("appConfig.properties",Context.MODE_PRIVATE);
            props.store(fos, null);
        } catch (Exception e) {
            Log.e(TAG, "outPut: " + e.getMessage() );
        }
    }
}


