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
package sim.market.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import sim.market.Instance.Instance;
import sim.market.Instance.InstanceProvider;
import sim.market.Instance.ReservedInstance;
import sim.market.log.MarketLogger;
import sim.market.order.Assignment;
import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.order.Order;
import sim.market.order.Trade;

/**
 * An instance trader buys and sells reserved instances
 *
 */
public class InstanceTrader extends ZipTrader{

	public static Logger logger = Logger.getLogger(InstanceTrader.class);
	
	protected double prob_buyingOnDemand;
	protected double prob_buyingRI;
	
	protected List<Instance> instances; // instances owned
	protected List<Instance> history;   // history of instances owned, but no longer available to use
	
	protected double cash;
	protected int utilisation;
	
	// Record the number of instances purchases from the provider...
	protected int onDemandPurchasesCount;
	protected int reservedInstancePurchasesCount;
	
	protected boolean isMarketMaker = false; // Market Makers only purchase RIs to re-sell. They have no intrinsic demand
	protected boolean marketMakersGrow = false;
	protected double marketMakerGrowUp = 0.0;
	protected double marketMakerGrowDown = 0.0;
	
	protected boolean purchaseRIsOnly = false; //if this is true the trader invests in RIs
	
	
	
	/**
	 * Construct an InstanceTrader
	 * @param prng - Pseudo-random number generator
	 * @param buyProfitLowerBound - lower bound on (uniformly distributed) profit margin for buy pricer
	 * @param buyProfitUpperBound - upper bound on (uniformly distributed) profit margin for buy pricer
	 * @param sellProfitLowerBound - lower bound on (uniformly distributed) profit margin for sell pricer
	 * @param sellProfitUpperBound - upper bound on (uniformly distributed) profit margin for sell pricer
	 */
	public InstanceTrader(Random prng, 
			double buyProfitLowerBound, 
			double buyProfitUpperBound, 
			double sellProfitLowerBound,
			double sellProfitUpperBound) {
		
		super("Instance Trader", prng, buyProfitLowerBound, buyProfitUpperBound, sellProfitLowerBound, sellProfitUpperBound);
		//super.name = name + " Instance Trader";
		instances = new ArrayList<Instance>();
		history = new ArrayList<Instance>();
		cash = 0; //cash flow
		utilisation = 0; // number of months a reserved instance has been used	

		// We want profit margins to start high, otherwise the sellers will just be throwing the instances away. //TODO
		//double PROFIT_MARGIN_LOW_BOUND_PERCENT = 0.05;   //cliff-zip = 0.05 // Previously 20.0 [configure via S&D?]
		//double PROFIT_MARGIN_HIGH_BOUND_PERCENT = 0.35;  //cliff-zip = 0.35 // Previously 30.0 [configure via S&D?]
		// we want the trader to begin unbiased, so just make buy_margin = -sell_margin
		//profit_margin_sell = prng.nextDouble()*(sellProfitUpperBound-sellProfitLowerBound)+sellProfitLowerBound;
		
		//PROFIT_MARGIN_LOW_BOUND_PERCENT = 0.05;   //cliff-zip = 0.05 
		//PROFIT_MARGIN_HIGH_BOUND_PERCENT = 0.35;  //cliff-zip = 0.35 
		
		//profit_margin_buy = prng.nextDouble()*(buyProfitUpperBound-buyProfitLowerBound)+buyProfitLowerBound;
		//buyPricer = new ZipBuyPricer(prng, -profit_margin_buy, learning_rate, momentum_coefficient);
		//sellPricer = new ZipSellPricer(prng, profit_margin_sell, learning_rate, momentum_coefficient);
		
		onDemandPurchasesCount = 0;
		reservedInstancePurchasesCount = 0;
	}

	
	/**
	 * Set the trader to be a market maker. 
	 * 
	 * Note: market makers only purchase RIs for re-sale. They have no intrinsic demand.
	 * @param isMarketMaker - true sets trader to be a market maker, false to set trader as non-MM
	 * @param marketMakersGrow - true sets the market maker to grow/shrink over time. 
	 */
	public void setMarketMaker(boolean isMarketMaker, boolean marketMakersGrow, double growUp, double growDown) {
		this.isMarketMaker = isMarketMaker;
		this.marketMakersGrow = marketMakersGrow;
		this.marketMakerGrowUp = growUp;
		this.marketMakerGrowDown = growDown;
	}
	
