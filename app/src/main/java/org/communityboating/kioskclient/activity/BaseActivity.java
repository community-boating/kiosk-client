package org.communityboating.kioskclient.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.admin.CBIKioskLauncherActivity;
import org.communityboating.kioskclient.event.events.CBIAPPEvent;
import org.communityboating.kioskclient.event.events.CBIAPPEventManager;
import org.communityboating.kioskclient.event.events.CBIAPPEventType;
import org.communityboating.kioskclient.event.events.printermanager.PrinterManagerPrinterStatusUpdateEvent;
import org.communityboating.kioskclient.event.handler.CBIAPPEventHandler;
import org.communityboating.kioskclient.fragment.NavButtonGroupFragment;
import org.communityboating.kioskclient.input.CustomInputManager;
import org.communityboating.kioskclient.print.PrintServiceHolder;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

import java.util.Timer;
import java.util.TimerTask;

/* This class contains general methods shared by most activities in this app */
public class BaseActivity<ps extends ProgressState> extends FragmentActivity {

    public static final String PROGRESS_EXTRA_KEY = "progress_extra";

    public Progress progress;

    public static final long PAGE_TIMEOUT_DURATION=20000;
    public static final long PAGE_TIMEOUT_DIALOG_DURATION=10000;

    public PrintServiceHolder printService = new PrintServiceHolder();

    private Timer pageTimeout = new Timer();
    private final TimerTask taskTimeoutPrompt = new TimerTask(){

        @Override
        public void run() {
            BaseActivity.this.handleTimeoutPrompt();
        }
    };
    private final TimerTask taskTimeoutExpire = new TimerTask(){

        @Override
        public void run(){
            BaseActivity.this.handleTimeoutExpire();
        }
    };

    //public DevicePolicyManager dpm;
    //public ComponentName cbiAdminDeviceSample;

    //public static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    //public static final int REQUEST_CODE_ENABLE_DPC = 2;

    /*private void enableAdmin(){
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
    }*/

    public void showNotifyInputError(){

    }

    public void handleNavButtonClickNext() {
        this.nextProgress();
    }

    public void handleNavButtonClickHelp(){
        displayFragment(new DialogFragmentHelp());
    }

    public void handleNavButtonClickBack(){
        this.progress.previousState();
        this.runActivityFromProgress(this.progress);
    }

    public void handleNavButtonClickCancel(){
        this.resetNewGuestProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CBIAPPEventManager.initiateIfRequired(this);
        CBIAPPEventManager.addEventHandler(CBIAPPEventType.EVENT_TYPE_PRINTER, new CBIAPPEventHandler() {
            @Override
            public void handleEvent(CBIAPPEvent event) {
                if(event instanceof PrinterManagerPrinterStatusUpdateEvent){
                    PrinterManagerPrinterStatusUpdateEvent updateEvent = (PrinterManagerPrinterStatusUpdateEvent)event;
                    if(updateEvent.getEventStatusType() == PrinterManagerPrinterStatusUpdateEvent.PrinterStatusUpdateEventType.PRINTER_PAPER_EMPTY){

                    }
                }
            }
        });
        //checkLocationPermission();
        printService.createPrintService(this);
        PackageManager pm = getPackageManager();
        //Log.d("h", "cccc: " + pm.hasSystemFeature(PackageManager.FEATURE_MANAGED_USERS));
        //this.dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        //Log.d("bbbb", ""  + dpm.isProfileOwnerApp(getApplicationContext().getPackageName()));
        eventConfiguration();
        //hideStatusNavBar();
        this.checkProgress();
        CustomInputManager.setActiveProgressState(progress.getCurrentProgressState());
        CustomInputManager.clearCustomInputs();
        Log.d("nullupdate", "has nav fragment " + this.hasNavFragment());
        //this.cbiAdminDeviceSample = CBIDeviceAdmin.getComponentName(this);
        //Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
        //enableAdmin();
        //dpm.setStatusBarDisabled(cbiAdminDeviceSample, false);
        //if(!isDPCEnabled()) {
            //enableDPC();
        //    Log.d("h", "xxxx: what now???");
        //}else {
        //    Log.d("h", "xxxx: dpc now done");
        //}
        //Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
    }

    private static final int REQUEST_CODE_LOCATION=10;

    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Location services are not granted");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void displayFragment(Fragment fragment){

