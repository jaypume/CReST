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
package sim.market.trader.pricer;

import org.apache.log4j.Logger;

import sim.market.OrderBookRecord;
import sim.market.order.Direction;
import sim.market.order.Order;

public class Shout {

	public static Logger logger = Logger.getLogger(Shout.class);
	
	protected Direction direction;
	protected double price;
	protected boolean executed;
	
	public Shout(OrderBookRecord record) {
		
		price = record.getPrice();
		executed = record.isExecuted();
		direction = record.getSide();
		// NOTE: For an OrderBook, a sell order that executes is equivalent (for ZIP) to a bid shout that is accepted.
		if(executed) {
			if(direction.isBid()) direction = Direction.SELL;
			else direction = Direction.BUY;
		}
	}
	
	public Shout(Order o, boolean executed) {
		if(o!=null) {
			price = o.getPrice(); 
			this.executed = executed;
			direction = o.getDirection();
		} else {
			logger.warn("Order is null. Not constructing shout");
			return;
		}
	}
	
	public Shout(double price, Direction direction, boolean executed) {
		this.price = price;
		this.direction = direction;
		this.executed = executed;
	}

	@Override
	public String toString() {
		return "Shout [direction=" + direction + ", price=" + price
				+ ", executed=" + executed + "]";
	}

	public Direction getDirection() {
		return direction;
	}
	
	public boolean isBid() {
		return direction==Direction.BUY;
	}
	
	public boolean isAsk() {
		return direction==Direction.SELL;
	}

	public double getPrice() {
		return price;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	
	
}
