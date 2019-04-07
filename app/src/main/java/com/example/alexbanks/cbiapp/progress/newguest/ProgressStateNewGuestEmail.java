package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;
import android.content.res.Resources;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.newguest.NewGuestEmailActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestEmail extends ProgressState {
    public static final String KEY_EMAIL="email";
    public ProgressStateNewGuestEmail(){
    }

    public String getEmail(){
        return get(KEY_EMAIL);
    }

    public void setEmail(String email){
        put(KEY_EMAIL, email);
    }

    public boolean isEmailValid(){
        return !("".equals(getEmail()));
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestEmailActivity.class; }

    @Override
    public ProgressState createNextProgressState() { return new ProgressStateEmergencyContactName(); }

    @Override
    public boolean isProgressStateComplete(){
        return isEmailValid();
    }
}
