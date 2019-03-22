package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestWaiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class NewGuestWaiverActivity extends BaseActivity<ProgressStateNewGuestWaiver> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_waiver);
        Log.d("nullupdate", "created");
        Log.d("not much", "created");
        loadWaiver();
    }

    //Load the html file storing the waiver into a WebView
    protected void loadWaiver(){
        //TODO offload to non-main thread probably
        WebView webView = (WebView)findViewById(R.id.waiver_web_view);
        webView.loadUrl("file:///android_asset/waiver.html");
    }

    public void handleButtonPressed(View v){
        this.nextProgress();
    }

}
