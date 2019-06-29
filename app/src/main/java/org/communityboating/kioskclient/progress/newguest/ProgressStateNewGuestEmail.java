package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestEmailActivity;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateEmailValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestEmail extends ProgressState {
    public static final String KEY_EMAIL="email";
    static {
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestEmail.class, KEY_EMAIL, new ProgressStateEmailValueValidator());
    }
    public ProgressStateNewGuestEmail(){
    }

    public String getEmail(){
        return get(KEY_EMAIL);
    }

    public void setEmail(String email){
        put(KEY_EMAIL, email);
    }

    //public boolean isEmailValid(){
    //    return !("".equals(getEmail()));
    //}

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestEmailActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return new ProgressStateEmergencyContactName();
    }

}
