package com.example.alexbanks.cbiapp.keyboard;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;

import java.util.LinkedList;
import java.util.List;

public class CustomKeyboard extends Keyboard implements KeyboardView.OnKeyboardActionListener {

    public static final int KEY_CODE_DELETE = 55551;
    public static final int KEY_CODE_NEXT = 55552;
    public static final int KEY_CODE_SHIFT = 55553;

    public static final int KEYBOARD_MODE_FULL = 0;
    public static final int KEYBOARD_MODE_SIMPLE = 1;
    public static final int KEYBOARD_MODE_NUMBER_PAD = 2;

    private boolean shiftStatus=false;

    private Activity activity;
    private KeyboardView keyboardView;
    private List<View> textViews;
    public CustomKeyboard(Activity activity, int mode, int viewId){
        super(activity, mode==KEYBOARD_MODE_FULL?R.xml.custom_keyboard:mode==KEYBOARD_MODE_SIMPLE?R.xml.custom_keyboard:R.xml.custom_keypad);
        this.activity = activity;
        keyboardView = activity.findViewById(viewId);
        keyboardView.setKeyboard(this);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false);
        updateShiftStatus();
        textViews = new LinkedList<>();
        for(Key k : this.getKeys()){
            Log.d("keyval", "value: " + k.label + ":" + k.codes.length + ":" + k.codes[0]);
        }
        initializeKeys();
    }
    private void updateShiftStatus(){
        keyboardView.setShifted(shiftStatus);
    }
    private void initializeKeys(){
        StringBuffer buffer = new StringBuffer(2);
        for(Key k : this.getKeys()){
            if(k.codes.length == 1 && k.codes[0] >= 'a' && k.codes[0] <= 'z') {
                char c = (char) k.codes[0];
                char C = Character.toUpperCase(c);
                buffer.delete(0, 2);
                buffer.append(c);
                buffer.append(C);
                k.popupCharacters = buffer.toString();
                //k.popupResId = R.xml.custom_keypad_popup;
            }
        }
    }
    public void addTextView(View v){
        textViews.add(v);
    }
    public void clearViews(){
        textViews.clear();
    }
    public void setTextViewFocuses(){
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) hideSoftwareKeyboard(v);
            }
        };
        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v.hasFocus()) hideSoftwareKeyboard(v);
            }
        };
        for(View v : textViews){
            v.setOnFocusChangeListener(focusChangeListener);
            v.setOnClickListener(clickListener);
            if(v instanceof EditText){
                ((EditText)v).setShowSoftInputOnFocus(false);
            }
        }
    }
    public void hideSoftwareKeyboard(View v){
        ((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public void showCustomKeyboard(){
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
    }
    public void hideCustomKeyboard(){
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if(primaryCode == KEY_CODE_NEXT) {
            if(activity instanceof BaseActivity){
                BaseActivity baseActivity = (BaseActivity)activity;
                baseActivity.nextProgress();
            }
        }
        View focusCurrent = activity.getWindow().getCurrentFocus();
        if(focusCurrent == null || !(focusCurrent instanceof EditText)) return;
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();
        if(primaryCode == KEY_CODE_DELETE){
            editable.delete(start - 1, start);
        }else if(primaryCode == KEY_CODE_SHIFT){
            shiftStatus = !shiftStatus;
            updateShiftStatus();
        }else{
            char c = (char)primaryCode;
            if(c >= 'a' && c <= 'z' && shiftStatus)
                c = Character.toUpperCase(c);
            editable.append(c);
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
}
