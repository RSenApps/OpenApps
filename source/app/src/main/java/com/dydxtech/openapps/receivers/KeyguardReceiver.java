package com.dydxtech.openapps.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

    }
}
