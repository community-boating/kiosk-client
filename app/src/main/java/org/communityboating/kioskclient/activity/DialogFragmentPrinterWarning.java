package org.communityboating.kioskclient.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.communityboating.kioskclient.R;

public class DialogFragmentPrinterWarning extends DialogFragmentBase {

    private TextView warningDescription;

    private Button continueButton;

    private String warningDescriptionString;

    private boolean canContinue;

    public static final String PRINTER_WARNING_ARGUMENT_WARNING_DESCRIPTION="argument.warning.description";
    public static final String PRINTER_WARNING_ARGUMENT_CAN_CONTINUE="argument.can.continue";

    @Override
    public int getLayoutResID(){
        return R.layout.printer_warning_dialog_fragment;
    }

    @Override
    public void handleDialogViewCreation(View dialogView){
        Bundle arguments = getArguments();
        warningDescriptionString = arguments.getString(PRINTER_WARNING_ARGUMENT_WARNING_DESCRIPTION);
        canContinue = arguments.getBoolean(PRINTER_WARNING_ARGUMENT_CAN_CONTINUE);
        warningDescription = dialogView.findViewById(R.id.printer_warning_dialog_fragment_warning_description);
        continueButton = dialogView.findViewById(R.id.printer_warning_dialog_fragment_button_continue);
        updateView();
    }

    private void updateView(){
        warningDescription.setText(warningDescriptionString);
        continueButton.setVisibility(canContinue ? View.VISIBLE : View.INVISIBLE);
    }

}
