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
package sim.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.order.Order;
import sim.market.order.PriceVolumeTuple;
import sim.market.order.Trade;
import sim.market.trader.pricer.Shout;
import utility.ShuffledObservable;

/**
 * An public order book, containing a list of bids (offers to buy) and asks (offers to sell).
 * 
 * The order book sorts bids and asks by value and then by time (oldest has priority).
 * 
 * Orders execute when bid and ask prices cross.
 *
 */
public class OrderBook extends ShuffledObservable{

	public static Logger logger = Logger.getLogger(OrderBook.class);

	public static List<OrderBook> books;
	
	protected static int id_counter = 0;
	
	protected final int id;
	protected final Commodity commodity;
	protected final Random prng;
	
	List<Order> bids = new ArrayList<Order>();
	List<Order> asks = new ArrayList<Order>();
	List<Trade> tradeHistory = new ArrayList<Trade>();
	List<OrderBookRecord> bookHistory = new ArrayList<OrderBookRecord>();
	
	protected static Trade lastTrade;
	
	public static double getLastTradePrice(Commodity c) {
		return lastTrade.getPrice();
	}
	
	public OrderBook(Random prng, Commodity c) {
		super(prng,  true); //shuffle order of observers before notifying of update
		this.prng = prng;
		commodity = c;
		id = getNextID();
		if(books==null) books = new ArrayList<OrderBook>();
		books.add(this);
	}
	
	protected static int getNextID() {
		return ++OrderBook.id_counter;
	}
	
	public static OrderBook getBook(Commodity c) {
		
		for(OrderBook ob: books) {
			if(ob.getCommodity()==c) return ob;
		}
		logger.warn("No order book exists for commodity: " + c + ". Returning null");
		return null;
	}
	
	protected Commodity getCommodity() {
		return commodity;
	}
	
	/**
	 * Add an order to the orderbook
	 * @param o - the order to add
	 * @return true if the order executes, false otherwise
	 */
	public boolean addOrder(Order o) {

		logger.debug(this + "\nAdding order to the book: " + o);
		if(!this.orderExecutes(o)) {
			if(o.isBid()) {
				bids.add(o);
				Collections.sort(bids);
			} else {
				asks.add(o);
				Collections.sort(asks);
			}
			bookHistory.add(new OrderBookRecord(o,false));
			logger.debug("Updating latest record to: " + getLatestRecord());
			
			logger.info(new Shout(getLatestRecord()));
			
			// Notify all observing Traders about the update
			notifyTradersOfUpdate();
			
			logger.debug(this);
			return false;
		} else {
			logger.debug("Order executes...");
			// order executes in the book.
			// generate a trade
			performExecutions(o);
			
			logger.debug(this);
			return true;
		}
	}
	
	/**
	 * Cancel an order on the order book
	 * @param o - the order
	 * @return true if successful, false otherwise
	 */
	public boolean cancelOrder(Order o) {

		logger.debug(this +"Cancelling order from the book: " + o);
		
		if(remove(o)) {
			o.delete();
			logger.debug("Order cancellation successful");
		} else {
			logger.warn("Order could not be cancelled: " + o);
		}
		
		logger.debug(this.toString());
		
		return true;
	}
	
//	/**
//	 * Delete order on book and tag order as deleted (notify owner)
//	 * @param o - the order
//	 * @return true if successful, false otherwise
//	 */
//	private boolean deleteOrder(Order o) {
//
//		logger.debug("Deleting order from the book: " + o);
//		
//		if(o.isBid()) {
//			if(bids.contains(o)) {
//				bids.remove(o);
//				o.delete();
//				logger.debug("Order deleted from book");
//			} else {
//				logger.warn("Could not find order in OB bids: " + o);
//			}
//		} else {
//			if(asks.contains(o)) {
//				asks.remove(o);
//				o.delete();
//				logger.debug("Order deleted from book");
//			} else {
//				logger.warn("Could not find order in OB asks: " + o);
//			}			
//		}
//		
//		logger.debug(this.toString());
//		
//		return true;
//	}

