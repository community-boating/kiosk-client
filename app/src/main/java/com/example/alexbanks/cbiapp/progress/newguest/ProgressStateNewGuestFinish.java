package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestBeginSignatureActivity;
import com.example.alexbanks.cbiapp.activity.newguest.NewGuestFinishActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestFinish extends ProgressState {

    public ProgressStateNewGuestFinish(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestFinishActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return null;
    }

    @Override
    public boolean isProgressStateComplete(){
        return true;
    }

}
