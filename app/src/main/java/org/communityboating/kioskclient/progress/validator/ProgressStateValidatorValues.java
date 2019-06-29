package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.ProgressState;

import java.util.Map;
import java.util.TreeMap;

public class ProgressStateValidatorValues implements ProgressStateValidator{

    Map<String, ProgressStateValueValidator> valueValidatorMap;

    public ProgressStateValidatorValues(){
        valueValidatorMap = new TreeMap<>();
    }

    public void putValueValidator(String key, ProgressStateValueValidator valueValidator){
        valueValidatorMap.put(key, valueValidator);
    }

    @Override
    public String isProgressStateValueValid(String key, String value){
        if(!valueValidatorMap.containsKey(key))
            return null;
        return valueValidatorMap.get(key).isValueValid(value);
    }

    @Override
    public boolean isProgressStateValid(ProgressState progressState){
        for(Map.Entry<String, ProgressStateValueValidator> validatorEntry : valueValidatorMap.entrySet()){
            if(validatorEntry.getValue().isValueValid(progressState.get(validatorEntry.getKey())) != null)
                return false;
        }
        return true;
    }

}
