package com.example.pastorabus.model;

import android.location.Location;

public class Stop {
    public long stop_id;
    public double latitude;
    public double longitude;

    public Stop(long stop_id, double latitude, double longitude) {
        this.stop_id = stop_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getStop_id() {
        return stop_id;
    }

    public void setStop_id(long stop_id) {
        this.stop_id = stop_id;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
