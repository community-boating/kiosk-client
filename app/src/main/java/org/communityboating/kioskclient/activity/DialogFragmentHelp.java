package org.communityboating.kioskclient.activity;

import android.view.View;

import org.communityboating.kioskclient.R;

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
