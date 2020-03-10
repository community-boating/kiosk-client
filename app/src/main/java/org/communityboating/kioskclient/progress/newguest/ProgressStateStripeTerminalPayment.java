package org.communityboating.kioskclient.progress.newguest;

import android.app.Activity;

import org.communityboating.kioskclient.activity.payment.StripeTerminalPaymentProcessorActivity;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.ProgressState;

public class ProgressStateStripeTerminalPayment extends ProgressState {
    public ProgressStateStripeTerminalPayment(){
    }

    @Override
    public Class<? extends Activity> getActivityClass(){ return StripeTerminalPaymentProcessorActivity.class; }

    @Override
    public ProgressState createNextProgressState(Progress progress) {
        return new ProgressStateNewGuestDOB();//new ProgressStateNewGuestSignature();
    }
}
