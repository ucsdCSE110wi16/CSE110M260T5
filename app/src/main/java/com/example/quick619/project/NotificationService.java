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
 *
 * NotificationService.class (Service)
 *
 * Service is started in MainActivity onCreate()
 *
 * Purpose: Acts as the clock/controller that will check if a stock has reached it's refresh time.
 *      When a stock reaches this time, it will Call ActiveStock.PriceCheck(), if it returns true,
 *      then it updates the stock in memory, and then will call ActiveStock.ThresholdCheck(),
 *      and if it returns true then it will send a notification to the user that the threshold has
 *      been passed.
 *
 *
 */
public class NotificationService extends Service {

    private final IBinder myBinder = new NotificationBinder();
    private ArrayList<ActiveStock> list;
    private boolean Destroy = false;
    final private int oneSECONDS = 1000;
    final private int sixtySECONDS = 60000;


 /**Basic Service Methods that need to be implemented
  * onStartCommand()
  * NotificationBinder.class
  * onBind()
  * onUnbind()
  * onRebind()
  * onDestroy()
  * */

 /**Initialize an ArrayList for stocks on starting*/
    public int onStartCommand(Intent intent, int flags, int startId){

        System.out.println(startId);
        list = new ArrayList<>();
        TimerStart();
        return flags;
    }

    /**Default binder*/
    public class NotificationBinder extends Binder {
        NotificationService getService() {
            // Return this instance of NotificationService so clients can call public methods
            return NotificationService.this;
        }
    }
    /**Default onBind*/
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    /**Default onUnbind*/
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        System.out.println("UNBINDED FROM WITHIN SERVICE");
        return false;
    }
    /**Default onRebind*/
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    /**OnDestroy
     * Need to save all data before destroying service
     * Data to be saved: stocks in our list
     * After saving data, set flag to stop the infinite loop timer
     */
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref",
                                                                                MODE_PRIVATE);
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


    /**Public Methods
     *
     * AddStock()
     * RemoveStock()
     * UpdateStock()
     * onNotify()
     * TimerStart()
     *
     */

    /**Adds a stock to the list*/
    public void AddStock(ActiveStock activeStock){
        list.add(activeStock);
    }

    /**Removes a stock from the list*/
    public void RemoveStock(int position){
        list.remove(position);
    }

    /**Updates the Preferences by adding the stock at the given index*/
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

    /**Sends a notification to the user*/
    private void onNotify(int index){

        ActiveStock activeStock = list.get(index);
        System.out.println("Notification is sending for: " + activeStock.getTicker());
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

    /**The timer in the Service
     * Infinitely counts down from 100sec to 0sec
     * On each 1sec, it will go through the list of stocks and tick their counter by 1
     * More specifically it will tick their counter by the change in time since the last tick
     *      (The purpose of this is so the app can accurately count even when the app is closed,
     *      because on startup it will add however many seconds have passed since the last close)
     * If a stock finishes counting (reaches it's refresh rate) then we call it's PriceCheck()
     * and update if it changes (returns true)
     * If the price changes then we will also call its Threshold check()
     * and send notification if it returns true
     */
    private void TimerStart() {

        new CountDownTimer(sixtySECONDS * 5, oneSECONDS) {
            public void onTick(long millisUntilFinished) {
                    //System.out.println(millisUntilFinished/1000);

                    //Flag for stopping loop
                    if(Destroy) this.cancel();
                    //Check if there are any stocks to update
                    if(!list.isEmpty())
                    {
                        //Iterate through the list of stocks
                        for(int i = 0; i < list.size(); i++)
                        {
                            //Get our stock and set flags
                            boolean priceChanged = false;
                            boolean thresholdPassed;
                            ActiveStock tempStock = list.get(i);

                            System.out.println(tempStock.getCurrentCount() + "/" +
                                    tempStock.getRefresh() * 60 + tempStock);

                            //Tick the count of the stock
                            tempStock.tickCurrentCount();

                            //Check if count has reached the refreshrate
                            if(tempStock.getCurrentCount() >= tempStock.getRefresh() * 60){
                                //Reset the count to start recounting
                                tempStock.resetCurrentCount();
                                try {
                                    //Check if the price has changed
                                    priceChanged = tempStock.PriceCheck();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //If price has changed
                                if(priceChanged){
                                    //Update our stock into Preferences memory
                                    UpdateStock(i);
                                    //Check if a threshold has been passed
                                    thresholdPassed = tempStock.ThresholdCheck();
                                    //Push notification if the threshold has been passed
                                    if(thresholdPassed){
                                        onNotify(i);
                                    }
                                }


                            }
                        }
                        System.out.println("------------------------------------------------------");
                    }
            }
            //Always restart unless we call onDestroy()
            public void onFinish() {
                if(!Destroy) start();
            }
        }.start();
    }
}