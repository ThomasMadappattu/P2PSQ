

package com.d2dsq.radio;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;

import com.d2dsq.ui.*;
import com.d2dsq.models.*;
import com.example.test123.*;

public class WifiClientReceiveThread extends Thread
{
	


		private static final int SERVER_PORT = 7000 ;
		
		private ServerSocket serverSocket;

	
		
		@Override
		public void run() 
		{
			try {
				serverSocket = new ServerSocket(SERVER_PORT);
				while(true){
					Socket clientSocket = serverSocket.accept();				
					
					InputStream inputStream = clientSocket.getInputStream();				
					ObjectInputStream objectIS = new ObjectInputStream(inputStream);
					Message message = (Message) objectIS.readObject();
					
					//Add the InetAdress of the sender to the message
					InetAddress senderAddr = clientSocket.getInetAddress();
					message.setSenderAddress(senderAddr);
					
					clientSocket.close();
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	        
			
		}

	
	
}
