package com.example.P2PSQ;

import android.telephony.SmsManager;

public class SmsUtil
{

	public static void SendSms(String phoneNum, String msgText)
	{
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNum, null, msgText, null, null);
	}
}
