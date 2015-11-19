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
package sim.market.trader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.OrderBook;
import sim.market.OrderBookRecord;
import sim.market.order.Assignment;
import sim.market.order.Commodity;
import sim.market.order.Order;
import sim.market.order.OrderManagementSystem;
import sim.market.order.Trade;

/**
 * BaseTrader class that all other TradingAgents will extend
 *
 */
public abstract class BaseTrader implements Observer, Comparable<BaseTrader>{

	public static Logger logger = Logger.getLogger(BaseTrader.class);
	
	protected static int id_counter = 0;
	
	/** Format to two decimal places */
	protected DecimalFormat df2 = new DecimalFormat("#.##");
	
	/** Format to three decimal places */
	protected DecimalFormat df3 = new DecimalFormat("#.###");
	
	protected Random prng;
	
	protected final int id;
	protected String name = "Agent";
	protected OrderManagementSystem oms;
	protected List<Trade> tradeHistory;
	
	protected int stockBought = 0;
	protected int stockSold = 0;
	protected double balance = 0;
	
	protected boolean executeOrKill = false; // If true only send executeOrKill orders (ie if not executed immediately, then cancel)
	
	public BaseTrader(Random prng) {
		this("BaseTrader", prng);
	}
	
	public BaseTrader(String name, Random prng) {
		this.name = name;
		id = BaseTrader.getNextID();
		tradeHistory = new ArrayList<Trade>();
		oms = new OrderManagementSystem(this);
		this.prng = prng;
	}
	
