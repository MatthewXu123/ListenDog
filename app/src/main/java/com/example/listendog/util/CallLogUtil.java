package com.example.listendog.util;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.example.listendog.MainActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallLogUtil {

    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    private List<Map<String, String>> getCallLogList() {
        MainActivity mainActivity = MainActivity.this;
        // 1.获得ContentResolver
        ContentResolver resolver = MainActivity.this.getContentResolver();
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
}
