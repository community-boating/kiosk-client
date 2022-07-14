package org.communityboating.kioskclient.input.listener;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.communityboating.kioskclient.input.CustomInputManager;
import org.communityboating.kioskclient.input.SpinnerCustomInput;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class CustomInputOnItemSelectedListener extends CustomInputProgressStateListener implements AdapterView.OnItemSelectedListener {

    SpinnerCustomInput inputRef;

    public CustomInputOnItemSelectedListener(String progressStateVariableName, SpinnerCustomInput inputRef) {
        super(progressStateVariableName);
        this.inputRef = inputRef;
    }

    @Override
    public void updateProgressStateValidatorError(boolean hidden) {
        TextView view = (TextView)inputRef.getSelectedView();
        String error = ProgressStateValidatorManager.isProgressStateValueValid(CustomInputManager.getActiveProgressState(), CustomInputManager.getActiveProgress(), progressStateVariableName);
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
