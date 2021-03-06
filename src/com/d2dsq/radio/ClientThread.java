package com.d2dsq.radio;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@SuppressLint("HandlerLeak")
public class ClientThread extends Thread {
    private final String TAG = "com.d2dsq.radio/ClientThread";
    private final BluetoothSocket socket;
    private final Handler handler;
    public Handler incomingHandler;
    private volatile boolean  isConnectionEstablished;   
    public ClientThread(BluetoothDevice device, Handler handler) {
        BluetoothSocket tempSocket = null;
        this.handler = handler;

        try {
        	// Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
        	
        	tempSocket =  device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID_STRING));;
        	
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        this.socket = tempSocket;
        isConnectionEstablished = false; 
       
    }
    
    public boolean hasConnectionEstablished()
    {
    	
    	return isConnectionEstablished; 
    }

    public void run() {
        try {
            Log.v(TAG, "Opening client socket");
            socket.connect();
            Log.v(TAG, "Connection established");
            isConnectionEstablished = true; 

        } catch (IOException ioe) {
            handler.sendEmptyMessage(MessageType.COULD_NOT_CONNECT);
            Log.e(TAG, ioe.toString());
            // Try the fall back methods now 
           
            
            
            Log.e(TAG, "Trying fall back");
            
        }

        Looper.prepare();

        incomingHandler = new Handler(){
            @Override
            public void handleMessage(Message message)
            {
                if (message.obj != null)
                {
                    Log.v(TAG, "Handle received data to send");
                    byte[] payload = (byte[])message.obj;

                    try {
                        handler.sendEmptyMessage(MessageType.SENDING_DATA);
                        OutputStream outputStream = socket.getOutputStream();

                        // Send the header control first
                        outputStream.write(Constants.HEADER_MSB);
                        outputStream.write(Constants.HEADER_LSB);

                        // write size
                        outputStream.write(Utils.intToByteArray(payload.length));

                        // write digest
                        byte[] digest = Utils.getDigest(payload);
                        outputStream.write(digest);

                        // now write the data
                        outputStream.write(payload);
                        outputStream.flush();

                        Log.v(TAG, "Data sent.  Waiting for return digest as confirmation");
                        InputStream inputStream = socket.getInputStream();
                        byte[] incomingDigest = new byte[16];
                        int incomingIndex = 0;

                        try {
                            while (true) {
                                byte[] header = new byte[1];
                                inputStream.read(header, 0, 1);
                                incomingDigest[incomingIndex++] = header[0];
                                if (incomingIndex == 16) {
                                    if (Utils.digestMatch(payload, incomingDigest)) {
                                        Log.v(TAG, "Digest matched OK.  Data was received OK.");
                                        ClientThread.this.handler.sendEmptyMessage(MessageType.DATA_SENT_OK);
                                    } else {
                                        Log.e(TAG, "Digest did not match.  Might want to resend.");
                                        ClientThread.this.handler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
                                    }

                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }

                        Log.v(TAG, "Closing the client socket.");
                        socket.close();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                }
            }
        };

        handler.sendEmptyMessage(MessageType.READY_FOR_DATA);
        Looper.loop();
    }

    public void cancel() {
        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}