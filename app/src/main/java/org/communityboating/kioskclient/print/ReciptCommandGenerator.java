package org.communityboating.kioskclient.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.starmicronics.starioextension.ICommandBuilder;

import org.communityboating.kioskclient.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ReciptCommandGenerator {

    public static void generatePrintReciptCommands(Context context, ICommandBuilder builder, String name, String cardNum){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String dateString = dateFormat.format(date);

        Bitmap logoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.cbi_logo_guest);
        builder.beginDocument();
        //builder.append(("Community Boating Guest Ticket\n\n\n").getBytes());
        builder.appendLogo(ICommandBuilder.LogoSize.Normal, 0x01);
        //builder.appendBitmapWithAlignment(logoImage, true, 380, true, ICommandBuilder.AlignmentPosition.Center);
        builder.append(("\n").getBytes());
        builder.appendBarcodeWithAlignment(
                ("{B" + cardNum).getBytes(),
                ICommandBuilder.BarcodeSymbology.Code128,
                ICommandBuilder.BarcodeWidth.Mode2,
                140,
                true,
                ICommandBuilder.AlignmentPosition.Center
        );
        int length = 38;
        length -= name.length();
        if(length < 0)
            length = 0;
        char[] spaces = new char[length];
        Arrays.fill(spaces, ' ');

        builder.append(("\n\n" + name + new String(spaces) + dateString + "\n\n").getBytes());
        //builder.append(("Take this ticket\nto the Dockhouse\nto complete your rental!\n\n\n").getBytes());
        //builder.append(("Printed: " + dateString + "\n\n").getBytes());
        builder.appendUnitFeed(32);


        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);

        builder.endDocument();
    }

}
