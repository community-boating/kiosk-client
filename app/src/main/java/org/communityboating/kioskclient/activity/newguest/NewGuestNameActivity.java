package org.communityboating.kioskclient.activity.newguest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.activity.TextWatcherDelegate;
import org.communityboating.kioskclient.admin.CBIKioskLauncherActivity;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestName;

public class NewGuestNameActivity extends BaseActivity<ProgressStateNewGuestName> {

    EditText firstNameText;
    EditText lastNameText;

    //TextWatcher firstNameTextWatch;
    //TextWatcher lastNameTextWatch;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        //if(event.getX() < 200)
        //    return true;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.layout_newguest_name);
        InputMethodManager methodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //methodManager.showInputMethodPicker();
        /*View derp = new View(this);
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.TOP;
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = derp.getWindowToken();
        derp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getX() < 200)
                    return true;
                return false;
            }
        });
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        manager.addView(derp, p);*/
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_SIMPLE, R.id.custom_keyboard_view_name);
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.showCustomKeyboard();
        customKeyboard.setTextViewFocuses();
    }

    @Override
    public boolean nextProgress(){
        boolean r = super.nextProgress();
        //removeListeners();
        return r;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //removeListeners();
    }

    @Override
    public void onStop(){
        super.onStop();
        //removeListeners();
    }

    @Override
    public void onPause(){
        super.onPause();
        //removeListeners();
    }

    /*public void removeListeners(){
        Log.d("listeners", "removing them");
        firstNameText.removeTextChangedListener(firstNameTextWatch);
        lastNameText.removeTextChangedListener(lastNameTextWatch);
    }*/

}