package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestRegistrationTypeActivity;
import org.communityboating.kioskclient.activity.newguest.NewGuestReturningActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestRegistrationType extends ProgressState {
    public static final String KEY_REGISTRATION_TYPE="registration.type";

    static{
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestRegistrationType.class, KEY_REGISTRATION_TYPE, new ProgressStateNotBlankValueValidator("Choose a registration type"));
    }

    @Override
    public ProgressState createNextProgressState(Progress progress){
        return new ProgressStateNewGuestName();
    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return NewGuestRegistrationTypeActivity.class;
    }
}
