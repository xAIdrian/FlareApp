package com.example.amohnacs.flareapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements FeatureCallback {

    GraphicsLayer graphicsLayer;
    private ArcGISFeatureLayer mFeatureLayer;
    private FeatureLayerClass mFeature;

    public MapView mMapView;
    public LocationDisplayManager ldm;
    public Location mLocation;
    public ImageButton mImageButton;

    ActionBarDrawerToggle mActionToggle;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigation;

    PictureMarkerSymbol mPictureMarkerSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);


        mPictureMarkerSymbol = new PictureMarkerSymbol(getResources().getDrawable(R.drawable.pin));
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
        graphicsLayer = new GraphicsLayer();
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
                            Log.d("SUNNY-ACTUAL", "lat: " + location.getLatitude() + " long: " + location.getLongitude());

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
            public void onSwipeTop() {
                Point tempPoint = new Point(mLocation.getLongitude(), mLocation.getLatitude());
                Point pointGeometry = (Point) GeometryEngine.project(tempPoint, SpatialReference.create(4326), SpatialReference.create(3857));
                // create a graphic with the geometry and marker symbol
                Graphic pointGraphic = new Graphic(pointGeometry, mPictureMarkerSymbol);
                // add the graphic to the graphics layer
                graphicsLayer.addGraphic(pointGraphic);
            }
        });

        //initialization of our FeatureLayer and its associated class
        mFeatureLayer = new ArcGISFeatureLayer(Constants.mFeatureServiceURL, ArcGISFeatureLayer.MODE.ONDEMAND);
        mFeature = new FeatureLayerClass(mMapView);

        mFeature.getExistingPoints(this);
    }

    private void fireFlares() {
        Location location = getNearbyRandomLocation(
                mLocation.getLongitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Tyler Durdern", new LatLng(location.getLongitude(), location.getLatitude()), "Social"));

        Location location1 = getNearbyRandomLocation(
                mLocation.getLongitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Wade Winston Wilson", new LatLng(location1.getLongitude(), location1.getLatitude()), "Service"));

        Location location2 = getNearbyRandomLocation(
                mLocation.getLongitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("Dick Grayson", new LatLng(location2.getLongitude(), location2.getLatitude()), "Emergency"));

        Location location3 = getNearbyRandomLocation(
                mLocation.getLongitude(), mLocation.getLatitude(), 25);
        mFeature.submitFlare(
                new Flare("James Howlett", new LatLng(location3.getLongitude(), location3.getLatitude()), "Service"));

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
            Log.d("SUNNY-Top", "long: " + flare.getLocation().longitude + " lat: " + flare.getLocation().latitude);
            Point tempPoint = new Point(flare.getLocation().longitude, flare.getLocation().latitude);
            Point pointGeometry = (Point) GeometryEngine.project(tempPoint, SpatialReference.create(4326), SpatialReference.create(3857));
            Graphic pointGraphic = new Graphic(pointGeometry, mPictureMarkerSymbol);
            graphicsLayer.addGraphic(pointGraphic);
        }
    }

}
