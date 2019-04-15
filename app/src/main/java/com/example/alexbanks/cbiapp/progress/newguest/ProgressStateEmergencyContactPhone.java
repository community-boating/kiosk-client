package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.EmergencyContactPhoneActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateEmergencyContactPhone extends ProgressStateGenericPhone {

    static{
        ProgressStateGenericPhone.addPhoneValidators(ProgressStateEmergencyContactPhone.class);
    }

    public ProgressStateEmergencyContactPhone(){

    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return EmergencyContactPhoneActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(){
        return new ProgressStateNewGuestWaiver();
    }

}
