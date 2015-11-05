package com.d2dsq.utils;

import java.io.UnsupportedEncodingException;

public class ByteUtil
{

     public static  String  ByteArrayToString(byte[] obj) throws UnsupportedEncodingException
     {
    	   return new String(obj).trim();
    	 
     }


}
