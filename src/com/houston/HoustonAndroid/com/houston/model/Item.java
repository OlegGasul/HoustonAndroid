package com.houston.HoustonAndroid.com.houston.model;


public class Item<K> {
    public K value;
    public long time;

    public Item(K value) {
        this.value = value;
    }

    public Item(K value, long time) {
        this.value = value;
        this.time = time;
    }
}
