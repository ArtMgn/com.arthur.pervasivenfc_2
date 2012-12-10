/**
 *  @author Geoffrey Leveque
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.ExecuteActivity;

import java.io.IOException;
import com.arthur.pervasivenfc.R;
import com.arthur.pervasivenfc.MainActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

@TargetApi(16) //Quiet compilator
public class ReadDestination extends Activity {

	private static final String TAG = null;
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
        
        setContentView(R.layout.activity_read_destination);
		
		try {
			resolveIntent(this.getIntent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
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
	        try {
				resolveIntent(intent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        		
	}
    
    private void resolveIntent(Intent intent) throws IOException {
    	
    	String contenu = null;
			 
    	// Change the view
		contenu = intent.getStringExtra(EXTRA_TAG);
	    displayNotification(contenu);
	    
	    //Display the tag content
	    //changeTextView(contenu);
	   
	    //Find the way between your current location and the destination chosen
	    addNewDestination(contenu);
	    
	    //moveTaskToBack(true);
	        
    }
    
    void changeTextView(String contenu) {
    	    	
    	//Just display the content of the tag
    	TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel_1);
    	
    	//We change the text of the view
    	currentRankText.setText(contenu);
    	
    }
    
    void addNewDestination(String contenu) {
    	
    	double hotelLat = 0;
        double hotelLng = 0;
        double lat = 0;
		double lng = 0;
		
		if (contenu.equals("Rome")) 
		{            
			//user location
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
                	
            if (location != null) {
            	System.out.println("Provider " + provider + " has been selected.");
                lat = (double) (location.getLatitude());
                lng = (double) (location.getLongitude());

                } else {
                	Log.d(TAG, "Cannot get the location");
                }
        	
        		hotelLat = 41.901571;
        		hotelLng = 12.461801;

        		/*final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ lat + "," + lng + "&daddr="+hotelLat+","+hotelLng));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
            
            else if (contenu.equals("Paris"))
            {
            	//user location
            	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                
                
        		
        		if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    lat = (double) (location.getLatitude());
                    lng = (double) (location.getLongitude());

                } else {
                    Log.d(TAG, "Cannot get the location");
                }
        	
        		hotelLat = 48.854975;
        		hotelLng = 2.34792;

        		/*final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ lat + "," + lng + "&daddr="+hotelLat+","+hotelLng));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
            
            else if (contenu.equals("Londres"))
            {
            	//user location
            	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                
               
        		
        		if (location != null) {
                    System.out.println("Provider " + provider + " has been selected.");
                    lat = (double) (location.getLatitude());
                    lng = (double) (location.getLongitude());

                } else {
                    Log.d(TAG, "Cannot get the location");
                }
        	
        		hotelLat = 51.507283;
        		hotelLng = -0.127408;

        		/*final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ lat + "," + lng + "&daddr="+hotelLat+","+hotelLng));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
            }
            
            final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ lat + "," + lng + "&daddr="+hotelLat+","+hotelLng));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }
    
	void displayNotification(String contenu) {
		
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("Content: " + contenu );
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
