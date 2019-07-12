package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.activity.DialogFragmentAdminTooltip;
import org.communityboating.kioskclient.activity.DialogFragmentUnder18;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;

import java.io.File;
import java.util.Scanner;

public class NewGuestBeginActivity extends BaseActivity<ProgressStateNewGuestBegin> implements View.OnClickListener, View.OnTouchListener {

    Button beginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaaa", "ffff");
        //TODO fix this name maybe?
        setContentView(R.layout.activity_test);
        beginButton=findViewById(R.id.new_guest_begin_button_begin);
        beginButton.setOnClickListener(this);
        beginButton.setOnTouchListener(this);
    }

    @Override
    public boolean canTimeout(){
        return false;
    }

    public void handleBeginClick(View view){
        this.nextProgress();
    }

    @Override
    public void onClick(View v) {
        this.nextProgress();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.isPressed() && event.getAction() == MotionEvent.ACTION_UP){
            long duration = event.getEventTime() - event.getDownTime();
            if(duration > 3000){
                displayFragment(new DialogFragmentAdminTooltip());
                return true;
            }
        }
        return false;
    }
}
