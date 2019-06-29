package org.communityboating.kioskclient.activity.newguest;

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

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.activity.DialogFragmentUnder18;
import org.communityboating.kioskclient.input.SpinnerCustomInput;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestDOB;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

        //spinnerDobDay = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_day);
        //spinnerDobMonth = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_month);
        //spinnerDobYear = (SpinnerCustomInput)findViewById(R.id.new_guest_dob_year);

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

        //spinnerDobDay.setAdapterFromList(days);
        //spinnerDobMonth.setAdapterFromList(months);
        //spinnerDobYear.setAdapterFromList(years);

        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_dob);
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.showCustomKeyboard();
        customKeyboard.setTextViewFocuses();
    }

    public static final String KEY_DOB_UNDERAGE_AGREE="key_underage_agree";

    @Override
    public boolean nextProgress(){
        Calendar maxDOB = Calendar.getInstance();
        maxDOB.add(Calendar.YEAR, -18);
        if(getProgressState().getCalendarDOB().after(maxDOB)){
            if(getProgressState().contains(KEY_DOB_UNDERAGE_AGREE)) {
                return super.nextProgress();
            }else{
                getProgressState().putBoolean(KEY_DOB_UNDERAGE_AGREE, true);
                displayFragment(new DialogFragmentUnder18());
                return false;
            }
        }
        return super.nextProgress();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}