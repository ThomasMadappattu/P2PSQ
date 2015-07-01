package com.d2dsq.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RoutingManager
{

	   private HashMap routeMap; 
	   private Set<Node> neighbours = new HashSet();
	   
	   public static RoutingManager theRouter = new RoutingManager(); 
	   
	   public void addRoute(String destination, String path)
	   {
 		    routeMap.put(destination, path);   
		   
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
	   
	   
	   
	   
	  
	
	
}
