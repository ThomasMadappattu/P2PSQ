package com.d2dsq.routing;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.*;
import com.d2dsq.utils.ByteUtil;
import com.d2dsq.utils.ConfigManager;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.d2dsq.baseservices.SmsUtil;
import com.d2dsq.models.Message;

public class RoutingManager
{

	   private HashMap routeMap = new HashMap(); 
	   private Set<Node> neighbours = new HashSet();
	   public final static String BLUETOOTH_PREFIX = ":B"; 
	   public static RoutingManager theRouter = new RoutingManager(); 
	 
	   
	   
	   public int getPathHops(String path)
	   {
		   
		   String[] nodes =  path.split("#"); 
		   return nodes.length; 
	   }
	   
	   
	   
	   
	   public void addRoute(String service, String path)
	   {
 		    
		   if ( !routeMap.containsKey(service) )
		   { 
		        routeMap.put(service, path);  
		   }
		   else
		   {
			   String existingPath  =  routeMap.get(path).toString(); 
			   if (getPathHops(path) < getPathHops(existingPath))
			   {
				   routeMap.put(service, path); 
			   }
		   }
		   
	   }
	   
	   public void addRoute(Node node, String path)
	   {
		   
		   routeMap.put(node.getNodeName(), path);
		   
	   }
	   
	   public String getServicePath( String service)
	   {
		  
		   if ( routeMap.containsKey(service) )
		   {
			   
			   return routeMap.get(service).toString(); 
		   }
		   return "None"; 
		   
	   }
	   
	   public String GetCompletePath(String node)
	   {
		   StringBuilder retString  = new StringBuilder(); 
		   retString.append(node);
		   retString.append("#");
		   retString.append(routeMap.get(node));
		   return retString.toString();
		   
	   }
	   
	   
	   
	   public String joinNodes( String node1, String node2)
	   {
		   return node1 +"#" + node2; 
	   }
	   
	   public String GetCompletePath(Node node)
	   {
		   
		   return GetCompletePath(node.nodeName);
		   
	   }
	   
	   public boolean isNeighbour(String nodeName)
	   {
		   
		   return neighbours.contains(nodeName);
	   }
	   
	   public void addNeighbour(Node node)
	   {
		   
		   neighbours.add(node); 
	   }
	 
	   public void FlushRoutes()
	   {
		  routeMap.clear();
	   }
	   
	   public void FlushNeighBours()
	   {
		   
		   neighbours.clear();
	   }
	   
