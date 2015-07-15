package com.houston.HoustonAndroid.com.houston.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;

import java.util.List;

public enum YandexHelper {
    INSTANCE;

    public void calculateRoute(Context context, GeoCoordinate from, GeoCoordinate to) {
        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP")
            .setPackage("ru.yandex.yandexnavi");

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

        if (infos != null && infos.size() > 0) {
            intent.putExtra("lat_from", 50.50165);
            intent.putExtra("lon_from", 30.50225);
            intent.putExtra("lat_to", 50.44656);
            intent.putExtra("lon_to", 30.42504);
        }

        context.startActivity(intent);
    }
}
