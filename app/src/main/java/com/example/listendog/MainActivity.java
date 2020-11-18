package com.example.listendog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.listendog.service.AlarmService;
import com.example.listendog.util.CallLogUtil;
import com.example.listendog.util.DateUtil;
import com.example.listendog.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
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
    private Map<String, Integer> numberMissCountMap = new HashMap<>();
    private ListView mLVShow;
    private Date lastQueryTime;
    private Properties properties;
    public static final List<String> REQUIRED_NUMBER_GROUP = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        INSTANCE = this;
        // Properties
        properties = PropertiesUtil.getProperties(getApplicationContext());
        getRequiredNumberGroup();

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
        List<Map<String, String>> callLogList = CallLogUtil.getSpecifiedCallLogList(MainActivity.this,
                REQUIRED_NUMBER_GROUP,
                Integer.valueOf(properties.getProperty(PropertiesUtil.CALL_DURATION)));
        if(callLogList.size() == 0 || !hasRequiredNumberGroup(callLogList)){
            TextView tvInfo = (TextView) findViewById(R.id.tv_info);
            tvInfo.setText("电话系统可能已暂停服务，请检查！");
            CallLogUtil.callPhone(MainActivity.this,
                    properties.getProperty(PropertiesUtil.CALL_NUMBER),
                    Integer.valueOf(properties.getProperty(PropertiesUtil.DEFAULT_SIM)));
        }
        SimpleAdapter adapter = new SimpleAdapter(this
                , callLogList
                , R.layout.item_call_log
                , CallLogUtil.COLUMNS
                , new int[] { R.id.tv_name, R.id.tv_number, R.id.tv_date});
        mLVShow.setAdapter(adapter);
        // Set the value of query time.
        TextView tvQueryTime = (TextView) findViewById(R.id.tv_query);
        tvQueryTime.setText(DateUtil.format(lastQueryTime, DateUtil.DEFAULT_DATETIME_FORMAT));
        TextView tvNextQueryTime = (TextView) findViewById(R.id.tv_next_query);
        tvNextQueryTime.setText(DateUtil.format(DateUtil.addMinutes(lastQueryTime,(Integer.valueOf(properties.getProperty(PropertiesUtil.RUN_DURATION))) / (60 * 1000)),
                DateUtil.DEFAULT_DATETIME_FORMAT));
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
                numberMissCountMap.put(requiredNumber, numberMissCountMap.get(requiredNumber) + 1);
            }
        }
        // To count the numbers whose missing times are equal to or larger than the threshold.
        int numberMissCount2 = 0;
        for(Map.Entry<String, Integer> entry : numberMissCountMap.entrySet()){
            String number = entry.getKey();
            Integer numberCount = entry.getValue();
            if(numberCount >= Integer.valueOf(properties.getProperty(PropertiesUtil.NUMBER_MISS_THRESHOLD))){
                numberMissCount2 ++;
                // Reset the missing times.
                numberMissCountMap.put(number, 0);
            }
        }
        if(numberMissCount2 > 0)
            return false;
        return  true;
    }

    public void getRequiredNumberGroup(){
        String numberGroupStr = MainActivity.this.properties.getProperty(PropertiesUtil.REQUIRED_NUMBER_GROUP);
        String[] split = numberGroupStr.split(",");
        for(String str : split)
            REQUIRED_NUMBER_GROUP.add(str);
    }

}

