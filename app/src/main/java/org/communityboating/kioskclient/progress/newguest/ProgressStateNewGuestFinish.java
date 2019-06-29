package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestFinishActivity;
import org.communityboating.kioskclient.progress.ProgressState;

public class ProgressStateNewGuestFinish extends ProgressState {

    public ProgressStateNewGuestFinish(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestFinishActivity.class; }

    @Override
    public ProgressState createNextProgressState() {
        return null;
    }

}
