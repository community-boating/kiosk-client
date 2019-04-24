package com.example.alexbanks.cbiapp.config;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class AdminConfigProperties {

    private static Properties adminProperties;

    private static final String adminConfigPropertiesLocation="adminconfig";

    private static final String PROPERTY_CBI_API_KEY="cbi.api.key";

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

    public static void loadProperties(Context context){
        if(adminProperties==null)
            adminProperties=new Properties();
        try {
            FileInputStream inputStream = context.openFileInput(adminConfigPropertiesLocation);
            adminProperties.load(inputStream);
            inputStream.close();
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
