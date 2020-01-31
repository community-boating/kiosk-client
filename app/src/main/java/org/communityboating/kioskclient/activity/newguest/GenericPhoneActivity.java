package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.keyboard.CustomKeyboard;
import org.communityboating.kioskclient.progress.ProgressState;

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
        TextView textView = (TextView)findViewById(R.id.header_title_text);
        textView.setText(this.getTitleTextID());
    }

}
