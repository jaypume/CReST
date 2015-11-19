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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.OrderBook;
import sim.market.OrderBookRecord;
import sim.market.order.Assignment;
import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.order.Order;
import sim.market.order.Trade;
import sim.market.trader.pricer.Shout;
import sim.market.trader.pricer.ZipBuyPricer;
import sim.market.trader.pricer.ZipSellPricer;

/**
 * Zero intelligence plus (ZIP) trader: http://www.hpl.hp.com/techreports/97/HPL-97-91.pdf
 * 
 * TODO: Need to raise profit margins when INACTIVE. Currently not doing this. (Cliff & Bruten 1997, p. 42)
 *
 */
public class ZipTrader extends BaseTrader {
	
	public static Logger logger = Logger.getLogger(ZipTrader.class);

	protected final double LEARNING_RATE_LOWER_BOUND = 0.1;
	protected final double LEARNING_RATE_UPPER_BOUND = 0.5;
	
	protected final double PROFIT_MARGIN_LOW_BOUND_PERCENT = 0.05;   //cliff = 0.05 : JPC = 0.8
	protected final double PROFIT_MARGIN_HIGH_BOUND_PERCENT = 0.35;  //cliff = 0.35 : JPC = 1.0

	protected double profit_margin_buy = 0.0; //  \mu ~ U(-0.35,-0.05) for buyers
	protected double profit_margin_sell = 0.0; // \mu ~ U(0.05,0.35) for sellers; 
	protected double learning_rate = 0.0; // \Beta ~ U(0.1,0.5) - initialised at time t=0
	protected double target_price = 0.0; //  \tau
	protected double momentum_coefficient = 0.0; // \gamma ~ U(0.2,0.8) initialised at time t=0
	
	protected double limitPrice = 0.0;
	
	protected Assignment defaultBuyAss; // dummy assignments for listening to the orderbook
	protected Assignment defaultSellAss;
	
	protected ZipBuyPricer buyPricer;
	protected ZipSellPricer sellPricer;	

	protected int internalSellLimitPrice = 1; // internal limit price used for re-selling RIs 
	protected int internalBuyLimitPrice = 1; // internal limit price used lsiteining to the book 
	
	/**
	 * Construct a ZipTrader
	 * @param prng - Pseudo-random number generator
	 * @param buyProfitLowerBound - lower bound on (uniformly distributed) profit margin for buy pricer
	 * @param buyProfitUpperBound - upper bound on (uniformly distributed) profit margin for buy pricer
	 * @param sellProfitLowerBound - lower bound on (uniformly distributed) profit margin for sell pricer
	 * @param sellProfitUpperBound - upper bound on (uniformly distributed) profit margin for sell pricer
	 */
	public ZipTrader(Random prng, 
			double buyProfitLowerBound, 
			double buyProfitUpperBound, 
			double sellProfitLowerBound,
			double sellProfitUpperBound) {
		this("ZIP", prng, buyProfitLowerBound, buyProfitUpperBound, sellProfitLowerBound, sellProfitUpperBound);		
	}
	
	/**
	 * Construct a ZipTrader
	 * @param name - the name of the trader
	 * @param prng - Pseudo-random number generator
	 * @param buyProfitLowerBound - lower bound on (uniformly distributed) profit margin for buy pricer
	 * @param buyProfitUpperBound - upper bound on (uniformly distributed) profit margin for buy pricer
	 * @param sellProfitLowerBound - lower bound on (uniformly distributed) profit margin for sell pricer
	 * @param sellProfitUpperBound - upper bound on (uniformly distributed) profit margin for sell pricer
	 */	
	public ZipTrader(String name, 
			Random prng,
			double buyProfitLowerBound, 
			double buyProfitUpperBound, 
			double sellProfitLowerBound,
			double sellProfitUpperBound) {	
			
		super(name, prng);
		
		 // From Cliff & Bruten 1997
		 // \Beta_i~U(0.1,0.5), initialised with the trader at time t=0
		 // Initial values of \mu_i~U(0.05,0.35) for sellers and ~U(-0.35,-0.05) for buyers: thus all traders begin with profit margins between 5 and 35 percent
		
		learning_rate = prng.nextDouble()*(LEARNING_RATE_UPPER_BOUND-LEARNING_RATE_LOWER_BOUND) + LEARNING_RATE_LOWER_BOUND;

		profit_margin_buy = -prng.nextDouble()*(buyProfitUpperBound-buyProfitLowerBound)+buyProfitLowerBound;
		profit_margin_sell = prng.nextDouble()*(sellProfitUpperBound-sellProfitLowerBound)+sellProfitLowerBound;
		
		//Initial values for gamma set U~(0.2,0.8) [Cliff&Bruten 1997, p45]
		momentum_coefficient = (prng.nextDouble()*0.6)+0.2;
		
		buyPricer = new ZipBuyPricer(prng, profit_margin_buy, learning_rate, momentum_coefficient);
		sellPricer = new ZipSellPricer(prng, profit_margin_sell, learning_rate, momentum_coefficient);
	}
	
