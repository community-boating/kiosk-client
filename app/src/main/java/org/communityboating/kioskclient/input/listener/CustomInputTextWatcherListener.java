package org.communityboating.kioskclient.input.listener;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.communityboating.kioskclient.input.CustomInputManager;
import org.communityboating.kioskclient.input.EditTextCustomInput;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class CustomInputTextWatcherListener extends CustomInputProgressStateListener implements TextWatcher {

    EditTextCustomInput inputRef;

    public CustomInputTextWatcherListener(String progressStateVariableName, EditTextCustomInput inputRef) {
        super(progressStateVariableName);
        this.inputRef = inputRef;
    }

    @Override
    public void updateProgressStateValidatorError(boolean hidden) {
        String error = ProgressStateValidatorManager.isProgressStateValueValid(CustomInputManager.getActiveProgressState(), CustomInputManager.getActiveProgress(), progressStateVariableName);
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
        if(CustomInputManager.getActiveProgressState()==null)
            return;
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
                Progress activeProgress = CustomInputManager.getActiveProgress();
                View view = inputRef.focusSearch(View.FOCUS_RIGHT);
                if(view != null && ProgressStateValidatorManager.isProgressStateValueValid(active, activeProgress, variableName) == null)
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
