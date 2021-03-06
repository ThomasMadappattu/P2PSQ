package com.d2dsq.routing;

import android.bluetooth.BluetoothDevice;

import com.d2dsq.radio.*;
import com.d2dsq.ui.MainApplication;
import com.d2dsq.utils.ByteUtil;
import com.d2dsq.utils.ConfigManager;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.d2dsq.baseservices.GrabPage;
import com.d2dsq.baseservices.SmsUtil;
import com.d2dsq.models.Message;

public class RoutingManager
{

	// active routing table as a map to store active paths for a node
	private HashMap routeMap = new HashMap();
	// for its neighbours
	private Set<Node> neighbours = new HashSet();

	// passive routing table as a map to store passive paths for a node
	private HashMap<String, String> passiveRouteMap = new HashMap<String, String>();

	// a routing object to manage routing
	public static RoutingManager theRouter = new RoutingManager();

	// bluetooth prefix
	public final static String BLUETOOTH_PREFIX = ":B";

	
	
	public final static int  TYPE_BLUETOOTH = 1;
	public final static int  TYPE_WIFI = 2 ;; 
	
	public final static int  TYPE_BOTH = 3; 
	
	/*
	 *  Get the type of the node based on the name 
	 * 
	 * 
	 */
	public static int  GetNodeType(String nodeName)
	{
		String typeStr =  nodeName.split(":")[1]; 
		
		if (typeStr.compareTo(BLUETOOTH_PREFIX)   == 0 ) 
			   return TYPE_BLUETOOTH;
		else if (typeStr.compareTo(WifiUtil.WIFI_PREFIX) == 0  )
			   return TYPE_WIFI;
		return TYPE_BOTH; 
		
		
	}
	
	
	
	/*
	 * Get passive route map paths to show the user possible paths that consist
	 * of desired service.
	 */
	public String getPassiveRouteMap(String service)
	{
		return passiveRouteMap.get(service);
	}

	/*
	 * Get number of path hops.
	 */
	public int getPathHops(String path)
	{

		String[] nodes = path.split("#");
		return nodes.length;
	}

	public Set<String> getPassiveKeys()
	{
	      return passiveRouteMap.keySet(); 
	}
	
	/*
	 * Add a new path to the router with its service.
	 */
	public void addRoute(String service, String path)
	{
		if (!routeMap.containsKey(service))
		{
			// if map does not contains service
			routeMap.put(service, path);
		} else
		{
			// if it contains the service
			String existingPath = routeMap.get(path).toString();

			/*
			 * UPDATE is needed in this part since the user can select one of
			 * the possible paths to get the service.
			 */
			if (getPathHops(path) < getPathHops(existingPath))
			{
				routeMap.put(service, path);
			}
		} // else

		/*
		 * Store all of the possible service paths in a String as a value in a
		 * map (delimeter : \n) This feature will bring two conveniences; 1.
		 * Using passive routing table in any case of crash 2. The user can
		 * select one of the possible services.
		 */
		if (passiveRouteMap.containsKey(service))
		{
			// if the hashmap contains the service
			String existedPaths = passiveRouteMap.get(service);
			existedPaths += path + "\n"; // delimeter is newLine
			passiveRouteMap.put(service, existedPaths);
		} else
		{
			// if the hashmap does not contain the service
			String updatedPath = path + "\n";
			passiveRouteMap.put(service, updatedPath);
		}
	} // addRoute method

	public void addRoute(Node node, String path)
	{
		routeMap.put(node.getNodeName(), path);

	}

	public String getServicePath(String service)
	{

		if (routeMap.containsKey(service))
		{
			return routeMap.get(service).toString();
		}
		return "None";

	}

	public String GetCompletePath(String node)
	{
		StringBuilder retString = new StringBuilder();
		retString.append(node);
		retString.append("#");
		retString.append(routeMap.get(node));
		return retString.toString();

	}

	public String joinNodes(String node1, String node2)
	{
		return node1 + "#" + node2;
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
	 * Method: SendDiscoverMessagesBluetooth description : Sending Discover
	 * Packets to bluetooth devices
	 * 
	 */
	public void SendDiscoverMessagesBluetooth(String service)
			throws UnknownHostException
	{

		BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service,
				GetBluetoothName(), RoutingManager.TYPE_BLUETOOTH);
		disThread.start();

	}

