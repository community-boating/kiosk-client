package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestNameActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateNotBlankValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateNewGuestName extends ProgressState {

    private static final String KEY_FIRST_NAME="first_name";
    private static final String KEY_LAST_NAME="last_name";
    static {
        Class<ProgressStateNewGuestName> clazz=ProgressStateNewGuestName.class;
        ProgressStateValidator.addProgressStateValidator(clazz, KEY_FIRST_NAME, new ProgressStateNotBlankValueValidator("Enter a first name"));
        ProgressStateValidator.addProgressStateValidator(clazz, KEY_LAST_NAME, new ProgressStateNotBlankValueValidator("Enter a last name"));
    }

    public ProgressStateNewGuestName() {
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestNameActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestDOB();
    }

    public String getFirstName(){
        return this.get(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName){
        this.put(KEY_FIRST_NAME, firstName);
    }

    public String getLastName(){
        return this.get(KEY_LAST_NAME);
    }

    public void setLastName(String lastName){
        this.put(KEY_LAST_NAME, lastName);
    }

    public boolean isFirstNameValid(){
        return !"".equals(getFirstName());
    }

    public boolean isLastNameValid(){
        return !"".equals(getLastName());
    }
}
