package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.view.View;

import com.example.alexbanks.cbiapp.BasePackageClass;
import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestReturning;

public class NewGuestReturningActivity extends BaseActivity<ProgressStateNewGuestReturning> {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_returning);
    }

    public void handleNewButtonClick(View view){
        ProgressStateNewGuestReturning progressState = this.getProgressState();
        progressState.setReturningMember(false);
        nextProgress();
    }

    public void handleReturningButtonClick(View view){
        ProgressStateNewGuestReturning progressState = this.getProgressState();
        progressState.setReturningMember(true);
        nextProgress();
    }

}
