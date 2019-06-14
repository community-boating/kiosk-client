package com.example.alexbanks.cbiapp.activity;

import android.view.View;

import com.example.alexbanks.cbiapp.R;

public class DialogFragmentTimeout extends DialogFragmentBase {

    @Override
    public int getLayoutResID(){
        return R.layout.timeout_dialog_fragment;
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.timeout_dialog_fragment_yes){
            getBaseActivity().handleTimeoutPromptYes(this);
        }else if(view.getId() == R.id.timeout_dialog_fragment_no){
            getBaseActivity().handleTimeoutPromptNo(this);
        }
    }

}
