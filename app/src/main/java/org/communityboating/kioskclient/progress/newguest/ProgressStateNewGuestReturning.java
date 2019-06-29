package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestReturningActivity;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestReturning extends ProgressState {

    public static final String KEY_RETURNING_MEMBER="returning_member";

    static{
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestReturning.class, KEY_RETURNING_MEMBER, new ProgressStateNotBlankValueValidator("Choose a new guest type"));
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
