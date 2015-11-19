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
package sim.market.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.market.order.Trade;
import sim.market.stats.MarketStats;
import sim.module.log.Log;
import sim.module.log.LogManager;

public class MarketLogger extends AbstractMarketLogger{

	public static Logger logger = Logger.getLogger(MarketLogger.class);
	
	protected static MarketLogger singleton;

	protected int totalDemand;			// total Demand for RIs in the population
	protected int numberOnDemand;		// number of onDemand purchased from the provider
	protected int numberRIs;    		// number of RIs purchased from the provider
	protected int numberTraded; 		// number of RIs traded on the exchange
	protected double totalTradePrice; 	// total price of all trades on exchange
	
	protected double equilibriumPrice;  // Theoretical equilibrium price in the market
	protected int equilibriumVolume; 	// Theoretical equilibrium volume in the market
	protected double profitSurplus;		// Profit surplus in the market
	
	protected List<Trade> trades; 		// List of trades
	
	protected double marketMakerRIProb; // Prob of buying RIs by market makers
	protected int RITermInMonths;		// Length of RI term
	
	private MarketLogger() {
		super("market");
		trades = new ArrayList<Trade>();
	}
	
	public static MarketLogger getSingleton() {
		if(singleton == null) {
			singleton = new MarketLogger();
		}
		return singleton;
	}

	@Override
	protected String getLogTitleString() {
		return "Month, Total Demand, #OnDemand, #RIs, #Traded, P0, Q0, Av Trade Price, Profit surplus, Alpha (%), MarketMakerProb, MarketMakerSupply";
	}
	
	@Override
	public void resetValues() {
		
		totalDemand = 0;
		numberOnDemand = 0;
		numberRIs = 0;
		numberTraded = 0;
		totalTradePrice = 0;
		
		equilibriumPrice = 0; 
		equilibriumVolume = 0;
		
		trades.clear();
	}
	
	public void addTrade(Trade t) {
		trades.add(t);
		numberTraded += t.getVolume();
		totalTradePrice += t.getPrice();
		
		logger.info("New trade: " + t);
		logger.info("#units="+numberTraded + ", totalPrice=" + totalTradePrice);
	}
	
	public void addDemand(int demand) {
		totalDemand += demand;
	}
	
	public void addRIsPurchased(int number) {
		numberRIs += number;
	}
	
	public void addOnDemandPurchased(int number) {
		numberOnDemand += number;
	}
	
	public void setEquilibriumPrice(double price) {
		equilibriumPrice = price;
	}
	
	public void setEquilibriumVolume(int volume) {
		equilibriumVolume = volume;
	}	
	
	public void setProfitSurplus(double surplus) {
		profitSurplus = surplus;
	}
	
	public double getMonthlyMeanTraderPrice() {
		if(numberTraded>0) {
			return (double) totalTradePrice/numberTraded;
		} else {
			return 0;
		}
	}
	
	public void setRITermInMonths(int term) {
		RITermInMonths = term;
	}
	
	public double getMarketMakerProb() {
		return marketMakerRIProb;
	}
	
	public void setMarketMakerProb(double prob) {
		marketMakerRIProb = prob;
	}
	
	@Override
	public void writeLog() {
       
		Log log = new Log();
    	log.add(String.valueOf(month));
    	log.add(String.valueOf(totalDemand));
    	log.add(String.valueOf(numberOnDemand));
    	log.add(String.valueOf(numberRIs));
    	log.add(String.valueOf(numberTraded));
    	if(equilibriumPrice==Double.NaN) log.add(""); //do not output NaNs
    	else log.add(String.valueOf(equilibriumPrice));
    	log.add(String.valueOf(equilibriumVolume));
    	log.add(String.valueOf(format.format(getMonthlyMeanTraderPrice())));
    	log.add(String.valueOf(profitSurplus));
    	log.add(String.valueOf(MarketStats.getSmithsAlpha(trades, equilibriumPrice)));
    	log.add(String.valueOf(marketMakerRIProb));
    	log.add(String.valueOf(marketMakerRIProb*RITermInMonths));
    	for(Trade t: trades) {
    		log.add(String.valueOf(t.getPrice()));
    	}
    	
    	LogManager.writeLog(resultsLog,log);
    	logger.info("Log written: " + log);
	}
}
