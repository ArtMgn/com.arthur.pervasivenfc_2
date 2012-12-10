/**
 *  @author Arthur Magnien & Geoffrey Leveque
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.ExecuteActivity;

import java.util.List;

import com.arthur.pervasivenfc.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;

import com.arthur.pervasivenfc.MainActivity;

@TargetApi(16) //Quiet compilator
public class OpenFacebook extends Activity {

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
        
        setContentView(R.layout.activity_open_facebook);
		
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
	    Intent facebookIntent = getOpenFacebookIntent(this);
    	startActivity(facebookIntent);
	    //moveTaskToBack(true);
	        
    }
    
    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://pages/310622139051065/")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/events/310622139051065/")); //catches and opens a url to the desired page
        }
        
        /*
         * fb://root
fb://feed
fb://feed/{userID}
fb://profile
fb://profile/{userID}
fb://page/{id}
fb://group/{id}
fb://place/fw?pid={id}
fb://profile/{#user_id}/wall
fb://profile/{#user_id}/info
fb://profile/{#user_id}/photos
fb://profile/{#user_id}/mutualfriends
fb://profile/{#user_id}/friends
fb://profile/{#user_id}/fans
fb://search
fb://friends
fb://pages
fb://messaging
fb://messaging/{#user_id}
fb://online
fb://requests
fb://events
fb://places
fb://birthdays
fb://notes
fb://places
fb://groups
fb://notifications
fb://albums
fb://album/{%s}?owner={#%s}
fb://video/?href={href}
fb://post/{postid}?owner={uid}¹*/
    }
    
    //This function return all the packages installed on the device
    public void listAllActivities() throws NameNotFoundException
    {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pack : packages)
        {
            ActivityInfo[] activityInfo = getPackageManager().getPackageInfo(pack.packageName, PackageManager.GET_ACTIVITIES).activities;
            Log.i("Pranay", pack.packageName + " has total " + ((activityInfo==null)?0:activityInfo.length) + " activities");
            if(activityInfo!=null)
            {
                for(int i=0; i<activityInfo.length; i++)
                {
                    Log.i("PC", pack.packageName + " ::: " + activityInfo[i].name);
                }
            }
        }
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
		//disableForegroundDispatch
		//WriteModeOff(); 
	}
    
  	@Override
  	public void onResume(){
  		super.onResume();
  		 //enableForegroundDispatch
  		//WriteModeOn();
  		
  	}
}