	/**
	 * Is the trader a market maker?
	 * 
	 * Note: market makers only purchase RIs for re-sale. They have no intrinsic demand.
	 * @return true if trader is a market maker, false otherwise
	 */
	public boolean isMarketMaker() {
		return isMarketMaker;
	}
	
	/**
	 * Prob buying on demand must be greater than or equal to 0 and less than or equal to 1. 
	 * 	
	 * @param prob
	 */
	public void setProbBuyingOnDemand(double prob) {
		if(prob<0) prob=0;
		if(prob>1) prob=1;
		prob_buyingOnDemand=prob;
		prob_buyingRI=1-prob;
	}
	
	public double getProbBuyingOnDemand() {
		return prob_buyingOnDemand;
	}

	/**
	 * Update trader to select instance type (on demand or RI).
	 * Use prob(RI) to select.
	 */
	public void selectInstanceType() {
		if(prng.nextDouble()<prob_buyingOnDemand) {
			purchaseRIsOnly=false; // only purchase on demand (from provider, or from orderbook)
		} else {
			purchaseRIsOnly=true; //only purchase RIs (ie do not buy on orderbook)
		}
		logger.info("Trader #" + this.getId() + " purchase onDemand=" + !purchaseRIsOnly);
	}
	
	/**
	 * Prob buying RI must be greater than or equal to 0. 
	 * If greater than 1, then multiple RIs may be bought
	 * @param prob
	 */
	public void setProbBuyingRI(double prob) {
		if(prob<0) prob=0;
		else if(prob>1) {
			prob_buyingRI=prob;
			prob_buyingOnDemand=0;
		}
		else {
			prob_buyingRI=prob;
			prob_buyingOnDemand=1-prob;			
		}
	
	}
	
	public double getProbBuyingRI() {
		return prob_buyingRI;
	}
	
	/**
	 * Does the trader buy reserved instances only?
	 * 
	 * @return true if trader buys only RIs, false if trader buys on demand from market, or from provider
	 */
	public boolean buyRIsOnly() {
		return purchaseRIsOnly;
	}
	
	/**
	 * Set the trader to purchase only reserved instances from the provider 
	 * (else, purchase on demand either from provider or from market)
	 * 
	 * @param buyRIsOnly - true sets trader to purchase RIs only, false sets trader to purchase on demand (from provider or from market)
	 */
	public void setPurchaseRIsOnly(boolean buyRIsOnly) {
		purchaseRIsOnly = buyRIsOnly;
	}
	
	public List<Instance> getInstances() {
		return instances;
	}
	
	/**
	 * Advance the month. Update internal accounts.
	 */
	public void advanceMonth() {
		
		List<Instance> expired = new ArrayList<Instance>();
		for(Instance i: instances) {
			if(!i.advanceMonth()) {
				history.add(i);
				expired.add(i);
				logger.debug("Adding instance to history list: " + i);
			}
		}
		logger.info("Trader #" + this.id + " Instances expired = " + expired.size());
		for(Instance e: expired) {
			instances.remove(e);
		}
	}
	
	/**
	 * Increment the number of months an instance has been utilised
	 */
	public void instanceUsed() {
		utilisation++;
	}
	
	public int instancesAvailable() {
		if(instances!=null && instances.size()>0) {
			int available = 0;
			for(Instance i: instances) {
				if(i.isAvailable()) available++;
			}
			return available;
		} else {
			return 0;
		}
	}
	
