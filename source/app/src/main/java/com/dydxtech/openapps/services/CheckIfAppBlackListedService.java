package com.dydxtech.openapps.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.dydxtech.openapps.utils.AudioUI;
import com.dydxtech.openapps.receivers.ScreenReceiver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Copyright RSenApps 2014
 */
public class CheckIfAppBlackListedService extends Service {
    public static boolean checkingForRelockOnly = false;
    public static boolean blacklisteddetected = false;
    public static String lastLaunchedPackage = "";
    final Handler checkIfAppBlacklistedDelayed = new Handler();
    Runnable runnable;

    public CheckIfAppBlackListedService() {
    }

    public static boolean checkIfBlacklistedBecauseOfMic(Context context, String pkg) {
        return false; //don't blacklist because of mic anymore...
        /*
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("blacklist_mic", true)) {
            return false;
        }
        if (pkg.equals(context.getPackageName())) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
        }

        String[] requestedPermissions = packageInfo.requestedPermissions;
        return Arrays.asList(requestedPermissions).contains(Manifest.permission.RECORD_AUDIO);
        */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final HashSet<String> blackListedApps = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(this).getStringSet("black_listed_apps", new HashSet<String>());
        final boolean useGetTasks = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true);
        final boolean relock = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("relock", true);
        //checkingForRelockOnly = ScreenReceiver.isActivating && !useGetTasks;
        runnable = new Runnable() {

            @Override
            public void run() {

                try {

                    List<ActivityManager.RunningTaskInfo> taskInfo = am
                            .getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (checkingForRelockOnly) {
                        if (!componentInfo.getPackageName().equals(
                               getPackageName())) {
                            if (relock) {
                                try {
                                    AudioUI.lock.reenableKeyguard();
                                } catch (Exception e) {
                                }
                            }

                            stopSelf();
                        } else {
                            checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                            return;
                        }
                    }
                    if (!componentInfo.getPackageName().equals(lastLaunchedPackage) && !blackListedApps.contains(componentInfo.getPackageName()) && !checkIfBlacklistedBecauseOfMic(CheckIfAppBlackListedService.this, componentInfo.getPackageName()) && !ScreenReceiver.isActivating) {

                        if (relock && useGetTasks && !componentInfo.getPackageName().equals(lastLaunchedPackage)) {
                            lastLaunchedPackage = "";
                            try {
                                AudioUI.lock.reenableKeyguard();
                            } catch (Exception e) {
                            }
                        }
                        if (!MyService.isRunning) {
                            if (relock && useGetTasks) {
                                try {
                                    AudioUI.lock.reenableKeyguard();
                                } catch (Exception e) {
                                }
                            }
                            blacklisteddetected = false;
                            Intent i = new Intent(getApplicationContext(),
                                    MyService.class);
                            startService(i);
                        }
                         stopSelf();
                    } else {

                        checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                    }
                } catch (Exception e) {
                    checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                }
            }
        };
        checkIfAppBlacklistedDelayed.postDelayed(runnable, 2000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        checkIfAppBlacklistedDelayed.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
