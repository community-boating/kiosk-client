package com.example.alexbanks.cbiapp.input.listener;

import android.view.View;
import android.widget.AdapterView;

public class CustomInputOnItemSelectedListener extends CustomInputProgressStateListener implements AdapterView.OnItemSelectedListener {

    public CustomInputOnItemSelectedListener(String progressStateVariableName) {
        super(progressStateVariableName);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
