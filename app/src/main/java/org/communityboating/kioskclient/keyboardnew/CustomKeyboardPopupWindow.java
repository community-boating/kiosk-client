package org.communityboating.kioskclient.keyboardnew;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.reflect.Field;

public class CustomKeyboardPopupWindow {

    View content;

    WindowManager windowManager;

    WindowManager.LayoutParams layoutParams;

    boolean show;

    public CustomKeyboardPopupWindow(View view, int w, int h){
        this.content=view;
        windowManager = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public void showPopupView(View parent, int g, int x, int y) throws Throwable{

        for(Field f : PopupWindow.class.getDeclaredFields()){
            Log.d("done now", "nerple : " + f.getName() + " : " + f.getType().getName());
        }

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        layoutParams.token = parent.getWindowToken();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.format = PixelFormat.TRANSLUCENT;

        show();
        //this.getContentView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN |
        //        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //this.getContentView().getRootView().setFitsSystemWindows(false);
        //this.getContentView().setFitsSystemWindows(false);
        //Field field = PopupWindow.class.getDeclaredField("mPopupView");
        //field.setAccessible(true);
        //View view = (View)field.get(this);
        //view.setFitsSystemWindows(false);
        //view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN |
        //        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void show(){
        if(show){return;}
        windowManager.addView(content, layoutParams);
        show=true;
    }

    public void hide(){
        if(!show){return;}
        windowManager.removeView(content);
        show=false;
    }

    public void updatePosition(int x, int y){
        layoutParams.x = x;
        layoutParams.y = y;
        if(show) {
            windowManager.updateViewLayout(content, layoutParams);
        }
    }

}
