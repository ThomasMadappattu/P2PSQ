package com.d2dsq.ui;

import com.d2dsq.baseservices.SmsUtil;
import com.d2dsq.utils.ConfigManager;
import com.d2dsq.utils.SerializeUtil;
import com.example.test123.R;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
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
import java.net.UnknownHostException;

import org.apache.commons.lang.SerializationUtils;

import com.d2dsq.radio.*;
import com.d2dsq.routing.*;


public class MainActivity extends Activity
{

	private BluetoothAdapter bluetooth;
	private BluetoothSocket socket;
	public static volatile boolean isWifiServer = false;
	BluetoothDevice pairedDevice = null;
	private UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");

	int REQUEST_ENABLE_BT = 3;

	private WifiP2pManager mManager;
	private Channel mChannel;
	private WifiDirectBroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;

	Camera camera = null;
	int cameraId = -1;

	public static ServerInit initServer = null;
	ClientInit initClient;
	private static final String TAG = "MainActivity";

	// ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("MainActivity", "onCreate");
		DoInitiliazation();

	}

	
	public void DoInitialConfig()
	{
		ConfigManager.Set("USESMS", "yes"); 
		ConfigManager.Set("USECAM", "yes");
		ConfigManager.Set("USEINET", "yes");
		
	}
	private void DoInitiliazation()
	{

		
		DoInitialConfig(); 
		
		BluetoothUtil.init();
        
		
		// Fetch all the peered devices

		for (String s : BluetoothUtil.GetPeerNames())
		{
			Log.e("MainActivity - Bluetooth", s);

		}

		for (BluetoothDevice dev : BluetoothUtil.pairedDevices)
		{

			Node node = new Node();
			node.setBluetoothNode(false);
			node.setWifiNode(true);
			node.setDevice(dev);
			node.setNodeName(dev.getName());
			RoutingManager.theRouter.addNeighbour(node);

		}

		// Set up default Configuration items

		// Start all deamons

		// Starting Bluetooth server thread
		try
		{
			StartBluetoothServer();
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		// Initializing Wifi Direct

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = WifiDirectBroadcastReceiver.createInstance();
		mReceiver.setmManager(mManager);
		mReceiver.setmChannel(mChannel);
		mReceiver.setmActivity(this);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		// MainApplication.serverThread.start();

		
	}

	@Override
	public void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);

		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener()
		{

			@Override
			public void onSuccess()
			{
				Log.v(TAG, "Discovery process succeeded");
			}

			@Override
			public void onFailure(int reason)
			{
				Log.v(TAG, "Discovery process failed");
			}
		});
	}

	@Override
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private void StartBluetoothServer() throws UnknownHostException
	{

		MainApplication.serverHandler = new Handler()
		{

			@Override
			public void handleMessage(Message message)
			{

				switch (message.what)
				{
				case MessageType.DATA_RECEIVED:
					Toast.makeText(MainActivity.this, "We got something ..  ",
							Toast.LENGTH_LONG).show();
					byte[] packet= ( byte[] ) message.obj;
					
					if ( packet[0]  ==  com.d2dsq.models.Message.DISCOVER_MESSAGE )
					{
						try
						{
							RoutingManager.theRouter.HandleBluetoothDiscoveryMessage(packet);
						} catch (UnknownHostException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedEncodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
					}
					else if ( packet[0]  ==  com.d2dsq.models.Message.RESPONSE_MESSAGE )
					{
						try
						{
							RoutingManager.theRouter.HandleBluetoothResponseMessage(packet);
						} catch (UnsupportedEncodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
					}
					else if ( packet[0] == com.d2dsq.models.Message.REQUEST_MESSAGE)
					{
						
					
						try
						{
							RoutingManager.theRouter.HandleBluetoothRequest(packet);
						} catch (UnsupportedEncodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
				

					break;

				case MessageType.DIGEST_DID_NOT_MATCH:
					break;

				case MessageType.DATA_PROGRESS_UPDATE:
					break;

				case MessageType.INVALID_HEADER:
					Toast.makeText(MainActivity.this, "Invalid header ..  ",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}

		};

		MainApplication.serverThread = new ServerThread(BluetoothUtil.adapter,
				MainApplication.serverHandler);
		MainApplication.serverThread.start();
		
		// Start Bluetooth Server 
	
		

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
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
		case R.id.action_config:

			Intent intent = new Intent(this, ServiceShareChoiceActivity.class);
			startActivity(intent);
			break;
		case R.id.action_chat:

			// Start the init process
			if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER)
			{
				Toast.makeText(
						MainActivity.this,
						"I'm the group owner  "
								+ mReceiver.getOwnerAddr().getHostAddress(),
						Toast.LENGTH_SHORT).show();
				initServer = new ServerInit();
				initServer.start();
			} else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT)
			{
				Toast.makeText(MainActivity.this, "I'm the client",
						Toast.LENGTH_SHORT).show();
				ClientInit client = new ClientInit(mReceiver.getOwnerAddr());
				client.start();
			}

			Intent chatIntent = new Intent(this, ChatActivity.class);
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
		case R.id.discover_resource: 
			try
			{
				// SMS 
				RoutingManager.theRouter.SendDiscoverMessagesBluetooth("SMS");
				// Internet 
				RoutingManager.theRouter.SendDiscoverMessagesBluetooth("INET");
			} catch (UnknownHostException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			break; 

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

}
