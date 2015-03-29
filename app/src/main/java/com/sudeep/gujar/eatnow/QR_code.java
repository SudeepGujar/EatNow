package com.sudeep.gujar.eatnow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import sudeep.gujar.eatnow.R;

/**
 * Created by GUJAR on 2/22/2015.
 */

public class QR_code extends ActionBarActivity {


    Toolbar toolbar;
    String clipTxt,name;
    ImageLoader imgLoader;
    ImageView qrImg;
    String copiedStr;
    TextView qrTxt;


    String BASE_QR_URL = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=";
    String fullurl = BASE_QR_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(this).build();

        imgLoader = ImageLoader.getInstance();
        imgLoader.init(config);
        qrImg = (ImageView)findViewById(R.id.qrImg);
        qrTxt = (TextView)findViewById(R.id.qrTxt);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clipTxt = extras.getString("contact");
            name = extras.getString("name");
        }
       // clipTxt = "Sudeep";

        if(clipTxt != null && (clipTxt.length() > 0))
        {

            qrTxt.setText(name +":"+clipTxt);
           // copiedStr = clipTxt.toString();
            fullurl = BASE_QR_URL + name +":"+clipTxt;

            imgLoader.displayImage(fullurl, qrImg);

        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("QRMaker")
                    .setCancelable(true)
                    .setMessage("Let data completely load.. go back and come again")
                    .setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();

                        }
                    });
            AlertDialog diag = builder.create();
            diag.show();
        }


    }

}

