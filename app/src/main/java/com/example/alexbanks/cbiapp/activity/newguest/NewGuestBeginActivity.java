package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestBegin;

import java.io.File;
import java.util.Scanner;

public class NewGuestBeginActivity extends BaseActivity<ProgressStateNewGuestBegin> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaaa", "ffff");
        //TODO fix this name maybe?
        setContentView(R.layout.activity_test);
    }

    @Override
    public boolean canTimeout(){
        return false;
    }

    public void handleBeginClick(View view){
        this.nextProgress();
    }

}
