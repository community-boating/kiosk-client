package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.GenericPhoneActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestPhone extends ProgressState {

    private static final String KEY_PHONE_1="phone_1";
    private static final String KEY_PHONE_2="phone_2";
    private static final String KEY_PHONE_3="phone_3";

    public ProgressStateNewGuestPhone(){
    }

    public void setPhone1(Integer v){
        putInteger(KEY_PHONE_1, v);
    }

    public Integer getPhone1(){
        return getInteger(KEY_PHONE_1);
    }

    public void setPhone2(Integer v){
        putInteger(KEY_PHONE_2, v);
    }

    public Integer getPhone2(){
        return getInteger(KEY_PHONE_2);
    }

    public void setPhone3(Integer v){
        putInteger(KEY_PHONE_3, v);
    }

    public Integer getPhone3(){
        return getInteger(KEY_PHONE_3);
    }

    public boolean isPhone1Valid(){
        return contains(KEY_PHONE_1);
    }

    public boolean isPhone2Valid() {
        return contains(KEY_PHONE_2);
    }

    public boolean isPhone3Valid(){
        return contains(KEY_PHONE_3);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return GenericPhoneActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestEmail();
    }

    @Override
    public boolean isProgressStateComplete(){
        return this.isPhone1Valid() && this.isPhone2Valid() && this.isPhone3Valid();
    }
}
