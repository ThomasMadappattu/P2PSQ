package com.example.P2PSQ;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.*;

public class BlutoothServiceUtil
{
	public static BloothServiceHandler btServer; 
	
	public static void Init(Context cxt , Handler mHandler )
	{
		btServer = new BloothServiceHandler(cxt , mHandler);
		btServer.start();
	}
	
	
	public  static void SendMessage()
	{
		
	}
	
	public static void TakePicture()
	{
	
	
	}
	
	public static void UpdateService()
	{
	
	}
	

}
