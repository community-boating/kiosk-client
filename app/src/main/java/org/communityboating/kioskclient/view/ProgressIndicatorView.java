package org.communityboating.kioskclient.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import org.communityboating.kioskclient.R;

public class ProgressIndicatorView extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener, View.OnClickListener {

    int barCount = 3;

    boolean hasInitialDraw = false;

    Bitmap bufferedInstance;
    Canvas bufferCanvas;

    float barOffset;

    float barPadding = 16;

    float barRX = 12;
    float barRY = 12;

    static Paint defaultPaint = new Paint();
    static Paint backgroundPaint = new Paint();
    static Paint bubblePaint = new Paint();

    {
        defaultPaint.setAntiAlias(true);
        backgroundPaint.setAntiAlias(true);
        bubblePaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.BLACK);
        bubblePaint.setColor(Color.BLUE);
    }

    float progress = 0.0f;

    public ProgressIndicatorView(Context context) {
        super(context);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void drawInitialIfRequired(Canvas canvas){
        if(hasInitialDraw)
            return;
        drawInitial(canvas);
        hasInitialDraw = true;
    }

    private void drawInitial(Canvas canvas){
        for(int i = 0; i < barCount; i++){
            drawProgressBar(i, canvas, progress);
        }
    }

    private float getBarSize(int bar, float currentProgress){
        float progressBarStart = ((float)bar)/((float)barCount);
        float progressBarEnd = ((float)bar + 1)/((float)barCount);
        if(progressBarEnd <= currentProgress) {
            return 1;
        }else if(progressBarStart >= currentProgress) {
            return 0;
        }else {
            return (currentProgress - progressBarStart) * (float) barCount;
        }
    }

    private void drawProgressBar(int bar, Canvas canvas, float currentProgress){
        float currentX = bar * (barOffset) + barPadding * .5f;
        float width = barOffset - barPadding;
        float height = bufferedInstance.getHeight();
        Log.d("derpderp", "derpderp : " + barOffset + " : " + width + " : " + bufferedInstance.getWidth());
        canvas.drawRoundRect(currentX, 0, currentX + width, height, barRX, barRY, backgroundPaint);
        float clippingWidth = width * getBarSize(bar, currentProgress);
        Log.d("derpderp", "what derp : " + clippingWidth);
        canvas.save();
        canvas.clipRect(currentX, 0, currentX + clippingWidth, height);
        canvas.drawRoundRect(currentX, 0, currentX + width, height, barRX, barRY, bubblePaint);
        canvas.restore();
    }

    private int getBarFromProgress(float progress){
        return Math.min((int)(progress * (barCount)), barCount - 1);
    }

    public void updateDrawBuffer(){
        Resources resource = getResources();
        if(bufferedInstance == null || bufferedInstance.getWidth() != getWidth() || bufferedInstance.getHeight() != getHeight()) {
            bufferedInstance = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            bufferCanvas = new Canvas(bufferedInstance);
            hasInitialDraw = false;
            barOffset = ((float)getWidth())/((float)barCount);
            int colorGradientStart = resource.getColor(R.color.progressShadowDark);
            int colorGradientEnd = resource.getColor(R.color.backgroundWhite);
            int colorAccent = resource.getColor(R.color.colorAccent);
            backgroundPaint.setShader(new LinearGradient(getWidth()/2, 0, getWidth()/2, getHeight(), colorGradientStart, colorGradientEnd, Shader.TileMode.CLAMP));
            bubblePaint.setColor(colorAccent);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        updateDrawBuffer();
        drawInitialIfRequired(bufferCanvas);
        canvas.drawBitmap(bufferedInstance, 0, 0, defaultPaint);
        if(activeAnimator != null && !activeAnimator.isRunning()){
            activeAnimator.start();
        }
    }

    float previousAnimatedValue;

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float animatedValue = (float)animation.getAnimatedValue();
        int previousBar = getBarFromProgress(previousAnimatedValue);
        previousAnimatedValue = animatedValue;
        int currentBar = getBarFromProgress(animatedValue);
        if(previousBar != currentBar) {
            drawProgressBar(previousBar, bufferCanvas, animatedValue);
            drawProgressBar(currentBar, bufferCanvas, animatedValue);
        }else{
            drawProgressBar(currentBar, bufferCanvas, animatedValue);
        }
        invalidate();
    }

    ValueAnimator activeAnimator;

    float initialProgress;
    float finalProgress;

    public void animateTo(float oldProgress, float newProgress){
        if(activeAnimator != null && activeAnimator.isRunning())
            activeAnimator.cancel();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(oldProgress, newProgress);
        initialProgress = oldProgress;
        finalProgress = newProgress;
        activeAnimator = valueAnimator;
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addListener(this);
        valueAnimator.addUpdateListener(this);
        valueAnimator.setDuration(500);
        if(bufferedInstance != null)
            valueAnimator.start();
    }

    private void redrawEntireProgressBar(){
        drawInitial(bufferCanvas);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        progress = initialProgress;
        previousAnimatedValue = initialProgress;
        redrawEntireProgressBar();
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        progress = finalProgress;
        redrawEntireProgressBar();
        activeAnimator = null;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onClick(View v) {
        animateTo(.5f, 1f);
    }
}
