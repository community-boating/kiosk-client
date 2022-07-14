package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.util.Log;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.input.CustomInputManager;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalGuestCount;

public class RentalGuestCountActivity extends BaseActivity<ProgressStateRentalGuestCount> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rental_guest_count);

        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_phone);
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.showCustomKeyboard();
        customKeyboard.setTextViewFocuses();
        Log.d("derpderp", "gerperer " + CustomInputManager.getActiveProgressState());
    }
}
