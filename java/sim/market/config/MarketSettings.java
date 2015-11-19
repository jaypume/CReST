/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012 John Cartlidge 
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
package sim.market.config;

import java.util.Random;

import org.apache.log4j.Logger;

public class MarketSettings {

	public static Logger logger = Logger.getLogger(MarketSettings.class);
	
	// Experiment settings...
	
	public static String SEED_KEY = "seed";
	long seed;
	
	public static String NUM_AGENTS_KEY = "numAgents";
	int numAgents;
	
	public static String MM_MODEL_KEY = "marketMakerModel";
	boolean marketMakerModel;
	
	public static String NUM_MARKET_MAKERS_KEY = "numMMs";
	int numMarketMakers;
	
	public static String NUM_UNITS_MARKET_MAKER_BUYS_KEY = "numUnitsMMBuys"; // number of RIs market maker buys each month
	double numMarketMakerUnits;	
	
	public static String MARKET_MAKERS_GROW_KEY = "marketMakersGrow"; // Do market makers grow?
	boolean marketMakersGrow;
	public static String MMS_GROW_UP_KEY = "growUp";
	double growUp;
	
	public static String MMS_GROW_DOWN_KEY = "growDown";
	double growDown;
	
	//proportion of population having demand each month [this seems to work!]
	
	public double getGrowUp() {
		return growUp;
	}

	public double getGrowDown() {
		return growDown;
	}

	public static String PROB_DEMAND_KEY = "probDemand";
	double probDemand;
	
	// Probability trader (non market maker) purchases On Demand (rather than RI)
	public static String PROB_PURCHASE_ON_DEMAND_KEY = "probPurchaseOnDemand";
	double probPurchaseOnDemand;

	// Probability trader (non market maker) purchases On Demand (rather than RI)
	public static String PROB_PURCHASE_ON_DEMAND_INC_KEY = "probPurchaseOnDemandInc";
	double probPurchaseOnDemandInc;
	public static String INC_MAX_KEY = "incMax";
	double incMax;
	
	public double getIncMax() {
		return incMax;
	}

	public double getProbPurchaseOnDemandInc() {
		return probPurchaseOnDemandInc;
	}

	public static String TRADING_ROUNDS_KEY = "tradingRounds";
	int numTradingRounds;
	
	public static String MONTHS_KEY = "months";
	int numMonths;

	// Trader pofiles...
	// Zip pricer profit margin distributions
	public static String BUY_MARGIN_LOW_KEY = "buyProfitMarginLowBound";
	public static String BUY_MARGIN_HIGH_KEY = "buyProfitMarginHighBound";
	public static String SELL_MARGIN_LOW_KEY = "sellProfitMarginLowBound";
	public static String SELL_MARGIN_HIGH_KEY = "sellProfitMarginHighBound";
	double buyProfitMarginLow;
	double buyProfitMarginHigh;
	double sellProfitMarginLow;
	double sellProfitMarginHigh;
	
	public static String EXECUTE_OR_KILL_KEY = "executeOrKill";
	boolean executeOrKill = false; // remove orders that do not execute immediately
	
	// Market mechanism...
	
	public static String RETAIL_MARKET_KEY = "retailMarket";
	boolean retailMarket; // true => buyers cannot BID on the exchange

	// Reserved Instance pricing from provider...
	
	public static String ON_DEMAND_PRICE_KEY = "onDemandPrice";
	double priceOD;
	
	public static String RI_PRICE_KEY = "RIPrice";
	double priceRI;
	
	public static String RI_TERM_KEY = "RITermInMonths";
	int termOfRIInMonths;
	
	public static String COMMISSION_KEY = "commissionRate";
	double commissionRate;
	
