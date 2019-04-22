package com.example.alexbanks.cbiapp.progress.validator;


public class ProgressStateDOBYearValueValidator implements ProgressStateValueValidator {

    @Override
    public String isValueValid(String value) {
        Integer yearValue;
        try{
            yearValue = Integer.parseInt(value);
        }catch(Exception e){
            return "Enter a valid year";
        }
        if(yearValue >= 2000)
            return "You must be over 18";
        if(yearValue < 1900)
            return "Enter year as 19xx";
        return null;
    }
}
