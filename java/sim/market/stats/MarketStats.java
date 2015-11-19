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
package sim.market.stats;

import java.util.List;

import org.apache.log4j.Logger;

import sim.market.order.Trade;

public class MarketStats {

	public static Logger logger = Logger.getLogger(MarketStats.class);
	
	/**
	 * Calculate Smith's Alpha metric for a series of trades and a theoretical equilibrium price
	 * 
	 * @param trades 
	 * @param equilibriumPrice 
	 * @return Smith's Alpha (as a percentage of equilibrium price)
	 */
	public static double getSmithsAlpha(List<Trade> trades, double equilibriumPrice) {
		
		
		double totalVolume = 0; // we weight trades by volume.
		double sum = 0;
		
		for(Trade t: trades) {
			totalVolume += t.getVolume(); // keep track of total volume
			
			sum += t.getVolume() * Math.pow(t.getPrice() - equilibriumPrice, 2); // volume weighted square of price difference
		}
		sum/=totalVolume; // divide by total volume
		
		double alpha = 100/equilibriumPrice * Math.sqrt(sum/totalVolume);

		logger.info("P_0 = " + equilibriumPrice + " trades = " + trades);
		logger.info("Alpha = " + alpha);
		return alpha;
	}
}