	//TODO
	public void setDefaultBuy(Commodity c, double limit) {
		defaultBuyAss=new Assignment(this, limit, 1, Direction.BUY, c, 0, 0);
		defaultBuyAss.setCompleted();
	}
	
	public void setDefaultSell(Commodity c) {
		defaultSellAss=new Assignment(this, internalSellLimitPrice, 1, Direction.SELL, c, 0, 0);
		defaultSellAss.setCompleted();
	}
	
	/**
	 * ZIP Trader pseudo code: from Cliff & Bruten (1997) 
	 * "Minimal-Intelligence Agents for Bargaining Behaviors in Market-Based Environments", HPL-91-91, Tech Report
	 * 
	 * For Sellers:
	 *   - if (the last shout was accepted at price q)   ## NOTE: For an OrderBook, a sell order that executes is equivalent (for ZIP) to a bid shout that is accepted.
	 *   - then
	 *      1. any seller s_i for which p_i<q should raise its profit margin
	 *      2. if (the last shout was a bid)
	 *         then
	 *         1. any active seller s_i for which p_i >= q should lower its margin
	 *   - else
	 *      1. if (the last shout was an offer)
	 *      then
	 *         1. any active seller s_i for which p_i >= q should lower its margin
	 *         
	 * For Buyers:
	 *    - if (the last shout was accepted at price q)
	 *    - then
	 *       1. any buyer b_i for which p_i>=q should raise its profit margin
	 *       2. if (the last shout was an offer)
	 *          then
	 *          1. any active buyer b_i for which p_i<=q should lower its margin
	 *    - else
	 *       1. if (the last shout was a bid)
	 *          then
	 *          1. any active buyer b_i for which p_i<=1 should lower its margin 
	 * 
	 * see http://www.hpl.hp.com/techreports/97/HPL-97-91.pdf
	 * 
	 * Adaptation:
	 * Shout price p_i(t) for unit j with limit lambda_{i,j}, using profit-margin /mu_i(t):
	 *    p_i(t) = lambda_{i,j}(1+/mu_i(t))
	 * For sellers, /mu_i(t)\in[0,\infty)
	 * For sellers, /mu_i(t)\in[-1,0]
	 * 
	 * Update rule for profit margin, \mu_i:
	 *    \mu_i(t+1) = (p_i(t) + \Delta_i(t)) / \lambda_{i,j} - 1
	 * where \Delta_i(t) is the Widrow-Hoff delta value, calculated using the individual trader's 
	 * learning rate \Beta_i:
	 *    \Delta_i(t) = \Beta_i( \tau_i(t) = p_i(t))
	 * and target price \tau_i(t):
	 *    \tau_i(t) = \R_i(t)q(t) + \A_i(t)
	 * where \R_i is a random var that sets target price relative to the price q(t) of the last shout,
	 * and \A_i(t) is a (small) random absolute price alteration.
	 * When the intention is to increase the shout price: \R_i>1.0 and \A_i > 0.0;
	 * When the intention is to decrease the shout price: 0.0 < \R_i < 1.0 and \A_i < 0.0
	 * 
	 * Every time the profit margin is altered, the target price is calculated using newly-generated random values of \R_i and \A_i.
	 * 
	 * Update rule used for ZIP traders:
	 *    \mu_i(t+1) = (p_i(t) + \Gamma_i(t)) / \lambda_{i,j} - 1
	 * 
	 * where \Gamma_i(t+1) = \gamma_i\Gamma_i(t) + (1-\gamma_i)\Delta_i(t)
	 * 
	 * \R_i~U(1.0,1.05) for price increases, and \R_i~U(0.95,1.0) for price decreases
	 * \A_i~U(0.0,0.05) for price increases, and \A_i~U(-0.05,0.0) for decreases
	 * 
	 * \Beta_i~U(0.1,0.5), initialised with the trader at time t=0
	 * Initial values of \mu_i~U(0.05,0.35) for sellers and ~U(-0.35,-0.05) for buyers: thus all traders begin with profit margins between 5 and 35 percent
	 */
	@Override
	public Order generateOrder(long timestamp, Commodity c, Random prng) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Order generateOrder(Assignment a) {
		
		logger.debug("Generating an order for assignment " + a);
		logger.debug("Current orders are: " + a.getOrders());
		
//		logger.debug("Removing current orders for assignment: " + a.getOrders());
		//We first need to cancel all associated orders from the orderbook
//		List<Order>orders = new ArrayList<Order>();
//		orders.addAll(a.getOrders());	
//		for(Order o: orders) {
//			OrderBook.getBook(o.getCommodity()).cancelOrder(o);
//		}
		
		// what is the price we want to quote?
		// do we want to buy, or sell? Use a different pricer under each circumstance
		int price;
		int margin_movement=0;
		if(a.isBuy()) {
			if(OrderBook.getBook(a.getCommodity()).isEmptyBids()) {
				logger.debug("Order book contains no bids. Not updating buy pricer...");
			} else {
				margin_movement = buyPricer.updatePrice(a, new Shout(OrderBook.getBook(a.getCommodity()).getBestBid(), false));
			}
			price = (int) buyPricer.getPrice(a);
		} else {
			if(OrderBook.getBook(a.getCommodity()).isEmptyAsks()) {
				logger.debug("Order book contains no asks. Not updating sell pricer...");
			} else {
				margin_movement = sellPricer.updatePrice(a, new Shout(OrderBook.getBook(a.getCommodity()).getBestAsk(), false));
			}
			price = (int) sellPricer.getPrice(a);
		}
		
		String margin = "unchanged";
		if(margin_movement>0) margin = "increased";
		if(margin_movement<0) margin = "decreased";
		logger.info("Trader #" + this.getId() + " " + a.getDirection() + " Margin " + margin + ", p=" + price);
		
		// is the new order price different to the current order price? 
		// if there is an order with a different price, remove all orders and re-send with new price.
		boolean cancelOrders = false;
		List<Order>orders = new ArrayList<Order>();
		orders.addAll(a.getOrders());	
		for(Order o: orders) {
			if(o.getPrice()!=price) {
				logger.debug("Order has a price different to p=" + price + ". Cancelling orders...");
				cancelOrders = true;
			}
		}
		
		if(cancelOrders) {
			logger.debug("Cancelling orders for current assignment: " + orders);	
			for(Order o: orders) OrderBook.getBook(o.getCommodity()).cancelOrder(o);
		}
		
		if(a.getOrders().size()==0) {
			Order o = new Order(this, a.getTimestamp(), a.getCommodity(), price, a.getVolume(), a.getDirection());
			a.newOrder(o);
			logger.info("Sending order to the book: " + o);
			OrderBook.getBook(a.getCommodity()).addOrder(o);
			logger.debug(OrderBook.getBook(a.getCommodity()));
			return o;
		} else {
			logger.debug("No new order to generate. Keep price at " + price);
			return null;
		}
	}

