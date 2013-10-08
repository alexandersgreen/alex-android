package com.alex.vmc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class morseCodeVibrate extends Activity{
    
	EditText textBox;
	Button button;
	
	NotificationManager notManager;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get the display elements
        textBox = (EditText) this.findViewById(R.id.text);
        button = (Button) this.findViewById(R.id.button);
        
        //get the notification manager
		notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			String text = textBox.getText().toString();
            	
            	int icon = R.drawable.icon;								// icon from resources
    			CharSequence tickerText = text;							// ticker-text
    			long when = System.currentTimeMillis();					// notification time
    			Context context = getApplicationContext();				// application Context
    			CharSequence contentTitle = "Morse code Vibrate";  		// expanded message title
    			CharSequence contentText = text;						// expanded message text

    			Intent notificationIntent = new Intent(getBaseContext(), morseCodeVibrate.class);
    			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    			PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent,0);

    			// the next two lines initialise the Notification, using the configurations above
    			Notification notification = new Notification(icon, tickerText, when);
    			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    			//vibrate morse code
    			notification.vibrate = vibrateMorse(text);
    			
    			notManager.notify(1,notification);
            }
        });
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	notManager.cancelAll();
    }
    
    public long[] vibrateMorse(String text){
		long[] vm = {};
		for(int i=0;i<text.length();i++){
			vm = concat(vm,vibrateMorseChar(text.charAt(i)));
		}
		return vm;
	}
	
	public long[] concat(long[] a,long[] b){
		long[] output = new long[a.length + b.length];
		for(int i=0;i<a.length;i++){
			output[i] = a[i];
		}
		for(int i=0;i<b.length;i++){
			output[i+a.length] = b[i];
		}
		return output;
	}
	
	public long[] vibrateMorseChar(char c){
		long dot = 100; 	//dot is 100ms
		long dash = 300; 	//dash is 300ms
		long s = 100;		//100ms between dots and dashes in same character
		long ls = 300;		//300ms between characters
		long ws = 400;		//700ms between words (=400+300(ls))
		switch(c){
		case 'a' : case 'A' : long[] vma = {ls,dot,s,dash}; return vma;
		case 'b' : case 'B' : long[] vmb = {ls,dash,s,dot,s,dot,s,dot}; return vmb;
		case 'c' : case 'C' : long[] vmc = {ls,dash,s,dot,s,dash,s,dot}; return vmc;
		case 'd' : case 'D' : long[] vmd = {ls,dash,s,dot,s,dot}; return vmd;
		case 'e' : case 'E' : long[] vme = {ls,dot}; return vme;
		case 'f' : case 'F' : long[] vmf = {ls,dot,s,dot,s,dash,s,dot}; return vmf;
		case 'g' : case 'G' : long[] vmg = {ls,dash,s,dash,s,dot}; return vmg;
		case 'h' : case 'H' : long[] vmh = {ls,dot,s,dot,s,dot,s,dot}; return vmh;
		case 'i' : case 'I' : long[] vmi = {ls,dot,s,dot}; return vmi;
		case 'j' : case 'J' : long[] vmj = {ls,dot,s,dash,s,dash,s,dash}; return vmj;
		case 'k' : case 'K' : long[] vmk = {ls,dash,s,dot,s,dash}; return vmk;
		case 'l' : case 'L' : long[] vml = {ls,dot,s,dash,s,dot,s,dot}; return vml;
		case 'm' : case 'M' : long[] vmm = {ls,dash,s,dash}; return vmm;
		case 'n' : case 'N' : long[] vmn = {ls,dash,s,dot}; return vmn;
		case 'o' : case 'O' : long[] vmo = {ls,dash,s,dash,s,dash}; return vmo;
		case 'p' : case 'P' : long[] vmp = {ls,dot,s,dash,s,dash,s,dot}; return vmp;
		case 'q' : case 'Q' : long[] vmq = {ls,dash,s,dash,s,dot,s,dash}; return vmq;
		case 'r' : case 'R' : long[] vmr = {ls,dot,s,dash,s,dot}; return vmr;
		case 's' : case 'S' : long[] vms = {ls,dot,s,dot,s,dot}; return vms;
		case 't' : case 'T' : long[] vmt = {ls,dash}; return vmt;
		case 'u' : case 'U' : long[] vmu = {ls,dot,s,dot,s,dash}; return vmu;
		case 'v' : case 'V' : long[] vmv = {ls,dot,s,dot,s,dot,s,dash}; return vmv;
		case 'w' : case 'W' : long[] vmw = {ls,dot,s,dash,s,dash}; return vmw;
		case 'x' : case 'X' : long[] vmx = {ls,dash,s,dot,s,dot,s,dash}; return vmx;
		case 'y' : case 'Y' : long[] vmy = {ls,dash,s,dot,s,dash,s,dash}; return vmy;
		case 'z' : case 'Z' : long[] vmz = {ls,dash,s,dash,s,dot,s,dot}; return vmz;
		case '0' : long[] vm0 = {ls,dash,s,dash,s,dash,s,dash,s,dash}; return vm0;
		case '1' : long[] vm1 = {ls,dot,s,dash,s,dash,s,dash,s,dash}; return vm1;
		case '2' : long[] vm2 = {ls,dot,s,dot,s,dash,s,dash,s,dash}; return vm2;
		case '3' : long[] vm3 = {ls,dot,s,dot,s,dot,s,dash,s,dash}; return vm3;
		case '4' : long[] vm4 = {ls,dot,s,dot,s,dot,s,dot,s,dash}; return vm4;
		case '5' : long[] vm5 = {ls,dot,s,dot,s,dot,s,dot,s,dot}; return vm5;
		case '6' : long[] vm6 = {ls,dash,s,dot,s,dot,s,dot,s,dot}; return vm6;
		case '7' : long[] vm7 = {ls,dash,s,dash,s,dot,s,dot,s,dot}; return vm7;
		case '8' : long[] vm8 = {ls,dash,s,dash,s,dash,s,dot,s,dot}; return vm8;
		case '9' : long[] vm9 = {ls,dash,s,dash,s,dash,s,dash,s,dot}; return vm9;
		case ' ' : long[] vmSpace = {ws,0}; return vmSpace;
		default : long[] vmother = {}; return vmother; //ignore other characters
		}
	}
}