package com.d2dsq.ui;

import com.d2dsq.routing.RoutingManager;
import com.d2dsq.utils.ConfigManager;
import com.example.test123.R;
import com.example.test123.R.layout;
import com.example.test123.R.menu;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.*;

public class ServiceShareChoiceActivity extends Activity
{

	CheckBox smsCheckBox;
	CheckBox camCheckBox;
	CheckBox inetCheckBox;
	Button doneButton;
	Spinner serviceSpinner;
	Bundle passedData = null;
	String avServices;
	Set<String> list = new HashSet<String>();
	Context actContext = this; 
	CountDownTimer mTimer = new CountDownTimer(5000,5000)
				
	{
		
		@Override
		public void onTick(long millisUntilFinished)
		{
			// TODO Auto-generated method stub
			
	            List<String> paths = new ArrayList<String>();   		
	            Set<String> keys = RoutingManager.theRouter.getPassiveKeys(); 
	            for ( String key : keys)
	            {
	            	String[] sPaths = RoutingManager.theRouter.getPassiveRouteMap(key).split("\n");
	            	for ( String path: sPaths)
	            	{
	            		
	            		if ( path.length() > 2 ) 
	            		{
	            		              String comboItem = key+"@"+ path; 
	            		              paths.add(comboItem);  
	            		}
	            	}
	            	
	            }
	            ArrayAdapter adapter = new ArrayAdapter(actContext,
	    				android.R.layout.simple_spinner_item, paths);
	    		serviceSpinner.setAdapter(adapter);
	           
 	            
			
		}
		
		@Override
		public void onFinish()
		{
			// TODO Auto-generated method stub
			
		}
	}; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_share_choice);
		smsCheckBox = (CheckBox) findViewById(R.id.smsCheckBox);
		camCheckBox = (CheckBox) findViewById(R.id.cameraCheckBox);
		inetCheckBox = (CheckBox) findViewById(R.id.internetCheckbox);
		doneButton = (Button) findViewById(R.id.doneButton);
		serviceSpinner = (Spinner) findViewById(R.id.spAvServices);
		
		
		
		/* 
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, serviceList);
		serviceSpinner.setAdapter(adapter);
		*/
		
		
		doneButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				
				String currentItem = serviceSpinner.getSelectedItem().toString(); 
				
				if ( currentItem.length() > 2 )
				{
					// Check sms ,  
					if ( currentItem.contains("SMS@") ) 
					{
						
					     ConfigManager.Set("SMS_PATH", currentItem.split("@")[1]);
					   
						
					}
					
					// do the same thing for camera internet and other services 
					
					
				}
				
				// TODO Auto-generated method stub
				boolean bFirst = false;
				String configString = "";
				if (smsCheckBox.isChecked())
				{
					configString += "SMS";
					bFirst = true;
					ConfigManager.Set("USESMS", "yes"); 
					
				}
				else
				{
					ConfigManager.Set("USESMS", "no"); 
					
				}
				if (camCheckBox.isChecked())
				{
					ConfigManager.Set("USECAM", "yes");
					
				}
				else
				{
					ConfigManager.Set("USECAM", "no");
				}
				if (inetCheckBox.isChecked())
				{
					ConfigManager.Set("USEINET", "yes");
					

				}
				else
				{
					ConfigManager.Set("USEINET", "no");
				}
				ConfigManager.Set("sharedServices", configString);
				ConfigManager.Set("useService", serviceSpinner
						.getSelectedItem().toString());
					Quit();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.service_share_choice, menu);
		return true;
	}

	private void Quit()
	{
		this.finish();
	}

}
