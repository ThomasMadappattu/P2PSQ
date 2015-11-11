package com.d2dsq.radio;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.d2dsq.routing.Node;
import com.d2dsq.routing.RoutingManager;
import com.d2dsq.utils.IPUtils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WifiUtil
{
	  public  static WifiDLite wifiDLite = WifiDLite.getInstance();
      public  static List<Peer> wifiPeers; 
	  public  static  List<String> wifiPeerNames  = new LinkedList<String>();  
	  public  static ConcurrentLinkedQueue<byte[]> packets = new ConcurrentLinkedQueue<byte[]>(); 
	  public  static int SERVER_PORT = 7000; 
	  
	  public static boolean  isGroupOwner = false; 
	  public static InetAddress groupOwnerAdderess; 
	  
	  public  static Set<String> neighbours = new HashSet<String>(); 
	  
	  
	  public static  Handler m_WifiHandler = new Handler()
	  {
		  
		     public void handleMessage(Message mes)
		     {
		    	 
		    	 
		     }
		  
		  
		  
	  };
	  
	  
      public static void  init(Context context)
      {
    	  wifiDLite.initialize(context, new DefaultConfiguration()); 
    	  wifiPeers = new LinkedList<Peer>(); 
    	  wifiPeerNames = new LinkedList<String>(); 
      }
      
      public static void GetPeers()
      {
    	  
    	  wifiPeers.clear(); 
    	  wifiDLite.acquireCurrentPeerList(new PeerListAcquisitionListener() {
    		    @Override
    		    public void onPeerListAcquisitionSuccess(List<Peer> peers) {
    		        // note that each peer device is represented by a special Peer object
    		        Log.v("WifiUtil", "Acquired a list of P2P Peers");
    		        for ( Peer p : peers)
    		        {
    		        	wifiPeers.add(p); 
    		        	Log.v("WifiUtil - Name",p.getWifiP2pDevice().deviceName );
    		                     	
    		        	Node node = new Node(); 
    			    	node.setWifiNode(true);
    			    	node.setBluetoothNode(false);
    			    	node.setWifiDevice(p);
    			    	node.setNodeName(p.getWifiP2pDevice().deviceName); 
    			    	node.setMacAddress(p.getWifiP2pDevice().deviceAddress); 
    			    	RoutingManager.theRouter.addNeighbour(node); 
    		        	
    		        }
    		        
    		    }
    		});
    	  
    	  
      }
      
      public static List<String> GetPeerNames()
      {
    	  GetPeers(); 
    	  
    	  for (Peer p : wifiPeers)
    	  {
    		  wifiPeerNames.add( p.getWifiP2pDevice().deviceName); 
    	     
    	  }
    	  return wifiPeerNames; 
    	  
      }
      
      public static boolean isWifiEnabled()
      {
    	  
    	  return wifiDLite.isWifiP2pEnabled(); 
    	  
      }
      
      public static void StartWifiServer()
      {
    	  Thread th = new Thread( new WifiServerThread(SERVER_PORT, packets)); 
    	  th.start(); 
    	  
      }
      
      public static void SendData(byte[] data, String ip )
  	  {
    	  
    	 WifiSenderThread th = new WifiSenderThread(ip,data); 
    	 th.start(); 
    	 
  	  }
  		
      public String GetWifiName()
      {
    	  return IPUtils.getLocalIPAddress(); 
    	  
      }


}
