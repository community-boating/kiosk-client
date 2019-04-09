package com.example.alexbanks.cbiapp.api;

import android.content.Context;
import android.os.AsyncTask;
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

public class CBIAPIRequestManager {

    public static final String CBI_API_URL_CREATE_USER = "https://api.community-boating.org/fo-kiosk/create-person";
    public static final String CBI_API_URL_CREATE_CARD = "https://api.community-boating.org/fo-kiosk/create-card";

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
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    /*
    This function will make two sequential calls to the api in order to create a user, and then a card for that user
    Pass the function a progress with all the user data populated and two callbacks
     */

    public void callCreateNewUserAndCard(Progress completeUserProgress, final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws JSONException{
        String tempTestFlag = "_FLAG_DEV_12124532";
        JSONObject requestObject = new JSONObject();
        ProgressStateNewGuestName progressStateNewGuestName = completeUserProgress.findByProgressStateType(ProgressStateNewGuestName.class);
        requestObject.put("firstName", progressStateNewGuestName.getFirstName() + tempTestFlag);
        requestObject.put("lastName", progressStateNewGuestName.getLastName() + tempTestFlag);
        ProgressStateNewGuestEmail progressStateNewGuestEmail = completeUserProgress.findByProgressStateType(ProgressStateNewGuestEmail.class);
        requestObject.put("emailAddress", progressStateNewGuestEmail.getEmail());
        ProgressStateNewGuestDOB progressStateNewGuestDOB = completeUserProgress.findByProgressStateType(ProgressStateNewGuestDOB.class);
        String dobString = progressStateNewGuestDOB.getDOBMonth() + "/" + progressStateNewGuestDOB.getDOBDay() + "/" + progressStateNewGuestDOB.getDOBYear();
        requestObject.put("Dob", dobString);
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
                Integer personID = null;
                try {
                    JSONObject cardRequestObject = new JSONObject();
                    cardRequestObject.put("personID", personID);
                    JsonObjectRequest createCardJsonObjectRequest = new JsonObjectRequest(CBI_API_URL_CREATE_CARD, cardRequestObject, responseListener, cardCreateErrorListener);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(CBI_API_URL_CREATE_USER, requestObject, userCreateListener, userCreateErrorListener);
        getRequestQueue().add(jsonObjectRequest);
    }

}
