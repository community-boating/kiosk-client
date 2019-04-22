package com.example.alexbanks.cbiapp.api;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alexbanks.cbiapp.progress.Progress;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class CBIAPIRequestManager {

    public static final String CBI_API_URL_CREATE_USER = "https://api-dev.community-boating.org/api/fo-kiosk/create-person";
    public static final String CBI_API_URL_CREATE_CARD = "https://api-dev.community-boating.org/api/fo-kiosk/create-card";

    private Context context;
    private RequestQueue requestQueue;

    private static CBIAPIRequestManager managerInstance;

    private CBIAPIRequestManager(Context context){
        this.context = context;
    }

    /*private void newRequestQueue(){
        Cache cache = new DiskBasedCache(context.getApplicationContext().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);
    }*/

    public static synchronized CBIAPIRequestManager getInstance(Context context){
        if(managerInstance == null)
            managerInstance = new CBIAPIRequestManager(context);
        return managerInstance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null) {
            //TODO remove this
            NukeSSLCerts.nuke();
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    /*
    This function will make two sequential calls to the api in order to create a user, and then a card for that user
    Pass the function a progress with all the user data populated and two callbacks
     */

    static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public void callCreateNewUserAndCard(Progress completeUserProgress, final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws JSONException{
        String tempTestFlag = "_FLAG_DEV_12124532";
        JSONObject requestObject = new JSONObject();
        ProgressStateNewGuestName progressStateNewGuestName = completeUserProgress.findByProgressStateType(ProgressStateNewGuestName.class);
        requestObject.put("firstName", progressStateNewGuestName.getFirstName() + tempTestFlag);
        requestObject.put("lastName", progressStateNewGuestName.getLastName() + tempTestFlag);
        ProgressStateNewGuestEmail progressStateNewGuestEmail = completeUserProgress.findByProgressStateType(ProgressStateNewGuestEmail.class);
        requestObject.put("emailAddress", progressStateNewGuestEmail.getEmail());
        ProgressStateNewGuestDOB progressStateNewGuestDOB = completeUserProgress.findByProgressStateType(ProgressStateNewGuestDOB.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(progressStateNewGuestDOB.getDOBYear(), progressStateNewGuestDOB.getDOBMonth() - 1, progressStateNewGuestDOB.getDOBDay());
        
        String dobString = dateFormat.format(calendar.getTime());
        //String dobString = "02/11/1983";
        requestObject.put("dob", dobString);

        Log.d("derpderpa", requestObject.toString());
        final Response.ErrorListener cardCreateErrorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
                Log.e("api error", "card creation failed");
            }
        };
        final Response.Listener<JSONObject> userCreateListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Long personID = null;
                try {
                    personID = response.getLong("personID");
                    JSONObject cardRequestObject = new JSONObject();
                    cardRequestObject.put("personID", personID);
                    JsonObjectRequest createCardJsonObjectRequest = new CBIAPIJsonObjectRequest(CBI_API_URL_CREATE_CARD, cardRequestObject, responseListener, cardCreateErrorListener);
                    getRequestQueue().add(createCardJsonObjectRequest);
                }catch(JSONException e){
                    Log.e("api error", "api error happened");
                    e.printStackTrace();
                }
            }
        };
        final Response.ErrorListener userCreateErrorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResponse(error);
                Log.e("api error", "user creation failed");
            }
        };
        JsonObjectRequest jsonObjectRequest = new CBIAPIJsonObjectRequest(CBI_API_URL_CREATE_USER, requestObject, userCreateListener, userCreateErrorListener);
        getRequestQueue().add(jsonObjectRequest);
    }

    public static final Map<String, String> cbiAPIRequestHeaders = new TreeMap<String, String>(){
        {
            put("Content-Type", "application/json");
            //put("Accept", "application/json");
            put("Am-CBI-Kiosk", "kiosk-2019-04-10_Ez4XAfPwYUMNTauLHGM8zCP9");
        }
    };

    public class CBIAPIJsonObjectRequest extends JsonObjectRequest{

        public CBIAPIJsonObjectRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public CBIAPIJsonObjectRequest(String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders(){
            return cbiAPIRequestHeaders;
        }
    }

}