        //ViewGroup group = (ViewGroup)getWindow().getDecorView().getRootView();
        if(findViewById(R.id.root_layout) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.root_layout, fragment).commit();
        }
    }

    public boolean canTimeout(){
        return true;
    }

    public void handleTimeoutPrompt(){
        pageTimeout.cancel();
        Runnable runnable = new Runnable(){
            @Override
            public void run(){

            }
        };
        DialogFragmentTimeout fragmentTimeout = new DialogFragmentTimeout();
        displayFragment(fragmentTimeout);
        //TimeoutDialogFragment fragment = new TimeoutDialogFragment();
        //fragment.show(getSupportFragmentManager(), "Simple Fragment");
        pageTimeout = new Timer();
        pageTimeout.schedule(new TimerTask(){

            @Override
            public void run(){
                BaseActivity.this.handleTimeoutExpire();
            }
        }, PAGE_TIMEOUT_DIALOG_DURATION);
    }

    public void lockDevice(){
        this.stopLockTask();
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.lockNow();
    }

    public void handleTimeoutExpire(){
        pageTimeout.cancel();
        resetNewGuestProgress();
    }

    public void resetTimeoutPrompt(){
        pageTimeout.cancel();
        pageTimeout = new Timer();
        pageTimeout.schedule(new TimerTask(){

            @Override
            public void run(){
                BaseActivity.this.handleTimeoutPrompt();
            }
        }, PAGE_TIMEOUT_DURATION);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        boolean ret = super.dispatchTouchEvent(event);
        if(canTimeout())
            resetTimeoutPrompt();
        return ret;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(canTimeout())
            pageTimeout.cancel();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        printService.destroyPrintService(this);
        CBIAPPEventManager.onClose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //displayPopup(new DialogFragmentTimeout());
        //Log.d("bbbb", "a"  + dpm.isProfileOwnerApp(getApplicationContext().getPackageName()));
        /* Re-hide the status/nav bar after being away for a while */
        //Log.d("derp", "dd: " + dpm.isLockTaskPermitted("com.example.alexbanks.cbiapp"));
        //dpm.setLockTaskPackages(cbiAdminDeviceSample, new String[]{"com.example.alexbanks.cbiapp"});
        //Log.d("bbbb", "aff" + dpm.isDeviceOwnerApp(getApplicationContext().getPackageName()));
        hideStatusNavBar();
        this.checkProgress();
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(CBIKioskLauncherActivity.getDPM(this).isLockTaskPermitted(getApplicationContext().getPackageName()) && !manager.isInLockTaskMode()){
            this.startLockTask();
            Log.d("bad", "no issues here");
        }else{
            Log.d("bad", "we have an issue here, no dpm");
        }
        if(this.hasNavFragment())
            this.startNavFragment();
        if(this.canTimeout())
            this.resetTimeoutPrompt();
        //dpm.setStatusBarDisabled(cbiAdminDeviceSample, true);
        //IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        //intentFilter.addCategory(Intent.CATEGORY_HOME);
        //intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        //dpm.addPersistentPreferredActivity(cbiAdminDeviceSample, intentFilter, new ComponentName(getPackageName(), BaseActivity.class.getName()));
        //Log.d("h", "xxxx: " + dpm.isAdminActive(this.cbiAdminDeviceSample));
    }

    private static final String MESSAGE_PROGRESS = "cbiapp.progress.current";

    public boolean nextProgress(){
        if(this.progress.checkPreviousProgressStates() == -1){
            this.progress.nextState();
            this.runActivityFromProgress(this.progress);
            return true;
        }
        else{
            CustomInputManager.updateShowInputErrors(false);
            return false;
        }
    }

    /*
   This one checks to see if this is the right activity for the given progress state
    */

    public boolean isActivityCorrect(Progress progress){
        return progress.getCurrentProgressState().getActivityClass().equals(this.getClass());
    }

    protected void checkProgress(){
        if(this.loadProgress()){
            if(!this.isActivityCorrect(this.progress)){
                this.runActivityFromProgress(this.progress);
            }
        }else{
            resetNewGuestProgress();
        }
    }

    public void resetNewGuestProgress(){
        this.runActivityFromProgress(Progress.createNewGuestProgress());
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
        runActivityFromProgress(progress, this);
    }

    //Run activity from progress context, and optionally a supplied base activity (null if no previous base activity exists)
    public static void runActivityFromProgress(Progress progress, Activity activity){
        ProgressState currentState = progress.getCurrentProgressState();
        Intent intent;
        BaseActivity baseActivity = null;
        if(activity instanceof BaseActivity)baseActivity=(BaseActivity)activity;

        if(baseActivity != null && baseActivity.isActivityCorrect(progress)){
            //Currently running activity matches current progress state, so just save and continue
            intent = baseActivity.getIntent();
        }
        else {
            //Create new intent for the correct activity
            intent = new Intent(activity, currentState.getActivityClass());
        }
        intent.putExtra(BaseActivity.PROGRESS_EXTRA_KEY, progress);
        activity.startActivity(intent);
        if(baseActivity != null && !baseActivity.isActivityCorrect(progress)){
            Log.d("finished", "finished a progress " + (activity.getClass().getName()));
            baseActivity.finish();
            //baseActivity.removeNavbarFragment();
        }
    }

    private boolean hasNavFragment(){
        return findViewById(R.id.layout_nav_fragment) != null;
    }

    private void startNavFragment(){
        NavButtonGroupFragment navFragment = new NavButtonGroupFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_nav_fragment, navFragment).commit();
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
        View v = this.getWindow().getDecorView();
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN |
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void changeFragment(Fragment targetFragment){
        //getSupportFragmentManager().beginTransaction().replace(R.id.layout_newguest_name
    }

    /*private void removeNavbarFragment(){
        Fragment f = this.getSupportFragmentManager().findFragmentByTag("nav_fragment");
        if(f != null){
            Log.d("nullupdate", "f located, now removing");
            this.getSupportFragmentManager().beginTransaction().remove(f).commitNowAllowingStateLoss();
        }else{
            Log.d("nullupdate", "f not located, removal failure (bad)");
        }
    }*/

    public ps getProgressState(){
        Log.d("aaaa", "activityType " + this.getClass().getName());
        return (ps)this.progress.getCurrentProgressState();

    }

    public void handleTimeoutPromptYes(DialogFragmentBase fragment) {
        fragment.removeDialog();
        resetTimeoutPrompt();
    }

    public void handleTimeoutPromptNo(DialogFragmentBase fragment) {
        fragment.removeDialog();
        pageTimeout.cancel();
        resetNewGuestProgress();
    }

}
