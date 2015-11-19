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

public class KlemmEguiluzNetworkTopology extends AbstractNetworkTopology {
	
	public KlemmEguiluzNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
	}

	/**
	 * Subscribe using Klemm-Eguiluz Small-World networks with Scale-Free properties method.
	 * 
	 * This code is taken from the SPECI2, (c) Ilango Sriram
	 * 
	 * Klemm-Eguiluz growing DIRECTED scale free networks with
	 * small-world behaviour as described in 
	 * http://arxiv.org/abs/cond-mat/0107607
	 * The number of the
	 * initial unconnected set of nodes is #max_num_subscriptions. 
	 * The next added node is connected to all of these initial nodes,
	 * and after that the BA model is used normally.
	 * 
	 * @param max_num_subscriptions - full description in initilise
	 * @param miuInScaleFreeSmallWorld - (example 0.15)
	 * @param timestamp - should be 0 if subscriptions happen at start of simulation
	 */
	@Override
	protected void configureNetworkTopology() {
		
		final int NUMBER_OF_NODES = allNodes.length;

		// is number of subscriptions though the code, but from what i can tell
		//it is the number of subscrptions per node
		final int NUMBER_SUBSCRIPTIONS = params.getMaxSubscriptions();

		//set indices to nodes and shuffles them into a random order
		ArrayList<Integer> unsubscribedNodes = new ArrayList<Integer>(NUMBER_OF_NODES);
		for (int i = 0; i < NUMBER_OF_NODES; i++) 
		{
			unsubscribedNodes.add((Integer)i);				
		}
		Collections.shuffle(unsubscribedNodes);

		//no idea why this would be here, seems fairly obvious to me
		if (unsubscribedNodes.size() != NUMBER_OF_NODES)
			System.out.println("#-# Something wrong here. NUMBER_OF_NODES != size of nodes ");

		// edge i goes from edges[2*i] to edges[2*i+1], 
		//so we need twice as many fields as edges
		//this will be slightly less than this due to initial unconnected nodes:
		int[] edges = new int[(2*NUMBER_OF_NODES*(NUMBER_SUBSCRIPTIONS+1))];
		//init the array edges with -1;
		for (int i = 0; i < edges.length; i++) 
		{
			edges[i] = -1;
		}
		int edgesFound = 0;

		//save the nodes that are marked active in the algorithm 
		//		ArrayList<Integer> active = new ArrayList<Integer>();
		int[] activeArray = new int[NUMBER_SUBSCRIPTIONS];
		int[] activeNodeDegree = new int[NUMBER_SUBSCRIPTIONS];
		for (int i = 0; i<NUMBER_SUBSCRIPTIONS; i++)
		{
			activeArray[i]=0;
			activeNodeDegree[i]=0;
		}


		/* 
		 * Part 1, Start:
		 * #NUMBER_SUBSCRIPTIONS fully connected nodes are unconnected, and 
		 * one nodes is connected to and from all of the initial set
		 * All initial nodes are marked active
		 * 
		 */
		//pick the initial set
		for(int i=0; i < NUMBER_SUBSCRIPTIONS; i++)
		{
			activeArray[i]=(int) unsubscribedNodes.remove(0);
		}


		// Fully connect initial set:
		// Link all to and from the last one: 	
		for(int i=0; i < NUMBER_SUBSCRIPTIONS; i++)
		{
			for(int j=0; j < NUMBER_SUBSCRIPTIONS; j++)
			{
				if (i!=j)
				{
					edges[2*edgesFound]= activeArray[i]; 
					edges[2*edgesFound+1]= activeArray[j];
					activeNodeDegree[i]+=1;
					activeNodeDegree[j]+=1;
					edgesFound++;
				}
			}
		}

		if (edgesFound> (NUMBER_OF_NODES*NUMBER_SUBSCRIPTIONS))
		{
			System.out.println("Error! 1 Too many Edges: "+edgesFound);
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

			//Step (i) a new node joins
			int current  = (int)unsubscribedNodes.remove(0);
			int edgesFoundB4Current = edgesFound;
			// connect to active nodes with probability 1-Miu else to random node with pref attachement
			for (int i=0; i < NUMBER_SUBSCRIPTIONS; i++)
			{

				if (uniformDistribution.nextDouble() > params.getMiu())
				{
					//in this case connect to the active node - small-world case
					
					if(uniformDistribution.nextDouble() <= SubscriptionsModuleConfigParams.DEFAULT_INVERT_EDGE_PROBABILITY)
					{
						edges[2*edgesFound]= current; 
						edges[2*edgesFound+1]= activeArray[i];
						activeNodeDegree[i]+=1;
						edgesFound++;
					}
					else
					{
						edges[2*edgesFound]= activeArray[i]; 
						edges[2*edgesFound+1]= current;
						activeNodeDegree[i]+=1;
						edgesFound++;
					}					

				}					
				else
				{
					// node joins to random node with preferential attachement  - Barabasi-Albert case
					int targetCandidate = -1;
					boolean validCandidate = false;
					while(!validCandidate)
					{
						targetCandidate = edges[uniformDistribution.nextIntFromTo(0, (2*edgesFound)-1)]; 						
						//Check if this Candidate is valid
						//it has to be different than self and not active
						if (targetCandidate == current)
							continue;					

						//and targetCandidate can't be used for current already
						//current is wired in the array from (2*edgesFound-2*i) to (2*edgesFound+1)
						validCandidate = true;
						for (int k = (2*edgesFoundB4Current); k < (2*edgesFound); k++) 
						{
							if (edges[k]==targetCandidate)
								validCandidate = false;
						}
					}

					for (int j=0; j<activeArray.length; j++ )
					{
						if (activeArray[j] == targetCandidate)
							activeNodeDegree[j] +=1;
					}					
					//Now set the edge:								
					//with low probability invert the edge
					//Invert Probability of 0.15 selected following
					//Yuan, Chen and Wang (2007) Growing Directed Networks: 
					//http://arxiv.org/abs/cond-mat/0408391
					if(uniformDistribution.nextDouble() <= SubscriptionsModuleConfigParams.DEFAULT_INVERT_EDGE_PROBABILITY)
					{
						edges[2*edgesFound]= targetCandidate;
						edges[2*edgesFound+1]= current ;
						edgesFound++;
					}
					else{
						edges[2*edgesFound]=current;
						edges[2*edgesFound+1]=targetCandidate;
						edgesFound++;
					}

				}
				if (edgesFound> (NUMBER_OF_NODES*NUMBER_SUBSCRIPTIONS))
				{
					System.out.println("Error! 2.. many Edges: "+edgesFound);
				}

			}

			// Now Steps (ii) and (iii) 
			// one of the active nodes is replaced by the current node

			//Get the degree of all active nodes  
			//			int[] deactivationDegree = new int[NUMBER_SUBSCRIPTIONS];
			double normalisation = 0.0;
			//Get the total degree of each node, and sum the inverse for normalisation factor
			for (int i=0; i < NUMBER_SUBSCRIPTIONS; i++)
			{
				normalisation += (1.0/activeNodeDegree[i]);
			}
			//Normalisation is factor "a" in the paper, so invert
			normalisation = 1.0/normalisation; 

			//Throw a draw
			double draw = uniformDistribution.nextDouble();
			//sum the probabilities. As soon as "draw" is reached this is the node to replace 
			for (int i=0; i < NUMBER_SUBSCRIPTIONS; i++)
			{
				draw = (draw - (normalisation*(1.0/activeNodeDegree[i])));
				if (draw <= 0){
					activeArray[i]=current;
					activeNodeDegree[i]=NUMBER_SUBSCRIPTIONS;
					break;
				}	
			}
			if (draw > 0)
			{
				System.out.println("Error, no node was inactivated. Probability greater than 100% ");
				activeArray[NUMBER_SUBSCRIPTIONS-1]=current;
				activeNodeDegree[NUMBER_SUBSCRIPTIONS-1]=NUMBER_SUBSCRIPTIONS;
			}
		}
		//System.out.println("Total edges found: "+edgesFound);

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
