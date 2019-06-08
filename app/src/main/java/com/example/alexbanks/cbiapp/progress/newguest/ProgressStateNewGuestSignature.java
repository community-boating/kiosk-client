package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestSignatureActivity;
import com.example.alexbanks.cbiapp.activity.newguest.NewGuestWaiverActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateNotBlankValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateNewGuestSignature extends ProgressState {

    public static final String KEY_SIGNATURE_VALID="signature_valid";

    static{
        ProgressStateValidator.addProgressStateValidator(ProgressStateNewGuestSignature.class, KEY_SIGNATURE_VALID, new ProgressStateNotBlankValueValidator(""));
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
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestFinish();
    }
}
