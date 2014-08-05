package com.dydxtech.openapps.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.dydxtech.openapps.receivers.ScreenReceiver;
import com.dydxtech.openapps.services.CheckIfAppBlackListedService;

public class WakeupActivity extends Activity {
    public static boolean useNewTask = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Your code here - Or if it doesn't trigger, see below
    }

    @Override
    public void onAttachedToWindow() {
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent i = new Intent(WakeupActivity.this, CheckIfAppBlackListedService.class);
        startService(i);

        ScreenReceiver.isActivating = false;

        CheckIfAppBlackListedService.blacklisteddetected = true;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true)) {
            CheckIfAppBlackListedService.checkingForRelockOnly = true;
        }

    }
}