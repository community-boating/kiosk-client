package org.communityboating.kioskclient.progress.validator;

import android.util.Log;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProgressStateValidatorManager {

    private static Map<Class<? extends ProgressState>, ProgressStateValidator> valueValidators;

    public static final ProgressStateValidator DEFAULT_VALUE_VALIDATOR = new ProgressStateValidator() {
        @Override
        public String isProgressStateValueValid(String key, String value) {
            return null;
        }
        @Override
        public boolean isProgressStateValid(ProgressState progressState){
            return true;
        }
    };

    public static void addValueValidator(Class<? extends ProgressState> progressStateClass,
                                                 String progressStateVariableName, ProgressStateValueValidator valueValidator) {
        ProgressStateValidatorValues validatorValues = null;
        if(getValueValidators().containsKey(progressStateClass)) {
            ProgressStateValidator validator = getValueValidators().get(progressStateClass);
            if(validator instanceof ProgressStateValidatorValues) {
                validatorValues = (ProgressStateValidatorValues) validator;
            }else if(validator instanceof ProgressStateValidatorMultiple){
                ProgressStateValidatorMultiple validatorMultiple = (ProgressStateValidatorMultiple)validator;
                List<ProgressStateValidator> subValidatorList = validatorMultiple.getValidators();
                boolean hasValidatorValues = false;
                for(ProgressStateValidator subValidator : subValidatorList){
                    if(subValidator instanceof ProgressStateValidatorValues){
                        validatorValues = (ProgressStateValidatorValues)subValidator;
                        hasValidatorValues = true;
                        break;
                    }
                }
                if(!hasValidatorValues){
                    validatorValues = new ProgressStateValidatorValues();
                    validatorMultiple.getValidators().add(validatorValues);
                }
            }else{
                validatorValues = new ProgressStateValidatorValues();
                ProgressStateValidatorMultiple validatorMultiple = new ProgressStateValidatorMultiple(validator, validatorValues);
                getValueValidators().put(progressStateClass, validatorMultiple);
            }
        } else {
            validatorValues = new ProgressStateValidatorValues();
            getValueValidators().put(progressStateClass, validatorValues);
        }
        validatorValues.putValueValidator(progressStateVariableName, valueValidator);
    }

    public static ProgressStateValidator getValidator(ProgressState progressState, String progressStateVariableName){
        ProgressStateValidator validator = getValueValidators().get(progressState.getClass());
        if(validator == null)
            return DEFAULT_VALUE_VALIDATOR;
        return validator;
    }

    public static String isProgressStateValueValid(ProgressState progressState, String progressStateValueName){
        ProgressStateValidator valueValidator = getValidator(progressState, progressStateValueName);
        String progressStateValue = progressState.get(progressStateValueName);
        return valueValidator.isProgressStateValueValid(progressStateValueName, progressStateValue);
    }

    public static boolean isProgressStateValid(ProgressState progressState){
        ProgressStateValidator validator = getValueValidators().get(progressState.getClass());
        if(validator==null) {
            Log.d("derpderpmerp", "not validated");
            return true;
        }
        return validator.isProgressStateValid(progressState);
    }

    private static Map<Class<? extends ProgressState>, ProgressStateValidator> getValueValidators(){
        if(valueValidators == null)
            valueValidators = new HashMap<>();
        return valueValidators;
    }

}
