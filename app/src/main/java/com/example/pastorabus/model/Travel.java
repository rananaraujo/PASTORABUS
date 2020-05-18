package com.example.pastorabus.model;

import android.location.Location;

import com.example.pastorabus.model.Bus;

public class Travel {
    public long id_travel;
    public Bus bus;
    public Location start_location;
    public Location end_location;

    public Travel(long id_travel, Bus bus, Location start_location, Location end_location) {
        this.id_travel = id_travel;
        this.bus = bus;
        this.start_location = start_location;
        this.end_location = end_location;
    }

    public long getId_travel() {
        return id_travel;
    }

    public void setId_travel(long id_travel) {
        this.id_travel = id_travel;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Location getStart_location() {
        return start_location;
    }

    public void setStart_location(Location start_location) {
        this.start_location = start_location;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public void setEnd_location(Location end_location) {
        this.end_location = end_location;
    }
}
