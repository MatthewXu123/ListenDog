package com.example.listendog.util;

import android.util.Log;

import com.example.listendog.AppConfig;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final String TAG = "PropertiesUtil";

    private static Properties props = new Properties();

    private static Properties initProperties(){
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            //InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            InputStream in = PropertiesUtil.class.getResourceAsStream("/assets/appConfig.properties");
            props.load(in);
        } catch (Exception e1) {
            Log.e(TAG, e1.getMessage() );
        }
        return props;
    }

    public static void initAppConfig() {
        if(props.isEmpty())
            initProperties();
        AppConfig appConfig = AppConfig.INSTANCE;
        appConfig.setCallNumber(props.getProperty("call_number"));
        appConfig.setRequiredNumberGroup(props.getProperty("required_number_group"));
        appConfig.setCheckPeriod(Integer.valueOf(props.getProperty("check_period")));
        appConfig.setDefaultSim(Integer.valueOf(props.getProperty("default_sim")));
        appConfig.setNumberMissThreshold(Integer.valueOf(props.getProperty("number_miss_threshold")));
        appConfig.setRunDuration(Integer.valueOf(props.getProperty("run_duration")));
    }
}


