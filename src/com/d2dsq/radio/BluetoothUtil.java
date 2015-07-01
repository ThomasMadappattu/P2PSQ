package com.d2dsq.radio;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set; 
public class BluetoothUtil
{

	private static final String TAG = "d2dsq/BluetoothUtil";
	public  static Set<BluetoothDevice> pairedDevices;
	static BluetoothAdapter adapter;
	static List<String> peerNames  = new LinkedList<String>(); ; 
	
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
	

}



