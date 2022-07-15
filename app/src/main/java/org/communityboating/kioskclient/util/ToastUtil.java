package org.communityboating.kioskclient.util;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public abstract class ToastUtil {
    public static void makeToast(Context context, int textRes) {
        Toast toast = Toast.makeText(context, textRes, Toast.LENGTH_LONG);
        showToast(context, toast);
    }
    public static void makeToast(Context context, String text){
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        showToast(context, toast);

    }
    private static void showToast(Context context, Toast toast){
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
    public static void toastThreadSafe(Activity context, String text){
        context.runOnUiThread(() -> {
            makeToast(context, text);
        });
    }
    public static void toastThreadSafe(Activity context, int textRes){
        context.runOnUiThread(() -> {
            makeToast(context, textRes);
        });
    }
}
