package com.and.dualclock;

/**
 * 
 * @author GK
 *
 */
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class OutgoingCallReceiver extends BroadcastReceiver {
	public static String phonenumber1 = "";
	public static int sms = 0, smscount = 0;
	StringBuffer add,final_no;
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;
		// outgoing
		if (intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER) != null) {

			String phonenumber = intent
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//			int phno_len = phonenumber.length();
//
//			phonenumber = phonenumber.substring(3, phno_len);

			IncomingCallReceiver in = new IncomingCallReceiver();
			
			try {

				final_no = new StringBuffer();
				for (int i = 0; i < phonenumber.length(); i++) {
					add = new StringBuffer();
					add.append(phonenumber.charAt(i));
					String temp = add.toString();
					boolean abc = in.validatemobile(temp);
					if (abc == true) {
						final_no.append(temp);
					}
					add = null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			phonenumber = final_no.toString();
			final Integer[] data = new Integer[] { 1, 3, 5, 7, 9 };
			Random r = new Random();

			int idx = r.nextInt(data.length);
			String text = ""+data[idx];
			long n = Long.parseLong(text);
			StringBuffer padNum = new StringBuffer();
			for (int i = 1; i <= 9; i++) {
				padNum.append(n);
				n++;
				if (n > 9)
					n = 1;
			}

			long padNum_lg = Long.parseLong(padNum.toString());

			long change_ph = Long.parseLong(phonenumber);
			change_ph += padNum_lg;
			String phone = Long.toString(change_ph);
			phonenumber1 = phone + text;
			in.phone_set(phonenumber1);
			/*boolean a = in.validatemobile(phonenumber);

			if (a == true) {

				final Integer[] data = new Integer[] { 1, 3, 5, 7, 9 };
				Random r = new Random();

				int idx = r.nextInt(data.length);
				String text = "";
				text = text + data[idx];
				Log.i("", "" + text);
				int n = Integer.parseInt(text);
				String padNum = "";
				for (int i = 1; i <= 9; i++) {
					padNum += Integer.toString(n);
					n = +1;
					if (n > 9)
						n = 1;
				}

				long padNum_lg = Long.parseLong(padNum);

				long change_ph = Long.parseLong(phonenumber);
				change_ph += padNum_lg;
				String phone = Long.toString(change_ph);

				phonenumber1 = phone + text;
				// IncomingCallReceiver in = new IncomingCallReceiver();
				in.phone_set(phonenumber1);
				Log.i("out", "" + phonenumber1);
			} else {
				phonenumber1 = "123456";
				Log.i("out", "" + phonenumber1);
				// IncomingCallReceiver in = new IncomingCallReceiver();
				in.phone_set(phonenumber1);
			}*/

		}

	}

	public void setsms(int sms1) {
		sms = sms1;
	}

}