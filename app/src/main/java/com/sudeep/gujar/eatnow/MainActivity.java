package com.sudeep.gujar.eatnow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import sudeep.gujar.eatnow.R;


public class MainActivity extends ActionBarActivity {
    static RecyclerView mRecyclerView;
    static RecyclerViewAdapter adapter;
    private static final String TAG = "RecyclerViewExample";
    static List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    static String uri;
    String section;
    static RelativeLayout load;
    RelativeLayout search;
    static FrameLayout loc_title_frame;
    Button navifood,navicoffee,navidrinks;
    Animation animRotate;
    static ImageView loading;
    EditText searchtext;
    Button searchbutton;
    static Boolean searching = false;
    GPSTracker gps;
    double latitude1,longitude1;



    static ArrayList<String> rawdata;



    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AIzaSyDtxpUim66OVg5xlhwtqDhS0uUGw4MvMm8
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putInt("search", 0);
        editor.commit();
       // toolbar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
        navi drawerFragment = (navi)getSupportFragmentManager().findFragmentById(R.id.fragmet_navi);
        drawerFragment.setup((DrawerLayout)findViewById(R.id.drawerlayout),toolbar);
        load = (RelativeLayout)findViewById(R.id.initial_layout);
        animRotate  = AnimationUtils.loadAnimation(this, R.anim.rotate);
        loading = (ImageView)findViewById(R.id.loadimage);
        search = (RelativeLayout)findViewById(R.id.searchbox);
        searchtext = (EditText)findViewById(R.id.searchtext);
        searchbutton = (Button)findViewById(R.id.searchbutton);
        loc_title_frame = (FrameLayout)findViewById(R.id.loc_title_frame);
        rawdata = new ArrayList<String>();

        gps = new GPSTracker(MainActivity.this);

        if(gps.canGetLocation())
        {
            latitude1 = gps.getLatitude();
            longitude1 = gps.getLongitude();


        }
        else
        {
            gps.showSettingsAlert();
        }
        //searchtext.startAnimation(animdown);

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchtext.getText().toString();
                if(query == null || query.equals("")){
                    query="food";
                }

                uri=  "https://api.foursquare.com/v2/venues/search"
                        + "?v=20130815"
                        + "&ll=" + latitude1 + "," + longitude1
                        + "&query="+query;
                uri = uri + "&client_id=1RXIWWMGEKK5MQUBU41REIYE4ZY3FGOAG4AOMFX20NBGSPWA" + "&client_secret=K4KP34R2NIJZXB1OSEUTGI02N5WU4MIHGXWUVOWU3KGORE5B" ;
                searching = true;



                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putInt("search", 1);
                editor.commit();
                search.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                loading.startAnimation(animRotate);
                load.setVisibility(View.VISIBLE);
                loc_title_frame.setVisibility(View.VISIBLE);

                Intent service = new Intent(MainActivity.this,listservice.class);
                MainActivity.this.startService(service);

            }
        });

        loading.startAnimation(animRotate);

        navifood = (Button)drawerFragment.getView().findViewById(R.id.navifood);
        navifood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                i.putExtra("section","food");
                finish();
                startActivity(i);
            }
        });

        navicoffee = (Button)drawerFragment.getView().findViewById(R.id.navicoffe);
        navicoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                i.putExtra("section","coffee");
                finish();
                startActivity(i);
            }
        });

        navidrinks = (Button)drawerFragment.getView().findViewById(R.id.navidrink);
        navidrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                i.putExtra("section","drinks");
                finish();
                startActivity(i);
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= "pressed";

                Intent intent = new Intent(MainActivity.this,maps.class);
                intent.putExtra("section",rawdata);
                startActivity(intent);


            }
        });

         /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        section = "food";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("section");
            section = value;
        }else{
            section = "food";
        }

        /*Downloading data from below url*/

        uri=  "https://api.foursquare.com/v2/venues/explore"
                + "?v=20130815"
                + "&ll="+latitude1+","+longitude1
                + "&section="+section;
        uri = uri + "&client_id=1RXIWWMGEKK5MQUBU41REIYE4ZY3FGOAG4AOMFX20NBGSPWA" + "&client_secret=K4KP34R2NIJZXB1OSEUTGI02N5WU4MIHGXWUVOWU3KGORE5B" ;

        //new AsyncHttpTask().execute(uri);
        Intent service = new Intent(this,listservice.class);
        //service.putExtra(listservice.Uri,uri);
        this.startService(service);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        Log.e(TAG, "searchView: " + searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search){
            if(search.getVisibility() == View.INVISIBLE) {
                search.setVisibility(View.VISIBLE);
            }else{
                search.setVisibility(View.INVISIBLE);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
