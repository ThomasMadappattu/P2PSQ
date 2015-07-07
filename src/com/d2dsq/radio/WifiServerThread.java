package com.d2dsq.radio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.d2dsq.routing.*;

public class WifiServerThread implements Runnable {

	private ServerSocket serverSocket;
	private ConcurrentLinkedQueue<Packet> packetQueue;
	private static final String TAG = "WifiServerThread"; 
	

	/**
	 * Constructor with the queue
	 * @param port
	 * @param queue
	 */
	public WifiServerThread(int port, ConcurrentLinkedQueue<Packet> queue) {
		try {
			Log.v(TAG , "Creating new Server Socket"); 
			this.serverSocket = new ServerSocket(port);
			Log.v(TAG,"Created new Server socket"); 
		} catch (IOException e) {
			System.err.println("Server socket on port " + port + " could not be created. ");
			e.printStackTrace();
			Log.v(TAG,"Some error haappened"); 
		}
		this.packetQueue = queue;
	}

	/**
	 * Thread runner
	 */
	@Override
	public void run() {
		Socket socket;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				
				Log.v(TAG , "Accepting"); 
				socket = this.serverSocket.accept();
				InputStream in = socket.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] buf = new byte[1024];
				while (true) {
					int n = in.read(buf);
					if (n < 0)
						break;
					baos.write(buf, 0, n);
				}

				byte trimmedBytes[] = baos.toByteArray();
				Packet p = Packet.deserialize(trimmedBytes);
				p.setSenderIP(socket.getInetAddress().getHostAddress());
				Log.v(TAG,"Got something "); 
				this.packetQueue.add(p);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}