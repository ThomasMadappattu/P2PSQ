package com.d2dsq.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.BluetoothUtil;
import com.d2dsq.radio.WifiUtil;

public class BluetoothRequestThread extends Thread
{

	private String service;
    int type; 
    
    
	public void setType(int t)
	{
		
		type = t; 
	}
	public String getService()
	{
		return service;
	}

	public void setService(String service)
	{
		this.service = service;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public com.d2dsq.models.Message getMes()
	{
		return mes;
	}

	public void setMes(com.d2dsq.models.Message mes)
	{
		this.mes = mes;
	}

	public String getDestPh()
	{
		return destPh;
	}

	public void setDestPh(String destPh)
	{
		this.destPh = destPh;
	}

	public String getTextMsg()
	{
		return textMsg;
	}

	public void setTextMsg(String textMsg)
	{
		this.textMsg = textMsg;
	}

	private String path;
	private com.d2dsq.models.Message mes;
	private String destPh;
	private String textMsg;
	private String desDev;

	public String getDesDev()
	{
		return desDev;
	}

	public void setDesDev(String desDev)
	{
		this.desDev = desDev;
	}

	public void run()
	{

		try
		{
			mes = new com.d2dsq.models.Message(
					com.d2dsq.models.Message.REQUEST_MESSAGE, textMsg,
					InetAddress.getLocalHost(), "ewssdfd");
			
			mes.SetDiscoverMessage(service, path);
			mes.setResponsePath(path);
			mes.setDestPhone(destPh);

		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ( type == RoutingManager.TYPE_BLUETOOTH )
		{
		// Get the paired devices
		BluetoothUtil.GetPairedDevices();

		for (BluetoothDevice dev : BluetoothUtil.pairedDevices)
		{
			if (dev.getName().compareTo(desDev) == 0)
			{
				BluetoothUtil.SendData(mes.CreateSMSRequestPacket(), dev);
			}

		}
		}
		else  if ( type == RoutingManager.TYPE_WIFI)
		{
			
		   if ( !WifiUtil.isGroupOwner)
		   {
			   WifiUtil.SendData(mes.CreateSMSRequestPacket(), WifiUtil.groupOwnerAdderess.getHostAddress());
			   
		   }
		   else
		   {
				for (String ip : WifiUtil.neighbours)
				{
					if (ip.compareTo(desDev) == 0)

						WifiUtil.SendData(mes.CreateSMSRequestPacket(), ip);

				}
		   }
		
		}

	}

}
