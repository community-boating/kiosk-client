package org.communityboating.kioskclient.stripe;

import android.content.Context;
import android.util.Log;

public class StripeTerminalJSInterface {
    Context context;
    public StripeTerminalJSInterface(Context context){
        this.context = context;
        Log.d("derpderp", "hello world111");
    }
    @android.webkit.JavascriptInterface
    public void helloWorld(String message){
        Log.d("wowow", "hello world + : " + message);
    }
}
