package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.newguest.NewGuestNameActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.validator.ProgressStateNotBlankValueValidator;
import org.communityboating.kioskclient.progress.validator.ProgressStateValidatorManager;

public class ProgressStateNewGuestName extends ProgressState {

    private static final String KEY_FIRST_NAME="first_name";
    private static final String KEY_LAST_NAME="last_name";
    static {
        Class<ProgressStateNewGuestName> clazz=ProgressStateNewGuestName.class;
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_FIRST_NAME, new ProgressStateNotBlankValueValidator("Enter a first name"));
        ProgressStateValidatorManager.addValueValidator(clazz, KEY_LAST_NAME, new ProgressStateNotBlankValueValidator("Enter a last name"));
    }

    public ProgressStateNewGuestName() {
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return NewGuestNameActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestDOB();
    }

    public String getFirstName(){
        return this.get(KEY_FIRST_NAME);
    }

    public void setFirstName(String firstName){
        this.put(KEY_FIRST_NAME, firstName);
    }

    public String getLastName(){
        return this.get(KEY_LAST_NAME);
    }

    public void setLastName(String lastName){
        this.put(KEY_LAST_NAME, lastName);
    }

    public boolean isFirstNameValid(){
        return !"".equals(getFirstName());
    }

    public boolean isLastNameValid(){
        return !"".equals(getLastName());
    }
}
