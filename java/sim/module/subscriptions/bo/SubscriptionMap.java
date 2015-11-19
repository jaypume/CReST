/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012 John Cartlidge 
 * 
 *   For a full list of contributors, refer to file CONTRIBUTORS.txt 
 *
 *   CReST was developed at the University of Bristol, UK, using 
 *   financial support from the UK's Engineering and Physical 
 *   Sciences Research Council (EPSRC) grant EP/H042644/1 entitled 
 *   "Cloud Computing for Large-Scale Complex IT Systems". Refer to
 *   <http://gow.epsrc.ac.uk/NGBOViewGrant.aspx?GrantRef=EP/H042644/1>
 * 
 *   CReST is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *
 *   For further information, contact: 
 *
 *   Dr. John Cartlidge: john@john-cartlidge.co.uk
 *   Department of Computer Science,
 *   University of Bristol, The Merchant Venturers Building,
 *   Woodland Road, Bristol, BS8-1UB, United Kingdom.
 *
 */
package sim.module.subscriptions.bo;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import sim.physical.network.IP;
import utility.time.TimeManager;

/**
 * Subscription Map for a Node n
 * 
 * Contains a mapping of all subscriptions of n
 */
public class SubscriptionMap
{
	
	public static Logger logger = Logger.getLogger(SubscriptionMap.class);
	
	/**
	 * PhysicalID is the ID of the node
	 */
	private int physicalID;
	
	/**
	 * Status of this node.  True if alive, false if not alive
	 */
	private boolean myStatus;
	
	/**
	 * The set of nodes that this node subscribes to and subscription info.  
	 * Integer  = nodeID of the subscribed node 
	 * Subscription = information about the subscription 
	 */
	private HashMap<Integer, Subscription> subscriptions;
	
	/**
	 * IP is the physical location of the node, useful for referencing to the hardware
	 */
	private IP thisIP;
	
	/**
	 * Set up a new subscription map for a node
	 * 
	 * @param nodeID - the ID of the node
	 * @param nodeIP - the IP of the node
	 */
	public SubscriptionMap(int nodeID, IP nodeIP)
	{
		physicalID = nodeID;
		myStatus = true; //set node alive - should we do this as default?
		subscriptions = new HashMap<Integer, Subscription>();
		thisIP = nodeIP;
	}		

	/**
	 * Get the status of this node
	 * 
	 * @return - true if alive, false if not 
	 */
	public boolean getMyStatus() 
	{
		return myStatus;
	}
	
	/**
	 * returns the IP of the subscribing node
	 * useful for quickly referencing the hardware
	 * which this node relates to (useful in post data processing)
	 * 
	 * @return - the IP of the node
	 */
	public IP getmyIP()
	{
		return thisIP;
	}

	/**
	 * gets the physical ID of the subscribing node 
	 * useful for referencing the node in the subscription topography
	 * can also be used for referencing the hardware, though slow at it.
	 * 
	 * @return - the ID of the node
	 */
	public int getmyphysicalID()
	{
		return physicalID;
	}
	
	/**
	 * will change the status of the subscribing node 
	 * 
	 * @param alive - boolean if true then the node is alive if false is not. 
	 */
	public void changeStatus(boolean alive)
	{
		myStatus = alive;
	}
	
	/**
	 * returns all of the nodes that this node subscribes to.
	 * 
	 * @return - int[] of the physicalID of the nodes that the node subscibes to
	 */
	public int[] getSubscribedNodes()
	{
		int[] subs = new int[subscriptions.keySet().size()];
		int i = 0;
		Iterator<Integer> itr = subscriptions.keySet().iterator(); 
		
		while(itr.hasNext()) 
		{
		    subs[i] = itr.next(); 
		    i++;
		} 
		return subs;
	}
	
	/**	
	 * returns the subscriptions of the subscribing node 
	 * @return all subscriptions as HashMap  
	 */
	public HashMap<Integer, Subscription> getSubscriptions()
    {
		return subscriptions;
	}

	/**
	 * Logs information about the inconsistencies, not used
	 * @param timestamp - the time at which the logging takes place
	 */
	public void LogInconsistencies(long timestamp)
	{
		logger.debug(TimeManager.log(timestamp + "," + thisIP.toString()));
	}
	
	/**
	 * used only for debugging, prints out the node and which nodes it subscribes to
	 * 
	 * @param numberNodes - the number of the subscribing node that information is being logged from
	 * @param timestamp - the time at which the logging takes place
	 */
	public void logSubscriptions(int numberNodes, long timestamp)
	{
		logger.debug("The node "+ numberNodes + " Subscribes to: " + subscriptions.keySet());
	}


	/**
	 * prints out more detailed information about a node and it's subscriptions
	 * 
	 * @param subscribedNodes - the nodes which Node subscribes to  
	 * @param node - the main node 
	 * @param timestamp - the time at which the logging takes place
	 */
	public void logSubscriptionsDetail(int[] subscribedNodes, int node, long timestamp)
	{
		int l = subscribedNodes.length;
		for (int i = 0 ; i < l ; i++)
		{
			System.out.println("The node "+ node + " Subscribes to: " + subscribedNodes[i] +
					" status: " + subscriptions.get(subscribedNodes[i]).status + " Time Stamp" 
					+ subscriptions.get(subscribedNodes[i]).timestamp);
		}
	}
	
	
	/**
	 * sets the subscription into the hashmap of the node
	 * @param subscriptionsMap - a subscription you wish to set
	 */
	public void setSubscriptions(HashMap<Integer, Subscription> subscriptionsMap) 
	{
		this.subscriptions = subscriptionsMap;		
	}
	
	/**
	 * Add a new subscription to the map
	 * 
	 * @param subscribedNode - the node that is subscribing
	 * @param timestamp - the time the subscription is created
	 */
	public void addSubscription(int subscribedNode, long timestamp) 
	{
		subscriptions.put(subscribedNode, new Subscription(true, timestamp) );	
	}
	
	
	/**
	 * updates the given subscription ensure that the given node is correct 
	 * or you'll make a new one
	 * 
	 * @param subscribedNode - the node that is being subscribed to 
	 * @param status - the status of the subscription
	 * @param timestamp - the time the subscription is updated
	 */
	public void updateSubscription(int subscribedNode, boolean status, long timestamp)
	{
		subscriptions.put(subscribedNode,new Subscription(status, timestamp));
	}
	
}