	/**
	 * Push the trader to invest in commodity (ie purchases to be resold)
	 * 
	 * @param c - the commodity to invest
	 * @return true if instances are bought, false otherwise 
	 */
	public boolean investInCommodity(Commodity c) {
		
		boolean invest = false;
		
		double units = 0; 
		if(prob_buyingRI>oms.getDemandVolume(c)) {
			logger.debug("Investing in commodity: " + c);
			logger.info("Prob purchasing RIs is " + prob_buyingRI + "; so will speculate...");
			units = Math.floor(prob_buyingRI);
			double prob = prob_buyingRI - units;
			if(prng.nextDouble()<prob) units++;
			
		}
		logger.debug("Trader #" + id + ": purchases for investment = " + units);
		
		// Purchasing additional RIs for investment/speculation
		for(int i=0; i<units; i++) {
			logger.info("Purchasing reserved instance");
			Instance mi = InstanceProvider.getSingleton().getReservedInstance();
			mi.setAvailable(true);
			instances.add(mi);
			cash-=InstanceProvider.getSingleton().getReservedInstancePrice(); // pay cash for purchase of instance
			reservedInstancePurchasesCount++;
			invest = true;
		}
		
		return invest;
	}
	
	
	/**
	 * Set a number of available instances to unavailable (ie, in use)
	 * @param numInstances - the number of instances to set unavailable
	 */
	private void setInstancesUnavailable(int numInstances) {
		
		for(Instance i: instances) {
			if(i.isAvailable()) {
				i.setAvailable(false);
				numInstances--;
				if(numInstances<=0) break;
			}
		}
	}
	
	/**
	 * Match demand (assignments to buy) against available instances owned
	 * @param c - the instance (commodity) type
	 * @return the number of instances matched to demand
	 */
	@Override
	public int internalizeTrades(Commodity c) {
		
		int available = instancesAvailable();
		
		logger.info("Trader #" + this.id + " attempting to match owned instances against internal demand...");	
		
		logger.info("Trader currently owns [" + available + "] available instances: " + instances);	
		logger.info("Trader has demand = " + oms.getDemandVolume(c));
		if(available>0) {
			// match what we own against our demand. Return the number matched. 
			int demandVol = oms.getDemandVolume(c);
			int excess = instancesAvailable() - demandVol;
			int amountMatched = 0;
			if(demandVol == 0) {
				//nothing to match
				//do nothing
				return 0;
			} else {
				logger.info("We have demand = " + demandVol + "this month, so lets match it against the " + available + " instances owned...");

				if(excess>0) amountMatched = demandVol; //match all demand volume
				else 		 amountMatched = available; // match just what we own

				int notMatched = oms.matchDemand(c, amountMatched); //match amount
				if(notMatched!=0) {
					logger.warn("WARNING! Some volume was not matched... This should be 0 [ " + notMatched + "]");
					amountMatched-=notMatched;
				}
				logger.warn("Amount matched =  " + amountMatched);
				logger.info("Updating utilisation. Previous = " + utilisation);
				utilisation += amountMatched;
				setInstancesUnavailable(amountMatched);
				
				logger.info("New utilisation = " + utilisation);
				logger.info("OMS is now: " + oms);
				
				return amountMatched;
			}
		} else {
			logger.info("No instances owned.");
			return 0;
		}
	}
	
	/**
	 * Set the internal limit price the trader will use for re-selling RIs
	 * @param limitPrice - sell limit
	 */
	public void setInternalSellLimitPrice(int limitPrice) {
		
		logger.info("Trader #" + id + " setting internal sell limit price = " + limitPrice);
		super.internalSellLimitPrice = limitPrice;
	}
	
	/**
	 * Set the internal limit price the trader will use for re-selling RIs
	 * return limitPrice 
	 */	
	public int getInternalSellLimitPrice() {
		return super.internalSellLimitPrice;
	}
	
