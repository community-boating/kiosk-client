package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.keyboard.CustomKeyboard;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;

public class NewGuestEmailActivity extends BaseActivity<ProgressStateNewGuestEmail> {

    EditText emailText;

    TextWatcher emailTextWatcher;

    CheckBox optionCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_email);

        emailText=findViewById(R.id.new_guest_email);

        optionCheckBox=findViewById(R.id.new_guest_option_checkbox);

        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_FULL, R.id.custom_keyboard_view_email);
        customKeyboard.showCustomKeyboard();
        customKeyboard.addTextView(emailText);
        customKeyboard.setTextViewFocuses();

    }

}
