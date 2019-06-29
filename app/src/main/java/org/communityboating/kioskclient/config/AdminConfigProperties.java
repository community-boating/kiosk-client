package org.communityboating.kioskclient.config;

import android.content.Context;
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

    public static final String PROPERTY_CBI_PRINTER_BLUETOOTH_ADDRESS="cbi.printer.bluetooth.address";



    private static Properties adminProperties;

    private static boolean hasProperties=false;

    private static final String adminConfigPropertiesLocation="adminconfig";

    private static final Map<String, String> defaultAdminConfigPropertyValues=new HashMap(){
        {
            put(PROPERTY_CBI_API_URL, "https://api-dev.community-boating.org/api");
            put(PROPERTY_CBI_PRINTER_BLUETOOTH_ADDRESS, "some bluetooth address");
        }
    };

    public static String getDefaultAdminConfigPropertyValue(String name){
        return defaultAdminConfigPropertyValues.get(name);
    }

    public static String setAdminConfigPropertyValue(String name, String value){
        checkAdminProperties();
        if(value==null||value.isEmpty())
            value=defaultAdminConfigPropertyValues.get(name);
        adminProperties.setProperty(name, value);
        return value;
    }

    public static String getAdminConfigPropertyValue(String name){
        checkAdminProperties();
        String defaultValue = defaultAdminConfigPropertyValues.get(name);
        return adminProperties.getProperty(name, defaultValue);
    }

    public static String getCBIPrinterBluetoothAddress(){
        return getAdminConfigPropertyValue(PROPERTY_CBI_PRINTER_BLUETOOTH_ADDRESS);
    }

    public static void setCBIPrinterBluetoothAddress(String printerBluetoothAddress){
        setAdminConfigPropertyValue(PROPERTY_CBI_PRINTER_BLUETOOTH_ADDRESS, printerBluetoothAddress);
    }

    public static String getPropertyCbiApiUrl(){
        return getAdminConfigPropertyValue(PROPERTY_CBI_API_URL);
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

}
