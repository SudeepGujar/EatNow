package com.sudeep.gujar.eatnow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sudeep.gujar.eatnow.R;

/**
 * Created by GUJAR on 2/15/2015.
 */
public class Venue extends ActionBarActivity {
    Toolbar toolbar;
    String venueID, name, address, url, suffix, prefix,VENUEname,longi,lat,phoneno,rate;
    Bitmap img;
    ImageView coverimg;
    Animation animRotate,animtranslate;
    Button call, qrcall;
    int updwn;
    boolean loadingmap = false;
    boolean hascontact = false;
    FloatingActionButton fabqr;
   // private ActionBar mActionBar;
   public static final String CALLBACK_URL = "http://www.coep.org.in";
    private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
    private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";
    private static final String API_URL = "https://api.foursquare.com/v2";
    String clientId = "15FTBHAAJVPPSU50UJOWFX55HS4KOZLA3BQFAABHIBECVOMH";
    String clientSecret = "OBEDPROF1DDQG0TZV0HRYI2HNLIHQ2SLIIYFBUR4ECDWEIGA";

    private FoursquareSession mSession;
    private FoursquareDialog mDialog;
   // private FsqAuthListener mListener;

    private String mTokenUrl;
    private String mAccessToken;


    Boolean login,hasmenu = false,hashrs = false;
    TextView addresstext,distance,rating,hours,likes,tipid1,tip1,tipid2,tip2,tipid3,
            tip3,tip4,tipid4,selftext,selfid,calltext,name_title,ratetitle;
    String menuurl,isopen_str,likes_str,self_review,other1,other1_name,other2,other2_name,other3,other3_name,
        other4,other4_name;
    String fulladd = "";
    boolean selfreviewed = false;
    int count;
    RelativeLayout load;
    ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        addresstext = (TextView)findViewById(R.id.address);
        distance = (TextView)findViewById(R.id.distance);
        rating = (TextView)findViewById(R.id.rate);
        hours = (TextView)findViewById(R.id.openornot);
        likes = (TextView)findViewById(R.id.likes);
        tip1 = (TextView)findViewById(R.id.other1);
        tip2 = (TextView)findViewById(R.id.other2);
        tip3 = (TextView)findViewById(R.id.other3);
        tip4 = (TextView)findViewById(R.id.other4);
        tipid1 = (TextView)findViewById(R.id.other1id);
        tipid2 = (TextView)findViewById(R.id.other2id);
        tipid3 = (TextView)findViewById(R.id.other3id);
        tipid4 = (TextView)findViewById(R.id.other4id);
        selftext = (TextView)findViewById(R.id.selftext);
        selfid = (TextView)findViewById(R.id.selfid);
        calltext = (TextView)findViewById(R.id.phonetext);
        name_title = (TextView)findViewById(R.id.vname);
        loading = (ImageView)findViewById(R.id.loadimage);
        load = (RelativeLayout)findViewById(R.id.initial_layout);
        ratetitle = (TextView)findViewById(R.id.review);
        animRotate  = AnimationUtils.loadAnimation(this, R.anim.rotate);
        loading.startAnimation(animRotate);
        call = (Button)findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneno));
                startActivity(callIntent);

            }
        });

        qrcall = (Button)findViewById(R.id.QRcall);
        qrcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Venue.this,QR_code.class);
                intent.putExtra("contact",phoneno);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        mSession= new FoursquareSession(this);

        mAccessToken= mSession.getAccessToken();
        if(mAccessToken == null){
            login = false;
        }

        mTokenUrl= TOKEN_URL + "&client_id=" + clientId + "&client_secret=" + clientSecret
                + "&redirect_uri=" + CALLBACK_URL;

        String url = AUTH_URL + "&client_id=" + clientId + "&redirect_uri=" + CALLBACK_URL;

        FoursquareDialog.FsqDialogListener listener = new FoursquareDialog.FsqDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                //mListener.onFail("Authorization failed");
            }
        };
        mDialog	= new FoursquareDialog(this, url, listener);


       /* SharedPreferences pref01 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int audio = pref01.getInt("sound", 1);
        int seeklen = pref01.getInt("seeklen", 0);*/
        animtranslate  = AnimationUtils.loadAnimation(this, R.anim.translate);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.updown);
        fabqr = (FloatingActionButton) findViewById(R.id.menufab);
        final FloatingActionButton fabreview = (FloatingActionButton) findViewById(R.id.reviewme);
        final FloatingActionButton fabdir = (FloatingActionButton) findViewById(R.id.direction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(updwn == 0){
                    fabdir.setActivated(true);
                    fabqr.setActivated(true);
                    fabreview.setActivated(true);
                    fabqr.startAnimation(animtranslate);
                    fabreview.startAnimation(animtranslate);
                    fabdir.startAnimation(animtranslate);
                    fabdir.setVisibility(View.VISIBLE);
                    fabqr.setVisibility(View.VISIBLE);
                    fabreview.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.down_white);
                    updwn = 1;

                }
                else if(updwn == 1){
                    fabdir.setVisibility(View.INVISIBLE);
                    fabqr.setVisibility(View.INVISIBLE);
                    fabreview.setVisibility(View.INVISIBLE);
                    fabdir.setActivated(false);
                    fabqr.setActivated(false);
                    fabreview.setActivated(false);
                    updwn = 0;
                    fab.setImageResource(R.drawable.up_white);
                }
                String s= "pressed";
                


            }
        });

        fabdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadingmap) {
                    String s = "pressed";

                    Intent intent = new Intent(Venue.this, mapdirection.class);
                    intent.putExtra("longi", longi);
                    intent.putExtra("lat", lat);
                    intent.putExtra("name", VENUEname);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Venue.this,"Just a moment.. Loading data",Toast.LENGTH_LONG).show();
                }


            }
        });



        fabqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= "pressed_ QR";

                if(hasmenu && loadingmap) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(menuurl)));
                  //  Toast.makeText(Venue.this,"Menuurl = "+menuurl,Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Venue.this,"Opps.. No Menu Url found\n Sorry",Toast.LENGTH_LONG).show();
                }

            }
        });


        fabreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= "pressed";

                if(mAccessToken == null || mAccessToken.equals("")){
                    //you are not loged in
                    new AlertDialog.Builder(Venue.this)
                            .setTitle("Login")
                            .setMessage("You must login to FOURAQUARE to post Review or Tips.\n")
                            .setNeutralButton(android.R.string.cancel, null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialoginterface, int i){
                                    mDialog.show();
                                }
                            })
                            .show();
                }
                else {
                    Intent intent = new Intent(Venue.this, ReviewTip.class);
                    intent.putExtra("id", venueID);
                    intent.putExtra("name", name);
                    intent.putExtra("oauth", mAccessToken);
                    startActivity(intent);
                }

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            venueID = extras.getString("id");
            address = extras.getString("address");
            name = extras.getString("name");
        }

        //toolbar.setTitle(name+"");
        //addresstext.setText(address +"");
        //Toast.makeText(this,"name = "+name,Toast.LENGTH_LONG).show();
        name_title.setText(name);


        coverimg = (ImageView)findViewById(R.id.venueimg);


       // url = "https://api.foursquare.com/v2/venues/"+venueID+"?oauth_token=ZKUCMKI1G5ENGWO0MKEDPU0UXDQN0SJWRKRUEW4SE2ZVWJHB&v=20150218";

        url=  "https://api.foursquare.com/v2/venues/"+venueID
                + "?v=20150115";

        if(mAccessToken == null || mAccessToken.equals("")) {
            url = url + "&client_id=15FTBHAAJVPPSU50UJOWFX55HS4KOZLA3BQFAABHIBECVOMH" + "&client_secret=OBEDPROF1DDQG0TZV0HRYI2HNLIHQ2SLIIYFBUR4ECDWEIGA";
        }else{
            url = url + "&oauth_token="+ mAccessToken;
        }

        new AsyncHttpTask().execute(url);

        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
       // final BitmapDrawable bd = new BitmapDrawable(bitmap);
        final ColorDrawable cd = new ColorDrawable(Color.parseColor("#3F51B5"));
       // mActionBar = getActionBar();
        //mActionBar.setBackgroundDrawable(cd);
        toolbar.setBackgroundDrawable(cd);

        cd.setAlpha(0);

        //toolbar.setDisplayHomeAsUpEnabled(true); //to activate back pressed on home button press
        //toolbar.setDisplayShowHomeEnabled(false); //


        ScrollViewX scrollView = (ScrollViewX) findViewById(R.id.scroll_view);
        scrollView.setOnScrollViewListener(new ScrollViewX.OnScrollViewListener() {

            @Override
            public void onScrollChanged(ScrollViewX v, int l, int t, int oldl, int oldt) {

                cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
            }

            private int getAlphaforActionBar(int scrollY) {
                int minDist = 0,maxDist = 255;
                if(scrollY>maxDist){
                    return 255;
                }
                else if(scrollY<minDist){
                    return 0;
                }
                else {
                    int alpha = 0;
                    alpha = (int)  ((255.0/maxDist)*scrollY);
                    return alpha;
                }
            }
        });

    }

    private void getAccessToken(final String code) {
        //mProgress.setMessage("Getting access token ...");
        //mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i("getting ouath", "Getting access token");

                int what = 0;

                try {
                    URL url = new URL(mTokenUrl + "&code=" + code);

                    Log.i("geting outh", "Opening URL " + url.toString());

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    //urlConnection.setDoOutput(true);

                    urlConnection.connect();

                    JSONObject jsonObj  = (JSONObject) new JSONTokener(streamToString(urlConnection.getInputStream())).nextValue();
                    mAccessToken 		= jsonObj.getString("access_token");

                    Log.i("got access", "Got access token: " + mAccessToken);
                } catch (Exception ex) {
                    what = 1;

                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();


    }

    private String streamToString(InputStream is) throws IOException {
        String str  = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    private void fetchUserName() {
        //mProgress.setMessage("Finalizing ...");

        new Thread() {
            @Override
            public void run() {
                Log.i("fetching username", "Fetching user name");
                int what = 0;

                try {
                    String v	= timeMilisToString(System.currentTimeMillis());
                    URL url 	= new URL(API_URL + "/users/self?oauth_token=" + mAccessToken + "&v=" + v);

                    Log.d("open url", "Opening URL " + url.toString());

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    //urlConnection.setDoOutput(true);

                    urlConnection.connect();

                    String response		= streamToString(urlConnection.getInputStream());
                    JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();

                    JSONObject resp		= (JSONObject) jsonObj.get("response");
                    JSONObject user		= (JSONObject) resp.get("user");

                    String firstName 	= user.getString("firstName");
                    String lastName;
                    if(user.has("lastName")) {
                         lastName = user.getString("lastName");
                    }else{
                        lastName = "";
                    }

                    Log.i("got user", "Got user name: " + firstName + " " + lastName);

                    mSession.storeAccessToken(mAccessToken, firstName + " " + lastName);
                } catch (Exception ex) {
                    what = 1;

                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
            }
        }.start();
    }

    private String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar   = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                if (msg.what == 0) {
                    fetchUserName();
                }
            }
        }
    };
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            //setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            Integer result = 0;
            HttpURLConnection urlConnection = null;

            try {
                {
                    /* forming th java.net.URL object */
                    URL url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    JSONObject venuesJson = executeHttpGet(params[0]);

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

                        parseResult(response.toString());
                        result = 1; // Successful
                    }else{
                        result = 0; //"Failed to fetch data!";
                    }}



            } catch (Exception e) {
               // Log.d(TAG, e.getLocalizedMessage());
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            //setProgressBarIndeterminateVisibility(false);
            /* Download complete. Lets update UI */
            //result = 1;
            if (result == 1) {
                Log.e("Venue", "fetch data!");
                // do here


                likes.setText(likes_str);
                if(hashrs) {
                    hours.setText(isopen_str);
                }else{
                    hours.setVisibility(View.GONE);
                }
                tip1.setText(other1);
                tip2.setText(other2);
                tip3.setText(other3);
                tip4.setText(other4);
                tipid1.setText(other1_name);
                tipid2.setText(other2_name);
                tipid3.setText(other3_name);
                tipid4.setText(other4_name);
                ratetitle.setText("Review And Tips : ");
                load.setVisibility(View.INVISIBLE);
                name_title.setText(name);
                if(selfreviewed) {
                    selftext.setText(self_review);
                    selfid.setText("Your Recent Review :");
                }else{
                    selftext.setVisibility(View.GONE);
                    selfid.setVisibility(View.GONE);
                }
                if(!hasmenu){
                    fabqr.setVisibility(View.GONE);
                }
                if(hascontact){
                    calltext.setText(" Contact : "+phoneno);
                    call.setVisibility(View.VISIBLE);
                    qrcall.setVisibility(View.VISIBLE);
                }
                else{
                    call.setVisibility(View.GONE);
                    qrcall.setVisibility(View.GONE);
                }




                rating.setText("Rating : " + rate);
                addresstext.setText(fulladd);
                loadingmap = true;
                if(prefix == null || prefix.equals("")){
                    Toast.makeText(Venue.this,"Looks like not much data about this venue is available",Toast.LENGTH_SHORT).show();
                }else{
                    new DownloadImage().execute();
                }



            } else {
                Log.e("Venue", "Failed to fetch data!");
            }



        }

        private class DownloadImage extends AsyncTask<Void, Void,Integer > {


            @Override
            protected Integer doInBackground(Void... params) {
                String urldisplay = prefix+"500x300"+suffix;
                int result;

                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    img = BitmapFactory.decodeStream(in);
                    result = 1;
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    result = 0;
                }
                return result;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                if(integer == 1){
                    coverimg.clearAnimation();
                    coverimg.setImageBitmap(img);
                }
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            // JSONArray posts = response.getJSONObject("response")
            //       .getJSONArray("venues");

            JSONObject responseVenue = response.getJSONObject("response").getJSONObject("venue");
            likes_str = responseVenue.getJSONObject("likes").optString("summary");
            hasmenu = responseVenue.optBoolean("hasMenu");
             lat = responseVenue.getJSONObject("location").optString("lat");
             longi = responseVenue.getJSONObject("location").optString("lng");
            if(responseVenue.has("contact") && responseVenue.getJSONObject("contact").has("formattedPhone")) {
                phoneno = responseVenue.getJSONObject("contact").optString("formattedPhone");
                hascontact = true;
            }
            if(responseVenue.has("rating"))rate = responseVenue.optString("rating");
            if(responseVenue.has("location") && responseVenue.getJSONObject("location").has("formattedAddress")) {
                JSONArray formatadd = responseVenue.getJSONObject("location").getJSONArray("formattedAddress");
                for (int addr = 0; addr < formatadd.length(); addr++) {
                    fulladd = fulladd + formatadd.optString(addr);
                }
            }
            if(hasmenu){
                menuurl = responseVenue.getJSONObject("menu").optString("url");
            }
            if (responseVenue.has("hours")) {
                hashrs = true;
                isopen_str = responseVenue.getJSONObject("hours").optString("status");
            }
            JSONArray tips = response.getJSONObject("response").getJSONObject("venue").getJSONObject("tips")
                    .getJSONArray("groups");
            for (int k = 0; k < tips.length(); k++) {
                JSONObject post = tips.optJSONObject(k);
                String user = post.optString("type");
                count = post.optInt("count");
                if(user.equals("self") && count > 0){
                    JSONArray items = post.getJSONArray("items");
                    JSONObject self_tip = items.getJSONObject(0);
                    self_review = self_tip.optString("text");
                    selfreviewed = true;

                }else if(user.equals("others") && count > 0){

                    JSONArray items = post.getJSONArray("items");
                    if(count>=0){
                    JSONObject other_tip1 = items.getJSONObject(0);
                    other1 = other_tip1.optString("text");
                    other1_name = other_tip1.getJSONObject("user").optString("firstName");}

                    if(count>=1){
                    JSONObject other_tip2 = items.getJSONObject(1);
                    other2 = other_tip2.optString("text");
                    other2_name = other_tip2.getJSONObject("user").optString("firstName");}

                        if(count>=2){
                    JSONObject other_tip3 = items.getJSONObject(2);
                    other3 = other_tip3.optString("text");
                    other3_name = other_tip3.getJSONObject("user").optString("firstName");}
                            if(count>=3){
                    JSONObject other_tip4 = items.getJSONObject(3);
                    other4 = other_tip4.optString("text");
                    other4_name = other_tip4.getJSONObject("user").optString("firstName");}
                }

            }


            JSONArray posts = response.getJSONObject("response").getJSONObject("venue").getJSONObject("photos")
                    .getJSONArray("groups");
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                JSONArray items = post.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject venue = items.optJSONObject(j);

                    prefix = venue.optString("prefix");
                    suffix = venue.optString("suffix");


                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.venuemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String msage = "Hey...\n Check out "+name+" Restaurant. I like this and I think you will also like this";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, msage);
            startActivity(Intent.createChooser(share, "Share"));
        }
        if(id == R.id.login){
            mDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }




}