	@Override
	public void orderBookUpdated(OrderBookRecord orderBookRecord) {
		logger.debug("#" + this.getId() + ": Order book has been updated. Updating profit margins for each assignment...");
		logger.debug("Assignments: " + oms.getAssignments());
		
		
		if(oms.getAssignments().size() > 0) {
			logger.warn("ZipTrader owns assignments...");
		} else if (oms.getCompletedAssignments().size() > 0){
			logger.warn("ZipTrader owns some completed assignments...");
		} else {
			logger.warn("ZipTrader has no assignments...");
		}
		
		// JPC: If the latest record is an execution, then use the record to update profit margin. Else, if it didn't execute, we
		// instead use the latest best bid and offer to update profit margin, since these have more information than shouts lower down the book.

		
		//Just choose the first assignment. 
		//TODO: Actually, we should probably choose the one with the highest profit (i.e., highest limit for buy and lowest limit for sell)
		boolean updatedSell = false;
		boolean updatedBuy = false;
		for(Assignment a: oms.getAllAssignments()) {
			logger.warn("Updating for assignment " + a);
			updateAssignment(orderBookRecord, a);
			if(a.isSell()) updatedSell = true;
			if(a.isBuy()) updatedBuy = true;
			//return;
		}
		if(!updatedSell) {
			logger.warn("#" + this.getId() +": no sell assignment to trade. So updating default sell...");
			if(defaultSellAss!=null) {
				updateAssignment(orderBookRecord, defaultSellAss);
			} else {
				logger.warn("There is no default sell assignment to update. Ignoring...");
			}	
		}
		if(!updatedBuy) {
			logger.warn("#" + this.getId() +": no buy assignment to trade. So updating default buy...");
			if(defaultBuyAss!=null) {
				updateAssignment(orderBookRecord, defaultBuyAss);
			} else {
				logger.warn("There is no default buy assignment to update. Ignoring...");
			}
		}
	}	
	
