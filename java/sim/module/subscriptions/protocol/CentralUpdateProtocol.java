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
import sim.module.subscriptions.protocol.poll.DatacentrePoller;
import sim.physical.World;
import sim.physical.network.PartialIP;

public class CentralUpdateProtocol extends AbstractUpdateProtocol {

	public static Logger logger = Logger.getLogger(CentralUpdateProtocol.class);
	
	protected int dcID = -1; //ID of thie datacentre
	
	protected DatacentrePoller statusPoller; //status poller for central controller
	
	public CentralUpdateProtocol(int datacentreID) {
		super(Protocol.CENTRAL);
		dcID = datacentreID;
		statusPoller = new DatacentrePoller(new PartialIP(PartialIP.UNKNOWN,PartialIP.UNKNOWN,PartialIP.UNKNOWN,dcID));
	}
	
	/**
	 * Update node subscriptions using Central-Datacentre Protocol
	 * 
	 * Nodes request status information of other nodes from a centralised
	 * DC poller that polls status of all nodes in datacentre.
	 * 
	 * @param nodeID - the node to update
	 * @param allNodes - array of all node subscriptions
	 * @return updated node subscriptions array
	 * 
	 */
	@Override
	public SubscriptionMap[] updateNode(int nodeID,
			SubscriptionMap[] allNodes) {
		
		
		//must subtract first node number to get node index 
		//(since nodeNumbers are unique across the world and nodeIndex starts from 0 for each datacentre)
		int nodeIndex = nodeID - allNodes[0].getmyphysicalID();
				
		logger.debug("Updating node " + nodeIndex + " using centralised DC protocol...");
		
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
				//update subscription with *latest* status of subscribed node held by DCPoller
				entry.getValue().update(statusPoller.requestNodeStatus(entry.getKey()), timeNow);
				
				//increment the network routing load for update request and reply
				PartialIP origin = new PartialIP(allNodes[nodeIndex].getmyIP());
				PartialIP destination = statusPoller.getLocation();
				networkLoad += 2 * PartialIP.navigatePointToPoint(origin, destination); //round-trip is 2*one-way
			}
		}
		
		return allNodes;
	}

	/**
	 * Update the subscriptions of all nodes using CentrelDC protocol
	 * 
	 * @param allNodes - array of all node subscriptions
	 * @return updated node subscriptions array
	 */
	@Override
	public SubscriptionMap[] updateAllNodes(SubscriptionMap[] allNodes) {
		
		logger.info("Updating subscriptions of all nodes using Central-DC Protocol...");
		
		int lowestNodeID = allNodes[0].getmyphysicalID(); //necessary to begin from the lowest node ID in this datacentre
		
		for ( int i = lowestNodeID; i < lowestNodeID + allNodes.length ; i++) {
			updateNode(i, allNodes);
		}	

		return allNodes;
	}

	@Override
	public int getNetworkLoad() {
		//with hierarchical, need to include the poller's calls
		return networkLoad + statusPoller.getNetworkLoad();
	}

	@Override
	public void resetNetworkLoad() {
		//with hierarchical, need to include the poller's calls
		networkLoad = 0;
		statusPoller.resetNetworkLoad();
	}
	
	/**
	 * Poll Sub-Nodes of Poller
	 */
	public void pollSubNodes() {
		statusPoller.pollSubNodes();
	}

}
