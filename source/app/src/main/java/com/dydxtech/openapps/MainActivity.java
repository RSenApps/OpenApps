package com.dydxtech.openapps;

import android.animation.Animator;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends Activity implements SpeechRecognizerListener {

    private Intent myServiceIntent;

    private BroadcastReceiver manualActivationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    private BroadcastReceiver serviceStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myServiceIntent = new Intent(getApplicationContext(), MyService.class);
        toggleService();
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        Drawable d = wpm.getDrawable ();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/gg.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {}
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUI();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStoppedReceiver, new IntentFilter("service-stopped"));
        LocalBroadcastManager.getInstance(this).registerReceiver(manualActivationReceiver, new IntentFilter("service-started"));
    }

    private void setupUI() {
    }

    protected void toggleService() {
        if (MyService.isRunning) {
            stopService();
        } else {
            startService();
        }
    }

    private void startService() {
        AudioUI.activationCount = 0;

        startService(new Intent(this, ScreenReceiversService.class));
        startService(myServiceIntent);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("lastStartedTime", System.currentTimeMillis()).commit();
    }

    private void stopService() {
        stopService(new Intent(this, ScreenReceiversService.class));
        stopService(new Intent(getApplicationContext(), CheckIfMusicPlayingService.class));

        try {
            AudioUI.lock.reenableKeyguard();
        } catch (Exception e) {
        }

        stopService(myServiceIntent);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(manualActivationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStoppedReceiver);
        super.onPause();
    }

    @Override
    public boolean onHeard(List<String> heard) {
        Toast.makeText(this, "Head hot phrase", Toast.LENGTH_LONG).show();
        return false;
    }
}