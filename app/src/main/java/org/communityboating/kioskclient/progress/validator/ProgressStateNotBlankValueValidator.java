package org.communityboating.kioskclient.progress.validator;

import android.util.Log;

public class ProgressStateNotBlankValueValidator extends ProgressStateValueValidator {

    private String invalidText;

    public ProgressStateNotBlankValueValidator(String invalidText){
        this.invalidText = invalidText;
    }

    @Override
    public String isValueValid(String value) {
        Log.d("derpderpherp", "valuevaluevalue " + value + " : " + invalidText +(value == null || value.isEmpty() ? invalidText : null));
        return (value == null || value.isEmpty() ? invalidText : null);
    }
}
