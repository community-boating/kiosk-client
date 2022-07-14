package org.communityboating.kioskclient.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomKeyboardView extends View implements View.OnTouchListener {

    //List<CustomKeyboardKey> keys = new LinkedList<>();
    {
    //    keys.add(new CustomKeyboardKey(50, 50, 100, 100, 'd'));
    //    keys.add(new CustomKeyboardKey(150, 50, 200, 100, 'a'));
    //    keys.add(new CustomKeyboardKey(250, 50, 300, 100, 'c'));

    }

    public CustomKeyboardView(Context context) {
        super(context);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        KeyboardView view;
    }

    Paint paint = new Paint();
    {
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas){
        KeyboardView view;
        this.getOverlay().add(new ShapeDrawable());
        Paint derp = new Paint();
        derp.setStyle(Paint.Style.FILL_AND_STROKE);
        //derp.setStyle(Paint.Style.STROKE);
        derp.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("derpderp", "derpderp : " + event.getPressure());
        return false;
    }
}
