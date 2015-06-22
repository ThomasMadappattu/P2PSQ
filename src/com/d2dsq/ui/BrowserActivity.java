package com.d2dsq.ui;


import com.example.test123.R;
import com.example.test123.R.layout;
import com.example.test123.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;

public class BrowserActivity extends Activity implements OnClickListener
{

	WebView page;
	EditText url;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		page = (WebView) findViewById(R.id.wvPage);
		url = (EditText) findViewById(R.id.edURL);

		page.getSettings().setJavaScriptEnabled(true);
		page.getSettings().setUseWideViewPort(true);
		page.getSettings().setLoadWithOverviewMode(true);
		page.getSettings().setBuiltInZoomControls(true);

		page.setWebViewClient(new PageViewClient());
		page.loadUrl("https://piazza.com");

		findViewById(R.id.bGo).setOnClickListener(this);
		findViewById(R.id.bBack).setOnClickListener(this);
		findViewById(R.id.bForward).setOnClickListener(this);
		findViewById(R.id.bRefresh).setOnClickListener(this);
		findViewById(R.id.bClear).setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browser, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{

		case R.id.bGo:
			if (!url.getText().toString()
					.regionMatches(true, 0, "http://", 0, "http://".length()))
			{
				url.setText("http://" + url.getText().toString());
			}
			page.loadUrl(url.getText().toString());
			
			break;

		case R.id.bBack:
			if (page.canGoBack())
			{
				page.goBack();
			}
			break;

		case R.id.bForward:
			if (page.canGoForward())
			{
				page.goForward();
			}
			break;

		case R.id.bRefresh:
			page.reload();
			break;

		case R.id.bClear:
			page.clearHistory();
			break;
		}
	}

}
