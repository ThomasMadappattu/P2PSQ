package com.d2dsq.routing;
import com.d2dsq.models.*;
public interface SenderInterface
{

	public void SendPackect(String mDeviceName, Packet packet); 
	public void SendMessage(String mDeviceNam, Message message);
	public String GetPeers();
	
}
