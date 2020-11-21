package com.example.listendog.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CallLogUtil {

    public static final String[] COLUMNS = {CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE};
    private static final int INCOMING_CALL = 1;
    private static final int OUTGOING_CALL = 2;
    private static final int MISSED_CALL = 3;

    public static final String[] dualSimTypes = { "subscription", "Subscription",
            "com.android.phone.extra.slot",
            "phone", "com.android.phone.DialingMode",
            "simId", "simnum", "phone_type",
            "simSlot" };

    public static List<Map<String, String>> getSpecifiedCallLogList(AppCompatActivity appCompatActivity, List<String> requiredNumberGroup, int callDuration){
        Cursor cursor = getCallLogCursor(appCompatActivity);
        List<Map<String, String>> callLogs = new ArrayList<>();
        while (cursor.moveToNext()) {
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            if(type != OUTGOING_CALL){
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                if(requiredNumberGroup.contains(number)){
                    Date callDate = new Date(Long.valueOf(cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))));
                    if(isCallDateQualified(callDate, callDuration)){
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
     * @param appCompatActivity
     * @return
     */
    private static Cursor getCallLogCursor(AppCompatActivity appCompatActivity) {
        return appCompatActivity.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[] { CallLog.Calls.CACHED_NAME
                        , CallLog.Calls.NUMBER
                        , CallLog.Calls.DATE
                        , CallLog.Calls.TYPE
                }
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER
        );
    }

    /***
     *
     * @param callLogDate
     * @return
     */
    private static boolean isCallDateQualified(Date callLogDate, int callDuration){
        //Date lastHourTime = DateUtil.getLastHourTime(-1);
        Date lastHourTime = DateUtil.getLastHourTime(0);
        return callLogDate.after(DateUtil.addMinutes(lastHourTime, -1 * callDuration)) && callLogDate.before(DateUtil.addMinutes(lastHourTime, callDuration));
    }

    public static void callPhone(AppCompatActivity appCompatActivity, String number, int simId) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        for (int i=0; i < dualSimTypes.length; i++) {
            callIntent.putExtra(dualSimTypes[i], simId);
        }
        appCompatActivity.startActivity(callIntent);
    }
}
