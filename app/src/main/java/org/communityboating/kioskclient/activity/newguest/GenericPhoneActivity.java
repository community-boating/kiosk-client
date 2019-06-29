package org.communityboating.kioskclient.activity.newguest;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.ProgressState;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestPhone;

import org.w3c.dom.Text;

public class GenericPhoneActivity<T extends ProgressState> extends BaseActivity<T> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_phone);

        CustomKeyboard customKeyboard = new CustomKeyboard(this, CustomKeyboard.KEYBOARD_MODE_NUMBER_PAD, R.id.custom_keyboard_view_phone);
        customKeyboard.addTextViewsFromCustomInputManager();
        customKeyboard.showCustomKeyboard();
        customKeyboard.setTextViewFocuses();
    }

    @Override
    public void onResume(){
        super.onResume();
        setTitleString();
    }

    public int getTitleTextID(){
        return -1;
    }

    private void setTitleString(){
        TextView textView = (TextView)findViewById(R.id.act_ng_phone_title);
        textView.setText(this.getTitleTextID());
    }

}
