package com.houston.HoustonAndroid;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import com.houston.HoustonAndroid.com.houston.ServerFacade;
import com.houston.HoustonAndroid.com.houston.helpers.YandexHelper;
import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;
import com.houston.HoustonAndroid.com.houston.model.Item;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class HoustonActivity extends Activity {
    private LocationManager locationManager;

    private volatile Item<GeoCoordinate> gps;
    private volatile Item<GeoCoordinate> destination;
    private volatile boolean calculateRouteFlag = false;

    private synchronized void processRoute() {
        if (gps != null && destination != null && calculateRouteFlag) {
            YandexHelper.INSTANCE.calculateRoute(this, gps.value, destination.value);
            calculateRouteFlag = false;
        }
    }

    public synchronized void setCurrentLocation(double gpsLat, double gpsLng) {
        if (gps == null)
            gps = new Item(new GeoCoordinate());
        gps.value.setLat(gpsLat);
        gps.value.setLng(gpsLng);
        processRoute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setCurrentLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Timer timer = new Timer();
                    TimerTask remoteRequestTask = new RemoteRequestTask();
                    timer.scheduleAtFixedRate(remoteRequestTask, 0, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
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

    class RemoteRequestTask extends TimerTask {
        public void run() {
            if (gps != null) {
                ServerFacade.INSTANCE.setKey("gps", gps.value.getLat() + ", " + gps.value.getLng());
            }
            ServerFacade.INSTANCE.setKey("test", Math.random() + "");

            JSONObject response = ServerFacade.INSTANCE.getKey("destination", destination != null ? destination.time : 0);
            if (response != null) {
                synchronized (HoustonActivity.class) {
                    destination = parseCoordinate(response);
                    calculateRouteFlag = true;
                    processRoute();
                }
            }
        }
    }
}
