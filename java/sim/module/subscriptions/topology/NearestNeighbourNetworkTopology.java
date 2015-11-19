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

import sim.module.subscriptions.bo.Subscription;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.physical.network.IP;

public class NearestNeighbourNetworkTopology extends AbstractNetworkTopology {

	public NearestNeighbourNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * nodes are arranged in 1D circular array and each attached to K nearest neighbours to the left and right. 
	 *  
	 *  Taken from Ilango Sriram's SPECI2:
	 *  www.speci.org
	 */
	@Override
	protected void configureNetworkTopology() {

		final int numNodes = allNodes.length;
		final int MAX_SUBSCRIPTIONS = params.getMaxSubscriptions();
		
		//subscribe to i=MAX_NUMBER_SUBSCRIPTIONS/2 to the left and MAX_NUMBER_SUBSCRIPTIONS-i+1 to the right
		int leftNeighbors = MAX_SUBSCRIPTIONS/2;
		int subscribeTo=0;
		
		for (int i = 0; i < numNodes; i++) {
			HashMap<Integer, Subscription> newSubscriptions = new HashMap<Integer, Subscription>();
			
			// add maxSubscriptions less duplicates 
			for (int j = i-leftNeighbors; j < i+MAX_SUBSCRIPTIONS-leftNeighbors+1; j++){
				//do not subscribe to itself
				if(j!=i) {
					
					subscribeTo = j;
					//loop array start-end
					if (subscribeTo<0) subscribeTo+=numNodes;
					if (subscribeTo>=numNodes) subscribeTo-=numNodes;

					newSubscriptions.put(subscribeTo, new Subscription(true, timeNow));

				} else {
					//do nothing - we do not want node to subscribe to itself
				}
			}
			allNodes[i].setSubscriptions(newSubscriptions);
		}
	}
}
