package com.d2dsq.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.BluetoothUtil;
import com.d2dsq.radio.WifiUtil;


public class BluetoothDiscoverThread extends Thread
{
	

	private String service; 
	private String path; 
	private com.d2dsq.models.Message mes;
    
	int type; ; 
	
	public BluetoothDiscoverThread (String ser, String pa, int type)
	{
		service = ser; 
		path = pa; 
	    this.type = type;	
		
	
		
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
	   
        
	    if (  type == RoutingManager.TYPE_BLUETOOTH )
	    {
		
		// Get the paired devices 
	    BluetoothUtil.GetPairedDevices(); 
	    
	    
	    for ( BluetoothDevice dev :  BluetoothUtil.pairedDevices )
	    {
	    	BluetoothUtil.SendData(mes.CreateDiscoverPacketByteArray(), dev); 
	    	
	    }
	    }
	    else if ( type  == RoutingManager.TYPE_WIFI )
	    {
	    	if (  !WifiUtil.isGroupOwner )
	    	{
	    	       WifiUtil.SendData(mes.CreateDiscoverPacketByteArray(), WifiUtil.groupOwnerAdderess.getHostAddress());
	    	}
	    	else
	    	{
	    		for ( String ip : WifiUtil.neighbours)
	    		{
	    			WifiUtil.SendData(mes.CreateDiscoverPacketByteArray(), ip); 
	    			
	    		}
	    		
	    	}
	    	
	    }
	 
		
		
	}

}
