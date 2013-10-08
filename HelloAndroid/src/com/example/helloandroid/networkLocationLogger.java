package com.example.helloandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Notification;
//import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class networkLocationLogger extends Service {
	
		LocationManager locManager;
		//NotificationManager notManager;
		networkLocationLoggerListener netListener;
		
		SimpleDateFormat dateFormat;
		String date;
		
		File root;
		File dir;
		File log;
		
		boolean firstLocation;
		
		FileOutputStream fos;
		BufferedOutputStream bos;
		OutputStreamWriter osw;
		
	    public class nllBinder extends Binder {
	        networkLocationLogger getService() {
	            return networkLocationLogger.this;
	        }
	    }
	    private final IBinder mBinder = new nllBinder();

		
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
			
			firstLocation = true;
			
			//get the notification manager
			//notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			int icon = R.drawable.icon;								// icon from resources
			CharSequence tickerText = "Network Location Logger";	// ticker-text
			long when = System.currentTimeMillis();					// notification time
			Context context = getApplicationContext();				// application Context
			CharSequence contentTitle = "Network Location Logger";  // expanded message title
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

			//get the location manager
		    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        //start the network listener
	        netListener = new networkLocationLoggerListener();
	        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);
		
	        Toast.makeText(this,"Network Location Logging Service Started", Toast.LENGTH_SHORT).show();	
		}
    	
		@Override
		public void onDestroy(){
			super.onDestroy();

			locManager.removeUpdates(netListener);
			
			String endKML = "   </coordinates>\n"
	                      + "  </LineString>\n"	
	                      + " </Placemark>\n"
                          + "</Document>\n"
	                      + "</kml>";
			
			try{
		    	root = Environment.getExternalStorageDirectory();
    			dir = new File(root,"Download/");
    			log = new File(dir,"nll"+date+"log.kml");
    			fos = new FileOutputStream(log,true);
    			bos = new BufferedOutputStream(fos);
    			osw = new OutputStreamWriter(bos);
				osw.write(endKML);
    			osw.close();
    			bos.close();
    			fos.close();
		    }catch(Exception e){
		    	Toast.makeText(getBaseContext(),"file write error: "+e.toString(), Toast.LENGTH_LONG).show();
		    	Toast.makeText(getBaseContext(),endKML, Toast.LENGTH_SHORT).show();
    		}
		    
		    try{
		    	File logz = new File(dir,"nll"+date+"log.kmz");
		    	fos = new FileOutputStream(logz,true);
		    	ZipOutputStream zos = new ZipOutputStream(fos);
		    	ZipEntry doc = new ZipEntry("doc.kml");
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
		    	Toast.makeText(getBaseContext(),"kmz error: "+e.toString(), Toast.LENGTH_LONG).show();
		    }
		    
			//notManager.cancelAll();
		    
				Toast.makeText(this,"Network Location Logging Service Stopped", Toast.LENGTH_SHORT).show();
		}
		
		public class networkLocationLoggerListener implements LocationListener {
	    	public void onLocationChanged(Location loc){
	    		// what to do when network location changes
	    		//long updateTime = System.currentTimeMillis();
	    		double lat = loc.getLatitude();
	    		double lon = loc.getLongitude();
	    		
	    		String coord = lon + "," + lat + ",0";
	    		
	    		if(firstLocation){
	    			String startKML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    	                        + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
	    	                        + "<Document>\n"
	    	                        + " <Style id=\"redLine\">\n"
	    	                        + "  <LineStyle>\n"
	    	                        + "   <color>ff0000ff</color>\n"
	    	                        + "   <width>4</width>\n"
	    	                        + "  </LineStyle>\n"
	    	                        + " </Style>\n"
	    			                + " <Placemark>\n"
	    				            + "  <name>Start</name>\n"
	    				            + "  <Point>\n"
			                        + "   <coordinates>" + coord + "</coordinates>\n"
	    				            + "  </Point>\n"
	    							+ " </Placemark>\n"
	    			 			    + " <Placemark>\n"
	                                + "  <styleUrl>#redLine</styleUrl>\n"
	    			 			    + "  <LineString>\n"
	                                + "   <coordinates>\n";	
		    		try{
				    	root = Environment.getExternalStorageDirectory();
		    			dir = new File(root,"Download/");
		    			log = new File(dir,"nll"+date+"log.kml");
		    			fos = new FileOutputStream(log,true);
		    			bos = new BufferedOutputStream(fos);
		    			osw = new OutputStreamWriter(bos);
		    			osw.write(startKML);
		    			osw.close();
		    			bos.close();
		    			fos.close();
		    		}catch(Exception e){
		    			Toast.makeText(getBaseContext(),"file write error: "+e.toString(), Toast.LENGTH_LONG).show();
			    		Toast.makeText(getBaseContext(),startKML, Toast.LENGTH_SHORT).show();
		    		}
	    		}
	    		
	    		firstLocation = false;
	    		try{
			    	root = Environment.getExternalStorageDirectory();
	    			dir = new File(root,"Download/");
	    			log = new File(dir,"nll"+date+"log.kml");
	    			fos = new FileOutputStream(log,true);
	    			bos = new BufferedOutputStream(fos);
	    			osw = new OutputStreamWriter(bos);
	    			osw.write(coord + "\n");
	    			osw.close();
	    			bos.close();
	    			fos.close();
	    		}catch(Exception e){
	    			Toast.makeText(getBaseContext(),"file write error: "+e.toString(), Toast.LENGTH_LONG).show();
		    		Toast.makeText(getBaseContext(),coord, Toast.LENGTH_SHORT).show();
	    		}
	    	}
	    		
	    		

			public void onProviderDisabled(String arg0) {
				// what to do when network disabled
				Toast.makeText(getBaseContext(),arg0, Toast.LENGTH_LONG).show();
			}

			public void onProviderEnabled(String arg0) {
				// what to do when network enabled
				Toast.makeText(getBaseContext(),arg0, Toast.LENGTH_LONG).show();
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// do nothing here for now...
			}
	    }
		
}
