package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.fragment.RentalOptionFragment;
import org.communityboating.kioskclient.payment.RentalBoatSpecificOptions;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatSpecificChoose;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatTypeChoose;

import java.util.List;

public class BoatSpecificRentalOptionActivity extends BaseActivity<ProgressStateRentalBoatSpecificChoose> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rental_boat_type);
        ProgressStateRentalBoatTypeChoose rentalBoatType = this.progress.findByProgressStateType(ProgressStateRentalBoatTypeChoose.class);
        RentalBoatTypeOptions typeOption = rentalBoatType.getChosenRentalOption();
        View rootLayout = findViewById(R.id.root_layout);
        List<RentalBoatSpecificOptions> specificOptions = RentalBoatSpecificOptions.ofType(typeOption);
        RentalOptionFragment fragmentOne = RentalOptionFragment.createFragment(specificOptions.get(0));
        RentalOptionFragment fragmentTwo = RentalOptionFragment.createFragment(specificOptions.get(1));
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.rental_options_holder, fragmentOne).commit();
        manager.beginTransaction().add(R.id.rental_options_holder, fragmentTwo).commit();
    }
    public void handleOptionClick(RentalBoatSpecificOptions optionClicked){
        ProgressStateRentalBoatSpecificChoose progressState = getProgressState();
        progressState.setChosenRentalOption(optionClicked);
        nextProgress();
    }
}
