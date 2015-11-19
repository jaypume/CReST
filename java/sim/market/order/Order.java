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

import org.apache.log4j.Logger;

import sim.market.trader.BaseTrader;
import sim.market.trader.TestTrader;

public class Order implements Comparable<Order>{
	
	public static Logger logger = Logger.getLogger(Order.class);
	
	protected static int order_id_counter = 0;
	
	protected Direction direction;
	protected int volume;
	protected double price;
	protected Commodity commodity;
	protected long timestamp;
	protected BaseTrader owner;
	protected int id;

	public Order(BaseTrader owner, long time, Commodity c, double p, int v, Direction d) {
		
		this.owner = owner;
		this.timestamp = time;
		this.commodity = c;
		this.price = p;
		this.volume = v;
		this.direction = d;
		this.id = Order.getNextOrderID();
	}
	
	protected static int getNextOrderID() {
		Order.order_id_counter++;
		return Order.order_id_counter;
	}
	
	/**
	 * Check volume against an order. 
	 * Volume is legal if greater than 0 and less than order volume
	 * Volume is not legal if less than zero or greater than order volume
	 * @param volume - the volume to check
	 * @return true if volume is legal, false otherwise
	 */
	public boolean isLegalVolume(int volume) {
		if (volume>0 && volume<this.volume) return true;
		else return false;
		
	}
	
	/**
	 * Split order. Original order has newOrderVolume removed.
	 * New order has newOrderVolume.
	 * @param newOrderVolume - the volume of the new order
	 * @return new Order containing newOrderVolume
	 */
	public Order splitOrder(int newOrderVolume) {
		logger.debug("Splitting order: " + this + ". New order will have volume: " + newOrderVolume);
		if (isLegalVolume(newOrderVolume)) {
			//split order. Reduce volume of this order and create new order with remaining volume
			this.volume = this.volume-newOrderVolume;
			Order newOrder = new Order(this.owner, this.timestamp, this.commodity, this.price, newOrderVolume, this.direction);
			logger.debug("Order now split. Old order: " + this + "; new order: " + newOrder);
			
			newOrder.owner.getOrders().add(newOrder); //Make owner aware of new order!
			
			this.owner.orderSplit(newOrder, this);
			
			return newOrder;
		} else {
			logger.warn("New volume: " + newOrderVolume + " is not legal. Not splitting order. Returning null");
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "[" + direction + ", " + volume
				+ ", $" + price + ", time=" + timestamp + ", owner=" + owner.getId() + ", #" + id
				+ "]";
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Delete order. Remove order from owner's order list
	 * @return true if deletion successful, false otherwise
	 */
	public boolean delete() {
		return this.getOwner().notifyOrderDeletion(this);
	}
	
	/**
	 * Is the Order a bid?
	 * @return true if buy order, false otherwise
	 */
	public boolean isBid() {
		if(direction==Direction.BUY) return true;
		else return false;
	}
	
	/**
	 * Is the Order an ask?
	 * @return true if sell order, false otherwise
	 */
	public boolean isAsk() {
		if(direction==Direction.SELL) return true;
		else return false;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getVolume() {
		return volume;
	}

	@Deprecated 
	/**
	 * DEPRECATED: THIS IS DANGEROUS. DO NOT ALLOW ORDERS TO BE EDIT EXTERNALLY! //TODO: REMOVE
	 * 
	 * Set the volume of the Order. 
	 * 
	 * WARNING: Volume must be greater than 0. If volume is not greater than zero,
	 * then the method returns 'false' and the volume is set to zero.
	 * 
	 * @param volume
	 * @return true if volume is set correctly, false if not.
	 */
	public boolean setVolume(int volume) {
		if (volume>0) {
			this.volume = volume;
			logger.debug("Order volume updated to " + volume + ". Order now: " + this);
			return true;
		} else {
			logger.warn("Ilegal volume: " + volume + " for order. Setting volume to zero. Order now: " + this);
			this.volume = 0;
			return false;
		}
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Commodity getCommodity() {
		return commodity;
	}

	public void setCommodity(Commodity commodity) {
		this.commodity = commodity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public BaseTrader getOwner() {
		return owner;
	}

	public void setOwner(TestTrader owner) {
		this.owner = owner;
	}

	public int compare(Object arg0, Object arg1) {
		
		//Return -ve if A<B, +ve if A>B
		//For Buys, A>B if Price_A > Price_B 
		//For Sells, A>B if Price_A < Price_B 
		//If Price_A==Price_B, A>B if Time_A < Time_B
		
		// TODO Auto-generated method stub
		Order A = (Order)arg0;
		Order B = (Order)arg1;
		
		if(A.getDirection().opposite(B.getDirection())) {
			logger.warn("WARNING: Comparing Orders with opposite directions. Returning 0");
			return 0;
		} else {
			
			if(A.getDirection()==Direction.BUY) {
				if(A.getPrice()>B.getPrice()) {
					return 1;
				} else if (A.getPrice()<B.getPrice()) {
					return -1;
				} else { //A.price == B.price
					if(A.getTimestamp()<B.getTimestamp()) {
						return 1;
					} else {
						return -1;
					}
				}
			} else { // Direction==SELL
				if(A.getPrice()>B.getPrice()) {
					return -1;
				} else if (A.getPrice()<B.getPrice()) {
					return 1;
				} else { //A.price == B.price
					if(A.getTimestamp()<B.getTimestamp()) {
						return 1;
					} else {
						return -1;
					}
				}					
			}
		}
	}

	@Override
	public int compareTo(Order o) {

		//We want the *best* order to be at the top, ie at the start of the list.
		//
		//Therefore:
		//
		//Return -ve if A>B i.e, "A is better than B", +ve if A<B
		//For Buys, A<B if Price_A > Price_B 
		//For Sells, A<B if Price_A < Price_B 
		//If Price_A==Price_B, A<B if Time_A < Time_B
		
		
		if(this.getDirection().opposite(o.getDirection())) {
			logger.warn("WARNING: Comparing Orders with opposite directions. Returning 0");
			return 0;
		} else {
			
			if(this.getDirection()==Direction.BUY) {
				if(this.getPrice()>o.getPrice()) {
					return -1;
				} else if (this.getPrice()<o.getPrice()) {
					return 1;
				} else { //this.price == o.price
					if(this.getTimestamp()<o.getTimestamp()) {
						return -1;
					} else {
						return 1;
					}
				}
			} else { // Direction==SELL
				if(this.getPrice()<o.getPrice()) {
					return -1;
				} else if (this.getPrice()>o.getPrice()) {
					return 1;
				} else { //this.price == o.price
					if(this.getTimestamp()<o.getTimestamp()) {
						return -1;
					} else {
						return 1;
					}
				}					
			}
		}
	}
}
