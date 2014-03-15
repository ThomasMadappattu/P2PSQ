package com.example.P2PSQ;

import com.example.test123.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import java.util.*;
import android.widget.*;
import android.content.*;
import android.bluetooth.*;  
import java.io.*;



public class MainActivity extends Activity {
	
	
	 private BluetoothAdapter bluetooth;
	 private BluetoothSocket socket;
	 BluetoothDevice pairedDevice=null;
	 private UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
     private Button sendButton; 
	 private EditText textMsg;
	 private EditText destPhone; 
	 int REQUEST_ENABLE_BT = 3; 
	 BloothServiceHandler btServer ; 
	 
//	 ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textMsg = (EditText)findViewById(R.id.editText3); 
		sendButton = (Button)findViewById(R.id.button1);
		destPhone = (EditText)findViewById(R.id.editText2); 
	    
		configBluetooth(); 
		if ( !bluetooth.isEnabled())
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            
			 
		}
		 
		
		if (bluetooth.getBondedDevices().size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : bluetooth.getBondedDevices()) {
		        // Add the name and address to an array adapter to show in a ListView
		        Toast.makeText(this , device.getName() , Toast.LENGTH_SHORT).show();
		    	//  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		    }
		    pairedDevice =  (BluetoothDevice) (bluetooth.getBondedDevices().toArray()[0]);  
		    
		}
		btServer = new BloothServiceHandler(this ,mHandler); 
		btServer.start(); 
		if( pairedDevice != null)
		{
			btServer.connect(pairedDevice);
			
		}
		String msgToSend; 
		sendButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        // Do something in response to button click
		    	sendMessage(destPhone.getText() + ":" +textMsg.getText());
		    }
		});
	}
	private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (btServer.getState() != BloothServiceHandler.STATE_CONNECTED) {
            Toast.makeText(this,"sendMesssage:not connected", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {

            // XXX !!!
            message = message + "\r\n"; // terminate for pc bluetooth spp server

            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            btServer.write(send);

            // Reset out string buffer to zero and clear the edit text field
           
            // mOutEditText.setText(mOutStringBuffer);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void configBluetooth()
	{
		this.bluetooth = BluetoothAdapter.getDefaultAdapter(); 
	}
	
	
	 private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case BloothServiceHandler.MESSAGE_STATE_CHANGE:
	               
	                switch (msg.arg1) {
	                case BloothServiceHandler.STATE_CONNECTED:
	                   
	                    break;
	                case BloothServiceHandler.STATE_CONNECTING:
	                    
	                    break;
	                case BloothServiceHandler.STATE_LISTEN:
	                case BloothServiceHandler.STATE_NONE:
	                    
	                    break;
	                }
	                break;
	            case BloothServiceHandler.MESSAGE_WRITE:
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                //mConversationArrayAdapter.add("Me:  " + writeMessage);
	                //Toast.makeText(null,"sending sms to  " +  writeMessage.split(":")[0] , Toast.LENGTH_SHORT).show();
	                Log.d("Hanlder"  ,"sending sms to  " +  writeMessage.split(":")[0]);
	                SmsUtil.SendSms(writeMessage.split(":")[0], writeMessage);
	                break;
	            case BloothServiceHandler.MESSAGE_READ:
	                byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	               String readMessage = new String(readBuf, 0, msg.arg1);
	               Log.d("Hanlder"  ,"sending sms to  " +  readMessage.split(":")[0]);
	               SmsUtil.SendSms(readMessage.split(":")[0], readMessage);
	               // mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
	               //         + readMessage);
	                break;
	            case BloothServiceHandler.MESSAGE_DEVICE_NAME:
	                
	                break;
	            case BloothServiceHandler.MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(),
	                        msg.getData().getString("toast"), Toast.LENGTH_SHORT)
	                        .show();
	                break;
	            }
	        }
	 };
}
