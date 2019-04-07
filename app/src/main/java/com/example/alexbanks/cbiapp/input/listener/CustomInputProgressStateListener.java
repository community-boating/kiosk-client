package com.example.alexbanks.cbiapp.input.listener;

import com.example.alexbanks.cbiapp.input.CustomInputManager;

public class CustomInputProgressStateListener {

    String progressStateVariableName;

    public CustomInputProgressStateListener(String progressStateVariableName){
        this.progressStateVariableName = progressStateVariableName;
    }

    public void updateProgressStateVariableValue(String value){
        if(CustomInputManager.activeProgressState!=null)
            CustomInputManager.activeProgressState.put(progressStateVariableName, value);
        else
            CustomInputManager.activeProgressState.remove(value);
    }

    public String getProgressStateVariableValue(){
        if(CustomInputManager.activeProgressState!=null)
            return CustomInputManager.activeProgressState.get(progressStateVariableName);
        return null;
    }

}
