package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.fragment.RentalOptionFragment;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatTypeChoose;

public class BoatTypeRentalOptionActivity extends BaseActivity<ProgressStateRentalBoatTypeChoose> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rental_boat_type);
        View rootLayout = findViewById(R.id.root_layout);
        RentalOptionFragment fragmentPaddle = RentalOptionFragment.createFragment(RentalBoatTypeOptions.PADDLE);
        RentalOptionFragment fragmentSail = RentalOptionFragment.createFragment(RentalBoatTypeOptions.SAIL);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.rental_options_holder, fragmentPaddle).commit();
        manager.beginTransaction().add(R.id.rental_options_holder, fragmentSail).commit();
    }
    public void handleOptionClick(RentalBoatTypeOptions optionClicked){
        ProgressStateRentalBoatTypeChoose progressState = getProgressState();
        progressState.setChosenRentalOption(optionClicked);
        nextProgress();
    }
}
