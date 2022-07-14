package org.communityboating.kioskclient.activity.newguest;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.Communication;

import org.communityboating.kioskclient.R;
import org.communityboating.kioskclient.activity.BaseActivity;
import org.communityboating.kioskclient.api.CBIAPIRequestManager;
import org.communityboating.kioskclient.config.AdminConfigProperties;
import org.communityboating.kioskclient.print.PrinterManager;
import org.communityboating.kioskclient.print.ReciptCommandGenerator;
import org.communityboating.kioskclient.progress.newguest.ProgressStateEmergencyContactName;
import org.communityboating.kioskclient.progress.newguest.ProgressStateEmergencyContactPhone;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestDOB;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestEmail;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestName;
import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestPhone;

import org.communityboating.kioskclient.progress.newguest.ProgressStateNewGuestReturning;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        printService.waitAndSendCommands(builder, new PrinterManager.SendCommandsCallback() {
            @Override
            public void handleSuccess() {
                resetHandler.postDelayed(resetProgressHandler, 3000);
                Log.d("printer", "done printing");
            }

            @Override
            public void handleError(Exception e) {
                resetHandler.postDelayed(resetProgressHandler, 3000);
                Log.e("printer", "printing failure", e);
            }
        });
        /*try {
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
        }*/
    }

    public void doPrintManualReceipt(){
        Log.d("evan", "something happening");
        ICommandBuilder builder = //PrinterManager.getCommandBuilder();
                StarIoExt.createCommandBuilder(StarIoExt.Emulation.StarPRNT);
        builder.beginDocument();
        builder.append("Please take this ticket to the front office".getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        ProgressStateNewGuestReturning newGuestReturning = this.progress.findByProgressStateType(ProgressStateNewGuestReturning.class);
        stringBuilder.append("ReturningGuest:");
        stringBuilder.append(newGuestReturning.getReturningMember());
        ProgressStateNewGuestName newGuestName = this.progress.findByProgressStateType(ProgressStateNewGuestName.class);
        stringBuilder.append("\nFirstName:");
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
        ProgressStateNewGuestPhone newGuestPhone = this.progress.findByProgressStateType(ProgressStateNewGuestPhone.class);
        stringBuilder.append("\nPhoneNumber:");
        stringBuilder.append(newGuestPhone.getPhoneNumber());
        if(!newGuestReturning.getReturningMember()) {
            ProgressStateNewGuestEmail newGuestEmail = this.progress.findByProgressStateType(ProgressStateNewGuestEmail.class);
            stringBuilder.append("\nEmail:");
            stringBuilder.append(newGuestEmail.getEmail());
            ProgressStateEmergencyContactName emergencyContactName = this.progress.findByProgressStateType(ProgressStateEmergencyContactName.class);
            stringBuilder.append("\nEmerg1Name:");
            stringBuilder.append(emergencyContactName.getECFirstName());
            stringBuilder.append(" ");
            stringBuilder.append(emergencyContactName.getECLastName());
            ProgressStateEmergencyContactPhone emergencyContactPhone = this.progress.findByProgressStateType(ProgressStateEmergencyContactPhone.class);
            stringBuilder.append("\nEmerg1Phone:");
            stringBuilder.append(emergencyContactPhone.getPhoneNumber());
        }
        builder.appendQrCode(stringBuilder.toString().getBytes(), ICommandBuilder.QrCodeModel.No2, ICommandBuilder.QrCodeLevel.M, 4);
        //builder.appendPdf417WithAlignment(data, 0, 1, ICommandBuilder.Pdf417Level.ECC0, 1, 1, ICommandBuilder.AlignmentPosition.Left);

        //builder.appendUnitFeed(32);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        printService.waitAndSendCommands(builder, new PrinterManager.SendCommandsCallback() {
            @Override
            public void handleSuccess() {
                resetHandler.postDelayed(resetProgressHandler, 3000);
                Log.d("printer", "done printing");
            }

            @Override
            public void handleError(Exception e) {
                resetHandler.postDelayed(resetProgressHandler, 3000);
                Log.e("printer", "printing failure", e);
            }
        });

        /*try {
            PrinterManager.sendCommands(this, builder, new Communication.SendCallback() {
                @Override
                public void onStatus(boolean result, Communication.Result communicateResult) {
                    Log.d("evansloop", "printed : " + result);
                    if(result)
                     //   resetHandler.postDelayed(resetProgressHandler, 3000);
                    textViewLoading.setText("Printing : " + result + " : " + communicateResult.name());
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
            Log.d("evanerror", t.getMessage());
            textViewLoading.setText(t.getMessage());
        }*/
    }

    public void performAction() {
        textViewLoading.setText("Starting the load process...");
        final Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("card status", "card failed", error);
                handleCardError(error);
                error.printStackTrace();
            }
        };
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                handleCardCreated(response);
                Log.d("card status", "card created");
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
        Log.d("card status", "card created");
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