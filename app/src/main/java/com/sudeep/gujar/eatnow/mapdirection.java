package com.sudeep.gujar.eatnow;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sudeep.gujar.eatnow.R;

/**
 * Created by GUJAR on 2/21/2015.
 */
public class mapdirection extends ActionBarActivity implements OnMapReadyCallback {
   GoogleMap Gmap;

    private final LatLng LOCATION_SHAHU = new LatLng(18.531329,73.846421);
    public int indicator = 0;

    String longi,lat,name;

    ArrayList<LatLng> markerPoints;
    LatLng Venue_loc;

   // Button btnShowLocation;
    GPSTracker gps;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

      //  btnShowLocation = (Button)findViewById(R.id.Show_Location);

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
                    indicator = 0;
                }


            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            longi = extras.getString("longi");
            lat = extras.getString("lat");
            name = extras.getString("name");


        }else{
            //rawdata is empty
        }

        double longitude_bundle = Double.valueOf(longi.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        double lattitude_bundle = Double.valueOf(lat.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));

        Venue_loc = new LatLng(lattitude_bundle,longitude_bundle);
       // map.addMarker(new MarkerOptions().position(Venue_loc).title(title));



        gps = new GPSTracker(mapdirection.this);

                if(gps.canGetLocation())
                {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();


                }
                else
                {
                    gps.showSettingsAlert();
                }




        markerPoints = new ArrayList<LatLng>();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(this);
        //if (fm.getMap() != null);
        // Gmap = fm.getMap();



    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Error downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady( GoogleMap map) {
        Gmap = map;



        gps = new GPSTracker(mapdirection.this);

        if(gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            LatLng  myloc = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(myloc).title("you are here"));
            map.addMarker(new MarkerOptions().position(Venue_loc).title(name));
            markerPoints.add(myloc);
            markerPoints.add(Venue_loc);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myloc, 14);
            map.animateCamera(update);
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
        else
        {
            gps.showSettingsAlert();
        }
        //indicator = indicator + 1;

        map.setMyLocationEnabled(true);


        // Getting URL to the Google Directions API



    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
//*************************************************************************************************************
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }




        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));

                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }


            // Drawing polyline in the Google Map for the i-th route
            Gmap.addPolyline(lineOptions);
           // Toast.makeText(mapdirection.this,"direction",Toast.LENGTH_LONG).show();
        }
    }


}
