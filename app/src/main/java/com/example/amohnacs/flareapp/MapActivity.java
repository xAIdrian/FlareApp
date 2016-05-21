package com.example.amohnacs.flareapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;

public class MapActivity extends AppCompatActivity {
    MapView mMapView;
    GraphicsLayer mLocationLayer;
    Point mLocationLayerPoint;
    String mLocationLayerPointString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
}
