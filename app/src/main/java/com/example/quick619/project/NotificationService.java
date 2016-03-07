package com.example.quick619.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Ty on 3/6/2016.
 */
public class NotificationService extends Service {

    private String ticker;
    private getquote quote = new getquote();
    private int refreshRate = 5;
    private double botThresh;
    private double topThresh;
    private double crossedThresh;
    private double price;
    private double change;
    private boolean passed = false;
    DecimalFormat numberFormat = new DecimalFormat("#.00");

    private ActiveStock activeStock;


    private String name = "";
    private String cityState = "";
    private String phone = "";

    public int onStartCommand(Intent intent, int flags, int startId){

        activeStock = intent.getParcelableExtra("ActiveStock");
        ticker = activeStock.getTicker();
        price = activeStock.getPrice();
        topThresh = activeStock.getUpper();
        botThresh = activeStock.getLower();
        refreshRate = activeStock.getRefresh();

        System.out.println(ticker);
        System.out.println(refreshRate);

        threshholdCheck();

        return flags;
    }


    public void threshholdCheck() {

        new CountDownTimer(refreshRate * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                    System.out.println(millisUntilFinished/1000);
            }

            public void onFinish() {
                double oldPrice = price;
                try {
                    price = quote.getprice(ticker);
                    price = Double.parseDouble(numberFormat.format(price));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (price >= topThresh) {
                    passed = true;
                    crossedThresh = topThresh;
                } else if (price <= botThresh) {
                    passed = true;
                    crossedThresh = botThresh;
                }
                if (oldPrice != price)
                {
                    try {
                        change = quote.getchange(ticker);
                        change = Double.parseDouble(numberFormat.format(change));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Send update
                     activeStock.setPrice(price);
                     activeStock.setChange(change);
                }
                //onNotify();

                if(!passed)
                    start();
                else {
                    // Send Crossed Thresh
                    System.out.println(passed);
                    System.out.println("Price: " + price + " OldPrice: " + oldPrice);
                    System.out.println("Top: " + topThresh + " Bottom: " + botThresh);
                    onNotify();
                }
            }
        }.start();
    }

    public void onNotify(){

        System.err.println("THIS IS ACTUALLY WORKING");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RSSPullService");

        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
        Context context = getApplicationContext();

        Notification.Builder builder;

        builder = new Notification.Builder (context)
                .setContentTitle(ticker)
                .setContentText(ticker + ": is at price: $" + price + " and has passed threshold of: $" + crossedThresh + ".")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
