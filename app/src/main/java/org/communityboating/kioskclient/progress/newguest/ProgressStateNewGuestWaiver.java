package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestWaiverActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestWaiver extends ProgressState {

    public static final String KEY_WAIVER_ACCEPT="waiver_accept";

    static {
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestWaiver.class, KEY_WAIVER_ACCEPT, new ProgressStateNotBlankValueValidator(""));
    }

    public ProgressStateNewGuestWaiver(){
    }

    public Boolean getWaiverAccept(){
        return this.get(KEY_WAIVER_ACCEPT)==null?false:true;
    }

    public void setWaiverAccept(Boolean accept){
        if(accept==null||!accept)
            this.remove(KEY_WAIVER_ACCEPT);
        this.put(KEY_WAIVER_ACCEPT, "true");
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestWaiverActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestSignature();
    }

}
