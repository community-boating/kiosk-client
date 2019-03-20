package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestPhoneActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestPhone extends ProgressState {
    public ProgressStateNewGuestPhone(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestPhoneActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestEmail();
    }
}
