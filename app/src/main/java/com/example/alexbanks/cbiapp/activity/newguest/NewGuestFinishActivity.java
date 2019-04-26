package com.example.alexbanks.cbiapp.activity.newguest;

import android.arch.core.util.Function;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.alexbanks.cbiapp.R;
import com.example.alexbanks.cbiapp.activity.BaseActivity;
import com.example.alexbanks.cbiapp.api.CBIAPIRequestManager;
import com.example.alexbanks.cbiapp.config.AdminConfigProperties;
import com.example.alexbanks.cbiapp.print.PrinterManager;
import com.example.alexbanks.cbiapp.print.ReciptCommandGenerator;
import com.example.alexbanks.cbiapp.progress.Progress;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestName;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.Communication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewGuestFinishActivity extends BaseActivity {

    TextView textViewLoading;

    //Spinner emulationSpinner;

    Handler resetHandler = new Handler();

    Runnable resetProgressHandler = new Runnable() {
        @Override
        public void run() {
            NewGuestFinishActivity.this.resetNewGuestProgress();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdminConfigProperties.loadProperties(this);
        setContentView(R.layout.layout_newguest_finish);
        textViewLoading = (TextView) findViewById(R.id.textview_loading);
        //emulationSpinner = (Spinner)findViewById(R.id.emulationSpinner);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<CharSequence> spinnerArray = new ArrayList(ICommandBuilder.BarcodeWidth.values().length);
        for (ICommandBuilder.BarcodeWidth e : ICommandBuilder.BarcodeWidth.values()) {
            spinnerArray.add(e.name());
        }
        performAction();
        resetHandler.postDelayed(resetProgressHandler, 15000);
        //resetHandler.removeCallbacks(resetProgressHandler);
        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, 0, spinnerArray);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //emulationSpinner.setAdapter(adapter);
    }

    public void handleButtonClick(View v) {
        //doPrintReceipt(123456789l, "Evan McCarter");
        //performAction();
        //performOtherAction();
    }

    public void doPrintReceipt(Long cardNumber, String fullName) {
        textViewLoading.setText("Starting the printing process...");
        //PrinterManager manager = PrinterManager.getInstance(this);
        //ICommandBuilder.BarcodeWidth width = ICommandBuilder.BarcodeWidth.values()[emulationSpinner.getSelectedItemPosition()];
        ICommandBuilder builder = //PrinterManager.getCommandBuilder();
                StarIoExt.createCommandBuilder(StarIoExt.Emulation.StarPRNT);
        ReciptCommandGenerator.generatePrintReciptCommands(this, builder, fullName, Long.toString(cardNumber, 10));
        try {
            PrinterManager.sendCommands(this, builder, new Communication.SendCallback() {
                @Override
                public void onStatus(boolean result, Communication.Result communicateResult) {
                    if(result)
                        resetHandler.postDelayed(resetProgressHandler, 3000);
                    textViewLoading.setText("Printing : " + result + " : " + communicateResult.name());
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
            Log.d("evanerror", t.getMessage());
            textViewLoading.setText(t.getMessage());
        }
    }

    public void performAction() {
        textViewLoading.setText("Starting the load process...");
        final Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

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

    public void handleCardCreated(JSONObject response) {
        ProgressStateNewGuestName guestName = this.progress.findByProgressStateType(ProgressStateNewGuestName.class);
        String fullName = guestName.getFirstName() + " " + guestName.getLastName();
        try {
            Long cardNumber = response.getLong("cardNumber");
            doPrintReceipt(cardNumber, fullName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textViewLoading.setText("Created user/card well successfully : " + response.toString());
    }

    public void handleCardError(VolleyError volleyError) {
        textViewLoading.setText("Failed to create user/card : " + new String(volleyError.networkResponse.data));
        volleyError.printStackTrace();
    }
}