package com.example.alexbanks.cbiapp.progress.validator;

import android.util.Log;

import com.example.alexbanks.cbiapp.progress.ProgressState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ProgressStateValidator {

    private static Map<Class<? extends ProgressState>, Map<String, ProgressStateValueValidator>> valueValidators;

    public static final ProgressStateValueValidator DEFAULT_VALUE_VALIDATOR = new ProgressStateValueValidator() {
        @Override
        public String isValueValid(String value) {
            return null;
        }
    };

    public static void addProgressStateValidator(Class<? extends ProgressState> progressStateClass,
                                                 String progressStateVariableName, ProgressStateValueValidator valueValidator) {
        Map<String, ProgressStateValueValidator> progressStateValueValidators;
        if(getValueValidators().containsKey(progressStateClass)) {
            progressStateValueValidators = getValueValidators().get(progressStateClass);
        } else {
            progressStateValueValidators = new TreeMap<String, ProgressStateValueValidator>();
            getValueValidators().put(progressStateClass, progressStateValueValidators);
        }
        progressStateValueValidators.put(progressStateVariableName, valueValidator);
    }

    public static ProgressStateValueValidator getValueValidator(ProgressState progressState, String progressStateVariableName){
        Map<String, ProgressStateValueValidator> progressStateValueValidators = getValueValidators().get(progressState.getClass());
        if(progressStateValueValidators == null || !progressStateValueValidators.containsKey(progressStateVariableName))
            return DEFAULT_VALUE_VALIDATOR;
        return progressStateValueValidators.get(progressStateVariableName);
    }

    public static String isProgressStateValueValid(ProgressState progressState, String progressStateValueName){
        ProgressStateValueValidator valueValidator = getValueValidator(progressState, progressStateValueName);
        String progressStateValue = progressState.get(progressStateValueName);
        return valueValidator.isValueValid(progressStateValue);
    }

    public static boolean isProgressStateValid(ProgressState progressState){
        Map<String, ProgressStateValueValidator> progressStateValueValidators = getValueValidators().get(progressState.getClass());
        if(progressStateValueValidators==null) {
            Log.d("derpderpmerp", "not validated");
            return true;
        }
        Log.d("derpderpmerp", "derpsize : " + progressStateValueValidators.size());
        for(Map.Entry<String, ProgressStateValueValidator> entry : progressStateValueValidators.entrySet()){
            String progressStateVariableName = entry.getKey();
            String progressStateVariableValue = progressState.get(progressStateVariableName);
            ProgressStateValueValidator valueValidator = entry.getValue();
            if(valueValidator.isValueValid(progressStateVariableValue) != null)
                return false;
        }
        return true;
    }

    private static Map<Class<? extends ProgressState>, Map<String, ProgressStateValueValidator>> getValueValidators(){
        if(valueValidators == null)
            valueValidators = new HashMap<>();
        return valueValidators;
    }

}
