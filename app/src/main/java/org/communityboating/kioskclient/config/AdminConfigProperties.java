package org.communityboating.kioskclient.config;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AdminConfigProperties {

    public static final String PROPERTY_CBI_API_URL="cbi.api.url";

    public static final String PROPERTY_CBI_API_KEY="cbi.api.key";

    public static final String PROPERTY_CBI_DEV_MODE="cbi.app.devmode";

    public static final String PROPERTY_CBI_IDLE_TIMEOUT="cbi.page.timeout.idle";

    public static final String PROPERTY_CBI_DURATION_DIALOG_TIMEOUT="cbi.page.timeout.dialog.duration";

    public static final String PROPERTY_CBI_ENABLE_LAUNCHER="cbi.app.launcher.enabled";

    public static final String PROPERTY_CBI_MAX_PRINT_ATTEMPTS="cbi.printer.max.attempts";

    public static final String PROPERTY_CBI_PRINT_CHECKED_BLOCK_TIMEOUT="cbi.printer.checked.block.timeout";

    public static final String PROPERTY_CBI_PRINT_STAR_IO_PORT_TIMEOUT="cbi.printer.star.io.port.timeout";

    public static final String PROPERTY_CBI_PRINT_STAR_IO_PORT_NAME="cbi.printer.star.io.port.name";

    public static final String PROPERTY_CBI_PRINT_STAR_IO_PORT_SETTINGS="cbi.printer.star.io.port.settings";

    public static final String PROPERTY_CBI_PRINT_STAR_IO_EXT_MAX_CONNECTION_ATTEMPTS="cbi.printer.star.io.ext.connection.attempts.max";

    private static Properties adminProperties;

    private static boolean hasProperties=false;

    private static final String adminConfigPropertiesLocation="adminconfig";

    private static final Map<String, DefaultAdminConfigProperty> defaultAdminConfigPropertyValues=new HashMap(){
        {
            put(PROPERTY_CBI_API_KEY, new DefaultAdminConfigProperty(InputType.TYPE_CLASS_TEXT));
            put(PROPERTY_CBI_API_URL, new DefaultAdminConfigProperty(InputType.TYPE_TEXT_VARIATION_URI, "https://api.community-boating.org/api"));
            put(PROPERTY_CBI_DEV_MODE, new DefaultAdminConfigProperty(InputType.TYPE_CLASS_NUMBER, new Integer(1)));
            put(PROPERTY_CBI_IDLE_TIMEOUT, new DefaultAdminConfigProperty(InputType.TYPE_CLASS_NUMBER, 20000l));
            put(PROPERTY_CBI_DURATION_DIALOG_TIMEOUT, new DefaultAdminConfigProperty(InputType.TYPE_CLASS_NUMBER, 10000l));
            put(PROPERTY_CBI_ENABLE_LAUNCHER, new DefaultAdminConfigProperty(new Boolean(false)));
            put(PROPERTY_CBI_MAX_PRINT_ATTEMPTS, new DefaultAdminConfigProperty(new Integer(0)));
            put(PROPERTY_CBI_PRINT_CHECKED_BLOCK_TIMEOUT, new DefaultAdminConfigProperty(new Integer(30000)));
            put(PROPERTY_CBI_PRINT_STAR_IO_PORT_TIMEOUT, new DefaultAdminConfigProperty(new Integer(20000)));
            put(PROPERTY_CBI_PRINT_STAR_IO_PORT_NAME, new DefaultAdminConfigProperty(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS));
            put(PROPERTY_CBI_PRINT_STAR_IO_PORT_SETTINGS, new DefaultAdminConfigProperty(new String()));
            put(PROPERTY_CBI_PRINT_STAR_IO_EXT_MAX_CONNECTION_ATTEMPTS, new DefaultAdminConfigProperty(new Integer(5)));
        }
    };

    public static String getDefaultAdminConfigPropertyValue(String name){
        return defaultAdminConfigPropertyValues.get(name).defaultValue();
    }

    public static String setAdminConfigPropertyValue(String name, String value){
        checkAdminProperties();
        if(value==null||value.isEmpty())
            value=defaultAdminConfigPropertyValues.get(name).defaultValue();
        adminProperties.setProperty(name, value);
        return value;
    }

    public static String get(String name){
        checkAdminProperties();
        String defaultValue = defaultAdminConfigPropertyValues.get(name).defaultValue();
        return adminProperties.getProperty(name, defaultValue);
    }

    public static String getPropertyCbiApiUrl(){
        return get(PROPERTY_CBI_API_URL);
    }

    public static void setPropertyCbiApiUrl(String apiUrl){
        setAdminConfigPropertyValue(PROPERTY_CBI_API_URL, apiUrl);
    }

    public static void setCBIAPIKey(String cbiAPIKey){
        checkAdminProperties();
        adminProperties.setProperty(PROPERTY_CBI_API_KEY, cbiAPIKey);
    }

    public static String getCBIAPIKey(){
        checkAdminProperties();
        if(!adminProperties.containsKey(PROPERTY_CBI_API_KEY))
            throw new RuntimeException("No key set for cbi api");
        return adminProperties.getProperty(PROPERTY_CBI_API_KEY);
    }

    public static boolean hasCBIAPIKey(){
        checkAdminProperties();
        return adminProperties.containsKey(PROPERTY_CBI_API_KEY);
    }

    public static void checkAdminProperties(){
        if(adminProperties==null)
            throw new RuntimeException("Admin properties should be initialized before this point");
    }

    public static void loadPropertiesIfRequired(Context context){
        if(!hasProperties)
            loadProperties(context);
    }

    public static void loadProperties(Context context){
        if(adminProperties==null)
            adminProperties=new Properties();
        try {
            FileInputStream inputStream = context.openFileInput(adminConfigPropertiesLocation);
            adminProperties.load(inputStream);
            inputStream.close();
            hasProperties=true;
        }catch(FileNotFoundException e){
            Log.i("cbiadmin", "no admin properties located");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void storeProperties(Context context){
        try{
            FileOutputStream outputStream = context.openFileOutput(adminConfigPropertiesLocation, Context.MODE_PRIVATE);
            adminProperties.store(outputStream, "");
            outputStream.flush();
            outputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static class DefaultAdminConfigProperty {

        private DefaultAdminConfigProperty(int editTextType, Object defaultValue){
            this.editTextType = editTextType;
            this.defaultValue = defaultValue;
        }

        private DefaultAdminConfigProperty(int editTextType){
            this.editTextType = editTextType;
            this.defaultValue = null;
        }

        private DefaultAdminConfigProperty(Object defaultValue){
            this.editTextType=InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            if(defaultValue instanceof Double || defaultValue instanceof Float)
                this.editTextType=InputType.TYPE_NUMBER_FLAG_DECIMAL;
            else if(defaultValue instanceof Integer || defaultValue instanceof Long || defaultValue instanceof Short)
                this.editTextType=InputType.TYPE_NUMBER_FLAG_SIGNED;
            else if(defaultValue instanceof Number)
                this.editTextType=InputType.TYPE_NUMBER_VARIATION_NORMAL;
            this.defaultValue=defaultValue;
        }

        public String defaultValue(){
            return this.defaultValue == null ? null : this.defaultValue.toString();
        }

        int editTextType;
        Object defaultValue;
    }

    public static Boolean getBoolean(String key){
        return get(key).equals("true");
    }

    public static Integer getInteger(String key){
        return Integer.parseInt(get(key));
    }

    public static Long getLong(String key){
        return Long.parseLong(get(key));
    }

    public static Float getFloat(String key){
        return Float.parseFloat(get(key));
    }

    public static Double getDouble(String key){
        return Double.parseDouble(key);
    }

    public static void set(String key, Object o){
        if(o == null)
            setAdminConfigPropertyValue(key, null);
        else
            setAdminConfigPropertyValue(key, o.toString());
    }

    public static Map<String, DefaultAdminConfigProperty> getDefaultProperties(){
        return defaultAdminConfigPropertyValues;
    }

    public static Properties getProperties(){
        return adminProperties;
    }

    public static Boolean getCBILauncherEnabled(){
        return getBoolean(PROPERTY_CBI_ENABLE_LAUNCHER);
    }

    public static void setCBILauncherEnabled(Boolean enabled){
        set(PROPERTY_CBI_ENABLE_LAUNCHER, enabled);
    }

    public static Integer getMaxPrintAttempts(){
        return getInteger(PROPERTY_CBI_MAX_PRINT_ATTEMPTS);
    }

    public static void setMaxPrintAttempts(Integer maxPrintAttempts){
        set(PROPERTY_CBI_MAX_PRINT_ATTEMPTS, maxPrintAttempts);
    }

    public static Integer getCheckedBlockTimeout(){
        return getInteger(PROPERTY_CBI_PRINT_CHECKED_BLOCK_TIMEOUT);
    }

    public static void setCheckedBlockTimeout(Integer checkedBlockTimeout){
        set(PROPERTY_CBI_PRINT_CHECKED_BLOCK_TIMEOUT, checkedBlockTimeout);
    }

    public static Integer getStarIOPortTimeout(){
        return getInteger(PROPERTY_CBI_PRINT_STAR_IO_PORT_TIMEOUT);
    }

    public static void setStarIOPortTimeout(Integer starIOPortTimeout){
        set(PROPERTY_CBI_PRINT_STAR_IO_PORT_TIMEOUT, starIOPortTimeout);
    }

    public static String getStarIOPortSettings(){
        return get(PROPERTY_CBI_PRINT_STAR_IO_PORT_SETTINGS);
    }

    public static void setStarIOPortSettings(String starIOPortSettings){
        set(PROPERTY_CBI_PRINT_STAR_IO_PORT_SETTINGS, starIOPortSettings);
    }

    public static int getInputType(String key){
        return getDefaultProperties().get(key).editTextType;
    }

    public static String getStarIOPortName(){
        return get(PROPERTY_CBI_PRINT_STAR_IO_PORT_NAME);
    }

    public static void setStarIOPortName(String portName){
        set(PROPERTY_CBI_PRINT_STAR_IO_PORT_NAME, portName);
    }

    public static Integer getStarIOExtMaxConnectionAttempts(){
        return getInteger(PROPERTY_CBI_PRINT_STAR_IO_EXT_MAX_CONNECTION_ATTEMPTS);
    }

    public static void setStarIOExtMaxConnectionAttempts(Integer maxConnectionAttempts){
        set(PROPERTY_CBI_PRINT_STAR_IO_EXT_MAX_CONNECTION_ATTEMPTS, maxConnectionAttempts);
    }

}
