package com.example.pastorabus.model;

import java.util.Date;

public class LocationData {
    public double latitude;
    public double longitude;
    public long horario;

    public LocationData(){

    }
    public LocationData(double latitude, double longitude, long horario) {
        this.horario = horario;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getHorario() {
        return horario;
    }

    public void setHorario(long horario) {
        this.horario = horario;
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
