package com.dydxtech.openapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Copyright RSenApps 2014
 */
public abstract class SpeechRecognizer {


    public abstract void startListening();

    public abstract void stopListening();

    public abstract void stop();
}
