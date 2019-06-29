package org.communityboating.kioskclient.input.listener;

import org.communityboating.kioskclient.input.CustomInputManager;

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
        if(CustomInputManager.getActiveProgressState() == null)
            return null;
        return CustomInputManager.getActiveProgressState().get(progressStateVariableName);
    }

    public abstract void updateProgressStateValidatorError(boolean hidden);

}
