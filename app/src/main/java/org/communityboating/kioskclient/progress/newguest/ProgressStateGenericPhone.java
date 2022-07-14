package org.communityboating.kioskclient.progress.newguest;

import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateSizedNumberValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

import java.util.Formatter;

public class ProgressStateGenericPhone extends ProgressState {

    private static final String KEY_PHONE_1="phone_1";
    private static final String KEY_PHONE_2="phone_2";
    private static final String KEY_PHONE_3="phone_3";

    protected static void addPhoneValidators(Class<? extends ProgressStateGenericPhone> clazz){
        String emptyText = "Enter phone number";
        String lengthText3 = "Requires 3 numbers";
        String lengthText4 = "Requires 4 numbers";
        String formatText = "Invalid phone number format";
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_PHONE_1, new ProgressStateSizedNumberValueValidator(
                3, emptyText, lengthText3, formatText)
        );
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_PHONE_2, new ProgressStateSizedNumberValueValidator(
                3, emptyText, lengthText3, formatText)
        );
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_PHONE_3, new ProgressStateSizedNumberValueValidator(
                4, emptyText, lengthText4, formatText)
        );
    }

    public ProgressStateGenericPhone(){
    }

    public String getPhoneNumber(){
        return String.format("%03d%03d%04d", getPhone1(), getPhone2(), getPhone3());
    }

    public void setPhone1(Integer v){
        putInteger(KEY_PHONE_1, v);
    }

    public Integer getPhone1(){
        return getInteger(KEY_PHONE_1);
    }

    public void setPhone2(Integer v){
        putInteger(KEY_PHONE_2, v);
    }

    public Integer getPhone2(){
        return getInteger(KEY_PHONE_2);
    }

    public void setPhone3(Integer v){
        putInteger(KEY_PHONE_3, v);
    }

    public Integer getPhone3(){
        return getInteger(KEY_PHONE_3);
    }

    public boolean isPhone1Valid(){
        return contains(KEY_PHONE_1);
    }

    public boolean isPhone2Valid() {
        return contains(KEY_PHONE_2);
    }

    public boolean isPhone3Valid(){
        return contains(KEY_PHONE_3);
    }

}
