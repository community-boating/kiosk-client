package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestBeginActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

public class ProgressStateNewGuestBegin extends ProgressState {

    public ProgressStateNewGuestBegin(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestBeginActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress){
        return new ProgressStateNewGuestReturning();
    }

}
