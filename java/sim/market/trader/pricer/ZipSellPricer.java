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

import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.order.Assignment;

public class ZipSellPricer extends AbstractZIPPricer{

	public static Logger logger = Logger.getLogger(ZipSellPricer.class);
	
	public ZipSellPricer(Random prng, double initial_profit_margin, double initial_learning_rate, double initial_momentum_coefficient) {
		super(prng, "ZipSellPricer", initial_profit_margin, initial_learning_rate, initial_momentum_coefficient);	
	}
	
	/**
	 * ZIP Trader pseudo code: from Cliff & Bruten (1997) 
	 * "Minimal-Intelligence Agents for Bargaining Behaviors in Market-Based Environments", HPL-91-91, Tech Report
	 * 
	 * For Sellers:
	 *   - if (the last shout was accepted at price q) ## NOTE: For an OrderBook, a sell order that executes is equivalent (for ZIP) to a bid shout that is accepted.
	 *   - then
	 *      1. any seller s_i for which p_i<q should raise its profit margin
	 *      2. if (the last shout was a bid)
	 *         then
	 *         1. any active seller s_i for which p_i >= q should lower its margin
	 *   - else
	 *      1. if (the last shout was an offer)
	 *      then
	 *         1. any active seller s_i for which p_i >= q should lower its margin
	 *         
	 * For Buyers:
	 *    - if (the last shout was accepted at price q)
	 *    - then
	 *       1. any buyer b_i for which p_i>=q should raise its profit margin
	 *       2. if (the last shout was an offer)
	 *          then
	 *          1. any active buyer b_i for which p_i<=q should lower its margin
	 *    - else
	 *       1. if (the last shout was a bid)
	 *          then
	 *          1. any active buyer b_i for which p_i<=1 should lower its margin 
	 * 
	 * see http://www.hpl.hp.com/techreports/97/HPL-97-91.pdf
	 * 
	 * Adaptation:
	 * Shout price p_i(t) for unit j with limit lambda_{i,j}, using profit-margin /mu_i(t):
	 *    p_i(t) = lambda_{i,j}(1+/mu_i(t))
	 * For sellers, /mu_i(t)\in[0,\infty)
	 * For sellers, /mu_i(t)\in[-1,0]
	 * 
	 * Update rule for profit margin, \mu_i:
	 *    \mu_i(t+1) = (p_i(t) + \Delta_i(t)) / \lambda_{i,j} - 1
	 * where \Delta_i(t) is the Widrow-Hoff delta value, calculated using the individual trader's 
	 * learning rate \Beta_i:
	 *    \Delta_i(t) = \Beta_i( \tau_i(t) = p_i(t))
	 * and target price \tau_i(t):
	 *    \tau_i(t) = \R_i(t)q(t) + \A_i(t)
	 * where \R_i is a random var that sets target price relative to the price q(t) of the last shout,
	 * and \A_i(t) is a (small) random absolute price alteration.
	 * When the intention is to increase the shout price: \R_i>1.0 and \A_i > 0.0;
	 * When the intention is to decrease the shout price: 0.0 < \R_i < 1.0 and \A_i < 0.0
	 * 
	 * Every time the profit margin is altered, the target price is calculated using newly-generated random values of \R_i and \A_i.
	 * 
	 * Update rule used for ZIP traders:
	 *    \mu_i(t+1) = (p_i(t) + \Gamma_i(t)) / \lambda_{i,j} - 1
	 * 
	 * where \Gamma_i(t+1) = \gamma_i\Gamma_i(t) + (1-\gamma_i)\Delta_i(t)
	 * 
	 * \R_i~U(1.0,1.05) for price increases, and \R_i~U(0.95,1.0) for price decreases
	 * \A_i~U(0.0,0.05) for price increases, and \A_i~U(-0.05,0.0) for decreases
	 * 
	 * \Beta_i~U(0.1,0.5), initialised with the trader at time t=0
	 * Initial values of \mu_i~U(0.05,0.35) for sellers and ~U(-0.35,-0.05) for buyers: thus all traders begin with profit margins between 5 and 35 percent
	 */
	@Override
	public int updatePrice(Assignment a, Shout shout) {

		int margin_movement = 0; // 0=>no change, 1=>margin increased, -1=> margin decreased
		
		double current_margin = profit_margin;
		double new_margin = current_margin; 
		
		logger.debug("Updating price... Seller is " + ((isActive)?"active":"inactive"));
		double p = getPrice(a);
		double q;     
			 
		// Update profit based on latest shout. 
		if(shout==null) {
			logger.debug("The shout is null. Not updating...");
		} else {
			logger.debug("Latest Shout: " + shout);
			logger.debug("Pricer = " + this);
			//p = Math.round(a.getLimitPrice()*(1+profit_margin));
			logger.debug("current p=" + p + ", limit=" + a.getLimitPrice());
			q = shout.getPrice();
			
			logger.info("p="+format.format(p)+", q="+format.format(q));
			
			//I am a seller.
			if(shout.isExecuted()) {
				//last shout was accepted at price q
				logger.debug("Shout was accepted at price " + q);
				
				if(p<=q) {
					//raise profit margin...
					logger.debug("p<=q, Raising profit margin...");
					new_margin = updateProfitMargin(p,q,a.getLimitPrice(),true); //could get more? - try raising margin
				}
				if(shout.isBid()) {
					if(p>=q && isActive) {
						//lower profit margin...
						logger.debug("p>=q, Lowering profit margin...");
						new_margin = updateProfitMargin(p,q,a.getLimitPrice(),false); // wouldn't have for this deal, so mark the price down
					}
				}
				
			} else {
				//last shout was not accepted at price q
				logger.debug("Shout was not accepted at price " + q);
				
				if(shout.isAsk()) {
					if(p>=q && isActive) {
						//lower profit margin
						logger.debug("p>=q, Lowering profit margin...");
						new_margin = updateProfitMargin(p,q,a.getLimitPrice(),false); // would have asked for more and lost the deal, so reduce profit
					}
				} else {
					// do nothing
					logger.debug("Shout is a bid, do not update profit margin...");
				}
			}
			
			p = getPrice(a);
			logger.debug("New p=" + p);
		}
		
		logger.debug("previous margin = " + current_margin + ", new_margin = " + new_margin);
		
		//Seller profit margins are positive, thus *higher* values mean *higher* margins...
		if(current_margin>=0 && new_margin>=0) {
			if(new_margin==current_margin) {
				margin_movement=0;
				logger.debug("margin unchanged");
			} else if (new_margin > current_margin) {
				margin_movement=+1;
				logger.debug("margin increased");
			} else {
				margin_movement=-1;
				logger.debug("margin decreased");
			}
		} else {
			logger.warn("Profit margin out of bounds: " + new_margin);
		}
		return margin_movement;
	}

