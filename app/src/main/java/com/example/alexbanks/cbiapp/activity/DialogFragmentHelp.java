package com.example.alexbanks.cbiapp.activity;

import android.view.View;

import com.example.alexbanks.cbiapp.R;

public class DialogFragmentHelp extends DialogFragmentBase{

    @Override
    public int getLayoutResID(){
        return R.layout.help_dialog_fragment;
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.help_dialog_fragment_close){
            removeDialog();
        }
    }

}
