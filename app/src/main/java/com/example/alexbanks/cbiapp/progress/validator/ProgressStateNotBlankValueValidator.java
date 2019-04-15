package com.example.alexbanks.cbiapp.progress.validator;

public class ProgressStateNotBlankValueValidator implements ProgressStateValueValidator {

    private String invalidText;

    public ProgressStateNotBlankValueValidator(String invalidText){
        this.invalidText = invalidText;
    }

    @Override
    public String isValueValid(String value) {
        return (value == null || value.isEmpty() ? invalidText : null);
    }
}
