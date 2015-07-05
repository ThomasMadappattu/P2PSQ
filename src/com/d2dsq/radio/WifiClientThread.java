package com.d2dsq.radio;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import com.d2dsq.routing.*;

public class WifiClientThread {

	Socket tcpSocket = null;

	public boolean sendPacket(String ip, int port, Packet data) {
		// Try to connect, otherwise remove from table
		try {
			System.out.println("IP: " + ip);
			InetAddress serverAddr = InetAddress.getByName(ip);
			tcpSocket = new Socket();
			tcpSocket.bind(null);
			tcpSocket.connect(new InetSocketAddress(serverAddr, port), 5000);

		} catch (Exception e) {
			/*
			 * If can't connect assume that they left the chat and remove them
			 */
		
			e.printStackTrace();
			return false;
		}

		OutputStream os = null;

		//try to send otherwise remove from table
		try {
			os = tcpSocket.getOutputStream();
			
			
	        os.write(data.serialize());
			os.close();
			tcpSocket.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return true;
	}

}
