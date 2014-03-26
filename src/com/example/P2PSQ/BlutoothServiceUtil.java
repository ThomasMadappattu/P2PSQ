package com.example.P2PSQ;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.*;
import java.util.*; 

public class BlutoothServiceUtil
{
	public static BloothServiceHandler btServer; 
    private static Context context ; 
	
    private static  Set<String> peers; 
    
    public static void AddPeers(String peerInfo)
    {
    	
    	for ( String peer : peerInfo.split(","))
    	{
    		peers.add(peer);
    	}
    	
    	

    }
    
    public static String  GetPeerString()
    {
    	String peerStr = "" ; 
    	int length;
    	for ( String peer : peers)
    	{
    		peerStr+=peer +","; 
    	}
    	length = peerStr.length(); 
    	peerStr = peerStr.substring(0 , length -2 ); 
    	return peerStr; 
    	
    }
    
   
    
    
    public static void BroadCastPeers()
    {
    	String msgToSend=""; 
    	int length = 0; 
    	
    	msgToSend="BCAST:";
    	
    	AddPeers(btServer.GetConnectedDevices()); 
    	; 
    	msgToSend += GetPeerString(); 
        msgToSend += "\r\n";       	
    	for ( String connectedDevice : btServer.GetConnectedDevices().split(","))
    	{
    		btServer.write(msgToSend.getBytes(), connectedDevice); 
    	}
         
    	
    }
    public static void Init(Context cxt , Handler mHandler )
	{
		btServer = new BloothServiceHandler(cxt , mHandler);
		context = cxt; 
		btServer.start();
		peers = new HashSet<String>(); 
		 
		
	}
	
	public static void ConnectToPeers(Set<BluetoothDevice> devices)
	{
		btServer.connectToDevices(devices);
	}
	
	
	public  static void SendMessage(String message , String deviceName)
	{
		String msgToSend = ConfigManager.Get("useService") + ":" + message + "\r\n"; 
		Log.d("Send to device" ,deviceName );
		
		// Send the message only if the device is connected  
		if (btServer.isConnected(deviceName))
		{
	     	btServer.write(msgToSend.getBytes(), deviceName);
		}
		else 
		{
               // The nofity that there is no connection 
			   Toast.makeText(context, "Not connected to : " + deviceName ,Toast.LENGTH_SHORT  ).show() ; 
			
		}
		
	}
	
	

}
