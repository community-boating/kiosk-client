package org.communityboating.kioskclient.progress.validator;

import android.util.Log;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

import java.util.Map;
import java.util.TreeMap;

public class ProgressStateValidatorValues implements ProgressStateValidator{

    Map<String, ProgressStateValueValidatorBase> valueValidatorMap;

    public ProgressStateValidatorValues(){
        valueValidatorMap = new TreeMap<>();
    }

    public void putValueValidator(String key, ProgressStateValueValidatorBase valueValidator){
        valueValidatorMap.put(key, valueValidator);
        Log.d("cbiapp", "derpderphello this called this is strange");
    }

    @Override
    public String isProgressStateValueValid(String key, String value, ProgressState progressState, Progress progress){
        if(!valueValidatorMap.containsKey(key))
            return null;
        ProgressStateValueValidatorBase validatorBase = valueValidatorMap.get(key);
        if(validatorBase instanceof ProgressStateValueValidator)
            return ((ProgressStateValueValidator)validatorBase).isValueValid(value);
        else if(validatorBase instanceof ProgressStateValueValidatorProgressContext)
            return((ProgressStateValueValidatorProgressContext)validatorBase).isValueValid(value, progressState, progress);
        else if(validatorBase instanceof ProgressStateValueValidatorProgressStateContext)
            return ((ProgressStateValueValidatorProgressStateContext)validatorBase).isValueValid(value, progressState);
        else return null;
    }

    @Override
    public boolean isProgressStateValid(ProgressState progressState, Progress progressContext){
        for(Map.Entry<String, ProgressStateValueValidatorBase> validatorEntry : valueValidatorMap.entrySet()){
            if(validatorEntry.getValue() instanceof ProgressStateValueValidator)
                if(((ProgressStateValueValidator)validatorEntry.getValue()).isValueValid(progressState.get(validatorEntry.getKey())) != null)
                    return false;
            if(validatorEntry.getValue() instanceof ProgressStateValueValidatorProgressContext)
                if(((ProgressStateValueValidatorProgressContext)validatorEntry.getValue()).isValueValid(progressState.get(validatorEntry.getKey()), progressState, progressContext) != null)
                    return false;
            if(validatorEntry.getValue() instanceof ProgressStateValueValidatorProgressStateContext)
                if(((ProgressStateValueValidatorProgressStateContext)validatorEntry.getValue()).isValueValid(progressState.get(validatorEntry.getKey()), progressState) != null)
                    return false;
                Log.d("derpderpmerp", "not validated111 " + validatorEntry.getValue().getClass());
        }
        return true;
    }

}
