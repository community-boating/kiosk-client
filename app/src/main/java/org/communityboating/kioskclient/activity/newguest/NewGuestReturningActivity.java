package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.view.View;

import org.communityboating.kioskclient.BasePackageClass;
import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestReturning;

public class NewGuestReturningActivity extends BaseActivity<ProgressStateNewGuestReturning> {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_returning);
    }

    public void handleNewButtonClick(View view){
        ProgressStateNewGuestReturning progressState = this.getProgressState();
        progressState.setReturningMember(false);
        progress.setTotalCompletionCount(10);
        nextProgress();
    }

    public void handleReturningButtonClick(View view){
        ProgressStateNewGuestReturning progressState = this.getProgressState();
        progressState.setReturningMember(true);
        progress.setTotalCompletionCount(3);
        nextProgress();
    }

}
