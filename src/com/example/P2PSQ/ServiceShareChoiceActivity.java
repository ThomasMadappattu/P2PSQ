package com.example.P2PSQ;

import com.example.test123.R;
import com.example.test123.R.layout;
import com.example.test123.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
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
		passedData = getIntent().getExtras();
		if (passedData != null)
		{
			avServices = passedData.getString("avServices");
		}
		List<String> serviceList = new ArrayList<String>();
		for (String item : avServices.split(","))
		{
			serviceList.add(item);
		}

		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, serviceList);
		serviceSpinner.setAdapter(adapter);
		doneButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				boolean bFirst = false;
				String configString = "";
				if (smsCheckBox.isChecked())
				{
					configString += "SMS";
					bFirst = true;
				}
				if (camCheckBox.isChecked())
				{
					if (bFirst)
					{
						configString += ",CAM";
					} else
					{
						configString = "CAM";
						bFirst = true;
					}
				}
				if (inetCheckBox.isChecked())
				{

					if (bFirst)
					{
						configString += ",INET";
					} else
					{
						configString = "INET";
					}
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
