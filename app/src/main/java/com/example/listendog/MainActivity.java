package com.example.listendog;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.listendog.service.CallLogCheckSerivce;
import com.example.listendog.util.CallLogUtil;
import com.example.listendog.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static MainActivity INSTANCE;
    // About the permissions...
    private List<String> unPermissionList = new ArrayList<String>();
    private AlertDialog mPermissionDialog;
    private String mPackName ;
    private String[] permissionList = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.CALL_PHONE
    };

    // About the call logs
    private Map<String, List<Date>> numberMissCountMap = new HashMap<>();
    private ListView mLVShow;
    public static List<String> REQUIRED_NUMBER_GROUP = new ArrayList<>();

    public static MainActivity getInstance(){
        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        INSTANCE = this;
        //PropertiesUtil.initAppConfig(MainActivity.this);
        getRequiredNumberGroup();

        for(String requiredNumber : REQUIRED_NUMBER_GROUP){
            numberMissCountMap.put(requiredNumber, new ArrayList<>());
        }
        setContentView(R.layout.layout_main);
        mLVShow = (ListView) findViewById(R.id.lv_show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.getItem(0);
        if(item.getTitle().equals("start")){
            item.setIcon(R.drawable.icon_stop);
            item.setTitle("stop");
        }
        if(item.getTitle().equals("stop")){
            item.setIcon(R.drawable.icon_start);
            item.setTitle("start");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LinearLayout lastQueryLayout = (LinearLayout)findViewById(R.id.llo_main_last_query);
        LinearLayout nextQueryLayout = (LinearLayout)findViewById(R.id.llo_main_next_query);
        ListView lv = (ListView)findViewById(R.id.lv_show);
        switch (item.getItemId()){
            case R.id.item_setting:{
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_start:{
                if(item.getTitle().equals(MainActivity.this.getResources().getText(R.string.item_title_start))){
                    // To clear the map
                    //clearNumberMissCountMap();

                    item.setIcon(R.drawable.icon_stop);
                    item.setTitle(R.string.item_title_stop);
                    Intent intent=new Intent(MainActivity.this, CallLogCheckSerivce.class);
                    startService(intent);
                    lastQueryLayout.setVisibility(View.VISIBLE);
                    nextQueryLayout.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.VISIBLE);
                }else if(item.getTitle().equals(MainActivity.this.getResources().getText(R.string.item_title_stop))){
                    item.setIcon(R.drawable.icon_start);
                    item.setTitle(R.string.item_title_start);
                    lastQueryLayout.setVisibility(View.INVISIBLE);
                    nextQueryLayout.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.INVISIBLE);
                    TextView tvMainInfo = (TextView)findViewById(R.id.tv_info);
                    tvMainInfo.setText(R.string.tv_main_info_tostart);
                    Intent intent=new Intent(MainActivity.this, CallLogCheckSerivce.class);
                    stopService(intent);
                }
                break;
            }
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void startCallLogAlarmManager(){
        AlarmManager callLogAlarmManger= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i=new Intent(MainActivity.this, CallLogCheckSerivce.class);
        PendingIntent pIntent=PendingIntent.getService(this,0,i,0);
/*
        callLogAlarmManger.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),(long)(1 * (60 * 1000)),
                pIntent );
*/
        callLogAlarmManger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10 * 1000, pIntent);
    }

    private void cancelCallLogAlarmManager(){
        AlarmManager callLogAlarmManger= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i=new Intent(this, CallLogCheckSerivce.class);
        PendingIntent pIntent=PendingIntent.getBroadcast(this,0,i,0);
        callLogAlarmManger.cancel(pIntent);
    }

    public void setListView(boolean isFirstRun, Date _nextQueryTime, Date lastQueryTime){
        try{
            Date nextQueryTime = null;
            if(isFirstRun){
                nextQueryTime = _nextQueryTime;
            }else{
                nextQueryTime = DateUtil.addMinutes(lastQueryTime, SHARED_PREFERENCES_UTIL.RUN_DURATION);
            }
            TextView tvQueryTime = (TextView) MainActivity.getInstance().findViewById(R.id.tv_query);
            tvQueryTime.setText(isFirstRun ? "—— ——" : DateUtil.format(lastQueryTime, DateUtil.DEFAULT_DATETIME_FORMAT));

            TextView tvNextQueryTime = (TextView) MainActivity.getInstance().findViewById(R.id.tv_next_query);
            tvNextQueryTime.setText(DateUtil.format(nextQueryTime,
                    DateUtil.DEFAULT_DATETIME_FORMAT));

            TextView tvInfo = (TextView) findViewById(R.id.tv_info);
            if(!isFirstRun){
                List<Map<String, String>> callLogList = CallLogUtil.getSpecifiedCallLogList(MainActivity.this,
                        REQUIRED_NUMBER_GROUP,
                        SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.CHECK_PERIOD));
                if(callLogList.size() == 0 || !hasRequiredNumberGroup(callLogList)){
                    tvInfo.setText(R.string.tv_main_info_abnormal);
                    CallLogUtil.callPhone(MainActivity.this,
                            SHARED_PREFERENCES_UTIL.getString(SHARED_PREFERENCES_UTIL.CALL_NUMBER),
                            SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.DEFAULT_SIM));
                }else{
                    tvInfo.setText(R.string.tv_main_info_normal);
                    SimpleAdapter adapter = new SimpleAdapter(this
                            , callLogList
                            , R.layout.item_call_log
                            , CallLogUtil.COLUMNS
                            , new int[] { R.id.tv_name, R.id.tv_number, R.id.tv_date});
                    mLVShow.setAdapter(adapter);
                }
            }else{
                tvInfo.setText(R.string.tv_main_info_start);
            }
        }catch (Exception e){
            Log.e(TAG, "setListView: " + e.getMessage() );
        }
    }

    /**
     * Check the required permissions
     */
    public void checkPermission() {
        unPermissionList.clear();
        for (int i = 0; i < permissionList.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissionList[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                unPermissionList.add(permissionList[i]);
            }
        }

        if (unPermissionList.size() > 0) {
            ActivityCompat.requestPermissions( this,permissionList, 100);
            Log.i(TAG, "check 有权限未通过");
        } else {
            Log.i(TAG, "check 权限都已经申请通过");
        }
    }

    /**
     * 5.请求权限后回调的方法
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限
     *                     名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG,"申请结果反馈");
        boolean hasPermissionDismiss = false;
        if (100 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true; //有权限没有通过
                    Log.i(TAG,"有权限没有被通过");
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {//如果有没有被允许的权限
            showPermissionDialog();
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.i(TAG, "onRequestPermissionsResult 权限都已经申请通过");
        }
    }

    /**
     * 不再提示权限时的展示对话框
     */
    private void showPermissionDialog() {
        Log.i(TAG,"mPackName: " + mPackName);
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + mPackName);     //去设置里面设置
                            Intent intent = new Intent(Settings.
                                    ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
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
                List<Date> missCount = numberMissCountMap.get(requiredNumber);
                missCount.add(new Date());
                numberMissCountMap.put(requiredNumber, missCount);
            }
        }
        // To count the numbers whose missing times are equal to or larger than the threshold.
        int numberMissCount2 = 0;
        for(Map.Entry<String, List<Date>> entry : numberMissCountMap.entrySet()){
            String number = entry.getKey();
            List<Date> missCount = entry.getValue();
            if(missCount.size() >= Integer.valueOf(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.NUMBER_MISS_THRESHOLD))){
                numberMissCountMap.put(number, new ArrayList<>());
                if(DateUtil.diffTime(missCount.get(1),missCount.get(0),DateUtil.CONSTANT_MINUTE) < 90)
                    numberMissCount2 ++;
            }
        }
        if(numberMissCount2 > 0)
            return false;
        return  true;
    }

    public void getRequiredNumberGroup(){
        REQUIRED_NUMBER_GROUP = new ArrayList<>();
        String numberGroupStr = SHARED_PREFERENCES_UTIL.getString(SHARED_PREFERENCES_UTIL.REQUIRED_NUMBER_GROUP);
        String[] split = numberGroupStr.split(",");
        for(String str : split)
            REQUIRED_NUMBER_GROUP.add(str);
    }

    private void clearNumberMissCountMap(){
        for(Map.Entry<String, List<Date>> entry : numberMissCountMap.entrySet()){
            String number = entry.getKey();
            numberMissCountMap.put(number, new ArrayList<>());
        }
    }

}

