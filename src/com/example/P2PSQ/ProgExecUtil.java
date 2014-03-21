package com.example.P2PSQ;

import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class ProgExecUtil
{
	public static boolean executeAsRoot(String command)
	{
		try
		{

			Process suProcess = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(
					suProcess.getOutputStream());

			os.writeBytes(command + "\n");
			os.flush();
			Log.d("ROOT", command);

			os.writeBytes("exit\n");
			os.flush();

			try
			{
				int suProcessRet = suProcess.waitFor();
				if (255 != suProcessRet)
					return true;
				else
					return false;
			} catch (Exception ex)
			{
				Log.w("ROOT-ERROR", ex);
			}
		} catch (IOException ex)
		{
			Log.w("ROOT", "Can't get root access", ex);
		} catch (SecurityException ex)
		{
			Log.w("ROOT", "Can't get root access", ex);
		} catch (Exception ex)
		{
			Log.w("ROOT", "Error executing internal operation", ex);
		}
		return false;
	}
}