	protected static int getNextID() {
		BaseTrader.id_counter++;
		return BaseTrader.id_counter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public List<Order> getOrders() {
		return oms.getOrders();
	}


	/**
	 * Stock balance = #bought-#sold 
	 * @return balance
	 */
	public int getStockOwned() {
		return stockBought-stockSold;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	/**
	 * is this trader equal to the other trader?
	 * @param other
	 * @return true if this trader has the same ID as the other trader, false otherwise
	 */
	public boolean isEqual(BaseTrader other) {
		if(this.id==other.id) return true;
		else return false;
	}
	
	/**
	 * Add assignment to assignments list
	 * @param a - new assignment
	 * @return true if assignment added, false otherwise
	 */
	public boolean addAssignment(Assignment a) {
		
		// check, is this assignment for me?
		if(!a.getOwner().isEqual(this)) {
			logger.debug("Assignment is not for this trader: Trader " + this.id + ". Ignoring: " + a);
			return false;
		}
		
		// check, is assignment volume greater than zero?
		if(a.getVolume()<=0) {
			logger.debug("Assignment volume [" +a.getVolume()+"] is illegal: Trader " + this.id + ". Ignoring: " + a);
			return false;
		}		
		
		//check, do we already have this assignment?
		if(oms.contains(a)) {
			logger.debug("Trader " + id + " already owns assignment. Ignoring: " + a);
			return false;
		} else {
			oms.add(a);
			logger.info("Trader #" + id + ", Assignment received: " + a);
			//generateOrder(a); //Immediately generate an order when assignment is received
			return true;
		}
	}
	
	/**
	 * Cancel trader's assignment
	 * @param a - the assignment to cancel
	 * @return true if assignment cancelled, false otherwise
	 */
	public boolean cancelAssignment(Assignment a) {
		
		logger.debug("Cancelling " + a);
		// check, is this assignment for me?
		if(!a.getOwner().isEqual(this)) {
			logger.debug("Assignment is not for this trader: Trader " + this.id + ". Ignoring: " + a);
			return false;
		}
		
		//check, do we own this assignment?
		if(!oms.contains(a)) {
			logger.debug("Trader " + id + " does not own this assignment. Ignoring: " + a);
			return false;
		} else {
			oms.cancel(a);
			return true;
		}
	}
	
	/**
	 * Cancel all orders in the orderbook
	 * 
	 * @return true if at least one order is cancelled, false otherwise
	 */
	public boolean cancelOrders() {
		
		boolean success = true;
		
		logger.info("Cancelling all orders for Trader #" + id);
		for(Assignment a: oms.getAssignments()) {
			if(!a.cancelOrders()) success = false;
		}
		
		return success;
	}
	
	/**
	 * Get total volume of buy assignments for a commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public int getDemandVolume(Commodity c) {
		return oms.getDemandVolume(c);
	}

	/**
	 * Get total volume of sell assignments for a commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public int getSupplyVolume(Commodity c) {
		return oms.getSupplyVolume(c);
	}
	
	/**
	 * Get list of all assignments
	 * @return assignments
	 */
	public List<Assignment> getAssignments() {
		return oms.getAssignments();
	}
	
	/**
	 * Get list of all buy assignments for a commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public List<Assignment> getDemandAssignments(Commodity c) {
		return oms.getDemandAssignments(c);
	}
	
	/**
	 * Get list of all sell assignments for a commodity
	 * @param c - the commodity
	 * @return assignments
	 */
	public List<Assignment> getSupplyAssignments(Commodity c) {
		return oms.getSupplyAssignments(c);
	}
	
	/**
	 * Remove assignment to assignments list
	 * @param a - assignment to remove
	 * @return true if assignment removed, false otherwise
	 */
	public boolean removeAssignment(Assignment a) {
		
		//check, does this assignment belong to me?
		if(!a.getOwner().isEqual(this)) {
			logger.debug("Assignment does not belong to this trader: " + this + ". Ignoring: " + a);
			return false;
		}

		return oms.cancel(a);
	}	
	
	/**
	 * Generate a new order
	 * @param timestamp - the current time
	 * @param c - the commodity to buy/sell
	 * @param prng - the pseudo-random number generator
	 * @return the new Order
	 */
	public abstract Order generateOrder(long timestamp, Commodity c, Random prng);
	
	/**
	 * Generate a new order for a particular assignment
	 * @param a - the assignment
	 * @return the new Order
	 */
	public abstract Order generateOrder(Assignment a);
	
	/**
	 * The agent has executed a trade
	 * @param t
	 */
	public void executedOrder(Order o, Trade t) {
		logger.debug("Trader " + this.id + " has traded: " + t);
		
		if(oms.orderExecuted(o)) {
			
			tradeHistory.add(t);
			if(o.isBid()) {
				// bought: increase stock and decrease balance
				stockBought += t.getVolume(); //TODO - we need a dictionary of commodity ownerships (put in OMS)
				balance -= t.getVolume()*t.getPrice();
			} else {
				// sold: decrease stock and increase balance
				stockSold += t.getVolume();
				oms.incrementMonthlySales(t.getVolume());
				balance += t.getVolume()*t.getPrice();
			}
			logger.debug("Accounts updated. Stock="+getStockOwned()+", balance=$"+balance);
		} else {
			logger.warn("WARNING: Order doesnt exist as far as agent is aware! Ignoring trade: " + t);
		}
		
		// We should also do any necessary clearing (i.e., transfer ownership of the underlying commodity
		performClearing(o,t);
		
		logger.debug(this);
	}

	public void orderSplit(Order newOrder, Order oldOrder) {
		oms.orderSplit(newOrder, oldOrder);
	}
	
	/**
	 * Notify trader that order has been deleted and remove order from trader's order list
	 * @param order - the order to delete
	 * @return true if successful deletion, false otherwise
	 */
	public boolean notifyOrderDeletion(Order order) {
		return oms.deleteOrder(order);
	}
	
	/**
	 * Match demand (assignments to buy) against available instances owned
	 * @param c - the instance (commodity) type
	 * @return the number of instances matched to demand
	 */

	/**
	 * Internalize trades (i.e. trade with itself). 
	 * If the trader has an assignment to buy AND sell a commodity, then it can match the quantity internally.
	 * 
	 * Return the number of units matched internally
	 * 
	 * NOTE: In the BaseTrader this method has no logic.  Override to add logic.
	 * 
	 * @return 0
	 */
	public int internalizeTrades(Commodity c) {
		return 0;
	}
	
	/**
	 * Check whether the trader has excess capacity for a commodity (i.e., a commodity that is no longer required).  
	 * 
	 * If the trader has excess volume, then create a new assignment to SELL...
	 * 
	 * NOTE: Base Trader does not have this functionality. Always returns false. Other classes must extend this method.
	 * 
	 * @return true if new sell assignment is created, false otherwise
	 */
	public boolean assignExcessVolumeForSale(Commodity c) {

		logger.info("Trader #" + this.id + " attempting to assign excess volume for sale...");		
		logger.warn("This trader type [" + this.name + "] does not have functionality for this. Returning false...");
		return false;
	}
	
	/**
	 * Push the trader to attempt to trade any assignments they have
	 * 
	 * @param retailMarket - if true, buyers do not enter orders into book.
	 */
	public void trade(boolean retailMarket) {
		logger.debug("Trader attempting to trade...");
		if(oms.getAssignments().size()>0) {
			Assignment a = oms.getAssignments().get(0);
			logger.debug("Attempting to trade first assignment in list: " + a);
			if(a.isBuy() && retailMarket) {
				logger.info("Assignment is a buy [limit=" + a.getLimitPrice() +"], Retail Market => keeping quiet, not generating order...");
			} else {
				generateOrder(a);
				
				if(executeOrKill) {
					logger.debug("Trader set to 'executeOrKill', so cancelling orders that have not executed.");
					a.cancelOrders();
				}
			}
		} else {
			logger.debug("Trader has no assignments to trade.");
		}
	}
	
	@Override
	public String toString() {
		return "\nTrader [#" + id + ", " + name + ", bought=" + stockBought + ", sold=" + stockSold + ", stock="
				+ getStockOwned() + ", balance=$" + balance + "] " + oms +"\n";
	}

	public String toStringLong() {
		return toString() + "TradeHistory: " + tradeHistory;
	}
	
	//The OrderBook has updated. Called from the OrderBook using Observer:Observable interface. 
	@Override
	public void update(Observable orderBook, Object orderBookRecord) {
		
		logger.debug("#" + this.id + " Received: " + orderBookRecord.getClass().getName() + " from: " + orderBook.getClass().getName());
	
		if(OrderBook.isOrderBook(orderBook) && OrderBookRecord.isOrderBookRecord(orderBookRecord)) {
			
			logger.debug("Now the trader will update based on latest OB record: " + orderBookRecord);
			orderBookUpdated((OrderBookRecord) orderBookRecord);
			
		} else {
			logger.warn("Unknown objects: " + orderBook + ", " + orderBookRecord);
		}
	}
	
	/**
	 * The order book has been updated. Update internal settings on the basis of this change, but do not act.
	 * @param orderBookRecord - the latest record of change to the orderbook
	 */
	public abstract void orderBookUpdated(OrderBookRecord orderBookRecord);
	
	/**
	 * If there is an acceptable price on the book, then execute against it.
	 * 
	 * @return true if trader decides to execute, false otherwise
	 */
	public abstract boolean execute();
	
	/**
	 * Push the trader to purchase commodities off exchange (ie from the provider) 
	 * 
	 * In base trader this function does nothing. It just returns false.
	 * 
	 * @param c - the commodity to purchase
	 * @return true if purchases made, false otherwise
	 */
	public boolean purchaseCommodities(Commodity c) {
		logger.info("Trader #" + id + " has demand = " + oms.getDemandVolume(c));
		logger.warn("BaseTrader cannot purchase commodities. This method should be Overriden by subclass. Returning false");
		return false;
	}
	
	
	/**
	 * Perform any accounting updates or other clean-up operations necessary at the end of a time step.
	 * 
	 * This method is blank in the BaseTrader. Override to add functionality
	 */
	public abstract void endTimeStep();
	
	/**
	 * Perform post execution clearing, such as ownership transfer of underlying assets 
	 * 
	 * @param o - the trader's order that executed
	 * @param t - the trade execution
	 * @return true if clearing (ownership transferal) successful, false otherwise
	 */
	public abstract boolean performClearing(Order o, Trade t);
	
	@Override
	public int compareTo(BaseTrader o) {

		//Order traders by id		
		if(this.getId()<o.getId()) return -1;
		else if (this.getId()==o.getId()) return 0;
		else return 1;
	}
	
	/**
	 * Set the trader to only enter 'executeOrKill' orders
	 * 
	 * If set to true, the trader will cancel orders if not executed immediately.
	 * Note: orders can partially execute (this is not 'fillOrKill')
	 * @param executeOrKill
	 */
	public void setExecuteOrKill(boolean executeOrKill) {
		logger.debug("Setting trader #" + this.id + " to 'executeOrKill'="+executeOrKill);
		this.executeOrKill = executeOrKill;
	}
	
	/**
	 * Does the trader only enter 'executeOrKill' orders
	 * @return true / false
	 */
	public boolean isExecuteOrKillMode() {
		return executeOrKill;
	}
}
