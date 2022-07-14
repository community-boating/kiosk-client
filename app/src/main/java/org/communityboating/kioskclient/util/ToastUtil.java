package org.communityboating.kioskclient.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public abstract class ToastUtil {
    public static void makeToast(Context context, int textRes) {
        Toast toast = Toast.makeText(context, textRes, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
