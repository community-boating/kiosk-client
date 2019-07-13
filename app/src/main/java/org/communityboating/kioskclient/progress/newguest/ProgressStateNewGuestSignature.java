package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestSignatureActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestSignature extends ProgressState {

    public static final String KEY_SIGNATURE_VALID="signature_valid";

    static{
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestSignature.class, KEY_SIGNATURE_VALID, new ProgressStateNotBlankValueValidator(""));
    }

    public ProgressStateNewGuestSignature(){
    }

    public Boolean isSignatureValid(){
        return getBoolean(KEY_SIGNATURE_VALID);
    }

    public void setSignatureValid(Boolean signatureValid){
        putBoolean(KEY_SIGNATURE_VALID, signatureValid);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestSignatureActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestFinish();
    }
}
