package com.example.alexbanks.cbiapp.activity.newguest;

import android.content.Context;
import android.hardware.input.InputManager;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSession;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.input.CustomInputManager;
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
