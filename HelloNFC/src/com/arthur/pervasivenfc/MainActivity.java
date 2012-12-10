/**
 *  @author Arthur Magnien & Geoffrey Leveque
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc;

import com.arthur.pervasivenfc.R;

import com.arthur.pervasivenfc.WriteActivity.WriteBluetooth;
import com.arthur.pervasivenfc.WriteActivity.WriteCall;
import com.arthur.pervasivenfc.WriteActivity.WriteCamera;
import com.arthur.pervasivenfc.WriteActivity.WriteFacebook;
import com.arthur.pervasivenfc.WriteActivity.WriteId;
import com.arthur.pervasivenfc.WriteActivity.WriteSetting;
import com.arthur.pervasivenfc.WriteActivity.WriteString;
import com.arthur.pervasivenfc.WriteActivity.WriteUri;
import com.arthur.pervasivenfc.WriteActivity.WriteWifi;
import com.arthur.pervasivenfc.WriteActivity.AddContact;
import com.arthur.pervasivenfc.ExecuteActivity.Beam;
import com.arthur.pervasivenfc.UserInterface.ClickListenerForScrolling;
import com.arthur.pervasivenfc.UserInterface.MyHorizontalScrollView;
import com.arthur.pervasivenfc.UserInterface.RayMenu;
import com.arthur.pervasivenfc.UserInterface.SizeCallbackForMenu;
import com.arthur.pervasivenfc.WriteActivity.WriteDestination;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

 public class MainActivity extends Activity{
	 
	private static final int[] ITEM_DRAWABLES = { R.drawable.string_button, R.drawable.url,
		R.drawable.brightness_button_2, R.drawable.contact_image, R.drawable.phone_image, R.drawable.google_maps_button };

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
	MyHorizontalScrollView scrollView;
	MyHorizontalScrollView scrollView_2;
	View menu;
	View menu_2;
	View app;
	Button slide_button;
	Button slide_button_2;
	boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		
		LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.horz_scroll_with_list_menu, null);
        setContentView(scrollView);

        menu = inflater.inflate(R.layout.main_2, null);
        app = inflater.inflate(R.layout.main, null);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);

        slide_button = (Button) tabBar.findViewById(R.id.slide_button);
        slide_button.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));

        final View[] children = new View[] { menu, app };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(slide_button));

		RayMenu rayMenu = (RayMenu) findViewById(R.id.ray_menu);
		final int itemCount_1 = ITEM_DRAWABLES.length;
		for (int i = 0; i < itemCount_1; i++) {
			ImageView item = new ImageView(this);
			item.setImageResource(ITEM_DRAWABLES[i]);

			final int position = i;
			rayMenu.addItem(item, new OnClickListener() {

				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
					if(position==0)
					{
						Intent intent = new Intent(ctx, WriteString.class);
					    startActivity(intent);
					}
					if(position==1)
					{
						Intent intent = new Intent(ctx, WriteUri.class);
						startActivity(intent);
					}
					if(position==2)
					{
						Intent intent = new Intent(ctx, WriteSetting.class);
						startActivity(intent);
					}
					if(position==3)
					{
						Intent intent = new Intent(ctx, AddContact.class);
						startActivity(intent);
					}
					if(position==4)
					{
						Intent intent = new Intent(ctx, WriteCall.class);
						startActivity(intent);
					}
					if(position==5)
					{
						Intent intent = new Intent(ctx, WriteDestination.class);
						startActivity(intent);
					}
				}
			});// Add a menu item
		}


		ctx=this;
				
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
	
	/** Called when the user clicks the Send button */
	public void writeString(View view) {
	    Intent intent = new Intent(this, WriteString.class);
	    startActivity(intent);
	}
	
	public void writeContact(View view) {
	    Intent intent = new Intent(this, AddContact.class);
	    startActivity(intent);
	}
	
	public void writeUri(View view) {
	    Intent intent = new Intent(this, WriteUri.class);
	    startActivity(intent);
	}
	
	public void writeBrightness(View view) {
	    Intent intent = new Intent(this, WriteSetting.class);
	    startActivity(intent);
	}
	
	public void writeCall(View view) {
	    Intent intent = new Intent(this, WriteCall.class);
	    startActivity(intent);
	}
	
	public void writeBluetooth(View view) {
	    Intent intent = new Intent(this, WriteBluetooth.class);
	    startActivity(intent);
	}
	
	public void writeWifi(View view) {
	    Intent intent = new Intent(this, WriteWifi.class);
	    startActivity(intent);
	}
	
	public void writeFacebook(View view) {
	    Intent intent = new Intent(this, WriteFacebook.class);
	    startActivity(intent);
	}
	
	public void writeCamera(View view) {
	    Intent intent = new Intent(this, WriteCamera.class);
	    startActivity(intent);
	}
	
	public void writeId(View view) {
		Intent intent = new Intent(this, WriteId.class);
	    startActivity(intent);
	}
	
	public void beam(View view) {
		Intent intent = new Intent(this, Beam.class);
		startActivity(intent);
	}
	
	public void writeDestination(View view) {
		Intent intent = new Intent(this, WriteDestination.class);
		startActivity(intent);
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

}