package org.communityboating.kioskclient.activity;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaMuxer;
import android.media.tv.TvView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.EventLog;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.IConnectionCallback;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.ModelCapability;

import org.communityboating.kioskclient.BasePackageClass;
import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.admin.CBIDeviceAdmin;
import org.communityboating.kioskclient.admin.CBIKioskLauncherActivity;
import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.event.events.CBIAPPEventManager;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.keyboard.CustomKeyboardView;
import org.communityboating.kioskclient.print.PrintServiceHolder;
import org.communityboating.kioskclient.print.PrinterManager;
import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AdminGUIActivity extends Activity implements CustomKeyboard.EnterListener, View.OnClickListener {

    public static final String adminPasswordFileName="apwhat";

    private byte[] adminPassword=null;
    private byte[] passwordSalt = new byte[16];
    private boolean hasPasswordSet = false;
    private boolean hasValidPassword=true;//false;

    PrintServiceHolder printService = new PrintServiceHolder();

    EditText passwordInput;

    TextView passwordText;

    CustomKeyboard customKeyboard;

    RecyclerView propertiesList;

    AdminGUIPropertiesListAdapter propertiesListAdapter;

    TextView propertyNameText;

    TextView propertyValueText;

    EditText propertyInputEditText;

    Button setPropertyButton;

    Button getPropertyButton;

    Button defaultPropertyButton;

    Button clearPropertyButton;

    RecyclerView eventList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CBIAPPEventManager.initiateIfRequired(this);
        AdminConfigProperties.loadProperties(this);
        this.setContentView(R.layout.layout_admin_gui);
        printService.createPrintService(this);
        //this.setContentView(R.layout.layout_admin_gui_main);
        //if(true)return;

        loadAdminPassword();

        passwordInput=findViewById(R.id.admin_gui_password_input);
        passwordText=findViewById(R.id.admin_gui_password_input_text);

        hasValidPassword=true;
        setContentView(R.layout.layout_admin_gui_main);
        getMainComponents();
        initMainComponents();

        if(true)
            return;

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

        customKeyboard.updatePreventSoftwareKeyboard(true);
    }

    int presses=0;
    int responses=0;

    public void handlePrintClick(View v){
        presses++;
        ICommandBuilder builder = StarIoExt.createCommandBuilder(ModelCapability.getEmulation(ModelCapability.SM_S230I));
        builder.beginDocument();
        builder.append("Test print here".getBytes());
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        try {
            printService.getPrinterService().sendCommands(builder, new PrinterManager.SendCommandsCallback() {
                @Override
                public void handleSuccess() {
                    Log.d("printer", "done printing");
                    responses++;
                    Log.d("printer", "presses : " + presses + " responses : " + responses);
                }

                @Override
                public void handleError(StarIOPortException e) {
                    Log.d("printer", "printer error : " + e.getMessage());
                    e.printStackTrace();
                    responses++;
                    Log.d("printer", "presses : " + presses + " responses : " + responses);
                }

            });
        }catch(Throwable t){
            t.printStackTrace();
        }
        Log.d("printer", "already done with the touch event");
    }

    @Override
    public void onClick(View v) {
        if(selectedProperty == null)
            return;
        switch(v.getId()){
            case R.id.admin_gui_properties_editor_button_get:
                updatePropertyGUI(true);
                break;
            case R.id.admin_gui_properties_editor_button_set:
                String currentValue = propertyInputEditText.getText().toString();
                if(currentValue==null)
                    currentValue = new String();
                AdminConfigProperties.setAdminConfigPropertyValue(selectedProperty, currentValue);
                AdminConfigProperties.storeProperties(this);
                updatePropertyGUI(false);
                propertiesListAdapter.notifyDataSetChanged();
                break;
            case R.id.admin_gui_properties_editor_button_default:
                String defaultValue = AdminConfigProperties.getDefaultAdminConfigPropertyValue(selectedProperty);
                AdminConfigProperties.setAdminConfigPropertyValue(selectedProperty, defaultValue);
                AdminConfigProperties.storeProperties(this);
                updatePropertyGUI(false);
                propertiesListAdapter.notifyDataSetChanged();
                break;
            case R.id.admin_gui_properties_editor_button_clear:
                propertyInputEditText.getText().clear();
                break;
        }
    }

    public static class AdminGUIPropertiesListViewHolder extends RecyclerView.ViewHolder{

        TextView propertyNameTextView;
        TextView propertyValueTextView;

        public AdminGUIPropertiesListViewHolder(View view) {
            super(view);
            propertyNameTextView=view.findViewById(R.id.admin_gui_property_name);
            propertyValueTextView=view.findViewById(R.id.admin_gui_property_value);
        }
    }

    public static final int COLOR_SELECTED = Color.GRAY;
    public static final int COLOR_DEFAULT = Color.WHITE;

    private String selectedProperty;

    public void handleSelectedPropertyChanged(String selectedProperty){
        this.selectedProperty=selectedProperty;
        updatePropertyGUI(true);
    }

    public void updatePropertyGUI(boolean updateEditText){
        if(selectedProperty==null){
            propertyNameText.setText("Select a Property");
            propertyValueText.setText(new String());
        }else {
            propertyNameText.setText("Property : " + selectedProperty);
            String value = AdminConfigProperties.get(selectedProperty);
            int inputType = AdminConfigProperties.getInputType(selectedProperty);
            propertyValueText.setText("Value : " + value);
            if(updateEditText) {
                propertyInputEditText.setInputType(inputType);
                propertyInputEditText.setText(value);
            }
        }
    }

    public class AdminGUIPropertiesListAdapter extends RecyclerView.Adapter<AdminGUIPropertiesListViewHolder>{

        Integer selectedProperty;
        Integer previousProperty;
        View previousPropertyView;

        public View.OnClickListener adminGUIPropertiesClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Integer selection = (Integer) v.getTag();
                if(previousProperty==selection){
                    selectedProperty=null;
                    previousProperty=null;
                    previousPropertyView=null;
                    v.setBackgroundColor(COLOR_DEFAULT);
                }
                else {
                    if (previousPropertyView != null) {
                        Integer curPrevViewSelection = (Integer) previousPropertyView.getTag();
                        if (curPrevViewSelection == previousProperty) {
                            previousPropertyView.setBackgroundColor(COLOR_DEFAULT);
                        }
                    }
                    previousProperty=selection;
                    previousPropertyView = v;
                    v.setBackgroundColor(COLOR_SELECTED);
                    selectedProperty = selection;
                }
                AdminGUIActivity.this.handleSelectedPropertyChanged(selectedProperty==null?null:propertiesList.get(selectedProperty));
            }
        };

        List<String> propertiesList;

        AdminGUIPropertiesListAdapter(List<String> propertiesList){
            this.propertiesList = propertiesList;
        }

        @Override
        public AdminGUIPropertiesListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_admin_gui_main_property, viewGroup, false);
            view.setOnClickListener(adminGUIPropertiesClickListener);
            return new AdminGUIPropertiesListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AdminGUIPropertiesListViewHolder holder, int i) {
            //holder.view.setText("this now : " + i);
            String propertyName = propertiesList.get(i);
            holder.propertyNameTextView.setText(propertyName);
            String propertyValue = AdminConfigProperties.get(propertyName);
            holder.propertyValueTextView.setText(propertyValue);
            holder.itemView.setTag(i);
            if(selectedProperty != null && i==selectedProperty){
                holder.itemView.setBackgroundColor(COLOR_SELECTED);
                previousProperty=i;
                previousPropertyView=holder.itemView;
            }else{
                holder.itemView.setBackgroundColor(COLOR_DEFAULT);
            }
        }

        @Override
        public int getItemCount() {
            return propertiesList.size();
        }
    }

    private void getMainComponents() {
        propertiesList=findViewById(R.id.admin_gui_properties_list);
        propertyNameText=findViewById(R.id.admin_gui_properties_editor_name);
        propertyValueText=findViewById(R.id.admin_gui_properties_editor_value);
        propertyInputEditText=findViewById(R.id.admin_gui_properties_editor_input);
        setPropertyButton=findViewById(R.id.admin_gui_properties_editor_button_set);
        getPropertyButton=findViewById(R.id.admin_gui_properties_editor_button_get);
        defaultPropertyButton=findViewById(R.id.admin_gui_properties_editor_button_default);
        clearPropertyButton=findViewById(R.id.admin_gui_properties_editor_button_clear);
        eventList=findViewById(R.id.admin_gui_event_list);
        //cbiAPIKeyUpdateButton = (Button) findViewById(R.id.admin_gui_cbi_api_key_update_button);
    }

    class ListThing extends RecyclerView.Adapter<EventViewHolder> {

        int size;

        ListThing(int size){
            this.size=size;
        }

        private final View.OnClickListener eventViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mainEventView=v.findViewById(R.id.admin_gui_event_message);
                mainEventView.setVisibility(View.VISIBLE);
                mainEventView.setText("HI IM AN EVENT MESSAGE");
            }
        };

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_admin_gui_main_event, viewGroup, false);
            view.setOnClickListener(eventViewClickListener);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventViewHolder viewHolder, int i) {
            viewHolder.eventType.setText("Hello : " + i);
            Log.d("ploob ooble", "plap blap : " + i);
            viewHolder.eventMessage.setVisibility(View.GONE);
            if(i%2==0)
                viewHolder.itemView.setBackgroundColor(Color.RED);
            else
                viewHolder.itemView.setBackgroundColor(Color.BLUE);
        }

        @Override
        public int getItemCount() {
            return size;
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        TextView eventType;
        TextView eventTimeStamp;
        TextView eventTitle;
        TextView eventMessage;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventType=itemView.findViewById(R.id.admin_gui_event_type);
            eventTimeStamp=itemView.findViewById(R.id.admin_gui_event_timestamp);
            eventTitle=itemView.findViewById(R.id.admin_gui_event_title);
            eventMessage=itemView.findViewById(R.id.admin_gui_event_message);
        }
    }

    ListThing adaper;

    private void initMainComponents(){
        String rootPackage = BasePackageClass.class.getPackage().getName();
        String fullClassName = CBIDeviceAdmin.class.getName();
        String commandText="adb shell dpm set-device-owner " + rootPackage + "/" + fullClassName;
        updateDeviceOwnerStatus();
        AdminConfigProperties.loadPropertiesIfRequired(this);
        Log.d("adapter set", "adapter set");
        propertiesList.setHasFixedSize(true);
        propertiesList.setLayoutManager(new LinearLayoutManager(this));
        List<String> propertiesKeyList = new LinkedList<>(AdminConfigProperties.getDefaultProperties().keySet());
        Collections.sort(propertiesKeyList);
        propertiesListAdapter = new AdminGUIPropertiesListAdapter(propertiesKeyList);
        propertiesList.setAdapter(propertiesListAdapter);
        setPropertyButton.setOnClickListener(this);
        getPropertyButton.setOnClickListener(this);
        defaultPropertyButton.setOnClickListener(this);
        clearPropertyButton.setOnClickListener(this);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this));
        adaper = new ListThing(1000);
        eventList.setAdapter(adaper);
    }

    public void handleClickUp(View v){
        adaper.size += 10;
        adaper.notifyDataSetChanged();
        eventList.scrollToPosition(adaper.getItemCount() - 1);
    }

    private void updateDeviceOwnerStatus(){
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean isDeviceOwner = dpm.isDeviceOwnerApp(this.getPackageName());
        if(isDeviceOwner){
        //    deviceOwnerStatusText.setText("App is currently device owner");
        //    deviceOwnerStatusText.setTextColor(Color.GREEN);
        }else{
        //    deviceOwnerStatusText.setText("App is not device owner");
        //    deviceOwnerStatusText.setTextColor(Color.RED);
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
        /*String key = new String(randomKey);
        cbiAPIKeyEditText.setText(key);
        AdminConfigProperties.setCBIAPIKey(key);
        AdminConfigProperties.storeProperties(this);
        setCBIAPIKeyText("created and saved the random key");*/
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
