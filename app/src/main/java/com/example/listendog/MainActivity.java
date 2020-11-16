package com.example.listendog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ListView mLvShow;
    private List<Map<String, String>> dataList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLvShow = (ListView) findViewById(R.id.lv_show);
        final TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                getDataList();
            }
        };
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(task, 0 , 1000, TimeUnit.MILLISECONDS);
        /*adapter = new SimpleAdapter(this, dataList, R.layout.simple_calllog_item//
                , new String[] { "name", "number", "date", "duration", "type" }//
                , new int[] { R.id.tv_name, R.id.tv_number, R.id.tv_date, R.id.tv_duration, R.id.tv_type });
        mLvShow.setAdapter(adapter);*/
    }

    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    private List<SingleCallLog> getDataList() {
        // 1.获得ContentResolver
        ContentResolver resolver = getContentResolver();
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式
         *
         */
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[] { CallLog.Calls.CACHED_NAME// 通话记录的联系人
                        , CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DATE// 通话记录的日期
                        }
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
        List<SingleCallLog> callLogs = new ArrayList<>();
        while (cursor.moveToNext()) {
            SingleCallLog singleCallLog = new SingleCallLog();
            singleCallLog.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
            singleCallLog.setNumber(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
            singleCallLog.setTime(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
            callLogs.add(singleCallLog);
        }
        return callLogs;
    }
}

