package com.example.alexbanks.cbiapp.progress.validator;

import com.example.alexbanks.cbiapp.progress.ProgressState;

public interface ProgressStateValidator {

    public String isProgressStateValueValid(String key, String value);
    public boolean isProgressStateValid(ProgressState progressState);

}
