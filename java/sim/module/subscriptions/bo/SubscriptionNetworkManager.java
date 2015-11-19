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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.subscriptions.protocol.AbstractUpdateProtocol;
import sim.module.subscriptions.protocol.CentralUpdateProtocol;
import sim.module.subscriptions.protocol.UpdateProtocolFactory;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.module.subscriptions.topology.AbstractNetworkTopology;
import sim.module.subscriptions.topology.NetworkTopologyFactory;
import sim.physical.World;
import sim.physical.network.IP;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;
import cern.jet.random.Uniform;


/**
 * SubscriptionNetworkManager: Manages the subscription network for a Datacentre
 */
public class  SubscriptionNetworkManager 
{
	public static Logger logger = Logger.getLogger(SubscriptionNetworkManager.class);
	
	/**
	 * Array of SubscriptionMaps, one for each node
	 */
	protected SubscriptionMap[] allNodes; 
	
	protected int dcID = -1; //datacentre ID
	
	protected Protocol protocol = Protocol.P2P; //default

	protected AbstractUpdateProtocol updateProtocol; //subscription update protocol
	
	protected Uniform uniformDistribution; //prng distribution

	public SubscriptionNetworkManager(int datacentreID)
	{
		//Get a new prng engine for the each SubscriptionNetworkManager
		uniformDistribution = new Uniform(RandomSingleton.getInstance().getNewEngine());
		dcID = datacentreID;

		logger.debug(TimeManager.log("New SubscriptionNetworkManager for Datacentre " + dcID));
	}
	
	/**
	 * Poll sub-nodes of Poller 
	 * 
	 * //TODO - JC, Jan 2012, this is a bit ugly.  Should be moved elsewhere, I think?
	 */
	public void pollSubNodes() {
		if(updateProtocol.getProtocolType().equals(Protocol.CENTRAL)) {
			((CentralUpdateProtocol) updateProtocol).pollSubNodes();
		} else {
			logger.warn(TimeManager.log("Attempted to poll sub nodes on incorrect protocol type: " + updateProtocol.getProtocolType()));
		}
	}

	/**
	 * Get the UpdateProtocolClass
	 * 
	 * @return updateProtocol for this datacentre
	 */
	public AbstractUpdateProtocol getUpdateProtocol() {
		return updateProtocol;
	}
	
	/**
	 * Get the latest network load counter
	 * 
	 * @return network load
	 */
	public int getNetworkLoad() {
		return updateProtocol.getNetworkLoad();
	}
	
	/**
	 * Rest network load counter to 0
	 */
	public void resetNetworkLoad() {
		updateProtocol.resetNetworkLoad();
	}
	
	/**
	 * Initialise the SubscriptionNetworkManager for this DC
	 * 
	 * @param ip - array of IPs for all servers in DC
	 * @param id - array of IDs for all servers in DC
	 * @param configParams - the subscriptions network configuration parameters
	 * @param timestamp - current time (will usually be 0, if initialisation is at startup)  
	 */
	public void initialise(IP[] ip, int[] id, SubscriptionsModuleConfigParams configParams, long timestamp)
	{
		//Initialise the subscriptions network topology 
		AbstractNetworkTopology networkTopology = NetworkTopologyFactory.getTopology(ip, id, configParams, timestamp);
		allNodes = networkTopology.subscribe(uniformDistribution);

		//Initialise the update protocol
		logger.warn("Initialising update protocol for dcID="+dcID);
		updateProtocol = UpdateProtocolFactory.getProtocol(configParams.getProtocolType(), dcID);
		
		//Do a first node update for all nodes
		allNodes = updateProtocol.updateAllNodes(allNodes);
	}
	
	/**
	 * Get subscriptions for all nodes
	 * 
	 * @return - array of SubscriptionMaps, one for each node
	 */
	public SubscriptionMap[] getSubs()
	{
		return allNodes;
	}


	/** 
	 * Logs a single node and the node which it subscribes to 
	 * adds the log of the subscriptions to the given node  	 
	 * @param node - node to be logged
	 * @param timestamp - the time at which it takes place
	 */
	public void LogSubscription(int node, long timestamp)
	{
		allNodes[node].logSubscriptions(node,timestamp);
	}


	/**
	 * Logs all nodes and the nodes that they subscribe to
	 *
	 * @param timestamp - time that logging takes place
	 */
	public void LogallSubscriptions( long timestamp)
	{
		final int numberNodes = allNodes.length;

		for(int x=0; x < numberNodes; x++) 
		{	
			allNodes[x].logSubscriptions(x, timestamp);
		}
	}


