package com.d2dsq.utils;

import android.app.Activity;
import android.os.Build;
import android.widget.TextView;

import com.example.test123.*;;

public class ActivityUtilities {

	public static void customiseActionBar(Activity activity)
    {
        int titleId = 0;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            titleId = activity.getResources().getIdentifier("action_bar_title", "id", "android");
        else
            titleId = R.id.action_config;

        if(titleId>0){
            TextView titleView = (TextView) activity.findViewById(titleId);
            titleView.setTextSize(22);
        }
    }
	
}
