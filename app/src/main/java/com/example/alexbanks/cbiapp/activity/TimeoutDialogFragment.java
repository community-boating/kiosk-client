package com.example.alexbanks.cbiapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class TimeoutDialogFragment extends DialogFragment {

    private TimeoutDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are You Still There?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.handleTimeoutPromptYes(TimeoutDialogFragment.this);
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.handleTimeoutPromptNo(TimeoutDialogFragment.this);
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (TimeoutDialogListener)context;
        }catch(ClassCastException e){
            Log.e("cbiapp", "owner of this fragment must inherit its listener methods");
            throw new RuntimeException(e);
        }
    }

    public static interface TimeoutDialogListener {
        public void handleTimeoutPromptYes(DialogFragment fragment);
        public void handleTimeoutPromptNo(DialogFragment fragment);
    }

}
