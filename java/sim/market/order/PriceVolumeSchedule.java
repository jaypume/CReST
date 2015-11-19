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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Price-Volume schedule for a commodity (i.e., a demand or a supply curve)
 *
 */
public abstract class PriceVolumeSchedule {

	public static Logger logger = Logger.getLogger(PriceVolumeSchedule.class);
	
	protected Commodity c;
	protected String name = "Abstract Commodity Schedule";
	protected Direction direction; 
	protected int pointer = -1;
	
	List<PriceVolumeTuple> schedule;
	
	public PriceVolumeSchedule(String name, Commodity commodity, Direction direction) {
		this.name = name;
		this.c = commodity;
		this.direction = direction;
		schedule = new ArrayList<PriceVolumeTuple>();
	}
	
	/**
	 * Convert a list of assignments into a list of PriceVolumeTuples
	 * @param assignments - list of assignments
	 * @return PVT list (with each tuple having a unique price)
	 */
	public PriceVolumeSchedule(List<Assignment> assignments) {

		schedule = new ArrayList<PriceVolumeTuple>();
		if(assignments.size()>0) {
			this.direction = assignments.get(0).getDirection();
			this.name = direction + " Schedule";
			this.c =  assignments.get(0).getCommodity();
		} else {
			return;
		}
		setSchedule(assignments);
	}

	/**
	 * Set the schedule using a list of assignments
	 * @param assignments 
	 */
	protected void setSchedule(List<Assignment> assignments) {
		
		schedule = new ArrayList<PriceVolumeTuple>();
		boolean firstAssignment = true;
		double lastPrice = -1;
		int volumeCounter = 0;
		
		for(Assignment a: assignments) {
			
			if(!(a.getCommodity()==c) || !(a.getDirection()==direction)) {
				logger.warn("Assignment of incorrect type. Not adding to schedule: " + a);
			} else {
				if(firstAssignment) {
					lastPrice = a.getLimitPrice();
					volumeCounter = a.getVolume();
					firstAssignment = false;
				} else {
					if(a.getLimitPrice()==lastPrice) {
						volumeCounter += a.getVolume();
					} else {
						PriceVolumeTuple pvt = new PriceVolumeTuple(lastPrice, volumeCounter);
						schedule.add(pvt);
						lastPrice = a.getLimitPrice();
						volumeCounter = a.getVolume();
					}
				}
			}
		}
		PriceVolumeTuple pvt = new PriceVolumeTuple(lastPrice, volumeCounter);
		schedule.add(pvt);
	}
	
	public void add(PriceVolumeTuple pv) {
		schedule.add(pv);
		sort();
	}
	
	public List<PriceVolumeTuple> getSchedule() {
		return schedule;
		
	}
	
	/**
	 * Return the next PriceVolumeTuple in the schedule.
	 * 
	 * @return the next tuple
	 */
	public PriceVolumeTuple getNext() {
		if(schedule.size()>0) {
			pointer++;
			if(pointer>=schedule.size()) pointer=0;
			logger.debug("Returning pv tuple using pointer " + pointer);
			return schedule.get(pointer);
		} else {
			logger.warn("Schedule is null. Returning null tuple");
			return null;
		}
	}
	
	/**
	 * Sort the schedule. 
	 * If demand schedule, sort highest price first.
	 * If supply schedule sort lowest price first.
	 */
	protected void sort() {
		Collections.sort(schedule); //now ordered lowest price first 
		if(this.direction.isBid()) Collections.sort(schedule,Collections.reverseOrder()); //reverse the order to highest price first for demand schedule
	}
	
	public String toString() {
		return name + ": " + schedule + "";
	}
	
	public int getTotalVolume() {
		int vol = 0;
		for(PriceVolumeTuple pvt: schedule) {
			vol+=pvt.getVolume();
		}
		return vol;
	}

	/**
	 * Convert a List of PriceVolumeTuples to a List where each Tuple has volume=1
	 * @param list - the original list
	 * @return a list with unit volume
	 */
	public static List<PriceVolumeTuple> convertToUnitVolume(List<PriceVolumeTuple> list) {
		
		PriceVolumeTuple.logger.info("Converting PVT list to unit volume list...");
		
		List<PriceVolumeTuple> unitVolList = new ArrayList<PriceVolumeTuple>();
		
		for(PriceVolumeTuple pvt: list) {
			for(int i=0; i<pvt.getVolume(); i++) {
				unitVolList.add(new PriceVolumeTuple(pvt.getPrice(),1));
			}
		}
		
		PriceVolumeTuple.logger.debug("Original list: " + list);
		PriceVolumeTuple.logger.debug("Unit volume list: " + unitVolList);
		
		return unitVolList;
	}
	
	/**
	 * Get the schedule as a PriceVolumeTuple list with each having unit volume
	 * @return PVT list, each element having unit volume
	 */
	protected List<PriceVolumeTuple> getUnitSchedule() {
		
		PriceVolumeTuple.logger.debug("Converting schedule to unit volume list...");
		
		List<PriceVolumeTuple> unitVolList = new ArrayList<PriceVolumeTuple>();
		
		for(PriceVolumeTuple pvt: schedule) {
			for(int i=0; i<pvt.getVolume(); i++) {
				unitVolList.add(new PriceVolumeTuple(pvt.getPrice(),1));
			}
		}
		
		PriceVolumeTuple.logger.debug("Original list: " + schedule);
		PriceVolumeTuple.logger.debug("Unit volume list: " + unitVolList);
		
		return unitVolList;	
	}
	
