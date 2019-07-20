package org.communityboating.kioskclient.admin;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.AdminGUIActivity;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.Progress;

public class CBIKioskLauncherActivity extends Activity {

    //public DevicePolicyManager dpm;
    //public ComponentName cbiAdminDeviceSample;

    public static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    public static final int REQUEST_CODE_ENABLE_DPC = 2;

    private static final String[] restrictions = {UserManager.DISALLOW_ADD_MANAGED_PROFILE, UserManager.DISALLOW_FACTORY_RESET,
    UserManager.DISALLOW_ADD_USER, UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, UserManager.DISALLOW_ADJUST_VOLUME, UserManager.DISALLOW_AIRPLANE_MODE,};

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.lock_screen_gui_test);
        Log.d("derpaderpderp", "what : " + getDPM().isDeviceOwnerApp("com.amazon.parentalcontrols"));
        if(isDeviceOwner()){
            setKioskPolicies(true);
        }else{
            //Warn that the program will not run properly until provisioning of the device is complete
            Toast toast = Toast.makeText(this, R.string.cbi_admin_provision_warning, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
        //Intent adminIntent = new Intent(this, AdminGUIActivity.class);
        //this.startActivity(adminIntent);
        startKioskActivity();
    }

    @Override
    public void onResume(){
        super.onResume();
        //this.startLockTask();
    }

    public void enableLockScreen(View v){
        //setKioskPolicies(true);
        this.startLockTask();
    }

    public void disableLockScreen(View v){
        //setKioskPolicies(false);
        this.stopLockTask();
    }

    public boolean isDeviceOwner(){
        return getDPM().isDeviceOwnerApp(this.getApplicationContext().getPackageName());
    }

    public static void setKioskPolicies(Context context, boolean enabled){
        DevicePolicyManager dpm = getDPM(context);
        ComponentName admin = CBIDeviceAdmin.getComponentName(context);

        for(String s : restrictions){
            if(enabled)dpm.addUserRestriction(admin, s);
            else dpm.clearUserRestriction(admin, s);
        }

        //dpm.setKeyguardDisabled(admin, enabled);
        //dpm.setStatusBarDisabled(admin, enabled);

        dpm.setLockTaskPackages(admin, enabled?new String[]{context.getPackageName()}:new String[0]);
        //dpm.setLockTaskFeatures(admin, DevicePolicyManager.LOCK_TASK_FEATURE_KEYGUARD);

        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

// Set the activity as the preferred option for the device.
        ComponentName activity = new ComponentName(context, CBIKioskLauncherActivity.class);
        //if(enabled)
        //    dpm.addPersistentPreferredActivity(admin, filter, activity);//else
        //else
        //dpm.clearPackagePersistentPreferredActivities(amazonComponentName, context.getPackageName());
        //    dpm.clearPackagePersistentPreferredActivities(admin, context.getPackageName());

    }

    private void setKioskPolicies(boolean enabled){
        setKioskPolicies(this, enabled);
    }

    private void enableAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, CBIDeviceAdmin.getComponentName(this));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, this.getString(R.string.cbi_admin_add_admin_extra_text));
        startActivityForResult(intent, CBIKioskLauncherActivity.REQUEST_CODE_ENABLE_ADMIN);
    }

    private DevicePolicyManager getDPM(){
        return getDPM(this);
    }

    private void startKioskActivity(){
        Progress progress = Progress.createNewGuestProgress();
        BaseActivity.runActivityFromProgress(progress, this);
        finish();
    }

    public boolean isDPCEnabled(){
        //dpm=(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        //Log.d("llll", "aaaa" + getApplicationContext().getPackageName() + ":" + dpm.isDeviceOwnerApp(getApplicationContext().getPackageName()));
        return getDPM().isProfileOwnerApp(getApplicationContext().getPackageName());
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
            startActivityForResult(intent, CBIKioskLauncherActivity.REQUEST_CODE_ENABLE_DPC);
            this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){
        if(requestCode == CBIKioskLauncherActivity.REQUEST_CODE_ENABLE_ADMIN){
            Log.d("h", "aaaa: admin stuff done");
        }
        else if(requestCode == CBIKioskLauncherActivity.REQUEST_CODE_ENABLE_DPC){
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

    public static DevicePolicyManager getDPM(Context context){
        return (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

}
