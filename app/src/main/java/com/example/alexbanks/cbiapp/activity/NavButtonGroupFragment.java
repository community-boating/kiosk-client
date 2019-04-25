package com.example.alexbanks.cbiapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.alexbanks.cbiapp.R;

public class NavButtonGroupFragment extends Fragment implements View.OnClickListener {

    private static View view;

    private Button navButtonNext;
    private Button navButtonBack;
    private Button navButtonHelp;
    private Button navButtonCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("nullupdate", "view created, inflation attempt here");
        if(view != null){
            ViewGroup parent = (ViewGroup)view.getParent();
            if(parent != null)
                parent.removeView(view);
        }
        try{
            view = inflater.inflate(R.layout.nav_button_group_layout, container, false);
            navButtonNext = view.findViewById(R.id.nav_button_next);
            navButtonBack = view.findViewById(R.id.nav_button_back);
            navButtonHelp = view.findViewById(R.id.nav_button_help);
            navButtonCancel = view.findViewById(R.id.nav_button_cancel);
            navButtonNext.setOnClickListener(this);
            navButtonBack.setOnClickListener(this);
            navButtonHelp.setOnClickListener(this);
            navButtonCancel.setOnClickListener(this);
        }catch(android.view.InflateException e){
            Log.d("nullupdate", "inflate issues");
        }
        return view;
    }

    /*@Override
    public void onDestroyView(){
        super.onDestroyView();
        NavButtonGroupFragment f = (NavButtonGroupFragment) getFragmentManager().findFragmentById(R.id.layout_newguest_dob);
        if(f == null)
            f = (NavButtonGroupFragment) getFragmentManager().findFragmentById(R.id.layout_newguest_name);
        if(f != null) {
            getFragmentManager().beginTransaction().remove(f).commitNowAllowingStateLoss();
            Log.d("nullupdate", "it was not null, ok");
        }else {
            Log.d("nullupdate", "it was null, not good");
        }
    }*/

    @Override
    public void onClick(View v) {
        BaseActivity baseActivity = getBaseActivity();
        if(baseActivity != null) {
            switch (v.getId()) {
                case R.id.nav_button_next:
                    baseActivity.handleNavButtonClickNext();
                    break;
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
