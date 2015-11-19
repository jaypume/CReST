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
package sim.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sim.market.Instance.InstanceProvider;
import sim.market.config.MarketConfigParams;
import sim.market.config.MarketSettings;
import sim.market.log.InstanceTraderPopLogger;
import sim.market.log.MarketLogger;
import sim.market.log.ProviderLogger;
import sim.market.order.Assignment;
import sim.market.order.AssignmentServer;
import sim.market.order.Commodity;
import sim.market.order.DemandSchedule;
import sim.market.order.PriceVolumeSchedule;
import sim.market.order.PriceVolumeTuple;
import sim.market.order.SupplySchedule;
import sim.market.trader.BaseTrader;
import sim.market.trader.InstanceTrader;
import sim.market.trader.TraderFactory;
import sim.market.trader.TraderFactory.TraderEnum;
import sim.module.log.LogManager;
import config.ConfigParams;

/**
 * For details, refer to the following publication:
 * 
 * J. Cartlidge, (2014), Trading experiments using financial agents in a simulated cloud computing commodity market,
 * in Proc. 6th Int. Conf. Agents and Artif. Intelligence, Vol. 2 - Agents (ICAART-2014). B. Duval, J. van den Herik, 
 * S. Loiseau & J. Filipe, Eds. Angers, France: SciTePress, Mar. 2014, pp. 311-317. doi:10.5220/0004925303110317
 *
 */
public class RunICAART2014InstanceMarket {

	public static Logger logger = Logger.getLogger(RunICAART2014InstanceMarket.class);
	
	public static Random prng;
	
	/**
	 * Initialise the loggers
	 * @param logManager
	 */
	public static void initLogs(LogManager logManager) {
		logManager.prepareLogFiles(); //generate log directory
		logManager.getLogResultsDirName();
		String dirPrefix = logManager.getLogResultsDirName()+"/"+logManager.getLogResultsNumberString()+"marketLog";
		MarketLogger.getSingleton().initLogs(dirPrefix);
		ProviderLogger.getSingleton().initLogs(dirPrefix);
		InstanceTraderPopLogger.getSingleton().initLogs(dirPrefix);
	}
	
	/**
	 * Log (theoretical) equilibrium values for the market
	 * @param population
	 * @param c
	 */
	public static void logEquilibriumValues(List<BaseTrader> population, Commodity c) {
		DemandSchedule demandSchedule = new DemandSchedule(population, c);
		SupplySchedule supplySchedule = new SupplySchedule(population, c);		
		logger.info("Demand schedule = " + demandSchedule);
		logger.info("Supply schedule = " + supplySchedule);
		PriceVolumeTuple equilibrium = PriceVolumeSchedule.getEquilibrium(demandSchedule, supplySchedule);
		double surplusProfit = PriceVolumeSchedule.getSurplusProfit(demandSchedule, supplySchedule, equilibrium);
		MarketLogger.getSingleton().setEquilibriumPrice(equilibrium.getPrice());
		MarketLogger.getSingleton().setEquilibriumVolume(equilibrium.getVolume());
		MarketLogger.getSingleton().setProfitSurplus(surplusProfit);
	}

	
	/**
	 * Write monthly data to log files
	 */
	public static void writeMonthlyLogs() {
		MarketLogger.getSingleton().writeLog();
		ProviderLogger.getSingleton().writeLog();
	}
	
	/**
	 * Update the month for the log files
	 * @param m - new month
	 */
	public static void updateLogMonth(int m) {
		
		MarketLogger.getSingleton().resetValues();
		MarketLogger.getSingleton().setMonth(m);
		ProviderLogger.getSingleton().setMonth(m);
	}
	
	public static void writeSummaryLogs(List<BaseTrader> population) {
		
		logger.info("Writing population log...");
		InstanceTraderPopLogger.getSingleton().writeLog(population);
	}
	
	public static String getPopulationOrderingAsString(List<BaseTrader> population) {
		
		String s = "Population ordering: [";
		for(BaseTrader t: population) {
			s += " " + t.getId();
		}
		s+= "]";
		return s;
	}
	
