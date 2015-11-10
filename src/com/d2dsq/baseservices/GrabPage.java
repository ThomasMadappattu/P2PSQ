package com.d2dsq.baseservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;

public class GrabPage {

	
	
	public void grabWebPage(String url){
		// variable declarations
		final int CONTENT_SIZE = 99999; // content size
		URL requestedUrl = null; // url
		InputStream input = null; // inputStream
 		BufferedReader buffer = null; // bufferReader
		String line; // line
		byte [] htmlContent; // byte array for the content
		
		try {
			requestedUrl = new URL(url);
			input = requestedUrl.openStream();
			buffer = new BufferedReader(new InputStreamReader(input));
			htmlContent = new byte[CONTENT_SIZE];
			
			// read html page
			int destPos = 0;
			while ( ( line = buffer.readLine() ) != null ){
				// line to byte array
				byte[] byteLine = line.getBytes();
				// copy line to content array
				System.arraycopy(byteLine, 0, htmlContent, destPos, line.length());
				destPos += line.length(); // increase the destination position
				
				// print the result
				// String byteString = new String(byteLine, "UTF-8");
				
				// System.out.println(byteString);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	} // grabWebPage function
	


}