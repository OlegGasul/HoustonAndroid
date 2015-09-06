package com.houston.HoustonAndroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import com.houston.HoustonAndroid.com.houston.AlarmReceiver;
import com.houston.HoustonAndroid.com.houston.AlarmService;

import java.util.Calendar;


public class HoustonActivity extends Activity {
    @Override
    public void onStart() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);
    }
}
