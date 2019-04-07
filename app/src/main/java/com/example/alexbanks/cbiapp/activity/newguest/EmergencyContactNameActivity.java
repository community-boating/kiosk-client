package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.keyboard.CustomKeyboard;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateEmergencyContactName;

public class EmergencyContactNameActivity extends BaseActivity<ProgressStateEmergencyContactName> {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_emergencycontact_name);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_SIMPLE, R.id.custom_keyboard_view_ec_name);
        customKeyboard.showCustomKeyboard();
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.setTextViewFocuses();
    }

}
