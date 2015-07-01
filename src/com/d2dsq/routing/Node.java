package com.d2dsq.routing;


import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.Peer;
import com.d2dsq.routing.*; 

public class Node
{
	
	String nodeName; 
	int cost; 
	
	boolean isWifiNode;
    boolean isBluetoothNode; 
    
	// Device specific items 
	
   BluetoothDevice device; 
   Peer wifiDevice; 
   
	
	public String getNodeName()
	{
		return nodeName;
	}
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}
	public int getCost()
	{
		return cost;
	}
	public void setCost(int cost)
	{
		this.cost = cost;
	}
	public boolean isWifiNode()
	{
		return isWifiNode;
	}
	public void setWifiNode(boolean isWifiNode)
	{
		this.isWifiNode = isWifiNode;
	}
	public boolean isBluetoothNode()
	{
		return isBluetoothNode;
	}
	public void setBluetoothNode(boolean isBluetoothNode)
	{
		this.isBluetoothNode = isBluetoothNode;
	}
	public BluetoothDevice getDevice()
	{
		return device;
	}
	public void setDevice(BluetoothDevice device)
	{
		this.device = device;
	}
	public Peer getWifiDevice()
	{
		return wifiDevice;
	}
	public void setWifiDevice(Peer wifiDevice)
	{
		this.wifiDevice = wifiDevice;
	}

   
   
   
	
	
}
