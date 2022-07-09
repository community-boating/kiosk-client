package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.ProgressState;

import java.util.LinkedList;
import java.util.List;

public class ProgressStateValidatorMultiple implements ProgressStateValidator {

    List<ProgressStateValidator> Validators;

    public ProgressStateValidatorMultiple(ProgressStateValidator... validators){
        Validators = new LinkedList<>();
        for(ProgressStateValidator validator : validators){
            Validators.add(validator);
        }
    }

    public List<ProgressStateValidator> getValidators(){
        return Validators;
    }

    @Override
    public String isProgressStateValueValid(String key, String value) {
        for(ProgressStateValidator validator : Validators){
            String isProgressStateValueValid = validator.isProgressStateValueValid(key, value);
            if(isProgressStateValueValid != null)
                return isProgressStateValueValid;
        }
        return null;
    }

    @Override
    public boolean isProgressStateValid(ProgressState progressState){
        for(ProgressStateValidator validator : Validators){
            if(!validator.isProgressStateValid(progressState))
                return false;
        }
        return true;
    }

}
