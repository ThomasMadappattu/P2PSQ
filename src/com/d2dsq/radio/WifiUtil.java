package com.d2dsq.radio;

import java.util.LinkedList;
import java.util.List;

import com.d2dsq.routing.Node;
import com.d2dsq.routing.RoutingManager;

import android.content.Context;
import android.util.Log;

public class WifiUtil
{
	  public  static WifiDLite wifiDLite = WifiDLite.getInstance();
      public  static List<Peer> wifiPeers; 
	  public  static  List<String> wifiPeerNames  = new LinkedList<String>();  
	  
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



}
