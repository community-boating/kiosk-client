package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestReturningActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateNotBlankValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateNewGuestReturning extends ProgressState {

    public static final String KEY_RETURNING_MEMBER="returning_member";

    static{
        ProgressStateValidator.addProgressStateValidator(ProgressStateNewGuestReturning.class, KEY_RETURNING_MEMBER, new ProgressStateNotBlankValueValidator("Choose a new guest type"));
    }

    public Boolean getReturningMember(){
        return getBoolean(KEY_RETURNING_MEMBER);
    }

    public void setReturningMember(Boolean returningMember){
        putBoolean(KEY_RETURNING_MEMBER, returningMember);
    }

    @Override
    public ProgressState createNextProgressState(){
        return new ProgressStateNewGuestName();
    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return NewGuestReturningActivity.class;
    }

}
