package org.communityboating.kioskclient.admin;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.util.ToastUtil;

public class CBIDeviceAdmin extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context){
        return new ComponentName(context.getApplicationContext(), CBIDeviceAdmin.class);
    }

    @Override
    public void onEnabled(Context context, Intent intent){
        ToastUtil.makeToast(context, R.string.cbi_kiosk_admin_enabled);
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent){
        Intent launch = new Intent(context, EnableDPCProfileActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
        Log.d("h", "ffff: something different now");
        Log.d("cccc", "starting the profile currently");
    }

}
