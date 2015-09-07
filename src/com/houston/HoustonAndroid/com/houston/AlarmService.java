package com.houston.HoustonAndroid.com.houston;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmService extends IntentService {
    private PendingIntent pendingIntent;
    private static AlarmService instance;
    public static AlarmService instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmService.this, 0, alarmIntent, 0);
    }

    public AlarmService() {
        super("");
        instance = this;
    }

    public AlarmService(String name) {
        super(name);
        instance = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        resetAlarm();
    }

    public synchronized void resetAlarm() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null)
            manager.cancel(pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 1500, pendingIntent);
    }
}