	public static void main(String[] args) {

		Date now = new Date(System.currentTimeMillis());
		Date start = now;
		System.out.println(now + ": Starting RunICAART2014InstanceMarket...");
		
		//Configure log4j using configuration properties file
		PropertyConfigurator.configure("resources/log4j_config/MarketTest-log4j.properties");
		
		// Initialise the log files...
		LogManager logManager = new LogManager();
		initLogs(logManager);
		
		logger.info("Number of command line arguments: " + args.length);
		
		String configFilename = "";
		
		//get config filename
		if(args.length==1) {
			configFilename = args[0];
			logger.info("Using config file: " + configFilename);
		} else {
			System.err.println("No config passed on command line. Exiting");
			logger.fatal("No config passed on command line. Exiting");
			System.exit(-1);
		}

		
		MarketConfigParams.init(configFilename);
		logger.info(ConfigParams.getProperties());

		Properties properties = MarketConfigParams.getProperties();
		logger.info(properties.stringPropertyNames());
		
		MarketSettings marketSettings = new MarketSettings();
		marketSettings = MarketConfigParams.update(marketSettings);
		
		MarketConfigParams.copyConfigFileToResultsDirectory(logManager);
		
		
		logger.info("*** Starting MarketTest *** Random seed = " + marketSettings.getSeed() + 
				", number of agents =" + marketSettings.getNumAgents()); 
		
		System.out.println("PRNG Seed = " + marketSettings.getSeed());
        prng = new Random(marketSettings.getSeed());
		
		Commodity c = new Commodity("RI");
		
		logger.info("Creating order book....");
		OrderBook ob = new OrderBook(prng, c); //an order book selling RIs
		logger.info(ob);
		
		logger.info("Creating Instance Provider...");		
		InstanceProvider.createInstanceProvider(
				marketSettings.getOD_PRICE(), 
				marketSettings.getRIPrice(), 
				marketSettings.getRITermInMonths(), 
				marketSettings.getCommsRate());
		
		logger.info("Provider = " + InstanceProvider.getSingleton());
		
		logger.info("Creating a population of traders...");
		// Create a population of traders
		List<BaseTrader> population = new ArrayList<BaseTrader>();
		List<Integer> traderIDs = new ArrayList<Integer>();
		for (int i=0; i<marketSettings.getNumAgents(); i++) {
			//population.add(TraderFactory.getTrader(TraderEnum.TestTrader, prng));
			
			population.add(TraderFactory.getTrader(TraderEnum.IT, 
					prng, 
					marketSettings.getBuyProfitMarginLow(),
					marketSettings.getBuyProfitMarginHigh(),
					marketSettings.getSellProfitMarginLow(),
					marketSettings.getSellProfitMarginHigh()));
			traderIDs.add(i);
		}

		for(BaseTrader t: population) {
			if(marketSettings.getRITermInMonths()>1) {
				((InstanceTrader) t).setInternalSellLimitPrice((int)(marketSettings.getRIPrice()-marketSettings.getOD_PRICE())/(marketSettings.getRITermInMonths()-1));
			} else {
				logger.error("RI term in months = " + marketSettings.getRITermInMonths() + ", It makes no sense for traders to re-sell...");
				((InstanceTrader) t).setInternalSellLimitPrice((int)(marketSettings.getRIPrice()));
			}
			((InstanceTrader) t).setDefaultBuy(c, marketSettings.getOD_PRICE()-1);
			
		}
		MarketLogger.getSingleton().setRITermInMonths(marketSettings.getRITermInMonths());
		
		// set the population to enter executeAndKill orders
		if(marketSettings.getExecuteOrKill()) {
			for(BaseTrader t: population) {
				t.setExecuteOrKill(true);
			}
		}
		
		if(!marketSettings.isMarketMakerModel()) {
			final double PROB_ON_DEMAND_PURCHASE = 0.2;
			final double PROB_RI_PUCHASE = 1 - PROB_ON_DEMAND_PURCHASE;

			logger.info("Setting traders' probability of buying ondemand = " + (PROB_ON_DEMAND_PURCHASE*100) + "%, reserved = "
					+ (PROB_RI_PUCHASE*100) + "%...");
			for(BaseTrader t: population) {
				((InstanceTrader) t).setProbBuyingOnDemand(PROB_ON_DEMAND_PURCHASE);
				((InstanceTrader) t).setProbBuyingRI(PROB_RI_PUCHASE);
			}
		} else {
		
			logger.info("Setting " + marketSettings.getNumMarketMakers() + 
					" MARKET MAKERS... prob(purchase_RI)=" + marketSettings.getNumUnitsMarketMakerBuys());
			int market_maker_counter = 0;
			for(BaseTrader t: population) {
				if(market_maker_counter < marketSettings.getNumMarketMakers()) {
					((InstanceTrader) t).setProbBuyingRI(marketSettings.getNumUnitsMarketMakerBuys());
					((InstanceTrader) t).setMarketMaker(true, marketSettings.isMarketMakersGrow(), marketSettings.getGrowUp(), marketSettings.getGrowDown());
					((InstanceTrader) t).setDefaultSell(c);
				} else {
					((InstanceTrader) t).setProbBuyingOnDemand(marketSettings.getProbPurchasingOnDemand());	
					((InstanceTrader) t).setDefaultSell(c);
				}
				market_maker_counter++;
			}	
		}
		
		logger.info("Adding population of traders as observers of the OrderBook...");
		//Add population as observers of the OrderBook
		for(BaseTrader t: population) {
			ob.addObserver(t);
		}

		
		logger.info("Setting up assignment server...");
		final int VOLUME = 1;
//		final int lowPrice = 10;
//		final int highPrice = (int) Math.floor(marketSettings.getOD_PRICE());
//		final int stepPrice = (highPrice-lowPrice)/((NUM_AGENTS/2)-1);
//		final int stepPrice = (highPrice-lowPrice)/((NUM_AGENTS/2)-1);
//		logger.info("Step price = " + stepPrice);
		AssignmentServer assServer = new AssignmentServer(prng, c);
		
		// symmetric supply and demand curves
		// assServer.generateTestSchedules(lowPrice, highPrice, stepPrice, VOLUME, NUM_AGENTS);
		
		logger.info("Configuring assignment server...");
		// inelastic (flat) supply and demand with excess demand.
		
		// demand schedule for non-market makers...
		assServer.generateDemandSchedule(marketSettings.getDemandScheduleLowPrice(), 
				marketSettings.getDemandScheduleHighPrice(), 
				VOLUME, 
				marketSettings.getNumAgents()-marketSettings.getNumMarketMakers());

		int supplyStep = (marketSettings.getSupplyScheduleHighPrice() - marketSettings.getSupplyScheduleLowPrice()) / 
				(marketSettings.getNumAgents()-marketSettings.getNumMarketMakers());
		
		logger.info("supply step = " + supplyStep);
		
		// supply schedule for market makers...
		assServer.generateSupplySchedule(marketSettings.getSupplyScheduleLowPrice(),
				marketSettings.getSupplyScheduleHighPrice(),  
				VOLUME, 
				marketSettings.getNumMarketMakers());
			
		int numRIsSold = 0;
		int numOnDemandSold = 0;
		
		double inc = 0.0;
		for(int m=1; m<=marketSettings.getMonths(); m++) {
			
			double p = marketSettings.getProbPurchasingOnDemand();
			inc += marketSettings.getProbPurchaseOnDemandInc();
			if(inc > marketSettings.getIncMax()) inc = marketSettings.getIncMax();
			logger.warn("Month " + m + " Setting Prob(ondemand)=" + (p-inc));
			for(BaseTrader t: population) {
				if(!((InstanceTrader) t).isMarketMaker()) { // trader is not a market maker - increment prob
					((InstanceTrader)t).setProbBuyingOnDemand(p-inc); 
				}
			}	
			
			double percentComplete = (m/(double)marketSettings.getMonths())*100;
			now = new Date(System.currentTimeMillis());
			if(percentComplete%20==0) System.out.print(now + ": " + percentComplete+"% complete...    \r");  // use \r to overwrite line
			if(percentComplete%100==0) System.out.println(now + ": " + percentComplete+"% complete...    ");
			
			logger.info("Starting month " + m);
			updateLogMonth(m);
			
			logger.info("Issuing demand...");
			
			// Shuffle assignment distribution?
			if(marketSettings.getShuffleMonth()>0 && (m % marketSettings.getShuffleMonth()==0)) {
				logger.info("Before shuffle: " + getPopulationOrderingAsString(population));
				Collections.shuffle(population,prng);
				logger.info("After shuffle: " + getPopulationOrderingAsString(population));
			} else {
				logger.info("Before sort: " + getPopulationOrderingAsString(population));
				Collections.sort(population);
				logger.info("After sort: " + getPopulationOrderingAsString(population));
			}
			
			//get population of non-market makers and population of market makers...
			List<BaseTrader> nonMMPopBaseTraders = new ArrayList<BaseTrader>();
			List<InstanceTrader> marketMakerPop = new ArrayList<InstanceTrader>();
			for(BaseTrader t: population) {
				if(!((InstanceTrader) t).isMarketMaker()) { // trader is a market maker - do not assign demand...
					nonMMPopBaseTraders.add(t); 
				} else {
					marketMakerPop.add((InstanceTrader) t);
				}
			}	
			
			// assign demand to traders
			MarketLogger.getSingleton().addDemand(
					assServer.assignDemand(nonMMPopBaseTraders, marketSettings.getProbDemand(), m));

			// assign internal sell limit price for re-selling RIs to traders
			assServer.assignInternalSellLimitPrices(marketMakerPop);
			
			
			logger.info("Before sort: " + getPopulationOrderingAsString(population));
			Collections.sort(population);
			logger.info("After sort: " + getPopulationOrderingAsString(population));
			
			logger.info("Allow traders to buy RIs for investment...");
			if(marketSettings.getMonths()-m >= (marketSettings.getRITermInMonths()-1)) {
				for(BaseTrader t: population) {
					if(((InstanceTrader) t).investInCommodity(c)) {
						logger.info("Trader # " + t.getId() + " makes investment...");
					}
				}
			} else {
				logger.info("There is/are " + (marketSettings.getMonths()-m) + " months left in sim, not buying any more RIs for investment...");
			}
			
			
			logger.info("Match demand internally...");
			for(BaseTrader t: population) {
				
				logger.info("Asking trader #" + t.getId() + " to internalize trade...");
				t.internalizeTrades(c);
			}
			
			logger.info("Offer for sale excess commodities...");
			for(BaseTrader t: population) {
				
				logger.info("Asking trader #" + t.getId() + " to offer for sale commodities that are not needed...");
				if(t.assignExcessVolumeForSale(c)) {
					logger.info("New assignment self-generated by trader #" + t.getId());
				}
			}

			// Log (theoretical) equilibrium values for the market
			logEquilibriumValues(population, c);
			
			
			
			/////////////////////////////////////////////
//			System.exit(-1);; // TODO REMOVE THIS!
			////////////////////////////////////////////
			
			
			
			
			logger.info("Setting traders to select instance type...");
			for(BaseTrader t: population) {
				((InstanceTrader) t).selectInstanceType();
			}
			
			logger.info("The population will now trade...");

			for(int i=0; i<marketSettings.getNumTradingRounds(); i++) {
				logger.info("Trading round " + i + "/" + marketSettings.getNumTradingRounds());
				
				logger.info("Before shuffle: " + getPopulationOrderingAsString(population));
				Collections.shuffle(population,prng);
				logger.info("After shuffle: " + getPopulationOrderingAsString(population));
				
				int market_demand = 0;
				int market_supply = 0;
				for(BaseTrader t: population) {
					logger.info("Pushing trader #" + t.getId() + " to trade");
					t.trade(marketSettings.isRetailMarket());
					market_demand += t.getDemandVolume(c);
					market_supply += t.getSupplyVolume(c);
				}
				logger.info("Total market demand=" + market_demand + ", supply=" + market_supply);
				if(market_demand==0 || market_supply==0) {
					logger.info("No more demand or supply, finishing trading..."); 
					//otherwise, if we have supply and no buyers, the market goes into free fall (and vice-versa)
					break;
				}
			}


			logger.info("End of month " + m);
			
			logger.info("Trading over on market...");
			logger.debug(population);
			
			double prob = 0;
			for (BaseTrader t: population) {
				if(((InstanceTrader)t).isMarketMaker()) {
					prob += ((InstanceTrader)t).getProbBuyingRI();
				}
			}
			MarketLogger.getSingleton().setMarketMakerProb(prob);
			
			logger.info("Updating market makers...");
			for (BaseTrader t: population) {
				if(((InstanceTrader)t).isMarketMaker()) {
					((InstanceTrader)t).updateMarketMaker(MarketLogger.getSingleton().getMonthlyMeanTraderPrice(),1.1,0.2);
				}
			}
			
			logger.info("Cancel all self-generated sell orders on market, now out of time...");
			Collections.sort(population);
			
			//TODO : Remove all orders from the orderbook. And delete all SELL assignments (auto-generated by market makers)
			
			// The following code deletes all sell assignments. Note: BUY orders remain in the orderbook...			
			for(BaseTrader t: population) {
				logger.info("Pushing trader " + t.getId() + " to cancel self-generated sell assignments...");
				List<Assignment> sells = new ArrayList<Assignment>();
				List<Assignment> toDelete = new ArrayList<Assignment>();
				sells.addAll(t.getAssignments());
				for(Assignment s: sells) {
					if(s.isSell()) toDelete.add(s); // we delete sells here. 		
				}
				logger.info("Deleting assignments: " + toDelete);
				for(Assignment a: toDelete) t.cancelAssignment(a);
			}
			
			// Now, we need to delete all the buy orders left in the market.
			logger.info("Now delete all buy orders left in the order book...");
			for(BaseTrader t: population) {
				logger.info("Pushing trader " + t.getId() + " to cancel orders in the book...");
				if(!t.cancelOrders()) logger.warn("WARNING: Order cancellation failure!");
			}
			
			logger.info("Forcing traders to purchase required instances");
			logger.info("Sorting population into order...");
			Collections.sort(population);
			for(BaseTrader t: population) {
				logger.info("Pushing trader " + t.getId() + " to purchase instances");
				t.purchaseCommodities(c);
			}

			logger.info("Now cancelling all assignments..."); 
			// note, this only cancels assignments generated by the assignment
			// server, it does not cancel the trader's self-generated assignments...
			assServer.cancelAll();
			

			
			logger.info("Trading history is: " + OrderBook.getBook(c).getTradePriceHistory());

			logger.info("Advancing month for all traders...");
			for (BaseTrader t: population) {
				logger.info("Pushing trader " + t.getId() + " to advance month..." + t);
				((InstanceTrader)t).endTimeStep();
			}
			
			logger.info("End of month " + m);
			logger.info(InstanceProvider.getSingleton());
			
		
			MarketLogger.getSingleton().addRIsPurchased(InstanceProvider.getSingleton().getReservedInstancesSold()-numRIsSold);
			MarketLogger.getSingleton().addOnDemandPurchased(InstanceProvider.getSingleton().getOnDemandInstancesSold()-numOnDemandSold);
			
			numRIsSold = InstanceProvider.getSingleton().getReservedInstancesSold();
			numOnDemandSold = InstanceProvider.getSingleton().getOnDemandInstancesSold();

			// log monthly data...
			writeMonthlyLogs();

		}
		
		logger.info("Writing trader population to log...");
		writeSummaryLogs(population);
		
		logger.info("End of test.");
		now = new Date(System.currentTimeMillis());
		long executionTime = now.getTime() - start.getTime();
		double seconds = (executionTime / (double) 1000);
		System.out.println(now + ": End. [Execution time: " + seconds + " seconds]");
	}
}
