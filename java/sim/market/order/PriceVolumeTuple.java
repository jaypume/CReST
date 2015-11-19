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

/**
 * A price and a volume tuple
 *
 */
public class PriceVolumeTuple implements Comparable<PriceVolumeTuple>{

	public static Logger logger = Logger.getLogger(PriceVolumeTuple.class);
	
	protected double price;
	protected int volume;
	
	public PriceVolumeTuple(double price, int volume) {
		if(price>=0 && volume>0) {
			this.price=price;
			this.volume=volume;
		} else {
			logger.warn("Illegal (price,volume) tuple (" + price + ", " + volume +"). Setting price and volume = 0");
			this.price = 0;
			this.volume = 0;
		}
	}

	/**
	 * Construct a null PriceVolumeTuple
	 */
	public PriceVolumeTuple() {
		this.price = Double.NaN;
		this.volume = 0;
	}
	
	public double getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public String toString() {
		return "Price=" + price + ", volume=" + volume;
	}

	@Override
	/**
	 * Order by price. Lowest first.
	 * Return -1 if this.price < other.price
	 * Return +1 if this.price > other.price
	 * Return 0 if htis.price == other.price
	 */
	public int compareTo(PriceVolumeTuple other) {
		// We want to order by price (lowest first)
		//
		//Therefore:
		//
		//Return -ve if this.price < other.price
		
		if(this.price < other.price) return -1;
		else if(this.price > other.price) return 1;
		else return 0;
	}
}