	protected void updateAssignment(OrderBookRecord orderBookRecord, Assignment a) {
		
		Shout latestShout;

		if(a.getCommodity()==null) logger.error("Assignment is null!");
		
		if(a.getCommodity()==orderBookRecord.getCommodity()) {
			
			if(a.isComplete()) {
				logger.warn("Assignment is completed. Setting trader inactive.");
				buyPricer.setInActive();
				sellPricer.setInActive();			
			}
			
			double limit = a.getLimitPrice();
			double price;
			double oldPrice;
			int margin_movement;
			double pmargin;
			double oldPMargin;
			if(a.isBuy()) {
						
				if(orderBookRecord.isExecuted()) latestShout = new Shout(orderBookRecord); // if execution, generate shout from order book record
				else latestShout = new Shout(OrderBook.getBook(orderBookRecord.getCommodity()).getBestBid(), false); // else, generate from best bid
						
				oldPrice = buyPricer.getPrice(a);
				oldPMargin = buyPricer.getProfitMargin(); //3 d.p
				margin_movement = buyPricer.updatePrice(a,latestShout);
				price = buyPricer.getPrice(a);
				pmargin = buyPricer.getProfitMargin(); //3 d.p
				
				if(!latestShout.isExecuted()) {
					if(OrderBook.getBook(orderBookRecord.getCommodity()).getBestAsk()!=null && OrderBook.getBook(orderBookRecord.getCommodity()).getBestAsk().getPrice() <= price) {
						logger.info("Trader #" + this.getId() + " " + "Best ask has not executed and I want to hit it (" + price + "). ");

						if(buyPricer.isActive()) {
							logger.info("Trader is active, generating order...");
							generateOrder(a); // execute immediately against a price I like...
						} else {
							logger.info("Trader is inactive, not generating order...");
						}
					}
				}
				
			} else {
				if(orderBookRecord.isExecuted()) latestShout = new Shout(orderBookRecord); // if execution, generate shout from order book record
				else latestShout = new Shout(OrderBook.getBook(orderBookRecord.getCommodity()).getBestAsk(), false); // else, generate from best ask
				
				oldPrice =  sellPricer.getPrice(a);
				oldPMargin = sellPricer.getProfitMargin(); //3 d.p
				margin_movement = sellPricer.updatePrice(a,latestShout);
				price = sellPricer.getPrice(a);
				pmargin = sellPricer.getProfitMargin(); //3 d.p.
				
				if(!latestShout.isExecuted()) {
					if(OrderBook.getBook(orderBookRecord.getCommodity()).getBestBid()!=null && OrderBook.getBook(orderBookRecord.getCommodity()).getBestBid().getPrice() > price) {
						logger.info("Trader #" + this.getId() + " " + "Best bid has not executed and I want to hit it (" + price + "). Generating order...");
						
						if(sellPricer.isActive()) {
							logger.info("Trader is active, generating order...");
							generateOrder(a); // execute immediately against a price I like...
						} else {
							logger.info("Trader is inactive, not generating order...");
						}
					}
				}
			}
			String margin = "unchanged";
			if(margin_movement>0) margin = "increased";
			if(margin_movement<0) margin = "decreased";
			logger.info("Trader #" + this.getId() 
					+ " " + a.getDirection() 
					+ " Margin " + margin 
					+ ", old p=" + df3.format(oldPrice)
					+ ", new p=" + df2.format(price) 
					+ ", limit = " + df2.format(limit)
					+ ", prof margin = " + df3.format(pmargin) 
					+ " (old=" + df3.format(oldPMargin) + ")");
			
			
			// now set pricers active again.
			buyPricer.setActive();
			sellPricer.setActive();
		}
	}
	
	
	@Override
	public boolean execute() {
		boolean execute = false;
		
		logger.info("Trader #" + this.getId() + ": Attempting to execute...");
		//go through assignments, if there is an acceptable quote price in the book, execute against it.

		for(Assignment a: oms.getAssignments()) {
			
			OrderBook ob = OrderBook.getBook(a.getCommodity());
			//get my price for the assignment
			if(a.isBuy()) {
				
				double price = buyPricer.getPrice(a);
				double quote = ob.getBestAskPrice();
				if(quote<price) {
					logger.info("Trader #" + this.getId() + ": Best ask quote a=" + quote + " < " + price + ". Generating buy order");
					execute = true; 
					generateOrder(a); //lets send an order to execute this
				} else {
					logger.info("Trader #" + this.getId() + ": No quote to hit...");
				}
			} else {
				double price = sellPricer.getPrice(a);
				double quote = ob.getBestBidPrice();
				if(quote>price) {
					logger.info("Trader #" + this.getId() + ": Best bid quote a=" + quote + " > " + price + ". Generating sell order");
					execute = true; 
					generateOrder(a); //lets send an order to execute this
				} else {
					logger.info("Trader #" + this.getId() + ": No quote to hit...");
				}
			}
		}
		return execute;
	}
	
	@Override
	/**
	 * This function does nothing. Return false.
	 */
	public boolean performClearing(Order o, Trade t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * This function does nothing. 
	 */
	public void endTimeStep() {
		oms.clearCompletedAssignments();
		logger.info("Finished time step. Trader is now: " + this);
	}
}
