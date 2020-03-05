package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestBegin;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestDOB;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestRegistrationType;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestReturning;
import org.communityboating.kioskclient.progress.newguest.ProgressStateRentalBoatTypeChoose;
import org.communityboating.kioskclient.stripe.StripeTerminalJSInterface;

public class NewGuestRegistrationTypeActivity extends BaseActivity<ProgressStateNewGuestRegistrationType> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_registration_type);
        /*WebView webView = findViewById(R.id.webviewid);
        webView.addJavascriptInterface(new StripeTerminalJSInterface(this), "stripeInterface");
        webView.loadUrl("file:///android_asset/stripe_terminal_script");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Log.d("derpderp", "hello world : " + webView.getTitle());*/
        //webView.addJavascriptInterface();
    }

    public void handleExploreMembershipClick(View v){
        handleRegistrationTypeSelectionChange(ProgressStateNewGuestRegistrationType.RegistrationType.EXPLORE_MEMBERSHIP);
    }

    public void handleRentalOptionClick(View v){
        handleRegistrationTypeSelectionChange(ProgressStateNewGuestRegistrationType.RegistrationType.RENTAL_OPTIONS);
    }

    public void handleNewGuestClick(View v){
        handleRegistrationTypeSelectionChange(ProgressStateNewGuestRegistrationType.RegistrationType.NEW_GUEST);
    }

    public void handleRegistrationTypeSelectionChange(ProgressStateNewGuestRegistrationType.RegistrationType newType){
        ProgressStateNewGuestRegistrationType.RegistrationType currentType = getProgressState().getRegistrationType();
        if(currentType != newType){
            if(currentType != null)
                progress.clearAfterActiveProgressState();
            getProgressState().setRegistrationType(newType);
            switch(newType){
                case EXPLORE_MEMBERSHIP:
                    progress.states.add(new ProgressStateNewGuestDOB());
                    break;
                case RENTAL_OPTIONS:
                    progress.states.add(new ProgressStateRentalBoatTypeChoose());
                    break;
                case NEW_GUEST:
                    progress.states.add(new ProgressStateNewGuestReturning());
                    break;
            }
        }
        nextProgress();
    }

}
