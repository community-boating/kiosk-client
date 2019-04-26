package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateEmergencyContactName;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestWaiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class NewGuestWaiverActivity extends BaseActivity<ProgressStateNewGuestWaiver> {

    CheckBox waiverCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_waiver);
        waiverCheckbox=findViewById(R.id.new_guest_waiver_checkbox);
        //TODO pretty bad, not generalized like the rest fix, this at some point
        if(this.getProgressState().getWaiverAccept())
            waiverCheckbox.setChecked(true);
        waiverCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getProgressState().setWaiverAccept(isChecked);
            }
        });
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
