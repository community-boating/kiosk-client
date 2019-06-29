package org.communityboating.kioskclient.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ToggleButton;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.input.listener.CustomInputOnCheckedChangeListener;

public class CustomInputRadioTableLayout extends TableLayout {

    CustomInputOnCheckedChangeListener onCheckedChangeListener;

    CharSequence[] radioOptions;

    int preferredColumns;

    public CustomInputRadioTableLayout(Context context) {
        super(context);
    }

    public CustomInputRadioTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        if(radioOptions==null)return;
        Context context = getContext();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //Drawable buttonBackground = ContextCompat.getDrawable(getContext(), R.drawable.custom_radio_button_drawable);
        String currentProgressStateVariableValue=onCheckedChangeListener.getProgressStateVariableValue();
        TableRow currentRow = null;
        int count = 0;
        for(CharSequence radioOption : radioOptions){
            if(count % preferredColumns == 0 || currentRow == null) {
                if(currentRow!=null)
                    this.addView(currentRow, layoutParams);
                currentRow = new TableRow(context);
            }
            count++;
            ToggleButton toggleButton = new ToggleButton(getContext());
            toggleButton.setTextOff(radioOption);
            toggleButton.setBackgroundResource(R.drawable.custom_radio_button_drawable);
            toggleButton.setTextOn(radioOption);
            toggleButton.setText(radioOption);
            //toggleButton.setChecked(radioOption.toString().equals(currentProgressStateVariableValue));
            toggleButton.setOnCheckedChangeListener(onCheckedChangeListener);
            currentRow.addView(toggleButton);
        }
        if(currentRow!=null)
            this.addView(currentRow, layoutParams);
        updateButtons();
    }

    public void updateButtons(){
        String value=onCheckedChangeListener.getProgressStateVariableValue();
        int children = this.getChildCount();
        Log.d("derp", "derpderpupdate" + value);
        for(int i = 0; i < children; i++){
            View v = this.getChildAt(i);
            if(v instanceof TableRow){
                TableRow tableRow = (TableRow)v;
                int tchildren = tableRow.getChildCount();
                for(int ti=0;ti<tchildren;ti++){
                    View tv = tableRow.getChildAt(ti);
                    if(tv instanceof ToggleButton){
                        ToggleButton toggleButton = (ToggleButton)tv;
                        boolean checked=toggleButton.getTextOn().equals(value);
                        if(checked!=toggleButton.isChecked()) {
                            toggleButton.setOnCheckedChangeListener(null);
                            toggleButton.setChecked(checked);
                            toggleButton.setOnCheckedChangeListener(onCheckedChangeListener);
                        }
                    }
                }
            }
        }
    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomInputRadioGridLayout, 0, 0);
        String progressStateVariableName = a.getString(R.styleable.CustomInputRadioGridLayout_progressStateVariable);
        onCheckedChangeListener = new CustomInputOnCheckedChangeListener(progressStateVariableName);
        radioOptions=a.getTextArray(R.styleable.CustomInputRadioGridLayout_valueStringArrayResource);
        preferredColumns=a.getInteger(R.styleable.CustomInputRadioGridLayout_preferredColumns, 3);
    }

}
