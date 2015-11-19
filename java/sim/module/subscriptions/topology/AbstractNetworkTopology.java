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
package sim.module.subscriptions.topology;

import sim.module.subscriptions.bo.SubscriptionMap;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.physical.network.IP;
import cern.jet.random.Uniform;

public abstract class AbstractNetworkTopology {

	protected IP[] ips;  //Array of IP addresses of all nodes
	protected int[] ids; //Array of IDs of all nodes
	protected SubscriptionsModuleConfigParams params; //Configuration parameters for Subscriptions
	protected SubscriptionMap[] allNodes; //Array of subscription manager nodes
//	protected int maxSubscriptions;	//Max subscriptions of any one node
	protected long timeNow; //Current timestamp
	protected Uniform uniformDistribution; //Random Uniform distibution
	
	public AbstractNetworkTopology(IP[] ips_array, int[] ids_array, SubscriptionsModuleConfigParams configParams, long timestamp) {
		ips = ips_array;
		ids = ids_array;
		params = configParams;
		timeNow = timestamp;
	}
	
	/** 
	 * Create array of subscriptions using network topology model
	 * 
	 * @return subscriptions array
	 */
	public SubscriptionMap[] subscribe(Uniform uniformDistribution) {

		this.uniformDistribution = uniformDistribution;
		
		//initialise the nodes array of subscriptions
		allNodes = new SubscriptionMap[ips.length];
		for (int i = 0; i < ips.length; i++) 
		{
			allNodes[i] = new SubscriptionMap(ids[i], ips[i]);
		}
		
		//configure network topology of subscriptions
		configureNetworkTopology();
		
		//return final subscriptions array
		return allNodes;
	}
	
	/**
	 * Configure the topology of the subscription network
	 */
	protected abstract void configureNetworkTopology();
	
}
