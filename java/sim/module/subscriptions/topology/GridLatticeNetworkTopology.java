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

public class GridLatticeNetworkTopology extends AbstractNetworkTopology {

	public GridLatticeNetworkTopology(IP[] ips_array, int[] ids_array,
			SubscriptionsModuleConfigParams configParams, long timestamp) {
		super(ips_array, ids_array, configParams, timestamp);
		// TODO Auto-generated constructor stub
	}

	/**
	 * nodes are arranged on a toroidal grid/lattice network structure and then subscribed to their K nearest neighbours
	 * 
	 * subscriptions for node i:
	 * 	subscribe to the node located above i, 
	 * 	and continue subscribing to each neighbouring node iterating coordinates clockwise
	 *  	until total number of K subscriptions reached.
	 *  	If grid boundary is reached, overalp to other side of grid (torus structure)
	 *  
	 *  Taken from Ilango Sriram's SPECI:
	 *  www.speci.org
	 *  
	 * @param timestamp - should be 0 
	 * @param MAX_NUMBER_SUBSCRIPTIONS - maximum number of subscriptions
	 */
	@Override
	protected void configureNetworkTopology() {
		
		//Regular nodes are placed on a grid with width floor(sqrt(#nodes)) and height sqrt(#nodes) or sqrt(#nodes)-1
		//MAX_NUMBER_SUBSCRIPTIONS must be < NUMBER_OF_NODES
		final int MAX_SUBSCRIPTIONS = params.getMaxSubscriptions();
		
		final int LEFT = 0;
		final int UP = 1;
		final int RIGHT = 2;
		final int DOWN = 3;
		final int NUMBER_OF_NODES = allNodes.length;
		
		int columns= (int)Math.floor( Math.sqrt( NUMBER_OF_NODES));
		int rows= (int) Math.ceil( (1.0 * NUMBER_OF_NODES) / (1.0 * columns) );

		int currentX, currentY=0; //Coordinates of currentNode
		int currentMinX, currentMaxX, currentMinY, currentMaxY, countSubscribed; //coordinates and value of the subscription to be added
		int lastIncrease;

		//Wire subscriptions for every node 
		for (int i = 0; i < NUMBER_OF_NODES; i++) {
			HashMap<Integer, Subscription> newSubscriptions = new HashMap<Integer, Subscription>();

			// GET COORDINATES OF i
			countSubscribed =0;
			lastIncrease=LEFT;
			currentY = i/columns;//integer division: this equals to Math.floor( (1.0*i)/(1.0*columns) )
			currentX = i - (currentY * columns); 
			currentMinX = currentX;
			currentMaxX = currentX;
			currentMinY = currentY;
			currentMaxY = currentY;

			while (countSubscribed<MAX_SUBSCRIPTIONS){
				lastIncrease++;
				if (lastIncrease>3)
					lastIncrease-=4;
				
				switch (lastIncrease) {
				case UP:
					currentMinY--;
					for (int x = currentMinX; x <= currentMaxX; x++) {
						if (countSubscribed>=MAX_SUBSCRIPTIONS){
							break;
						}
						addGrid(x, currentMinY, columns, rows, NUMBER_OF_NODES, newSubscriptions, timeNow);
						countSubscribed++;
					}
					break;
				case RIGHT:
					currentMaxX++;
					for (int y = currentMinY; y <= currentMaxY; y++) {
						if (countSubscribed>=MAX_SUBSCRIPTIONS){
							break;
						}
						addGrid(currentMaxX, y, columns, rows, NUMBER_OF_NODES, newSubscriptions, timeNow);
						countSubscribed++;
					}
					break;
				case DOWN:
					currentMaxY++;
					for (int x = currentMaxX; x >= currentMinX; x--) {
						if (countSubscribed>=MAX_SUBSCRIPTIONS){
							break;
						}
						addGrid(x, currentMaxY, columns, rows, NUMBER_OF_NODES, newSubscriptions, timeNow);
						countSubscribed++;
					}
					break;
				case LEFT:
					currentMinX--;
					for (int y = currentMaxY; y >= currentMinY; y--) {
						if (countSubscribed>=MAX_SUBSCRIPTIONS){
							break;
						}
						addGrid(currentMinX, y, columns, rows, NUMBER_OF_NODES, newSubscriptions, timeNow);
						countSubscribed++;
					}
					break;
				default:
					break;
				}			
				
			}

			allNodes[i].setSubscriptions(newSubscriptions);
		}
	}

	/**
	 * Used to build grid for RegularGridLattice subscription network
	 * @param x
	 * @param y
	 * @param columns
	 * @param rows
	 * @param total
	 * @param newSubscriptions
	 * @param timestamp
	 */
	private void addGrid(int x, int y, int columns, int rows, int total, HashMap<Integer, Subscription> newSubscriptions, long timestamp) {		
		
		//if the current coordinates are across the border of the big square bring them in from other end
 		if (y<0)
			y+=rows;
		if (y>rows)
			y-=rows;

		if (x<0)
			x+=columns;
		if (x>columns)
			x-=columns;					
	
		//get the number of the current node and subscribe it	
		int subscribeToInt = (y * columns) + x;				
		//if the outer grid does not fill a rectangle the coordinate might not exist so:

		subscribeToInt = subscribeToInt%total;	
		
		//and actually subscribe
		newSubscriptions.put(subscribeToInt, new Subscription(true, timestamp));
	}
	
}
