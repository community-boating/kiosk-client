package org.communityboating.kioskclient.print;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.starmicronics.starioextension.ICommandBuilder;

import java.io.FileDescriptor;

public class PrintService extends Service {

    IBinder binder = new PrinterServiceBinder();

    private PrinterManager printerManager;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate(){
        printerManager = new PrinterManager(this);
        printerManager.connectToPrinter();
        Log.d("printer", "created printer service");
    }

    @Override
    public void onDestroy(){
        printerManager.destroy();
    }

    public class PrinterServiceBinder extends Binder {

        public void sendCommands(byte[] data, final PrinterManager.SendCommandsCallback callback) {
            printerManager.sendCommands(data, callback);
        }

        public void getPortStatus(PrinterManager.RetrievePortStatusAsyncCallback callback){
            printerManager.retrievePortStatusAsync(callback);
        }

    }

}
