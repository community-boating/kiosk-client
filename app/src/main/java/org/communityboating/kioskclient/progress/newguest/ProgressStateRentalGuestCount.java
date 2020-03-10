package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.BoatSpecificRentalOptionActivity;
import org.communityboating.kioskclient.activity.newguest.RentalGuestCountActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateGuestNumberValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;
import org.communityboating.kioskclient.progress.validator.ProgressStateValueValidator;

public class ProgressStateRentalGuestCount extends ProgressState {

    public static final String KEY_RENTAL_GUEST_COUNT="rental.guest.count";

    static{
        ProgressStateValidatorManager.addValueValidator(ProgressStateRentalGuestCount.class, KEY_RENTAL_GUEST_COUNT, new ProgressStateGuestNumberValueValidator());
    }

    public ProgressStateRentalGuestCount(){

    }

    public int getRentalGuestCount(){
        return getInteger(KEY_RENTAL_GUEST_COUNT);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return RentalGuestCountActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestName();//new ProgressStateNewGuestSignature();
    }


}
