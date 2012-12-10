/**
 *  @author Arthur Magnien & Geoffrey Leveque
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.ExecuteActivity;

import com.arthur.pervasivenfc.MainActivity;
import com.arthur.pervasivenfc.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.widget.TextView;

@TargetApi(16) //Quiet compilator
public class ConnectWifi extends Activity {

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
    final String EXTRA_TAG = "new_tag";
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_connect_wifi);
		
		resolveIntent(this.getIntent());
			
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_change_setting, menu);
        return false;
    }
    
    @Override
	protected void onNewIntent(Intent intent){
		
			//setIntent(intent);
			//Process payload or msgs
	        resolveIntent(intent);
	        		
	}
    
    private void resolveIntent(Intent intent) {
    	
    	String contenu = null;
			 
    	// Change the view
		contenu = intent.getStringExtra(EXTRA_TAG);
	    displayNotification(contenu);
	    changeTextView(contenu);
	    connectToWifi(contenu);
	    //moveTaskToBack(true);
	        
    }
    
    void connectToWifi(String contenu) {
    	
    	String connect[] = contenu.split(":");
    	String ssid = connect[0];
    	String password = connect[1];
    	
    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = ssid;
        wc.preSharedKey = password;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
    }
    
    void changeTextView(String contenu) {
    	    	
    	//Just display the content of the tag
    	TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel);
    	
    	String connect[] = contenu.split(":");
    	String ssid = connect[0];
    	String password = connect[1];
    	
    	
    	//We change the text of the view
    	currentRankText.setText("Ssid: "+ ssid + ", Password: " + password);
    	
    }
    
	void displayNotification(String contenu) {
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("New contact added! " );
        	// Creates an explicit intent for an Activity in your app
         Intent resultIntent = new Intent(this, MainActivity.class);

        	// The stack builder object will contain an artificial back stack for the
        	// started Activity.
        	// This ensures that navigating backward from the Activity leads out of
        	// your application to the Home screen.
        	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        	// Adds the back stack for the Intent (but not the Intent itself)
        	stackBuilder.addParentStack(MainActivity.class);
        	// Adds the Intent that starts the Activity to the top of the stack
        	stackBuilder.addNextIntent(resultIntent);
        	PendingIntent resultPendingIntent =
        	        stackBuilder.getPendingIntent(
        	            0,
        	            PendingIntent.FLAG_UPDATE_CURRENT
        	        );
        	mBuilder.setContentIntent(resultPendingIntent);
        	NotificationManager mNotificationManager =
        	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        	int mId = 0;
			// mId allows you to update the notification later on.
        	mNotificationManager.notify(mId, mBuilder.build());
       
    }
    
    @Override
	public void onPause(){
		super.onPause();
	}
    
  	@Override
  	public void onResume(){
  		super.onResume();
  	}
}
