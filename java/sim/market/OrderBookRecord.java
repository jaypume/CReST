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

import org.apache.log4j.Logger;

import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.order.Order;

public class OrderBookRecord {

	public static Logger logger = Logger.getLogger(OrderBookRecord.class);

	protected static int id_counter = 0;
	
	protected int id;
	protected int volume;
	protected double price;
	protected Direction side;
	protected boolean executed;
	protected long timestamp;
	protected Commodity commodity;
	
	public OrderBookRecord(int volume, double price, Direction side, boolean executed, Commodity commodity, long timestamp) {
		this.id = getNextID();
		this.volume = volume;
		this.price = price;
		this.side = side;
		this.executed = executed;
		this.timestamp = timestamp;
		this.commodity = commodity;
	}
	
	public OrderBookRecord(Order o, boolean executed) {
		this.id = getNextID();
		this.volume = o.getVolume();
		this.price = o.getPrice();
		this.side = o.getDirection();
		this.executed = executed;
		this.timestamp = o.getTimestamp();
		this.commodity = o.getCommodity();
	}
	
	public int getId() {
		return id;
	}

	public int getVolume() {
		return volume;
	}

	public double getPrice() {
		return price;
	}

	public Direction getSide() {
		return side;
	}

	public boolean isExecuted() {
		return executed;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setSide(Direction side) {
		this.side = side;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setCommodity(Commodity commodity) {
		this.commodity = commodity;
	}

	protected static int getNextID() {
		OrderBookRecord.id_counter++;
		return OrderBookRecord.id_counter;
	}
	
	/**
	 * Is the Object an OrderBookRecord?
	 * @param arg - the Object to check
	 * @return - true if arg is an OrderBookRecord, false otherwise
	 */
	public static boolean isOrderBookRecord(Object arg) {
		if (arg==null) {
			logger.warn("Object is null: " + arg);
			return false;
		} else {
			return arg.getClass().getName().equals(OrderBookRecord.class.getName());
		}
	}
	
	@Override
	public String toString() {
		return "OrderBookRecord [id=" + id + ", executed=" + executed
				+ ", price=" + price + ", side=" + side + ", volume=" + volume
				+ ", commodity=" + commodity + ", timestamp=" + timestamp + "]";
	}
}
