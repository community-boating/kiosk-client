package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestDOBActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateDOBDayValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateDOBMonthValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateDOBYearValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

import java.util.Calendar;

import java.util.Calendar;
import java.util.Date;

public class ProgressStateNewGuestDOB extends ProgressState {

    public static final String KEY_DOB_DAY="dob_day";
    public static final String KEY_DOB_MONTH="dob_month";
    public static final String KEY_DOB_YEAR="dob_year";

    public static void addValidators(){
        Class<ProgressStateNewGuestDOB> clazz = ProgressStateNewGuestDOB.class;
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_DAY, new ProgressStateDOBDayValueValidator());
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_MONTH, new ProgressStateDOBMonthValueValidator());
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_YEAR, new ProgressStateDOBYearValueValidator());
    }

    static{
        /*Class<ProgressStateNewGuestDOB> clazz = ProgressStateNewGuestDOB.class;
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_DAY, new ProgressStateDOBDayValueValidator());
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_MONTH, new ProgressStateDOBMonthValueValidator());
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_DOB_YEAR, new ProgressStateDOBYearValueValidator());*/
    }

    public ProgressStateNewGuestDOB() {
    }

    public Calendar getCalendarDOB() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getDOBYear(), getDOBMonth() - 1, getDOBDay());
        return calendar;
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
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestPhone();
    }

}
