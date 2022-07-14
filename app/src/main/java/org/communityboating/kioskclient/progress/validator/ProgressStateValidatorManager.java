package org.communityboating.kioskclient.progress.validator;

import android.util.Log;

import org.communityboating.kioskclient.progress.ProgressState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.communityboating.kioskclient.progress.*;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestDOB;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestName;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestRegistrationType;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestReturning;

public class ProgressStateValidatorManager {

    private static Map<Class<? extends ProgressState>, ProgressStateValidator> valueValidators;

    static {
        new ProgressStateNewGuestName();
        new ProgressStateNewGuestBegin();//.class.getName();
        new ProgressStateNewGuestRegistrationType();//.class.getName();
        new ProgressStateNewGuestReturning();//.class.getName();
        new ProgressStateNewGuestDOB();//.class.getName();
        ProgressStateNewGuestDOB.addValidators();
    }

    public static final ProgressStateValidator DEFAULT_VALUE_VALIDATOR = new ProgressStateValidator() {
        @Override
        public String isProgressStateValueValid(String key, String value, ProgressState progressState, Progress progress) {
            return null;
        }
        @Override
        public boolean isProgressStateValid(ProgressState progressState, Progress progress){
            return true;
        }
    };

    public static void addValueValidator(Class<? extends ProgressState> progressStateClass,
                                                 String progressStateVariableName, ProgressStateValueValidatorBase valueValidator) {
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

    public static String isProgressStateValueValid(ProgressState progressState, Progress progress, String progressStateValueName){
        ProgressStateValidator valueValidator = getValidator(progressState, progressStateValueName);
        String progressStateValue = progressState.get(progressStateValueName);
        return valueValidator.isProgressStateValueValid(progressStateValueName, progressStateValue, progressState, progress);
    }

    public static boolean isProgressStateValid(ProgressState progressState, Progress progressContext){
        ProgressStateValidator validator = getValueValidators().get(progressState.getClass());
        Log.d("derpderpmerp", "not validated " + getValueValidators().containsKey(progressState.getClass()));
        Log.d("derpderpmerp", "not validated " + progressState.getClass().getName() + " : " + getValueValidators().entrySet().iterator().next().getKey().getName());
        for(Map.Entry<Class<? extends ProgressState>, ProgressStateValidator> entry : getValueValidators().entrySet()){
            Log.d("derpderpmerp", "not validated + " + entry.getKey().getName());
        }
        if(validator==null) {
            Log.d("derpderpmerp", "not validated");
            return true;
        }
        return validator.isProgressStateValid(progressState, progressContext);
    }

    private static Map<Class<? extends ProgressState>, ProgressStateValidator> getValueValidators(){
        if(valueValidators == null)
            valueValidators = new HashMap<>();
        return valueValidators;
    }

}
