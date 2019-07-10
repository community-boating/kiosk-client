package org.communityboating.kioskclient.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class PrintServiceHolder {

    private PrintService.PrinterServiceBinder printerService;

    private ServiceConnection connection;

    public void createPrintService(Context context){
        Intent intent = new Intent(context, PrintService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                printerService = (PrintService.PrinterServiceBinder)service;
                Log.d("printer", "Service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                printerService = null;
                Log.d("printer", "Service disconnected");
            }
        };
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void waitOnPrinterService(){
        int count = 0;
        while(printerService==null && count < 1000){
            count++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public PrintService.PrinterServiceBinder getPrinterService(){
        return printerService;
    }

}
