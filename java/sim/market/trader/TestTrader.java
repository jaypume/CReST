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

import sim.market.OrderBook;
import sim.market.OrderBookRecord;
import sim.market.order.Assignment;
import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.order.Order;
import sim.market.order.Trade;
import sim.market.trader.pricer.ZicPricer;

public class TestTrader extends BaseTrader{
	
	protected ZicPricer pricer;
	protected final int MAX_PRICE = 50;
	
	public TestTrader(Random prng) {
		this("Agent", prng);
	}
	
	public TestTrader(String name, Random prng) {
		super(name, prng);
		pricer = new ZicPricer(prng, MAX_PRICE);
	}

	@Override
	public Order generateOrder(Assignment a) {
		
		logger.debug("Removing current orders for assignment: " + a.getOrders());
		//We first need to cancel all associated orders from the orderbook
		List<Order>orders = new ArrayList<Order>();
		orders.addAll(a.getOrders());	
		for(Order o: orders) {
			OrderBook.getBook(o.getCommodity()).cancelOrder(o);
		}
		
		int price = (int) pricer.getPrice(a);
		Order o = new Order(this, a.getTimestamp(), a.getCommodity(), price, a.getVolume(), a.getDirection());
		a.newOrder(o);
		logger.debug("Sending order to the book: " + o);
		OrderBook.getBook(a.getCommodity()).addOrder(o);
		logger.debug(OrderBook.getBook(a.getCommodity()));
		return o;
	}
	
	/**
	 * Generate a random order (for testing)
	 * @return Order
	 */
	public Order generateOrder(long timestamp, Commodity c, Random prng) {
		
		final int MAX_P = 50;
		final int LOW_P = 10;
		final int MAX_V = 5;
		
		Direction d;
		double p;
		int v;
		
		
		if(prng.nextBoolean()) d = Direction.BUY;
		else d = Direction.SELL;
		
		if (d==Direction.SELL) {
			p = LOW_P+prng.nextInt(MAX_P-LOW_P);
		} else {
			p = prng.nextInt(MAX_P-LOW_P);
		}
		
		v = prng.nextInt(MAX_V)+1;
		

		Assignment a = new Assignment(this, p, v, d, c, timestamp, timestamp+10000);
		
		double price = (int) pricer.getPrice(a);
		
		Order o = new Order(this, timestamp, c, price, v, d);
		a.newOrder(o);
		oms.add(a);
		logger.debug("Order: " + o);
		//orders.add(o);
		return o;
	}

	@Override
	public void orderBookUpdated(OrderBookRecord orderBookRecord) {
		// TODO Auto-generated method stub
		logger.debug("Order book has been updated. Doing nothing.");
	}

	@Override
	public boolean execute() {
		boolean execute = false;
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
		// TODO Auto-generated method stub
	}
}
