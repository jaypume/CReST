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

public class Trade {

	public static Logger logger = Logger.getLogger(Trade.class);

	protected static int trade_id_counter = 0;
	
	protected Order buyOrder;
	protected Order sellOrder;
	protected int trade_id;
	protected double price;
	protected int volume;
	protected long timestamp;
	
	public Trade(double price, int volume, double timestamp, Order buy, Order sell) {
		if(buy.getDirection()==sell.getDirection()) logger.warn("WARNING: buy and sell orders same direction!");
		
		if(buy.getDirection()==Direction.BUY) buyOrder=buy; else sellOrder=buy;
		if(sell.getDirection()==Direction.SELL) sellOrder=sell; else buyOrder=sell;
		this.price = price;
		this.volume = volume;
		trade_id=Trade.getNextTradeID();
	}
	
	protected static int getNextTradeID() {
		Trade.trade_id_counter++;
		return Trade.trade_id_counter;
	}

	@Override
	public String toString() {
		return "Trade [price=" + price + ", volume=" + volume + ", timestamp="
				+ timestamp + ", trade_id=" + trade_id + ", buyOrder="
				+ buyOrder + ", sellOrder=" + sellOrder + "]";
	}
	
	/**
	 * Update the owners of a trade about the trade
	 */
	public void updateOwners() {
		buyOrder.getOwner().executedOrder(buyOrder,this);
		sellOrder.getOwner().executedOrder(sellOrder,this);
	}
	
	public static int getTrade_id_counter() {
		return trade_id_counter;
	}

	public static void setTrade_id_counter(int trade_id_counter) {
		Trade.trade_id_counter = trade_id_counter;
	}

	public Order getBuyOrder() {
		return buyOrder;
	}

	public void setBuyOrder(Order buyOrder) {
		this.buyOrder = buyOrder;
	}

	public Order getSellOrder() {
		return sellOrder;
	}

	public void setSellOrder(Order sellOrder) {
		this.sellOrder = sellOrder;
	}

	public int getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(int trade_id) {
		this.trade_id = trade_id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
