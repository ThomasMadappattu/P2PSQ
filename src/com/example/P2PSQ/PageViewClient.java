package com.example.P2PSQ;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PageViewClient extends WebViewClient
{

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url)
	{

		view.loadUrl(url);
		return true;
	}
}