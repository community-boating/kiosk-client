package org.communityboating.kioskclient.api;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.progress.Progress;
import org.communityboating.kioskclient.progress.newguest.ProgressStateEmergencyContactName;
import org.communityboating.kioskclient.progress.newguest.ProgressStateEmergencyContactPhone;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestDOB;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestEmail;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestName;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestPhone;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestReturning;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class CBIAPIRequestManager {

    public static final String CBI_API_URL_CREATE_USER = "/fo-kiosk/create-person";
    public static final String CBI_API_URL_CREATE_CARD = "/fo-kiosk/create-card";

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

    public static JSONObject getCreateNewUserJSONObject(Progress completeUserProgress) throws JSONException {
        JSONObject requestObject = new JSONObject();
        ProgressStateNewGuestName progressStateNewGuestName = completeUserProgress.findByProgressStateType(ProgressStateNewGuestName.class);
        requestObject.put("firstName", progressStateNewGuestName.getFirstName());
        requestObject.put("lastName", progressStateNewGuestName.getLastName());
        ProgressStateNewGuestEmail progressStateNewGuestEmail = completeUserProgress.findByProgressStateType(ProgressStateNewGuestEmail.class);
        requestObject.put("emailAddress", progressStateNewGuestEmail.getEmail());
        ProgressStateNewGuestDOB progressStateNewGuestDOB = completeUserProgress.findByProgressStateType(ProgressStateNewGuestDOB.class);

        Calendar calendar = progressStateNewGuestDOB.getCalendarDOB();

        String dobString = dateFormat.format(calendar.getTime());
        //String dobString = "02/11/1983";
        requestObject.put("dob", dobString);

        ProgressStateNewGuestPhone progressStateNewGuestPhone = completeUserProgress.findByProgressStateType(ProgressStateNewGuestPhone.class);
        String phoneString = progressStateNewGuestPhone.getPhoneNumber();
        requestObject.put("phonePrimary", phoneString);
        ProgressStateEmergencyContactName progressStateEmergencyContactName = completeUserProgress.findByProgressStateType(ProgressStateEmergencyContactName.class);
        String emerg1NameString = progressStateEmergencyContactName.getECFirstName() + " " + progressStateNewGuestName.getLastName();
        requestObject.put("emerg1Name", emerg1NameString);
        ProgressStateEmergencyContactPhone progressStateEmergencyContactPhone = completeUserProgress.findByProgressStateType(ProgressStateEmergencyContactPhone.class);
        String emerg1PhoneString = progressStateEmergencyContactPhone.getPhoneNumber();
        requestObject.put("emerg1PhonePrimary", emerg1PhoneString);
        //String ecType = progressStateEmergencyContactName.getECType();
        //requestObject.put("emerg1Relation", ecType == null ? JSONObject.NULL : ecType);
        ProgressStateNewGuestReturning progressStateNewGuestReturning = completeUserProgress.findByProgressStateType(ProgressStateNewGuestReturning.class);
        requestObject.put("previousMember", progressStateNewGuestReturning.getReturningMember().booleanValue());
        return requestObject;
    }

    public void callCreateNewUserAndCard(Progress completeUserProgress, final Response.Listener<JSONObject> responseListener, final Response.ErrorListener errorListener) throws JSONException{
        AdminConfigProperties.loadProperties(context);
        JSONObject requestObject = getCreateNewUserJSONObject(completeUserProgress);
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
                    JsonObjectRequest createCardJsonObjectRequest = new CBIAPIJsonObjectRequest(AdminConfigProperties.getPropertyCbiApiUrl() + CBI_API_URL_CREATE_CARD, cardRequestObject, responseListener, cardCreateErrorListener);
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
        JsonObjectRequest jsonObjectRequest = new CBIAPIJsonObjectRequest(AdminConfigProperties.getPropertyCbiApiUrl() + CBI_API_URL_CREATE_USER, requestObject, userCreateListener, userCreateErrorListener);
        getRequestQueue().add(jsonObjectRequest);
    }

    public class CBIAPIJsonObjectRequest extends JsonObjectRequest{

        public CBIAPIJsonObjectRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public CBIAPIJsonObjectRequest(String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders(){
            Map<String, String> cbiAPIRequestHeaders = new TreeMap<String, String>();
            cbiAPIRequestHeaders.put("Content-Type", "application/json");
            //put("Accept", "application/json");
            if(AdminConfigProperties.hasCBIAPIKey()){
                cbiAPIRequestHeaders.put("Am-CBI-Kiosk", AdminConfigProperties.getCBIAPIKey());
            }else{
                Log.w("cbiadmin", "cbi kiosk key not set, doing dev mode");
                cbiAPIRequestHeaders.put("Am-CBI-Kiosk", "true");
            }
            return cbiAPIRequestHeaders;
        }
    }

}
