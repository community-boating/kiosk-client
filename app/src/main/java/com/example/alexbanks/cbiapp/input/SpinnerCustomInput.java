package com.example.alexbanks.cbiapp.input;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.input.listener.CustomInputOnItemSelectedListener;
import com.example.alexbanks.cbiapp.progress.validator.ProgressStateValidator;

import java.util.List;

public class SpinnerCustomInput<T> extends AppCompatSpinner {

    public String progressStateVariableName;

    CustomInputOnItemSelectedListener itemSelectedListener;

    List<T> values;

    public SpinnerCustomInput(Context context) {
        super(context);
    }

    public SpinnerCustomInput(Context context, int mode) {
        super(context, mode);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
        itemSelectedListener = new CustomInputOnItemSelectedListener(progressStateVariableName, this);
        this.setOnItemSelectedListener(itemSelectedListener);
        CustomInputManager.addCustomInput(itemSelectedListener);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(context, attrs);
        itemSelectedListener = new CustomInputOnItemSelectedListener(progressStateVariableName, this);
        this.setOnItemSelectedListener(itemSelectedListener);
        CustomInputManager.addCustomInput(itemSelectedListener);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        loadAttributes(context, attrs);
        itemSelectedListener = new CustomInputOnItemSelectedListener(progressStateVariableName, this);
        this.setOnItemSelectedListener(itemSelectedListener);
        CustomInputManager.addCustomInput(itemSelectedListener);
    }

    public void setAdapterFromList(List<T> array){
        this.values = array;
        ArrayAdapter adapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(adapter);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        initiateValuesFromProgressState();
    }

    public String getProgressStateValue(int pos){
        return this.values.get(pos).toString();
    }

    private void initiateValuesFromProgressState(){
        String value = itemSelectedListener.getProgressStateVariableValue();
        if(value==null)
            return;
        int pos = -1;
        for(int i = 0; i < values.size(); i++){
            if(values.get(i).toString().equals(value)){
                pos=i;
                break;
            }
        }
        Log.d("derpderp", "something else : " + pos);
        if(pos==-1)
            this.setSelected(false);
        else
            this.setSelection(pos, false);
    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpinnerCustomInput, 0, 0);

        //inputType = a.getInteger(R.styleable.EditTextCustomInput_variableType, 4);
        progressStateVariableName = a.getString(R.styleable.SpinnerCustomInput_progressStateVariable);
    }

}