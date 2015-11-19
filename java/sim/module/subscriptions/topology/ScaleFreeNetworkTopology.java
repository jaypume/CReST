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

import java.util.ArrayList;
import java.util.Collections;

import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.physical.network.IP;

public class ScaleFreeNetworkTopology extends AbstractNetworkTopology {

	public ScaleFreeNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * Subscribe using Barabasi-Albert Scale-Free network method.
	 * 
	 * This code is taken from the SPECI2, (c) Ilango Sriram
	 * 
	 * Barabasi-Albert growing DIRECTED scale free networks as
	 * described in http://arxiv.org/pdf/cond-mat/0408391</a>
	 * The number of the
	 * initial unconnected set of nodes is #NUMBER_SUBSCRIPTIONS. 
	 * The next added node is connected to all of these initial nodes,
	 * and after that the BA model is used normally.
	 *
	 * @param max_num_subscriptions - full description in initilise
	 * @param timestamp - should be 0 if subscription happens at start of simulation 
	 */
	@Override
	protected void configureNetworkTopology() {

		final int NUMBER_OF_NODES = allNodes.length;
		//create a random order of the indices using shuffle:
		ArrayList<Integer> unsubscribedNodes = new ArrayList<Integer>(NUMBER_OF_NODES);
		for (int i = 0; i < NUMBER_OF_NODES; i++) 
		{
			unsubscribedNodes.add((Integer)i);				
		}
		Collections.shuffle(unsubscribedNodes);


		// edge i goes from edge[2*i] to ends[2*i+1], 
		//so we need twice as many fields as edges
		//this will be slightly less than this due to initial unconnected nodes:
		int[] edges = new int[(2*params.getMaxSubscriptions()*NUMBER_OF_NODES)];
		//init the array edges with -1;
		for (int i = 0; i < edges.length; i++) 
		{
			edges[i] = -1;
		}
		int edgesFound = 0;

		/* 
		 * Part 1, Start:
		 * #NUMBER_SUBSCRIPTIONS nodes are unconnected, and 
		 * one nodes is connected to and from all of the initial set
		 * 
		 */
		Integer firstToConnect = unsubscribedNodes.remove(0);

		// Add initial edges to and from first connected node:
		// Link all to and from the last one: 
		for(int i=0; i < 2*params.getMaxSubscriptions(); i=i+2)
		{
			Integer current  = unsubscribedNodes.remove(0);
			// from first to current
			edges[2*i]= (int)firstToConnect; 
			edges[2*i+1]= (int)current;
			edgesFound++;

			// from current to first
			edges[2*i+2]= (int)current; 
			edges[2*i+3]= (int)firstToConnect;
			edgesFound++;
		}


		/* 
		 * Part 2, 
		 * take remaining nodes and wire them up with with preferential attachment
		 * with small fraction reverse link
		 */			

		// over the remaining nodes
		int nodesToDo =unsubscribedNodes.size();
		for(int rest=0; rest < nodesToDo; rest++)
		{	
			int current  = (int)unsubscribedNodes.remove(0);
			// over the new edges for this new node			
			for (int j=0; j < params.getMaxSubscriptions(); j++)
			{    		
				int targetCandidate = -1;
				boolean validCandidate = false;
				while(!validCandidate)
				{
					targetCandidate = edges[uniformDistribution.nextIntFromTo(0, (2*edgesFound)-1)]; 						
					//Check if this Candidate is valid
					//it has to be different than self
					if (targetCandidate == current)
						continue;

					//and targetCandidate can't be used for current already
					//current is wired in the array from (2*edgesFound) to (2*edgesFound+2*j+1)
					validCandidate = true;
					for (int k = (2*edgesFound); k < (2*edgesFound+2*j+1); k++)
					{
						if (edges[k]==targetCandidate)
							validCandidate = false;
					}
				}

				//Now set the edge:								
				//with low probability invert the edge
				if(uniformDistribution.nextDouble() <= SubscriptionsModuleConfigParams.DEFAULT_INVERT_EDGE_PROBABILITY)
				{
					edges[2*edgesFound+2*j]= targetCandidate;
					edges[2*edgesFound+2*j+1]= current ;

				}
				else{
					edges[2*edgesFound+2*j]=current;
					edges[2*edgesFound+2*j+1]=targetCandidate;
				}
			}
			//This Loop has found #NUMBER_SUBSCRIPTIONS new edges
			edgesFound += params.getMaxSubscriptions();
		}

		/* 
		 * Part 3: 
		 * Now make the array into subscriptions
		 */
		for (int i=0; i<edges.length; i=i+2)
		{
			//if the value is negative then this is from init of array and it is not a valid edge
			if ((edges[i]<0)||(edges[i+1]<0))
				continue;
			allNodes[(edges[i])].addSubscription( edges[i+1], timeNow );

		}
	}

}
