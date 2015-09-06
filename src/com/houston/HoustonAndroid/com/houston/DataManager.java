package com.houston.HoustonAndroid.com.houston;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.houston.HoustonAndroid.HoustonActivity;
import com.houston.HoustonAndroid.com.houston.helpers.GeoHelper;
import com.houston.HoustonAndroid.com.houston.helpers.YandexHelper;
import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;
import com.houston.HoustonAndroid.com.houston.model.Item;
import org.json.JSONObject;


public class DataManager implements LocationListener {
    private static DataManager instance;

    private LocationManager locationManager;

    public volatile Item<GeoCoordinate> gps;
    public volatile Item<Float> bearingItem;
    public volatile Item<GeoCoordinate> destination;
    public volatile boolean calculateRouteFlag = false;

    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private DataManager() {
        // todo remove
//        gps = new Item(new GeoCoordinate(50.40196, 30.508));

        locationManager = (LocationManager) AlarmService.instance().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 5, this);
    }

    private synchronized void processRoute() {
        if (gps != null && destination != null && calculateRouteFlag) {
            YandexHelper.INSTANCE.calculateRoute(AlarmService.instance(), gps.value, destination.value);
            calculateRouteFlag = false;
        }
    }

    public void doLogic() {
        if (gps != null) {
            ServerFacade.INSTANCE.setKey("gps", gps.value.getLat() + ", " + gps.value.getLng());
            if (bearingItem != null)
                ServerFacade.INSTANCE.setKey("bearing", bearingItem.value.toString());
        }

        JSONObject response = ServerFacade.INSTANCE.getKey("destination", destination != null ? destination.time : 0);
        if (response != null) {
            synchronized (DataManager.class) {
                destination = GeoHelper.INSTANCE.parseCoordinate(response);
                calculateRouteFlag = true;
            }
        }

        processRoute();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gps == null)
            gps = new Item(new GeoCoordinate());
        gps.value.setLat(location.getLatitude());
        gps.value.setLng(location.getLongitude());

        bearingItem = new Item(new Float(location.getBearing()));

        processRoute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }
}
