package sim.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sim.market.order.AssignmentServer;
import sim.market.order.Commodity;
import sim.market.trader.BaseTrader;
import sim.market.trader.TraderFactory;
import sim.market.trader.TraderFactory.TraderEnum;

public class TestMarket {

	public static Logger logger = Logger.getLogger(TestMarket.class);
	
	public static Random prng;
	
	public static void main(String[] args) {

		//Configure log4j using configuration properties file
		PropertyConfigurator.configure("resources/log4j_config/MarketTest-log4j.properties");
		
		final boolean buyersSilent = false; //true = BUYERS DO NOT BID (ie retail market)
		
		final int NUM_AGENTS = 50;
		
		final int SEED = 28;
		
		logger.info("*** Starting MarketTest *** SEED = " + SEED + ", NUM_AGENTS=" + NUM_AGENTS); 
		
        prng = new Random(SEED);
		
		Commodity c = new Commodity("RI");
		
		if(buyersSilent) logger.info("Buyers are silent: i.e., a RETAIL MARKET");
		else logger.info("Buyers not silent: i.e., a CDA MARKET");
		
		logger.info("Creating order book....");
		OrderBook ob = new OrderBook(prng, c); //an order book selling RIs
		
		logger.info(ob);
		
		long time = 0;
		
		logger.info("Creating a population of traders...");
		

		
		// Create a population of traders
		List<BaseTrader> population = new ArrayList<BaseTrader>();
		List<Integer> traderIDs = new ArrayList<Integer>();
		for (int i=0; i<NUM_AGENTS; i++) {
			//population.add(TraderFactory.getTrader(TraderEnum.TestTrader, prng));
			// Initialisation bounds (uniformly distributed) for trader profit margins...
			// Using Cliff & Bruten 97: ~U(0.05,0.035)
			population.add(TraderFactory.getTrader(TraderEnum.ZIP, prng, 0.05, 0.35, 0.05, 0.35));
			traderIDs.add(i);
		}


		logger.info("Adding population of traders as observers of the OrderBook...");
		//Add population as observers of the OrderBook
		for(BaseTrader t: population) {
			ob.addObserver(t);
		}

		
		logger.info("Setting up assignment server...");
		final int VOLUME = 1;
		final int lowPrice = 140;
		final int highPrice = 260;
		final int stepPrice = (highPrice-lowPrice)/((NUM_AGENTS/2)-1);
//		final int stepPrice = (highPrice-lowPrice)/((NUM_AGENTS/2)-1);
		logger.info("Step price = " + stepPrice);
		AssignmentServer assServer = new AssignmentServer(prng, c);
		
		// symmetric supply and demand curves
		assServer.generateTestSchedules(lowPrice, highPrice, stepPrice, VOLUME, NUM_AGENTS);
		
		// inelastic (flat) supply and demand with excess demand.
		//assServer.generateDemandSchedule(highPrice, highPrice, 0, VOLUME, NUM_AGENTS);
		//assServer.generateSupplySchedule(lowPrice, lowPrice, 0, VOLUME, NUM_AGENTS);
		
		int DAYS=1;
		for(int d=1; d<=DAYS; d++) {
			logger.info("Starting day " + d);

			logger.info("Now assigning assignments...");
			Collections.shuffle(population,prng);
			assServer.assign(population, time);

			logger.info("The population will now trade...");
			int total=20;
			for(int i=0; i<total; i++) {
				logger.info("Trading round " + i + "/" + total);
				Collections.shuffle(population,prng);
				for(BaseTrader t: population) {
					logger.info("Pushing trader " + t.getId() + " to trade");
					t.trade(buyersSilent);
				}
			}

			
			logger.debug(population);

			logger.debug("Now cancelling all assignments...");
			assServer.cancelAll();
			
			logger.info("End of day " + d);
			logger.info("Trading history is: " + OrderBook.getBook(c).getTradePriceHistory());

		}
		
		logger.debug("End of test.");
	}
}
