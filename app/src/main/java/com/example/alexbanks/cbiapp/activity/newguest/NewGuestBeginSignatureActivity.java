package com.example.alexbanks.cbiapp.activity.newguest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;

public class NewGuestBeginSignatureActivity extends BaseActivity{

    private Bitmap bitmap;

    ImageView signatureView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_signature);
        bitmap = Bitmap.createBitmap(800, 480, Bitmap.Config.ARGB_8888);
        signatureView = findViewById(R.id.signature_image_view);
        signatureView.setClickable(true);
        //signatureView.setFocusable(View.FOCUSABLE);
        //signatureView.setOnClickListener(this);
        //signatureView.setOnDragListener(this);
        Log.d("dragged", "derpa" + (signatureView==null));
    }

    public void updateImageView(){
        signatureView.setImageBitmap(bitmap);
    }

    /*@Override
    public boolean onClick(View v) {
        Log.d("dragged", "dragged");
        switch(v.getId()){
            case R.id.signature_image_view:
                double width = v.getWidth();
                double height = v.getHeight();
                int xP = (int)(event.getX()/width * signatureView.getWidth());
                int yP = (int)(event.getY()/height * signatureView.getHeight());
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                Canvas canvas = new Canvas(bitmap);

                canvas.drawPoint(xP, yP, paint);
                updateImageView();
                break;
        }
        return true;
    }*/
}