	@Override
	/**
	 * Check whether the trader has excess capacity for a commodity (i.e., a commodity that is no longer required).  
	 * 
	 * If the trader has excess volume, then create a new assignment to SELL...
	 * 
	 * @return true if new sell assignment is created, false otherwise
	 */
	public boolean assignExcessVolumeForSale(Commodity c) {

		logger.info("Trader #" + this.id + " attempting to assign excess volume for sale...");	
		
		logger.info("First, match demand against available instances owned...");
		int matched = internalizeTrades(c);
		int available = instancesAvailable();
		logger.info(matched + " instances were matched. There are now " + available + " instances available... Putting these for sale.");
		
		if(available<0) {
			logger.warn("WARNING! Number of instances available [ " + available + "] is out of bounds. Returning false");
			return false;
		} else if (available == 0) {
			logger.info("No instances available to sell. Returning false");
			return false;
		} else {
			logger.info("We have " + available + " instances available to sell...");
			
			// Create a new assignment to sell with volume = instances available ....
			Assignment newAssignment = new Assignment(this, internalSellLimitPrice, available, Direction.SELL, c, 0, 0+1000);
			logger.info("Generated new assignment: " + newAssignment);
			this.addAssignment(newAssignment);
			
			return true;
		}
	}
	
	@Override
	/**
	 * Push the trader to purchase instance commodities off exchange (ie from the provider) 
	 * 
	 * @param c - the commodity to purchase
	 * @return true if purchases made, false otherwise
	 */
	public boolean purchaseCommodities(Commodity c) {
		int demand = oms.getDemandVolume(c);
		logger.info("Trader #" + id + " has demand = " + demand);
		
		if(demand == 0) return false;
		else {
			logger.info("Trader will purchase instances from provider... Prob(onDemand)=" + (int) Math.round(100*prob_buyingOnDemand) + "%, Prob(RI)=" + (int) Math.round(100*prob_buyingRI) +"%");
			for(int i=0; i<demand; i++) {
				if(prng.nextDouble()<prob_buyingOnDemand) {
					logger.info("Purchasing on demand instance");
					Instance mi = InstanceProvider.getSingleton().getOnDemandInstance();
					mi.setAvailable(false);
					instances.add(mi);
					cash-=InstanceProvider.getSingleton().getOnDemandPrice(); // pay cash for purchase of instance
					utilisation++; // increment utilisation for one month
					onDemandPurchasesCount++;
				} else {
					logger.info("Purchasing reserved instance");
					
					Instance mi = InstanceProvider.getSingleton().getReservedInstance();
					mi.setAvailable(false);
					instances.add(mi);
					cash-=InstanceProvider.getSingleton().getReservedInstancePrice(); // pay cash for purchase of instance
					utilisation++; // increment utilisation for one month
					reservedInstancePurchasesCount++;
				}
			}
			logger.info("Assignment now completed. Set assignments completed ...");
			if(oms.demandFulfilled(c)) logger.info("Assignments set completed.");
			else logger.info("No assignments set completed.");
			
			oms.performAdmin();
			logger.info(this);
			return true;
		}
	}
	
	
	/**
	 * Perform clearing. Transfer ownership of instances.
	 * 
	 * Return true if successful, false otherwise
	 */
	@Override
	public boolean performClearing(Order o, Trade t) {
		
		logger.info("Trader #" + id + " performing clearing on order " + o + ", trade: " + t);
		if(o.isBid()) {
			logger.info("Trader is a buyer, so will accept bought instances from seller. Nothing to do.");
		} else {
			logger.info("Trader is a seller, so will pass bought instances to buyer...");
			int vol = t.getVolume();
			logger.info("There are " + vol + " instances to pass. TODO: we should check commodity type here, but we only have one type (RI) at the moment..."); //TODO
			
			List<Instance> sold = new ArrayList<Instance>();
			InstanceTrader counterparty = (InstanceTrader) t.getBuyOrder().getOwner();
			logger.debug("Passing " + sold + " instances to Trader #" + counterparty.getId());
			//pass instances over.
			for(int i=0; i< vol; i++) {
				sold.add(getOneMonthReservedInstance(t.getPrice())); //This passes over the entire instance. We want to pop one month only
			}
			logger.debug("Passing instances: " + sold);
			counterparty.acceptInstances(sold);
			
			InstanceProvider provider = InstanceProvider.getSingleton();
			double comms = provider.getCommissionRate()*t.getPrice();
			logger.info("Paying commission on sale to provider. CommsRate=" + df2.format(provider.getCommissionRate())
					+ ", Sale=$" + t.getPrice() + ", Comms=$" + df2.format(comms));
			
			balance-=comms;
			provider.payCommission(comms);
			
			MarketLogger.getSingleton().addTrade(t);
			logger.info(this);
			logger.info(provider);
		}

		return false;
	}
	
