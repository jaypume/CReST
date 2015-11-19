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

import sim.physical.network.PartialIP;

/**
 * Abstract Poller class used for centralised/hierarchical subscription protocols.
 * 
 * AbstractStatusPoller must be instantiated by a concrete Poller subclass.
 * 
 * Poller classes maintain a record of the status of sub-nodes in the hierarchy.
 * Records are periodically updated by requesting the real status from sub-nodes directly.
 * 
 * Pollers pass on the recorded status of sub-nodes to other nodes that request
 * sub-nodes status information.
 * 
 * @author cszjpc, Jan 2012
 *
 */
public abstract class AbstractStatusPoller {

	/**
	 * The address of the poller
	 */
	protected PartialIP location;
	
	/**
	 * Load (Network Hops) used by this poller to poll subnodes
	 */
	protected int networkLoad = 0;
	
	/**
	 * Construct Poller with a location consisting of a partially defined IP address.
	 * 
	 * E.g., IP(-1,-1,-1,3) location = DC3; 
	 * E.g., IP(-1,-1,2,2) location = Aisle2, DC2;
	 * E.g., IP(-1,3,1,0) location = Rack3, Aisle1, DC0;
	 * 
	 * @param location - the location (partial IP) of the Poller
	 */
	public AbstractStatusPoller(PartialIP location) { this.location = location; };
	
	/**
	 * Return the location (partial IP) of the Poller
	 * 
	 * @return PartialIP location address
	 */
	public PartialIP getLocation() {

		return location;
	}

	/**
	 * Poll the status of all subnodes of this poller 
	 */
	public abstract void pollSubNodes();
	
	/**
	 * Request the status of a node with nodeIndex.
	 * 
	 * Node index starts at zero for each datacentre.
	 * Note: *not* the same as server ID.
	 */
	public abstract boolean requestNodeStatus(int nodeIndex);

	/**
	 * Get the network load (network hops) used by the Poller
	 * @return - network load (hops)
	 */
	public int getNetworkLoad() {
		return networkLoad;
	}
	
	/**
	 * Reset the network load (network hops) to zero
	 */
	public void resetNetworkLoad() {
		networkLoad = 0;
	}
}

