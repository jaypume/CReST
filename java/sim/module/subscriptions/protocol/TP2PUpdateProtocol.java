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
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.module.subscriptions.bo.Subscription;
import sim.module.subscriptions.bo.SubscriptionMap;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.physical.World;
import sim.physical.network.IP;

public class TP2PUpdateProtocol extends AbstractUpdateProtocol {

	public static Logger logger = Logger.getLogger(TP2PUpdateProtocol.class);
	
	public TP2PUpdateProtocol() {
		super(Protocol.TP2P);
	}
	
	/** 
	 * Update subscriptions of a particular node using Transitive P2P Protocol
	 * 
	 * SideEffect: If this node is broken, the status of each subscription will be set to false.
	 * 
	 * @param nodeID - the number of the node to be updated 
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
				
//		System.out.println("Updating node " + nodeIndex + " using transitive p2p protocol...");
		
		boolean statusOfThisNode = allNodes[nodeIndex].getMyStatus(); //status of this node
		long timeNow = World.getInstance().getTime(); //the time now
		
		HashMap<Integer, Subscription> subscriptions = allNodes[nodeIndex].getSubscriptions(); //subscription map for this node
		
		//if this node is broken, set all subscriptions false and return
		if(statusOfThisNode == false) {
			for(Map.Entry<Integer, Subscription> entry: subscriptions.entrySet() ) {
				entry.getValue().update(false, timeNow);
			}
			return allNodes;
		}

		//else, update subscriptions using transitive P2P
		LinkedList<Integer> peerIDsList = new LinkedList<Integer>(subscriptions.keySet()); //id list of peers (subscribed nodes)
		HashMap<Integer, Subscription> peerSubscriptions; //map to hold the subscriptions map of the peer (subscribed node)
		
		int peerID = -1; //ID of the peer
		boolean statusOfPeer;  //Status of the peer
		
		
		IP origin = allNodes[nodeIndex].getmyIP();
		IP destination;
		
		//while there are still subscriptions to be updated
		while (peerIDsList.size() > 0) {
			
			peerID = peerIDsList.pop(); //get the peer node ID (subscribed node)
			statusOfPeer = allNodes[peerID].getMyStatus(); //get the status of the peer
			peerSubscriptions = allNodes[peerID].getSubscriptions(); //get all subscriptions from the peer
			
			//increment the network routing load for update request and reply
			destination = allNodes[peerID].getmyIP();
			networkLoad += 2 * origin.navigateToIP(destination); //round-trip is 2*one-way

//			networkLoad += destination.navigateToIP(origin);
			
			//update information this node has on the peer
			subscriptions.get(peerID).update(statusOfPeer, timeNow);

			//if the peer is not broken
			if(statusOfPeer) {
				
//				System.out.println("peer ID = " + peerID);
				
				//compare subscriptions of the peer with subscriptions of this node
				//if peer has more recent information about any other peers of this node
				//then copy that information and do not directly request info from peer
				//
				//for each subscription entry the peer has
				//
				for(Map.Entry<Integer, Subscription> entry: peerSubscriptions.entrySet()) {
									
					//check to see if the peer has any other subscriptions in common with this node
					if(peerIDsList.contains( entry.getKey())) {
					
						int mutualPeerID = entry.getKey(); //the node is a mutual peer

//						System.out.println("Mutual Peer ID = " + mutualPeerID);
						
						//only copy subscription information from the peer if the peer has more recent information
						if( entry.getValue().timestamp > subscriptions.get(mutualPeerID).timestamp ) {
							
							//update Subscription of mutual peer with status and timestamp from peer
							subscriptions.get(mutualPeerID).update(entry.getValue().status, entry.getValue().getTimeStamp());
							
							//now we have information about mutual peer, no longer request information from mutual peer directly
							peerIDsList.remove(entry.getKey());  
							
//							System.out.println("Mutual Peer ID data ***copied***");
							
						} else {
							//do nothing - the information about mutual peer is no newer.
//							System.out.println("Mutual Peer ID data not copied");
						}
					}
				}
			} else {
				//peer is broken, so do nothing: ignore all the information it has
//				System.out.println("peerID=" + peerID + " is broken, so ignoring all information it has...");
			}
		}
		
		return allNodes;
	}

	/**
	 * Update subscriptions all nodes using Transitive P2P Protocol
	 */
	@Override
	public SubscriptionMap[] updateAllNodes(SubscriptionMap[] allNodes) {
		
		logger.info("Updating subscriptions of all nodes using Transitive P2P Protocol...");

		int lowestNodeID = allNodes[0].getmyphysicalID(); //necessary to begin from the lowest node ID in this datacentre
		
		for ( int i = lowestNodeID; i < lowestNodeID + allNodes.length ; i++)
		{
			updateNode(i, allNodes); //pass the node number
		}
		
		return allNodes;
	}

	@Override
	public void resetNetworkLoad() {
		// TODO Auto-generated method stub
		networkLoad = 0;
	}

}
