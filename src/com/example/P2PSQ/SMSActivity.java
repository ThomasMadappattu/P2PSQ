package com.example.P2PSQ;

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
				//int deviceIndex = ConfigManager.Get("useService").indexOf(':'); 
				String deviceName = ConfigManager.Get("useService");//.substring(0,deviceIndex);
				if ( deviceName.contains(":"))
				{
                     deviceName  = deviceName.split(":")[0]; 					
				}
				BlutoothServiceUtil.SendMessage("SMS:" +destPh + ":" +msg,deviceName);
				
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
