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

import java.text.DecimalFormat;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.order.Assignment;

public abstract class AbstractZIPPricer extends Pricer {

	public static Logger logger = Logger.getLogger(AbstractZIPPricer.class);
	
	protected DecimalFormat format = new DecimalFormat("#.##");
	
	protected double profit_margin = 0.0; // \mu ~ U(0.05,0.35) for sellers; ~ U(-0.35,-0.05) for buyers
	protected double learning_rate = 0.0; // \Beta ~ U(0.1,0.5) - initialised at time t=0
	protected double target_price = 0.0; //  \tau
	protected double momentum_coefficient = 0.0; // \gamma ~U(0.2,0.8)
	protected double Gamma = 0.0; //initialise to zero
	
	protected boolean isActive = true;
	
	protected String name = "AbstractZipPricer";
	
	public AbstractZIPPricer(Random prng, String name, double initial_profit_margin, double initial_learning_rate, double initial_momentum_coefficient) {
		super(prng);	
		this.name = name;
		this.profit_margin = initial_profit_margin;
		this.learning_rate = initial_learning_rate;
		this.momentum_coefficient = initial_momentum_coefficient;
		logger.warn("Created new " + this);
	}

	/**
	 * Update price and profit margin for a given assignment using latest shout
	 * @param a - the assignment to work
	 * @param shout - the latest shout
	 * @return updated price
	 */
	public abstract int updatePrice(Assignment a, Shout shout);
	
	/**
	 * Get shout price for an Assignment
	 * @param a - the assignment
	 * @return - shout price (rounded to nearest whole value using Math.round())
	 */
	public double getPrice(Assignment a) {
		return Math.round(a.getLimitPrice()*(1+profit_margin));
	}

	public String toString() {
		String s = name + "[profit_margin=" + format.format(profit_margin) 
				+ ", learning_rate=" + format.format(learning_rate) 
				+ ", target_price=" + format.format(target_price)
				+ ", momentum_coeff=" + format.format(momentum_coefficient) + "]";
		return s;
	}
	
	public void setActive() {
		isActive = true;
		logger.info("pricer is now active");
	}
	
	public void setInActive() {
		isActive = false;
		logger.info("Pricer is now inactive.");
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * calculate random relative price alteration: 
	 * R~Uniform[0.95,1] for price decrease, 
	 * R~Uniform[1,1.05] for price increase
	 * 
	 * Sellers should always look for price increase, buyers for a price decrease [Cliff & Bruten 1997, p.44]
	 * @param priceIncrease
	 * @return R
	 */
	protected double getRelativePriceAlteration(boolean priceIncrease) {
	
		final double range = 0.05;
		final double ran_value = prng.nextDouble()*range;
		
		double R;
		
		if(priceIncrease) R = 1+ran_value;
		else R= 1-ran_value;
		
		logger.debug("Price Increase="+priceIncrease+", R="+format.format(R));
		return R;
	}
	
	/**
	 * calculate random absolute price perturbation:
	 *  A~Uniform[0,0.05] for price increase, 
	 *  A~Uniform[-0.05,0] for price decrease
	 *  
	 *  Sellers should always look for price increase, buyers for a price decrease [Cliff & Bruten 1997, p.44]
	 * @param priceIncrease
	 * @return
	 */
	protected double getAbsolutePricePertubation(boolean priceIncrease) {
		final double range = 0.5; //0.05;
		final double ran_value = prng.nextDouble()*range;
		
		double A;
		
		if(priceIncrease) A = ran_value;
		else A = -ran_value;
		
		logger.info("Price Increase="+priceIncrease+", A="+ format.format(A));
		return A;		
	}
	
	
	/**
	 * Update profit margin based on latest shout price, q.
	 * 
	 * @param p - my price
	 * @param q - shout price
	 * @param raiseMargin; true=raise margin, false=lower magin
	 * @return new profit margin
	 */
	public abstract double updateProfitMargin(double p, double q, double limitPric, boolean raiseMargin);
	
	
	public double getProfitMargin() {
		return profit_margin;
	}
}
