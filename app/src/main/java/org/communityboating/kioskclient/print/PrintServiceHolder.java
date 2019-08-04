package org.communityboating.kioskclient.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;
import android.widget.Toast;

import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.IConnectionCallback;

public class PrintServiceHolder {

    private PrintService.PrinterServiceBinder printerService;

    private ServiceConnection connection;

    private final Object lock = new Object();

    public void createPrintService(Context context){
        Intent intent = new Intent(context, PrintService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                synchronized (lock) {
                    printerService = (PrintService.PrinterServiceBinder) service;
                    lock.notifyAll();
                }
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

    public void destroyPrintService(Context context){
        if(connection != null) {
            context.unbindService(connection);
            connection = null;
        }
    }

    public void waitForService(){
        synchronized (lock) {
            while (printerService == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void waitAndSendCommands(final ICommandBuilder commandBuilder, final PrinterManager.SendCommandsCallback callback){
        final Thread waitThread = new Thread(){
            @Override
            public void run(){
                PrintServiceHolder.this.waitForService();
                PrintServiceHolder.this.printerService.sendCommands(commandBuilder, callback);
            }
        };
        waitThread.start();
    }

    public PrintService.PrinterServiceBinder getPrinterService(){
        return printerService;
    }

}
