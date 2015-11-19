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
package sim.market.Instance;

import org.apache.log4j.Logger;

public class Instance {

	public static Logger logger = Logger.getLogger(Instance.class);
	
	protected static int id_counter = 0;
	
	protected final int id;
	protected int length_in_months;
	protected int months_remaining;
	protected double price;
	protected boolean available = true;
	protected String name;
	
	/**
	 * Create an Instance with name, length and price
	 * @param name
	 * @param length_in_months
	 * @param price
	 */
	public Instance(String name, int length_in_months, double price) {
		this.name = name;
		this.length_in_months = length_in_months;
		months_remaining = length_in_months;
		this.price = price;
		this.id = getNextID();
		logger.debug("New instance created: " + this);
	}
		
	protected static int getNextID() {
		Instance.id_counter++;
		return Instance.id_counter;
	}
	
	/**
	 * Advance the month. 
	 * @return true if the instance is still live, false otherwise (i.e., reached end of life)
	 */
	public boolean advanceMonth() {
		available = true;
		if(months_remaining>1) {
			months_remaining--;
			return true;
		} else if(months_remaining==1) {
			months_remaining--;
			available=false;
			logger.debug("Instance now reached end of life: " + this);
			return false;
		} else {
			months_remaining=0;
			logger.warn("Can't advance month. Instance has no time remaining...");
			return false;
		}
	}

	public void setAvailable(boolean isAvailable) {
		available = isAvailable;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	@Override
	public String toString() {
		return "Instance [id=" + id + ", name=" + name + ", length_in_months="
				+ length_in_months + ", months_remaining=" + months_remaining
				+ ", available=" + available + ", price=" + price 
				+ "]";
	}
	
	
}
