package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.activity.DialogFragmentUnder18;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;

import java.io.File;
import java.util.Scanner;

public class NewGuestBeginActivity extends BaseActivity<ProgressStateNewGuestBegin> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaaa", "ffff");
        //TODO fix this name maybe?
        setContentView(R.layout.activity_test);
    }

    @Override
    public boolean canTimeout(){
        return false;
    }

    public void handleBeginClick(View view){
        this.nextProgress();
    }

}
