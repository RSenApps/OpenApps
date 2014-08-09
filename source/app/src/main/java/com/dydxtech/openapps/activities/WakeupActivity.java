package com.dydxtech.openapps.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.dydxtech.openapps.R;
import com.dydxtech.openapps.receivers.ScreenReceiver;
import com.dydxtech.openapps.services.CheckIfAppBlackListedService;

public class WakeupActivity extends Activity {
    public static boolean useNewTask = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onAttachedToWindow() {
        final String pkgName = getIntent().getStringExtra("pkg_name");
        CheckIfAppBlackListedService.lastLaunchedPackage = pkgName;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                PackageManager pm = getPackageManager();
                Intent LaunchIntent = pm.getLaunchIntentForPackage(pkgName);
                startActivity(LaunchIntent);
                overridePendingTransition(R.anim.show_bottom, android.R.anim.fade_out);

            }
        };
        handler.postDelayed(runnable, 100);

    }
    @Override
    public void onPause() {
        super.onPause();
        Intent i = new Intent(WakeupActivity.this, CheckIfAppBlackListedService.class);
        startService(i);

        ScreenReceiver.isActivating = false;

        CheckIfAppBlackListedService.blacklisteddetected = false;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true)) {
            CheckIfAppBlackListedService.checkingForRelockOnly = true;
        }

    }
}