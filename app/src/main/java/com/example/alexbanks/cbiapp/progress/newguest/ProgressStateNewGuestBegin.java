package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestBeginActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestBegin extends ProgressState {

    public ProgressStateNewGuestBegin(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestBeginActivity.class; }

    @Override
    public ProgressState createNextProgressState(){
        return new ProgressStateNewGuestName();
    }

    @Override
    public boolean isProgressStateComplete(){ return true;}

}
