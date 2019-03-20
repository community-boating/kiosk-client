package com.example.alexbanks.cbiapp.activity.newguest;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;

//TODO move all this keyboard stuff into somewhere more general so I don't keep copying it like this

public class NewGuestDOBActivity extends BaseActivity {

    protected KeyboardView customKeyPadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_newguest_dob);
        setContentView(R.layout.layout_newguest_dob);
        createKeyboards();
        showCustomKeyboard(getWindow().getCurrentFocus());
        initiateKeyboardListeners();
    }

    //Add some event listeners to trigger our keyboard
    protected void initiateKeyboardListeners(){
        ConstraintLayout layoutBase = (ConstraintLayout) this.findViewById(R.id.layout_base);
        int count = layoutBase.getChildCount();
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("not much", "focus has been changed");
                if(hasFocus){
                    NewGuestDOBActivity.this.showCustomKeyboard(v);
                }else{
                    NewGuestDOBActivity.this.hideCustomKeyPad();
                }
            }
        };

        for(int i = 0; i < count; i++){
            View view = layoutBase.getChildAt(i);
            if(view instanceof EditText){
                //Do some hiding of the software keyboard
                ((EditText) view).setShowSoftInputOnFocus(false);
                //
                view.setOnFocusChangeListener(focusChangeListener);
            }
        }

    }

    public static final int KEY_CODE_DELETE = -5;
    public static final int KEY_CODE_ENTER = -10;

    //Do some things to make our fancy keypad work like it should
    protected void createKeyboards(){
        //TODO is this even the main activity?
        Keyboard customKeyPad = new Keyboard(this, R.xml.custom_keypad);
        customKeyPadView = (KeyboardView)findViewById(R.id.customKeyPadView);

        customKeyPadView.setKeyboard(customKeyPad);
        //todo don't handle this for now
        customKeyPadView.setPreviewEnabled(false);

        //set up a handler for the key events from our custom key-pad
        customKeyPadView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                View focusCurrent = NewGuestDOBActivity.this.getWindow().getCurrentFocus();
                if(focusCurrent == null || !(focusCurrent instanceof EditText)) return;
                EditText editText = (EditText) focusCurrent;
                Editable editable = editText.getText();
                int start = editText.getSelectionStart();
                Log.d("not much", "key event fired " + ((char)(primaryCode + '0')) + ":" + (editable == null));
                if(primaryCode==KEY_CODE_DELETE){
                    if(editable!=null && start>0)
                        editable.delete(start-1, start);
                }else if(primaryCode==KEY_CODE_ENTER){
                    //TODO handle some form of enter here
                }else{
                    if(editable!=null)
                        editable.insert(start, Character.toString((char)(primaryCode + '0')));
                }
            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });

        //Hide built in keyboard so its not in the way
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void hideCustomKeyPad(){
        customKeyPadView.setVisibility(View.GONE);
        customKeyPadView.setEnabled(false);
    }

    public void showCustomKeyboard( View v ) {
        customKeyPadView.setVisibility(View.VISIBLE);
        customKeyPadView.setEnabled(true);
        Log.d("not much", "something interesting " + (v == null));
        if( v!=null ) ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
