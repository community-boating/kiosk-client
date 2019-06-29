package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.newguest.ProgressStateEmergencyContactName;

public class EmergencyContactNameActivity extends BaseActivity<ProgressStateEmergencyContactName> {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_emergencycontact_name);
        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_SIMPLE, R.id.custom_keyboard_view_ec_name);
        customKeyboard.showCustomKeyboard();
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.setTextViewFocuses();
    }

}
