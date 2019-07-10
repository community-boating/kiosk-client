package org.communityboating.kioskclient.activity;

import android.content.Intent;
import android.view.View;

import org.communityboating.kioskclient.R;

public class DialogFragmentAdminTooltip extends DialogFragmentBase {

    @Override
    public int getLayoutResID(){
        return R.layout.admin_tooltop_dialog_fragment;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.admin_tooltip_dialog_fragment_button_lock:
                break;
            case R.id.admin_tooltip_dialog_fragment_button_print:
                break;
            case R.id.admin_tooltip_dialog_fragment_button_settings:
                Intent adminIntent = new Intent(this.getContext(), AdminGUIActivity.class);
                this.getActivity().startActivity(adminIntent);
                break;
        }
    }

}
