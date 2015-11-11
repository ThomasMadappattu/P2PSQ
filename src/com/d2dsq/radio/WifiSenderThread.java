package com.d2dsq.radio;


class WifiSenderThread extends Thread
{
	
	  byte[] data;
	  String ip; 
	  WifiSenderThread( String ip, byte[] data)
	  {
		  this.data = data; 
		  this.ip = ip; 
	  }
	  
	  public void run()
	  {
		   WifiClientThread th = new WifiClientThread();
		   th.sendPacket(ip, WifiUtil.SERVER_PORT, data); 
		   
		  
	  }
	  
}
