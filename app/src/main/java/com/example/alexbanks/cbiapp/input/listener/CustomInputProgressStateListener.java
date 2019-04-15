package com.example.alexbanks.cbiapp.input.listener;

import com.example.alexbanks.cbiapp.input.CustomInputManager;

public abstract class CustomInputProgressStateListener {

    String progressStateVariableName;

    public CustomInputProgressStateListener(String progressStateVariableName){
        this.progressStateVariableName = progressStateVariableName;
    }

    public void updateProgressStateVariableValue(String value){
        if(value!=null)
            CustomInputManager.getActiveProgressState().put(progressStateVariableName, value);
        else
            CustomInputManager.getActiveProgressState().remove(value);
    }

    public String getProgressStateVariableValue(){
        return CustomInputManager.getActiveProgressState().get(progressStateVariableName);
    }

    public abstract void updateProgressStateValidatorError(boolean hidden);

}
