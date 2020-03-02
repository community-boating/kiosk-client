package org.communityboating.kioskclient.stripe;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stripe.stripeterminal.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.callable.ConnectionTokenProvider;
import com.stripe.stripeterminal.model.external.ConnectionTokenException;

import org.communityboating.kioskclient.api.CBIAPIRequestManager;

public class TokenProvider implements ConnectionTokenProvider {
    @Override
    public void fetchConnectionToken(final ConnectionTokenCallback callback) {
        try {
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    callback.onSuccess(response);
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFailure(new ConnectionTokenException("Volley network error", error));
                }
            };
            CBIAPIRequestManager.getExistingInstance().callRetrieveStripeToken(listener, errorListener);
        } catch (Exception e) {
            callback.onFailure(
                    new ConnectionTokenException("Failed to fetch connection token", e));
        }
    }
}
