package com.example.alexbanks.cbiapp.input.listener;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.alexbanks.cbiapp.input.CustomInputManager;
import com.example.alexbanks.cbiapp.input.SpinnerCustomInput;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class CustomInputOnItemSelectedListener extends CustomInputProgressStateListener implements AdapterView.OnItemSelectedListener {

    SpinnerCustomInput inputRef;

    public CustomInputOnItemSelectedListener(String progressStateVariableName, SpinnerCustomInput inputRef) {
        super(progressStateVariableName);
        this.inputRef = inputRef;
    }

    @Override
    public void updateProgressStateValidatorError(boolean hidden) {
        TextView view = (TextView)inputRef.getSelectedView();
        String error = ProgressStateValidator.isProgressStateValueValid(CustomInputManager.getActiveProgressState(), progressStateVariableName);
        if(error != null && !hidden)
            view.setError(error);
        else
            view.setError(null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value=inputRef.getProgressStateValue(position);
        Log.d("derpderp", "wow just got done : " + position);
        CustomInputManager.getActiveProgressState().put(progressStateVariableName, value);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        CustomInputManager.getActiveProgressState().remove(progressStateVariableName);
    }

}