	/**
	 * Get the best ask price in the orderbook
	 * @return best ask price if exists, else Double.MAX_VALUE
	 */
	public double getBestAskPrice() {
		if (asks.size() > 0) {
			Collections.sort(asks); //TODO: expensive to do this each time
			return asks.get(0).getPrice();
		} else {
			logger.debug("Asks list empty");
			return Double.MAX_VALUE;
		}
	}

	/**
	 * Get the best bid price in the orderbook
	 * @return best bid price if exists, else 0
	 */
	public double getBestBidPrice() {
		if (bids.size() > 0) {
			Collections.sort(bids); //TODO: expensive to do this each time
			return bids.get(0).getPrice();
		} else {
			logger.debug("Bids list empty");
			return 0;
		}
	}

	/**
	 * Get the volume at touch
	 * @param d - the side of the book we are looking at
	 * @return volume at touch on side d
	 */
	public int getVolumeAtTouch(Direction d) {
		
		if (d==Direction.BUY) {
			if (bids.size()>0) {
				return bids.get(0).getVolume();
			}
		} else {
			if (asks.size()>0) {
				return asks.get(0).getVolume();
			}
		}
		return 0;
	}

	/**
	 * Get the volume available volume at touch to execute against order o
	 * @param o - the order
	 * @return the volume available at touch
	 */
	public int getAvailableVolumeAtTouch(Order order) {
				
		if(order.isBid()) {
			for(Order ask: asks) {
				if(order.getPrice()>=ask.getPrice()) {
					return ask.getVolume();
				} else {
					logger.debug("Bid order price: " + order.getPrice() + " is less than ask price: " + ask.getPrice() + "; breaking");
					break;
				}
			}
		} else { //order direction == SELL
			for(Order bid: bids) {
				if(order.getPrice()<=bid.getPrice()) {
					return bid.getVolume();
				} else {
					logger.debug("Ask order price: " + order.getPrice() + " is greater than bid price: " + bid.getPrice() + "; breaking");
					break;
				}
			}
		}
		return 0;		
	}	
	
	/**
	 * Get the volume available on the book to execute against order o
	 * @param o - the order
	 * @return the volume available on the book
	 */
	public int getAvailableVolume(Order order) {
		
		int volume = 0;
		
		if(order.isBid()) {
			for(Order ask: asks) {
				if(order.getPrice()>=ask.getPrice()) {
					volume += ask.getVolume();
				} else {
					//logger.debug("Bid order price: " + order.getPrice() + " is less than ask price: " + ask.getPrice() + "; breaking");
					break;
				}
			}
		} else { //order direction == SELL
			for(Order bid: bids) {
				if(order.getPrice()<=bid.getPrice()) {
					volume += bid.getVolume();
				} else {
					//logger.debug("Ask order price: " + order.getPrice() + " is greater than bid price: " + bid.getPrice() + "; breaking");
					break;
				}
			}
		}
		return volume;		
	}
	
	public String bidsListing() {
		String s = "Bids\n";
		for(Order o: bids) {
			s += o + "\n";
		}	
		return s;
	}

	public String asksListing() {
		String s = "Asks\n";
		for(Order o: asks) {
			s += o + "\n";
		}	
		return s;
	}
	
	@Override
	public String toString() {
		final int DEFAULT_DEPTH = Integer.MAX_VALUE;
		return toString(DEFAULT_DEPTH);
	}
	
