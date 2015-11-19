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

/**
 * The Direction of an order (BUY/SELL)
 */
public enum Direction {
	
	BUY, SELL;
	
	public String toString(){
		String s="";
		switch(this) {
		case BUY:
			s+="Buy";
			break;
		case SELL:
			s+="Sell";
			break;
		default:
			s+="WARNING: Unknown Direction!";
		}
		
		return s;
	}
	
	/**
	 * Are the directions equal?
	 * @param other - the other Direction to compare this Direction with
	 * @return True if equal, false otherwise
	 */
	public boolean equals(Direction other){
		if(this==other) return true;
		else return false;
	}
	
	/**
	 * Are the directions opposite?
	 * @param other - the other Direction to compare this Direction with
	 * @return True if opposite, false otherwise
	 */
	public boolean opposite(Direction other){
		if(this==other) return false;
		else return true;
	}	
	
	/**
	 * Is this direction a bid?
	 * @return true is BUY, false if SELL
	 */
	public boolean isBid() {
		if (this==BUY) return true;
		else return false;
	}
	
	/**
	 * Is this direction an ask?
	 * @return true if SELL, false if BUY
	 */
	public boolean isAsk() {
		if (this==SELL) return true;
		else return false;
	}
};
