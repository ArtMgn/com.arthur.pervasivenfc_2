/**
 *  @author Arthur Magnien
 *  @date November&December 2012
 *  @Project Pervasive Computing - NFC Project
 */
package com.arthur.pervasivenfc.ExecuteActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.arthur.pervasivenfc.R;
import com.arthur.pervasivenfc.R.id;
import com.arthur.pervasivenfc.R.layout;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("unused")
@TargetApi(16) //Quiet compilator
public class OpenCamera extends Activity implements OnPreparedListener {

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	static Context ctx; 
    final String EXTRA_TAG = "new_tag";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
	private static final String TAG = "MediaplayerArthur";
    private Camera mCamera;
    private ParcelFileDescriptor pfd;
	private ParcelFileDescriptor pfd2;
    private final String TAG1 = "AudioTest";
    private SurfaceHolder holder;
    private CameraView mPreview;
    private SurfaceView mPreview2;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer = null;
    private StreamProxy proxy;
    Button captureButton;
    boolean DEVELOPER_MODE = true;
    String ip = null;
    OutputStream out = null;
    NanoHTTPD server;
    private int port_ecoute_server = 12350;
    private int port_ecoute_proxy = 8888;
    private String file_name = ".";
    Socket soc = null;
    FileOutputStream fos = null;
    private String playUrl;
    private int bytesRead = 0;
	int filesize=6022386; // filesize temporary hardcoded
	InputStream is = null;
	int current = 0;
	long start = System.currentTimeMillis();
	// receive file
    byte [] mybytearray  = new byte [filesize];
    BufferedOutputStream bos;
    