	public String toString(int depth) {
		
		//TODO:- Aggregate by volume
		int maxSize=bids.size();
		if(asks.size()>bids.size()) maxSize = asks.size();
		
		String s = "\n============================\n\t   " + this.commodity.getName() + " Orderbook \n";
		s += "Vol\tPrice\t  |\tPrice\tVol \n";
		int i=0;
		while(i<depth && i<maxSize) {
			if(bids.size()>i) {
				s += bids.get(i).getVolume() +"\t$" + bids.get(i).getPrice() + "\t  |\t";
			} else { 
				s += " -\t  -  \t  |\t";
			}
			if(asks.size()>i) {
				s += "$" +asks.get(i).getPrice() + "\t" + asks.get(i).getVolume() +"\t\n";
			} else {
				s += "  -  \t - \t\n";
			}		
			i++;
		}
		s+=" -\t  -  \t  |\t" + "  -  \t -  \t\n";
		s+="\n============================\n";
		return s; 
	}

//	public String toString() {
//		return "OrderBook [id=" + id + ", commodity=" + commodity + ", bids=" + bids
//				+ ", asks=" + asks + ", tradeHistory=" + tradeHistory + "]";
//	}

	/**
	 * Does the order execute in the book?
	 * @param o - the order
	 * @return true if order executes, false otherwise
	 */
	protected boolean orderExecutes(Order o) {
	
		if(o.isBid()) {
			if(asks.size()>0 && o.getPrice()>=getBestAskPrice()) {
				logger.debug("New order executes: " + o);
				logger.debug("Counterparty in book: " + asks.get(0));
				return true;
			}
		} else {
			if(bids.size()>0 && o.getPrice()<=getBestBidPrice()) {
				logger.debug("New order executes: " + o);
				logger.debug("Counterparty in book: " + bids.get(0));
				return true;
			}
		}
		return false;
	}

	protected void performExecutions(Order o) {
		
		logger.debug("performing executions generated by order: " + o);
		
		// eat through the book generating trades for each execution 
		Order residualOrder = o;
		
		int orderVolumeToExecute = residualOrder.getVolume();
		
		while(orderVolumeToExecute > 0) {
			
			//get volume available at top of book.
			int volumeAtTouch = getAvailableVolumeAtTouch(residualOrder);
			
			if (volumeAtTouch<=0) { // no more volume available, add residual to book and break loop
				
				logger.debug("No more volume available, adding residual order to book: " + residualOrder);
				addOrder(residualOrder);
				break;
			}
			
			logger.debug("Volume available at touch = " + volumeAtTouch);
			
			//performing execution
			residualOrder = performExecution(residualOrder);
			if(residualOrder==null) orderVolumeToExecute=0;
			else orderVolumeToExecute = residualOrder.getVolume();
			logger.debug("Order volume left to execute = " + orderVolumeToExecute);
		}
		
		logger.debug("Finished executing order");
		return;
	}

	/**
	 * Perform an execution on the order book using order o
	 * 
	 * Remove executed orders from the book.
	 * Update traders of execution.
	 * Add trade to trade history
	 * Return residual order (volume of o remaining after execution) 
	 * 
	 * @param o - order to execute
	 * @return - residual order (remaining from o after execution): null if no residual
	 */
	protected Order performExecution(Order o) {
		
		Order counterparty;
		Order residualOrder = null; //The residual of o remaining after execution
		Trade t=null;
		
		if(orderExecutes(o)) {
			if(o.isBid()) counterparty = asks.get(0); 
			else counterparty = bids.get(0);
				
			logger.debug("Found counterparty for order. Order = " + o + "; counterparty = " + counterparty);
	
			//If volumes match, then we have no problem, just make trade
			if(o.getVolume()==counterparty.getVolume()) {
	
				logger.debug("Order volume = " + o.getVolume() + " matches counterparty volume = " + counterparty.getVolume() +
						". Performing single trade execution.");
				remove(counterparty);
				t = new Trade(counterparty.getPrice(),counterparty.getVolume(),o.getTimestamp(),o,counterparty);
	
			} else if(o.getVolume()<counterparty.getVolume()) { //execute order, and update book volume
	
				logger.debug("Order volume = " + o.getVolume() + " is less than counterparty volume = " + counterparty.getVolume() +
						". Splitting counterparty order and performing single trade execution");
				Order executedCounterpartyOrder = counterparty.splitOrder(o.getVolume());
				t = new Trade(executedCounterpartyOrder.getPrice(),o.getVolume(),o.getTimestamp(),o,executedCounterpartyOrder);
	
			} else { // execute as much volume of order as we can, then continue "eating" through book.
	
				logger.debug("Order volume = " + o.getVolume() + " is greater than counterparty volume = " + counterparty.getVolume() +
						". Executing what we can, then will continue to eat through book.");	
				logger.debug("Splitting order");
				residualOrder = o.splitOrder(o.getVolume()-counterparty.getVolume());
				remove(counterparty);
				t = new Trade(counterparty.getPrice(),counterparty.getVolume(),o.getTimestamp(),o,counterparty);
				
			}
	
		} else {
			logger.warn("Warning, order doesn't execute. No trade generated");
			t = null;
		}
		   
		// Perform accounting
		if(t!=null) {
			bookHistory.add(new OrderBookRecord(t.getVolume(), t.getPrice(), o.getDirection(), true, o.getCommodity(), o.getTimestamp()));
			logger.debug("Updating latest record to: " + getLatestRecord());
		    tradeHistory.add(t); // add trade to history 
		    lastTrade = t;
		    t.updateOwners();    // update owners of the trade
		    
			// Notify all observing Traders about the latest record
		    notifyTradersOfUpdate();
		}
		
		return residualOrder; //the residual order volume remaining after execution [may be null]
	}
	
