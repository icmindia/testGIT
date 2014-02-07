package com.and.dualclock;

/**
 * 
 * @author GK
 *
 */

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class Main extends Activity {
	SharedPreferences myPrefs;
	RelativeLayout r1, r2, r3, r4;
	TextView date;
	TimePicker tp;
	public static long a;
	public static int smsdate = 0, mail_lock=0;

	public void onCreate(Bundle Sav) {
		super.onCreate(Sav);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setup);
		myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = myPrefs.edit();
		editor.putInt("mail_lock", mail_lock);
		editor.commit();
		date = (TextView) findViewById(R.id.date_tv);

		Thread myThread = null;

		Runnable runnable = new CountDownRunner();
		myThread = new Thread(runnable);
		myThread.start();

		TextView change = (TextView) findViewById(R.id.changetimedate_btn);
		change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(
						android.provider.Settings.ACTION_DATE_SETTINGS));

			}
		});
		r1 = (RelativeLayout) findViewById(R.id.relativeLayout2);
		r2 = (RelativeLayout) findViewById(R.id.relativeLayout3);
		r3 = (RelativeLayout) findViewById(R.id.relativeLayout4);
		r4 = (RelativeLayout) findViewById(R.id.relativeLayout5);

		r1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				a = 0;
				a = 1;
				SharedPreferences.Editor editor = myPrefs.edit();
				editor.putInt("mail_lock", mail_lock);
				editor.commit();

			}
		});

		r2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (a == 1)
					a++;
				else if (a == 3)
					a++;
			}
		});

		r3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (a == 2)
					a++;
				else if (a == 4)
					a++;
			}
		});

		r4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (a == 5) {
					startActivity(new Intent(Main.this, Setup.class));
					a = 0;

				}
			}
		});

	}

	public void doWork() {

		runOnUiThread(new Runnable() {
			public void run() {
				try {
					TextView txtCurrentTime = (TextView) findViewById(R.id.time_tv);

					java.util.Date noteTS = Calendar.getInstance().getTime();

					String delegate = "hh:mm:ss a";
					txtCurrentTime.setText(DateFormat.format(delegate, noteTS));

					String delegate1 = "EEEE, MMMM dd, yyyy";
					date.setText(DateFormat.format(delegate1, noteTS));
					mail_lock = myPrefs.getInt("mail_lock", mail_lock);
					if (mail_lock == 1) {

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

				} catch (Exception e) {
				}
			}
		});

	}

	class CountDownRunner implements Runnable {
		@Override
		public void run() {

			while (!Thread.currentThread().isInterrupted()) {
				try {
					doWork();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}
			}

		}
	}

	public void setlock(int lock) {

		mail_lock = lock;
		/*myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		 SharedPreferences.Editor editor = myPrefs.edit();
			editor.putInt("mail_lock", mail_lock);
			editor.commit();*/
		
	}

}