    /** Prepare all the surface to display what the camera is viewing and 
     *  what will be send by the client
     *  Start also the server (if it is the method used),
     *  that way, the socket wait until client send video and then display it 
     *  by using the appropriate url*/ 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_view);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        
        mPreview2 = (SurfaceView) findViewById(R.id.video_display);
        holder = mPreview2.getHolder();    
     
    }
    
    /** Server side First method with parcel file descriptor from socket*/
	private boolean prepareVideoReader(){
    	
    	ServerSocket welcomeSocket = null;
    	
    	// Default constructor.
    	if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
        }
    	
		// Constructs a new ServerSocket instance bound to the given port.
		try {
			welcomeSocket = new ServerSocket(port_ecoute_server);
			final int port = welcomeSocket.getLocalPort();
			runOnUiThread(new Runnable() {
	            public void run() {
	        Context context = getApplicationContext();
			CharSequence text = "Welcom socket new server port on :" + port_ecoute_server + "on port " + port;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
	            }
	        });
			// Waits for an incoming request and blocks until the connection is opened.
			soc = welcomeSocket.accept(); //works
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    	    
		final String ipclient = soc.getRemoteSocketAddress().toString();
		
		runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "Accept ok, ip client =" + ipclient;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
		
		/*  Create a new ParcelFileDescriptor from the specified Socket. 
    	 *  The new ParcelFileDescriptor holds a dup of the original FileDescriptor in the Socket,
    	 *  so we must still close the Socket as well as the new ParcelFileDescriptor.
    	 */
    	pfd = ParcelFileDescriptor.fromSocket(soc);
    	mMediaPlayer = new MediaPlayer();
    	//initialize
    	mMediaPlayer.setDisplay(null);
    	mMediaPlayer.reset();
    
    	try {
			mMediaPlayer.setDataSource(pfd.getFileDescriptor());    // here is still the problem, 
    		pfd.close();											// the file descriptor of a 
			mMediaPlayer.prepareAsync();							// socket is not seekable
			mMediaPlayer.setOnPreparedListener(this);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
		return true;
    	
    }
    
	/** Server side Second method, write incoming data from socket in file 
	 * and then display it */
	private boolean prepareVideoReader2(){

    	ServerSocket welcomeSocket = null;
    	
    	// Default constructor.
    	if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
        }
    	
		// Constructs a new ServerSocket instance bound to the given port.
		try {
			welcomeSocket = new ServerSocket(port_ecoute_server);
			int port = welcomeSocket.getLocalPort();
			// Waits for an incoming request and blocks until the connection is opened.
			soc = welcomeSocket.accept(); //works
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//File path = Environment.getExternalStorageDirectory();
	    String path = "/storage/sdcard0/Pictures/MyCameraApp/";
		File file = new File(path, "sample.mp4");
		
	    
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        final byte[] data = new byte[1024];

        Thread t = new Thread(new Runnable() {

            public void run() {
                int count = 0;
                try {
                    count = soc.getInputStream().read(data, 0, data.length);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                 while (count != -1) {
                    System.out.println(count);
                try {
                    fos.write(data, 0, count);
                    count = soc.getInputStream().read(data, 0, data.length);
                    Log.e("Receiving", "Receiving");
                    } catch (IOException e) {
                    e.printStackTrace();
                }

                }
                 Log.e("Receiving", "Finish");
            }
        });
        t.start();
	    
		final String ipclient = soc.getRemoteSocketAddress().toString();
		
		runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "Accept ok, ip client =" + ipclient;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
		
    	mMediaPlayer = new MediaPlayer();
    	//initialize
    	mMediaPlayer.setDisplay(null);
    	mMediaPlayer.reset();
    
    	try {
    		    		
			mMediaPlayer.setDataSource(file.getAbsolutePath());  
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
		return true;
    	
	}
	
	/** Server side Third method, using NanoHTTPD local server
	 *  in order to store data,
	 *  and then set the input data with an uri like http://127.0.0.1:port/file
	 *   */
	private boolean prepareVideoReader3(){

    	// Default constructor.
    	if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
        }
		runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "NanoHttp started ok";
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
    	mMediaPlayer = new MediaPlayer();
    	//initialize
    	mMediaPlayer.setDisplay(null);
    	mMediaPlayer.reset();
    
    	try {
    		// Sets the data source (file-path or http/rtsp URL) to use.
    		String url;
    		url = "http://127.0.0.1:" + port_ecoute_server + "/" +  file_name;
  
			mMediaPlayer.setDataSource(url);  
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(this);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
    	
	}
	
	/** Server side Third method, using StreamProxy local server
	 *  in order to store data,
	 *  and then set the input data with an uri like http://127.0.0.1:port/file
	 *   */
	private boolean prepareVideoReader4() {
		
		runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "Strart preparevideo reader";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
		playUrl = "127.0.0.1:" + port_ecoute_proxy + "/" + file_name; 
	
	    synchronized (this) {
	        Log.d(TAG, "C'est partit pour le media player coco" + playUrl);
	        mMediaPlayer = new MediaPlayer();
	    	//initialize
	    	mMediaPlayer.setDisplay(null);
	    	mMediaPlayer.reset();
	    	try {
	    		Log.d(TAG, "On met le playURL dans data source : " + playUrl);
				mMediaPlayer.setDataSource(playUrl);
				runOnUiThread(new Runnable() {
				    public void run() {
				Context context = getApplicationContext();
				CharSequence text = "Media player set data source ok!";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				    }
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        Log.d(TAG, "PreparingAsync: " + playUrl);
	        mMediaPlayer.prepareAsync();
	        Log.d(TAG, "Waiting for prepare");
	      }
		return true;
	}

	private boolean startProxy(){
		
	    // From 2.2 on (SDK ver 8), the local mediaplayer can handle Shoutcast
	    // streams natively. Let's detect that, and not proxy.
	    //if (Build.VERSION.SDK_INT < 8) {
	      if (proxy == null) {
	        proxy = new StreamProxy();
	        proxy.init();
	        proxy.start();
	      }
	      playUrl = String.format("http://127.0.0.1:%d/%s",
	          proxy.getPort(), file_name);
	    //}
	    runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "stream Proxy launch! We listen on" + playUrl;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
		return true;
	}
	
	@SuppressWarnings("resource")
	private boolean forwardFlow(){
		    	
		/** Here we receive the data incoming from the other phone (Server side)*/
		
		ServerSocket welcomeSocket = null;
		Log.d(TAG, "C'est partit le forwardFlow");
		try {
			welcomeSocket = new ServerSocket(port_ecoute_server);
			final int port = welcomeSocket.getLocalPort();
			runOnUiThread(new Runnable() {
	            public void run() {
	        Context context = getApplicationContext();
			CharSequence text = "Welcom socket new server port on :" + port_ecoute_server + "on port " + port;
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
	            }
	        });
			/* Accept */
			soc = welcomeSocket.accept(); //works
			Log.d(TAG, "Socket accepted, formidable");
			/* Socket accepted */
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    	    
		final String ipclient = soc.getRemoteSocketAddress().toString();
		
		runOnUiThread(new Runnable() {
		    public void run() {
		Context context = getApplicationContext();
		CharSequence text = "Accept ok, ip client =" + ipclient;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		    }
		});
		
		pfd =  ParcelFileDescriptor.fromSocket(soc); 

		/** Then, we start the proxy */
		try {
			Log.d(TAG, "Ok, on lance nano");
			NanoHTTPD server = new NanoHTTPD(port_ecoute_proxy, new File("."));
			Log.d(TAG, "Nano lancé sur le port " + port_ecoute_proxy);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//startProxy();
		runOnUiThread(new Runnable() {
            public void run() {
        Context context = getApplicationContext();
		CharSequence text = "http server is started, now we will send data to him";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
            }
        });
		/** And now, we send these data to the proxy server (Client side) */
        
    	ip = "127.0.0.1";
		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByName(ip), port_ecoute_proxy); //Nexus = server
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "Inside  UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG1, "Inside  IOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		/** First way, directly receive data in file desciptor of client socket */
		pfd2 = ParcelFileDescriptor.fromSocket(socket); 
		
		try {
			while(soc.getInputStream() != null){
			FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
			BufferedInputStream bis = new BufferedInputStream(fis);
			try {
				bis.read(mybytearray,0,mybytearray.length);
				OutputStream os = socket.getOutputStream();
				Log.d(TAG, "Write into http server ");
			    os.write(mybytearray,0,mybytearray.length);
			    os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /**/
		/*
		try {
			while(soc.getInputStream() != null){
			try {
				is = soc.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileOutputStream fos = new FileOutputStream(pfd2.getFileDescriptor()); // destination path and name of file        
			bos = new BufferedOutputStream(fos);
			try {
				bytesRead = is.read(mybytearray,0,mybytearray.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			current = bytesRead;

			// thanks to A. Cádiz for the bug fix
			do {
			   try {
				   Log.d(TAG, "On remplit le buffer pour faire le premier sample et après on en fera d'autre" + port_ecoute_proxy);
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   if(bytesRead >= 0) current += bytesRead;
			   Log.d(TAG, "Write into http server ");
			} while(bytesRead > -1);

			      
			try {
				bos.write(mybytearray, 0 , current);
				bos.flush();
			    long end = System.currentTimeMillis();
			    System.out.println(end-start);
			    bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			soc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
              /* */
		return true;
	}
	
	/** Just play a video from an URL to test if the mediaplayer works 
	 *  And yes, it works*/
	private boolean prepareVideoReader5() {
		
    	
    	// Default constructor.
    	if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
        }
    					
    	mMediaPlayer = new MediaPlayer();
    	//initialize
    	mMediaPlayer.setDisplay(null);
    	mMediaPlayer.reset();
    
    	try {
    		// Sets the data source (file-path or http/rtsp URL) to use.
    		String url;
    		 
    		url = "http://daily3gp.com/vids/747.3gp";
   
			mMediaPlayer.setDataSource(url);

			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
		
	}
    
	/** Client side
     *  Open the camera, send the content of the data over a socket
     *  the Server ip is obtain by reading the tag for now
     *  But after it will be receive from a remote server on art-mgn.fr */
    private boolean prepareVideoRecorder(Camera mCamera){
    	
        /*Socket stuff */
        
    	ip = this.getIntent().getStringExtra(EXTRA_TAG);
		Socket socket = null;
		try {
			socket = new Socket(InetAddress.getByName(ip), port_ecoute_server); //Nexus = server
			runOnUiThread(new Runnable() {
	            public void run() {
	        Context context = getApplicationContext();
			CharSequence text = "Socket server :" + ip + "on port " + port_ecoute_server;
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
	            }
	        });
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.d(TAG1, "Inside  UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG1, "Inside  IOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		pfd = ParcelFileDescriptor.fromSocket(socket); 
        // end of socket stuff
    	
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        
        // Step 4: Set output file 
        mMediaRecorder.setOutputFile(pfd.getFileDescriptor()); // Write the output directly in the socket
        runOnUiThread(new Runnable() {
            public void run() {
        Context context = getApplicationContext();
		CharSequence text = "setOutputFile without exeption! Sending to: jojjojjjojojojojojojoj" + ip;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
            }
        });

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            Log.i(TAG1, pfd.getFileDescriptor().toString());
        } catch (Exception e) {
            Log.d(TAG1, "Inside  MyException");
        }
        
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
        
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

  
    /** Wait until the video is ready to play */
    public void onPrepared(MediaPlayer player) {
    	mMediaPlayer.setDisplay(holder);
    	mMediaPlayer.start();
    }
    
    
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0));
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    
    /** Do nothing */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_change_setting, menu);
        return false;
    }
    
    /** Do nothing */
    @Override
	protected void onNewIntent(Intent intent){
		
			//setIntent(intent);
			//Process payload or msgs
	        //resolveIntent(intent);
	        		
	}
    
    /** Used at first in the case of using Intent to play video */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                         data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                         data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }
	
    /** Release recorder and player*/
    @Override
	public void onPause(){
		super.onPause();
		//disableForegroundDispatch
		releaseMediaRecorder();       // if MediaRecorder is used, release it first
        releaseCamera();              // release the camera immediately on pause event
        if(mMediaPlayer != null){
        mMediaPlayer.release();
        }
        mMediaPlayer = null;      
	}
    
    /** Clear recorder configuration
     *  Release the recorder object */
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   
            mMediaRecorder.release();
            mMediaRecorder = null;
            //mCamera.lock();           // lock camera for later use
        }
    }

    /**  release the camera for other applications */
    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();     
            mCamera = null;
        }
    }
    
    /** Here is the magic
     *  When a user click on the button, cleint start to send data
     *  to Server,
     *  and for the server side, he starts to display the video */
  	@Override
  	public void onResume(){
  		super.onResume();
 
  	     captureButton = (Button) findViewById(R.id.button_capture);
  	// Add a listener to the Capture button
  	     captureButton.setOnClickListener(
  	         new View.OnClickListener() {
  	             public void onClick(View v) {
  	            	new Thread(new Runnable() {
  	                  public void run() {
  	                 if (isRecording) {
  	                     // stop recording and release camera
  	                     mMediaRecorder.stop();  // stop the recording
  	                     releaseMediaRecorder(); // release the MediaRecorder object
  	                     mCamera.lock();         // take camera access back from MediaRecorder
  	                     isRecording = false;
  	                 } else {
  	                	/** Activate to make client side */
  	                	 /*
  	                     // initialize video camera
  	                    if (prepareVideoRecorder(mCamera)) {
  	                         // Camera is available and unlocked, MediaRecorder is prepared,
  	                         // now you can start recording
  	                         mMediaRecorder.start();

  	                         // inform the user that recording has started
  	                         //captureButton.setText("Stop");
  	                         isRecording = true;
  	                     } else {
  	                         // prepare didn't work, release the camera
  	                         releaseMediaRecorder();
  	                     }
  	                     /**/
  	                    /** Activate to make server side */
  	                	 
  	                	forwardFlow();
  	                	runOnUiThread(new Runnable() {
  	          		    public void run() {
  	          		Context context = getApplicationContext();
  	          		CharSequence text = "End of forwardFlow";
  	          		int duration = Toast.LENGTH_LONG;

  	          		Toast toast = Toast.makeText(context, text, duration);
  	          		toast.show();
  	          		    }
  	          		});
  	                	prepareVideoReader4();
  	                	/**/
  	                 }
  	                }
  	            	}).start();
  	             }
  	         }
  	     );
  	}	
}
