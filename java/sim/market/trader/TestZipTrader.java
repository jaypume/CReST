package sim.market.trader;

import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sim.market.order.Assignment;
import sim.market.order.Commodity;
import sim.market.order.Direction;
import sim.market.trader.TraderFactory.TraderEnum;
import sim.market.trader.pricer.Shout;
import sim.market.trader.pricer.ZipBuyPricer;

public class TestZipTrader {


	public static Logger logger = Logger.getLogger(TestZipTrader.class);

	public static Random prng;
	
	public static void main(String[] args) {
		
		//Configure log4j using configuration properties file
		PropertyConfigurator.configure("resources/log4j_config/MarketTest-log4j.properties");
				
		final int SEED = 27;
		
		logger.info("*** Starting ZipTraderTest *** SEED = " + SEED ); 
		
        prng = new Random(SEED);
		
		Commodity c = new Commodity("RI");
		
		logger.info("Creating a zip trader....");
		BaseTrader trader = TraderFactory.getTrader(TraderEnum.ZIP, prng, 0.05, 0.35, 0.05, 0.35);
		
		ZipBuyPricer buyPricer = new ZipBuyPricer(prng, -0.5, 0.2, 0.2);
//		ZipSellPricer sellPricer = new ZipSellPricer(prng, 0.5, 0.2, 0.2);
		Assignment b = new Assignment(trader, 100, 1, Direction.BUY, c, 1, 1001);
//		Assignment s = new Assignment(trader, 100, 1, Direction.BUY, c, 1, 1001);
		
//		int volume = 1;
		double q = 200;
		Direction direction = Direction.BUY;
		boolean executed = false;
		Shout shout;
		

		logger.info("Starting ZipBuyPricer test...");
		
		shout = new Shout(q, direction, executed);
		
		int i=0;
		int expectation=0;
		
		executed = true;
		q=1;

		logger.info("Testing zip buy pricer");
		logger.debug("TEST " + ++i + ": Buy pricer. Last shout executed at price " + q + ". p>=" + buyPricer.getPrice(b)+". p>q. So ***RAISE*** margin...");
		logger.debug("last shout is a bid");
		direction = Direction.BUY;
		shout = new Shout(q, direction, executed);
		
		expectation=1;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}
		logger.debug("last shout is an ask");
		direction = Direction.SELL;
		shout = new Shout(q, direction, executed);		
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}		
		
		direction=Direction.BUY;
		shout = new Shout(q, direction, executed);
		logger.debug("TEST " + ++i + ": Buy pricer. Last shout executed. Last shout a bid. p>q. So ***RAISE*** margin...");
		expectation=1;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}	
		
		q=1000;
		shout = new Shout(q, direction, executed);
		logger.debug("TEST " + ++i + ": Buy pricer. Last shout executed. Last shout a bid. ***DO NOT ALTER*** margin...");
		expectation=0;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}	
		
		
		
		logger.debug("TEST " + ++i+ ": Buy pricer. Last shout not executed. Last shout a bid. p <= q. So **LOWER** margin...");
		expectation=-1;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}
		
		q = 1;
		shout = new Shout(q, direction, executed);
		logger.debug("TEST " + ++i+ ": Buy pricer. Last shout not executed. Last shout a bid. p > q. So **DO NOT ALTER** margin...");
		expectation=0;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}

		direction = Direction.SELL;
		shout = new Shout(q, direction, executed);
		logger.debug("TEST " + ++i + ": Buy pricer. Last shout not executed. Last shout an ask. So ***DO NOT ALTER*** margin...");
		expectation=0;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}
		
			
		
		direction=Direction.SELL;
		
		executed = false;
		direction = Direction.SELL;
		shout = new Shout(q, direction, executed);
		logger.debug("TEST " + ++i + ": Buy pricer. Last shout not executed. Last shout an ask. So ***DO NOT ALTER*** margin...");
		expectation=0;
		if(buyPricer.updatePrice(b, shout)==expectation) {
			logger.info("Test " + i+ " Pass");
		} else {
			logger.info("Test " + i+ " Fail");
		}		
		
		logger.debug("End of test.");
	}
}