	   public void FlushAll()
	   {
		   neighbours.clear();
		   routeMap.clear();
		   
	   }
	   /*
	    *   Method: SendDiscoverMessagesBluetooth  
	    *   description : Sending Discover Packets to bluetooth devices
	    * 
	    */
	   public void SendDiscoverMessagesBluetooth(String service) throws UnknownHostException
	   {
		    
		   BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service, GetBluetoothName()); 
		   disThread.start(); 
		    
	   
	   }
	   
	   public void SendDiscoverMessagesBluetooth(String service , String path) throws UnknownHostException
	   {
		    
		   BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service, path); 
		   disThread.start(); 
		    
	   
	   }
	   
	   public void SendResponseMessageBluetooth(String service, String path1 , String path2 , String device)
	   {
		   BluetoothResponseThread th  = new BluetoothResponseThread(service, path1, path2, device); 
		   th.start(); 
		   
	   }
	   
	   public void SendRequestMessageBluetoothSMS(String service, String path, String destPh, String text , String destDevice)
	   {
		   BluetoothRequestThread th = new BluetoothRequestThread(); 
		   th.setDestPh(destPh); 
		   th.setService(service);
		   th.setPath(path);
		   th.setTextMsg(text); 
		   th.setDesDev(destDevice); 
		   th.start(); 
		   
		   
	   }
	  
	   public String GetBluetoothName()
	   {
		   
		  return  BluetoothUtil.adapter.getName() + BLUETOOTH_PREFIX; 
	   }
	   
	   
	   public void HandleBluetoothRequest( byte[]  requestPacket) throws UnsupportedEncodingException
	   {
		   // get path, service and phone
		   byte[] service = new byte[1025];  
		   byte[] path = new byte[8096-1024];
		   byte[] requestType = new byte[1];
		   byte[] phone = new byte[1024];
		   byte[] text = new byte[4*8096 - (8097 + 1024)];
 		   
		   // create the request packet
		   System.arraycopy(requestPacket, 1, service, 0, 1023);
		   System.arraycopy(requestPacket, 1024, path, 0, 8096-1024);
		   System.arraycopy(requestPacket, 8096, requestType, 0, 1);
		   System.arraycopy(requestPacket, 8097, phone, 0, 1024);
		   System.arraycopy(requestPacket, 8097+1024, text, 0, 4*8096 - (8097+ 1024));
		   
		   
		   
		   // get the path and service name as string
		   String strPath = ByteUtil.ByteArrayToString(path);
		   String servName = ByteUtil.ByteArrayToString(service);
		   String desPhone = ByteUtil.ByteArrayToString(phone);
		   String mesText = ByteUtil.ByteArrayToString(text);
		   
		   
		   //  if request is for this node, then, service the request
		   String updatedPath = RemoveFirstNodeName(strPath);
		   String [] nodes = updatedPath.split("#");
		   if ( nodes.length == 1  && ( GetLastNodeName(updatedPath).compareTo(BluetoothUtil.adapter.getName()) == 0 ) ){
			   
			   SmsUtil.SendSms(desPhone, mesText);
			   
		   }else{
			   
			   // if the request is not for this node, update path and forward to next node 
			   String nextNode = GetFirstNodeName(strPath);
			  
			   
			   // send request message
			   SendRequestMessageBluetoothSMS(servName, updatedPath, desPhone, mesText,nextNode);
		   }
		   
		   
	   }
	   
	   public void HandleBluetoothResponseMessage( byte[] responsePacket) throws UnsupportedEncodingException
	   {
		   
		   // Seperate out path1 , path2 
		   byte[] path1 = new byte[8096-1024];
		   byte[] path2 = new byte[8096];
		   byte[] service = new byte[1025];   
		   
		   System.arraycopy(responsePacket, 1024, path1,0, 8096-1024); 
		   System.arraycopy(responsePacket, 8096, path2, 0 , 8096); 
		   System.arraycopy(responsePacket, 1, service, 0, 1023); 
		   
		   String path1Str = ByteUtil.ByteArrayToString( path1); 
		   String path2Str = ByteUtil.ByteArrayToString( path2); 
		   String serviceName  = ByteUtil.ByteArrayToString( service);
		   
		   
		   //    if the response message is meant for this node , then update routing tables
		   String[] nodes = path2Str.split("#"); 
		   if ( ( nodes.length == 1) &&  ( GetLastNodeName(path2Str).compareTo(BluetoothUtil.adapter.getName()) == 0 ) )
		   {
			   
			        addRoute(serviceName, path1Str);    
			    
		   
		   }
		   
		   
		   //   if the response message is not meant for this node, then forward to neighbouring nodes
		   else  
   
		   {
			   String previousNodeName = GetLastNodeName(path2Str);
	    	   String newPath =   RemoveLastNodeName(path2Str);
	    	   
	    	   
	    	   
	    	   SendResponseMessageBluetooth(serviceName, path1Str, newPath, previousNodeName);      			         
			   
		   }
		      
		   
		   
	   }
	   
	   // last node operations
	   public String GetLastNodeName(String path){ 
		   String[] devices =  path.split("#");    
    	   String devNamePrefix = devices[ devices.length-1]; 
    	   String devName = devNamePrefix.split(":")[0];
		   return devName; // return the device name
	   }
	   public String RemoveLastNodeName(String path){
		   
		    // new path to be returned
		   String [] paths = path.split("#");
		   String newPath = paths[0]; 
		   for (int i=1; i < paths.length - 1; i++){
			  newPath = joinNodes(newPath,paths[i]);
		   }
		   return newPath;
	   }
	   
	   // first node operations
	   public String GetFirstNodeName(String path){
		   return path.split("#")[0].split(":")[0];		   
	   }
	   public String RemoveFirstNodeName(String path){
		   String newPath = "";
		   String[] paths = path.split("#");
		   if ( paths.length >= 2 )
		   {
			   newPath = paths[1]; 
			   
		   }
		   // there are 3 cases
		   for ( int i = 2; i < paths.length; i++ )
			   {
				   newPath= joinNodes(newPath,paths[i]); 
				   
			   }
		   return newPath;
	   }
	   
	   
	   public void HandleBluetoothDiscoveryMessage(byte[] discoveryPacket) throws UnknownHostException, UnsupportedEncodingException
	   {
		  
		   
		   
		   // See if we can service the request ? 
		       // Get the type of the resource from the discovery packet
		       byte[] service = new byte[1025]; 
		       byte[] path = new byte[8096-1024]; 
		       System.arraycopy(discoveryPacket, 1, service, 0, 1023);
		       System.arraycopy(discoveryPacket, 1024, path, 0, 8096-1024); 
		       String serviceName = ByteUtil.ByteArrayToString(service);
		       String pathStr = ByteUtil.ByteArrayToString(path);  
		       
		      
		     
		     
		       // if we can , send response packet immediately   
		       if (ConfigManager.Get("USE" + serviceName) == "yes" )
		       {
		    	   // copy the full path to another space in packet
		    	   
		    	   
		    	   // get the previous node's name
		    	   String previousNodeName = GetLastNodeName(pathStr);
		  
		    	   pathStr = joinNodes(pathStr,GetBluetoothName());      
		      	   String path2 =   RemoveLastNodeName(pathStr);
		    	   SendResponseMessageBluetooth(serviceName, pathStr, path2, previousNodeName);
		                  	   
		       
		       }
		       else
		       {
		    	
		    	   
		    	   // if we cannot, send the discovery packet to its neighbours  after  updating path   
		    	   String newPath  = joinNodes( pathStr, GetBluetoothName());    
		    	   SendDiscoverMessagesBluetooth(serviceName,newPath); 
		       }
		     
		   
		       
		   
	   }
	   
	   	  	
	
}
