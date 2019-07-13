package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.EmergencyContactPhoneActivity;
import org.communityboating.kioskclient.activity.newguest.NewGuestPhoneActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

public class ProgressStateNewGuestPhone extends ProgressStateGenericPhone {

    static{
        ProgressStateGenericPhone.addPhoneValidators(ProgressStateNewGuestPhone.class);
    }

    public ProgressStateNewGuestPhone(){

    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return NewGuestPhoneActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(Progress progress){
        ProgressStateNewGuestReturning progressStateNewGuestReturning = progress.findByProgressStateType(ProgressStateNewGuestReturning.class);
        if(progressStateNewGuestReturning.getReturningMember())
            return new ProgressStateNewGuestFinish();
        return new ProgressStateNewGuestEmail();
    }
}
