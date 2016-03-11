package com.example.quick619.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ty on 3/6/2016.
 */
public class NotificationService extends Service {

    private final IBinder myBinder = new NotificationBinder();
    private ArrayList<ActiveStock> list;
    private boolean Destroy = false;
    final private int oneSECONDS = 1000;
    final private int sixtySECONDS = 60000;



    public int onStartCommand(Intent intent, int flags, int startId){

        System.out.println(startId);
        list = new ArrayList<>();
        TimerStart();
        return flags;
    }

    public class NotificationBinder extends Binder {
        NotificationService getService() {
            // Return this instance of NotificationService so clients can call public methods
            return NotificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        System.out.println("UNBINDED FROM WITHIN SERVICE");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();

        for(int i = 0; i < list.size(); i++){
            String json = gson.toJson(list.get(i));
            prefsEditor.putString("stock" + i, json);
            System.out.println("Saving Before Destroy: " + list.get(i));
        }

        prefsEditor.apply();
        Destroy = true;
        System.out.println("Service is being stopped");
    }



    public void AddStock(ActiveStock activeStock){
        list.add(activeStock);
    }

    public void RemoveStock(int position){
        list.remove(position);
    }

    private void TimerStart() {

        new CountDownTimer(sixtySECONDS * 5, oneSECONDS) {
            public void onTick(long millisUntilFinished) {
                    //System.out.println(millisUntilFinished/1000);

                    if(Destroy) this.cancel();
                    if(!list.isEmpty())
                    {
                        for(int i = 0; i < list.size(); i++)
                        {
                            boolean priceChanged = false;
                            boolean thresholdPassed;
                            ActiveStock tempStock = list.get(i);

                            System.out.println(tempStock.getCurrentCount() + "/" + tempStock.getRefresh() * 60 + tempStock);
                            tempStock.tickCurrentCount();

                            if(tempStock.getCurrentCount() >= tempStock.getRefresh() * 60){
                                tempStock.resetCurrentCount();
                                try {
                                    priceChanged = tempStock.PriceCheck();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(priceChanged){
                                    UpdateStock(i);
                                    thresholdPassed = tempStock.ThresholdCheck();
                                    if(thresholdPassed){
                                        onNotify(i);
                                    }
                                }


                            }
                        }
                        System.out.println("------------------------------------------------------");
                    }
            }

            public void onFinish() {
                if(!Destroy) start();
            }
        }.start();
    }

    private void UpdateStock(int index){
        ActiveStock activeStock = list.get(index);
        //Updates the preferences by storing the given stock
        System.out.println("Sending Update: " + activeStock.getTicker() + "Price: " + activeStock.getPrice());

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        System.out.println(activeStock.getTicker() + "- At Index: " + activeStock.getIndex());
        String json = gson.toJson(activeStock);
        prefsEditor.putString("stock" + activeStock.getIndex(), json);
        prefsEditor.apply();

    }

    private void onNotify(int index){

        ActiveStock activeStock = list.get(index);
        System.err.println("Notification is sending for: " + activeStock.getTicker());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RSSPullService");

        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
        Context context = getApplicationContext();

        Notification.Builder builder;

        builder = new Notification.Builder (context)
                .setContentTitle(activeStock.getTicker())
                .setContentText(activeStock.getTicker() + ": " + activeStock.getPrice() + " , " + activeStock.getChange())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