	// Demand and supply schedules for the market...
	public static String DEMAND_SCHEDULE_LOW_PRICE_KEY = "demandLowPrice";		
	int demandScheduleLowPrice;
	public static String DEMAND_SCHEDULE_HIGH_PRICE_KEY = "demandHighPrice";
	int demandScheduleHighPrice;
	public static String SUPPLY_SCHEDULE_LOW_PRICE_KEY = "supplyLowPrice";
	int supplyScheduleLowPrice;
	public static String SUPPLY_SCHEDULE_HIGH_PRICE_KEY = "supplyHighPrice";
	int supplyScheduleHighPrice;
	
	public static String SHUFFLE_MONTH__KEY = "shuffleMonth"; //shuffle the market every X months
	int shuffleMonth = 0; // default=0 => dont shuffle	
	
	/**
     * Set a configuration parameter using <kev,value> pair
     * 
     * @param key - the name of the parameter
     * @param value - the new value of the parameter
     * @return true if new value has been set, false otherwise
     */
	public boolean setValue(String key, String value) {

		boolean valueChanged = true;
		
		if(key.equals(MarketSettings.SEED_KEY)) {
			setSeed(Long.parseLong(value));
		} else if(key.equals(MarketSettings.NUM_AGENTS_KEY)) {
			numAgents = Integer.parseInt(value);
		} else if(key.equals(MarketSettings.MM_MODEL_KEY)) {
			marketMakerModel = Boolean.parseBoolean(value);
		} else if(key.equals(MarketSettings.NUM_MARKET_MAKERS_KEY)) {
			numMarketMakers = Integer.parseInt(value);
		} else if(key.equals(MarketSettings.NUM_UNITS_MARKET_MAKER_BUYS_KEY)) {
			numMarketMakerUnits = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.MARKET_MAKERS_GROW_KEY)) {
			marketMakersGrow = Boolean.parseBoolean(value);	
		} else if(key.equals(MarketSettings.MMS_GROW_UP_KEY)) {
			growUp = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.MMS_GROW_DOWN_KEY)) {
			growDown = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.PROB_DEMAND_KEY)) {
			probDemand = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.PROB_PURCHASE_ON_DEMAND_KEY)) {
			probPurchaseOnDemand = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.PROB_PURCHASE_ON_DEMAND_INC_KEY)) {
			probPurchaseOnDemandInc = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.INC_MAX_KEY)) {
			incMax = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.TRADING_ROUNDS_KEY)) {
			numTradingRounds = Integer.parseInt(value);
		} else if(key.equals(MarketSettings.MONTHS_KEY)) {
			numMonths = Integer.parseInt(value);
		} else if(key.equals(MarketSettings.RETAIL_MARKET_KEY)) {
			retailMarket = Boolean.parseBoolean(value);	
		} else if(key.equals(MarketSettings.ON_DEMAND_PRICE_KEY)) {
			priceOD = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.RI_PRICE_KEY)) {
			priceRI = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.RI_TERM_KEY)) {
			termOfRIInMonths = Integer.parseInt(value);	
		} else if(key.equals(MarketSettings.COMMISSION_KEY)) {
			commissionRate = Double.parseDouble(value);	
		} else if(key.equals(MarketSettings.DEMAND_SCHEDULE_LOW_PRICE_KEY)) {   // Demand & Supply Schedules...
			demandScheduleLowPrice = Integer.parseInt(value);	
		} else if(key.equals(MarketSettings.DEMAND_SCHEDULE_HIGH_PRICE_KEY)) {   
			demandScheduleHighPrice = Integer.parseInt(value);	
		} else if(key.equals(MarketSettings.SUPPLY_SCHEDULE_LOW_PRICE_KEY)) {   
			supplyScheduleLowPrice = Integer.parseInt(value);	
		} else if(key.equals(MarketSettings.SUPPLY_SCHEDULE_HIGH_PRICE_KEY)) {   
			supplyScheduleHighPrice = Integer.parseInt(value);	
		} else if(key.equals(MarketSettings.SHUFFLE_MONTH__KEY)) {
			shuffleMonth = Integer.parseInt(value);
		} else if(key.equals(MarketSettings.BUY_MARGIN_HIGH_KEY)) { // Zip pricer profit margin distributions
			buyProfitMarginHigh = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.BUY_MARGIN_LOW_KEY)) {
			buyProfitMarginLow = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.SELL_MARGIN_LOW_KEY)) {
			sellProfitMarginLow = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.SELL_MARGIN_HIGH_KEY)) {
			sellProfitMarginHigh = Double.parseDouble(value);
		} else if(key.equals(MarketSettings.EXECUTE_OR_KILL_KEY)) { // Trader fillOrKill setting
			executeOrKill = Boolean.parseBoolean(value);
		} else {
			logger.info("No parameter key: " + key);
			valueChanged = false;
		}		
				
		return valueChanged;
	}

	//ZIP pricer profit margin distributions
	
	/**
	 * Zip pricer profit margin distribution. 
	 * @return Buyer, lower bound
	 */
	public double getBuyProfitMarginLow() {
		return buyProfitMarginLow;
	}

	/**
	 * Zip pricer profit margin distribution. 
	 * @return Buyer, upper bound
	 */
	public double getBuyProfitMarginHigh() {
		return buyProfitMarginHigh;
	}

	/**
	 * Zip pricer profit margin distribution. 
	 * @return Seller, lower bound
	 */
	public double getSellProfitMarginLow() {
		return sellProfitMarginLow;
	}

	/**
	 * Zip pricer profit margin distribution. 
	 * @return Seller, upper bound
	 */
	public double getSellProfitMarginHigh() {
		return sellProfitMarginHigh;
	}

	public static String getSeedKey() {
		return SEED_KEY;
	}

	/**
	 * Get the random seed used to initialise the pseudo-random number generator
	 * @return seed
	 */
	public long getSeed() {
		return seed;
	}

    /**
     * Set random seed for simulation.  If seed == -1, randomize seed automatically.
     * @param seed
     */
	private void setSeed(long seed) {

	    if(seed == -1) {
    		long randomSeed = new Random().nextLong();
    		logger.warn("Seed is -1.  Randomizing new seed: " + randomSeed);
    		this.seed = randomSeed;
    	} else {
    		this.seed = seed;
    	}
	}
	
	public static String getNumAgentsKey() {
		return NUM_AGENTS_KEY;
	}

	/**
	 * Get the number of agents in the market
	 * @return number of agents
	 */
	public int getNumAgents() {
		return numAgents;
	}

	public static String getMarketMakderModelKey() {
		return MM_MODEL_KEY;
	}

	/**
	 * Are we using a market maker model?
	 * 
	 * A market maker model assumes that some agents in the market specifically act as market makers and will
	 * purchase instances purely to sell (for profit) on the secondary market
	 * @return true if market maker model, false otherwise 
	 */
	public boolean isMarketMakerModel() {
		return marketMakerModel;
	}

	public static String getNumMarketMakersKey() {
		return NUM_MARKET_MAKERS_KEY;
	}

	/**
	 * The number of RI units that a market maker buys each month.
	 * 
	 * Note: Probabilistic for partial values. Eg 1.5 => Buy one and buy a second with prob = 0.5.
	 * 
	 * @return config key string
	 */
	public static String getNumUnitsMarketMakerBuysKey() {
		return NUM_UNITS_MARKET_MAKER_BUYS_KEY;
	}
	
	/**
	 * The number of RI units that a market maker buys each month.
	 * 
	 * Note: Probabilistic for partial values. Eg 1.5 => Buy one, then buy a second with prob = 0.5.
	 * 
	 * @return units
	 */
	public double getNumUnitsMarketMakerBuys() {
		return numMarketMakerUnits;
	}
	
	/**
	 * Do Market Makers grow in size? If false, then number of units to buy each month is fixed
	 * @return true if market makers grow, false otherwise
	 */
	public boolean isMarketMakersGrow() {
		return marketMakersGrow;
	}
	
	/**
	 * Get the number of market makers in the market
	 * 
	 * Note: Maerkt Makers do not have any intrinsic demand. 
	 * They only buy RIs to re-sell on the secondary market.
	 * @return number of market makers
	 */
	public int getNumMarketMakers() {
		return numMarketMakers;
	}

	public static String getProbDemandKey() {
		return PROB_DEMAND_KEY;
	}

	/**
	 * Get the probability of assigning each trader a unit of demand (ie the proportion of the population that has demand)
	 * @return probability
	 */
	public double getProbDemand() {
		return probDemand;
	}

	// Probability trader (non market maker) purchases On Demand (rather than RI)
	public static String getProbPurchasingOnDemandKey() {
		return PROB_PURCHASE_ON_DEMAND_KEY;
	}

	/**
	 * Get the probability that a trader (non market maker) with a unit of demand will purchase
	 * an 'on demand' instance from the provider, rather than a 'reserved instance'. 
	 * @return probability
	 */
	public double getProbPurchasingOnDemand() {
		return probPurchaseOnDemand;
	}
	
	public static String getTradingRoundsKey() {
		return TRADING_ROUNDS_KEY;
	}

	/**
	 * Get the number of trading rounds that the experiment will run
	 * @return rounds
	 */
	public int getNumTradingRounds() {
		return numTradingRounds;
	}

	public static String getMonthsKey() {
		return MONTHS_KEY;
	}

	/**
	 * Get the number of months that the experiment will run
	 * @return months
	 */
	public int getMonths() {
		return numMonths;
	}

	public static String getRetailMarketKey() {
		return RETAIL_MARKET_KEY;
	}

	/**
	 * Are buyers silent in the market?
	 * 
	 * If true, then we have a retail market (only sellers shout)
	 * 
	 * @return true if retail market, false if continuous double auction
	 */
	public boolean isRetailMarket() {
		return retailMarket;
	}

	public static String getOnDemandPriceKey() {
		return ON_DEMAND_PRICE_KEY;
	}

	/**
	 * Price of an monthly instance bought on demand directly from the provider
	 * @return
	 */
	public double getOD_PRICE() {
		return priceOD;
	}

	public static String getRiPriceKey() {
		return RI_PRICE_KEY;
	}

	/**
	 * Price of a reserved instance bought directly from the provider
	 * @return
	 */
	public double getRIPrice() {
		return priceRI;
	}

	public static String getRiTermKey() {
		return RI_TERM_KEY;
	}

	/**
	 * Length/term of a reserved instance (in months)
	 * @return term
	 */
	public int getRITermInMonths() {
		return termOfRIInMonths;
	}

	public static String getCommissionKey() {
		return COMMISSION_KEY;
	}

	/**
	 * Commission rate charged by provider for all sales on the instance market
	 * @return rate
	 */
	public double getCommsRate() {
		return commissionRate;
	}

	public int getDemandScheduleLowPrice() {
		return demandScheduleLowPrice;
	}

	public int getDemandScheduleHighPrice() {
		return demandScheduleHighPrice;
	}

	public int getSupplyScheduleLowPrice() {
		return supplyScheduleLowPrice;
	}

	public int getSupplyScheduleHighPrice() {
		return supplyScheduleHighPrice;
	}
	
	/**
	 * 'ExecuteOrKill' orders are cancelled if not immediately executed.
	 * Note: this is not 'fillOrKill' - ie 'executeOrKill' enables partial volume execution.
	 * @return true if executeOrKill, false otherwise
	 */
	public boolean getExecuteOrKill() {
		return executeOrKill;
	}
	
	/**
	 * Shuffle assignments every X months.
	 * @return X
	 */
	public int getShuffleMonth() {
		return shuffleMonth;
	}
}
