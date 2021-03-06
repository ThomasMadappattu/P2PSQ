package com.d2dsq.radio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.Message;
import android.util.Log;

import com.d2dsq.routing.*;
import com.d2dsq.ui.MainApplication;

public class WifiServerThread implements Runnable {

	private ServerSocket serverSocket;
	private ConcurrentLinkedQueue<byte[]> packetQueue;
	private static final String TAG = "WifiServerThread"; 
	

	/**
	 * Constructor with the queue
	 * @param port
	 * @param queue
	 */
	public WifiServerThread(int port, ConcurrentLinkedQueue<byte[]> queue) {
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
				if (WifiUtil.isGroupOwner)
				{
					WifiUtil.neighbours.add(socket.getInetAddress().getHostAddress());
					Log.v("server socket", "adding client"); 
				}
				
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
				
				
				Log.v(TAG,"Got something ");
			    Message m = new Message(); 
			    m.what = 1; 
			    MainApplication.wifiMessage.sendMessage(m); 
				this.packetQueue.add(trimmedBytes);
				socket.close();
				WifiUtil.m_WifiHandler.sendEmptyMessage(1); 
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

}