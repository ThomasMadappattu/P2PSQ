package com.d2dsq.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.BluetoothUtil;


public class BluetoothDiscoverThread extends Thread
{
	

	private String service; 
	private String path; 
	private com.d2dsq.models.Message mes;

	public BluetoothDiscoverThread (String ser, String pa)
	{
		service = ser; 
		path = pa; 
		
		
	
		
	}
	
	
	
	
	public void run()
	{
	    
	   
		try
		{
			mes = new com.d2dsq.models.Message(com.d2dsq.models.Message.DISCOVER_MESSAGE, "sfsdsdf",InetAddress.getLocalHost() ,"ewssdfd");
			mes.SetDiscoverMessage(service,path) ; 
		} 
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	   
        
	    // Get the paired devices 
	    BluetoothUtil.GetPairedDevices(); 
	    
	    for ( BluetoothDevice dev :  BluetoothUtil.pairedDevices )
	    {
	    	BluetoothUtil.SendData(mes.CreateDiscoverPacketByteArray(), dev); 
	    	
	    }
	 
		
		
	}

}
