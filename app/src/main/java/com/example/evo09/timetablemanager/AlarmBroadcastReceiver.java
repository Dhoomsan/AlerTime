package com.example.evo09.timetablemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }

        if (action != null && Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String dataString = intent.getDataString();
            if (dataString != null && dataString.equals(context.getPackageName())) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
                wl.acquire();

                Intent myStarterIntent = new Intent(context, AlarmService.class);
                myStarterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(myStarterIntent);

                wl.release();
            }
        }
    }
}