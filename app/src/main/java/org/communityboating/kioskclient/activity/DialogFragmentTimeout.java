package org.communityboating.kioskclient.activity;

import android.view.View;

import org.communityboating.kioskclient.R;

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
