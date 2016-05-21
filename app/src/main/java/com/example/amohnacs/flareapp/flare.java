package com.example.amohnacs.flareapp;

import android.location.Location;

/**
 * Created by sunny on 5/21/16.
 */

public class flare {
    private String mUsername;
    private String mCategory;
    private Location mLocation;

    public flare(String username, String category, Location location){

        mUsername = username;
        mCategory = category;
        mLocation = location;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }
}
