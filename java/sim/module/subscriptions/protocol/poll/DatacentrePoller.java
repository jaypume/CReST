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
package sim.module.subscriptions.protocol.poll;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sim.physical.Datacentre;
import sim.physical.World;
import sim.physical.network.PartialIP;

public class DatacentrePoller extends AbstractStatusPoller {
	
	public static Logger logger = Logger.getLogger(DatacentrePoller.class);
	
	HashMap<Integer, Boolean> subNodesStatusMap;
	
	/**
	 * DatacentrePoller periodically polls and records the status of all nodes in the network,
	 * making this status information available for other nodes on the network to enquire.
	 *  
	 * @param location - the network location of the DatacentrePoller
	 */
	public DatacentrePoller(PartialIP location) {
		super(location);
		
		logger.info("Constructing new DCPoller, IP(" + location + ")");
		
		Datacentre dc = World.getInstance().getDatacentre(location.dc());
		int[] serverIDs = dc.getServerIDs();
		
		subNodesStatusMap = new HashMap<Integer,Boolean>();
		
		logger.debug("ServerIDs: " + Arrays.toString(serverIDs));
		
		
		int firstNodeID = serverIDs[0];
		
		int nodeIndex;
		
		//initial set up of subNodesMap
		for(int i=0; i<serverIDs.length; i++) {
			
			nodeIndex = serverIDs[i] - firstNodeID;
			
			subNodesStatusMap.put(nodeIndex, dc.getServer(nodeIndex).isAlive());
			
			PartialIP node_ip = new PartialIP(dc.getServer(nodeIndex).getIP());
			
			int hops = 2 * PartialIP.navigatePointToPoint(location, node_ip); // x2 for round trip (request/response)
			
			networkLoad += hops; 
		}
			
		logger.info("Load=" + networkLoad + "hops, Created new DCPoller with subNodeStatusMap: " + subNodesStatusMap.entrySet().toString());
		
	}

	@Override
	public void pollSubNodes() {
		
//		System.out.println("Before SubNodesPolled: Load=" + networkLoad + "hops, subNodeStatusMap: " + subNodesStatusMap.entrySet().toString());
		
		Datacentre dc = World.getInstance().getDatacentre(location.dc());
	
		//poll each sub-node for latest status
		for(Map.Entry<Integer,Boolean> entry: subNodesStatusMap.entrySet()) {
			
//			Server s = dc.getServer(entry.getKey());
//			System.out.println(s.getStatus());
			entry.setValue(dc.getServer(entry.getKey()).isAlive()); //poll current server status
			
			PartialIP node_ip = new PartialIP(dc.getServer(entry.getKey()).getIP());
			int hops = 2 * PartialIP.navigatePointToPoint(location, node_ip); // x2 for round trip (request/response)
			networkLoad += hops; 
		}
		
//		System.out.println("After SubNodesPolled: Load=" + networkLoad + "hops, subNodeStatusMap: " + subNodesStatusMap.entrySet().toString());
	}

	@Override
	public boolean requestNodeStatus(int nodeIndex) {
		
//		logger.warn("DC" + location.dc() + "NodeStatusRequest: nodeID " + nodeID);
		return subNodesStatusMap.get(nodeIndex);
	}
}
