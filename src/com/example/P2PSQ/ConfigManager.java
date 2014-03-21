package com.example.P2PSQ;

import java.util.Properties;

public class ConfigManager
{

	public static Properties configValues = new Properties();

	public static void Set(String key, String value)
	{
		configValues.setProperty(key, value);
	}

	public static String Get(String key)
	{
		return configValues.getProperty(key);
	}

}
