package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestRegistrationType;

public class NewGuestRegistrationTypeActivity extends BaseActivity<ProgressStateNewGuestRegistrationType> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_registration_type);
    }
}
