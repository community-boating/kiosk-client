package org.communityboating.kioskclient.fragment;



import androidx.fragment.app.Fragment;

import org.communityboating.kioskclient.activity.BaseActivity;

public abstract class BaseActivityFragment extends Fragment {

    public BaseActivity getBaseActivity(){
        if(this.getActivity() instanceof BaseActivity){
            return (BaseActivity)this.getActivity();
        }else{
            return null;
        }
    }

}
