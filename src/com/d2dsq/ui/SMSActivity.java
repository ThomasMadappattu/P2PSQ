package com.d2dsq.ui;

import com.d2dsq.routing.RoutingManager;
import com.d2dsq.utils.ConfigManager;
import com.example.test123.R;
import com.example.test123.R.layout;
import com.example.test123.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import java.util.*;
import android.widget.*;
import android.content.*;
public class SMSActivity extends Activity
{

	Button sendButton ; 
	EditText destNum;
	EditText msgText; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		
		sendButton = (Button)findViewById(R.id.buttonSend);
		destNum = (EditText)findViewById(R.id.editText1);
		msgText = (EditText)findViewById(R.id.editText2);
		
		sendButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				String destPh = destNum.getText().toString();
				String msg = msgText.getText().toString();
				String restOfDev=""; 
				String firstDev; 
				//deviceIndex = ConfigManager.Get("useService").indexOf(':'); 
				String deviceName = ConfigManager.Get("useService");//.substring(0,deviceIndex);
				firstDev = deviceName;  
				String destDev ;
				//= RoutingManager.theRouter.getServicePath("SMS").split("#")[1].split(":")[0];
				 String prefPath ; 
				if ( ConfigManager.configValues.containsKey("SMS_PATH"))
				{
				
				       prefPath =  ConfigManager.Get("SMS_PATH");  
				}
				else
				{
					   prefPath =   RoutingManager.theRouter.getServicePath("SMS"); 
					
				}
				destDev  = prefPath.split("#")[1].split(":")[0];
				String nextDev = prefPath.split("#")[1]; 
				
				if ( RoutingManager.GetNodeType(nextDev)  == RoutingManager.TYPE_BLUETOOTH ) 
				{
					RoutingManager.theRouter.SendRequestMessageBluetoothSMS("SMS",prefPath, destPh, msg, destDev);	  
				}
				else 
				{
					RoutingManager.theRouter.SendRequestMessageWifiSMS("SMS",prefPath, destPh, msg, destDev);	  
					
				}
				
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sm, menu);
		return true;
	}

}
