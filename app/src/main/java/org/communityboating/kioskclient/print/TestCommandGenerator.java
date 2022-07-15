package org.communityboating.kioskclient.print;

import android.app.Activity;
import android.util.Log;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.ModelCapability;

import org.communityboating.kioskclient.util.ToastUtil;

public class TestCommandGenerator {
    public static void generateTestCommands(ICommandBuilder builder) {
        builder.beginDocument();
        builder.append("=====Test print=====".getBytes());
        builder.appendLogo(ICommandBuilder.LogoSize.Normal, 0x01);
        builder.append("Logo Should Be Above!!!".getBytes());
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        Log.d("printer", "already done with the touch event");
    }

    public static void sendTestCommands(Activity instance, PrintServiceHolder holder){
        ICommandBuilder builder = StarIoExt.createCommandBuilder(ModelCapability.getEmulation(ModelCapability.SM_S230I));
        holder.getPrinterService().sendCommands(builder.getCommands(), new PrinterManager.SendCommandsCallback() {
            @Override
            public void handleSuccess() {
                ToastUtil.toastThreadSafe(instance, "Test print successful");
            }

            @Override
            public void handleError(Exception e) {
                ToastUtil.toastThreadSafe(instance, e.getMessage());
            }
        });
    }

}
