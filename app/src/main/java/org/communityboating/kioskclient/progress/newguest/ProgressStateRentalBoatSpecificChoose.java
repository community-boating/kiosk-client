package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;
import android.util.Log;

import org.communityboating.kioskclient.activity.newguest.BoatSpecificRentalOptionActivity;
import org.communityboating.kioskclient.activity.newguest.NewGuestNameActivity;
import org.communityboating.kioskclient.activity.newguest.NewGuestWaiverActivity;
import org.communityboating.kioskclient.payment.RentalBoatSpecificOptions;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateRentalBoatSpecificChoose extends ProgressState {
    public static final String KEY_RENTAL_CHOOSE_SPECIFIC="rental_choose_specific";

    static {
        ProgressStateValidatorManager.addValueValidator(ProgressStateRentalBoatSpecificChoose.class, KEY_RENTAL_CHOOSE_SPECIFIC, new ProgressStateNotBlankValueValidator("Choose option a"));
    }

    public ProgressStateRentalBoatSpecificChoose(){
    }

    public RentalBoatSpecificOptions getChosenRentalOption(){
        return RentalBoatSpecificOptions.valueOf(get(KEY_RENTAL_CHOOSE_SPECIFIC));
    }

    public void setChosenRentalOption(RentalBoatSpecificOptions option){
        put(KEY_RENTAL_CHOOSE_SPECIFIC, option.name());
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return BoatSpecificRentalOptionActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateRentalGuestCount();//new ProgressStateNewGuestSignature();
    }
}
