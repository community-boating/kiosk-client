package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestNameActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestName extends ProgressState {
    public ProgressStateNewGuestName() {
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestNameActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestDOB();
    }
}
