package com.example.alexbanks.cbiapp.input;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.alexbanks.cbiapp.R;

public class SpinnerCustomInput extends AppCompatSpinner implements AdapterView.OnItemSelectedListener {

    public String progressStateVariableName;

    public SpinnerCustomInput(Context context) {
        super(context);
    }

    public SpinnerCustomInput(Context context, int mode) {
        super(context, mode);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinnerCustomInput(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditTextCustomInput, 0, 0);
        //inputType = a.getInteger(R.styleable.EditTextCustomInput_variableType, 4);
        //progressStateVariableName = a.getString(R.styleable.EditTextCustomInput_progressStateVariable);
    }

}