package com.example.listendog;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    private EditText etCallNumber;
    private EditText etRequiredNumberGroup;
    private EditText etDefaultSim;
    private EditText etCheckPeriod;
    private EditText etNumberMissThreshold;
    private EditText etRunDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        initSettings();
        Button btn = (Button)findViewById(R.id.btn_settings_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> strMap = new HashMap<>();
                strMap.put("call_number", etCallNumber.getText().toString().trim());
                strMap.put("required_number_group", etRequiredNumberGroup.getText().toString().trim());
                Map<String, Integer> intMap = new HashMap<>();
                intMap.put("default_sim", Integer.valueOf(etDefaultSim.getText().toString().trim()));
                intMap.put("check_period", Integer.valueOf(etCheckPeriod.getText().toString().trim()));
                intMap.put("number_miss_threshold", Integer.valueOf(etNumberMissThreshold.getText().toString().trim()));
                //intMap.put("run_duration", Integer.valueOf(etRunDuration.getText().toString().trim()));
                SHARED_PREFERENCES_UTIL.putStringMap(strMap);
                SHARED_PREFERENCES_UTIL.putIntMap(intMap);
                Toast.makeText(SettingsActivity.this, R.string.toast_submit, Toast.LENGTH_SHORT ).show();
            }
        });
    }

    private void initSettings(){
        etCallNumber = (EditText)findViewById(R.id.et_call_number);
        etRequiredNumberGroup = (EditText)findViewById(R.id.et_required_number_group);
        etDefaultSim = (EditText)findViewById(R.id.et_default_sim);
        etCheckPeriod = (EditText)findViewById(R.id.et_check_period);
        etNumberMissThreshold = (EditText)findViewById(R.id.et_number_miss_threshold);
        //etRunDuration = (EditText)findViewById(R.id.et_run_duration);

        etCallNumber.setText(SHARED_PREFERENCES_UTIL.getString(SHARED_PREFERENCES_UTIL.CALL_NUMBER));
        etRequiredNumberGroup.setText(SHARED_PREFERENCES_UTIL.getString(SHARED_PREFERENCES_UTIL.REQUIRED_NUMBER_GROUP));
        etDefaultSim.setText(String.valueOf(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.DEFAULT_SIM)));
        etCheckPeriod.setText(String.valueOf(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.CHECK_PERIOD)));
        etNumberMissThreshold.setText(String.valueOf(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.NUMBER_MISS_THRESHOLD)));
        //etRunDuration.setText(String.valueOf(SHARED_PREFERENCES_UTIL.getInt(SHARED_PREFERENCES_UTIL.RUN_DURATION)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}