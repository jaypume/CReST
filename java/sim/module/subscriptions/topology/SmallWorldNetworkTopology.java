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

public class SmallWorldNetworkTopology extends AbstractNetworkTopology{
	
	public SmallWorldNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * Configure Subscriptions network using Watts-Strogatz Small-World network method.
	 * 
	 * This code is taken from the SPECI2, (c) Ilango Sriram
	 * 
	 * @param max_num_subscriptions - full description in initialise
	 * @param rewiringProbability - an example value is 0.1
	 * @param timestamp - should be 0 if subscription happens at start of simulation
	 */
	@Override
	protected void configureNetworkTopology() {
		
		final int NUMBER_OF_NODES = allNodes.length;
		int MAX_SUBSCRIPTIONS = params.getMaxSubscriptions();
		
		//Make sure that the degree chosen is feasible
		if(2*MAX_SUBSCRIPTIONS >= NUMBER_OF_NODES)
		{
			MAX_SUBSCRIPTIONS = (NUMBER_OF_NODES - 1)/4;
			System.out.println("#### infeasible number of subscriptions for Watts-Strogatz Model. Number changed to; " + MAX_SUBSCRIPTIONS);
		}

		/* 
		 * First Step of the algorithm is to create the ring
		 * Create #nodes nodes and for each of them #subscriptions subscriptions
		 */
		int[][] subscriptionMap = new int[NUMBER_OF_NODES][MAX_SUBSCRIPTIONS];					

		int start, subscribeTo;
		//wire neighbours. Each node (i) has an edge to all nodes stored in subscriptionMap[i]
		for (int i = 0; i < subscriptionMap.length; i++) 
		{
			start = i - (MAX_SUBSCRIPTIONS/2);
			if(start < 0)
				start = NUMBER_OF_NODES + start;

			for (int j=0; j<subscriptionMap[i].length; j++)
			{
				//subscribe to nodes to the left counting up, once you reach self shift by one towards the right	
				subscribeTo = (start+j) % NUMBER_OF_NODES;
				if (subscribeTo==i)
				{
					subscribeTo = (subscribeTo+1) % NUMBER_OF_NODES;
					start = (start+1) % NUMBER_OF_NODES;
				}
				subscriptionMap[i][j]=subscribeTo;
			}				
		}

		/* 
		 * Second Step of the algorithm is for each edge 
		 * to change it to a new one with specified probability
		 */

		//For each edge
		for (int i=0; i<subscriptionMap.length; i++)
		{
			for (int j=0; j<subscriptionMap[i].length; j++)
			{

				//If this edge is selected to be changed
				double percent = uniformDistribution.nextDouble();							
				if(percent <= params.getRewire())
				{

					//Choose a new node 
					int newEdgeTo = -1;
					boolean candidate = false;

					//Keep looping until we have a suitable candidate
					while(!candidate)
					{							
						//choose a new edge
						newEdgeTo = uniformDistribution.nextIntFromTo(0, NUMBER_OF_NODES-1);

						//reset candidate variable for this pass, and don't allow linking to self						
						candidate = !(newEdgeTo==i);

						//Don't allow edge that already exists
						for (int k=0; k<subscriptionMap[i].length; k++)
						{
							if (subscriptionMap[i][k] == newEdgeTo)
								candidate = false;
						}

						//The model is invalid if (nodes < subscriptions+2) and then this loop would not terminate
						if (subscriptionMap.length < (subscriptionMap[i].length+2))
						{
							System.out.println("#### infeasible number of subscriptions for Watts-Strogatz Model. Possibly invalid wiring; ");
							break;
						}
					}

					//Re-subscribe edge to the newly found candidate
					subscriptionMap[i][j] = newEdgeTo;						
				}
			}
		}

		// Now make the array into subscriptions:			
		for (int i=0; i<subscriptionMap.length; i++)
		{
			HashMap<Integer, Subscription> newSubscriptions = new HashMap<Integer, Subscription>();
			for (int j=0; j<subscriptionMap[i].length; j++)
			{				
				newSubscriptions.put(subscriptionMap[i][j],  new Subscription(true, timeNow));
			}
			allNodes[i].setSubscriptions(newSubscriptions);
		}

		
	}

}
