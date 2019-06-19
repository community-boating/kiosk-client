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
import com.example.alexbanks.cbiapp.api.CBIAPIRequestResponseHandler;
import com.example.alexbanks.cbiapp.config.AdminConfigProperties;
import com.example.alexbanks.cbiapp.print.PrinterManager;
import com.example.alexbanks.cbiapp.print.ReciptCommandGenerator;
import com.example.alexbanks.cbiapp.progress.Progress;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateEmergencyContactName;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateEmergencyContactPhone;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestEmail;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestName;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestPhone;
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
import java.util.Map;

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

    public void doPrintManualReceipt(){
        ICommandBuilder builder = //PrinterManager.getCommandBuilder();
                StarIoExt.createCommandBuilder(StarIoExt.Emulation.StarPRNT);
        builder.beginDocument();
        builder.append("Please take this ticket to the Front Office to complete your registration".getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        ProgressStateNewGuestName newGuestName = this.progress.findByProgressStateType(ProgressStateNewGuestName.class);
        stringBuilder.append("FirstName:");
        stringBuilder.append(newGuestName.getFirstName());
        stringBuilder.append("\nLastName:");
        stringBuilder.append(newGuestName.getLastName());
        ProgressStateNewGuestDOB newGuestDOB = this.progress.findByProgressStateType(ProgressStateNewGuestDOB.class);
        stringBuilder.append("\nDOBDay:");
        stringBuilder.append(newGuestDOB.getDOBDay());
        stringBuilder.append("\nDOBMonth:");
        stringBuilder.append(newGuestDOB.getDOBMonth());
        stringBuilder.append("\nDOBYear:");
        stringBuilder.append(newGuestDOB.getDOBYear());
        ProgressStateNewGuestEmail newGuestEmail = this.progress.findByProgressStateType(ProgressStateNewGuestEmail.class);
        stringBuilder.append("\nEmail:");
        stringBuilder.append(newGuestEmail.getEmail());
        ProgressStateNewGuestPhone newGuestPhone = this.progress.findByProgressStateType(ProgressStateNewGuestPhone.class);
        stringBuilder.append("\nPhoneNumber:");
        stringBuilder.append(newGuestPhone.getPhoneNumber());
        ProgressStateEmergencyContactName emergencyContactName = this.progress.findByProgressStateType(ProgressStateEmergencyContactName.class);
        stringBuilder.append("\nEmerg1Name:");
        stringBuilder.append(emergencyContactName.getECFirstName());
        stringBuilder.append(" ");
        stringBuilder.append(emergencyContactName.getECLastName());
        ProgressStateEmergencyContactPhone emergencyContactPhone = this.progress.findByProgressStateType(ProgressStateEmergencyContactPhone.class);
        stringBuilder.append("\nEmerg1Phone:");
        stringBuilder.append(emergencyContactPhone.getPhoneNumber());
        builder.appendPdf417WithAlignment(stringBuilder.toString().getBytes(), 0, 1, ICommandBuilder.Pdf417Level.ECC0, 2, 2, ICommandBuilder.AlignmentPosition.Center);

        builder.appendUnitFeed(32);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        
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
        Log.d("derp", "about to start creating card");
        try {
            CBIAPIRequestManager.getInstance(this).callCreateNewUserAndCard(this.progress, responseListener, responseErrorListener);
        } catch (JSONException e) {
            Log.d("bad","Card failure sadly");
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

        //textViewLoading.setText("Failed to create user/card : " + new String(volleyError.networkResponse.data));
        volleyError.printStackTrace();
        //Log.d("response", new String(volleyError.networkResponse.data));
        doPrintManualReceipt();
    }
}