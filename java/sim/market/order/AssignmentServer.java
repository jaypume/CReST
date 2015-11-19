/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
package sim.market.order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.trader.BaseTrader;
import sim.market.trader.InstanceTrader;


/**
 * Set up demand and supply profiles and serve assignments to traders
 *
 */
public class AssignmentServer {

	public static Logger logger = Logger.getLogger(AssignmentServer.class);
	
	protected DecimalFormat format = new DecimalFormat("#.##");
	
	protected Random prng;
	protected Commodity commodity;
	protected DemandSchedule demand;
	protected SupplySchedule supply;
	protected List<Assignment> assignments;
	
	public AssignmentServer (Random prng, Commodity c) {
		commodity = c;
		this.prng = prng;
		//Set up a demand schedule and a supply schedule
		demand = new DemandSchedule(c);
		supply = new SupplySchedule(c);
		assignments = new ArrayList<Assignment>();
	}
	
	public void generateTestSchedules(int lowPrice, int highPrice, int stepPrice, int volume, int numTraders) {
		
		for(int i=0; i<numTraders/2; i++) {
			int price = lowPrice + stepPrice*i;
			demand.add(new PriceVolumeTuple(price,prng.nextInt(volume)+1));
			supply.add(new PriceVolumeTuple(price,prng.nextInt(volume)+1));
		}
		logger.info("Demand Schedule generated: " + demand);
		logger.info("Supply Schedule generated: " + supply);
	}
	
	public void generateDemandSchedule(int lowPrice, int highPrice, int volume, int numTraders) {
				
		double stepPrice = 0;
		if(numTraders>1) stepPrice = (highPrice - lowPrice)/(double)(numTraders-1);

		logger.info("New demand schedule: [" + highPrice + ", " + lowPrice + ", " + format.format(stepPrice) + ", numTraders=" + numTraders + "]");

		for(int i=0; i<numTraders; i++) {
			int price = (int) Math.round(lowPrice + stepPrice*i);
			demand.add(new PriceVolumeTuple(price,prng.nextInt(volume)+1));
		}
		logger.info("Demand Schedule generated: " + demand);
	}	
	
	public void generateSupplySchedule(int lowPrice, int highPrice, int volume, int numTraders) {
		
		double stepPrice = 0;
		if(numTraders>1) stepPrice = (highPrice - lowPrice)/(double)(numTraders-1);
		
		logger.info("New supply schedule: [" + lowPrice + ", " + highPrice + ", " + format.format(stepPrice) + ", numTraders=" + numTraders + "]");
		
		for(int i=0; i<numTraders; i++) {
			int price = (int) Math.round(lowPrice + stepPrice*i);
			supply.add(new PriceVolumeTuple(price,prng.nextInt(volume)+1));
		}
		logger.info("Supply Schedule generated: " + supply);
	}
	
	public void assign(List<BaseTrader> population, long time) {
		
		boolean buy = true;
		Assignment a;
		
		for(BaseTrader t: population) {
			time += 1;
			if(buy) {
				PriceVolumeTuple pvt = demand.getNext();
				a = new Assignment(t, pvt.getPrice(), pvt.getVolume(), Direction.BUY, commodity, time, time+1000);
			} else {
				PriceVolumeTuple pvt = supply.getNext();
				a = new Assignment(t, pvt.getPrice(), pvt.getVolume(), Direction.SELL, commodity, time, time+1000);
			}
			
			// add to assignments list
			assignments.add(a);
			// send to trader
			t.addAssignment(a);
			
			buy = !buy;
		}
	}
	
	/**
	 * Assign demand to the population
	 * @param population - the population of traders
	 * @param probDemand - the probability of assigning each trader a unit of demand (ie the proportion of the population that has demand)
	 * @param time - current timestamp
	 * 
	 * @return total volume assigned
	 */
	public int assignDemand(List<BaseTrader> population, double probDemand, long time) {
		
		logger.info("Assigning demand...");
		
		Assignment a;
		int vol = 0;
		
		for(BaseTrader t: population) {
			time += 1;
			if(prng.nextDouble()<probDemand) {
				PriceVolumeTuple pvt = demand.getNext();
				a = new Assignment(t, pvt.getPrice(), pvt.getVolume(), Direction.BUY, commodity, time, time+1000);
		
				// add to assignments list
				assignments.add(a);
				// send to trader
				t.addAssignment(a);
				
				vol += pvt.getVolume();
			}
		}
		return vol;
	}
	
	/**
	 * Assign internal sell limit prices to InstanceTraders for re-selling RI instances
	 * @param population - the population of InstanceTraders to assign limit internal sell limit prices
	 */
	public void assignInternalSellLimitPrices(List<InstanceTrader> population) {
		for(InstanceTrader t: population) {
			PriceVolumeTuple pvt = supply.getNext();
			t.setInternalSellLimitPrice((int) pvt.getPrice());
		}
	}
	
	/**
	 * Cancel all assignments
	 */
	public void cancelAll() {
		logger.debug("Cancelling all assignments...");
		for(Assignment a: assignments) {
			a.getOwner().cancelAssignment(a);
		}
		logger.debug("Finished cancelling assignments");
	}
	
	public DemandSchedule getDemand() {
		return demand;
	}
	
	public SupplySchedule getSupply() {
		return supply;
	}
}
