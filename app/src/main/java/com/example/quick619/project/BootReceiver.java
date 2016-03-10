package com.example.quick619.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ty on 3/8/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Messaged Received", "OHH");
        context.startService(new Intent(context, NotificationService.class));
        System.out.println("SYSTEM HAS BOOTED");
    }
}
