package com.houston.HoustonAndroid.com.houston.helpers;

import com.houston.HoustonAndroid.com.houston.model.GeoCoordinate;
import com.houston.HoustonAndroid.com.houston.model.Item;
import org.json.JSONException;
import org.json.JSONObject;

public enum GeoHelper {
    INSTANCE;

    public Item parseCoordinate(JSONObject jsonObject) {
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
