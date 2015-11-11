package com.d2dsq.radio;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.d2dsq.models.Message;

public class WifiServerSendThread extends Thread
{

	private static final int SERVER_PORT = 7000;
	public byte[] data;
	String destination;

	public void WifiServerSendThread(String dest, byte[] data)
	{
		this.data = data;
		destination = dest;
	}

	public void run()
	{

		// Send the message to clients
		try
		{
			ArrayList<InetAddress> listClients = ServerInit.clients;
			for (InetAddress addr : listClients)
			{

				Socket socket = new Socket();
				socket.setReuseAddress(true);
				socket.bind(null);

				socket.connect(new InetSocketAddress(addr, SERVER_PORT));

				OutputStream outputStream = socket.getOutputStream();

				new ObjectOutputStream(outputStream).writeObject(data);

				socket.close();
			}

		} catch (IOException e)
		{
			e.printStackTrace();

		}

	}

}