	/**
	 * Pop a reserved instance from the instances owned
	 * @return Instance (type=reserved)
	 */
	protected Instance popReservedInstance() {
		
		for(Instance i: instances) {
			if(i instanceof ReservedInstance) {
				instances.remove(i);
				return i;
			}
		} 
		logger.warn("WARNING No reserved instance to pop. Returning null!");
		return null;
		
	}
	
	/**
	 * Get one month reserved instance from the available RIs owned. 
	 * @return Instance (type=reserved)
	 */
	protected Instance getOneMonthReservedInstance(double price) {
		
		for(Instance i: instances) {
			if(i instanceof ReservedInstance && i.isAvailable()) {
				i.setAvailable(false);
				return new ReservedInstance("Reserved-bought from Trader#" + this.id, 1, price);
			}
		} 
		logger.warn("WARNING No reserved instance to pop. Returning null!");
		return null;
	}
	
	/**
	 * Accept instances from another trader. 
	 * Side effect increment utilisation.
	 * @param bought - instances to accept
	 * @return true if instances accepted, false otherwise
	 */
	protected boolean acceptInstances(List<Instance> bought) {
		
		logger.info("Trader #" + this.id + " accepting instances: " + bought);
		boolean changed = this.instances.addAll(bought);
		utilisation+=bought.size();
		logger.info(this);
		return changed;
	}
	
	/**
	 * Increment the month of all instances. 
	 * 
	 * NOTE: InstanceTrader will increase profit margin slightly at the end of each month.
	 * 
	 * Perform any accounting updates or other clean-up operations necessary at the end of a time step.
	 * 
	 * This method is blank in the BaseTrader. Override to add functionality
	 */
	@Override
	public void endTimeStep() {
		advanceMonth();
		oms.clearCompletedAssignments();
		oms.resetMonthlySales();
		history.clear();
		logger.info("Finished time step. Trader is now: " + this);
	}
	
	/**
	 * update the number of instances the market maker will purchase
	 * @param meanTradePrice - the current monthly mean trade price
	 * @param multiplier - a buffer on the trade price
	 * @param delta - the amount to change
	 */
	public void updateMarketMaker(double meanTradePrice, double multiplier, double delta) {
		
		
		//TODO - do not purchase more if we didnt sell enough!
		if(this.isMarketMaker && this.marketMakersGrow) {
			//if(meanTradePrice>multiplier*this.getInternalSellLimitPrice()) {
			if(oms.getSupplyAssignments().size()==0) {
				this.prob_buyingRI+=marketMakerGrowUp;
				logger.warn("Increasing number to buy = " + (this.prob_buyingRI));
			} else {
				this.prob_buyingRI-=marketMakerGrowDown; //
				if(this.prob_buyingRI<=0) this.prob_buyingRI=0;
				logger.warn("Sell assignments incomplete = " + oms.getSupplyAssignments().size() + ", Decreasing number to buy = " + (this.prob_buyingRI));
			}
		} else {
			logger.warn("Trader #" + id + " is not a market maker...");
		}
	}
	
