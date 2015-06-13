package com.d2dsq.ui;

import com.d2dsq.baseservices.SmsUtil;
import com.d2dsq.utils.ConfigManager;
import com.example.P2PSQ.BloothServiceHandler;
import com.example.P2PSQ.BlutoothServiceUtil;
import com.example.P2PSQ.CameraPreview;
import com.example.test123.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import java.util.*;
import android.widget.*;
import android.content.*;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.bluetooth.*;
import java.io.*;


public class MainActivity extends Activity
{

	private BluetoothAdapter bluetooth;
	private BluetoothSocket socket;
	BluetoothDevice pairedDevice = null;
	private UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
	private Button configButton;
	private Button smsButton;
	private Button camButton;
	private Button browserButton;
    private Button connectButton; 
	int REQUEST_ENABLE_BT = 3;
	
	
	
	Camera camera = null;
	int cameraId = -1;
	CameraPreview mPreview;

	// ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate"); 
			
	}

	private void initServicesList()
	{
		String avStr=""; 
		for ( BluetoothDevice device : bluetooth.getBondedDevices())
		{
			avStr +=  device.getName() + "," ; 
		}
		avStr += "Rise:LG-L38C,Rise:node"; 
	    ConfigManager.Set("avServices", avStr); 	
	}
	private void setupCamera()
	{
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++)
		{
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK)
			{
				Log.d("P2PSQ", "Camera found");
				cameraId = i;

				break;
			}
		}

		if (cameraId >= 0)
		{

			Log.d("Camera: ", "Camera Opened ! ");
			camera = Camera.open(cameraId);
			if (camera == null)
				Log.d("Camera: ", "Unable to open camera ");

			/*
			 * Intent browserIntent = new
			 * Inent(getApplicationContext(),BrowserActivity.class);
			 * startActivity(browserIntent); mPreview = new CameraPreview(this,
			 * camera); FrameLayout preview = (FrameLayout)
			 * findViewById(R.id.layout2); preview.addView(mPreview);
			 */
			camera.startPreview();
			/*
			 * mPreview = new CameraPreview(this, camera); FrameLayout preview =
			 * (FrameLayout) findViewById(R.id.layout2);
			 * preview.addView(mPreview);
			 */
		}

	}

	private void sendMessage(String message)
	{
		// Check that we're actually connected before trying anything
		
		/*
		if (btServer.getState() != BloothServiceHandler.STATE_CONNECTED)
		{
			Toast.makeText(this, "sendMesssage:not connected",
					Toast.LENGTH_SHORT).show();
			return;
		}
        */ 
		// Check that there's actually something to send
		if (message.length() > 0)
		{

			// XXX !!!
			message = message + "\r\n"; // terminate for pc bluetooth spp server

			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			// btServer.write(send);

			// Reset out string buffer to zero and clear the edit text field

			// mOutEditText.setText(mOutStringBuffer);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
		
	}

	private void configBluetooth()
	{
		this.bluetooth = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_config:
	        	Intent intent = new Intent(this, ServiceShareChoiceActivity.class);
	        	startActivity(intent); 
	            break; 
	        case R.id.action_browser:
	        	Intent browserIntent = new Intent(this, BrowserActivity.class);
	        	startActivity(browserIntent); 
	        	break;
	        case R.id.action_sms:
	        	Intent smsActivity = new Intent(this, SMSActivity.class); 
	        	startActivity(smsActivity); 
	        	break; 
	        	
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true; 
	}

	private final Handler mHandler = new Handler()
	{
		
		
		private void ParseMessage(String message)
		{
			
			String tempMessage; 
			String phoneNum;
			String msgText; 
			String nextDevice;
			int colonIndex = 0 ; 
			Log.d("Read Message", message); 
			// Is it as broad cast message 
			if ( message.startsWith("BCAST:"))
			{
				
				 tempMessage = message.substring(0 , "BAST:".length());  
			     BlutoothServiceUtil.AddPeers(tempMessage);
			     ConfigManager.Set("avServices",BlutoothServiceUtil.GetPeerString());
				 
			}		
			
			// if the given message is intended for this device 
			else if (message.startsWith(bluetooth.getName()))
			{
			    tempMessage = message.substring(bluetooth.getName().length()+1); 
			    Log.d("tempMessage" , tempMessage);
			    
			    // See if the we need to provide one of the services 
			    if (tempMessage.startsWith("SMS"))
			    {
			    	tempMessage = tempMessage.substring("SMS".length() +1 );
			        phoneNum = tempMessage.split(":")[0];
			        msgText = tempMessage.split(":")[1]; 
			        Log.d("MainActivity" , "Trying to send sms ..."); 
			        Log.d("Phone num =" ,phoneNum);
			        Log.d("Msg Text = " , msgText); 
			    	SmsUtil.SendSms(phoneNum, msgText); 
			    }
			    
			    
			    // it is not one of the known services 
			     // Extract he next device name 
			    else
			    {
			       colonIndex = tempMessage.indexOf(':') ;
			       nextDevice  = tempMessage.substring(0,colonIndex); 
				   Log.d("ParseMessage", "Redirecting to  " + nextDevice ); 
				   Log.d("Redirect Message " , tempMessage) ; 
				   BlutoothServiceUtil.SendMessage(tempMessage, nextDevice,true);
			    }
			}
			else   
			{
				
	                // We are receving some garbage from somewhere handle is here 
				    Log.d(" Unknown Meassed Read :" , message);
				
			}
			
			//if (message.matches(bl)
			
			
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case BloothServiceHandler.MESSAGE_STATE_CHANGE:

				switch (msg.arg1)
				{
				case BloothServiceHandler.STATE_CONNECTED:

					break;
				case BloothServiceHandler.STATE_CONNECTING:

					break;
				case BloothServiceHandler.STATE_LISTEN:
				case BloothServiceHandler.STATE_NONE:

					break;
				}
				break;
			case BloothServiceHandler.MESSAGE_WRITE:
				
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				/*
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				// Toast.makeText(null,"sending sms to  " +
				// writeMessage.split(":")[0] , Toast.LENGTH_SHORT).show();
				Log.d("Hanlder", "sending sms to  "
						+ writeMessage.split(":")[0]);
				SmsUtil.SendSms(writeMessage.split(":")[0], writeMessage);
				*/
				Log.d("Message Written" ,writeMessage );
				break;
			case BloothServiceHandler.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.d("Message Read:" , readMessage);
				ParseMessage(readMessage); 
				
								
				//Log.d("Hanlder", "sending sms to  " + readMessage.split(":")[0]);
				//SmsUtil.SendSms(readMessage.split(":")[0], readMessage);
				// mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
				// + readMessage);
				break;
			case BloothServiceHandler.MESSAGE_DEVICE_NAME:

				break;
			case BloothServiceHandler.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString("toast"), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};
	
}
