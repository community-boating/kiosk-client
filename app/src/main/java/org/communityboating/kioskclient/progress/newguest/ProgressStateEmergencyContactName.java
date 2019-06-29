package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.EmergencyContactNameActivity;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateEmergencyContactName extends ProgressState {
    public static final String KEY_EC_NAME_FIRST="ec_name_first";
    public static final String KEY_EC_NAME_LAST="ec_name_last";
    static{
        Class<ProgressStateEmergencyContactName> emergencyContactNameClass = ProgressStateEmergencyContactName.class;
        ProgressStateValidatorManager.addValueValidator(emergencyContactNameClass, KEY_EC_NAME_FIRST, new ProgressStateNotBlankValueValidator("Enter a first name"));
        ProgressStateValidatorManager.addValueValidator(emergencyContactNameClass, KEY_EC_NAME_LAST, new ProgressStateNotBlankValueValidator("Enter a last name"));
    }
    public ProgressStateEmergencyContactName(){

    }

    public String getECFirstName(){
        return get(KEY_EC_NAME_FIRST);
    }

    public void setECFirstName(String ecFirstName){
        put(KEY_EC_NAME_FIRST, ecFirstName);
    }

    public String getECLastName(){
        return get(KEY_EC_NAME_LAST);
    }

    public void setECNameLast(String ecNameLast){
        put(KEY_EC_NAME_LAST, ecNameLast);
    }

    @Override
    public Class<? extends Activity> getActivityClass(){
        return EmergencyContactNameActivity.class;
    }

    @Override
    public ProgressState createNextProgressState(){return new ProgressStateEmergencyContactPhone();}

}
