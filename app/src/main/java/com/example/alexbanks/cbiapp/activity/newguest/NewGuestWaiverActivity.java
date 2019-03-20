package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;

import java.io.IOException;
import java.io.InputStream;

public class NewGuestWaiverActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_waiver);
        Log.d("not much", "created");
        loadWaiver();
    }

    //Load the html file storing the waiver into a WebView
    protected void loadWaiver(){
        //TODO offload to non-main thread probably
        WebView webView = (WebView)findViewById(R.id.waiver_web_view);
        try{
            InputStream fin = getAssets().open("waiver.html");
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            fin.close();

            webView.loadData(new String(buff), "text/html", "UTF-8");
        }catch(IOException e){
            Log.d("not much", "failed");
            e.printStackTrace();
        }
    }

}