	/**
	 * Profit margins for sellers are in the range [0,infinity)
	 * Sellers raise their profit margin by increasing mu.
	 */
	@Override
	public double updateProfitMargin(double p, double q, double limitPrice, boolean raiseMargin) {
		{
			
			logger.debug("p="+p+ ", q="+q+", limit="+limitPrice + " profit margin=" + profit_margin);
			
			// calculate random relative price alteration 
			double R = getRelativePriceAlteration(raiseMargin); //I'm a seller, so raising margin is increasing price
			// calculate random absolute price alteration 
			double A = getAbsolutePricePertubation(raiseMargin); //I'm a seller, so raising margin is increasing price
			// calculate target price
			double target = R*q + A;  //[Cliff & Bruten 1997, p.44, equation (14)]
			// calculate delta
			double delta = learning_rate*(target-p);
			
			double Gamma_new = momentum_coefficient*Gamma + (1-momentum_coefficient)*delta;
			// profit margin update
			profit_margin = ((p + Gamma_new)/limitPrice) - 1;
			
			// check within bounds
			if(profit_margin < 0) profit_margin=0;
			
			// Finally, update Gamma
			Gamma = Gamma_new;
			
			logger.debug("Profit margin is now: " + profit_margin);
			return profit_margin;
		}
	}
}
