package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestWaiverActivity;
import org.communityboating.kioskclient.payment.RentalBoatSpecificOptions;
import org.communityboating.kioskclient.payment.RentalBoatTypeOptions;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateRentalBoatTypeChoose extends ProgressState {
    public static final String KEY_RENTAL_CHOOSE="rental_choose";

    static {
        ProgressStateValidatorManager.addValueValidator(ProgressStateNewGuestWaiver.class, KEY_RENTAL_CHOOSE, new ProgressStateNotBlankValueValidator("Choose option"));
    }

    public ProgressStateRentalBoatTypeChoose(){
    }

    public RentalBoatTypeOptions getChosenRentalOption(){
        return RentalBoatTypeOptions.valueOf(get(KEY_RENTAL_CHOOSE));
    }

    public void setChosenRentalOption(RentalBoatTypeOptions option){
        put(KEY_RENTAL_CHOOSE, option.name());
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestWaiverActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateRentalBoatSpecificChoose();//new ProgressStateNewGuestSignature();
    }
}
