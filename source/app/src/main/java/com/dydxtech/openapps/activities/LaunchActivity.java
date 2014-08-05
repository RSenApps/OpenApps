package com.dydxtech.openapps.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.dydxtech.openapps.R;
import com.dydxtech.openapps.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        ArrayList<String> userInputList = getIntent().getStringArrayListExtra(Constants.KEY_USER_INPUT);
        String topResult = userInputList.get(0);
        String hot_phrase = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.KEY_HOT_PHRASE, getResources().getString(R.string.hot_phrase));
        String appName = topResult.replace(hot_phrase, "").trim().toLowerCase();

        //Launch the app by the name
        try {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = getPackageManager();
            final List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
            for (ResolveInfo app : appList) {
                String name = app.loadLabel(pm).toString().toLowerCase();
                if (appName.startsWith(name)) {
                    final MediaPlayer mp = MediaPlayer.create(this, R.raw.heard);
                    mp.start();
                    Intent LaunchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName);
                    startActivity(LaunchIntent);
                    overridePendingTransition(R.anim.show_bottom, android.R.anim.fade_out);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }
}
