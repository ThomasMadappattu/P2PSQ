package com.d2dsq.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.BluetoothUtil;
import com.d2dsq.radio.WifiUtil;

public class BluetoothResponseThread extends Thread
{

	private String service;
	private String path1;
	private String path2;
	private String desDev;
	private com.d2dsq.models.Message mes;
	private int type;

	public BluetoothResponseThread(String ser, String path1, String path2,
			String desDev, int type)
	{
		service = ser;
		this.path1 = path1;
		this.path2 = path2;
		this.desDev = desDev;
		this.type = type;
	}

	public void run()
	{

		try
		{
			mes = new com.d2dsq.models.Message(
					com.d2dsq.models.Message.DISCOVER_MESSAGE, "sfsdsdf",
					InetAddress.getLocalHost(), "ewssdfd");
			mes.SetDiscoverMessage(service, path1);
			mes.setResponsePath(path2);
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (type == RoutingManager.TYPE_BLUETOOTH)
		{

			BluetoothUtil.GetPairedDevices();

			for (BluetoothDevice dev : BluetoothUtil.pairedDevices)
			{
				if (dev.getName().compareTo(desDev) == 0)
				{
					BluetoothUtil.SendData(mes.CreateResponsePacket(), dev);

				}

			}
		} else if (type == RoutingManager.TYPE_WIFI)
		{
			for (String ip : WifiUtil.neighbours)
			{
				if (ip.compareTo(desDev) == 0)
				{

					WifiUtil.SendData(mes.CreateResponsePacket(), ip);
				}

			}

		}

	}

}