	/**
	 * Remove an order from the book. 
	 * @param o
	 * @return
	 */
	private boolean remove(Order o) {
		
		if(o.isBid()) {
			if(bids.contains(o)) {
				logger.debug("removing order from OB bids: " + o);
				bids.remove(o);
				return true;
			} else {
				logger.debug("Cannot remove order from OB bids, doesn't exist in book: " + o);
				return false;
			}
		} else {
			if(asks.contains(o)) {
				logger.debug("removing order from OB asks: " + o);
				asks.remove(o);
				return true;
			} else {
				logger.debug("Cannot remove order from OB asks, doesn't exist in book: " + o);
				return false;
			}			
		}
	}
	
	public Order getBestBid() {
		if(bids!=null && bids.size()>0) {
			Collections.sort(bids);
			return bids.get(0);
		} else {
			logger.warn("No bids in book. Returning null");
			return null;
		}
	}
	
	public boolean isEmptyBids() {
		if(bids!=null && bids.size()>0) return false;
		else return true;
	}
	
	
	public Order getBestAsk() {
		if(asks!=null && asks.size()>0) {
			Collections.sort(asks);
			return asks.get(0);
		} else {
			logger.warn("No asks in book. Returning null");
			return null;
		}
	}
	
	public boolean isEmptyAsks() {
		if(asks!=null && asks.size()>0) return false;
		else return true;
	}
	
	public List<Trade> getTradeHistory() {
		return tradeHistory;
	}
	
	public List<PriceVolumeTuple> getTradePriceHistory() {
		
		List<PriceVolumeTuple> pvt= new ArrayList<PriceVolumeTuple>();
		
		for(Trade t: tradeHistory) {
			pvt.add(new PriceVolumeTuple(t.getPrice(),t.getVolume()));
		}
		
		return pvt;
	}
	
	/**
	 * Get the latest OrderBookRecord (the latest order in the book and whether it executed)
	 * @return latest record, or "null" if none exist
	 */
	public OrderBookRecord getLatestRecord() {
		int records = bookHistory.size();
		if(records > 0) return bookHistory.get(records-1);
		else return null;
	}

	/**
	 * Is the Object an OrderBook?
	 * @param arg - the Object to check
	 * @return - true if arg is an OrderBook, false otherwise
	 */
	public static boolean isOrderBook(Object arg) {
		return arg.getClass().getName().equals(OrderBook.class.getName());
	}
	
	/**
	 * Notify all observers that there has been a new shout or trade on the order book.
	 * Note: observers are always updated in the same order. We cannot randomise here, 
	 * so the traders need to be randomised before they act.
	 */
	public void notifyTradersOfUpdate() {
		
		setChanged();
		notifyObservers(getLatestRecord());
	}
}