	/**
	 * Logs all nodes, the nodes which they subscribe to and their status 
	 * 
	 * @param timestamp time that the logging takes place
	 */
	public void LogAllSubscriptionsDetail( long timestamp)
	{
		final int numberNodes = allNodes.length;

		for ( int x = 0 ; x < numberNodes ; x++)
		{
			int[] SubscribedNodes = allNodes[x].getSubscribedNodes();
			allNodes[x].logSubscriptionsDetail(SubscribedNodes, x, timestamp);
		}
	}


	/**
	 * prints a detailed description of a single node and subscription
	 *
	 * @param nodeToLog - the node to be logged
	 * @param timestamp - the time that the logging takes place
	 */
	public void logSubscriptionDetail(int nodeToLog, long timestamp)
	{
		final SubscriptionMap nodes = allNodes[nodeToLog];
		int[] subscribedNodes = nodes.getSubscribedNodes();

		nodes.logSubscriptionsDetail(subscribedNodes, nodeToLog, timestamp);
	}


	/**
	 * updates the node status  
	 * call when node fails or get repaired
	 *
	 * @param nodeID - ID of node whose status is to be updated
	 * @param status - true if the node is alive, false if not
	 */
	public void updateNodeStatus(int nodeID, boolean status)
	{
		int nodeIndex = nodeID - allNodes[0].getmyphysicalID();
		allNodes[nodeIndex].changeStatus(status);
	}

	/**
	 * Update node subscriptions for node
	 * 
	 * @param node - the node that should be updated
	 */
	public void updateNodeSubscription(int node)
	{
		allNodes = updateProtocol.updateNode(node, allNodes);
	}


	/**
	 * This handles whether the node will be updated p2p or tp2p for all node updates
	 */
	public void updateNodeSubscription()
	{
		allNodes = updateProtocol.updateAllNodes(allNodes);
	}

	/**
	 * Update subscriptions all nodes using Transitive P2P Protocol
	 * Update world time by 1 microsecond between each update.  
	 */
	protected void TEST_METHOD_updateSubscriptionsUsingTP2P(int dcID)
	{
		logger.warn("This is a test method!  Not to be used in Simulation");
		
		//set protocol = TP2P
		updateProtocol = UpdateProtocolFactory.getProtocol(Protocol.TP2P, dcID);
		
		logger.info("Updating subscriptions of all nodes using Transitive P2P Protocol...");

		int lowestNodeID = allNodes[0].getmyphysicalID(); //necessary to begin from the lowest node ID in this datacentre
		long timeNow = World.getInstance().getTime();
		
		for ( int i = lowestNodeID; i < lowestNodeID + allNodes.length ; i++)
//		for ( int i = lowestNodeID + allNodes.length - 1; i >= lowestNodeID; i--)
		{
			timeNow ++;
			updateNodeSubscription(i); //update all nodes (will auto choose Protocol)
			World.getInstance().setTime(timeNow);
		}
	}
	
	/**
	 * 
	 * //TODO JC, Jan 2012 -- sort this ugly mess out.  
	 *  
	 * Measures the number of inconsistent nodes. 
	 * 
	 * A node is inconsistent if it holds an incorrect 'view' at least one of the nodes it subscribes to
	 * 
	 * @return - returns int[] of inconsistent nodes in order
	 */
	public int[] measureInconsistencies()
	{
		
//		logger.info(TimeManager.log("Measuring inconsistencies... " + Debug.getMemoryUsage()));
		
//		int numbernodes = allNodes.length;
		boolean consistent = true;
		
//		List<Integer> consistentnodes = new ArrayList<Integer>();
//		int consistentcount = 0;
		List<Integer> inconsistentNodesList = new ArrayList<Integer>();
//		int inconsistentcount = 0;
		
		//for each node
		for ( int i = 0; i < allNodes.length ; i++)
		{
			int[] SubscribedNodes = allNodes[i].getSubscribedNodes();  
			boolean statusthisnode = allNodes[i].getMyStatus(); 
			
			consistent = true;
			
			//for each node subscribed
			for (int j = 0 ; j < SubscribedNodes.length ; j++)
			{	
				//is the subscription 'inconsistent'?  Note - broken nodes *cannot* be inconsistent 
				if (statusthisnode && allNodes[i].getSubscriptions().get(SubscribedNodes[j]).status != allNodes[SubscribedNodes[j]].getMyStatus()) {
					consistent = false;
				}
			}
			
			if (!consistent)
			{
				inconsistentNodesList.add(i);
			}	
		}
				
		//(1) Create an array of inconsistent nodes, (2) sort it and (3) return it
		int[] inconsistentNodesArray = new int[inconsistentNodesList.size()];
		for (int p = 0; p < inconsistentNodesList.size(); p++)
		{
			inconsistentNodesArray[p] = inconsistentNodesList.get(p); 
		}

		Arrays.sort(inconsistentNodesArray);

//		logger.info(TimeManager.log("Measuring inconsistencies 3 " + Debug.getMemoryUsage()));
		
		return inconsistentNodesArray;
	}
	
