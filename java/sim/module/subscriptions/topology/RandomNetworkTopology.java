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

import java.util.HashMap;

import org.apache.log4j.Logger;

import sim.module.subscriptions.bo.Subscription;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.physical.network.IP;

public class RandomNetworkTopology extends AbstractNetworkTopology {

	public static Logger logger = Logger.getLogger(RandomNetworkTopology.class);
	
	public RandomNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * Configure the subscriptions network randomly.
	 * 
	 * This makes every node subscribe to another (i.e., not itself) node. 
	 * The node that is picked to be subscribed to is chosen completely at random
	 * 
	 * This is the "null" network topology used to test against the other topologies 
	 */
	@Override
	protected void configureNetworkTopology() {
		
		logger.debug("Configure random network topology for subscriptions...");
		final int numNodes = allNodes.length;
		
		try {
			
			int randomIndex = uniformDistribution.nextIntFromTo(0, numNodes - 1);
		
			for (int i = 0; i < numNodes; i++) 
			{
				HashMap<Integer, Subscription> newSubscriptions = new HashMap<Integer, Subscription>();
	
				// add maxSubscriptions less duplicates 
				for (int j = 0; j < params.getMaxSubscriptions(); j++)
				{
					randomIndex = uniformDistribution.nextIntFromTo(0, numNodes - 1);
	
					if (! (newSubscriptions.containsKey(randomIndex)) || randomIndex == i)
					{
						newSubscriptions.put(randomIndex, new Subscription(true, timeNow));
					}
				}
				allNodes[i].setSubscriptions(newSubscriptions);
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error: " + e.getMessage() + ".  Exiting system...");
			logger.error(e.getStackTrace());
			//TODO - throw error and deal with at a higher level in a more sensible way
			System.exit(1);
		}
	}
}
