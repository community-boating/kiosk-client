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
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;

import org.w3c.dom.Text;

//TODO move all this keyboard stuff into somewhere more general so I don't keep copying it like this

public class NewGuestDOBActivity extends BaseActivity<ProgressStateNewGuestDOB> {

    EditText dobDayText;
    EditText dobMonthText;
    EditText dobYearText;

    TextWatcher dobDayTextWatch;
    TextWatcher dobMonthTextWatch;
    TextWatcher dobYearTextWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_newguest_dob);
        setContentView(R.layout.layout_newguest_dob);
        ProgressStateNewGuestDOB progressState = getProgressState();
        dobDayText=findViewById(R.id.new_guest_dob_day);
        dobDayText.setText(progressState.getDOBDay()==null?null:progressState.getDOBDay().toString());
        dobMonthText=findViewById(R.id.new_guest_dob_month);
        dobMonthText.setText(progressState.getDOBMonth()==null?null:progressState.getDOBMonth().toString());
        dobYearText=findViewById(R.id.new_guest_dob_year);
        dobYearText.setText(progressState.getDOBYear()==null?null:progressState.getDOBYear().toString());
        dobDayTextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestDOB progressState = getProgressState();
                progressState.setDOBDay(Integer.parseInt(s.toString()));
            }
        };
        dobDayText.addTextChangedListener(dobDayTextWatch);
        dobMonthTextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestDOB progressState = getProgressState();
                progressState.setDOBMonth(Integer.parseInt(s.toString()));
            }
        };
        dobMonthText.addTextChangedListener(dobMonthTextWatch);
        dobYearTextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestDOB progressState = getProgressState();
                progressState.setDOBYear(Integer.parseInt(s.toString()));
            }
        };
        dobYearText.addTextChangedListener(dobYearTextWatch);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_dob);
        customKeyboard.showCustomKeyboard();
        customKeyboard.addTextView(dobDayText);
        customKeyboard.addTextView(dobMonthText);
        customKeyboard.addTextView(dobYearText);
        customKeyboard.setTextViewFocuses();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dobDayText.removeTextChangedListener(dobDayTextWatch);
        dobMonthText.removeTextChangedListener(dobMonthTextWatch);
        dobYearText.removeTextChangedListener(dobMonthTextWatch);
    }

}
