package com.example.alexbanks.cbiapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alexbanks.cbiapp.R;

public class NavButtonGroupFragment extends Fragment implements View.OnClickListener {

    private Button navButtonBack;
    private Button navButtonHelp;
    private Button navButtonCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.nav_button_group_layout, container, false);
        navButtonBack = view.findViewById(R.id.nav_button_back);
        navButtonHelp = view.findViewById(R.id.nav_button_help);
        navButtonCancel = view.findViewById(R.id.nav_button_cancel);
        navButtonBack.setOnClickListener(this);
        navButtonHelp.setOnClickListener(this);
        navButtonCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        BaseActivity baseActivity = getBaseActivity();
        if(baseActivity != null) {
            switch (v.getId()) {
                case R.id.nav_button_back:
                    baseActivity.handleNavButtonClickBack();
                    break;
                case R.id.nav_button_help:
                    baseActivity.handleNavButtonClickHelp();
                    break;
                case R.id.nav_button_cancel:
                    baseActivity.handleNavButtonClickCancel();
                    break;
            }
        }
    }

    public BaseActivity getBaseActivity(){
        if(this.getActivity() instanceof BaseActivity){
            return (BaseActivity)this.getActivity();
        }else{
            return null;
        }
    }

}
