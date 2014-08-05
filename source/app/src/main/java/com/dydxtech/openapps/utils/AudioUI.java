package com.dydxtech.openapps.utils;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.dydxtech.openapps.activities.LaunchActivity;
import com.dydxtech.openapps.activities.WakeupActivity;
import com.dydxtech.openapps.receivers.KeyguardReceiver;
import com.dydxtech.openapps.receivers.ScreenReceiver;
import com.dydxtech.openapps.services.CheckIfAppBlackListedService;
import com.dydxtech.openapps.services.MyService;
import com.dydxtech.openapps.speech.GoogleSpeechRecognizer;
import com.dydxtech.openapps.speech.SpeechRecognizer;

import java.util.ArrayList;

/**
 * Copyright RSenApps 2014
 * all ui speaking/listening/waving...etc. handled
 *
 * @author Ryan
 */
public class AudioUI {

    public static KeyguardLock lock;
    public static int activationCount = 0;
    private SpeechRecognizer speechRecognizer;
    // activation methods
    private boolean listenHotword = false;
    private Context context;
    private boolean onPhoneCall = false;

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                stopListening();
                onPhoneCall = true;
            } else if (state == TelephonyManager.CALL_STATE_IDLE && onPhoneCall) { // called
                startListening();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public AudioUI(Context context) {
        this.context = context;

        setActivationMethods();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        KeyguardManager myKeyGuard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (lock == null) {
            lock = myKeyGuard.newKeyguardLock("openmic");
        }
        if ((listenHotword) && prefs.getBoolean("listen_screen_off", false)) {
            WakelockManager.acquireWakelock(context);
        }

        if (listenHotword) {
            speechRecognizer = new GoogleSpeechRecognizer(context, this);
        }

        TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void stopListening() {
        if (listenHotword) {
            speechRecognizer.stopListening();
        }
    }

    public void startListening() {
        if (listenHotword) {
            speechRecognizer.startListening();
        }
    }

    private void setActivationMethods() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        listenHotword = prefs.getBoolean("listenHotword", true);

    }

    public void stop() {
        WakelockManager.releaseWakelock();
        if (listenHotword) {
            speechRecognizer.stop();
        }
        TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void HotwordHeard() {
        activationCount++;
        ScreenReceiver.isActivating = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Intent i = new Intent(context, CheckIfAppBlackListedService.class);
        context.stopService(i);

        WakeupActivity.useNewTask = false;
        if (!ScreenReceiver.isScreenOn || KeyguardReceiver.keyguardEnabled) {
            WakeupActivity.useNewTask = true;
            lock.disableKeyguard();
        }

        WakelockManager.turnOnScreen(context);
        if (prefs.getBoolean("use_gettasks", true)) {
            i = new Intent(context, MyService.class);
            i.setAction("GNACTIVATED");
            context.startService(i);
        }
    }
}
