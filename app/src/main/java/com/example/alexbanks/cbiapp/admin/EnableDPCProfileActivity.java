package com.example.alexbanks.cbiapp.admin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.activity.newguest.NewGuestBeginActivity;
import com.example.alexbanks.cbiapp.progress.Progress;

public class EnableDPCProfileActivity extends Activity implements View.OnClickListener{

    private Button button;

    @Override
    public void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);

        setContentView(R.layout.layout_dpc_add);

        this.button=findViewById(R.id.dpc_add_button);
        this.button.setOnClickListener(this);

        enableProfile();
    }

    private void enableProfile(){
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = CBIDeviceAdmin.getComponentName(this);
        dpm.setProfileName(componentName, getString(R.string.cbi_kiosk_profile_name));
        dpm.setProfileEnabled(componentName);
        Log.d("h", "ffff: profile enabled");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dpc_add_button:
            this.runProgram();
            break;
        }
    }

    private void runProgram(){
        Progress progress = Progress.createNewGuestProgress();
        //TODO maybe make single entry point to progress
        Intent intent = new Intent(this, NewGuestBeginActivity.class);
        intent.putExtra(BaseActivity.PROGRESS_EXTRA_KEY, progress);
        startActivity(intent);
        finish();
    }

}
