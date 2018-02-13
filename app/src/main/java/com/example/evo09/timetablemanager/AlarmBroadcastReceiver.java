package com.example.evo09.timetablemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class InstallBroadcastReceiver extends BroadcastReceiver {

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
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
                wl.acquire();

                context.startService(new Intent(context, AlarmService.class));

                wl.release();
            }
        }
    }
}