	public void SendDiscoverMessagesBluetooth(String service, String path)
			throws UnknownHostException
	{

		BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service,
				path,RoutingManager.TYPE_BLUETOOTH);
		disThread.start();

	}

	public void SendResponseMessageBluetooth(String service, String path1,
			String path2, String device)
	{
		BluetoothResponseThread th = new BluetoothResponseThread(service, path1,
				path2, device,RoutingManager.TYPE_BLUETOOTH);
		th.start();

	}

	public void SendDiscoverMessagesWifi(String service, String path)
			throws UnknownHostException
	{

		BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service,
				path, RoutingManager.TYPE_WIFI);
		disThread.start();

	}

	public void SendDiscoverMessageWifi(String service, String path)
			throws UnknownHostException
	{

		BluetoothDiscoverThread disThread = new BluetoothDiscoverThread(service,
				path,RoutingManager.TYPE_WIFI);
		disThread.start();

	}

	public void SendResponseMessageWifi(String service, String path1,
			String path2, String device)
	{
		BluetoothResponseThread th = new BluetoothResponseThread(service, path1,
				path2, device,RoutingManager.TYPE_WIFI);
		th.start();

	}

	public void SendResponseMessageBluetoothInternet(String service, String path1,
			String path2, String device, byte[] data)
	{
		BluetoothResponseThread th = new BluetoothResponseThread(service, path1,
				path2, device,RoutingManager.TYPE_BLUETOOTH);
		th.SetPacketType(Message.RESPONSE_MESSAGE_DATA); 
		th.SetData(data); 
		th.start();

	}
	
	public void SendResponseMessageWifiInternet(String service, String path1,
			String path2, String device, byte[] data)
	{
		BluetoothResponseThread th = new BluetoothResponseThread(service, path1,
				path2, device,RoutingManager.TYPE_WIFI);
		th.SetPacketType(Message.RESPONSE_MESSAGE_DATA); 
		th.SetData(data); 
		th.start();

	}
	
	public void SendRequestMessageWifiSMS(String service, String path,
			String destPh, String text, String destDevice)
	{
		BluetoothRequestThread th = new BluetoothRequestThread();
		th.setDestPh(destPh);
		th.setService(service);
		th.setPath(path);
		th.setTextMsg(text);
		th.setDesDev(destDevice);
		th.setType(TYPE_WIFI);
		th.start();

	}
	public void SendRequestMessageWifiInet(String service, String path,
			 String url, String destDevice)
	{
		BluetoothRequestThread th = new BluetoothRequestThread();
		th.setDestPh(" ");
		th.setService(service);
		th.setPath(path);
		th.setTextMsg(url);
		th.setType(TYPE_WIFI);
		
		th.setDesDev(destDevice);
		th.start();

	}
	
	public void SendRequestMessageBluetoothSMS(String service, String path,
			String destPh, String text, String destDevice)
	{
		BluetoothRequestThread th = new BluetoothRequestThread();
		th.setDestPh(destPh);
		th.setService(service);
		th.setPath(path);
		th.setTextMsg(text);
		th.setDesDev(destDevice);
		th.setType(TYPE_BLUETOOTH);
		th.start();

	}
	public void SendRequestMessageBluetoothInet(String service, String path,
			 String url, String destDevice)
	{
		BluetoothRequestThread th = new BluetoothRequestThread();
		th.setDestPh(" ");
		th.setService(service);
		th.setPath(path);
		th.setTextMsg(url);
		th.setDesDev(destDevice);
		th.setType(TYPE_BLUETOOTH); 
		th.start();

	}
	public String GetBluetoothName()
	{

		return BluetoothUtil.adapter.getName() + BLUETOOTH_PREFIX;
	}

	public void HandleBluetoothRequest(byte[] requestPacket)
			throws UnsupportedEncodingException
	{
		// get path, service and phone
		byte[] service = new byte[1025];
		byte[] path = new byte[8096 - 1024];
		byte[] requestType = new byte[1];
		byte[] phone = new byte[1024];
		byte[] text = new byte[2048];
        byte[] path2 = new byte[2 * 8096 - ( 8097 + 1024 + 2048) ];
		 
		// create the request packet
		System.arraycopy(requestPacket, 1, service, 0, 1023);
		System.arraycopy(requestPacket, 1024, path, 0, 8096 - 1024);
		System.arraycopy(requestPacket, 8096, requestType, 0, 1);
		System.arraycopy(requestPacket, 8097, phone, 0, 1024);
		System.arraycopy(requestPacket, 8097 + 1024, text, 0,
				2048);
		System.arraycopy(requestPacket, 8097 + 1024 + 2048, path2, 0,
				2 * 8096 - ( 8097 + 1024 + 2048) );   
		
		// get the path and service name as string
		String strPath = ByteUtil.ByteArrayToString(path);
		String servName = ByteUtil.ByteArrayToString(service);
		String desPhone = ByteUtil.ByteArrayToString(phone);
		String mesText = ByteUtil.ByteArrayToString(text);
        String pathCopy = ByteUtil.ByteArrayToString(path2); 
		
		// if request is for this node, then, service the request
		
		String firstNode = GetFirstNodeName(strPath); 
		String updatedPath = RemoveFirstNodeName(strPath);
	
		String[] nodes = updatedPath.split("#");
		if (nodes.length == 1 && (GetLastNodeName(updatedPath)
				.compareTo(BluetoothUtil.adapter.getName()) == 0))
		{
			if (servName.contains("SMS"))
			{
			    SmsUtil.SendSms(desPhone, mesText);
			}
			else if (servName.contains("INET"))
			{
			   byte[] pageData = 	GrabPage.grabWebPage(mesText); 
			// get the previous node's name
			 String updatePath = RemoveLastNodeName(pathCopy); 
			 String destDev = GetLastNodeName(updatePath); 
			 
			 
			 if( GetNodeType(destDev) == TYPE_BLUETOOTH)
			 {
			    
				 SendResponseMessageBluetoothInternet(servName, pathCopy, updatePath, destDev, pageData); 
			 }
			 else if ( GetNodeType(destDev) == TYPE_WIFI)
			 {
				 SendResponseMessageWifiInternet(servName, pathCopy, updatePath, destDev, pageData); 
				 
			 }
			   //(serviceName, pathStr, path2,
				//		previousNodeName);
			   
			 
			   
			   
			}

		} else
		{

			// if the request is not for this node, 
			// next node update path and forward to
			String nextNode = GetFirstNodeName(strPath);

			// send request message
			if ( GetNodeType(firstNode)  == TYPE_BLUETOOTH ) 
			{
		      	SendRequestMessageBluetoothSMS(servName, updatedPath, desPhone,
					  mesText, nextNode);
			}
			else  if (  GetNodeType(firstNode)  == TYPE_WIFI)
			{
				SendRequestMessageWifiSMS(servName, updatedPath, desPhone,
						  mesText, nextNode);
				
			}
				
		}

	}
     
	public void HandleWifiRequest(byte[] requestPacket)
			throws UnsupportedEncodingException
	{
		// get path, service and phone
		byte[] service = new byte[1025];
		byte[] path = new byte[8096 - 1024];
		byte[] requestType = new byte[1];
		byte[] phone = new byte[1024];
		byte[] text = new byte[4 * 8096 - (8097 + 1024)];
		byte[] path2 = new byte[2 * 8096 - (8097 + 1024 + 2048)];

		// create the request packet
		System.arraycopy(requestPacket, 1, service, 0, 1023);
		System.arraycopy(requestPacket, 1024, path, 0, 8096 - 1024);
		System.arraycopy(requestPacket, 8096, requestType, 0, 1);
		System.arraycopy(requestPacket, 8097, phone, 0, 1024);
		
		System.arraycopy(requestPacket, 8097 + 1024, text, 0, 2048);
		System.arraycopy(requestPacket, 8097 + 1024 + 2048, path2, 0,
				2 * 8096 - (8097 + 1024 + 2048));

		// get the path and service name as string
		String strPath = ByteUtil.ByteArrayToString(path);
		String servName = ByteUtil.ByteArrayToString(service);
		String desPhone = ByteUtil.ByteArrayToString(phone);
		String mesText = ByteUtil.ByteArrayToString(text);
		String pathCopy = ByteUtil.ByteArrayToString(path2);

		// if request is for this node, then, service the request

		String firstNode = GetFirstNodeName(strPath);
		String updatedPath = RemoveFirstNodeName(strPath);

		String[] nodes = updatedPath.split("#");
		if (nodes.length == 1
				&& (GetLastNodeName(updatedPath).compareTo(WifiUtil.GetName()) == 0))
		{

			if (servName.contains("SMS"))
			{
				SmsUtil.SendSms(desPhone, mesText);
			} else if (servName.contains("INET"))
			{
				
				/*
				 * REPLACE dummy data with function call!!
				 * 
				 */
				String htmlData = "<html><h1>Testinging</h1> </html>";
				byte[] pageData = htmlData.getBytes(); 
						
						//   !GrabPage.grabWebPage(mesText);
				// get the previous node's name
				String updatePath = RemoveLastNodeName(pathCopy);
				String destDev = GetLastNodeName(updatePath);
                String destNodes[]  =  updatePath.split("#");
                int lastIndex = destNodes.length - 1; 
                String  nextNode = destNodes[lastIndex]; 
                
				
				if (GetNodeType(nextNode) == TYPE_BLUETOOTH)
				{

					SendResponseMessageBluetoothInternet(servName, pathCopy,
							updatePath, destDev, pageData);
				} else 
				{
					SendResponseMessageWifiInternet(servName, pathCopy,
							updatePath, destDev, pageData);

				}

			} 
		}
			else
			{

				// if the request is not for this node,
				// next node update path and forward to
				String nextNode = GetFirstNodeName(strPath);

				// send request message
				if (GetNodeType(firstNode) == TYPE_BLUETOOTH)
				{
					SendRequestMessageBluetoothSMS(servName, updatedPath,
							desPhone, mesText, nextNode);
				} else if (GetNodeType(firstNode) == TYPE_WIFI)
				{
					SendRequestMessageWifiSMS(servName, updatedPath, desPhone,
							mesText, nextNode);

				}

			}

		
	}
	public void HandleBluetoothResponseMessage(byte[] responsePacket)
			throws UnsupportedEncodingException
	{

		// Seperate out path1 , path2
		byte[] path1 = new byte[8096 - 1024];
		byte[] path2 = new byte[8096];
		byte[] service = new byte[1025];

		System.arraycopy(responsePacket, 1024, path1, 0, 8096 - 1024);
		System.arraycopy(responsePacket, 8096, path2, 0, 8096);
		System.arraycopy(responsePacket, 1, service, 0, 1023);

		String path1Str = ByteUtil.ByteArrayToString(path1);
		String path2Str = ByteUtil.ByteArrayToString(path2);
		String serviceName = ByteUtil.ByteArrayToString(service);

		
		String[] nodes = path2Str.split("#");
		
		/* 
		 * if the response message is meant for this node , then update routing tables
		*/
		if ((nodes.length == 1) && (GetLastNodeName(path2Str)
				.compareTo(BluetoothUtil.adapter.getName()) == 0))
		{
			
			 //At this point, there might be more than one possible
			 //service paths, user should choose one of them.
			 
			addRoute(serviceName, path1Str);

		}

		/*
		 * If the response message has not arrived at source node,
		 * update send the response message again.
		 */
		if ( (nodes.length != 1) || (GetLastNodeName(path2Str).compareTo(BluetoothUtil.adapter.getName()) != 0) )
		{
			String previousNodeName = GetLastNodeName(path2Str);
			String newPath = RemoveLastNodeName(path2Str);

			if ( GetNodeType(previousNodeName) == TYPE_BLUETOOTH)
			{
			      SendResponseMessageBluetooth(serviceName, path1Str, newPath,
					previousNodeName);
			}
			else if ( GetNodeType(previousNodeName) == TYPE_WIFI)
			{
				SendResponseMessageWifi(serviceName, path1Str, newPath,
						previousNodeName);
				
			}
			
		}
	}
    

	public void HandleWifiResponseMessage(byte[] responsePacket)
			throws UnsupportedEncodingException
	{

		// Seperate out path1 , path2
		byte[] path1 = new byte[8096 - 1024];
		byte[] path2 = new byte[8096];
		byte[] service = new byte[1025];

		System.arraycopy(responsePacket, 1024, path1, 0, 8096 - 1024);
		System.arraycopy(responsePacket, 8096, path2, 0, 8096);
		System.arraycopy(responsePacket, 1, service, 0, 1023);

		String path1Str = ByteUtil.ByteArrayToString(path1);
		String path2Str = ByteUtil.ByteArrayToString(path2);
		String serviceName = ByteUtil.ByteArrayToString(service);

		
		String[] nodes = path2Str.split("#");
		
		/* 
		 * if the response message is meant for this node , then update routing tables
		*/
		if ((nodes.length == 1) && (GetLastNodeName(path2Str)
				.compareTo(WifiUtil.GetWifiName().split(":")[0]) == 0))
		{
			
			 //At this point, there might be more than one possible
			 //service paths, user should choose one of them.
			 
			addRoute(serviceName, path1Str);

		}

		/*
		 * If the response message has not arrived at source node,
		 * update send the response message again.
		 */
		if ( (nodes.length != 1) || (GetLastNodeName(path2Str).compareTo(WifiUtil.GetWifiName().split(":")[0]) != 0) )
		{
			String previousNodeName = GetLastNodeName(path2Str);
			String newPath = RemoveLastNodeName(path2Str);

			if ( GetNodeType(previousNodeName) == TYPE_BLUETOOTH)
			{
			      SendResponseMessageBluetooth(serviceName, path1Str, newPath,
					previousNodeName);
			}
			else if ( GetNodeType(previousNodeName) == TYPE_WIFI)
			{
				SendResponseMessageWifi(serviceName, path1Str, newPath,
						previousNodeName);
				
			}
		}
		

	}
	
	// last node operations
	public String GetLastNodeName(String path)
	{
		String[] devices = path.split("#");
		String devNamePrefix = devices[devices.length - 1];
		String devName = devNamePrefix.split(":")[0];
		return devName; // return the device name
	}

	public String RemoveLastNodeName(String path)
	{

		// new path to be returned
		String[] paths = path.split("#");
		String newPath = paths[0];
		for (int i = 1; i < paths.length - 1; i++)
		{
			newPath = joinNodes(newPath, paths[i]);
		}
		return newPath;
	}

	// first node operations
	public String GetFirstNodeName(String path)
	{
		return path.split("#")[0].split(":")[0];
	}

	public String RemoveFirstNodeName(String path)
	{
		String newPath = "";
		String[] paths = path.split("#");
		if (paths.length >= 2)
		{
			newPath = paths[1];

		}
		// there are 3 cases
		for (int i = 2; i < paths.length; i++)
		{
			newPath = joinNodes(newPath, paths[i]);

		}
		return newPath;
	}
	
	
	public void HandleBluetoothResponseDataMessage(byte[] responseDataPacket ) throws UnsupportedEncodingException
	{
		
		// Seperate out path1 , path2
				byte[] path1 = new byte[8096 - 1024];
				byte[] path2 = new byte[8096];
				byte[] service = new byte[1025];
				byte[] dataLen  = new byte[4];
				byte[] data; 
				int dataLenInt; 
				System.arraycopy(responseDataPacket, 1024, path1, 0, 8096 - 1024);
				System.arraycopy(responseDataPacket, 8096, path2, 0, 8096);
				System.arraycopy(responseDataPacket, 1, service, 0, 1023);

				String path1Str = ByteUtil.ByteArrayToString(path1);
				String path2Str = ByteUtil.ByteArrayToString(path2);
				String serviceName = ByteUtil.ByteArrayToString(service);

				byte packType = responseDataPacket[ 2 * 8096 + 4 + 1];
			    System.arraycopy(responseDataPacket, 2 * 8096, dataLen, 0, 4);
				
			    BigInteger len = new BigInteger(dataLen);
			    dataLenInt = ( dataLen[0] & 0xFF)  + ( ( dataLen[1] & 0xFF) << 8) +  ( ( dataLen[2] & 0xFF) << 16) +  ( ( dataLen[1] & 0xFF) << 24)  ;  
			    
			    data = new byte[dataLenInt]; 
			    
			    System.arraycopy(responseDataPacket, 2 * 8096 + 4 + 2,data , 0, dataLenInt);
			    
				String[] nodes = path2Str.split("#");
				
				/* 
				 * if the response message is meant for this node , then update routing tables
				*/
				if ((nodes.length == 1) && (GetLastNodeName(path2Str)
						.compareTo(BluetoothUtil.adapter.getName()) == 0))
				{
					
					 //At this point, there might be more than one possible
					 //service paths, user should choose one of them.
					 
				     // Handle Internet and Camera Packets 
					MainApplication.dataQueue.add(data); 
					
				}

				/*
				 * If the response message has not arrived at source node,
				 * update send the response message again.
				 */
				if ( (nodes.length != 1) || (GetLastNodeName(path2Str).compareTo(BluetoothUtil.adapter.getName()) != 0) )
				{
					String previousNodeName = GetLastNodeName(path2Str);
					String newPath = RemoveLastNodeName(path2Str);

					if ( GetNodeType(previousNodeName) == TYPE_BLUETOOTH)
					{
						
						SendResponseMessageBluetooth(serviceName, path1Str, newPath,
								previousNodeName);
					}
					else  if ( GetNodeType(previousNodeName) == TYPE_WIFI )
					{
					    SendResponseMessageWifi(serviceName, path1Str, newPath,
							previousNodeName);
					}
				}
		
		
	}
    

	public void HandleWifiResponseDataMessage(byte[] responseDataPacket ) throws UnsupportedEncodingException
	{
		
		// Seperate out path1 , path2
				byte[] path1 = new byte[8096 - 1024];
				byte[] path2 = new byte[8096];
				byte[] service = new byte[1025];
				byte[] dataLen  = new byte[4];
				byte[] data; 
				int dataLenInt; 
				System.arraycopy(responseDataPacket, 1024, path1, 0, 8096 - 1024);
				System.arraycopy(responseDataPacket, 8096, path2, 0, 8096);
				System.arraycopy(responseDataPacket, 1, service, 0, 1023);

				String path1Str = ByteUtil.ByteArrayToString(path1);
				String path2Str = ByteUtil.ByteArrayToString(path2);
				String serviceName = ByteUtil.ByteArrayToString(service);

				byte packType = responseDataPacket[ 2 * 8096 + 4 + 1];
			    System.arraycopy(responseDataPacket, 2 * 8096, dataLen, 0, 4);

			    dataLenInt = ByteUtil.fromByteArray(dataLen);  
			    
			    data = new byte[dataLenInt]; 
			    
			    System.arraycopy(responseDataPacket, 2 * 8096 + 4 + 2,data , 0, dataLenInt);
			    
				String[] nodes = path2Str.split("#");
				
				/* 
				 * if the response message is meant for this node , then update routing tables
				*/
				if ((nodes.length == 1) && (GetLastNodeName(path2Str)
						.compareTo(WifiUtil.GetName()) == 0))
				{
					
					 //At this point, there might be more than one possible
					 //service paths, user should choose one of them.
					 
				     // Handle Internet and Camera Packets 
					MainApplication.dataQueue.add(data); 
					
				}

				/*
				 * If the response message has not arrived at source node,
				 * update send the response message again.
				 */
				if ( (nodes.length != 1) || (GetLastNodeName(path2Str).compareTo(WifiUtil.GetName()) != 0) )
				{
					String previousNodeName = GetLastNodeName(path2Str);
					String newPath = RemoveLastNodeName(path2Str);

					if ( GetNodeType(previousNodeName) == TYPE_BLUETOOTH)
					{
						
						SendResponseMessageBluetooth(serviceName, path1Str, newPath,
								previousNodeName);
					}
					else  if ( GetNodeType(previousNodeName) == TYPE_WIFI )
					{
					    SendResponseMessageWifi(serviceName, path1Str, newPath,
							previousNodeName);
					}
				}
			
		
		
	}

	
	
	public void HandleBluetoothDiscoveryMessage(byte[] discoveryPacket)
			throws UnknownHostException, UnsupportedEncodingException
	{

		// See if we can service the request ?
		// Get the type of the resource from the discovery packet
		byte[] service = new byte[1025];
		byte[] path = new byte[8096 - 1024];
		System.arraycopy(discoveryPacket, 1, service, 0, 1023);
		System.arraycopy(discoveryPacket, 1024, path, 0, 8096 - 1024);
		String serviceName = ByteUtil.ByteArrayToString(service);
		String pathStr = ByteUtil.ByteArrayToString(path);

		/*
		 * STOPPING CRITERIA for ROUTING To avoid looping and using TTL, if the
		 * path contains the same node more than one time stop this process.
		 */
		if (pathStr.contains(GetBluetoothName()))
		{
			return; // get out
		}

		/*
		 * If that is ok, send the response packet.
		 */
		if (ConfigManager.Get("USE" + serviceName) == "yes")
		{

			// get the previous node's name
			String previousNodeName = GetLastNodeName(pathStr);
			pathStr = joinNodes(pathStr, GetBluetoothName());

			// copy the full path to another space in packet
			String path2 = RemoveLastNodeName(pathStr);
			SendResponseMessageBluetooth(serviceName, pathStr, path2,
					previousNodeName);

			/*
			 * To return all of the possible services to source node, we are
			 * sending discover packets to neighbours if there is more
			 * neighbour.
			 */
			String newPath = joinNodes(pathStr, GetBluetoothName());
			SendDiscoverMessagesBluetooth(serviceName, newPath);
			 if ( ConfigManager.configValues.containsKey("wifinode"))
			    {
			    	
			    	String wifiPath = joinNodes(pathStr,WifiUtil.GetWifiName());
				    SendDiscoverMessagesWifi(serviceName, wifiPath);
			    	
			    }

		} else
		{

			// if we cannot, send the discovery packet to its neighbours after
			// updating path
			String newPath = joinNodes(pathStr, GetBluetoothName());
			SendDiscoverMessagesBluetooth(serviceName, newPath);
			 if ( ConfigManager.configValues.containsKey("wifinode"))
			    {
					String wifiPath = joinNodes(pathStr,WifiUtil.GetWifiName());
				    SendDiscoverMessagesWifi(serviceName, wifiPath);
			    }
		}

	}
	public void HandleWifiDiscoveryMessage(byte[] discoveryPacket)
			throws UnknownHostException, UnsupportedEncodingException
	{

		// See if we can service the request ?
		// Get the type of the resource from the discovery packet
		byte[] service = new byte[1025];
		byte[] path = new byte[8096 - 1024];
		System.arraycopy(discoveryPacket, 1, service, 0, 1023);
		System.arraycopy(discoveryPacket, 1024, path, 0, 8096 - 1024);
		String serviceName = ByteUtil.ByteArrayToString(service);
		String pathStr = ByteUtil.ByteArrayToString(path);

		/*
		 * STOPPING CRITERIA for ROUTING To avoid looping and using TTL, if the
		 * path contains the same node more than one time stop this process.
		 */
		if (pathStr.contains(WifiUtil.GetWifiName()))
		{
			return; // get out
		}

		/*
		 * If that is ok, send the response packet.
		 */
		if (ConfigManager.Get("USE" + serviceName) == "yes")
		{

			// get the previous node's name
			String previousNodeName = GetLastNodeName(pathStr);
			pathStr = joinNodes(pathStr, WifiUtil.GetWifiName());

			// copy the full path to another space in packet
			String path2 = RemoveLastNodeName(pathStr);
			SendResponseMessageWifi(serviceName, pathStr, path2,
					previousNodeName);

			/*
			 * To return all of the possible services to source node, we are
			 * sending discover packets to neighbours if there is more
			 * neighbour.
			 */
			String newPath = joinNodes(pathStr, WifiUtil.GetWifiName());
			SendDiscoverMessagesWifi(serviceName, newPath);

		    if ( ConfigManager.configValues.containsKey("btnode"))
		    {
		    	String Bt = joinNodes(pathStr, GetBluetoothName());
		    	SendDiscoverMessagesBluetooth(serviceName, Bt);
		    	
		    }
		
		} else
		{

			// if we cannot, send the discovery packet to its neighbours after
			// updating path
			String newPath = joinNodes(pathStr, WifiUtil.GetWifiName());
			SendDiscoverMessagesWifi(serviceName, newPath);
			 if ( ConfigManager.configValues.containsKey("btnode"))
			    {
			    	
				 String Bt = joinNodes(pathStr, GetBluetoothName());
			    	SendDiscoverMessagesBluetooth(serviceName, Bt);
			    	
			    }
		 
		}

	}
}
