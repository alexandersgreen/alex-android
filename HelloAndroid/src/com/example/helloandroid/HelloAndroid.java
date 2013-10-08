package com.example.helloandroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class HelloAndroid extends Activity {
    
	Boolean serviceRunning;
	Boolean service2Running;

	private networkLocationLogger nll;
	private ServiceConnection nllConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        nll = ((networkLocationLogger.nllBinder)service).getService();
	        serviceRunning = true;
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        nll = null;
	        serviceRunning = false;
	    }
	};
	
	private networkLogger nl;
	private ServiceConnection nlConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        nl = ((networkLogger.nlBinder)service).getService();
	        service2Running = true;
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        nl = null;
	        service2Running = false;
	    }
	};

	
	long updateTime;
	
	TextView network,netlat,netlong,gpslat,
	         gpslong,gpsalt,nmea,sentences;
	String nmea0,nmea1,nmea2,nmea3,nmea4,
	       nmea5,nmea6,nmea7,nmea8,nmea9,
	       nmea10,nmea11,nmea12,nmea13,nmea14,
	       nmea15,nmea16,nmea17,nmea18,nmea19;
	
	GLSurfaceView opengl;
	
	Button serviceButton;
	Button serviceButton2;
	
	float red,green,blue;
	int hours,minutes,seconds;
	
	LocationManager locManager;
	LocationListener netListener;
	LocationListener gpsListener;
	NmeaListener nmeaListener;
	
	int satellites;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        
        updateTime = System.currentTimeMillis();
 
        network = (TextView) this.findViewById(R.id.network);
        netlat = (TextView) this.findViewById(R.id.netlat);
        netlong = (TextView) this.findViewById(R.id.netlong);
        gpslat = (TextView) this.findViewById(R.id.gpslat);
        gpslong = (TextView) this.findViewById(R.id.gpslong);
        gpsalt = (TextView) this.findViewById(R.id.gpsalt);
        sentences = (TextView) this.findViewById(R.id.sentences);
        nmea = (TextView) this.findViewById(R.id.nmea);
        
    	serviceRunning = false;
    	service2Running = false;
    	if(nll!=null){
    		serviceRunning = true;
    	}
    	if(nl!=null){
    		service2Running = true;
    	}
    	
    	serviceButton = (Button) this.findViewById(R.id.serviceButton);
    	if(serviceRunning){
    		serviceButton.setText(R.string.buttonStop);
    	}else{
    		serviceButton.setText(R.string.buttonStart);
    	}
    	serviceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("serviceButton","Button Pressed");
            	if(serviceRunning){
                	serviceRunning = false;
                	unbindService(nllConnection);
                	serviceButton.setText(R.string.buttonStart);
                }
                else {
                	serviceRunning = true;
                	bindService(new Intent(getBaseContext(),networkLocationLogger.class),nllConnection,Context.BIND_AUTO_CREATE);
                	serviceButton.setText(R.string.buttonStop);
                }
            }
        });
    	
    	serviceButton2 = (Button) this.findViewById(R.id.serviceButton2);
    	if(service2Running){
    		serviceButton2.setText(R.string.buttonStop);
    	}else{
    		serviceButton2.setText(R.string.buttonStart2);
    	}
    	serviceButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d("serviceButton2","Button Pressed");
            	if(service2Running){
                	service2Running = false;
                	unbindService(nlConnection);
                	serviceButton2.setText(R.string.buttonStart2);
                }
                else {
                	service2Running = true;
                	bindService(new Intent(getBaseContext(),networkLogger.class),nlConnection,Context.BIND_AUTO_CREATE);
                	serviceButton2.setText(R.string.buttonStop);
                }
            }
        });
    	
    	opengl = (GLSurfaceView) this.findViewById(R.id.opengl);
        opengl.setRenderer(new OpenGLRenderer());
        
        satellites = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
             
        nmea0 = ".\n";
        nmea1 = ".\n";
        nmea2 = ".\n";
        nmea3 = ".\n";
        nmea4 = ".\n";
        nmea5 = ".\n";
        nmea6 = ".\n";
        nmea7 = ".\n";
        nmea8 = ".\n";
        nmea9 = ".\n";
        nmea10 = ".\n";
        nmea11 = ".\n";
        nmea12 = ".\n";
        nmea13 = ".\n";
        nmea14 = ".\n";
        nmea15 = ".\n";
        nmea16 = ".\n";
        nmea17 = ".\n";
        nmea18 = ".\n";
        nmea19 = ".\n";
        
        //start the manager
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //start the gps and network listeners
        netListener = new networkLocationListener();
        gpsListener = new gpsLocationListener();
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER    , 0, 0, gpsListener);
        //start the nmea listener
        nmeaListener = new myNmeaListener();
        locManager.addNmeaListener(nmeaListener);
    }    
    
    @Override
    public void onPause(){
    	locManager.removeUpdates(netListener);
    	locManager.removeUpdates(gpsListener);
    	locManager.removeNmeaListener(nmeaListener);
    	super.onPause();
    }
    
    @Override
    public void onResume(){
    	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER    , 0, 0, gpsListener);
        locManager.addNmeaListener(nmeaListener);
        super.onResume();
    }
    
    public class gpsLocationListener implements LocationListener {
    	public void onLocationChanged(Location loc){
    		// what to do when GPS location changes
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			double alt = loc.getAltitude();
    		gpslat.setText("    Latitude: " + lat);
			gpslong.setText("    Longitude: " + lon);
			gpsalt.setText("    Altitude: " + alt);
    	}

		public void onProviderDisabled(String arg0) {
			// what to do when GPS disabled
			gpslat.setText("    Latitude: DISABLED");
			gpslong.setText("    Longitude: DISABLED");
			gpsalt.setText("    Altitude: DISABLED");
		}

		public void onProviderEnabled(String arg0) {
			// what to do when GPS enabled
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// do nothing here for now...
			}
    }
    
    public class networkLocationListener implements LocationListener {
    	public void onLocationChanged(Location loc){
    		// what to do when network location changes
    		long tempTime = updateTime;
    		updateTime = System.currentTimeMillis();
    		tempTime = updateTime - tempTime;
    		network.setText("Network Location: (" + (tempTime / 1000) + " seconds)");
    		netlat.setText("    Latitude: " + loc.getLatitude());
			netlong.setText("    Longitude: " + loc.getLongitude());
		}

		public void onProviderDisabled(String arg0) {
			// what to do when network disabled
			netlat.setText("    Latitude: DISABLED");
			netlong.setText("    Longitude: DISABLED");	
		}

		public void onProviderEnabled(String arg0) {
			// what to do when network enabled
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// do nothing here for now...
		}
    }
    
    public class myNmeaListener implements NmeaListener{
		public void onNmeaReceived(long timestamp, String sentence) {
			// when a new NMEA sentence is received
			parseNMEA(sentence);
			nmea19 = nmea18;
			nmea18 = nmea17;
			nmea17 = nmea16;
			nmea16 = nmea15;
			nmea15 = nmea14;
			nmea14 = nmea13;
			nmea13 = nmea12;
			nmea12 = nmea11;
			nmea11 = nmea10;
			nmea10 = nmea9;
			nmea9 = nmea8;
			nmea8 = nmea7;
			nmea7 = nmea6;
			nmea6 = nmea5;
			nmea5 = nmea4;
			nmea4 = nmea3;
			nmea3 = nmea2;
			nmea2 = nmea1;
			nmea1 = nmea0;
			nmea0 = sentence;
			nmea.setText(nmea0+nmea1+nmea2+nmea3+nmea4
					    +nmea5+nmea6+nmea7+nmea8+nmea9
					    +nmea10+nmea11+nmea12+nmea13+nmea14
					    +nmea15+nmea16+nmea17+nmea18+nmea19);
		}
    }
    
    
    public void parseNMEA(String sentence){
    	//possible NMEA sentences (on Hero at least):
    	//$GPGSA, $GPRMC, $GPVTG, $GPGGA
    	//$GPGSV (approx x4 compared to other sentences)
    	//$GPGSA = GPS Overall Satellite Data
    	//$GPRMC = GPS Recommended Minimum Data
    	//$GPVTG = GPS Vector Track and Speed Over Ground
    	//$GPGGA = GPS Fix Information
    	//$GPGSV = GPS Detailed Satellite Data
    	StringTokenizer tokenizer = new StringTokenizer(sentence,",");
    	if(tokenizer.hasMoreTokens() == false) return;
    	String sentenceType = tokenizer.nextToken();
    	switch(Sentence.toSentence(sentenceType)){
    		case $GPGSA: parseGPGSA(tokenizer);
    					 break;
    		case $GPRMC: parseGPRMC(tokenizer);
    					 break;
    		case $GPVTG: parseGPVTG(tokenizer);
    					 break;
    		case $GPGGA: parseGPGGA(tokenizer);
    					 break;
    		case $GPGSV: parseGPGSV(tokenizer);
    					 break;
    		default: 	sentences.setText("NMEA Parse Enum Error: " + sentenceType);
    					break;
    	}
    }
    
    public enum Sentence
    {
        $GPGSA,$GPRMC,$GPVTG,$GPGGA,$GPGSV,NOVALUE;

        public static Sentence toSentence(String str)
        {
            try {
                return valueOf(str);
            } 
            catch (Exception ex) {
                return NOVALUE;
            }
        }   
    }

    
    public void parseGPGSA(StringTokenizer st){
    	/*
    	$GPGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39
		Where:
    	     GSA      Satellite status
    	     A        Auto selection of 2D or 3D fix (M = manual) 
    	     3        3D fix - values include: 1 = no fix
    	                                       2 = 2D fix
    	                                       3 = 3D fix
    	     04,05... PRNs of satellites used for fix (space for 12) 
    	     2.5      PDOP (dilution of precision) 
    	     1.3      Horizontal dilution of precision (HDOP) 
    	     2.1      Vertical dilution of precision (VDOP)
    	     *39      the checksum data, always begins with *
		*/
    	
    }
    
    public void parseGPRMC(StringTokenizer st){
    	/*
    	$GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
		Where:
     		RMC          Recommended Minimum sentence C
    		123519       Fix taken at 12:35:19 UTC
   			A            Status A=active or V=Void.
   			4807.038,N   Latitude 48 deg 07.038' N
     		01131.000,E  Longitude 11 deg 31.000' E
     		022.4        Speed over the ground in knots
     		084.4        Track angle in degrees True
     		230394       Date - 23rd of March 1994
     		003.1,W      Magnetic Variation
    	 	*6A          The checksum data, always begins with *
    	 */
    	
    }
    
    public void parseGPVTG(StringTokenizer st){
    	/*
    	$GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*48
		Where:
        	VTG          Track made good and ground speed
        	054.7,T      True track made good (degrees)
        	034.4,M      Magnetic track made good
        	005.5,N      Ground speed, knots
        	010.2,K      Ground speed, Kilometers per hour
    	 	*48          Checksum
    	 */
    }
    
    public void parseGPGGA(StringTokenizer st){
    	/*
    	$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
		Where:
     		GGA          Global Positioning System Fix Data
     		123519       Fix taken at 12:35:19 UTC
     		4807.038,N   Latitude 48 deg 07.038' N
     		01131.000,E  Longitude 11 deg 31.000' E
     		1            Fix quality: 0 = invalid
                               		  1 = GPS fix (SPS)
                               		  2 = DGPS fix
                               		  3 = PPS fix
			       					  4 = Real Time Kinematic
			       					  5 = Float RTK
                               		  6 = estimated (dead reckoning) (2.3 feature)
			       					  7 = Manual input mode
			       					  8 = Simulation mode
     		08           Number of satellites being tracked
     		0.9          Horizontal dilution of position
     		545.4,M      Altitude, Meters, above mean sea level
     		46.9,M       Height of geoid (mean sea level) above WGS84 ellipsoid
     		(empty field) time in seconds since last DGPS update
     		(empty field) DGPS station ID number
    	 	*47          the checksum data, always begins with *
    	 	*/
    	SimpleDateFormat inputdf = new SimpleDateFormat("HHmmss.S");
    	String timeString = st.nextToken();
    	try{
    		Date time = inputdf.parse(timeString);
    		SimpleDateFormat outputdf = new SimpleDateFormat("HH:mm:ss");
    		sentences.setText(outputdf.format(time) + " (UTC)");
    		hours = time.getHours();
    		minutes = time.getMinutes();
    		seconds = time.getSeconds();
    	}catch(Exception e){
    		sentences.setText("NMEA Time Fix Unavailable");
    		}
    }

    public void parseGPGSV(StringTokenizer st){
    		//creates a Satellite instance for each satellite in view
    		//updates global map of satellites with each new sentence
    		//currently can ignore no. of sentences etc.
    		/*
    		$GPGSV,2,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75
			Where:
      			GSV          Satellites in view
      			2            Number of sentences for full data
      			1            sentence 1 of 2
      			08           Number of satellites in view
      				*
      			01           Satellite PRN number
      			40           Elevation, degrees
      			083          Azimuth, degrees
      			46           SNR - higher is better
           			for up to 4 satellites per sentence
    		 	*75          the checksum data, always begins with *
    		 */
    		 //satellites = siv.intValue();
    }
    
    public class Satellite{
    	int satellitePRN;
    	int elevation;
    	int azimuth;
    	int snr;
    	
    	public Satellite(Integer sp,Integer e,Integer a,Integer s){
    		satellitePRN = sp.intValue();
    		elevation = e.intValue();
    		azimuth = a.intValue();
    		snr = s.intValue();
    	}
    }
    
    public class OpenGLRenderer implements Renderer{

    FloatBuffer trianglesBuffer;	
    	
		public void onDrawFrame(GL10 gl) {
			//called when drawing openGL frame
			//set the colour to clear with
	        gl.glClearColor(0.0f,0.0f,0.0f, 1.0f);
	        // clear the screen
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	        
	        // draw the triangle
	        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, trianglesBuffer);
	        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 60*3);
	        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
	        int hours12;
	        if(hours>11){hours12 = hours-12;}else{hours12=hours;}
	        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, (hours12*60/12)*3, 3);
	        gl.glColor4f (0.0f, 1.0f, 0.0f, 1.0f);
	        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, minutes*3, 3);	        
	        gl.glColor4f (1.0f, 0.0f, 0.0f, 1.0f);
	        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, seconds*3, 3);
	        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			// called when frame size changes
			gl.glViewport(0, 0, w, h);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// called upon creation
			float[] triangles = circle(60,4.5f,2.5f,2.4f);
			ByteBuffer bb = ByteBuffer.allocateDirect(triangles.length*4);
	        bb.order(ByteOrder.nativeOrder());
			trianglesBuffer = bb.asFloatBuffer();
			trianglesBuffer.put(triangles);
			trianglesBuffer.position(0);
	        // left,right,bottom,top,near,far
	        gl.glOrthof(0.0f, 9.0f, 0.0f, 5.0f, -1.0f, 1.0f);
		}
		
		public float[] circle(int segments,float x, float y, float r){
			float[] circles = new float[segments*9];
			for(int i=0;i<segments;i++){
				//cos(0) = 1, sin(0) = 0
				//center
				circles[(9*i)+0] = x;
				circles[(9*i)+1] = y;
				circles[(9*i)+2] = 0.0f;
				//clockwise from center
				circles[(9*i)+3] = x+r*((float) (Math.sin((2*Math.PI*i)/(float) segments)));
				circles[(9*i)+4] = y+r*((float) (Math.cos((2*Math.PI*i)/(float) segments)));
				circles[(9*i)+5] = 0.0f;
				//clockwise from previous
				circles[(9*i)+6] = x+r*((float) (Math.sin((2*Math.PI*(i+1))/(float) segments)));
				circles[(9*i)+7] = y+r*((float) (Math.cos((2*Math.PI*(i+1))/(float) segments)));
				circles[(9*i)+8] = 0.0f;
			}
			return circles;
		}
    }
}