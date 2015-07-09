package com.d2dsq.ui;

import com.d2dsq.baseservices.SmsUtil;
import com.d2dsq.utils.ConfigManager;
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

import com.d2dsq.radio.*;
import com.d2dsq.routing.*;

public class MainActivity extends Activity
{

	private BluetoothAdapter bluetooth;
	private BluetoothSocket socket;
	public static boolean isWifiServer = false; 
	BluetoothDevice pairedDevice = null;
	private UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
	private Button configButton;
	private Button smsButton;
	private Button camButton;
	private Button browserButton;
    private Button connectButton; 
	int REQUEST_ENABLE_BT = 3;
	private static final String TAG="MainActivity"; 
	
	
	Camera camera = null;
	int cameraId = -1;
	
	// ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate"); 
        DoInitiliazation(); 
			
	}

	private void DoInitiliazation()
	{
		
		BluetoothUtil.init(); 
		
		// Fetch all the peered devices 
		
		for ( String s : BluetoothUtil.GetPeerNames() )
		{
			Log.e("MainActivity - Bluetooth", s ); 
			
		}
		
	    for ( BluetoothDevice dev : BluetoothUtil.pairedDevices)
	    {
	    	
	    	Node node = new Node(); 
	    	node.setBluetoothNode(false);
	    	node.setWifiNode(true);
	    	node.setDevice(dev);
	    	node.setNodeName(dev.getName()); 
	    	RoutingManager.theRouter.addNeighbour(node); 
	    	
	    }
		
		WifiUtil.init(getApplicationContext()); 
		for ( String s:  WifiUtil.GetPeerNames())
		{
			Log.e("MainActivity - Wifi" , s);
			
			
		}
		
		
		// Set up default Configuration items 
	
		
		// Start all deamons 
				
		
		// Starting Bluetooth server thread 
		StartBluetoothServer(); 
		
		
		// Starting Wifi server thread 
		StartWifiServer(); 
		 
		
		
		//MainApplication.serverThread.start();
		
		
		
		
		
	}
	
	private void StartWifiServer()
	{
		
		WifiServerThread th = new WifiServerThread(4453,MainApplication.packetQueue); 
		Thread wifiServerThread = new Thread(th); 
		if (WifiUtil.isWifiEnabled())
		{
			
			Log.v(TAG, "Staring wifi Server Thread"); 
			wifiServerThread.start(); 
			
			
		}
		
		
	}
	
	
	private void testSendPacket()
	{
		
	
		Packet p = new Packet(null, null, null, null);
		p.setData("Hello".getBytes());
        p.setMac(((Peer)WifiUtil.wifiPeers.toArray()[0]).getWifiP2pDevice().deviceAddress);
	    
        WifiClientThread wi = new WifiClientThread();
        
        wi.sendPacket(((Peer)WifiUtil.wifiPeers.toArray()[0]).getWifiP2pDevice().deviceAddress, 4453, p); 
		
		
		  
	}
	
	private void StartBluetoothServer()
	{
		
		MainApplication.serverHandler = new Handler(){
			
			
			@Override
			public void handleMessage(Message message)
			{
				
			
			
				switch (message.what) 
				{
				    case  MessageType.DATA_RECEIVED: 
				    	Toast.makeText(MainActivity.this, "We got something ..  ", Toast.LENGTH_SHORT).show();

						break;
						
					case MessageType.DIGEST_DID_NOT_MATCH: 
						break;
					
					case MessageType.DATA_PROGRESS_UPDATE: 
	                    break; 
	                    
					case MessageType.INVALID_HEADER: 
						Toast.makeText(MainActivity.this, "Invalid header ..  ", Toast.LENGTH_SHORT).show();
						break;
				}
                     

			
			
			
			}
             
			

		};	
		
		
		MainApplication.serverThread = new ServerThread(BluetoothUtil.adapter, MainApplication.serverHandler)	;
		MainApplication.serverThread.start(); 
		
				
		
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

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_config:
	        	
	        	// TESTING !! 
	        	//BluetoothUtil.SendData( "foo".getBytes(), (BluetoothDevice)BluetoothUtil.pairedDevices.toArray()[0]); 
	        	// Wifi Testing 
	        	//  testSendPacket();
	        	
	        	
	        	Intent intent = new Intent(this, ServiceShareChoiceActivity.class);
	        	startActivity(intent); 
	            break; 
	        case R.id.action_chat:
	        	Intent chatIntent = new Intent ( this , ChatActivity.class); 
	        	startActivity(chatIntent); 
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

	
}
