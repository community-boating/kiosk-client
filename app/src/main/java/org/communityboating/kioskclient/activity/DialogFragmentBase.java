package org.communityboating.kioskclient.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import org.communityboating.kioskclient.R;

public abstract class DialogFragmentBase extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        //Pad the base view with a RelativeLayout for centering
        RelativeLayout layout = new RelativeLayout(inflater.getContext());
        layout.setZ(1000);
        layout.setClickable(true);
        layout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        View dialogView = inflater.inflate(getLayoutResID(), layout, false);
        handleDialogViewCreation(dialogView);
        //Automatically set all buttons to use this as their click listeners
        for(View touchable : dialogView.getTouchables()){
            if(touchable instanceof Button)
                touchable.setOnClickListener(this);
        }
        layout.addView(dialogView);
        return layout;
    }

    public void handleDialogViewCreation(View dialogView){
    }

    public int getLayoutResID(){
        return R.layout.popuplayout;
    }

    public BaseActivity getBaseActivity(){
        if(getActivity() instanceof BaseActivity)
            return (BaseActivity)getActivity();
        return null;
    }

    public void removeDialog(){
        this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() != null && v.getTag().equals(getContext().getString(R.string.dialog_fragment_button_close_tag))){
            this.removeDialog();
        }
    }
}
