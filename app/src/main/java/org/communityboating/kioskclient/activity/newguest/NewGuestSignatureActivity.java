package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.view.View;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.SignatureView;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestSignature;

public class NewGuestSignatureActivity extends BaseActivity<ProgressStateNewGuestSignature> implements SignatureView.SignatureViewEventListener {

    private SignatureView signatureView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_signature);
        signatureView = findViewById(R.id.new_guest_signature_box);
        signatureView.setSignatureEventListener(this);
        //signatureView.setFocusable(View.FOCUSABLE);
        //signatureView.setOnClickListener(this);
        //signatureView.setOnDragListener(this);
    }

    public void handleContinueButtonPressed(View v){this.nextProgress();}

    @Override
    public void onViewValidChange(boolean valid) {
        getProgressState().setSignatureValid(valid);
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
