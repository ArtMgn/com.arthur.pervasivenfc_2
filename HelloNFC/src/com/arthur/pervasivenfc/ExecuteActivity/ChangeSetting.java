/**
 *  @author Arthur Magnien
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
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.WindowManager.LayoutParams;

@TargetApi(16) //Quiet compilator
public class ChangeSetting extends Activity {

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
    //a variable to store the system brightness  
    private int brightness;   
    final String EXTRA_TAG = "new_tag";
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting);
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
		contenu = intent.getStringExtra(EXTRA_TAG);
		// Change the Brightness
	    changeBrightness(contenu);
	    displayNotification(contenu);
	    //moveTaskToBack(true);

    	
	        
    }
        
	void changeBrightness(String contenu) {
    	    	 
		brightness = Integer.parseInt(contenu);
		
		Settings.System.putInt(this.getContentResolver(), 
				               Settings.System.SCREEN_BRIGHTNESS_MODE,
				               Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		
		Settings.System.putInt(this.getContentResolver(),
				               Settings.System.SCREEN_BRIGHTNESS,
				               brightness);  
    	 
		LayoutParams params = this.getWindow().getAttributes();
		
		if (brightness == 255) {
			params.screenBrightness = 1.0f;
			
		}
		else {
			params.screenBrightness = 0.2f;
		}
		
		this.getWindow().setAttributes(params);
		
		//Just display the content of the tag
    	//TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel);

    	//We change the text of the view
    	//currentRankText.setText("Setting brightness to " + contenu);
    	
    	Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() { 
             public void run() { 
                  
             } 
        }, 2000);
    	
    	Intent i = new Intent(Intent.ACTION_MAIN);
    	i.addCategory(Intent.CATEGORY_HOME);
    	startActivity(i);
		
	}
	
	void displayNotification(String contenu) {
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("Changing display setting to: " + contenu );
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
