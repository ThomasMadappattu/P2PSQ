package com.d2dsq.routing;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.*;
import com.d2dsq.utils.ConfigManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.d2dsq.models.Message;

public class RoutingManager
{

	   private HashMap routeMap; 
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
 		    
		   if ( routeMap.get(service) == null )
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
	  
	   public String GetBluetoothName()
	   {
		   
		  return  BluetoothUtil.adapter.getName() + BLUETOOTH_PREFIX; 
	   }
	   
	   
	   public void HandleBluetoothResponseMessage( byte[] responsePacket)
	   {
		   
		   
		   //    if the response message is meant for this node , then update routing tables
		   
		   
		   
		   //   if the response message is not message is not m
		   
		      
		   
		   
	   }
	   
	   
	   public String GetLastNodeName(String path){ 
		   String[] devices =  path.split("#");    
    	   String devNamePrefix = devices[ devices.length-1]; 
    	   String devName = devNamePrefix.split(":")[0];
		   return devName; // return the device name
	   }
	   
	   
	   public String RemoveLastNodeName(String path){
		   
		   String newPath = new String(); // new path to be returned
		   String [] paths = path.split("#");
		   for (int i=0; i < paths.length-1; i ++){
			   if ( i != paths.length-2 )
				   newPath += paths[i] + "#";
		   }
		   return newPath;
	   }
	   
	   
	   public void HandleBluetoothDiscoveryMessage(byte[] discoveryPacket) throws UnknownHostException
	   {
		  
		   
		   
		   // See if we can service the request ? 
		       // Get the type of the resource from the discovery packet
		       byte[] service = new byte[1025]; 
		       byte[] path = new byte[8096-1024]; 
		       System.arraycopy(discoveryPacket, 1, service, 0, 1023);
		       System.arraycopy(discoveryPacket, 1024, path, 0, 8096-1024); 
		       String serviceName = service.toString();
		       String pathStr = path.toString();  
		       
		     
		       
		       // if we can , send response packet immediately   
		       if (ConfigManager.Get("USE" + serviceName) == "yes" )
		       {
		    	   // copy the full path to another space in packet
		    	   
		    	   
		    	   // get the previous node's name
		    	   String previousNodeName = GetLastNodeName(pathStr);
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