	/**
	 * Get the equilibrium price and volume of a demand and supply schedule
	 * @param demand - the demand schedule
	 * @param supply - the supply schedule
	 * @return equilibrium price and volume
	 */
	public static PriceVolumeTuple getEquilibrium(DemandSchedule demand, SupplySchedule supply) {
		
		PriceVolumeTuple equilibrium;
		
		logger.info("Calculating equilibrium");
		logger.info("Demand = " + demand);
		logger.info("Supply = " + supply);
		
		double p0 = 0;
		int v0 = 0;
		double totalSurplus = 0;
		
		// Find equilibrium value: logic reproduced from Cliff & Bruten (1997) pp. 97-98.
		if(supply.getSchedule().size()==0 || demand.getSchedule().size()==0) {
			logger.warn("No equilibrium");
			return new PriceVolumeTuple(); // return null Tuple
		} else if(supply.getSchedule().get(0).getPrice() > demand.getSchedule().get(0).getPrice()) {
			logger.warn("Lowest sell limit is higher than highest buy limit. No equilibrium");
			return new PriceVolumeTuple(); // return null Tuple
		} else { // find intersection points for demand and supply curves... 
			
			List<PriceVolumeTuple> unitDemand = demand.getUnitSchedule();
			List<PriceVolumeTuple> unitSupply = supply.getUnitSchedule();
			
			int demandVol = unitDemand.size();
			int supplyVol = unitSupply.size();
			logger.warn("Total demand = " +  demandVol + ", Total supply = " + supplyVol);
			
			if(demandVol == 0 || supplyVol==0) return new PriceVolumeTuple(); // return null Tuple
			
			int maxSize = (demandVol>supplyVol)?demandVol:supplyVol;
			
			boolean notFound = true;
			
			double profit = 0;
		
			for(int i=0; i<maxSize; i++) {

				if(notFound) {
					
					profit = unitDemand.get(i).getPrice() - unitSupply.get(i).getPrice();
					
					if(unitSupply.get(i).getPrice()>unitDemand.get(i).getPrice()) { // supply price is greater than demand
					
						p0 = (unitSupply.get(i-1).getPrice() + unitDemand.get(i-1).getPrice()) / 2.0; //intersection
						v0 = i;
						notFound = false;
						
						logger.debug("Supply is greater than demand. Calculating simple intersection...");
						logger.debug("Unit=" + i+ ", Supply price [" + unitSupply.get(i).getPrice() 
								+ "] > Demand price [ " + unitDemand.get(i).getPrice() + "] ");
						logger.debug("Last supply price = " + unitSupply.get(i-1).getPrice());
						logger.debug("Last demand price = " + unitDemand.get(i-1).getPrice());
						logger.debug("Setting p0=" + p0 + " and q0=" + v0);
						
					} else {
						
						if((i+1==supplyVol) && (i+1==demandVol)) { // last buyer and seller
							
							p0 = (unitSupply.get(i).getPrice() + unitDemand.get(i).getPrice()) / 2.0; 
							v0 = i+1;
							totalSurplus += profit;
							notFound = false;
							
							logger.debug("Last buyer and seller. Calculating equilibrium as midpoint...");
							logger.debug("Unit=" + i+ ", Supply price [" + unitSupply.get(i).getPrice() 
									+ "],  Demand price [ " + unitDemand.get(i).getPrice() + "] ");
							logger.debug("Setting p0=" + p0 + " and q0=" + v0);
							
						} else {
							
							if((i+1)==supplyVol) { // last seller, but still some buyers
								
								p0 = (unitDemand.get(i).getPrice() +  unitDemand.get(i+1).getPrice() ) / 2.0;
								v0 = i+1;
								totalSurplus += profit;
								notFound = false;
								
								logger.debug("Last seller, but still some buyers. Calculating equilibrium as demand midpoint...");
								logger.debug("Unit=" + i+ ", Demand price [" + unitDemand.get(i).getPrice() 
										+ "],  Next demand price [ " + unitDemand.get(i+1).getPrice() + "] ");
								logger.debug("Setting p0=" + p0 + " and q0=" + v0);
								
							} else if ((i+1)==demandVol) { // last buyer, but still some sellers
								
								p0 = (unitSupply.get(i).getPrice() +  unitSupply.get(i+1).getPrice() ) / 2.0;
								v0 = i+1;
								totalSurplus += profit;
								notFound = false;
								
								logger.debug("Last buyer, but still some sellers. Calculating equilibrium as supply midpoint...");
								logger.debug("Unit=" + i+ ", Supply price [" + unitSupply.get(i).getPrice() 
										+ "],  Next supply price [ " + unitSupply.get(i+1).getPrice() + "] ");
								logger.debug("Setting p0=" + p0 + " and q0=" + v0);
							}
						}
					}
				}
				
				if(notFound) {
					totalSurplus += profit;
				}
			}
			
		}
		
		logger.info("Total surplus profit = " + totalSurplus);
		
		equilibrium = new PriceVolumeTuple(p0,v0);
		
		logger.info("Equilibrium = [" + equilibrium + "]");
		
		return equilibrium;
	}
	
	/**
	 * Get the surplus profit from a demand and supply schedule, using a pre-calculated equilibrium value. 
	 * @param demand - the demand schedule
	 * @param supply - the supply schedule
	 * @param equilibrium - the market equilibrium
	 * @return surplusProfit (= intramarginal demand - supply)
	 */
	public static double getSurplusProfit(DemandSchedule demand, SupplySchedule supply, PriceVolumeTuple equilibrium) {

		if(equilibrium==null) return 0;
		
		double surplusProfit = 0;
		for(int i=0; i<equilibrium.getVolume(); i++) {
			surplusProfit += demand.getUnitSchedule().get(i).getPrice()-supply.getUnitSchedule().get(i).getPrice();
		}
		return surplusProfit;
	}
}
