package com.kuzasystems.zoner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SyncService extends Service {
    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        new Thread(){
            public void run(){
                sync();
            }
        }.start();
        return Service.START_STICKY;//super.onStartCommand(intent,flags,startId);
    }
    public void onCreate(){
        super.onCreate();
        Toast.makeText(SyncService.this, "service count : " , Toast.LENGTH_SHORT).show();
        Thread thread = new Thread() {
            public void onRun() {
               // for ( int a = 0; a < 50; a++){
                    try {
                        Thread.sleep(1);
                        Toast.makeText(SyncService.this, "service count : " , Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
              //  }
            }
        };
        //thread.start();
    }
    public void sync(){
        try{
            while(true) {
                Thread.sleep(10000);
                Toast.makeText(SyncService.this, "service count : " , Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }
}
