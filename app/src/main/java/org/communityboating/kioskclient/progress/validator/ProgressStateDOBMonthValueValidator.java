package org.communityboating.kioskclient.progress.validator;

import android.inputmethodservice.InputMethodService;

public class ProgressStateDOBMonthValueValidator extends ProgressStateValueValidator {
    @Override
    public String isValueValid(String value) {
        Integer monthValue;
        try{
            monthValue=Integer.parseInt(value);
        }catch(Exception e){
            return "Enter a valid month (1-12)";
        }
        if(monthValue > 12 || monthValue < 1)
            return "Months are 1 (Jan) to 12 (Dec)";
        return null;
    }
}
