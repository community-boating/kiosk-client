package org.communityboating.kioskclient.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class CustomKeyboardView extends View {

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
    }

    Paint paint = new Paint();
    {
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas){
        this.getOverlay().add(new ShapeDrawable());
        Paint derp = new Paint();
        derp.setStyle(Paint.Style.FILL_AND_STROKE);
        //derp.setStyle(Paint.Style.STROKE);
        derp.setColor(Color.BLACK);
        //canvas.drawRect(0, 0, 100, 100, derp);
        //canvas.drawLine(-1280, 0, 1280, 666, derp);
        //canvas.drawRect(0, 0, 100, 666, derp);
        //canvas.drawLine(100, 120, 800, 666, derp);
        //canvas.drawRect(canvas.getClipBounds(), derp);
        //for(CustomKeyboardKey key : keys){
        //    canvas.drawRect(key.left, key.top, key.right, key.bottom, paint);
        //}
        Log.d("derpderp", "painting : " + canvas.getClipBounds().left + " : " + canvas.getClipBounds().top);
    }

}
