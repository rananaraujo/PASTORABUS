package com.example.pastorabus.model;

import com.example.pastorabus.model.Stop;
import com.example.pastorabus.model.User;

public class Evento {
    public long evento_id;
    public Stop stop;
    public User usr;
    public String evento_type;

    public Evento(long evento_id, Stop stop, User usr, String evento_type) {
        this.evento_id = evento_id;
        this.stop = stop;
        this.usr = usr;
        this.evento_type = evento_type;
    }

    public long getEvento_id() {
        return evento_id;
    }

    public void setEvento_id(long evento_id) {
        this.evento_id = evento_id;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public User getUsr() {
        return usr;
    }

    public void setUsr(User usr) {
        this.usr = usr;
    }

    public String getEvento_type() {
        return evento_type;
    }

    public void setEvento_type(String evento_type) {
        this.evento_type = evento_type;
    }
}
