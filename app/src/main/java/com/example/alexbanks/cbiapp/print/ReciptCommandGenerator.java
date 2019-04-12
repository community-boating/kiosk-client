package com.example.alexbanks.cbiapp.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.alexbanks.cbiapp.R;
import com.starmicronics.starioextension.ICommandBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReciptCommandGenerator {

    public static void generatePrintReciptCommands(Context context, ICommandBuilder builder, String name, String cardNum){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateString = dateFormat.format(date);

        Bitmap logoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.cbi_logo_vert);

        builder.beginDocument();
        builder.append(("Community Boating Guest Ticket\n\n\n").getBytes());
        builder.appendBitmapWithAlignment(logoImage, true, 380, true, ICommandBuilder.AlignmentPosition.Center);
        builder.append(("\n").getBytes());
        //TODO: figure out why it's not respecting the alignment.  The demo app doesn't work either (although I swear it used to)
        builder.appendBarcodeWithAlignment(
                cardNum.getBytes(),
                ICommandBuilder.BarcodeSymbology.Code128,
                ICommandBuilder.BarcodeWidth.Mode1,
                40,
                true,
                ICommandBuilder.AlignmentPosition.Center
        );
        builder.append(("\n\nGuest: " + name + "\n\n").getBytes());
        builder.append(("Take this ticket\nto the Dockhouse\nto complete your rental!\n\n\n").getBytes());
        builder.append(("Printed: " + dateString + "\n\n").getBytes());
        builder.appendUnitFeed(32);


        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);

        builder.endDocument();
    }

}
