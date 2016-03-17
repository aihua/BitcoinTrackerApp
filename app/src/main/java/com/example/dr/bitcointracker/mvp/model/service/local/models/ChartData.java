package com.example.dr.bitcointracker.mvp.model.service.local.models;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

// Realm ChartData table
public class ChartData extends RealmObject {
    @PrimaryKey
    private long timestamp;

    private float value;

    public ChartData() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
