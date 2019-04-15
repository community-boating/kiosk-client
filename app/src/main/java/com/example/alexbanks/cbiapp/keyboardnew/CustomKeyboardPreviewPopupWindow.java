package com.example.alexbanks.cbiapp.keyboardnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

public class CustomKeyboardPreviewPopupWindow extends View {

    public int x, y;

    public CustomKeyboardPreviewPopupWindow(Context context) {
        super(context);
    }

    public CustomKeyboardPreviewPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardPreviewPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomKeyboardPreviewPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int w, int h){
        this.setMeasuredDimension(w, h);
        //super.onMeasure(w, h);
    }

    static Paint p = new Paint();
    static{
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.YELLOW);
    }
    @Override
    public void onDraw(Canvas c){
        Log.d("derpderp", "drawdraw");
        c.drawRect(x, y, x + 100, y + 100, p);
    }

}
