package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.starmicronics.stario.StarPrinterStatus;
import com.starmicronics.starioextension.StarIoExtManager;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.activity.DialogFragmentAdminTooltip;
import org.communityboating.kioskclient.activity.DialogFragmentPrinterWarning;
import org.communityboating.kioskclient.print.PrinterManager;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;

public class NewGuestBeginActivity extends BaseActivity<ProgressStateNewGuestBegin> implements View.OnClickListener, View.OnTouchListener {

    Button beginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaaa", "ffff");
        setContentView(R.layout.layout_newguest_begin);
        beginButton=findViewById(R.id.new_guest_begin_button_begin);
        beginButton.setOnClickListener(this);
        beginButton.setOnTouchListener(this);
    }

    private class RetrievePortStatusAndDisplayFragmentCallback implements PrinterManager.RetrievePortStatusAsyncCallback{

        @Override
        public void handlePortStatusError(Exception e) {
            Log.d("derpderp", "whatwhat", e);
            displayPrinterWarningFragment(e.getMessage(), true);
        }

        @Override
        public void handlePortStatus(StarIoExtManager.PrinterStatus status, StarIoExtManager.PrinterPaperStatus paperStatus, StarIoExtManager.PrinterCoverStatus coverStatus) {
            if(status == StarIoExtManager.PrinterStatus.Online){
                if(paperStatus == StarIoExtManager.PrinterPaperStatus.NearEmpty)
                    displayPrinterWarningFragment("Printer paper nearly empty", true);
                else
                    return;
            }else if(status == StarIoExtManager.PrinterStatus.Offline){
                if(paperStatus != StarIoExtManager.PrinterPaperStatus.Ready)
                    displayPrinterWarningFragment("Bad paper status : " + paperStatus.name(), false);
                else if(coverStatus != StarIoExtManager.PrinterCoverStatus.Close)
                    displayPrinterWarningFragment("Bad cover status : " + coverStatus.name(), false);
                else
                    displayPrinterWarningFragment("Printer is offline", false);
            }else{
                displayPrinterWarningFragment("Bad printer status : " + status.name(), false);
            }
        }
    }

    private void displayPrinterWarningFragment(String description, boolean canContinue){
        Bundle arguments = new Bundle();
        arguments.putString(DialogFragmentPrinterWarning.PRINTER_WARNING_ARGUMENT_WARNING_DESCRIPTION, description);
        arguments.putBoolean(DialogFragmentPrinterWarning.PRINTER_WARNING_ARGUMENT_CAN_CONTINUE, canContinue);
        DialogFragmentPrinterWarning fragment = new DialogFragmentPrinterWarning();
        fragment.setArguments(arguments);
        displayFragment(fragment);
    }

    @Override
    public void onResume(){
        super.onResume();
        printService.waitAndGetPortStatus(new RetrievePortStatusAndDisplayFragmentCallback());
    }

    @Override
    public boolean canTimeout(){
        return false;
    }

    public void handleBeginClick(View view){
        this.nextProgress();
    }

    @Override
    public void onClick(View v) {
        this.nextProgress();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.isPressed() && event.getAction() == MotionEvent.ACTION_UP){
            long duration = event.getEventTime() - event.getDownTime();
            if(duration > 3000){
                displayFragment(new DialogFragmentAdminTooltip());
                return true;
            }
        }
        return false;
    }
}
