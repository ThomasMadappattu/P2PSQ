package com.d2dsq.radio;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class WifiUtil
{
	  public  static WifiDLite wifiDLite = WifiDLite.getInstance();
      public  static List<Peer> wifiPeers; 
	  public static  List<String> wifiPeerNames; 
	  
      public static void  init(Context context)
      {
    	  wifiDLite.initialize(context, new DefaultConfiguration()); 
    	  
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
    		        	
    		        }
    		        
    		    }
    		});
    	  
      }
      
      public static List<String> GetPeerNames()
      {
    	  GetPeers(); 
    	  wifiPeerNames.clear(); 
    	  for (Peer p : wifiPeers)
    	  {
    		  wifiPeerNames.add( p.getWifiP2pDevice().deviceName); 
    	     
    	  }
    	  return wifiPeerNames; 
    	  
      }



}
