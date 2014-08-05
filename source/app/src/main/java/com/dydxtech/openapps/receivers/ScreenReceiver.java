package com.dydxtech.openapps.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dydxtech.openapps.utils.AudioUI;
import com.dydxtech.openapps.services.MyService;

/**
 * Copyright RSenApps 2014
 */
public class ScreenReceiver extends BroadcastReceiver {
	public static boolean isActivating = false; // only used when onlyScreenOff
												// and to signal that Open Mic
												// will turn itself off after it
												// finishes activating

	public static boolean isScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean screenOff = prefs.getBoolean("listen_screen_off", true);
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

			try
			{
				AudioUI.lock.reenableKeyguard();
			}
			catch(Exception e) {}
			isScreenOn = false;

			if (!screenOff) {
				final Intent myServiceIntent = new Intent(context,
						MyService.class);
				context.stopService(myServiceIntent);
			} else {
				final Intent myServiceIntent = new Intent(context,
						MyService.class);
				
				context.startService(myServiceIntent);
			}
			
			
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			KeyguardReceiver.keyguardEnabled = true;
			isScreenOn = true;
			if (!screenOff) {
				final Intent myServiceIntent = new Intent(context,
						MyService.class);
				context.startService(myServiceIntent);
			}
			
		}
	}


}
