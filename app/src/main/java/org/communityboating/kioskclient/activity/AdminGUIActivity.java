package org.communityboating.kioskclient.activity;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaMuxer;
import android.media.tv.TvView;
import android.os.Bundle;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.communityboating.kioskclient.BasePackageClass;
import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.admin.CBIDeviceAdmin;
import org.communityboating.kioskclient.admin.CBIKioskLauncherActivity;
import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.keyboard.CustomKeyboardView;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AdminGUIActivity extends Activity implements CustomKeyboard.EnterListener {

    public static final String adminPasswordFileName="apwhat";

    private byte[] adminPassword=null;
    private byte[] passwordSalt = new byte[16];
    private boolean hasPasswordSet = false;
    private boolean hasValidPassword=false;

    EditText passwordInput;

    TextView passwordText;

    TextView cbiAPIKeyText;

    EditText cbiAPIKeyEditText;

    TextView deviceOwnerStatusText;

    TextView deviceOwnerCommandText;

    CustomKeyboard customKeyboard;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        AdminConfigProperties.loadProperties(this);
        this.setContentView(R.layout.layout_admin_gui);
        //this.setContentView(R.layout.layout_admin_gui_main);
        //if(true)return;

        loadAdminPassword();

        passwordInput=findViewById(R.id.admin_gui_password_input);
        passwordText=findViewById(R.id.admin_gui_password_input_text);

        //hasValidPassword=true;
        //setContentView(R.layout.layout_admin_gui_main);
        //getMainComponents();

        if(!hasPasswordSet){
            setPasswordText("No Admin password set, enter one now to complete");
        }else{
            setPasswordText("Enter Admin password");
        }

        customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_FULL, R.id.custom_keyboard_view_admin_gui);
        //customKeyboard.addTextView(emailText);
        //customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.setEnterListener(this);
        customKeyboard.addTextView(passwordInput);
        customKeyboard.showCustomKeyboard();
        customKeyboard.setTextViewFocuses();

    }

    private void getMainComponents() {
        cbiAPIKeyText=findViewById(R.id.admin_gui_cbi_api_key_text);
        cbiAPIKeyEditText=findViewById(R.id.admin_gui_cbi_api_key_edit_text);
        deviceOwnerStatusText=findViewById(R.id.admin_gui_device_owner_status_text);
        deviceOwnerCommandText=findViewById(R.id.admin_gui_device_owner_command_text);
        //cbiAPIKeyUpdateButton = (Button) findViewById(R.id.admin_gui_cbi_api_key_update_button);
    }

    private void initMainComponents(){
        String rootPackage = BasePackageClass.class.getPackage().getName();
        String fullClassName = CBIDeviceAdmin.class.getName();
        String commandText="adb shell dpm set-device-owner " + rootPackage + "/" + fullClassName;
        deviceOwnerCommandText.setText(commandText);
        updateDeviceOwnerStatus();
    }

    private void updateDeviceOwnerStatus(){
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean isDeviceOwner = dpm.isDeviceOwnerApp(this.getPackageName());
        if(isDeviceOwner){
            deviceOwnerStatusText.setText("App is currently device owner");
            deviceOwnerStatusText.setTextColor(Color.GREEN);
        }else{
            deviceOwnerStatusText.setText("App is not device owner");
            deviceOwnerStatusText.setTextColor(Color.RED);
        }
    }

    private void setCBIAPIKeyText(String text){
        if(cbiAPIKeyText.getVisibility()!=View.VISIBLE)
            cbiAPIKeyText.setVisibility(View.VISIBLE);
        cbiAPIKeyText.setText(text);
    }

    public void handleCBIAPIKeySetClick(View v){
        checkAdminReady();
        String key = cbiAPIKeyEditText.getText().toString();
        if(key==null||key.length()<32){
            setCBIAPIKeyText("Key is not long enough (32+)");
        }else{
            AdminConfigProperties.setCBIAPIKey(key);
            AdminConfigProperties.storeProperties(this);
            setCBIAPIKeyText("Set the key");
        }
    }

    private static final String ALLOWED_CHARACTERS="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public void handleCBIAPIKeySetRandomClick(View v){
        checkAdminReady();
        SecureRandom random = new SecureRandom();
        char[] randomKey = new char[32];
        int length=ALLOWED_CHARACTERS.length();
        for(int i = 0; i < randomKey.length; i++){
            randomKey[i]=ALLOWED_CHARACTERS.charAt(random.nextInt(length));
        }
        String key = new String(randomKey);
        cbiAPIKeyEditText.setText(key);
        AdminConfigProperties.setCBIAPIKey(key);
        AdminConfigProperties.storeProperties(this);
        setCBIAPIKeyText("created and saved the random key");
    }

    public void handleCBIAPIKeyGetClick(View v){
        checkAdminReady();
        if(!AdminConfigProperties.hasCBIAPIKey()){
            setCBIAPIKeyText("CBI API Key has not been set yet");
        }else{
            String key = AdminConfigProperties.getCBIAPIKey();
            cbiAPIKeyEditText.setText(key);
            setCBIAPIKeyText("Got the key");
        }
    }

    public void handleExitAppClick(View v){
        checkAdminReady();
        this.stopLockTask();
        CBIKioskLauncherActivity.setKioskPolicies(this.getApplicationContext(), false);
        System.exit(0);
        //System.exit(0);
    }

    private void hidePasswordText(){
        passwordText.setVisibility(View.INVISIBLE);
    }

    private void setPasswordText(String text){
        if(passwordText.getVisibility() != View.VISIBLE)
            passwordText.setVisibility(View.VISIBLE);
        passwordText.setText(text);
    }

    public void checkAdminReady(){
        if(!this.hasValidPassword)
            throw new RuntimeException("Password has not been entered");
    }

    private void loadAdminPassword(){
        try{
            FileInputStream inputStream = openFileInput(adminPasswordFileName);
            int read=0;
            while(read<passwordSalt.length) {
                read+=inputStream.read(passwordSalt, read, passwordSalt.length-read);
            }
            byte[] buffer = new byte[2048];
            read=0;
            while(inputStream.available()>0&&read<buffer.length){
                read+=inputStream.read(buffer, read, buffer.length-read);
            }
            inputStream.close();
            adminPassword=new byte[read];
            System.arraycopy(buffer, 0, adminPassword, 0, read);
            hasPasswordSet=true;

        }catch (FileNotFoundException e){
            //Has no password
            hasPasswordSet=false;
            Log.i("cbi admin", "no password set for admin, this should be done");
        }catch (IOException e){
            e.printStackTrace();
            hasPasswordSet=false;
        }
    }

    private void writeAdminPassword(){
        try{
            FileOutputStream outputStream = openPrivateFile(adminPasswordFileName);
            outputStream.write(passwordSalt);
            outputStream.write(adminPassword);
            outputStream.flush();
            outputStream.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private FileOutputStream openPrivateFile(String fileName) throws FileNotFoundException {
        return openFileOutput(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public boolean handleEnter() {
        if(!this.hasPasswordSet){
            String password = passwordInput.getText().toString();
            if(password==null||password.isEmpty()||password.length()<6){
                setPasswordText("Enter a valid password (6+)");
                return true;
            }
            try{
                setAdminPassword(password);
            }catch(Exception e){
                setPasswordText("Password setting failed");
                e.printStackTrace();
            }
        }else{
            String password = passwordInput.getText().toString();
            if(password==null||password.isEmpty()){
                setPasswordText("Enter a password");
                return true;
            }
            try {
                char[] passwordCharArray=password.toCharArray();
                boolean valid = validatePassword(passwordCharArray, passwordSalt, adminPassword);
                if(valid){
                    customKeyboard.updatePreventSoftwareKeyboard(false);
                    setContentView(R.layout.layout_admin_gui_main);
                    hasValidPassword=true;
                    getMainComponents();
                    initMainComponents();
                    setPasswordText("Password is valid");
                }else{
                    setPasswordText("Password is invalid");
                }
            }catch(Exception e){
                setPasswordText("Password check failure");
                e.printStackTrace();
            }
        }
        return true;
    }

    private void setAdminPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
        passwordSalt=getSalt();
        adminPassword=digestPassword(password.toCharArray(), passwordSalt);
        hasPasswordSet=true;
        writeAdminPassword();
        setPasswordText("Password set");
    }

    public static final String KeyFactoryAlgorithm="PBKDF2WithHmacSHA1";
    public static final int iterations=1000;

    private byte[] digestPassword(char[] password, byte[] salt)throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KeyFactoryAlgorithm);
        return skf.generateSecret(spec).getEncoded();
    }

    private boolean validatePassword(char[] password, byte[] salt, byte[] hashed) throws NoSuchAlgorithmException, InvalidKeySpecException{
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hashed.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KeyFactoryAlgorithm);
        byte[] attemptHashed = skf.generateSecret(spec).getEncoded();
        if(attemptHashed.length!=hashed.length)
            return false;
        for(int i = 0; i < hashed.length; i++){
            if(hashed[i] != attemptHashed[i])
                return false;
        }
        return true;
    }

    private byte[] getSalt(){
        SecureRandom saltRandom = new SecureRandom();
        byte[] bytes = new byte[16];
        saltRandom.nextBytes(bytes);
        return bytes;
    }

    public void onCloseClick(View v){
        finish();
    }

}
