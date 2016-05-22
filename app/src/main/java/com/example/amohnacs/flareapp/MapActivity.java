package com.example.amohnacs.flareapp;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.Random;

public class MapActivity extends AppCompatActivity {

    public MapView mMapView;
    public LocationDisplayManager ldm;
    public Location mLocation;
    public ImageButton mImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.map);
        // Create GraphicsLayer
        final GraphicsLayer graphicsLayer = new GraphicsLayer();
        // Add empty GraphicsLayer
        mMapView.addLayer(graphicsLayer);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if ((status == STATUS.INITIALIZED) && (o instanceof MapView )) {
                    Log.d("SUNNY","Map initialization succeeded");
                    ldm = mMapView.getLocationDisplayManager();
                    ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.OFF);
                    ldm.setShowLocation(true);
                    ldm.setLocationListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            mLocation = new Location("Actual Location");
                            Log.d("SUNNY", "lat: " + location.getLatitude() + " long: " + location.getLongitude());

                            mLocation.setLongitude(location.getLongitude());
                            mLocation.setLatitude(location.getLatitude());


                            if(location != null){
                                Log.d("SUNNY", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                                ldm.setShowLocation(true);
                                ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                                Log.d("SUNNY", "Location found....");
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("SUNNY", "onStatusChanged");
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d("SUNNY", "onProviderEnabled");

                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d("SUNNY", "LOCATION PROVIDER DISABLED");
                            ldm.setShowLocation(false);
                        }
                    });

                    ldm.start();

                }
            }
        });

        mImageButton = (ImageButton) findViewById(R.id.flareButton);
        mImageButton.setOnTouchListener(new OnSwipeTouchListener(MapActivity.this) {

            public void onSwipeBottom() {
                Toast.makeText(MapActivity.this, "Down", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Toast.makeText(MapActivity.this, "Left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeTop() {
                Toast.makeText(MapActivity.this, "Up", Toast.LENGTH_SHORT).show();


            }

            public void onSwipeRight() {
                Toast.makeText(MapActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static Location getNearbyRandomLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        // Set the adjusted location
        Location newLocation = new Location("Loc in radius");
        newLocation.setLongitude(foundLongitude);
        newLocation.setLatitude(foundLatitude);

        return newLocation;
    }
}
