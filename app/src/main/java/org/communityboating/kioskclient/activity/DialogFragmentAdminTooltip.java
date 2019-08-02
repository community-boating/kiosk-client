package org.communityboating.kioskclient.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.ModelCapability;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.admingui.AdminGUIActivity;
import org.communityboating.kioskclient.print.PrinterManager;

public class DialogFragmentAdminTooltip extends DialogFragmentBase {

    @Override
    public int getLayoutResID(){
        return R.layout.admin_tooltop_dialog_fragment;
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch(v.getId()){
            case R.id.admin_tooltip_dialog_fragment_button_lock:
                lockDevice();
                break;
            case R.id.admin_tooltip_dialog_fragment_button_print:
                testPrint();
                break;
            case R.id.admin_tooltip_dialog_fragment_button_settings:
                launchAdminSettings();
                break;
        }
    }

    private void launchAdminSettings(){
        Intent adminIntent = new Intent(this.getContext(), AdminGUIActivity.class);
        this.getActivity().startActivity(adminIntent);
    }

    long testPrintCount = 0;

    private void testPrint(){
        BaseActivity baseActivity = this.getBaseActivity();
        ICommandBuilder builder = StarIoExt.createCommandBuilder(ModelCapability.getEmulation(ModelCapability.SM_S230I));
        builder.beginDocument();
        for(int i = 0; i < 30; i++) {
            builder.append(("Test print here" + testPrintCount).getBytes());
        }
        testPrintCount++;
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        baseActivity.printService.getPrinterService().sendCommands(builder, new PrinterManager.SendCommandsCallback() {
            @Override
            public void handleSuccess() {
                Log.d("printer", "done printing");
            }

            @Override
            public void handleError(StarIOPortException e) {
                Log.d("printer", "printer error : ", e);
                e.printStackTrace();
            }
        });
    }

    private void lockDevice(){
        BaseActivity baseActivity = this.getBaseActivity();
        baseActivity.lockDevice();
    }

}
