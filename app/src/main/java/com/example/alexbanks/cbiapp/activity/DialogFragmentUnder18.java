package com.example.alexbanks.cbiapp.activity;

import android.view.View;

import com.example.alexbanks.cbiapp.R;

public class DialogFragmentUnder18 extends DialogFragmentBase {

    @Override
    public int getLayoutResID(){
        return R.layout.under_18_dialog_fragment;
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.under_18_dialog_fragment_ok){
            removeDialog();
        }
    }

}
