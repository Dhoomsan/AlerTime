package com.example.evo09.timetablemanager;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;


public class AlermNotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(AlermNotificationReciever.class.getSimpleName(), "Service Stops! Opss!!!");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        context.startService(new Intent(context, AlarmService.class));

        wl.release();

    }
}
