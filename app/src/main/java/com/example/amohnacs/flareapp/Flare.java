package com.example.amohnacs.flareapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by amohnacs on 5/21/16.
 */
public class Flare {

    private String userName;
    private LatLng location;
    private String category;

    public Flare() {
    }

    public Flare(String userName, LatLng location, String category) {
        this.userName = userName;
        this.location = location;
        this.category = category;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
