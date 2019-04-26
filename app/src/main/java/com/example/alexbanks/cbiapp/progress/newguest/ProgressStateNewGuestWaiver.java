package com.example.alexbanks.cbiapp.progress.newguest;

import android.app.Activity;

import com.example.alexbanks.cbiapp.activity.newguest.NewGuestWaiverActivity;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateNotBlankValueValidator;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class ProgressStateNewGuestWaiver extends ProgressState {

    public static final String KEY_WAIVER_ACCEPT="waiver_accept";

    static {
        ProgressStateValidator.addProgressStateValidator(ProgressStateNewGuestWaiver.class, KEY_WAIVER_ACCEPT, new ProgressStateNotBlankValueValidator(""));
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
    public ProgressState createNextProgressState() {
        return new ProgressStateNewGuestSignature();
    }

}
