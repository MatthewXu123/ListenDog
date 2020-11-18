package com.example.listendog.util;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final String TAG = "PropertiesUtil";

    public static final String CALL_NUMBER = "call_number";
    public static final String REQUIRED_NUMBER_GROUP = "required_number_group";
    public static final String DEFAULT_SIM = "default_sim";
    public static final String CALL_DURATION = "call_duration";
    public static final String NUMBER_MISS_THRESHOLD = "number_miss_threshold";
    public static final String RUN_DURATION  = "run_duration";

    public static Properties getProperties(Context c){
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            Log.e(TAG, e1.getMessage() );
        }
        return props;
    }
}


