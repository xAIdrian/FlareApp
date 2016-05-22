package com.example.amohnacs.flareapp;

import android.util.Log;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.MapView;
import com.esri.core.geodatabase.GeodatabaseEditError;
import com.esri.core.geodatabase.GeodatabaseFeature;
import com.esri.core.geodatabase.GeodatabaseFeatureServiceTable;
import com.esri.core.geometry.Point;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.table.TableException;
import com.esri.core.tasks.query.QueryParameters;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amohnacs on 5/21/16.
 */
public class FeatureLayerClass {
    public final String TAG = getClass().getSimpleName();

    private GeodatabaseFeatureServiceTable mFeatureServiceTable;
    private FeatureLayer featureLayer;

    private MapView mMap;

    public FeatureLayerClass(MapView map) {
        mFeatureServiceTable =
                new GeodatabaseFeatureServiceTable(Constants.mFeatureServiceURL, 0);
        mMap = map;

        mFeatureServiceTable.initialize(new CallbackListener<GeodatabaseFeatureServiceTable.Status>() {
            @Override
            public void onCallback(GeodatabaseFeatureServiceTable.Status status) {

                if (status == GeodatabaseFeatureServiceTable.Status.INITIALIZED) {
                    featureLayer = new FeatureLayer(mFeatureServiceTable);
                    mMap.addLayer(featureLayer);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void submitFlare(Flare flare) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("UserName", flare.getUserName());
        attributes.put("Description", null);
        attributes.put("Category", flare.getCategory());

        try {
            //Create a feature with these attributes at the given point
            GeodatabaseFeature gdbFeature = new GeodatabaseFeature(attributes,
                    new Point(flare.getLocation().longitude, flare.getLocation().latitude), mFeatureServiceTable);
            //new Point(latLng.longitude, latLng.latitude)
            //Add the feature into the geodatabase table returning the new feature ID
            long fid = mFeatureServiceTable.addFeature(gdbFeature);
            mFeatureServiceTable.applyEdits(new CallbackListener<List<GeodatabaseEditError>>() {
                @Override
                public void onCallback(List<GeodatabaseEditError> geodatabaseEditErrors) {
                    for (GeodatabaseEditError editError : geodatabaseEditErrors) {
                        Log.e("ERROR", editError.toString());
                    }
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });

            String geomStr = mFeatureServiceTable.getFeature(fid).getGeometry().toString();
            Log.d(TAG, "added fid = " + fid + " " + geomStr);
        } catch (TableException e) {
            // report errors, e.g. to console
            Log.e(TAG, "", e);
        }
    }

    public void getExistingPoints(final FeatureCallback callback) {
        Log.e(TAG, "TAG");

        final ArrayList<Flare> mFlares = new ArrayList<Flare>();

        mFeatureServiceTable =
                new GeodatabaseFeatureServiceTable(Constants.mFeatureServiceURL, 0);
        mFeatureServiceTable.setFeatureRequestMode(GeodatabaseFeatureServiceTable.FeatureRequestMode.ON_INTERACTION_CACHE);
        mFeatureServiceTable.initialize(new CallbackListener<GeodatabaseFeatureServiceTable.Status>() {
            @Override
            public void onCallback(GeodatabaseFeatureServiceTable.Status status) {
                if (status == GeodatabaseFeatureServiceTable.Status.INITIALIZED) {
                    try {
                        QueryParameters qp = new QueryParameters();
                        qp.setWhere("OBJECTID > 0");
                        qp.setReturnGeometry(true);

                        mFeatureServiceTable.queryFeatures(qp, new CallbackListener<FeatureResult>() {
                            @Override
                            public void onCallback(FeatureResult objects) {

                                if(objects.featureCount() > 1) {
                                    for(Object obj : objects) {

                                        Flare tempFlare = new Flare();
                                        Feature f = (Feature) obj;

                                        tempFlare.setUserName(f.getAttributeValue("UserName").toString());
                                        tempFlare.setCategory(f.getAttributeValue("Category").toString());
                                        Point p = (Point) f.getGeometry();
                                        tempFlare.setLocation(new LatLng(p.getX(), p.getY()));

                                        mFlares.add(tempFlare);

                                    }

                                    callback.setExistingFlares(mFlares);

                                } else {
                                    Log.e(TAG, objects.toString());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "GeoFeature was not initialized");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

        });
    }

    public boolean clearData() {
        mFeatureServiceTable =
                new GeodatabaseFeatureServiceTable(Constants.mFeatureServiceURL, 0);
        try {
            mFeatureServiceTable.deleteFeatures(featureLayer.getSelectionIDs());
            return true;
        } catch (TableException e) {
            e.printStackTrace();
        }
        return false;
    }
}
