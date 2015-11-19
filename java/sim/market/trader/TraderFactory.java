package sim.market.trader;

import java.util.Random;

import org.apache.log4j.Logger;

public class TraderFactory {

	public static Logger logger = Logger.getLogger(TraderFactory.class);
	
	/**
	 * Enumeration class for Trader Types
	 * 
	 * @author cszjpc
	 *
	 */
	public enum TraderEnum { 
		
		//The Traders
		TestTrader("Test Trader", "TestTrader"), 
		ZIP("Zero Intelligence Plus", "ZIP"),
		IT("Instance Trader", "IT");
		
		private String humanReadableString;
		private String nameString;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 * @param nameString - must be exactly the same as the enum type name
		 */
		TraderEnum(String humanReadableDescription, String nameString) {
			this.humanReadableString = humanReadableDescription;
			this.nameString = nameString;
		}
		
		/**
		 * Human-readable Trader string 
		 */
		public String toString() {
			return humanReadableString;
		}
		
		/**
		 * Enumeration name as string
		 */
		public String getNameString() {
			return nameString;
		}
		
		/**
		 * A short label description of the Enum class
		 * 
		 * @return name label
		 */
		public static String getLabel() {
			return "Trader Type";
		}
	}
	
	/**
	 * Do not instantiate an object of this type (hence, private)
	 */
	private TraderFactory() {}
	
	/**
	 * Construct a Trader
	 * @param traderType - the type of trader
	 * @param prng - pseudo-random number generator
	 * @param buyProfitLowerBound - lower bound on (uniformly distributed) profit margin for buy pricer
	 * @param buyProfitUpperBound - upper bound on (uniformly distributed) profit margin for buy pricer
	 * @param sellProfitLowerBound - lower bound on (uniformly distributed) profit margin for sell pricer
	 * @param sellProfitUpperBound - upper bound on (uniformly distributed profit margin for sell pricer
	 */
	public static BaseTrader getTrader(TraderEnum traderType, 
			Random prng,
			double buyProfitLowerBound, 
			double buyProfitUpperBound, 
			double sellProfitLowerBound,
			double sellProfitUpperBound) {
		
		logger.info("Creating Trader of type: " + traderType);
		
		//Return Correct Topology Type
		switch(traderType) {
		
			case TestTrader: return (BaseTrader) new TestTrader(prng);
			case ZIP: return (BaseTrader) new ZipTrader(prng,
					buyProfitLowerBound,buyProfitUpperBound,sellProfitLowerBound,sellProfitUpperBound);
			case IT: return (BaseTrader) new InstanceTrader(prng,
					buyProfitLowerBound,buyProfitUpperBound,sellProfitLowerBound,sellProfitUpperBound);
			
			default: {
				logger.fatal("Uknown Trader type: " + traderType + " Exiting System...");
				System.exit(0);
				return null;				
			}	
		}
	}
}
