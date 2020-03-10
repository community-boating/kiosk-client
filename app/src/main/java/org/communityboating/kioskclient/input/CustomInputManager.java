package org.communityboating.kioskclient.input;

import android.app.Activity;
import android.util.Log;

import org.communityboating.kioskclient.input.listener.CustomInputProgressStateListener;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

import java.util.LinkedList;
import java.util.List;

public abstract class CustomInputManager {

    private static List<CustomInputProgressStateListener> customInputList = new LinkedList<>();

    private static ProgressState activeProgressState = null;
    private static Progress activeProgress = null;

    public static void clearCustomInputs(){
        customInputList.clear();
    }

    protected static void addCustomInput(CustomInputProgressStateListener customInput){
        customInputList.add(customInput);
    }

    public static void setActiveProgressState(ProgressState activeProgressState, Progress progress){
        CustomInputManager.activeProgressState=activeProgressState;
        CustomInputManager.activeProgress=progress;
    }

    public static ProgressState getActiveProgressState(){
        if(activeProgressState == null)
            Log.e("cbiapp error", "active progress was null, this should never happen");
        return activeProgressState;
    }

    public static Progress getActiveProgress(){
        if(activeProgress == null)
            Log.e("cbiapp error", "active progress was null, this should never happen");
        return activeProgress;
    }

    public static List<CustomInputProgressStateListener> getCustomInputs(){
        return customInputList;
    }

    public static void updateShowInputErrors(boolean hidden){
        Log.d("errordatastuff", "derp : " + getCustomInputs().size());
        for(CustomInputProgressStateListener customInput : getCustomInputs()){
            customInput.updateProgressStateValidatorError(hidden);
        }
    }

}
