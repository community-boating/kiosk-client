package org.communityboating.kioskclient.progress.validator;

import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatSpecificChoose;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalGuestCount;

public class ProgressStateGuestNumberValueValidator extends ProgressStateValueValidatorProgressContext {
    @Override
    public String isValueValid(String value, ProgressState progressState, Progress progress) {
        int enteredGuestNumber = Integer.parseInt(value);
        ProgressStateRentalBoatSpecificChoose specificChoose = progress.findByProgressStateType(ProgressStateRentalBoatSpecificChoose.class);
        int boatMinimumGuest = specificChoose.getChosenRentalOption().getMinNumberOfGuests();
        int boatMaximumGuest = specificChoose.getChosenRentalOption().getMaxNumberOfGuests();
        if(enteredGuestNumber > boatMaximumGuest)
            return "Boat can only hold " + boatMaximumGuest + " guests";
        if(enteredGuestNumber < boatMinimumGuest)
            return "Enter at least " + boatMinimumGuest + " guests";
        return null;
    }
}
