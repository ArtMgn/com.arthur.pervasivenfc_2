/**
 *  @author Arthur Magnien
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.WriteActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.arthur.pervasivenfc.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class WriteSetting extends Activity {
	
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
	//UI objects//
	//the seek bar variable
	private SeekBar brightbar;

	// a variable to store the system brightness
	private int brightness;
	//the content resolver used as a handle to the system's settings
	private ContentResolver cResolver;
		//a window object, that will store a reference to the current window
	private Window window;


    @SuppressLint({ "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_setting);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        ctx=this;
		Button btnWrite = (Button) findViewById(R.id.button1);
		//final TextView message = (TextView)findViewById(R.id.edit_message);
		//get the seek bar from main.xml file
	    brightbar = (SeekBar) findViewById(R.id.seekBar1);
	    
	    //get the content resolver
	    cResolver = getContentResolver();
	    
	    //get the current window
	    window = getWindow();
	    
	    //seek bar settings//
	    //sets the range between 0 and 255
	    brightbar.setMax(255);
	    //set the seek bar progress to 1
	    brightbar.setKeyProgressIncrement(1);
		
	    try 
        {
        	//get the current system brightness
        	brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
		} 
        catch (SettingNotFoundException e) 
		{
        	//throw an error case it couldn't be retrieved
			Log.e("Error", "Cannot access system brightness");
			e.printStackTrace();
		}
		
	  //sets the progress of the seek bar based on the system's brightness
	  brightbar.setProgress(brightness);
	  
	//register OnSeekBarChangeListener, so it can actually change values
			brightbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
			{
				public void onStartTrackingTouch(SeekBar seekBar) 
				{
				}
				
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
				{
					//sets the minimal brightness level
					//if seek bar is 20 or any value below
					if(progress<=20)
					{
						//set the brightness to 20
						brightness=20;
					}
					else //brightness is greater than 20
					{
						//sets brightness variable based on the progress bar 
						brightness = progress;
					}
				}

				public void onStopTrackingTouch(SeekBar seekBar) {
					//set the system brightness using the brightness variable value
					Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
					
					//preview brightness changes at this window
					//get the current window attributes
					LayoutParams layoutpars = window.getAttributes();
					//set the brightness of this window
					layoutpars.screenBrightness = brightness / (float)255;
					//apply attribute changes to this window
					window.setAttributes(layoutpars);
					
				}
			});
		

		btnWrite.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) {
				try {
					if(mytag==null){
						Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
					}else{
						//Write TNF_WELL_KNOWN with uri
						write(String.valueOf(brightness), mytag);
						//Write customize mime with content
						//write(message.getText().toString(),mytag);
						Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_LONG ).show();
					}
				} catch (IOException e) {
					Toast.makeText(ctx, ctx.getString(R.string.error_writing), Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				} catch (FormatException e) {
					Toast.makeText(ctx, ctx.getString(R.string.error_writing) , Toast.LENGTH_LONG ).show();
					e.printStackTrace();
				}
			}
		});
		
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter == null) {
			// NFC is not available
			finish();
			return;
		}
		if(!adapter.isEnabled()) {
			// NFC is disabled
			finish();
			return;
		}
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
    }
    
	private void write(String text, Tag tag) throws IOException, FormatException {

		String text2 = "tags.to/test";
		NdefRecord[] records = {  createWellKnownRecord(text2), createMimeRecord("0/set/" + text) };
		NdefMessage  message = new NdefMessage(records);
		// Get an instance of Ndef for the tag.
		Ndef ndef = Ndef.get(tag);
		// Enable I/O
		ndef.connect();
		// Write the message
		ndef.writeNdefMessage(message);
		// Close the connection
		ndef.close();
	}
	
	private NdefRecord createWellKnownRecord(String text) throws UnsupportedEncodingException {
		
		byte[] textBytes  = text.getBytes(Charset.forName("US-ASCII"));
		int    textLength = textBytes.length;
		byte[] payload    = new byte[1 + textLength ];
		
        payload[0] = 0x03;
        
		// copy textbytes into payload
        java.lang.System.arraycopy(textBytes, 0, payload, 1, textLength);

		//Write an URL
		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN ,  NdefRecord.RTD_URI,   new byte[0] , payload);
		
		//Write a text
		//NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN ,  NdefRecord.RTD_TEXT,  new byte[0], payload);
		
		return recordNFC;
	}

	private NdefRecord createMimeRecord(String text) throws UnsupportedEncodingException {
		String lang       = "";
		byte[] textBytes  = text.getBytes();
		byte[] langBytes  = lang.getBytes("US-ASCII");
		int    langLength = langBytes.length;
		int    textLength = textBytes.length;
		byte[] payload    = new byte[1 + langLength + textLength];

		// set status byte (see NDEF spec for actual bits)
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		java.lang.System.arraycopy(langBytes, 0, payload, 1,              langLength);
		java.lang.System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		//Define new MIME
		String mimeType = "test";
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
		NdefRecord recordNFC = new NdefRecord( NdefRecord.TNF_MIME_MEDIA,
				mimeBytes,  new byte[0], payload);
		return recordNFC;
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		//Toast.makeText(ctx, ctx.getString(R.string.app_name) , Toast.LENGTH_LONG ).show();
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			//Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		WriteModeOff(); //disableForegroundDispatch
	}

	@Override
	public void onResume(){
		super.onResume();
		//enableForegroundDispatch
		WriteModeOn();
		
		}

	private void WriteModeOn(){
		writeMode = true;
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
	}

	private void WriteModeOff(){
		writeMode = false;
		adapter.disableForegroundDispatch(this);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_write_string, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
