package com.example.amohnacs.flareapp;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements FeatureCallback {

    private ArcGISFeatureLayer mFeatureLayer;
    private FeatureLayerClass mFeature;

    public MapView mMapView;
    public LocationDisplayManager ldm;
    public Location mLocation;
    public ImageButton mImageButton;

    ActionBarDrawerToggle mActionToggle;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        mActionToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.activity_drawer_navigation_view);
        mActionToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerListener(mActionToggle);


        mActionToggle.setToolbarNavigationClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

            }
        });

        mNavigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        //Handle switching Fragments here
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        mMapView = (MapView) findViewById(R.id.map);
        // Create GraphicsLayer
        final GraphicsLayer graphicsLayer = new GraphicsLayer();
        // Add empty GraphicsLayer
        mMapView.addLayer(graphicsLayer);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if ((status == STATUS.INITIALIZED) && (o instanceof MapView)) {
                    Log.d("SUNNY", "Map initialization succeeded");
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


                            if (location != null) {
                                Log.d("SUNNY", "lat: " + location.getLatitude() + " long: " + location.getLongitude());
                                ldm.setShowLocation(true);
                                ldm.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                                Log.d("SUNNY", "Location found....");
                                //fireFlares();
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

                // create a point marker symbol (red, size 10, of type circle)
                SimpleMarkerSymbol simpleMarker = new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE);

                // create a point at x=-302557, y=7570663 (for a map using meters as units; this depends
                // on the spatial reference)
                //Point pointGeometry = new Point(mLocation.getLatitude(), mLocation.getLongitude());
                Point pointGeometry = new Point(-302557, 7570663);

                // create a graphic with the geometry and marker symbol
                Graphic pointGraphic = new Graphic(pointGeometry, simpleMarker);

                // add the graphic to the graphics layer
                graphicsLayer.addGraphic(pointGraphic);
            }

            public void onSwipeRight() {
                Toast.makeText(MapActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });

        //initialization of our FeatureLayer and its associated class
        mFeatureLayer = new ArcGISFeatureLayer(Constants.mFeatureServiceURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mFeature = new FeatureLayerClass(mMapView);





        mFeature.getExistingPoints(this);

    }

    private void fireFlares() {
        Location location = getNearbyRandomLocation(
                mLocation.getLatitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Tyler Durdern", new LatLng(location.getLatitude(), location.getLongitude()), "Social"));

        Location location1 = getNearbyRandomLocation(
                mLocation.getLatitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Wade Winston Wilson", new LatLng(location1.getLatitude(), location1.getLongitude()), "Service"));

        Location location2 = getNearbyRandomLocation(
                mLocation.getLatitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Dick Grayson", new LatLng(location2.getLatitude(), location2.getLongitude()), "Emergency"));

        Location location3 = getNearbyRandomLocation(
                mLocation.getLatitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("James Howlett", new LatLng(location3.getLatitude(), location3.getLongitude()), "Service"));

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

    @Override
    public void setExistingFlares(List<Flare> flares) {

        for (Flare flare : flares) {
            //todo: set Marker on map using flare.getLocation()
        }
    }

}
