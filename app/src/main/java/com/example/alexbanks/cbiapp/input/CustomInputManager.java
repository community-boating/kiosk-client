package com.example.alexbanks.cbiapp.input;

import android.app.Activity;

import com.example.alexbanks.cbiapp.progress.ProgressState;

import java.util.LinkedList;
import java.util.List;

public abstract class CustomInputManager {

    private static List<EditTextCustomInput> customInputList = new LinkedList<>();

    public static ProgressState activeProgressState = null;

    public static void clearCustomInputs(){
        customInputList.clear();
    }

    protected static void addCustomInput(EditTextCustomInput customInput){
        customInputList.add(customInput);
    }

    public static List<EditTextCustomInput> getCustomInputs(){
        return customInputList;
    }

}
