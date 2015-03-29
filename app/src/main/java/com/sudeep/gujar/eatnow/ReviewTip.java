package com.sudeep.gujar.eatnow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sudeep.gujar.eatnow.R;

/**
 * Created by GUJAR on 2/22/2015.
 */
public class ReviewTip extends ActionBarActivity {
    Toolbar toolbar;
    EditText reviewtext;
    TextView name;
    String v_name,Accesstoken,venueID,input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AIzaSyDtxpUim66OVg5xlhwtqDhS0uUGw4MvMm8
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.reviewtip_layout);
        toolbar = (Toolbar) findViewById(R.id.appbar);

        setSupportActionBar(toolbar);
        reviewtext = (EditText)findViewById(R.id.review);
        name = (TextView)findViewById(R.id.v_name);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            venueID = extras.getString("id");
            v_name = extras.getString("name");
            Accesstoken = extras.getString("oauth");
        }else{
            //rawdata is empty
        }
        toolbar.setTitle("Your Review");
        name.setText(v_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.doneReview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= "pressed";
                Toast.makeText(ReviewTip.this, s, Toast.LENGTH_SHORT).show();
                input = reviewtext.getText().toString();
                new AsyncpostTask().execute();
                //postData();

            }
        });



    }

    public JSONObject postData() {
        // Create a new HttpClient and Post Header

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://api.foursquare.com/v2/tips/add"
                   /*+ "?venueId="+venueID +
                    "&text=" + URLEncoder.encode(input, "UTF-8") +
                    "&oauth_token=" + Accesstoken */);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("oauth_token", Accesstoken));
            nameValuePairs.add(new BasicNameValuePair("venueId",venueID));
            nameValuePairs.add(new BasicNameValuePair("text",input));
            nameValuePairs.add(new BasicNameValuePair("v","20150115"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader r = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = r.readLine()) != null) {
                sb.append(s);
            }
            return new JSONObject(sb.toString());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class AsyncpostTask extends AsyncTask<Void, Void, Integer> {
        String Error;
        @Override
        protected Integer doInBackground(Void... params) {
          JSONObject tipresponce = postData();
            try {
                int returnCode = Integer.parseInt(tipresponce.getJSONObject("meta")
                        .getString("code"));
                if(returnCode == 200){
                    //success
                    return 1;
                }
                else {
                        Error=tipresponce.getJSONObject("meta")
                                .getString("errorDetail");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                Toast.makeText(ReviewTip.this,"Done",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ReviewTip.this,Error,Toast.LENGTH_SHORT).show();
            }

        }
    }
}
