package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;

public class NewGuestEmailActivity extends BaseActivity<ProgressStateNewGuestEmail> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_email);
    }

}
