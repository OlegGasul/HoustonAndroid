package com.houston.HoustonAndroid.com.houston.model;

public class GeoCoordinate {
    private double lat;
    private double lng;

    public GeoCoordinate() {
    }

    public GeoCoordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
