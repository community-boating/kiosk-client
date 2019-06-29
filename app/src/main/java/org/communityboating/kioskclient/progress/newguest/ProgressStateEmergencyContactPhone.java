package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.EmergencyContactPhoneActivity;
import org.communityboating.kioskclient.progress.ProgressState;

public class ProgressStateEmergencyContactPhone extends ProgressStateGenericPhone {

    static{
        ProgressStateGenericPhone.addPhoneValidators(ProgressStateEmergencyContactPhone.class);
    }

    public ProgressStateEmergencyContactPhone(){

    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return EmergencyContactPhoneActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(){
        return new ProgressStateNewGuestWaiver();
    }

}
