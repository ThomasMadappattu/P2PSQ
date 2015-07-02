package com.d2dsq.radio;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set; 

public class BluetoothUtil
{

	private static final String TAG = "d2dsq/BluetoothUtil";
	public  static Set<BluetoothDevice> pairedDevices;
	public  static BluetoothAdapter adapter;
	static List<String> peerNames  = new LinkedList<String>(); ; 
	public static ClientThread cThread;
	public static Message msg = new Message(); 
	
	public static Handler defHandler = new Handler()
	{
		// Do nothing dummy handler, All we need to do is send some data
		@Override
        public void handleMessage(Message message) 
		{

			  switch (message.what)
			  { 
			                 case MessageType.READY_FOR_DATA:
			                	 break;
			                  
			                 case MessageType.COULD_NOT_CONNECT:
			                	 break;
			                
			                 case MessageType.SENDING_DATA:
			                	 break; 
			                	 
			                 case MessageType.DATA_SENT_OK:
			                	 break; 
			                	 
			                 case MessageType.DIGEST_DID_NOT_MATCH:
			                	
			                	  break;
				  
			  
			  
			  
			  }
			
		}
		
		
		
	}; 
	
	public static void init()
	{
		peerNames = new LinkedList<String>(); 
		adapter = BluetoothAdapter.getDefaultAdapter(); 
		
	}
	
	
	public static void   GetPairedDevices()
	{

		if (adapter != null) 
		{
			if (adapter.isEnabled()) 
			{
				pairedDevices = adapter.getBondedDevices();
			} 
			else 
			{
				Log.e(TAG, "Bluetooth is not enabled");
			}
		} 
		else 
		{
			Log.e(TAG, "Bluetooth is not supported on this device");
		}
		
		
	}
	
	public static List<String> GetPeerNames()
	{
		
		GetPairedDevices(); 
		
		for (BluetoothDevice d : pairedDevices)
		{
			if ( d.getName() != null )
			{
			   peerNames.add(d.getName());
			}
		}
		return peerNames; 
		
	}
	
	
	public static void SendData(byte[] data, BluetoothDevice dev )
	{
		
		cThread = new ClientThread(dev,defHandler);
		cThread.start(); 
		Log.v("BluetoothUtil.SendData : ", dev.getName()) ;
		msg.obj = data; 
		
		
	}
	
	
	

}



