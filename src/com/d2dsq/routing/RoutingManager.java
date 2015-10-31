package com.d2dsq.routing;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.*;
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
	   
	   public static RoutingManager theRouter = new RoutingManager(); 
	   public final static String BLUETOOTH_PREFIX = ":B"; 
	   
	   
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
		    
		    String path= BluetoothUtil.adapter.getName() + BLUETOOTH_PREFIX; 
		    com.d2dsq.models.Message mes = new com.d2dsq.models.Message(Message.DISCOVER_MESSAGE, "",InetAddress.getLocalHost() ,"");    
		    mes.SetDiscoverMessage(service,path) ; 
	        
		    // Get the paired devices 
		    BluetoothUtil.GetPairedDevices(); 
		    
		    for ( BluetoothDevice dev :  BluetoothUtil.pairedDevices )
		    {
		    	BluetoothUtil.SendData(mes.CreateDiscoverPacketByteArray(), dev); 
		    	
		    }
		 
		    
	   
	   }
	   
	   	  	
	
}
