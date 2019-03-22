package com.example.alexbanks.cbiapp.activity;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatcherDelegate implements TextWatcher {

    private BeforeTextChanged beforeTextChanged;
    private OnTextChanged onTextChanged;
    private AfterTextChanged afterTextChanged;

    public TextWatcherDelegate(BeforeTextChanged beforeTextChanged, OnTextChanged onTextChanged, AfterTextChanged afterTextChanged){
        this.beforeTextChanged = beforeTextChanged;
        this.onTextChanged = onTextChanged;
        this.afterTextChanged = afterTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(this.beforeTextChanged != null)
            this.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(this.onTextChanged != null)
            this.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(this.afterTextChanged != null)
            this.afterTextChanged(s);
    }

    public interface BeforeTextChanged{
        public void beforeTextChanged(CharSequence s, int start, int count, int after);
    }

    public interface OnTextChanged{
        public void onTextChanged(CharSequence s, int start, int before, int count);
    }

    public interface AfterTextChanged {
        void afterTextChanged(Editable s);
    }

}
