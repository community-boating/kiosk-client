package org.communityboating.kioskclient.activity.newguest;

import android.content.Context;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.keyboardnew.CustomKeyboardPopupWindow;
import org.communityboating.kioskclient.keyboardnew.CustomKeyboardPreviewPopupWindow;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestEmail;

public class NewGuestEmailActivity extends BaseActivity<ProgressStateNewGuestEmail> {

    CustomKeyboardPopupWindow window;

    CustomKeyboardPreviewPopupWindow view;

    EditText emailText;

    TextWatcher emailTextWatcher;

    CheckBox optionCheckBox;

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        //View parent = findViewById(R.id.root_layout);
        //view = new CustomKeyboardPreviewPopupWindow(this);
        //window = new CustomKeyboardPopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //view.setFitsSystemWindows(false);
        //view.getRootView().setFitsSystemWindows(false);
        //parent.setFitsSystemWindows(false);
        try {
        //    window.showPopupView(parent, Gravity.NO_GRAVITY, 0, 0);
        //    window.hide();
            Log.d("done now", "done now");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.d("done now", "not done now, very sad D:");
        }
    }

    VelocityTracker tracker = VelocityTracker.obtain();

    @Override
    public boolean dispatchTouchEvent(MotionEvent e){
        boolean r = super.dispatchTouchEvent(e);
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            tracker.clear();
            tracker.addMovement(e);
            //view.x = (int)e.getRawX();
            //view.y = (int)e.getRawY();
            //window.show();
        }else if(e.getAction() == MotionEvent.ACTION_UP){
            //window.hide();
            tracker.addMovement(e);
        }
        else if(e.getAction() == MotionEvent.ACTION_MOVE){
            tracker.addMovement(e);
            tracker.computeCurrentVelocity(1000);
            int maxOff = 0;
            float lagOffset = .12f;
            float xOff = Math.max(Math.min(tracker.getXVelocity() * lagOffset, maxOff), -maxOff);
            float yOff = Math.max(Math.min(tracker.getYVelocity() * lagOffset, maxOff), -maxOff);
            //view.x = (int)(e.getRawX() + xOff);
            //view.y = (int)(e.getRawY() + yOff);
            //view.postInvalidateOnAnimation();
        }
        //window.updatePosition((int)e.getRawX(), (int)e.getRawY());
        return r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_email);
        InputMethodManager inputMethodService = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputMethodService.dispatchKeyEventFromInputMethod(em);
        //InputManager session = (InputManager) getSystemService(Context.INPUT_SERVICE);
        //inputMethodService.showInputMethodPicker();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //CustomInputManager.activeProgressState = progress.getCurrentProgressState();

        emailText=findViewById(R.id.new_guest_email);

        optionCheckBox=findViewById(R.id.new_guest_option_checkbox);

        emailText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    nextProgress();
                    return true;
                }
                return false;
            }
        });

        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_FULL, R.id.custom_keyboard_view_email);
        //customKeyboard.addTextView(emailText);
        customKeyboard.addTextViewsFromCustomInputManager();
        if(customKeyboard.displayCustomKeyboard(this)) {
            customKeyboard.showCustomKeyboard();
            customKeyboard.setTextViewFocuses();
        }

    }

}
