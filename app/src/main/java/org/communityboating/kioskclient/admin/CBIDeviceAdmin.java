package org.communityboating.kioskclient.admin;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothClass;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.communityboating.kioskclient.R;

public class CBIDeviceAdmin extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context){
        return new ComponentName(context.getApplicationContext(), CBIDeviceAdmin.class);
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
