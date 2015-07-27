package com.houston.HoustonAndroid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.houston.HoustonAndroid.com.houston.ServerFacade;
import com.houston.HoustonAndroid.com.houston.helpers.YandexHelper;
import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;
import com.houston.HoustonAndroid.com.houston.model.Item;
import org.json.JSONException;
import org.json.JSONObject;


public class HoustonActivity extends Activity {
    private LocationManager locationManager;

    private volatile Item<GeoCoordinate> gps;
    private volatile Item<Float> bearingItem;
    private volatile Item<GeoCoordinate> destination;
    private volatile boolean calculateRouteFlag = false;

    private synchronized void processRoute() {
        if (gps != null && destination != null && calculateRouteFlag) {
            YandexHelper.INSTANCE.calculateRoute(this, gps.value, destination.value);
            calculateRouteFlag = false;
        }
    }

    public synchronized void setCurrentLocation(double gpsLat, double gpsLng, float bearing) {
        if (gps == null)
            gps = new Item(new GeoCoordinate());
        gps.value.setLat(gpsLat);
        gps.value.setLng(gpsLng);

        bearingItem = new Item(new Float(bearing));

        processRoute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setCurrentLocation(location.getLatitude(), location.getLongitude(), location.getBearing());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override
            public void onProviderEnabled(String provider) { }
            @Override
            public void onProviderDisabled(String provider) { }
        });

        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                if (gps != null) {
                    ServerFacade.INSTANCE.setKey("gps", gps.value.getLat() + ", " + gps.value.getLng());
                    if (bearingItem != null)
                        ServerFacade.INSTANCE.setKey("bearing", bearingItem.value.toString());
                }

                JSONObject response = ServerFacade.INSTANCE.getKey("destination", destination != null ? destination.time : 0);
                if (response != null) {
                    synchronized (HoustonActivity.class) {
                        destination = parseCoordinate(response);
                        calculateRouteFlag = true;
                        processRoute();
                    }
                }
            }
        };
        registerReceiver(br, new IntentFilter("com.houston.HoustonAndroid"));

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent("com.houston.HoustonAndroid"), 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000, pi);
    }

    private Item parseCoordinate(JSONObject jsonObject) {
        try {
            String value = jsonObject.getString("value");
            String[] latLng = value.split(",");
            GeoCoordinate coordinate = new GeoCoordinate(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
            return new Item<GeoCoordinate>(coordinate, jsonObject.getLong("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
