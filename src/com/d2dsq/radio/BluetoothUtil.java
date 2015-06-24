package com.d2dsq.radio;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.List;
import java.util.Set; 
public class BluetoothUtil
{

	private static final String TAG = "d2dsq/BluetoothUtil";
	static Set<BluetoothDevice> pairedDevices;
	static BluetoothAdapter adapter;
	static List<String> peerNames; 
	
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
		peerNames.clear();
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



