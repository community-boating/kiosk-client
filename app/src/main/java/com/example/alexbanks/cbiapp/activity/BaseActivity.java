package com.example.alexbanks.cbiapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.admin.CBIDeviceAdmin;
import com.example.alexbanks.cbiapp.progress.Progress;
import com.example.alexbanks.cbiapp.progress.ProgressState;

/* This class contains general methods shared by most activities in this app */
public class BaseActivity extends AppCompatActivity {

    public static final String PROGRESS_EXTRA_KEY = "progress_extra";

    public Progress progress;

    public DevicePolicyManager dpm;
    public ComponentName cbiAdminDeviceSample;

    public static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    public static final int REQUEST_CODE_ENABLE_DPC = 2;

    //TODO temp method
    private void enableAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, this.cbiAdminDeviceSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, this.getString(R.string.cbi_admin_add_admin_extra_text));
        startActivityForResult(intent, BaseActivity.REQUEST_CODE_ENABLE_ADMIN);
    }

    public boolean isDPCEnabled(){
        dpm=(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.d("llll", "aaaa" + getApplicationContext().getPackageName() + ":" + dpm.isDeviceOwnerApp(getApplicationContext().getPackageName()));
        return dpm.isProfileOwnerApp(getApplicationContext().getPackageName());
    }

    private void enableDPC(){
        PackageManager pm = getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_MANAGED_USERS)){
            Log.e("error", "device does not support work profiles");
        }
        Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, this.getApplicationContext().getPackageName());
        if(intent.resolveActivity(this.getPackageManager()) == null){
            Log.e("error", "no intent handler");
        }else{
            Log.d("h", "ffff: what?");
            startActivityForResult(intent, BaseActivity.REQUEST_CODE_ENABLE_DPC);
            this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){
        if(requestCode == BaseActivity.REQUEST_CODE_ENABLE_ADMIN){
            Log.d("h", "aaaa: admin stuff done");
        }
        else if(requestCode == BaseActivity.REQUEST_CODE_ENABLE_DPC){
            if(responseCode == Activity.RESULT_OK){
                Log.i("info", "profile created");
            }else{
                Log.e("error", "profile creation failed");
            }
        }
        else{
            super.onActivityResult(requestCode, responseCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager pm = getPackageManager();
        Log.d("h", "cccc: " + pm.hasSystemFeature(PackageManager.FEATURE_MANAGED_USERS));
        this.dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        Log.d("bbbb", ""  + dpm.isProfileOwnerApp(getApplicationContext().getPackageName()));
        eventConfiguration();
        //hideStatusNavBar();
        this.checkProgress();
        this.cbiAdminDeviceSample = CBIDeviceAdmin.getComponentName(this);
        Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
        enableAdmin();
        if(!isDPCEnabled()) {
            //enableDPC();
            Log.d("h", "xxxx: what now???");
        }else {
            Log.d("h", "xxxx: dpc now done");
        }
        Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bbbb", "a"  + dpm.isProfileOwnerApp(getApplicationContext().getPackageName()));
        /* Re-hide the status/nav bar after being away for a while */
        //Log.d("derp", "dd: " + dpm.isLockTaskPermitted("com.example.alexbanks.cbiapp"));
        dpm.setLockTaskPackages(cbiAdminDeviceSample, new String[]{"com.example.alexbanks.cbiapp"});
        Log.d("bbbb", "aff" + dpm.isDeviceOwnerApp(getApplicationContext().getPackageName()));
        //hideStatusNavBar();
        this.checkProgress();
        if(dpm.isLockTaskPermitted(getApplicationContext().getPackageName())){
            this.startLockTask();
            Log.d("bad", "no issues here");
        }else{
            Log.d("bad", "we have an issue here, no dpm");
        }
        Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
    }

    private static final String MESSAGE_PROGRESS = "cbiapp.progress.current";

    /*
    This one checks to see if this is the right activity for the given progress state
     */
    public boolean nextProgress(){
        if(this.progress.checkProgressStates() == -1){
            this.progress.nextState();
            this.runActivityFromProgress(this.progress);
            return true;
        }
        else{
            Log.d("h", "well this is unfortunate");
            return false;
        }
    }

    public boolean isActivityCorrect(Progress progress){
        return progress.getCurrentProgressState().getActivityClass().equals(this.getClass());
    }

    protected void checkProgress(){
        if(this.loadProgress()){
            if(!this.isActivityCorrect(this.progress)){
                this.runActivityFromProgress(this.progress);
            }
        }else{
            this.runActivityFromProgress(Progress.createNewGuestProgress());
        }
    }

    public boolean loadProgress(){
        Intent i = this.getIntent();
        if(i.hasExtra(BaseActivity.PROGRESS_EXTRA_KEY)){
            this.progress = i.getParcelableExtra(BaseActivity.PROGRESS_EXTRA_KEY);
            return true;
        }else{
            return false;
        }
    }

    public void saveProgress(){
        Intent intent = this.getIntent();
        intent.putExtra(BaseActivity.PROGRESS_EXTRA_KEY, this.progress);
        this.setIntent(intent);
    }

    public void runActivityFromProgress(Progress progress){
        ProgressState currentState = progress.getCurrentProgressState();
        Intent intent;
        if(this.isActivityCorrect(progress)){
            //Currently running activity matches current progress state, so just save and continue
            intent = this.getIntent();
        }
        else {
            //Create new intent for the correct activity
            intent = new Intent(this.getBaseContext(), currentState.getActivityClass());
        }
        intent.putExtra(BaseActivity.PROGRESS_EXTRA_KEY, progress);
        startActivity(intent);
        if(this.isActivityCorrect(progress)){
            //this.finish();
        }
    }

    /* Setup some event callbacks here */
    private void eventConfiguration(){
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideStatusNavBar();
            }
        });
    }

    /* Hides the status and nav bar, must be called whenever UI options change */
    private void hideStatusNavBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.hide();
    }
}
