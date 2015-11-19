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

import sim.module.subscriptions.bo.SubscriptionMap;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;

public abstract class AbstractUpdateProtocol {

	protected int networkLoad; //network load counter (network 'hops')
	
	protected Protocol protocolType;
	
	public AbstractUpdateProtocol(Protocol protocolType) {
		networkLoad = 0;
		this.protocolType = protocolType;
	}
	
	/**
	 * Update subscriptions for a particular node
	 * 
	 * @param nodeID - node ID of node to update
	 * @param allNodes - array of all node subscriptions
	 * 
	 * @return updated node subscriptions array
	 */
	public abstract SubscriptionMap[] updateNode (int nodeID, SubscriptionMap[] allNodes);
	
	/**
	 * Update subscriptions of all nodes
	 * 
	 * @param allNodes - array of all node subscriptions
	 * 
	 * @return updated node subscriptions array
	 */
	public abstract SubscriptionMap[] updateAllNodes(SubscriptionMap[] allNodes);
	
	/**
	 * Return the latest network load counter (number of network 'hops')
	 * 
	 * @return latest load
	 */
	public int getNetworkLoad() {
		return networkLoad;
	}
	
	/**
	 * Reset the network load counter (number of network 'hops')
	 * 
	 * Set load counter to zero.
	 */
	public abstract void resetNetworkLoad();
	
	/**
	 * The Protocol Update Type for this Update Protocol
	 * 
	 * @return Protocol
	 */
	public Protocol getProtocolType() {
		return protocolType;
	}
}
