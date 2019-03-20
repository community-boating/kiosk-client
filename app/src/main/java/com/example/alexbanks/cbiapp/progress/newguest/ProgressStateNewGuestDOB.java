package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestDOBActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestDOB extends ProgressState {
    public ProgressStateNewGuestDOB() {
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestDOBActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestPhone();
    }
}
