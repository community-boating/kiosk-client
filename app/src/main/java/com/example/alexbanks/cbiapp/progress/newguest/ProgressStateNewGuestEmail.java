package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestEmailActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateEmailValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateNewGuestEmail extends ProgressState {
    public static final String KEY_EMAIL="email";
    static {
        ProgressStateValidator.addProgressStateValidator(ProgressStateNewGuestEmail.class, KEY_EMAIL, new ProgressStateEmailValueValidator());
    }
    public ProgressStateNewGuestEmail(){
    }

    public String getEmail(){
        return get(KEY_EMAIL);
    }

    public void setEmail(String email){
        put(KEY_EMAIL, email);
    }

    //public boolean isEmailValid(){
    //    return !("".equals(getEmail()));
    //}

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestEmailActivity.class; }

    @Override
    public ProgressState createNextProgressState() { return new ProgressStateEmergencyContactName(); }

}
