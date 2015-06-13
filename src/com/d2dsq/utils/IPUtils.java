package com.d2dsq.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class IPUtils
{

	private final static String p2pInt = "p2p-p2p0";
    private static final String LOG_TAG = "Utils";
    public static volatile boolean sIsDialogOpen = false;
	private static AlertDialog sOkDialog = null;


	public static String getIPFromMac(String MAC)
	{
		/*
		 * method modified from:
		 * 
		 * http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac
		 * -address-of-a-remote-host/
		 */
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null)
			{

				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4)
				{
					// Basic sanity check
					String device = splitted[5];
					if (device.matches(".*" + p2pInt + ".*"))
					{
						String mac = splitted[3];
						if (mac.matches(MAC))
						{
							return splitted[0];
						}
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	  
	 

	   public static void showToast(final Context context, final int resourceId)
	   {
	      IPUtils.showToast(context, context.getString(resourceId));
	   }

	   public static void showToast(final Context context, final String toastMsg)
	   {
	      if (context != null) {
	         ((Activity) context).runOnUiThread(new Runnable()
	         {
	            @Override
	            public void run()
	            {
	               Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
	            }
	         });
	      }
	   }

	   public static void showOkMsg(final Context context, final int resourceId, final DialogInterface.OnClickListener action)
	   {
	      IPUtils.showOkMsg(context, context.getString(resourceId), action);
	   }

	   public static void showOkMsg(final Context context, final String okMsg, final DialogInterface.OnClickListener action)
	   {
	      if (context == null || IPUtils.sIsDialogOpen) {
	         return;
	      }

	      IPUtils.sIsDialogOpen = true;

	      ((Activity) context).runOnUiThread(new Runnable()
	      {
	         @Override
	         public void run()
	         {
	            if (sOkDialog == null) {
	               sOkDialog = new AlertDialog.Builder(context).create();
	               sOkDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener()
	               {
	                  @Override
	                  public void onClick(DialogInterface dialog, int which)
	                  {
	                	  IPUtils.sIsDialogOpen = false;
	                  }
	               });
	               sOkDialog.setTitle("ShipsDroid");
	               sOkDialog.setCancelable(false);
	            }

	            sOkDialog.setMessage(okMsg);
	            sOkDialog.show();
	         }
	      });
	   }

	   public static void closeOkMsg(final Context context)
	   {
	      if (context == null || !IPUtils.sIsDialogOpen) {
	         return;
	      }

	      ((Activity) context).runOnUiThread(new Runnable()
	      {
	         @Override
	         public void run()
	         {
	            if (sOkDialog != null) {
	               sOkDialog.dismiss();
	            }
	            IPUtils.sIsDialogOpen = false;
	         }
	      });
	   }


	   public static Inet4Address getLocalIpAddress()
	   {

	      try {
	         for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
	            NetworkInterface networkInterface = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
	               InetAddress inetAddress = enumIpAddr.nextElement();
	               if (!inetAddress.isLoopbackAddress()) {
	                  if (inetAddress instanceof Inet4Address) {
	                     return (Inet4Address)inetAddress;
	                  }
	               }
	            }
	         }
	      } catch (Exception e) {
	         Log.e(LOG_TAG, "getLocalIpAddress()",e);
	      }

	      Inet4Address addr = null;
	      try { addr = (Inet4Address)Inet4Address.getByName("1.1.1.1"); } catch (Exception e) {/*IGNORED*/}
	      return addr;
	   }


	   public static void launchWifiSettings(final Activity activity)
	   {
	      try {
	         Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
	         activity.startActivity(intent);
	         //activity.startActivityForResult(intent,0);
	      } catch (ActivityNotFoundException e) {
	         Log.e(LOG_TAG, "Unable to launch wifi settings activity", e);
	         activity.runOnUiThread(new Runnable()
	         {
	            @Override
	            public void run()
	            {
	               Toast.makeText(activity, "Please enable Wifi", Toast.LENGTH_SHORT).show();
	            }
	         });
	      }
	   }
  
	public static String getLocalIPAddress()
	{
		/*
		 * modified from:
		 * 
		 * http://thinkandroid.wordpress.com/2010/03/27/incorporating-socket-
		 * programming-into-your-applications/
		 */
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();

					String iface = intf.getName();
					if (iface.matches(".*" + p2pInt + ".*"))
					{
						if (inetAddress instanceof Inet4Address)
						{ // fix for Galaxy Nexus. IPv4 is easy to use :-)
							return getDottedDecimalIP(inetAddress.getAddress());
						}
					}
				}
			}
		} catch (SocketException ex)
		{
			Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		} catch (NullPointerException ex)
		{
			Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
		}
		return null;
	}

	private static String getDottedDecimalIP(byte[] ipAddr)
	{
		/*
		 * ripped from:
		 * 
		 * http://stackoverflow.com/questions/10053385/how-to-get-each-devices-ip
		 * -address-in-wifi-direct-scenario
		 */
		String ipAddrStr = "";
		for (int i = 0; i < ipAddr.length; i++)
		{
			if (i > 0)
			{
				ipAddrStr += ".";
			}
			ipAddrStr += ipAddr[i] & 0xFF;
		}
		return ipAddrStr;
	}
}
