package org.communityboating.kioskclient.activity;

import android.view.View;

import org.communityboating.kioskclient.R;

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
