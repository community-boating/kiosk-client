package org.communityboating.kioskclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SignatureView extends View implements View.OnTouchListener{

    private Bitmap signature;
    private Canvas signatureCanvas;
    private static Paint signaturePaint = new Paint();
    private SignatureViewEventListener listener;
    private boolean signatureValid=false;
    //private static Paint signaturePaintPreview = new Paint();

    private float lastX=-1,lastY=-1;

    static {
        //signaturePaint.setStrokeWidth(8);
        signaturePaint.setARGB(255, 0, 0, 0);
        signaturePaint.setStyle(Paint.Style.STROKE);
        signaturePaint.setAntiAlias(true);
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);
        //signaturePaintPreview.setStrokeWidth(8);
        //signaturePaintPreview.setARGB(150, 30, 15, 15);
        //signaturePaintPreview.setStyle(Paint.Style.STROKE);
    }

    public SignatureView(Context context) {
        super(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setSignatureEventListener(SignatureViewEventListener listener){
        this.listener = listener;
    }

    private void updateSignatureValid(boolean valid){
        if(signatureValid != valid){
            signatureValid=valid;
            listener.onViewValidChange(valid);
        }
    }

    int w, h;

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        //this.setOnDragListener(this);
        //this.setOnClickListener(this);
        //this.setOnCapturedPointerListener(this);
        this.setOnTouchListener(this);
    }

    private void drawInitialBitmap(){
        float outerStroke = 1.0f;
        int outerInset = (int)Math.ceil(outerStroke/2.0f);
        Rect clipBounds = signatureCanvas.getClipBounds();
        float top=clipBounds.top;
        float left=clipBounds.left;
        float right=clipBounds.right;
        float bottom=clipBounds.bottom;
        top+=outerStroke/2;
        left+=outerStroke/2;
        right-=outerStroke;
        bottom-=outerStroke;
        //clipBounds.inset(outerInset, outerInset);
        //clipBounds.top+=outerStroke/2;
        //clipBounds.left+=outerStroke/2;
        //clipBounds.right-=outerStroke;
        //clipBounds.bottom-=outerStroke;
        Paint paint = new Paint();
        paint.setStrokeWidth(outerStroke);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        signatureCanvas.drawRect(left, top, right, bottom, paint);
        paint.setColor(Color.argb(255, 230, 230, 230));
        paint.setStyle(Paint.Style.FILL);
        float spacing = 3.0f;
        top+=spacing;
        left+=spacing;
        right-=spacing;
        bottom-=spacing;
        //rect.inset(spacing, spacing);
        signatureCanvas.drawRect(left, top, right, bottom, paint);
        //signatureCanvas.drawRect(rect, paint);
        float xoff = (clipBounds.right-clipBounds.left)*0.05f;
        float yoff = clipBounds.top+(clipBounds.bottom-clipBounds.top)*0.8f;
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4f);
        paint.setAntiAlias(true);
        float lx=xoff;
        float ly=yoff-10;
        float lxx=lx+48;
        float lyy=ly-64;
        signatureCanvas.drawLine(lx, ly, lxx, lyy, paint);
        signatureCanvas.drawLine(lx, lyy, lxx, ly, paint);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        signatureCanvas.drawLine(lxx + 40, yoff, clipBounds.right-xoff, yoff, paint);
    }

    @Override
    public void onSizeChanged(int w, int h, int wold, int hold){
        signature = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        signatureCanvas = new Canvas(signature);
        drawInitialBitmap();
        //signaturePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.w=w;
        this.h=h;
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawBitmap(signature, 0, 0, signaturePaint);
        for(SignatureTrace path : previousPoints.values()){
            canvas.drawBitmap(path.tempMap, 0, 0, signaturePaint);
        }
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();
            signatureCanvas.drawCircle(x, y, 5, signaturePaint);
            this.invalidate();
            return true;
        }
        return true;
    }*/

    //@Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d("derpderp","ondrag");
        //if(event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();
            //signatureCanvas.drawCircle(x, y, 5, signaturePaint);
            //this.invalidate();
            return true;
        //}
        //return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.handleMotionEvent(v, event);
        this.invalidate();
        return true;
        //}
        //return true;
    }

    private Map<Integer, SignatureTrace> previousPoints = new TreeMap<Integer, SignatureTrace>();

    private static float calculateRadius(float pressure){
        return 8.0f*pressure + 4.0f;
    }

    public void handleMotionEvent(View v, MotionEvent event){
        //Log.d("derpderp", "event size: " + event.getHistorySize());
        int historySize = event.getHistorySize();
        int pointerCount = event.getPointerCount();
        int action = event.getAction();
        int actionIndex = event.getActionIndex();
        Log.d("derpderp2", "pointers: " + historySize);
        for(int p = 0; p < pointerCount; p++){
            int pointerID = event.getPointerId(p);
            SignatureTrace points = previousPoints.get(pointerID);
            float cx = event.getX(p);
            float cy = event.getY(p);
            float cr = calculateRadius(event.getPressure(p));
            if(points==null||action==MotionEvent.ACTION_DOWN||((action&MotionEvent.ACTION_POINTER_DOWN)==MotionEvent.ACTION_POINTER_DOWN)) {
                if(points == null) {
                    points = new SignatureTrace();
                    points.tempMap = Bitmap.createBitmap(signature.getWidth(), signature.getHeight(), signature.getConfig());
                    points.tempMapCanvas = new Canvas(points.tempMap);
                }
                float fx = cx;
                float fy = cy;
                float fr = cr;
                if(historySize > 0) {
                    fx = event.getHistoricalX(p, 0);
                    fy = event.getHistoricalY(p, 0);
                    fr = event.getHistoricalPressure(p, 0);
                }
                signaturePaint.setStrokeWidth(fr);
                points.tempMapCanvas.drawPoint(fx, fy, signaturePaint);
                points.lastX=fx;
                points.lastY=fy;
                points.lastR=fr;
            }
            for(int h = 0; h < historySize; h++){
                float x=event.getHistoricalX(p, h);
                float y=event.getHistoricalY(p, h);
                float r = calculateRadius(event.getHistoricalPressure(p, h));
                /*if(h+2<historySize) {
                    points.cubicTo(x, y, event.getHistoricalX(p, h+1), event.getHistoricalY(p, h+1),
                            event.getHistoricalX(p, h+2), event.getHistoricalY(p, h+2));
                    h+=3;
                }else if(h+1<historySize){
                    points.quadTo(x, y, event.getHistoricalX(p, h+1), event.getHistoricalY(p, h+1));
                    h+=2;
                }else{
     az               points.lineTo(x, y);
                    h+=1;
                }*/
                //signaturePaint.setStrokeWidth(r);
                //points.tempMapCanvas.drawLine(points.lastX, points.lastY, x, y, signaturePaint);
                interpolateTo(points, x, y, r);
            }
            if(action==MotionEvent.ACTION_UP) {
                //signatureCanvas.drawPath(points, signaturePaint);
                //points.reset();
                signatureCanvas.drawBitmap(points.tempMap, 0, 0, signaturePaint);
                points.tempMap.eraseColor(Color.TRANSPARENT);
                updateSignatureValid(true);
                //points.tempMap.eraseColor(signaturePaint.getColor());
            }else if((action&MotionEvent.ACTION_POINTER_UP)==MotionEvent.ACTION_POINTER_UP){
                if(p==actionIndex){
                    signatureCanvas.drawBitmap(points.tempMap, 0, 0, signaturePaint);

                    //signatureCanvas.drawBitmap(points.tempMap, 0, 0, signaturePaint);
                    points.tempMap.eraseColor(Color.TRANSPARENT);
                    updateSignatureValid(true);
                    //points.tempMap.eraseColor(signaturePaint.getColor());
                    //signatureCanvas.drawPath(points, signaturePaint);
                    //points.reset();
                }
            }else if(action==MotionEvent.ACTION_MOVE){
                //points.lineTo(cx, cy);
                //signaturePaint.setStrokeWidth(cr);
                //points.tempMapCanvas.drawLine(points.lastX, points.lastY, cx, cy, signaturePaint);
                //signatureCanvas.drawLine(points.lastX, points.lastY, cx, cy, signaturePaint);
                interpolateTo(points, cx, cy, cr);
            }else if(action==MotionEvent.ACTION_DOWN||((action&MotionEvent.ACTION_POINTER_DOWN)==MotionEvent.ACTION_POINTER_DOWN)){
            }else if(action==MotionEvent.ACTION_CANCEL) {
                if(p==actionIndex)
                    points.tempMap.eraseColor(Color.TRANSPARENT);
            }else{
                Log.e("motion event", "action not handled : " + event.getAction() + ":" + (event.getAction()==MotionEvent.ACTION_POINTER_3_DOWN) + ":" + MotionEvent.ACTION_POINTER_3_DOWN);
            }
            previousPoints.put(pointerID, points);
        }
    }


    //@Override
    public boolean onGenericMotion(View v, MotionEvent event) {
        Log.d("derpderp", "generic motion event");
        return true;
    }

    @Override
    public boolean onCapturedPointerEvent(MotionEvent event){
        Log.d("derpderp", "pointer captured");
        return true;
    }

    public static float interpolateValue(float v1, float v2, float d){
        //return v1;
        //return (float)(v1 + (v2-v1)*d);
        return (float)(v1 + (v2-v1)*Math.sin(d * Math.PI/2));
    }

    public static void interpolateTo(SignatureTrace points, float x2, float y2, float r2){
        float x1=points.lastX;
        float y1=points.lastY;
        float r1=points.lastR;
        float xx=x1-x2;
        float yy=y1-y2;
        float sd = (float)Math.sqrt(xx*xx+yy*yy);
        float rd = (float)Math.abs(r1-r2);
        Canvas canvas = points.tempMapCanvas;
        int interpoles = 2;
        if(sd > 3.0f || rd > .5f){
            interpoles=3;
        }
        if(sd > 6.0f || rd > 1.0f){
            interpoles=5;
        }
        if(sd > 12.0f || rd > 1.5f){
            interpoles=8;
        }
        if(interpoles==2){
            Log.d("derpderp2","not doing much now");
            canvas.drawLine(x1, y1, x2, y2, signaturePaint);
            points.lastX=x2;
            points.lastY=y2;
            points.lastR=r2;
            return;

        }else{
            Log.d("derpderp2", "actually doing something");
        }
        float d = (1.0f)/(interpoles);
        float dd = 0;
        //float dy = (y2-y1)/(interpoles-1);
        //float dr = (r2-r1)/(interpoles-1);
        float lx=x1;
        float ly=y1;
        for(int i = 0; i < interpoles; i++){
            dd+=d;
            float x = interpolateValue(x1, x2, dd);
            float y = interpolateValue(y1, y2, dd);
            float dx=x-lx;
            float dy=y-ly;
            float r = interpolateValue(r1, r2, dd);
            if(Math.sqrt(dx*dx+dy*dy) < r*.1)
                canvas.drawPoint((x+lx)/2.0f, (y+ly)/2.0f, signaturePaint);
            else
                canvas.drawLine(lx, ly, x, y, signaturePaint);
            //signaturePaint.setStrokeWidth(r);
            //canvas.drawLine(lx, ly, x, y, signaturePaint);
            lx=x;
            ly=y;
        }
        points.lastX=x2;
        points.lastY=y2;
        points.lastR=r2;
    }

    public static class SignatureTraceElement{
        float x;
        float y;
        float r;
    }

    public static class SignatureTrace{
        public float lastX;
        public float lastY;
        public float lastR;
        Bitmap tempMap;
        Canvas tempMapCanvas;
    }

    public static interface SignatureViewEventListener{
        void onViewValidChange(boolean valid);
    }

}
