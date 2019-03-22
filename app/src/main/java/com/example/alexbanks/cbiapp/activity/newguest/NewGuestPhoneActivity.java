package com.example.alexbanks.cbiapp.activity.newguest;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.keyboard.CustomKeyboard;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestPhone;

import org.w3c.dom.Text;

//TODO move all this keyboard stuff into somewhere more general so I don't keep copying it like this

public class NewGuestPhoneActivity extends BaseActivity<ProgressStateNewGuestPhone> {

    EditText phone1Text;
    EditText phone2Text;
    EditText phone3Text;

    TextWatcher phone1TextWatch;
    TextWatcher phone2TextWatch;
    TextWatcher phone3TextWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_phone);
        ProgressStateNewGuestPhone progressState = getProgressState();
        phone1Text = findViewById(R.id.new_guest_phone_1);
        phone1Text.setText(progressState.getPhone1()==null?null:progressState.getPhone1().toString());
        phone2Text = findViewById(R.id.new_guest_phone_2);
        phone2Text.setText(progressState.getPhone2()==null?null:progressState.getPhone2().toString());
        phone3Text = findViewById(R.id.new_guest_phone_3);
        phone3Text.setText(progressState.getPhone3()==null?null:progressState.getPhone3().toString());
        phone1TextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestPhone progressState = getProgressState();
                progressState.setPhone1(Integer.parseInt(s.toString()));
            }
        };
        phone1Text.addTextChangedListener(phone1TextWatch);
        phone2TextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestPhone progressState = getProgressState();
                progressState.setPhone2(Integer.parseInt(s.toString()));
            }
        };
        phone2Text.addTextChangedListener(phone2TextWatch);
        phone3TextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestPhone progressState = getProgressState();
                progressState.setPhone3(Integer.parseInt(s.toString()));
            }
        };
        phone3Text.addTextChangedListener(phone3TextWatch);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_phone);
        customKeyboard.showCustomKeyboard();
        customKeyboard.addTextView(phone1Text);
        customKeyboard.addTextView(phone2Text);
        customKeyboard.addTextView(phone3Text);
        customKeyboard.setTextViewFocuses();
        Log.d("nullupdate", "hmm, phone running still");
    }
}
