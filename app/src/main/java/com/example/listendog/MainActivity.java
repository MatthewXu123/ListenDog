package com.example.listendog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listendog.service.AlarmService;
import com.example.listendog.util.DateUtil;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String[] COLUMNS = {CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE};
    private static final int INCOMING_CALL = 1;
    private static final int OUTGOING_CALL = 2;
    private static final int MISSED_CALL = 3;

    private static final List<String> REQUIRED_NUMBER_GROUP = Arrays.asList("051266628226", "051266628227");

    private Map<String, Integer> numberMissCountMap = new HashMap<>();

    private static final Integer NUMER_MISS_COUNT_THRESHOLD = 2;

    private static MainActivity INSTANCE;

    private ListView mLVShow;

    private Date lastQueryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: This activity is created...");
        INSTANCE = this;
        for(String requiredNumber : REQUIRED_NUMBER_GROUP){
            numberMissCountMap.put(requiredNumber, 0);
        }
        setContentView(R.layout.layout_main);
        mLVShow = (ListView) findViewById(R.id.lv_show);
        Intent intent=new Intent(this, AlarmService.class);
        startService(intent);
    }

    public static MainActivity getINSTANCE(){
        return INSTANCE;
    }

    public void setListView(){
        lastQueryTime = new Date();
        Log.d(TAG, "run: Enter this task...");
        List<Map<String, String>> callLogList = getCallLogList();
        if(callLogList.size() == 0 || !hasRequiredNumberGroup(callLogList)){
            TextView tvInfo = (TextView) findViewById(R.id.tv_info);
            tvInfo.setText("电话系统可能已暂停服务，请检查！");
            callPhone("15366203524");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, callLogList, R.layout.item_call_log
                , COLUMNS
                , new int[] { R.id.tv_name, R.id.tv_number, R.id.tv_date});
        mLVShow.setAdapter(adapter);
        TextView tvQueryTime = (TextView) findViewById(R.id.tv_query);
        tvQueryTime.setText(DateUtil.format(lastQueryTime, DateUtil.DEFAULT_DATETIME_FORMAT));
    }

    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    private List<Map<String, String>> getCallLogList() {
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
                        , CallLog.Calls.TYPE
                        }
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
        List<Map<String, String>> callLogs = new ArrayList<>();
        while (cursor.moveToNext()) {
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            if(type != OUTGOING_CALL){
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                if(REQUIRED_NUMBER_GROUP.contains(number)){
                    Date callDate = new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
                    if(isCallDateQualified(callDate)){
                        Map<String, String> map = new HashMap<>();
                        // Name
                        String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                        map.put(COLUMNS[0], StringUtils.isBlank(cachedName) ? "未备注的联系人" : cachedName);
                        // Number
                        map.put(COLUMNS[1], number);
                        // Date
                        map.put(COLUMNS[2], DateUtil.format(callDate, DateUtil.DEFAULT_DATETIME_FORMAT));
                        callLogs.add(map);
                    }
                }
            }
        }
        return callLogs;
    }

    /**
     *
     * @param callLogDate
     * @return
     */
    private boolean isCallDateQualified(Date callLogDate){
        Date lastHourTime = DateUtil.getLastHourTime(-1);
        return callLogDate.after(DateUtil.addMinutes(lastHourTime, -15)) && callLogDate.before(DateUtil.addMinutes(lastHourTime, 15));
    }

    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    /**
     *
     * @param callLogList
     * @return
     */
    public boolean hasRequiredNumberGroup(List<Map<String, String>> callLogList){
        List<String> numberList = new ArrayList<>();
        for(Map<String, String> callLog : callLogList){
            // Add all the number into the list.
            numberList.add(callLog.get(CallLog.Calls.NUMBER));
        }
        for(String requiredNumber : REQUIRED_NUMBER_GROUP){
            if(!numberList.contains(requiredNumber)){
                numberMissCountMap.put(requiredNumber, numberMissCountMap.get(requiredNumber) + 1);
            }
        }
        // To count the numbers whose missing times are equal to or larger than the threshold.
        int numberMissCount2 = 0;
        for(Map.Entry<String, Integer> entry : numberMissCountMap.entrySet()){
            String number = entry.getKey();
            Integer numberCount = entry.getValue();
            if(numberCount >= NUMER_MISS_COUNT_THRESHOLD){
                numberMissCount2 ++;
                // Reset the missing times.
                numberMissCountMap.put(number, 0);
            }
        }
        if(numberMissCount2 > 0)
            return false;
        return  true;
    }
}

