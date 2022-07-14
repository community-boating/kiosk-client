package org.communityboating.kioskclient.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.admingui.AdminGUIActivity;

public class AdminConfigPropertyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private String adminConfigPropertyName;

    private LinearLayout parentLayout;

    private AdminGUIActivity activity;

    private TextView nameText;

    public AdminConfigPropertyEditText(Context context) {
        super(context);
    }

    public AdminConfigPropertyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity=(AdminGUIActivity)this.getContext();
        loadAttributes(context, attrs);
    }

    public AdminConfigPropertyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity=(AdminGUIActivity)this.getContext();
        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AdminConfigPropertyEditText, 0, 0);
        adminConfigPropertyName=typedArray.getString(R.styleable.AdminConfigPropertyEditText_adminConfigPropertyName);
    }

    private void setupView(){
        if(!(this.getParent() instanceof LinearLayout) || ((LinearLayout)this.getParent()).getOrientation()!=LinearLayout.HORIZONTAL) {
            setupLinearLayout();
            return;
        }
        parentLayout=(LinearLayout)this.getParent();
        Context context = this.getContext();
        Button getButton = new Button(context);
        getButton.setText("Get");
        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGetClick();
            }
        });
        Button setButton = new Button(context);
        setButton.setText("Set");
        setButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetClick();
            }
        });
        Button setDefaultButton = new Button(context);
        setDefaultButton.setText("Set Default");
        setDefaultButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetDefaultClick();
            }
        });
        nameText = new TextView(context);
        nameText.setText("Property name : " + adminConfigPropertyName);
        parentLayout.addView(getButton);
        parentLayout.addView(setButton);
        parentLayout.addView(setDefaultButton);
        parentLayout.addView(nameText);
    }

    private void handleGetClick(){
        activity.checkAdminReady();
        String propertyValue = AdminConfigProperties.get(adminConfigPropertyName);
        setText(propertyValue);
        nameText.setText("Got value of " + adminConfigPropertyName);
    }

    private void handleSetClick(){
        activity.checkAdminReady();
        String value = getText().toString();
        AdminConfigProperties.setAdminConfigPropertyValue(adminConfigPropertyName, value);
        AdminConfigProperties.storeProperties(this.getContext());
        nameText.setText("Set value of " + adminConfigPropertyName);
    }

    private void handleSetDefaultClick(){
        activity.checkAdminReady();
        String defaultValue = AdminConfigProperties.getDefaultAdminConfigPropertyValue(adminConfigPropertyName);
        setText(defaultValue);
        AdminConfigProperties.setAdminConfigPropertyValue(adminConfigPropertyName, defaultValue);
        AdminConfigProperties.storeProperties(this.getContext());
        nameText.setText("Set to default value for " + adminConfigPropertyName);
    }

    private void setupLinearLayout(){
        Context context = this.getContext();
        LinearLayout newLayout = new LinearLayout(context);
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup parent = (ViewGroup)this.getParent();
        int parentIndex = parent.indexOfChild(this);
        parent.removeViewAt(parentIndex);
        newLayout.addView(this);
        parent.addView(newLayout, parentIndex);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        setupView();
    }

}