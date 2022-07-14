package org.communityboating.kioskclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.communityboating.kioskclient.view.ProgressIndicatorView;

public class ProgressIndicatorFragment extends BaseActivityFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ProgressIndicatorView view = new ProgressIndicatorView(getContext());
        view.setOnClickListener(view);
        Log.d("derpderp", "not attached");
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        ProgressIndicatorView view = (ProgressIndicatorView)getView();
        float previousProgress = getBaseActivity().progress.getPreviousCompletionPercentage();
        float currentProgress = getBaseActivity().progress.computeCompletionPercentage();
        Log.d("derpderp", "derpa : " + previousProgress + " : " + currentProgress);
        view.animateTo(previousProgress, currentProgress);
        Log.d("derpderp", "derpa : " + getBaseActivity().progress.getPreviousCompletionPercentage());
    }

}
