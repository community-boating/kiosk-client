package org.communityboating.kioskclient.keyboardnew;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.communityboating.kioskclient.R;

public class CustomKeyboardViewNew extends View {
    public CustomKeyboardViewNew(Context context) {
        super(context);
    }

    public CustomKeyboardViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
    }

    public CustomKeyboardViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(context, attrs);
    }

    public CustomKeyboardViewNew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAttributes(context, attrs);
    }

    private void loadAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomKeyboardViewNew, 0, 0);
        try{
            int d = a.getDimensionPixelOffset(R.styleable.SpinnerCustomInput_progressStateVariable, 0);
            Log.d("helpful", "derp " + d);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int wid, int hei){

        int rWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int rHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        int w = measureDimension(rWidth, wid);
        int h = measureDimension(rHeight, hei);

        setMeasuredDimension(w, h);

    }

    private static int measureDimension(int rSize, int dim){
        int dMode = MeasureSpec.getMode(dim);
        int dSize = MeasureSpec.getMode(dim);
        if(dMode==MeasureSpec.EXACTLY){
            return dSize;
        }else{
            if(dMode==MeasureSpec.AT_MOST){
                return Math.min(rSize, dSize);
            }
            return rSize;
        }
    }

}
