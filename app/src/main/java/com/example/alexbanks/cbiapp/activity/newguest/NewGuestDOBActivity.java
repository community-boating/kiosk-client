package com.example.alexbanks.cbiapp.activity.newguest;

import android.app.Activity;
import android.icu.util.Calendar;
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
import com.example.alexbanks.cbiapp.input.SpinnerCustomInput;
import com.example.alexbanks.cbiapp.keyboard.CustomKeyboard;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NewGuestDOBActivity extends BaseActivity<ProgressStateNewGuestDOB> {

    SpinnerCustomInput spinnerDobDay;
    SpinnerCustomInput spinnerDobMonth;
    SpinnerCustomInput spinnerDobYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_newguest_dob);
        setContentView(R.layout.layout_newguest_dob);

        spinnerDobDay = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_day);
        spinnerDobMonth = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_month);
        spinnerDobYear = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_year);

        List<String> months = Arrays.asList(getResources().getStringArray(R.array.act_4_birthday_months));
        Date date = new Date();
        java.util.Calendar c = java.util.Calendar.getInstance();
        int yearEnd = c.get(Calendar.YEAR);

        int yearStart = yearEnd-70;
        List<String> years = new ArrayList<>(yearEnd-yearStart);
        for(int i = yearStart; i <= yearEnd; i++){
            years.add(Integer.toString(i, 10));
        }
        List<String> days = new ArrayList<>(31);
        for(int i = 1; i <= 31; i++){
            days.add(Integer.toString(i, 10));
        }

        spinnerDobDay.setAdapterFromList(days);
        spinnerDobMonth.setAdapterFromList(months);
        spinnerDobYear.setAdapterFromList(years);

        //CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_dob);
        //customKeyboard.addTextViewsFromCustomInputManager();
        //customKeyboard.showCustomKeyboard();
        //customKeyboard.setTextViewFocuses();
    }

    public void onContinueClick(View v){
        this.nextProgress();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}