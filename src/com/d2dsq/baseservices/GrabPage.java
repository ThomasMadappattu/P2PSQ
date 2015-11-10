package com.d2dsq.baseservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GrabPage {

	
	
	public static byte[] grabWebPage(String url){
		// variable declarations
		URL requestedUrl = null; // url
		InputStream input = null; // inputStream
 		BufferedReader buffer = null; // bufferReader
		String line; // line
		
		// linked list that stores byte arrays
		LinkedList<byte[]> htmlContent = new LinkedList<byte[]>();

		try {
			requestedUrl = new URL(url);
			input = requestedUrl.openStream();
			buffer = new BufferedReader(new InputStreamReader(input));
			
			// read html page
			while ( ( line = buffer.readLine() ) != null ){
				
				// line to byte array
				byte[] byteLine = line.getBytes();
				
				// copy byte line list to html Content
				htmlContent.addLast(byteLine);

				
				// print the result
				//System.out.println(new String(byteLine));
			} // while
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int sizeOfArray = 0;
		for ( int i = 0; i < htmlContent.size(); i ++ ){
			sizeOfArray += htmlContent.get(i).length;
		}
		
		byte[] resultArray = new byte[sizeOfArray];
		int destPos = 0;
		for ( int i = 0; i < htmlContent.size(); i ++ ){
			System.arraycopy(htmlContent.get(i), 0, resultArray, destPos, htmlContent.get(i).length);
			destPos += htmlContent.get(i).length;
		}
		
		return resultArray;
	} // grabWebPage function
	
	

}