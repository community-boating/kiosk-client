package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestRegistrationType;
import org.communityboating.kioskclient.stripe.StripeTerminalJSInterface;

public class NewGuestRegistrationTypeActivity extends BaseActivity<ProgressStateNewGuestRegistrationType> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_registration_type);
        WebView webView = findViewById(R.id.webviewid);
        webView.addJavascriptInterface(new StripeTerminalJSInterface(this), "stripeInterface");
        webView.loadUrl("file:///android_asset/stripe_terminal_script");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Log.d("derpderp", "hello world : " + webView.getTitle());
        //webView.addJavascriptInterface();
    }
}
