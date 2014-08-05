package com.dydxtech.openapps.services;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.dydxtech.openapps.utils.AudioUI;
import com.dydxtech.openapps.R;
import com.dydxtech.openapps.activities.MainActivity;
import com.dydxtech.openapps.activities.StartListeningActivity;

/**
 * Copyright RSenApps 2014
 */
public class MyService extends Service {

    public static boolean isRunning = false;
    public static AudioUI ui;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("service-started"));

            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();

            Builder builder = new Builder(this);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentIntent(PendingIntent.getActivity(this, 12343, new Intent(this, MainActivity.class), 0));
            builder.setOngoing(true);
            if (prefs.getBoolean("hide_notification", false)) {
                builder.setPriority(Notification.PRIORITY_MIN);
            }
            builder.setSmallIcon(R.drawable.ic_stat_notfiy);
            builder.addAction(0, "Stop", PendingIntent.getService(this, 51251, new Intent(getApplicationContext(), MyService.class).setAction("STOP"), 0));
            builder.addAction(0, "Pause", PendingIntent.getService(getApplicationContext(), 51241, new Intent(this, MyService.class).setAction("PAUSE"), 0));
            startForeground(12342, builder.build());
            ui = new AudioUI(this);
        } else if (intent.getAction() == null) {
            return Service.START_STICKY;
        } else if (intent.getAction().equals("STOP")) {
            AudioUI.lock.reenableKeyguard();
            stopService(new Intent(this, ScreenReceiversService.class));
            stopService(new Intent(getApplicationContext(), CheckIfMusicPlayingService.class));
            stopService(new Intent(getApplicationContext(), CheckIfAppBlackListedService.class));
            stopSelf();
        } else if (intent.getAction().equals("PAUSE")) {
            Builder builder = new Builder(this);
            builder.setContentTitle("Open Mic+ Paused");
            builder.setContentText("Swipe away to stop...");
            builder.setContentIntent(PendingIntent.getActivity(this, 12343,
                    new Intent(this, MainActivity.class), 0));
            builder.setPriority(Notification.PRIORITY_MIN);
            builder.setOnlyAlertOnce(true);
            builder.setSmallIcon(R.drawable.ic_stat_notfiy);
            builder.addAction(0, "Resume", PendingIntent.getActivity(this, 51241, new Intent(this, StartListeningActivity.class), 0));
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(583535, builder.build());

            stopSelf();
            AudioUI.lock.reenableKeyguard();
            stopService(new Intent(this, ScreenReceiversService.class));
        } else if (intent.getAction().equals("GNACTIVATED")) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            Builder builder = new Builder(this);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentIntent(PendingIntent.getActivity(this, 12343,
                    new Intent(this, MainActivity.class), 0));
            builder.setOngoing(true);
            if (prefs.getBoolean("hide_notification", false)) {
                builder.setPriority(Notification.PRIORITY_MIN);
            }
            builder.setSmallIcon(R.drawable.ic_stat_notfiy);
            builder.setAutoCancel(true);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(583535, builder.build());

            stopSelf();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("service-stopped"));
        try {
            stopForeground(true);
            ui.stop();
            ui = null;
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        throw new UnsupportedOperationException();
    }
}
