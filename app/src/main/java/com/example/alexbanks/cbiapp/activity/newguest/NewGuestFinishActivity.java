package com.example.alexbanks.cbiapp.activity.newguest;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.api.CBIAPIRequestManager;
import com.example.alexbanks.cbiapp.progress.Progress;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestName;

import org.json.JSONException;
import org.json.JSONObject;

public class NewGuestFinishActivity extends BaseActivity {

    TextView textViewLoading;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_newguest_finish);
        textViewLoading = (TextView)findViewById(R.id.textview_loading);
    }

    public void handleButtonClick(View v){
        performAction();
    }

    public void performAction(){
        textViewLoading.setText("Starting the load process...");
        final Response.ErrorListener responseErrorListener = new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                handleCardError(error);
            }
        };
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleCardCreated(response);
            }
        };
        Progress progress = new Progress();
        ProgressStateNewGuestName progressStateNewGuestName = new ProgressStateNewGuestName();
        progressStateNewGuestName.setFirstName("test_first_name");
        progressStateNewGuestName.setLastName("test_last_name");
        progress.states.add(progressStateNewGuestName);
        ProgressStateNewGuestDOB progressStateNewGuestDOB = new ProgressStateNewGuestDOB();
        progressStateNewGuestDOB.setDOBDay(15);
        progressStateNewGuestDOB.setDOBMonth(10);
        progressStateNewGuestDOB.setDOBYear(1998);
        progress.states.add(progressStateNewGuestDOB);
        ProgressStateNewGuestEmail progressStateNewGuestEmail = new ProgressStateNewGuestEmail();
        progressStateNewGuestEmail.setEmail("test_email@test.com");
        progress.states.add(progressStateNewGuestEmail);
        try {
            CBIAPIRequestManager.getInstance(this).callCreateNewUserAndCard(this.progress, responseListener, responseErrorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleCardCreated(JSONObject response){
        textViewLoading.setText("Created user/card well successfully");
    }

    public void handleCardError(VolleyError volleyError){
        textViewLoading.setText("Failed to create user/card");
    }

}
