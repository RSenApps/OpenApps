package com.dydxtech.openapps.utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * Copyright RSenApps 2014
 */
public class WakelockManager {
    public static boolean timeoutAltered = false;
    private static WakeLock wakeLock;
    private static WakeLock screenOnWakelock;

    public static void acquireWakelock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OpenMic");
        wakeLock.acquire();

    }

    public static void releaseWakelock() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    public static void turnOnScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            return;
        }
        if (screenOnWakelock == null) {
            screenOnWakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Open Mic screen");
        }
        if (screenOnWakelock.isHeld()) {
            screenOnWakelock.release();
        } else {
            screenOnWakelock.acquire(15000);
        }
    }


}
