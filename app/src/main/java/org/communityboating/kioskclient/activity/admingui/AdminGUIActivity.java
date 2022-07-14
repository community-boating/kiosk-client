package org.communityboating.kioskclient.activity.admingui;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.ModelCapability;

import org.communityboating.kioskclient.BasePackageClass;
import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.admin.CBIDeviceAdmin;
import org.communityboating.kioskclient.admin.CBIKioskLauncherActivity;
import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.event.events.CBIAPPEventManager;
import org.communityboating.kioskclient.event.events.CBIAPPEventType;
import org.communityboating.kioskclient.event.handler.CBIAPPEventCollectionUpdateHandler;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventCollection;
import org.communityboating.kioskclient.event.sqlite.CBIAPPEventSelection;
import org.communityboating.kioskclient.event.sqlite.SQLiteEvent;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.print.PrintServiceHolder;
import org.communityboating.kioskclient.print.PrinterManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    TextView deviceOwnerStatusText;

    TextView activeAdminStatusText;

    RecyclerView eventList;

    EventCollectionAdapter eventAdapter;

    Spinner eventTypeSpinner;
    Spinner eventSortSpinner;
    ArrayAdapter<EventTypeAdapterItem> eventTypeAdapter;
    ArrayAdapter<SortTypeAdapterItem> sortTypeAdapter;
    EditText eventDateStart;
    EditText eventTimeStart;
    EditText eventDateEnd;
    EditText eventTimeEnd;
    Button eventTime1Hr;
    Button eventTime5Hr;
    Button eventTime1Day;
    Button eventTime5Day;
    Button eventSearch;
    CheckBox eventAutoscroll;

    DevicePolicyManager dpm;

    CBIAPPEventCollection eventCollection;
    CBIAPPEventSelection eventSelection;

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
        dpm=(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    public static final int REQUEST_CODE_ENABLE_DPC = 2;
    public void handleEnableAdminClick(View v){
        enableAdmin();
    }
    public void enableAdmin(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, CBIDeviceAdmin.getComponentName(this));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, this.getString(R.string.cbi_admin_add_admin_extra_text));
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }

    public boolean isDPCEnabled(){
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
            startActivityForResult(intent, REQUEST_CODE_ENABLE_DPC);
            this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data){
        if(requestCode == REQUEST_CODE_ENABLE_ADMIN){
            Log.d("h", "aaaa: admin stuff done");
        }
        else if(requestCode == REQUEST_CODE_ENABLE_DPC){
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
    public void onDestroy(){
        super.onDestroy();
        CBIAPPEventManager.onClose();
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
            printService.getPrinterService().sendCommands(builder.getCommands(), new PrinterManager.SendCommandsCallback() {
                @Override
                public void handleSuccess() {
                    Log.d("printer", "done printing");
                    responses++;
                    Log.d("printer", "presses : " + presses + " responses : " + responses);
                }

                @Override
                public void handleError(Exception e) {
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
        switch (v.getId()) {
            case R.id.admin_gui_button_event_time_1hr:
                eventTimePreselect(1);
                break;
            case R.id.admin_gui_button_event_time_5hr:
                eventTimePreselect(5);
                break;
            case R.id.admin_gui_button_event_time_1day:
                eventTimePreselect(24);
                break;
            case R.id.admin_gui_button_event_time_5day:
                eventTimePreselect(5 * 24);
                break;
            case R.id.admin_gui_button_event_search:
                searchEvents();
                break;
        }
        if (selectedProperty == null)
            return;
        switch (v.getId()) {
            case R.id.admin_gui_properties_editor_button_get:
                updatePropertyGUI(true);
                break;
            case R.id.admin_gui_properties_editor_button_set:
                String currentValue = propertyInputEditText.getText().toString();
                if (currentValue == null)
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

    private void searchEvents(){
        try{
            Date date = dateFormat.parse(eventDateStart.getText().toString());
            Date time = timeFormat.parse(eventTimeStart.getText().toString());
            eventSelection.setStartTimeStamp(makeTime(date, time));
            Log.d("derpderp", "derpderp : " + date.getTime() + " : " + time.getTime());
        }catch (ParseException e){
            eventSelection.setStartTimeStamp(null);
        }
        try{
            Date date = dateFormat.parse(eventDateEnd.getText().toString());
            Date time = timeFormat.parse(eventTimeEnd.getText().toString());
            eventSelection.setEndTimeStamp(makeTime(date, time));
        }catch (ParseException e){
            eventSelection.setEndTimeStamp(null);
        }
        int sortType = sortTypeAdapter.getItem(eventSortSpinner.getSelectedItemPosition()).getSortTypeValue();
        eventSelection.setSortType(sortType);
        CBIAPPEventType eventType = eventTypeAdapter.getItem(eventTypeSpinner.getSelectedItemPosition()).getEventType();
        eventSelection.setEventType(eventType);
        eventCollection.updateSelection(eventSelection);
    }

    public long makeTime(Date date, Date time){
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);
        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDate.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
        calendarDate.set(Calendar.MILLISECOND, calendarTime.get(Calendar.MILLISECOND));
        return calendarDate.getTimeInMillis();
    }

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public void eventTimePreselect(int hours){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -hours);
        eventSelection.setEndTimeStamp(null);
        eventSelection.setStartTimeStamp(calendar.getTimeInMillis());
        Log.d("derpderp", "what what ?");
        updateInputsFromSelection();
    }

    public void updateInputsFromSelection(){
        int sortType = eventSelection.getSortType();
        CBIAPPEventType eventType = eventSelection.getEventType();
        for(int i = 0; i < sortTypeAdapter.getCount(); i++){
            SortTypeAdapterItem item = sortTypeAdapter.getItem(i);
            if(item.getSortTypeValue() == sortType){
                eventSortSpinner.setSelection(i);
                break;
            }
        }
        for(int i = 0; i < eventTypeAdapter.getCount(); i++){
            EventTypeAdapterItem item = eventTypeAdapter.getItem(i);
            if(item.getEventType() == eventType){
                eventTypeSpinner.setSelection(i);
                break;
            }
        }
        if(eventSelection.getStartTimeStamp() == null){
            eventDateStart.getText().clear();
            eventTimeStart.getText().clear();
        }else{
            Date startDate = new Date(eventSelection.getStartTimeStamp());
            String dateString = dateFormat.format(startDate);
            String timeString = timeFormat.format(startDate);
            eventDateStart.setText(dateString);
            eventTimeStart.setText(timeString);
        }
        if(eventSelection.getEndTimeStamp() == null){
            eventDateEnd.getText().clear();
            eventTimeEnd.getText().clear();
        }else{
            Date endDate = new Date(eventSelection.getEndTimeStamp());
            String dateString = dateFormat.format(endDate);
            String timeString = timeFormat.format(endDate);
            eventDateEnd.setText(dateString);
            eventTimeEnd.setText(timeString);
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
        eventTypeSpinner=findViewById(R.id.admin_gui_spinner_event_type);
        eventSortSpinner=findViewById(R.id.admin_gui_spinner_sort_type);
        eventDateStart=findViewById(R.id.admin_gui_edit_text_date_start);
        eventTimeStart=findViewById(R.id.admin_gui_edit_text_time_start);
        eventDateEnd=findViewById(R.id.admin_gui_edit_text_date_end);
        eventTimeEnd=findViewById(R.id.admin_gui_edit_text_time_end);
        eventTime1Hr=findViewById(R.id.admin_gui_button_event_time_1hr);
        eventTime5Hr=findViewById(R.id.admin_gui_button_event_time_5hr);
        eventTime1Day=findViewById(R.id.admin_gui_button_event_time_1day);
        eventTime5Day=findViewById(R.id.admin_gui_button_event_time_5day);
        eventSearch=findViewById(R.id.admin_gui_button_event_search);
        eventAutoscroll=findViewById(R.id.admin_gui_checkbox_autoscroll);
        deviceOwnerStatusText=findViewById(R.id.admin_gui_device_owner_status);
        activeAdminStatusText=findViewById(R.id.admin_gui_active_admin_status);
        //cbiAPIKeyUpdateButton = (Button) findViewById(R.id.admin_gui_cbi_api_key_update_button);
    }

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
        eventCollection = CBIAPPEventManager.getDBHelper().getCollection();
        eventSelection = new CBIAPPEventSelection();
        eventAdapter=new EventCollectionAdapter(eventCollection);
        eventCollection.setCollectionUpdateHandler(new CBIAPPEventCollectionUpdateHandler() {
            @Override
            public void handleCollectionUpdate() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        eventAdapter.notifyDataSetChanged();
                        if(eventAutoscroll.isChecked())
                            eventList.scrollToPosition(eventAdapter.getItemCount() - 1);
                    }
                });
            }
        });
        eventList.setAdapter(eventAdapter);
        EventTypeAdapterItem[] eventTypeItems = new EventTypeAdapterItem[CBIAPPEventType.values().length + 1];
        eventTypeItems[0] = new EventTypeAdapterItem(null);
        for(int i = 1; i < eventTypeItems.length; i++){
            eventTypeItems[i] = new EventTypeAdapterItem(CBIAPPEventType.values()[i - 1]);
        }
        eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypeItems);
        eventTypeSpinner.setAdapter(eventTypeAdapter);
        SortTypeAdapterItem[] sortTypeItems = new SortTypeAdapterItem[]{
                new SortTypeAdapterItem("Event Type", CBIAPPEventSelection.SORT_TYPE_EVENT_TYPE),
                new SortTypeAdapterItem("Event Timestamp", CBIAPPEventSelection.SORT_TYPE_TIMESTAMP),
                new SortTypeAdapterItem("Event Timestamp (rev)", CBIAPPEventSelection.SORT_TYPE_TIMESTAMP_REVERSE)
        };
        sortTypeAdapter = new ArrayAdapter<SortTypeAdapterItem>(this, android.R.layout.simple_spinner_item, sortTypeItems);
        eventSortSpinner.setAdapter(sortTypeAdapter);
        eventTime1Hr.setOnClickListener(this);
        eventTime5Hr.setOnClickListener(this);
        eventTime1Day.setOnClickListener(this);
        eventTime5Day.setOnClickListener(this);
        eventSearch.setOnClickListener(this);
    }

    public static class EventTypeAdapterItem{
        CBIAPPEventType eventType;
        private EventTypeAdapterItem(CBIAPPEventType eventType){
            this.eventType=eventType;
        }
        public CBIAPPEventType getEventType(){
            return eventType;
        }
        @Override
        public String toString(){
            if(eventType==null)
                return "None";
            else
                return eventType.name();
        }
    }

    public static class SortTypeAdapterItem{
        String sortTypeName;
        int sortTypeValue;
        private SortTypeAdapterItem(String sortTypeName, int sortTypeValue){
            this.sortTypeName = sortTypeName;
            this.sortTypeValue = sortTypeValue;
        }
        public int getSortTypeValue(){
            return sortTypeValue;
        }
        @Override
        public String toString(){
            return sortTypeName;
        }
    }

    public void handleClickAddEvent(View v){
        SQLiteEvent event = new SQLiteEvent();
        event.setEventTimestamp(new Date().getTime());
        event.setEventType(CBIAPPEventType.EVENT_TYPE_PRINTER);
        event.setEventTitle("WOOP WOOP");
        event.setEventMessage("AN EVENT");
        CBIAPPEventManager.getDBHelper().insertEventAsync(event);
    }

    public void handleClickUp(View v){
        eventList.scrollToPosition(eventAdapter.getItemCount() - 1);
    }

    private void updateDeviceOwnerStatus(){
        if(deviceOwnerStatusText == null){
            return;
        }
        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean isDeviceOwner = dpm.isDeviceOwnerApp(this.getPackageName());
        boolean isActiveAdmin = dpm.isAdminActive(CBIDeviceAdmin.getComponentName(this));
        if(isDeviceOwner){
            deviceOwnerStatusText.setText("App is currently device owner");
            deviceOwnerStatusText.setTextColor(Color.GREEN);
        }else{
            String setDeviceOwnerCommand = "adb shell dpm set-device-owner " + CBIDeviceAdmin.getComponentName(this);
            deviceOwnerStatusText.setText("App is not device owner" + setDeviceOwnerCommand);
            deviceOwnerStatusText.setTextColor(Color.RED);
        }
        if(isActiveAdmin){
            activeAdminStatusText.setText("App is active admin");
            activeAdminStatusText.setTextColor(Color.GREEN);
        }else{
            activeAdminStatusText.setText("App not active admin");
            activeAdminStatusText.setTextColor(Color.RED);
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
