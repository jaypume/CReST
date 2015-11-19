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
package sim.module.subscriptions.protocol;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.module.subscriptions.bo.Subscription;
import sim.module.subscriptions.bo.SubscriptionMap;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.physical.World;
import sim.physical.network.IP;

public class P2PUpdateProtocol extends AbstractUpdateProtocol {

	public static Logger logger = Logger.getLogger(P2PUpdateProtocol.class);
	
	public P2PUpdateProtocol() {
		super(Protocol.P2P);
	}
	
	/** 
	 * Update subscriptions of a particular node using Simple P2P Protocol
	 * 
	 * SideEffect: If this node is broken, the status of each subscription will be set to false.
	 * 
	 * @param nodeID - the node to be updated 
	 * @param allNodes - the SubscriptionMap array of all nodes
	 * 
	 * @return an updated SubscriptionMap array
	 */
	@Override
	public SubscriptionMap[] updateNode(int nodeID,
			SubscriptionMap[] allNodes) {
		
		//must subtract first node number to get node index 
		//(since nodeNumbers are unique across the world and nodeIndex starts from 0 for each datacentre)
		int nodeIndex = nodeID - allNodes[0].getmyphysicalID();
				
		logger.debug("Updating node " + nodeIndex + " using simple p2p protocol...");

		HashMap<Integer, Subscription> subscriptionMap = allNodes[nodeIndex].getSubscriptions(); //subscription map for this node
		boolean statusOfThisNode = allNodes[nodeIndex].getMyStatus(); //status of this node
		long timeNow = World.getInstance().getTime(); //the time now
		
		//Iterate over each subscription entry in the subscriptions map of this node
		for(Map.Entry<Integer, Subscription> entry: subscriptionMap.entrySet() ) {
			
			if(statusOfThisNode == false) {
				//if this node is broken, then set status of subscription to false as default
				entry.getValue().update(false, timeNow);
			} else {
				//if this node is not broken, then 
				//update subscription with *real* status of subscribed node and new timestamp
				entry.getValue().update(allNodes[entry.getKey()].getMyStatus(), timeNow);
				
				//increment the network routing load for update request and reply
				IP origin = allNodes[nodeIndex].getmyIP();
				IP destination = allNodes[entry.getKey()].getmyIP();
				networkLoad += 2 * origin.navigateToIP(destination); //round-trip is 2*one-way
//				networkLoad += destination.navigateToIP(origin);
			}
		}

		return allNodes;
	}

	/**
	 * Updates subscriptions of all nodes using Simple P2P Protocol
	 */
	@Override
	public SubscriptionMap[] updateAllNodes(SubscriptionMap[] allNodes) {
		
		logger.info("Updating subscriptions of all nodes using Simple P2P Protocol...");
		
		int lowestNodeID = allNodes[0].getmyphysicalID(); //necessary to begin from the lowest node ID in this datacentre
		
		for ( int i = lowestNodeID; i < lowestNodeID + allNodes.length ; i++)
		{
			updateNode(i, allNodes);
		}

		return allNodes;
	}

	@Override
	public void resetNetworkLoad() {
		networkLoad = 0;
	}

}
