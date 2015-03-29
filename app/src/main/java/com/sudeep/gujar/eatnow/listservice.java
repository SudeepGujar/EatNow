package com.sudeep.gujar.eatnow;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUJAR on 3/24/2015.
 */
public class listservice extends IntentService {
   // String[] params;
    String Uri = MainActivity.uri;
    boolean searching = MainActivity.searching;
    static List<FeedItem> feedItemList = MainActivity.feedItemList;
    ArrayList<String> rawdata = MainActivity.rawdata;
    RecyclerView mRecyclerView = MainActivity.mRecyclerView;
    RecyclerViewAdapter adapter = MainActivity.adapter;
    Handler mhandler;
     RelativeLayout load = MainActivity.load;
     ImageView loading = MainActivity.loading;
     FrameLayout loc_title_frame = MainActivity.loc_title_frame;

    public listservice() {
        super("listservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();
         mhandler = new Handler() ;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        Log.e("RecyclViewIntentService",""+Uri);
        try {
            {
                    /* forming th java.net.URL object */


                URL url = new URL(Uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                JSONObject venuesJson = executeHttpGet(Uri);

                    /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = Integer.parseInt(venuesJson.getJSONObject("meta")
                        .getString("code"));

                    /* 200 represents HTTP OK */
                if (statusCode ==  200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    if(searching){
                        feedItemList = new ArrayList<FeedItem>();
                        rawdata = new ArrayList<String>();
                        parseResultSearch(response.toString());
                    }else{
                        parseResult(response.toString());
                    }

                    result = 1; // Successful
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new RecyclerViewAdapter(getApplicationContext(), feedItemList);
                            mRecyclerView.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();
                            loading.clearAnimation();
                            load.setVisibility(View.INVISIBLE);
                            loc_title_frame.setVisibility(View.VISIBLE);

                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                   // adapter = new RecyclerViewAdapter(getApplicationContext(), feedItemList);
                   // mRecyclerView.setAdapter(adapter);
                }else{
                    result = 0; //"Failed to fetch data!";
                }}


            Log.d("RecyclViewIntentService","in try ");
        } catch (Exception e) {
            Log.d("RecyclViewIntentService","in catch ");
        }
        return ; //"Failed to fetch data!";
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            // JSONArray posts = response.getJSONObject("response")
            //       .getJSONArray("venues");
            JSONArray posts = response.getJSONObject("response")
                    .getJSONArray("groups");

            /*Initialize array if null*/
            if (null == feedItemList) {
                feedItemList = new ArrayList<FeedItem>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                JSONArray items = post.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject venue = items.optJSONObject(j);

                    FeedItem item = new FeedItem();
                    item.setVenueID(venue.getJSONObject("venue").optString("id"));
                    String title = venue.getJSONObject("venue").optString("name");
                    item.setTitle(title);
                    item.setDist(venue.getJSONObject("venue").getJSONObject("location").optInt("distance"));
                    item.setAddress(venue.getJSONObject("venue").getJSONObject("location").optString("address"));

                    String longi= venue.getJSONObject("venue").getJSONObject("location").optString("lng");
                    String lat=venue.getJSONObject("venue").getJSONObject("location").optString("lat");

                    rawdata.add(lat);
                    rawdata.add(longi);
                    rawdata.add(title);


                    item.setRank(venue.getJSONObject("venue").optString("rating"));


                /*item.setTitle(post.optString("name"));
                item.setDist(post.getJSONObject("location").optInt("distance"));*/
                    // item.setThumbnail(post.optString("thumbnail"));
                    feedItemList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResultSearch(String result) {
        try {
            JSONObject response = new JSONObject(result);
            // JSONArray posts = response.getJSONObject("response")
            //       .getJSONArray("venues");
            JSONArray posts = response.getJSONObject("response")
                    .getJSONArray("venues");

            /*Initialize array if null*/
            if (null == feedItemList) {
                feedItemList = new ArrayList<FeedItem>();
            }

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);

                FeedItem item = new FeedItem();
                item.setVenueID(post.optString("id"));
                String title = post.optString("name");
                item.setTitle(title);
                item.setAddress(post.getJSONObject("location").optString("distance"));
                String longi= post.getJSONObject("location").optString("lng");
                String lat=post.getJSONObject("location").optString("lat");

                rawdata.add(lat);
                rawdata.add(longi);
                rawdata.add(title);
                //item.setAddress(post.getJSONObject("venues").getJSONObject("location").optString("address"));
                //item.setRank(post.getJSONObject("venues").optString("rating"));

                /*item.setTitle(post.optString("name"));
                item.setDist(post.getJSONObject("location").optInt("distance"));*/
                // item.setThumbnail(post.optString("thumbnail"));
                feedItemList.add(item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject executeHttpGet(String uri) throws Exception {
        HttpGet req = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse resLogin = client.execute(req);
        BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = r.readLine()) != null) {
            sb.append(s);
        }

        return new JSONObject(sb.toString());
    }
}
