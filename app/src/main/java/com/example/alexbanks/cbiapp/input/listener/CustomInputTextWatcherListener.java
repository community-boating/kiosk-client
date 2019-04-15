package com.example.alexbanks.cbiapp.input.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.alexbanks.cbiapp.input.CustomInputManager;
import com.example.alexbanks.cbiapp.input.EditTextCustomInput;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

public class CustomInputTextWatcherListener extends CustomInputProgressStateListener implements TextWatcher {

    EditTextCustomInput inputRef;

    public CustomInputTextWatcherListener(String progressStateVariableName, EditTextCustomInput inputRef) {
        super(progressStateVariableName);
        this.inputRef = inputRef;
    }

    @Override
    public void updateProgressStateValidatorError(boolean hidden) {
        String error = ProgressStateValidator.isProgressStateValueValid(CustomInputManager.getActiveProgressState(), progressStateVariableName);
        if(error != null && !hidden){
            inputRef.setError(error);
        }else{
            inputRef.setError(null);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        CustomInputManager.getActiveProgressState().put(progressStateVariableName, value);
    }

    public EditTextCustomInput getInputRef(){
        return inputRef;
    }

}
