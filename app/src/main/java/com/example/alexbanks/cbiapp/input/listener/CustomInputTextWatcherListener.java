package com.example.alexbanks.cbiapp.input.listener;

import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.input.CustomInputManager;
import com.example.alexbanks.cbiapp.input.EditTextCustomInput;
import com.example.alexbanks.cbiapp.progress.ProgressState;
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
        checkCompletion(value.length());
    }

    private void checkCompletion(int len){
        //TODO this is maybe not the bset
        if(inputRef.getInputType() == InputType.TYPE_CLASS_NUMBER){
            int max = getFilterLength(inputRef);
            if(max > -1 && len == max){
                String variableName = inputRef.progressStateVariableName;
                ProgressState active = CustomInputManager.getActiveProgressState();
                View view = inputRef.focusSearch(View.FOCUS_RIGHT);
                if(view != null && ProgressStateValidator.isProgressStateValueValid(active, variableName) == null)
                    view.requestFocus();
            }
        }
    }

    private int getFilterLength(EditText view){
        if(view.getFilters().length < 1)
            return -1;
        InputFilter filter = view.getFilters()[0];
        if(filter instanceof InputFilter.LengthFilter){
            InputFilter.LengthFilter lengthFilter = (InputFilter.LengthFilter)filter;
            return lengthFilter.getMax();
        }
        return -1;
    }

    public EditTextCustomInput getInputRef(){
        return inputRef;
    }

}