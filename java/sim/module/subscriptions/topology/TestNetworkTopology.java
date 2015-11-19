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

public class TestNetworkTopology extends AbstractNetworkTopology {

	public TestNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * Makes a pre-defined subscription topology for testing, as below:
	 *  
	 * 0 = {1,4,5}
	 * 1 = {2,3,7}
	 * 2 = {0,1,4}
	 * 3 = {2,4,7}
	 * 4 = {2,5,6}
	 * 5 = {0,4,6}
	 * 6 = {2,5,7}
	 * 7 = {0,3,4}
	 * 
	 * This is exclusively used for testing purposes 
	 */
	@Override
	protected void configureNetworkTopology() {
		
		HashMap<Integer, Subscription> newSubscription0 = new HashMap<Integer, Subscription>();
		newSubscription0.put(1, new Subscription(true, timeNow));
		newSubscription0.put(4, new Subscription(true, timeNow));
		newSubscription0.put(5, new Subscription(true, timeNow));
		allNodes[0].setSubscriptions(newSubscription0);

		HashMap<Integer, Subscription> newSubscription1 = new HashMap<Integer, Subscription>();
		newSubscription1.put(2, new Subscription(true, timeNow));
		newSubscription1.put(3, new Subscription(true, timeNow));
		newSubscription1.put(7, new Subscription(true, timeNow));
		allNodes[1].setSubscriptions(newSubscription1);

		HashMap<Integer, Subscription> newSubscription2 = new HashMap<Integer, Subscription>();
		newSubscription2.put(0, new Subscription(true, timeNow));
		newSubscription2.put(1, new Subscription(true, timeNow));
		newSubscription2.put(4, new Subscription(true, timeNow));
		allNodes[2].setSubscriptions(newSubscription2);

		HashMap<Integer, Subscription> newSubscription3 = new HashMap<Integer, Subscription>();
		newSubscription3.put(2, new Subscription(true, timeNow));
		newSubscription3.put(4, new Subscription(true, timeNow));
		newSubscription3.put(7, new Subscription(true, timeNow));
		allNodes[3].setSubscriptions(newSubscription3);

		HashMap<Integer, Subscription> newSubscription4 = new HashMap<Integer, Subscription>();
		newSubscription4.put(2, new Subscription(true, timeNow));
		newSubscription4.put(5, new Subscription(true, timeNow));
		newSubscription4.put(6, new Subscription(true, timeNow));
		allNodes[4].setSubscriptions(newSubscription4);

		HashMap<Integer, Subscription> newSubscription5 = new HashMap<Integer, Subscription>();
		newSubscription5.put(0, new Subscription(true, timeNow));
		newSubscription5.put(4, new Subscription(true, timeNow));
		newSubscription5.put(6, new Subscription(true, timeNow));
		allNodes[5].setSubscriptions(newSubscription5);

		HashMap<Integer, Subscription> newSubscription6 = new HashMap<Integer, Subscription>();
		newSubscription6.put(2, new Subscription(true, timeNow));
		newSubscription6.put(5, new Subscription(true, timeNow));
		newSubscription6.put(7, new Subscription(true, timeNow));
		allNodes[6].setSubscriptions(newSubscription6);

		HashMap<Integer, Subscription> newSubscription7 = new HashMap<Integer, Subscription>();
		newSubscription7.put(0, new Subscription(true, timeNow));
		newSubscription7.put(3, new Subscription(true, timeNow));
		newSubscription7.put(4, new Subscription(true, timeNow));
		allNodes[7].setSubscriptions(newSubscription7);
	}
}
