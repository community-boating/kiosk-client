package org.communityboating.kioskclient.keyboard;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;

import org.communityboating.kioskclient.R;

import java.lang.reflect.Field;

public class CustomSystemKeyboard extends InputMethodService {

    @Override
    public View onCreateInputView(){
        View v = getLayoutInflater().inflate(R.layout.layout_custom_keyboard, null);
        stuff();
        return v;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("not bad", "something okay");
        stuff();
    }

    @Override
    public void onInitializeInterface(){
        stuff();
    }

    public void stuff(){
        Log.d("something", "start");
        try{
            doReflectionStuff();
        }catch(Exception e){
            e.printStackTrace();
            Log.d("something", "bad/failure");
        }
        Log.d("something", "done");
    }

    public void doReflectionStuff() throws Exception{
        Class clazz = InputMethodService.class;
        //Field rootView = InputMethodService.class.getDeclaredField("mRootView");
        //rootView.setAccessible(true);
        Log.d("something", "fields : " + clazz.getFields().length + " : " + clazz.getDeclaredFields().length);
        for(Field fa : clazz.getDeclaredFields()){
            Log.d("something", "field : " + fa.getName() + " : " + fa.getType().getName());
        }
        Field f = clazz.getDeclaredField("mRootView");
        boolean oldA = f.isAccessible();
        f.setAccessible(true);
        final View d = (View)f.get(this);

        //final View v = (View)rootView.get(this);

        /*d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        d.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        DisplayMetrics metrics = new DisplayMetrics();
        d.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        d.getWindow().setLayout(metrics.widthPixels, metrics.heightPixels+100);*/
        d.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //d.setContentView(getLayoutInflater().inflate(R.layout.nav_button_group_layout, null));
    }

}
