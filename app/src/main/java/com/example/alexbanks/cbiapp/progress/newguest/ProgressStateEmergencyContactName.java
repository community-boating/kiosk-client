package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.EmergencyContactNameActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateEmergencyContactName extends ProgressState {
    public static final String KEY_EC_NAME_FIRST="ec_name_first";
    public static final String KEY_EC_NAME_LAST="ec_name_last";
    public static final String KEY_EC_TYPE="ec_type";
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

    public String getECType(){
        return get(KEY_EC_TYPE);
    }

    public void setEcType(String ecType){
        put(KEY_EC_TYPE, ecType);
    }

    public boolean isProgressStateComplete(){
        return true;
    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return EmergencyContactNameActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(){return new ProgressStateEmergencyContactPhone();}

}
