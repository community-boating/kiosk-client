package org.communityboating.kioskclient.input.listener;

import android.util.Log;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.communityboating.kioskclient.input.CustomInputRadioTableLayout;

public class CustomInputOnCheckedChangeListener extends CustomInputProgressStateListener implements CompoundButton.OnCheckedChangeListener{

    public CustomInputOnCheckedChangeListener(String progressStateVariableName) {
        super(progressStateVariableName);
    }

    @Override
    public void updateProgressStateValidatorError(boolean hidden) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView instanceof ToggleButton){
            ToggleButton toggleButton = (ToggleButton)buttonView;
            Log.d("derp", "derp" + (buttonView instanceof ToggleButton));
            if(isChecked){
                String ecType = toggleButton.getTextOn().toString();
                this.updateProgressStateVariableValue(ecType);
                Log.d("derp", "derpderp" + this.getProgressStateVariableValue());
                CustomInputRadioTableLayout tableLayout = findCustomInputRadioTableLayout(toggleButton.getParent());
                Log.d("derp", "derpwhat" + (tableLayout!=null));
                if(tableLayout!=null)
                    tableLayout.updateButtons();
            }else{
                this.updateProgressStateVariableValue(null);
            }
        }
    }

    private CustomInputRadioTableLayout findCustomInputRadioTableLayout(ViewParent viewParent){
        if(viewParent == null)
            return null;
        if(viewParent instanceof CustomInputRadioTableLayout){
            return (CustomInputRadioTableLayout)viewParent;
        }
        ViewParent parent = viewParent.getParent();
        if(parent == null)
            return null;
        return findCustomInputRadioTableLayout(parent);
    }

}
