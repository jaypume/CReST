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
import java.util.List;

import org.apache.log4j.Logger;

import sim.market.OrderBook;
import sim.market.trader.BaseTrader;

/**
 * Used by traders to manage orders and assignments.
 *
 */
public class OrderManagementSystem {

	public static Logger logger = Logger.getLogger(OrderManagementSystem.class);
	
	protected BaseTrader owner;
	
	protected List<Assignment> assignments;
	protected List<Assignment> completedAssignments;	
	
	protected boolean storeCompletedAssignments = true; // If true, store a record of all completed assignments.
	
	protected int totalBuyAssigned;
	protected int totalSellAssigned;
	
	protected int monthlySales = 0;
	
	public OrderManagementSystem(BaseTrader owner) {
		
		this.owner = owner;
		assignments = new ArrayList<Assignment>();
		completedAssignments = new ArrayList<Assignment>();
		totalBuyAssigned = 0;
		totalSellAssigned = 0;
	}
	
	/**
	 * Get list of assignments (both buy and sell)
	 * @return assignments
	 */
	public List<Assignment> getAssignments() {
		return assignments;
	}
	
	/**
	 * Get all assignments. Both current and completed.
	 * @return all assignments
	 */
	public List<Assignment> getAllAssignments() {
		List<Assignment> allAssignments = new ArrayList<Assignment>();
		allAssignments.addAll(assignments);
		allAssignments.addAll(completedAssignments);
		return allAssignments;
	}
	
	/**
	 * Get list of buy assignments
	 * @return assignments
	 */
	public List<Assignment> getDemandAssignments() {
		List<Assignment> demand = new ArrayList<Assignment>();
		for(Assignment a: assignments) {
			if(a.isBuy()) demand.add(a);
		}
		return demand;
	}	
	
