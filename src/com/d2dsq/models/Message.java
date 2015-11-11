package com.d2dsq.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private static final String TAG = "Message";
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	public static final int AUDIO_MESSAGE = 4;
	public static final int FILE_MESSAGE = 5;
	public static final int DRAWING_MESSAGE = 6;
	public static final int DISCOVER_MESSAGE = 7;
	public static final int RESPONSE_MESSAGE = 8; 
	public static final int REQUEST_MESSAGE  = 9 ; 

	public static final int REQUEST_MESSAGE_SMS = 10; 
	public static final int REQUEST_MESSAGE_CAM = 11; 
	public static final int REQUEST_MESSAGE_INET = 12;
	
	public static final int RESPONSE_MESSAGE_DATA = 13; 
 
	
	
	public static final int DISCOVER_SIZE = 8096; 
	
	
	private int mType;
	private String mText;
	private String chatName;
	private byte[] byteArray;
	private InetAddress senderAddress;
	private String fileName;
	private long fileSize;
	private String filePath;
	private boolean isMine;
	
	
	private String path;
	private String service; 
	private String respPath; 

	
	// SMS data 
	

	private String destPhone; 


	
   	 
	
	
	
	
	
	//Getters and Setters
	public void  setDestPhone(String ph)
	{
		destPhone  = ph; 
	}
	
	
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public String getChatName() { return chatName; }
	public void setChatName(String chatName) { this.chatName = chatName; }
	public byte[] getByteArray() { return byteArray; }
	public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
	public InetAddress getSenderAddress() { return senderAddress; }
	public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }
	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }
	public boolean isMine() { return isMine; }
	public void setMine(boolean isMine) { this.isMine = isMine; }
	
	
	
	public void setResponsePath(String repPath)
	{
		respPath = repPath; 
	}
	
	
	
	public byte[] CreateSMSRequestPacket()
	{
		byte[] SMSRequestPacket = new byte[4 * DISCOVER_SIZE]; 
		byte[] bService = service.getBytes(); 
		byte[] bPath =  path.getBytes(); 
		byte[] destPh = destPhone.getBytes(); 
		byte[] text   = mText.getBytes(); 
		
		
		SMSRequestPacket[0] = REQUEST_MESSAGE; 
		System.arraycopy(bService, 0, SMSRequestPacket, 1, bService.length); 
		System.arraycopy(bPath, 0, SMSRequestPacket, 1024, bPath.length);
		SMSRequestPacket[8096] = REQUEST_MESSAGE_SMS;
		System.arraycopy(destPh, 0, SMSRequestPacket, 8097, destPh.length); 
		System.arraycopy(text, 0, SMSRequestPacket, 8097 + 1024 , text.length); 
		
		
	
	    return SMSRequestPacket; 
	}
	
	public byte[] CreateResponsePacket()
	{
		byte[] responsePacket = new byte[ 2 * DISCOVER_SIZE]; 
		byte[] bPath =  path.getBytes(); 
		byte[] bService = service.getBytes(); 
		byte[] bPath2 =  respPath.getBytes(); 
	   
		responsePacket[0] = RESPONSE_MESSAGE; 
		   
		System.arraycopy(bService, 0, responsePacket, 1, bService.length); 
		System.arraycopy(bPath, 0, responsePacket, 1024, bPath.length);
		System.arraycopy(bPath2, 0, responsePacket, 8096, bPath2.length); 
	
		
		return responsePacket; 
	}
	
	public byte[] CreateResponsePacketWithData(byte packType)
	{
		
		byte[] responsePacket = new byte[ 2 * DISCOVER_SIZE + 4 + 1 + byteArray.length] ; 
		byte[] bPath =  path.getBytes(); 
		byte[] bService = service.getBytes(); 
		byte[] bPath2 =  respPath.getBytes(); 
	   
		responsePacket[0] = RESPONSE_MESSAGE; 
		   
		System.arraycopy(bService, 0, responsePacket, 1, bService.length); 
		System.arraycopy(bPath, 0, responsePacket, 1024, bPath.length);
		System.arraycopy(bPath2, 0, responsePacket, 8096, bPath2.length); 
		
		
		BigInteger packetLen = BigInteger.valueOf(byteArray.length);  
		byte[] respLen = packetLen.toByteArray();
	    
		
		// set packet Type  
		responsePacket[ 2 * DISCOVER_SIZE + 4 + 1] = packType; 
		
		System.arraycopy(respLen,0,responsePacket , 2 * DISCOVER_SIZE,respLen.length ); 	
		System.arraycopy(byteArray, 0, responsePacket,  2 * DISCOVER_SIZE + 4,  byteArray.length);
	    
	    
		
		
	    return responsePacket; 
		
	}
	
	
	public byte[] CreateDiscoverPacketByteArray()
	{
	   
	   
	   byte[] bPath =  path.getBytes(); 
	   byte[] bService = service.getBytes(); 
	   
	   byte[] discoverPacket = new byte[DISCOVER_SIZE];
	   
	   discoverPacket[0] = DISCOVER_MESSAGE; 
	   
	   System.arraycopy(bService, 0, discoverPacket, 1, bService.length); 
	   System.arraycopy(bPath, 0, discoverPacket, 1024, bPath.length); 
	   
	   
	   
	   return discoverPacket; 
		
	}
	
	public Message(int type, String text, InetAddress sender, String name){
		mType = type;
		mText = text;	
		senderAddress = sender;
		chatName = name;
	}
	
	public void SetDiscoverMessage(String service, String path)
	{
		this.service = service; 
		this.path  = path; 
		
		
	}
	public Bitmap byteArrayToBitmap(byte[] b){
		Log.v(TAG, "Convert byte array to image (bitmap)");
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
	
	public void saveByteArrayToFile(Context context){
		Log.v(TAG, "Save byte array to file");
		switch(mType){
			case Message.AUDIO_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/"+fileName;
				break;
			case Message.VIDEO_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/"+fileName;
				break;
			case Message.FILE_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName;
				break;
			case Message.DRAWING_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+fileName;
				break;
		}
		
		File file = new File(filePath);

		if (file.exists()) {
			file.delete();
		}

		try {
			FileOutputStream fos=new FileOutputStream(file.getPath());

			fos.write(byteArray);
			fos.close();
			Log.v(TAG, "Write byte array to file DONE !");
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Write byte array to file FAILED !");
		}
	}
}
