package com.d2dsq.utils;

import java.io.UnsupportedEncodingException;

public class ByteUtil
{

     public static  String  ByteArrayToString(byte[] obj) throws UnsupportedEncodingException
     {
    	   return new String(obj).trim();
    	 
     }
     
     public static int fromByteArray(byte[] bytes) {
         return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }


}
