package com.example.alexbanks.cbiapp.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.input.listener.CustomInputTextWatcherListener;
import com.example.alexbanks.cbiapp.keyboard.CustomKeyboard;
import com.example.alexbanks.cbiapp.progress.ProgressState;

import java.lang.annotation.Retention;

public class EditTextCustomInput extends AppCompatEditText{

    public static final int INPUT_TYPE_INTEGER=0;
    public static final int INPUT_TYPE_FLOAT=1;
    public static final int INPUT_TYPE_DOUBLE=2;
    public static final int INPUT_TYPE_CHARACTER=3;
    public static final int INPUT_TYPE_STRING=4;

    public int inputType;
    public String progressStateVariableName;

    CustomInputTextWatcherListener textWatcherListener;

    public EditTextCustomInput(Context context) {
        super(context);
        textWatcherListener = new CustomInputTextWatcherListener(progressStateVariableName, this);
        this.addTextChangedListener(textWatcherListener);
        CustomInputManager.addCustomInput(textWatcherListener);
        //CustomInputManager.addCustomInput(this);
    }

    public EditTextCustomInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        //CustomInputManager.addCustomInput(this);
        //this.addTextChangedListener(this);
        loadAttributes(context, attrs);
        textWatcherListener = new CustomInputTextWatcherListener(progressStateVariableName, this);
        this.addTextChangedListener(textWatcherListener);
        CustomInputManager.addCustomInput(textWatcherListener);
    }

    public EditTextCustomInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(context, attrs);
        textWatcherListener = new CustomInputTextWatcherListener(progressStateVariableName, this);
        this.addTextChangedListener(textWatcherListener);
        CustomInputManager.addCustomInput(textWatcherListener);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        initiateValueFromProgressState();
        if(this.hasFocus()){
            CustomKeyboard.instance.handleFocusChanged(this);
        }
    }

    /*@Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /*@Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        ProgressState progressState = CustomInputManager.activeProgressState;
        if(value.isEmpty()){
            progressState.remove(progressStateVariableName);
            return;
        }
        switch(inputType){
            case INPUT_TYPE_INTEGER:
                Integer i=Integer.parseInt(value);
                progressState.putInteger(progressStateVariableName, i);
                break;
            case INPUT_TYPE_FLOAT:
                Float f=Float.parseFloat(value);
                progressState.putFloat(progressStateVariableName, f);
                break;
            case INPUT_TYPE_DOUBLE:
                Double d=Double.parseDouble(value);
                progressState.putDouble(progressStateVariableName, d);
                break;
            case INPUT_TYPE_CHARACTER:
                Character c=value.charAt(0);
                progressState.putCharacter(progressStateVariableName, c);
                break;
            case INPUT_TYPE_STRING:
            default:
                progressState.put(progressStateVariableName, value);
        }
    }*/

    private void initiateValueFromProgressState(){
        String value = textWatcherListener.getProgressStateVariableValue();
        this.setText(value);
    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditTextCustomInput, 0, 0);
        inputType = a.getInteger(R.styleable.EditTextCustomInput_variableType, 4);
        progressStateVariableName = a.getString(R.styleable.EditTextCustomInput_progressStateVariable);
    }

}
