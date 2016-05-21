package com.example.amohnacs.flareapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;

public class MapActivity extends AppCompatActivity {

    public MapView mMapView;
    public LocationDisplayManager ldm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.map);

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
                            Log.d("SUNNY", "lat: " + location.getLatitude() + " long: " + location.getLongitude());

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
    }
}
