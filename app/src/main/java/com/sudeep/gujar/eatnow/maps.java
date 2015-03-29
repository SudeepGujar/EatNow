package com.sudeep.gujar.eatnow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import sudeep.gujar.eatnow.R;

/**
 * Created by GUJAR on 2/12/2015.
 */
public class maps extends ActionBarActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    GoogleMap Gmap;

   // private final LatLng LOCATION_SHAHU = new LatLng(18.531329,73.846421);
    public int indicator = 0;
    double latitude1,longitude1;



    ArrayList<LatLng> markerPoints;
    ArrayList<String> rawdata;


    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        markerPoints = new ArrayList<LatLng>();
        rawdata = new ArrayList<String>();
        FloatingActionButton fabview = (FloatingActionButton) findViewById(R.id.mapfab);
        fabview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator++;

                if(indicator == 0)
                    Gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                else if(indicator == 1)
                    Gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                else if(indicator == 2)
                    Gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                else {
                    Gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    indicator = -1;
                }

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rawdata = extras.getStringArrayList("section");


        }else{
            //rawdata is empty
        }





              gps = new GPSTracker(maps.this);

                if(gps.canGetLocation())
                {
                     latitude1 = gps.getLatitude();
                     longitude1 = gps.getLongitude();


                }
                else
                {
                    gps.showSettingsAlert();
                }





        ///markerPoints.add(LOCATION_SHAHU);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
           // map = fm.getMap();


    }


    @Override
    public void onMapReady(GoogleMap map) {
        Gmap = map;

        gps = new GPSTracker(maps.this);

        if(gps.canGetLocation())
        {
             //latitude = gps.getLatitude();
             //longitude = gps.getLongitude();
            LatLng  myloc = new LatLng(latitude1, longitude1);
            map.addMarker(new MarkerOptions().position(myloc).title("you are here").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            markerPoints.add(myloc);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myloc, 14);
            map.animateCamera(update);
        }
        else
        {
            gps.showSettingsAlert();
        }

        for(int i=0; i<rawdata.size();i+=3){
            String lat = rawdata.get(i);
            String longi = rawdata.get(i+1);
            String title = rawdata.get(i+2);
            double longitude = Double.valueOf(longi.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
            double lattitude = Double.valueOf(lat.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));

            LatLng Venue_loc = new LatLng(lattitude,longitude);
            map.addMarker(new MarkerOptions().position(Venue_loc).title(title));
        }

    }
}

