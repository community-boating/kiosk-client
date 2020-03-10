package org.communityboating.kioskclient.activity.payment;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.progress.newguest.ProgressStateStripeTerminalPayment;
import org.communityboating.kioskclient.stripe.StripeTerminalJSInterface;

public class StripeTerminalPaymentProcessorActivity extends BaseActivity<ProgressStateStripeTerminalPayment> {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment_stripe_terminal);
        WebView webView = (WebView)findViewById(R.id.webviewid);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new StripeTerminalJSInterface(this), "stripeInterface");
        webView.loadUrl("file:///android_asset/stripe_terminal_script");
        Log.d("derpderp", "hello world : " + webView.getTitle());
    }
}
