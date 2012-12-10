/**
 *  @author Arthur Magnien & Geoffrey Leveque
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.WriteActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
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
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arthur.pervasivenfc.R;

public class WriteCall extends Activity {
	
	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;

    @SuppressLint({ "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_call);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        ctx=this;
		Button btnWrite = (Button) findViewById(R.id.button);
		final TextView message = (TextView)findViewById(R.id.edit_message);

		btnWrite.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) {
				try {
					if(mytag==null){
						Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
					}else{
						//Write TNF_WELL_KNOWN with uri
						write(message.getText().toString(), mytag);
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
		NdefRecord[] records = {  createWellKnownRecord(text2), createMimeRecord("0/cal/" + text) };
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
		System.arraycopy(textBytes, 0, payload, 1, textLength);

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
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

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

	@SuppressWarnings("unused")
	private void toast(String text) {
		Toast.makeText(ctx, ctx.getString(R.string.hello_world) , Toast.LENGTH_LONG ).show();
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
