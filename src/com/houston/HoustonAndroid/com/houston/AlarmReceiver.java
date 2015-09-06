package com.houston.HoustonAndroid.com.houston;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.houston.HoustonAndroid.HoustonActivity;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        DataManager dataManager = DataManager.getInstance(context);
        dataManager.doLogic();

        AlarmService.instance().resetAlarm();
    }
}
