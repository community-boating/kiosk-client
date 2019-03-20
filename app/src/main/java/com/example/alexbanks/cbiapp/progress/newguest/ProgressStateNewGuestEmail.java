package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestEmailActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestEmail extends ProgressState {
    public ProgressStateNewGuestEmail(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestEmailActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestWaiver();
    }
}
