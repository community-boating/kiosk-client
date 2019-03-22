package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.activity.TextWatcherDelegate;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestName;

public class NewGuestNameActivity extends BaseActivity<ProgressStateNewGuestName> implements View.OnClickListener {

    Button continueButton;
    EditText firstNameText;
    EditText lastNameText;

    TextWatcher firstNameTextWatch;
    TextWatcher lastNameTextWatch;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.layout_newguest_name);
        ProgressStateNewGuestName progressState = getProgressState();
        continueButton = findViewById(R.id.tempcontinue);
        continueButton.setOnClickListener(this);
        firstNameText = findViewById(R.id.new_guest_name_first);
        firstNameTextWatch = new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestName progressState = getProgressState();
                progressState.setFirstName(s.toString());
            }
        };
        firstNameText.addTextChangedListener(firstNameTextWatch);
        firstNameText.setText(progressState.getFirstName());
        lastNameText = findViewById(R.id.new_guest_name_last);
        lastNameTextWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                ProgressStateNewGuestName progressState = getProgressState();
                progressState.setLastName(s.toString());
            }
        };
        lastNameText.addTextChangedListener(lastNameTextWatch);
        lastNameText.setText(progressState.getLastName());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tempcontinue:
                this.nextProgress();
                break;
        }
    }

}