	/**
	 * Get list of buy assignments for a commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public List<Assignment> getDemandAssignments(Commodity c) {
		List<Assignment> demand = new ArrayList<Assignment>();
		for(Assignment a: assignments) {
			if(a.getCommodity()==c && a.isBuy()) demand.add(a);
		}
		return demand;
	}
	
	/**
	 * Get total demand volume for all assignments for a commodity
	 * @param c - the commodity
	 * @return the total volume demanded for this commodity
	 */		
	public int getDemandVolume(Commodity c) {
		List<Assignment> demand = getDemandAssignments(c);
		if(demand!=null && demand.size()>0) {
			int totalVol = 0;
			for(Assignment d: demand) {
				totalVol += d.getVolume();
			}
			return totalVol;
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the total volume of all completed assignments
	 * @return - total volume completed
	 */
	public int getCompletedVolume() {
		
		int volume = 0;
		for(Assignment a: completedAssignments) {
			volume += a.volume;
		}
		return volume;
	}
	
	/**
	 * Get the total volume assigned to buy
	 * @return total volume assigned
	 */
	public int getTotalBuyVolumeAssigned() {
		return totalBuyAssigned;
	}
	
	/**
	 * Get the total volume assigned to sell
	 * @return total volume assigned
	 */
	public int getTotalSellVolumeAssigned() {
		return totalSellAssigned;
	}
	
	/**
	 * Get total supply volume for all assignments for a commodity
	 * @param c - the commodity
	 * @return the total volume supplied for this commodity
	 */		
	public int getSupplyVolume(Commodity c) {
		List<Assignment> supply = getSupplyAssignments(c);
		if(supply!=null && supply.size()>0) {
			int totalVol = 0;
			for(Assignment d: supply) {
				totalVol += d.getVolume();
			}
			return totalVol;
		} else {
			return 0;
		}
	}
	
	/**
	 * Set all demand (BUY) assignments for commodity c to complete (demand has been fulfilled elsewhere)
	 * @param c - commodity
	 * @return true if assignments completed, false otherwise
	 */
	public boolean demandFulfilled(Commodity c) {
		List<Assignment> demand = getDemandAssignments(c);
		if(demand!=null && demand.size()>0) {
			for(Assignment d: demand) {
				d.setCompleted();
				assignments.remove(d);
				if(storeCompletedAssignments) completedAssignments.add(d);
			}
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * Get list of assignments to sell
	 * @return assignments
	 */
	public List<Assignment> getSupplyAssignments() {
		List<Assignment> supply = new ArrayList<Assignment>();
		for(Assignment a: assignments) {
			if(a.isSell()) supply.add(a);
		}
		return supply;
	}
	
	/**
	 * Get list of assignments to sell for a specific commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public List<Assignment> getSupplyAssignments(Commodity c) {
		List<Assignment> supply = new ArrayList<Assignment>();
		for(Assignment a: assignments) {
			if(a.getCommodity()==c && a.isSell()) supply.add(a);
		}
		return supply;
	}
	
	public List<Assignment> getCompletedAssignments() {
		if(storeCompletedAssignments) logger.warn("OMS not storing completed assignments...");
		return completedAssignments;
	}
	
	/**
	 * Perform some basic admin/accounting. 
	 * Move completed assignments to the completed list
	 */
	public void performAdmin() {
		List<Assignment> toRemove = new ArrayList<Assignment>();
		for(Assignment a: assignments) {
			if(a.isComplete()) {
				toRemove.add(a);
			}
		}
		for(Assignment a: toRemove) {
			assignments.remove(a);
			if(completedAssignments.contains(a)) {
				logger.warn("completed assignments already contains assignment " + a);
			} else {
				if(storeCompletedAssignments) completedAssignments.add(a);
			}
		}
	}
	
	public boolean add(Assignment a) {
	
		boolean successful = false;
		if(assignments.contains(a)) {
			successful = false;
			logger.debug("Can't add assignment: " + a + ". Already exists in oms: " + this);
		} else {
			assignments.add(a);
			logger.debug("Adding assignment: #" + a.getId() + ". oms is now: " + this);
			successful = true;
			if(a.isBuy()) totalBuyAssigned += a.getVolume();
			else totalSellAssigned += a.getVolume();
		}
		return successful;
	}
	
	/**
	 * Cancel assignment to assignments list
	 * @param a - assignment to remove
	 * @return true if assignment removed, false otherwise
	 */
	public boolean cancel(Assignment a) {				
		
		ArrayList<Order> orders;
		
		//check, do we already have this assignment?
		if(assignments.contains(a)) {
			logger.debug("Cancelling assignment: " + a);
			
			//We first need to cancel all associated orders from the orderbook
			orders = new ArrayList<Order>();
			orders.addAll(a.getOrders());			
			for(Order o: orders) {
				OrderBook.getBook(o.getCommodity()).cancelOrder(o);
			}
			
			logger.debug("Orders cancelled");			
			assignments.remove(a);
			logger.debug("Assignment removed: " + this);
			return true;
		} else {
			logger.debug("Cannot remove assignment: " + a + ". Assignment unknown.");
			return false;
		}
	}	
	
	public boolean contains(Assignment a) {
		
		if(completedAssignments.contains(a)) logger.info("Assignment has been completed.");
		return assignments.contains(a);
	}
	
	public List<Order> getOrders() {
		List<Order> orders = new ArrayList<Order>();
		for(Assignment a: assignments) {
			for(Order o: a.getOrders()) {
				orders.add(o);
			}
		}
		return orders;
	}

	/**
	 * Delete an order from the OMS
	 * @param o - the order to delete
	 * @return true if successful deletion, false otherwise
	 */
	public boolean deleteOrder(Order order) {
	
		boolean successful = false;
	
		for(Assignment a: assignments) {
			if(a.getOrders().contains(order)) {
				logger.debug("removing order " + order + " from assignment " + a);
				a.deleteOrder(order);
				logger.debug("assignment is now: " + a);
				successful = true;
			}
		}
		if(!successful) {
			logger.warn("Order not owned by trader. Cannot delete. Order: " + order );
		}
		return successful;
	}
	
	/**
	 * An order has executed. Update OMS.
	 * @param order - the order that has executed
	 * @return true if successful, false otherwise
	 */
	public boolean orderExecuted(Order order) {

		boolean successful = false;

		for(Assignment a: assignments) {
			if(a.contains(order)) {
				logger.debug("Order " + order + " has executed from assignment " + a.getId());
				a.orderExecutes(order);
				logger.debug("Assignment updated: " + a);
				successful = true;
			}
		}
		if(!successful) {
			logger.warn("Order unknown to OMS. Cannot delete. Order: " + order );
		}
		performAdmin();
		return successful;	
	}
	
	/**
	 * Return assignment associated with an order
	 * @param order - the order
	 * @return the assignment
	 */
	protected Assignment getAssignment(Order order) {
		
		Assignment ass = null;
		boolean success = false;
		
		for(Assignment a: assignments) {
			if(a.contains(order)) {
				ass = a;
				success = true;
			}
		}
		if(!success) logger.warn("Could not find assignment associated with order " + order + ". Returning null");
		return ass;
	}
	
	public void clearCompletedAssignments() {
		logger.info("Clearing completed assignments");
		completedAssignments.clear();
	}
	
	public boolean match(Commodity c) {
		
		boolean match = false;
		
		List<Assignment> buy = this.getDemandAssignments(c);
		List<Assignment> sell = this.getSupplyAssignments(c);
		
		if(buy.size()>0 && sell.size()>0) {
			logger.info("We have internal matches to make... Demand: " + buy + ", Supply: " + sell);
			logger.warn("I dont have any logic to do this yet!!! TODO...");		//TODO
			match = true;
		}
		
		return match;
	}
	
	/**
	 * Increment monthly sales
	 * @param increment - increment amount
	 */
	public void incrementMonthlySales(int increment) {
		monthlySales+=increment;
	}
	
	public void resetMonthlySales() {
		monthlySales = 0;
	}
	
	/**
	 * Match demand against BUY assignments in the OMS with volume = amount 
	 * @param c - commodity
	 * @param amount - amount/volume to match
	 * @return amount not matched (0 if all matched)
	 */
	public int matchDemand(Commodity c, int amount) {
		List<Assignment> buy = new ArrayList<Assignment>();
		buy.addAll(this.getDemandAssignments(c));
		//TODO - sort on volume... and price?
		for(Assignment b: buy) {
			int volume = b.getVolume();
			if(amount>=volume) {
				//complete the entire assignment
				amount -= volume;
				b.setCompleted();
			} else {
				logger.warn("Reduce volume of assignment. TODO - logic does not exist...");
				amount = 0;
			}
		}
		performAdmin(); //move all completed assignments to completed list
		return amount;
	}
	
	public void orderSplit(Order newOrder, Order oldOrder) {
		
		Assignment a = getAssignment(oldOrder);
		if(a!=null) {
			a.newOrder(newOrder);
			logger.debug("Order added to assignment " + a);
		} else {
			logger.warn("Assignment not found. Split order lost!");
		}
	}
	
	public String toString() {
		String s = "\nOrder Management System for Trader #" + owner.getId();
		s += "\nAssignments pending:";
		if (assignments.size()==0) {
			s+= "\n[None]";
		}
		for (Assignment a: assignments) {
			s += "\n" + a;
		}
		s += "\nAssignments completed:";
		if(storeCompletedAssignments) {
			if (completedAssignments.size()==0) {
				s+= "\n[None]";
			} else for (Assignment a: completedAssignments) {
				s += "\n" + a;
			}
		} else {
			s += "\n[Not stored.]"; //To record completed assignments, set 'storeCompletedAssignments = true'
		}
		return s;
	}
}
