package com.dydxtech.openapps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

public class WakeupActivity extends Activity {
    public static boolean useNewTask = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Your code here - Or if it doesn't trigger, see below
    }

    @Override
    public void onAttachedToWindow() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WakeupActivity.this, "Something happened", Toast.LENGTH_SHORT).show();
            }
        };
        handler.postDelayed(runnable, 100);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent i = new Intent(WakeupActivity.this,
                CheckIfAppBlackListedService.class);
        startService(i);

        ScreenReceiver.isActivating = false;

        CheckIfAppBlackListedService.blacklisteddetected = true;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true)) {
            CheckIfAppBlackListedService.checkingForRelockOnly = true;
        }

    }
}