	/**
	 * Return some internal data for debugging
	 * 
	 * @return debug data string
	 */
	public String getDebugData() {
		int size = 0;
		for(int i=0; i<allNodes.length; i++) {
			size += allNodes[i].getSubscriptions().size();
		}
		String s = "All subscriptions size = " + size;
		return s;
	}

	/**
	 * This puts out more useful data than a int[] of the inconsistent nodes 
	 * 
	 * @return the percent of inconsistent nodes
	 */
	public double percentinconsistent()
	{
		int[] inconsistentNodes = measureInconsistencies();

		measureInconsistencies();
		
		int numberInconsistent = inconsistentNodes.length;
		int numberTotal = allNodes.length;
		double percentInconsistent = ((1.0 * numberInconsistent) / numberTotal) * 100;

		return percentInconsistent;
	}

	/**
	 * Output subscription topology in Pajek .net format for further use with Pajek 
	 * network visualisation tool.  Refer to: http://vlado.fmf.uni-lj.si/pub/networks/pajek/
	 */
	public void printSubscriptionsInPajekFormat(PrintWriter out) {
		
		out.println("*Vertices " + allNodes.length);
		
		//for each node
		for ( int i = 0; i < allNodes.length ; i++)
		{
			out.println((i+1) + " \"" + i + "\""); //node number and node name
		}
		out.println("*Arcs");
		
		//for each node
		for ( int i = 0; i < allNodes.length ; i++)
		{
			int[] subscribedNodes = allNodes[i].getSubscribedNodes();  
			//for each node subscribed
			for (int j = 0 ; j < subscribedNodes.length ; j++)
			{
				//number of node and number if sybscribed node
				out.println((i+1) + " " + (subscribedNodes[j]+1));
			}
		}
		
		out.println("*Edges");
		out.println(); //trailing blank line
	}
	
	/**
	 * Output subscription topology to the screen.
	 */
	public void printSubscriptions() {
		
		System.out.println("Subscription Topology");
		
		String nodeString = "";
		//for each node
		for ( int i = 0; i < allNodes.length ; i++)
		{
			int[] SubscribedNodes = allNodes[i].getSubscribedNodes();  
 
			nodeString = "Node " + i + " { ";
			//for each node subscribed
			for (int j = 0 ; j < SubscribedNodes.length ; j++)
			{
				nodeString += SubscribedNodes[j] + ((j<SubscribedNodes.length-1)?", ":" ");
				
			}
			nodeString += "}";
			System.out.println(nodeString);
		}
	}
	
	/**
	 * Output subscription topology to the screen with status of each node.
	 * 
	 * Broken nodes are marked with a leading '*'
	 * 
	 * Inconsistent nodes are marked with a trailing '***'
	 */
	public void printSubscriptionsStatus() {
		
		String inconsistentMarker = "***";
		String brokenMarker = "*";
		
		System.out.println("Subscription Topology:");
		
		String nodeString = "";
		//for each node
		for ( int i = 0; i < allNodes.length ; i++)
		{
			int[] SubscribedNodes = allNodes[i].getSubscribedNodes();  
			boolean statusthisnode = allNodes[i].getMyStatus(); 

			nodeString = ( (statusthisnode?"":brokenMarker)) + "Node " + i + " (" + statusthisnode + ") { "; //leading star if node is broken
			
			boolean consistent = true;
			
			//for each node subscribed
			for (int j = 0 ; j < SubscribedNodes.length ; j++)
			{
				//number of subscribed node, plus 'expected' status
				nodeString += SubscribedNodes[j] + " (" + allNodes[i].getSubscriptions().get(SubscribedNodes[j]).status + ")";
				nodeString += ((j<SubscribedNodes.length-1)?", ":" ");
				
				//is the subscription 'inconsistent'?  Note - broken nodes *cannot* be inconsistent 
				if (statusthisnode && allNodes[i].getSubscriptions().get(SubscribedNodes[j]).status != allNodes[SubscribedNodes[j]].getMyStatus()) {
					consistent = false;
				}
			}
			
			nodeString += "} " + ( (consistent?"":inconsistentMarker)); //mark inconsistent nodes with trailing stars
			System.out.println(nodeString);
		}
		System.out.println("Number inconsistent = " + measureInconsistencies().length + ", Percent inconsistent = " + percentinconsistent());
		System.out.println(); //trailing blank line
	}
}
