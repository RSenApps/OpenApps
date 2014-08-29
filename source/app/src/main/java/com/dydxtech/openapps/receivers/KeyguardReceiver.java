package com.dydxtech.openapps.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.dydxtech.openapps.services.MyService;

/**
 * Copyright RSenApps 2014
 */
public class KeyguardReceiver extends BroadcastReceiver {
    public static boolean keyguardEnabled = false;

    public KeyguardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        keyguardEnabled = false;
        boolean runLockScreen = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("run_lockscreen", false);
        if (!runLockScreen)
        {
            final Intent myServiceIntent = new Intent(context,
                    MyService.class);
            context.startService(myServiceIntent);
        }
    }
}
