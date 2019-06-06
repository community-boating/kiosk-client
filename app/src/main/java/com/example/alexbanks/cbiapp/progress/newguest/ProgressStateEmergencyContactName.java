package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.EmergencyContactNameActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateNotBlankValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValueValidator;

public class ProgressStateEmergencyContactName extends ProgressState {
    public static final String KEY_EC_NAME_FIRST="ec_name_first";
    public static final String KEY_EC_NAME_LAST="ec_name_last";
    static{
        Class<ProgressStateEmergencyContactName> emergencyContactNameClass = ProgressStateEmergencyContactName.class;
        ProgressStateValidator.addProgressStateValidator(emergencyContactNameClass, KEY_EC_NAME_FIRST, new ProgressStateNotBlankValueValidator("Enter a first name"));
        ProgressStateValidator.addProgressStateValidator(emergencyContactNameClass, KEY_EC_NAME_LAST, new ProgressStateNotBlankValueValidator("Enter a last name"));
    }
    public ProgressStateEmergencyContactName(){

    }

    public String getECFirstName(){
        return get(KEY_EC_NAME_FIRST);
    }

    public void setECFirstName(String ecFirstName){
        put(KEY_EC_NAME_FIRST, ecFirstName);
    }

    public String getECLastName(){
        return get(KEY_EC_NAME_LAST);
    }

    public void setECNameLast(String ecNameLast){
        put(KEY_EC_NAME_LAST, ecNameLast);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return EmergencyContactNameActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(){return new ProgressStateEmergencyContactPhone();}

}
