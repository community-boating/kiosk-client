package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestWaiverActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestWaiver extends ProgressState {
    public ProgressStateNewGuestWaiver(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestWaiverActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return null;
    }
}