	/**
	 * Get the cost per utilisation.
	 * @return cost/util, or 0 if utilisation=0
	 */
	protected double getCostPerUtil() {
		double cost_per_util = 0;
		if(utilisation>0) cost_per_util = (cash+balance)/utilisation;
		return cost_per_util; 
		
	}
	
	protected double getBalancePerVolume() {
		
		double balance_per_volume = Math.round(balance);
		if(getStockOwned()!=0) balance_per_volume = balance/Math.abs(getStockOwned());
		return balance_per_volume;
	}
	
	protected double getProviderUnitCost() {
		double provider_unit_cost = Math.round(cash);
		if(onDemandPurchasesCount+reservedInstancePurchasesCount>0) provider_unit_cost=cash/(onDemandPurchasesCount+reservedInstancePurchasesCount);
		return provider_unit_cost;
	}
	
	
	@Override 
	public String toString() {
		
		return "\nTrader [#" + id + ", " 
					+ name + ", " + ((isMarketMaker)?"market maker":"trader")
					+ ", util=" + utilisation 
					+ ", cost/util=$" + df2.format(-getCostPerUtil()) + "]"
					+ ", prob(purchase RIs)=" + prob_buyingRI
					+ ", internalSellLimitPrice=" + internalSellLimitPrice
				+ ",\n Accounts "
				+ "[Provider: onDemand=" + onDemandPurchasesCount 
					+ ", reserved=" + reservedInstancePurchasesCount 
					+ ", totalCost=$" + df2.format(cash) 
					+ ", unitCost=$" + df2.format(getProviderUnitCost()) +"]"
				+ ", [Trading: bought=" + stockBought 
					+ ", sold=" + stockSold 
					+ ", balance=$" + df2.format(balance) 
					+ ", balance/vol=$" + df2.format(getBalancePerVolume()) 
					+ "] ] " 	
				+ "\n Instances owned: " + instances 
				+ "\n Instances history: " + history
				+ oms +"\n";
		
	}
	
	/**
	 * Title string (column headers) for instance trader log output
	 * @return column headers string
	 */
	public static String getLogTitleString() {
		return "Trader, "
				+ "Type, "
				+ "MarketMaker?, "
				+ "Profit, "
				+ "Utilisation, "
				+ "Cost/Util, "
				+ "Prob(purchase RIs), "
				+ "#OD_purchased, "
				+ "#RI_purchased, "
				+ "TotalCost, "
				+ "UnitCost, "
				+ "#Buy_Market, "
				+ "#Sell_Market, "
				+ "TradeBalance, "
				+ "UnitTrade, "
				+ "Buy_Volume_Assigned, "
				+ "Sell_Volume_Assigned";
	}
	
	/**
	 * Return InstanceTrader data as a csv string for logging
	 * @return
	 */
	public String toLogString() {
		
		String csvData = "";
		
		csvData += id + ", "
				+ name + ", "
				+ isMarketMaker + ", "
				+ df2.format(cash+balance) + ", "
				+ utilisation + ", "
				+ df2.format(getCostPerUtil()) + ", "
				+ prob_buyingRI + ", "
				+ onDemandPurchasesCount + ", "
				+ reservedInstancePurchasesCount + ", "
				+ df2.format(cash) + ", "
				+ getProviderUnitCost() + ", "
				+ stockBought + ", "
				+ stockSold + ", "
				+ df2.format(balance) + ", "
				+ df2.format(getBalancePerVolume()) + ", "
				+ oms.getTotalBuyVolumeAssigned() + ", "
				+ oms.getTotalSellVolumeAssigned();
		
		logger.info("Trader #" + id + " csv_data = [" + csvData + "]");
		
		return csvData;
	}
}
