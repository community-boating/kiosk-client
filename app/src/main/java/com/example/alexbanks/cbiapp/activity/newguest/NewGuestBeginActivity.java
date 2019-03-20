package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;

public class NewGuestBeginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaaa", "ffff");
        //TODO fix this name maybe?
        setContentView(R.layout.activity_test);
    }

    public void handleBeginClick(View view){
        this.nextProgress();
    }

}
