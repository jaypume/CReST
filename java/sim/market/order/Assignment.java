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
 * Assignments for traders to complete.
 * 
 * Contains an owner, a limit price, a direction (buy/sell), a volume, a commodity, and a deadline
 *
 */
public class Assignment implements Comparable<Assignment> {

	public static Logger logger = Logger.getLogger(Assignment.class);
	
	protected static int id_counter = 0;
	
	protected int id;
	protected double limitPrice;
	protected int volume;
	protected int volumeExecuted = 0;
	protected boolean complete = false; //has the assignment completed?
	protected Direction direction;
	protected Commodity commodity;
	protected long timestamp;
	protected long deadline;
	protected BaseTrader owner;
	
	protected List<Order> orders;
	
	public Assignment(BaseTrader owner, double limitPrice, int volume, Direction direction, 
			Commodity commodity, long timestamp, long deadline){
		
		id = Assignment.getNextID();
		this.limitPrice = limitPrice;
		this.volume = volume;
		this.direction = direction;
		this.commodity = commodity;
		this.timestamp = timestamp;
		this.deadline = deadline;
		this.owner = owner;
		this.orders = new ArrayList<Order>();
	};
	
	protected static int getNextID() {
		Assignment.id_counter++;
		return Assignment.id_counter;
	}

	public static int getId_counter() {
		return id_counter;
	}

	public int getId() {
		return id;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	/**
	 * Get the original volume of an assignment
	 * @return
	 */
	public int getOriginalVolume() {
		return volume; 
	}
	
	/**
	 * Get the volume remaining in an assignment
	 * @return volume remaining
	 */
	public int getVolume() {
		return volume-volumeExecuted;
	}

	public Direction getDirection() {
		return direction;
	}
	
	public boolean isBuy() {
		return direction==Direction.BUY;
	}
	
	public boolean isSell() {
		return direction==Direction.SELL;
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getDeadline() {
		return deadline;
	}

	public BaseTrader getOwner() {
		return owner;
	}

	/**
	 * Add new order to assignment
	 * @param o - the order to add
	 */
	public void newOrder(Order o) {
		orders.add(o);
	}
	
	/**
	 * Get all orders associated with assignment
	 * @return orders
	 */
	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	/**
	 * Cancel all orders associated with assignment
	 * @return true if successful, false otherwise
	 */
	public boolean cancelOrders() {
		
		logger.info("Cancelling all orders for Assignment #" + id);
		logger.info("Orders outstanding for assignment: " + orders);
		
		boolean success = true;
		//Cancel all associated orders on the Orderbook
		ArrayList<Order> ordersToCancel = new ArrayList<Order>(); // copy list so we don't comodify...
		ordersToCancel.addAll(getOrders());			
		for(Order o: ordersToCancel) {
			logger.info("Cancelling order from book: " + o);
			if(!OrderBook.getBook(o.getCommodity()).cancelOrder(o)) success = false;
		}
		return success;
	}
	
	/**
	 * Set assignment completed 
	 * 
	 * SideEffect: Cancels all associated orders on the OrderBook.
	 */
	public void setCompleted() {
		if(complete) logger.warn("Assignment already complete. Cannot set completed(): " + this);
		//Cancel all associated orders on the Orderbook
		cancelOrders();
		complete = true; // set assignment completed
	}
	
	/**
	 * Delete order
	 * @param o - the order to delete
	 * @return true if successful deletion, false otherwise
	 */
	public boolean deleteOrder(Order o) {
		
		boolean successful = false;
		
		if(orders.contains(o)) {
			orders.remove(o);
			logger.debug("Order deleted from assignment's orders. Order: " + o + "; " + this);
			successful = true;
		} else {
			logger.warn("Order not part of assignment. Cannot delete. Order: " + o + "; " + this);
			successful=false;
		}
		return successful;
	}
	
	public boolean orderExecutes(Order o) {
		
		if(orders.contains(o)) {
			orders.remove(o);
			volumeExecuted += o.getVolume();
			if(volumeExecuted==volume) {
				logger.debug("Assignment now complete");
				complete = true;
			}
			return true;
		} else {
			logger.warn("WARNING: Order doesnt exist in this assignment!");
			return false;
		}
	}
	
	/**
	 * Does this assignment contain an Order o?
	 * @param o - the order
	 * @return true/false
	 */
	public boolean contains(Order o) {
		return orders.contains(o);
	}
	
	@Override
	public String toString() {
		
		String comp = (complete)?"complete":"incomplete";
		String dir = (direction==Direction.BUY)?"BUY":"SELL";
		return "Assignment [#" + id + ", owner=" + owner.getId() + ", " + comp + ", " + dir
				+ ", $" + limitPrice + ", volume=" + volume + ", executed=" + volumeExecuted + ", commodity=" + commodity.getName() + ", time="
				+ timestamp + ", deadline=" + deadline + ", orders=" + orders + "]";
	}

	
	@Override
	/**
	 * Order assignments by LimitPrice.
	 * 
	 * If Assignments are both BUY, then the highest limit price comes first
	 * If Assignments are both SELL, then the lowest limit price comes first
	 */
	public int compareTo(Assignment a) {
		
		//We want the *best* assignment to come first.
		//
		//Therefore:
		//
		//Return -ve if A>B i.e, "A is better than B", +ve if A<B
		//For Buys, A>B if Price_A > Price_B => return -ve
		//For Sells, A>B if Price_A < Price_B => return -ve
		//If Price_A==Price_B, return 0
		
		
		if(this.getDirection().opposite(a.getDirection())) {
			logger.warn("WARNING: Comparing Assignments with opposite directions. Returning 0");
			return 0;
		} else {
			
			if(this.getDirection()==Direction.BUY) {
				if(this.getLimitPrice()>a.getLimitPrice()) {
					return -1;
				} else if (this.getLimitPrice()<a.getLimitPrice()) {
					return 1;
				} else { //this.price == a.price
					return 0;
				}
			} else { // Direction==SELL
				if(this.getLimitPrice()<a.getLimitPrice()) {
					return -1;
				} else if (this.getLimitPrice()>a.getLimitPrice()) {
					return 1;
				} else { //this.price == a.price
					return 0;
				}					
			}
		}
	}
	
	
}
