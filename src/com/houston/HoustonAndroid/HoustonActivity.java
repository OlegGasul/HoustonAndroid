package com.houston.HoustonAndroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import com.houston.HoustonAndroid.com.houston.AlarmReceiver;

import java.util.Calendar;


public class HoustonActivity extends Activity {
    private PendingIntent pendingIntent;

    private static HoustonActivity instance;
    public static HoustonActivity instance() {
        return instance;
    }

    @Override
    public void onStart() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onStart();
        instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent alarmIntent = new Intent(HoustonActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HoustonActivity.this, 0, alarmIntent, 0);

        resetAlarm();
    }

    public synchronized void resetAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null)
            manager.cancel(pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 1000, pendingIntent);
    }
}
