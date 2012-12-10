/**
 *  @author Arthur Magnien
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.ExecuteActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.arthur.pervasivenfc.MainActivity;
import com.arthur.pervasivenfc.R;

@TargetApi(16) //Quiet compilator
public class TestProtocol extends Activity {

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
    String id1;
    String id2;
    Socket soc = null;
    String IP;
    String myIp;
    String ipToConnect;
    final String EXTRA_TAG = "new_tag";
    boolean requestComplete = false;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_display_string);
		
		resolveIntent(this.getIntent());
			
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    @Override
	protected void onNewIntent(Intent intent){
		
	        resolveIntent(intent);
	        		
	}
    
	private void resolveIntent(Intent intent) {
    	
    	String contenu = null;
			 
    	// Change the view
		contenu = intent.getStringExtra(EXTRA_TAG);
		String ids[] = contenu.split(":");
		id1 = ids[0];
		id2 = ids[1];
	    displayNotification("Id of P1: " + id1 + " Id of P2: " + id2);
	    new SendData().execute(id1,id2);
	    changeTextView("Waiting for connexion... ");	    
    }
	
	private class SendData extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... id) {
			// TODO Auto-generated method stub
			ipToConnect = postData(id[0], id[1]);
			myIp = getLocalIpAddress();
			runOnUiThread(new Runnable() {
	            public void run() {
	            	changeTextView("The ip to be connect with is " + ipToConnect +
	            			"\n My IP is " + myIp);
	            }
	        });
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestComplete = true;
			String result = ipToConnect;
			return result;
		}
		
		protected void onPostExecute(String result) {
			if(requestComplete == true){
				Intent intent2 = new Intent(getApplicationContext(), OpenCamera.class);
			    intent2.putExtra(EXTRA_TAG, result);
			    startActivity(intent2);
    		}
		}
		
		public String postData(String... id) {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://art-mgn.fr/NFCProtocol/index.php");
		    final String Id1 = id[0];
		    final String Id2 = id[1];
		    final String localIP = getLocalIpAddress();
		    String ip = null;
		    InputStream is = null;
			String result = "";

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        
		        nameValuePairs.add(new BasicNameValuePair("id1", Id1));
		        nameValuePairs.add(new BasicNameValuePair("id2", Id2));
		        nameValuePairs.add(new BasicNameValuePair("ip", localIP));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        final HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		        
		        final StatusLine status = response.getStatusLine();
		        
		        Log.d("Http Post request", "Response is : " + response);
		        Log.d("Http Post request", "Status is : " + status.toString());
		        Log.d("Http Post request", "Ids are : " + "id1: " + Id1 + " id2: " + Id2);
		        runOnUiThread(new Runnable() {
		            public void run() {
		        Context context = getApplicationContext();
				CharSequence text = "Http Response : " + status.toString() + "\n id1: " + Id1 + " id2: " + Id2;
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
		            }
		        });
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }

		  //convert response to string
		    try{
		            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                    sb.append(line + "\n");
		            }
		            is.close();
		     
		            result=sb.toString();
		    }catch(Exception e){
		            Log.e("log_tag", "Error converting result "+e.toString());
		    }
		     
		    //parse json data
		    try{
		            
		            JSONObject json_data = new JSONObject(result);
		            
		                    Log.i("log_tag","IP adress: "+json_data.getString("ip")+
		                    		", id1: "+json_data.getInt("id1")+
		                            ", id2: "+json_data.getInt("id2"));
		                    ip = json_data.getString("ip");
		    }catch(JSONException e){
		            Log.e("log_tag", "Error parsing data "+e.toString());
		    }
			return ip; 
	
		} 
		
		@SuppressWarnings("deprecation")
		public String getLocalIpAddress() {
			WifiManager myWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
			int ipAddress = myWifiInfo.getIpAddress();
			return android.text.format.Formatter.formatIpAddress(ipAddress);
		}
		
		
		
	}
    
    void changeTextView(String contenu) {
    	    	
    	//Just display the content of the tag
    	TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel);
    	
    	//We change the text of the view
    	currentRankText.setText(contenu);
    	
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
