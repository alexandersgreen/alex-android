package com.example.helloandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class networkLogger extends Service {
	
		Boolean firstTime;
	
		SimpleDateFormat dateFormat;
		String date;
		int count = 0;
		
		File root;
		File dir;
		File log;
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		OutputStreamWriter osw;
		
	    public class nlBinder extends Binder {
	        networkLogger getService() {
	            return networkLogger.this;
	        }
	    }
	    private final IBinder mBinder = new nlBinder();

		
		@Override
		public IBinder onBind(Intent arg0) {
			return mBinder;
		}
		
    	@Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return START_STICKY;
        }
    	
		@Override
		public void onCreate(){
			super.onCreate();
			
			firstTime = true;
			count=0;
			
			//get the notification manager
			//notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			int icon = R.drawable.icon2;								// icon from resources
			CharSequence tickerText = "Network Logger";	// ticker-text
			long when = System.currentTimeMillis();					// notification time
			Context context = getApplicationContext();				// application Context
			CharSequence contentTitle = "Network Logger";  			// expanded message title
			CharSequence contentText = "Running...";				// expanded message text

			Intent notificationIntent = new Intent(this, HelloAndroid.class);
			//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

			// the next two lines initialise the Notification, using the configurations above
			Notification notification = new Notification(icon, tickerText, when);
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			//add the notification
			//notManager.notify(1,notification);
			super.startForeground(1, notification);
			
			//gate the date format and filenames
			dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		    Date dateD = new Date();
		    date = dateFormat.format(dateD);

	        Toast.makeText(this,"Network Information Logging Service Started", Toast.LENGTH_SHORT).show();	
	        
	        Timer timer = new Timer();
	        timer.scheduleAtFixedRate(new TimerTask(){
	        	public void run() {
	        		count++;
	        		String information = getInformation();
	    		    saveInformation(information);
	    		    Log.d("networkLogger","Timer Task " + count + " Executed");
	            }	        		
			}, 0, 15000);
		}
    	
		@Override
		public void onDestroy(){
			super.onDestroy();

		    try{
		    	File logz = new File(dir,"nl"+date+"log.zip");
		    	fos = new FileOutputStream(logz,true);
		    	ZipOutputStream zos = new ZipOutputStream(fos);
		    	ZipEntry doc = new ZipEntry("data.csv");
		    	zos.putNextEntry(doc);
		    	bos = new BufferedOutputStream(zos);
		    	FileInputStream fis = new FileInputStream(log);
		    	BufferedInputStream bis = new BufferedInputStream(fis);
		    	int count;
		    	byte[] data = new byte[1024];
	            while((count = bis.read(data)) != -1) {
	               bos.write(data, 0, count);
	            }
	            bis.close();
		    	fis.close();
		    	bos.close();
		    	zos.close();
		    	fos.close();
		    	log.delete();
		    }catch(Exception e){
		    	Toast.makeText(getBaseContext(),"zip error: "+e.toString(), Toast.LENGTH_LONG).show();
		    }
		    
		    Toast.makeText(this,"Network Information Logging Service Stopped", Toast.LENGTH_SHORT).show();
		}
		

	    public void saveInformation(String info){

	    	if(firstTime){
	    		String titles = "mcc,mnc,lac,cellid,measured_at";
	    		try{
				    root = Environment.getExternalStorageDirectory();
		    		dir = new File(root,"Download/");
		    		log = new File(dir,"nl"+date+"log.csv");
		    		fos = new FileOutputStream(log,true);
		    		bos = new BufferedOutputStream(fos);
		    		osw = new OutputStreamWriter(bos);
		    		osw.write(titles + "\n");
		    		osw.flush();
		    		osw.close();
		    		bos.close();
		    		fos.close();
		    	}catch(Exception e){
		    		Toast.makeText(getBaseContext(),"file write error: "+e.toString(), Toast.LENGTH_LONG).show();
			    	Toast.makeText(getBaseContext(),titles, Toast.LENGTH_SHORT).show();
		    	}
	    	}
	    	firstTime = false;
	    		
	    	try{
	    		fos = new FileOutputStream(log,true);
	    		bos = new BufferedOutputStream(fos);
	    		osw = new OutputStreamWriter(bos);
	    		osw.write(info + "\n");
	    		osw.flush();
	    		osw.close();
	    		bos.close();
	    		fos.close();
	    	}catch(Exception e){
	    		Toast.makeText(getBaseContext(),"file write error: "+e.toString(), Toast.LENGTH_LONG).show();
		    	Toast.makeText(getBaseContext(),info, Toast.LENGTH_SHORT).show();
	    	}
	    }
	    
	    public String getInformation(){
	    	//mcc,mnc,lac,cellid,measured_at
	    	int mcc = -1;
	    	int mnc = -1;
	    	int lac = -1;
	    	int cellid = -1;
	    	long measured_at = System.currentTimeMillis();
	    	
	    	TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	        String networkOperator = tel.getNetworkOperator();

	        if (networkOperator != null) {
	            mcc = Integer.parseInt(networkOperator.substring(0, 3));
	            mnc = Integer.parseInt(networkOperator.substring(3));
	        }
	        
	       GsmCellLocation location = (GsmCellLocation) tel.getCellLocation();

	       if(location != null) {
	    	   lac = location.getLac();
	    	   cellid = location.getCid();
	       }
	    	
	    	String info = mcc + "," + mnc + "," + lac + "," + cellid + "," + measured_at;
	        
	    	return info;
	    }
	    
}
