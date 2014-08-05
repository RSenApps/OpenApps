package com.dydxtech.openapps.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;

public class CheckIfMusicPlayingService extends Service {
    final Handler checkIfMusicPlayingDelayed = new Handler();
    boolean isMusicPlaying = false;
    Runnable runnable;

    public CheckIfMusicPlayingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        isMusicPlaying = true; //google engine will only start this if music is already playing, ps will start at the beginning
        runnable = new Runnable() {

            @Override
            public void run() {
                if (!am.isMusicActive()) {

                        Intent i = new Intent(getApplicationContext(),
                                MyService.class);
                        startService(i);
                        stopSelf();

                } else {

                    isMusicPlaying = true;
                    checkIfMusicPlayingDelayed.postDelayed(this, 2000);
                }
            }
        };
        checkIfMusicPlayingDelayed.postDelayed(runnable, 2000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        checkIfMusicPlayingDelayed.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
