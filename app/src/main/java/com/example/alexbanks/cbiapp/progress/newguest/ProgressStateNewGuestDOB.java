package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestDOBActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;

public class ProgressStateNewGuestDOB extends ProgressState {

    private static final String KEY_DOB_DAY="dob_day";
    private static final String KEY_DOB_MONTH="dob_month";
    private static final String KEY_DOB_YEAR="dob_year";

    public ProgressStateNewGuestDOB() {
    }

    public Integer getDOBDay(){
        return getInteger(KEY_DOB_DAY);
    }

    public void setDOBDay(Integer v){
        putInteger(KEY_DOB_DAY, v);
    }

    public Integer getDOBMonth(){
        return getInteger(KEY_DOB_MONTH);
    }

    public void setDOBMonth(Integer v){
        putInteger(KEY_DOB_MONTH, v);
    }

    public Integer getDOBYear(){
        return getInteger(KEY_DOB_YEAR);
    }

    public void setDOBYear(Integer year){
        putInteger(KEY_DOB_YEAR, year);
    }

    public boolean isDOBDayValid(){
        return contains(KEY_DOB_DAY);
    }

    public boolean isDOBMonthValid(){
        return contains(KEY_DOB_MONTH);
    }

    public boolean isDOBYearValid(){
        return contains(KEY_DOB_YEAR);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestDOBActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestPhone();
    }

    @Override
    public boolean isProgressStateComplete(){
        return this.isDOBDayValid() && this.isDOBMonthValid() && this.isDOBYearValid();
    }
}
