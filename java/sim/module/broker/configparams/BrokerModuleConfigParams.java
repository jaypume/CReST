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
package sim.module.broker.configparams;

import org.apache.log4j.Logger;
import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;

public class BrokerModuleConfigParams implements ModuleParamsInterface {
	
	public static Logger logger = Logger.getLogger(BrokerModuleConfigParams.class);
	
	// How to forecast demand over the next 36-month period. This code needs to be tidied up
	/**
	 * Enumeration of the Broker's demand forecasting methods.
	 * 
	 * Includes:
	 * (1) ROGERS_AND_CLIFF_2012_DEMAND_FORECASTER
	 * (2) REGRESSION_DEMAND_FORECASTER
	 */
	public enum DemandForecaster {
		/**
		 * The forecasting method used by Rogers & Cliff (2012),
		 * i.e., future demand = historic demand (lag 36 monhts)
		 */
		ROGERS_AND_CLIFF_2012_DEMAND_FORECASTER("RC_Demand_Forecaster"), 
		/**
		 * Use linear regression on historic demand to determine best fit linear trend,
		 * then use trend to forecast future demand
		 */
		REGRESSION_DEMAND_FORECASTER("Regression_Demand_Forecaster");
		
		public static final String DEMAND_FORECASTER_XML_TAG = "demandForecaster";
		
		private final String name; //string representation
		
		private static final DemandForecaster DEFAULT_DEMAND_FORECASTER = DemandForecaster.ROGERS_AND_CLIFF_2012_DEMAND_FORECASTER;		
		
		DemandForecaster(String name) {
			this.name = name;
		}
		
		/**
		 * Return the default demand forecaster. 
		 */
		public static DemandForecaster getDefault() {
			return DEFAULT_DEMAND_FORECASTER;
		}
		
		public String getName() {
			return this.name;
		}
		
		/** Get a DemandForecaster by name */
		public static DemandForecaster get(String name) {
			for (DemandForecaster d: DemandForecaster.values()) {
				if (name.equals(d.getName())) {
					return d;
				}
			} 
			// we should not get here if a DemandForecaster is found with name = name
			// so, log a warning and return default
			logger.warn("DemandForecaster with name '"+name+ "' does not exist. Returning default: " + DEFAULT_DEMAND_FORECASTER);
			return DEFAULT_DEMAND_FORECASTER;
		}
	}
	
	
	
	/**
	 * Enumeration of the reserved instance reservation period (12 or 36 months)
	 */
	public enum ReservationPeriod {
		
		/** 12 months reserved instances */
		ONE_YEAR("12_months",12), 
		/** 36 months reserved instance */
		THREE_YEARS("36_months",36);
		
		public static final String RESERVATION_PERIOD_XML_TAG = "reservationPeriod";
		
		public static final ReservationPeriod DEFAULT_RESERVATION_PERIOD = ReservationPeriod.THREE_YEARS;
		
		private final int reservation_period_in_months; 
		private final String name;
		
		ReservationPeriod(String name, int months) {
			this.name = name;
			this.reservation_period_in_months = months;
		}
		
		public String getName() {
			return this.name;
		}
		
		/** 
		 * Number of months of a reserved instance
		 * @return months
		 */
		public int getMonths() {
			return this.reservation_period_in_months;
		}
		
		/** Return the default ReservationPeriod */
		public static ReservationPeriod getDefault() {
			return DEFAULT_RESERVATION_PERIOD;
		}
		
		/** Get a ReservationPeriod by name */
		public static ReservationPeriod get(String name) {
			for (ReservationPeriod r: ReservationPeriod.values()) {
				if (name.equals(r.getName())) {
					return r;
				}
			} 
			// we should not get here if a DemandForecaster is found with name = name
			// so, log a warning and return default
			logger.warn("ReservationPeriod with name '"+name+ "' does not exist. Returning default: " + DEFAULT_RESERVATION_PERIOD);
			return DEFAULT_RESERVATION_PERIOD;
		}		
	}
	
	private static final String XML_ELEMENT_NAME = "broker";
	
	public static String NUM_AGENTS_XML_TAG = "agents";
	public static String NUM_EACH_USER_XML_TAG = "numEachUser";
	public static String VARIANCE_XML_TAG = "variance";
	public static String LEARING_PERIOD_XML_TAG = "learningPeriod";
	public static String K_XML_TAG = "k";
	public static String COST_FACTOR_XML_TAG = "costFactor";
	public static String MRU_XML_TAG = "mru";
	public static String DEMAND_PROFILE_XML_TAG = "demandProfile";
	public static String ADAPT_THRESHOLD_XML_TAG = "adapt";
	public static String ADAPT_MOMENTUM_XML_TAG = "momentum";
	public static String ADAPT_ALPHA_XML_TAG = "alpha";
	public static String MARKET_SHOCK_XML_TAG = "marketShock";
	public static String MARKET_SHOCK_MONTH_XML_TAG = "marketShockMonth";
	public static String MARKET_SHOCK_PROFILE_XML_TAG = "marketShockProfile";
		
	private static int DEFAULT_NUM_AGENTS = 4;	
	private static int DEFAULT_NUM_OF_EACH_USER = 10; 
	private static double DEFAULT_VARIANCE = 0.1;
	private static int DEFAULT_LEARNING_PERIOD = 36;
	private static double DEFAULT_K = 1.5;
	private static double DEFAULT_COST_FACTOR = 35;
	private static double DEFAULT_MRU = 0.8;
	private static boolean DEFAULT_ADAPT = false;
	private static double DEFAULT_ADAPT_MOMENTUM = 0.3;
	private static double DEFAULT_ADAPT_ALPHA = 0.2;
	private static int DEFAULT_DEMAND_PROFILE = 40;
	private static boolean DEFAULT_MARKET_SHOCK = false;
	private static int DEFAULT_MARKET_SHOCK_MONTH = 0;
	private static int DEFAULT_MARKET_SHOCK_PROFILE = 0;
	
	/** Implement the 'payment bug' in: 
	 * 
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * The 'bug' is described in:
	 * 
	 * J. Cartlidge & P. Clamp (Submitted) "Correcting a financial brokerage model 
	 * for cloud computing: the commercialisation window of opportunity has closed."
	 * Journal of Cloud Computing: Advances, Systems and Applications (2013)
	 * 
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," 
	 * Masters dissertation, Department of Computer Science, University of Bristol, UK.
	 * 
	 * Description of 'the bug':
	 * 
	 * When the number of instances demanded, D, is greater than the reservation capacity, R,
	 * the Broker is charged for the additional on-demand instances D-R. However, the broker
	 * is *not* charged for the use of the reserved instances, hence is *undercharged* by:
	 * R*reservedMonthlyPrice.
	 * 
	 * The 'bug' is included as a switch in the code to enable replication of the results
	 * presented in Rogers & Cliff (2012)
	 */
	public static String ROGERS_CLIFF_2012_PAYMENT_BUG = "RC_Payment_Bug";
	public static boolean DEFAULT_ROGERS_CLIFF_2012_PRICING_BUG = false;
	protected boolean rc_payment_bug = DEFAULT_ROGERS_CLIFF_2012_PRICING_BUG;
	
	/**
	 * Implement the 'reservations bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2).
	 * 
	 * 'The bug' is described in:
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * On page 26, "algorithm 2" presents pseudo-code of the R&C implementation. Pages 27 and 28 then describe the
	 * implementation with a numerical example. Page 33 then explains:
	 * 
	 *  "Analysis of the algorithm reveals what may be a mistake - line 22 (Algorithm 2, Page 26) involves adding a
	 *  'free' reservation upon the purchase of a reservation. However, the model suggests that the reservation being purchased 
	 *  would simply be added to the total capacity and would not be 'free' as suggested. This leads to much fewer reservations
	 *  being purchased than one would have initially expected. While this may indeed by a 'bug', it does not necessarily
	 *  invalidate the results as it simply means that the number of reservations purchased is just a function of the number of
	 *  instances to hedge, most likely offsetting the time at which instances are purchased. Because this is only a minor wrinkle
	 *  and doesn't invalidate the model, the algorithm was kept in the original form for experimentation." Clamp (2013, page 33)
	 *  
	 * Thus, for each new reservation that will be purchased, the "following" unit of demand is also expected to use this reservation.
	 * This is clearly a bug in the work of Rogers & CLiff (2012). For the reasons given above it is also replicated in Clamp 2013.
	 * 
	 * To 'fix' the bug, change line 21 of algorithm 2 on page 26 to only increment reservations for months 2 or more ahead (i.e., *not* next month).
	 * This then stops the next demanded unit from being 'swallowed' by the new instance to reserve.
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_reservations_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012
	 * If 'implement_reservations_bug = false', then *do not* increment next months free reservations when a new instance will be purchased. Only increment 2 or more months ahead.
	 */	
	public static String ROGERS_CLIFF_2012_RESERVATIONS_BUG = "RC_Reservations_Bug";
	public static boolean DEFAULT_ROGERS_CLIFF_2012_RESERVATIONS_BUG = false;
	protected boolean rc_reservations_bug = DEFAULT_ROGERS_CLIFF_2012_RESERVATIONS_BUG;
	
	/**
	 * Implement the 'deficit bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * Pseudo-code of Rogers & CLiff (2012) implementation is detailed in:
	 * "Algorithm 2", page 26, of P. J. Clamp, (2013), 
	 * "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * We see on line 12 that 'deficit' is calculated as the different between forecast demand and future capacity.
	 * However, the algorithm shows that although the new reservations to be purchased are added to the "freeReservations"
	 * array, they are not immediately added to the "capacity" array (this only happens once the new reservations are purchased from 
	 * the provider. Hence, there is a 'bug' on line 12 of Algorithm 2, page 26, where deficit = demand - capacity. 
	 * Actually, this *should* be deficit = demand - freeReservations, since capacity does not vary during the loop
	 * (it is only incremented to equal freeReservations once the loop has completed and reserved instances are purchased from
	 * the provider).
	 * 
	 * Example:
	 * 
	 * numHedge = 4
	 * freeReservations (next month) = 1
	 * demand = [3, 4, 5]
	 * capacity = [3, 3, 3]
	 * freeReservations = capacity  = [3, 3, 3]
	 * MRU = 0.5
	 * 
	 * first iteration:
	 * 	the first unit demanded is covered by the freeReservation available. Now numHedge=3, freeReservations=0
	 * second iteration:
	 *  there are no reservations available for the following month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3> 0.5, the broker will reserve a new instance, so now: 
	 *  freeReservations = [4, 4, 4]
	 * third iteration:
	 *  Again, there are no reservations available next month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3 > 0.5, the broker *will* reserve a new instance.
	 *  
	 *  However, the broker has already decided to purchase a new instance in the second iteration, so in actual fact, to calculate expected marginal
	 *  utilisation of a new reservation purchase, the broker should perform the following calculation:
	 *  Deficit = demand - freeReservations = [3, 4, 5] - [4, 4, 4] = [-1, 0, 1], therefore months deficit = 1/3 
	 *  Since 1/3 < 0.5, the broker *will not* reserve a new instance.
	 * 
	 * fourth iteration:
	 *  once again, using 'capacity' to calculate deficit will lead to another reservation purchase. However, using 'freeReservations' will *not*
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_deficit_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012, i.e., 'deficit = demand - capacity'
	 * If 'implement_deficit_bug = false', then 'deficit' is calculated using 'deficit = demand - freeReservations'. This 
	 * approach considers new reserved instances the broker is going to purchase this month when calculating the marginal resource 
	 * utilisation of further purchases.
	 */
	public static String ROGERS_CLIFF_2012_DEFICIT_BUG = "RC_Deficit_Bug";
	public static boolean DEFAULT_ROGERS_CLIFF_2012_DEFICIT_BUG = false;
	protected boolean rc_deficit_bug = DEFAULT_ROGERS_CLIFF_2012_DEFICIT_BUG;
	
	protected int numAgents = DEFAULT_NUM_AGENTS;
	protected int numEachUser = DEFAULT_NUM_OF_EACH_USER; 
	protected double variance = DEFAULT_VARIANCE;
	protected int learningPeriod = DEFAULT_LEARNING_PERIOD;
	protected double k = DEFAULT_K;
	protected double mru = DEFAULT_MRU;
	protected boolean adapt = DEFAULT_ADAPT;
	protected double adaptMomentum = DEFAULT_ADAPT_MOMENTUM;
	protected double adaptAlpha = DEFAULT_ADAPT_ALPHA;
	protected double costFactor = DEFAULT_COST_FACTOR;
	protected int demandProfile = DEFAULT_DEMAND_PROFILE;
	protected boolean marketShock = DEFAULT_MARKET_SHOCK;
	protected int marketShockMonth = DEFAULT_MARKET_SHOCK_MONTH;
	protected int marketShockProfile = DEFAULT_MARKET_SHOCK_PROFILE;
	protected DemandForecaster demandForecaster = DemandForecaster.getDefault();
	protected ReservationPeriod reservationPeriod = ReservationPeriod.getDefault();
	
	public BrokerModuleConfigParams(int numAgents, int numEachUser, double variance, int learningPeriod, double k, double costFactor, double mru, boolean adapt, double adaptMomentum, double adaptAlpha,
			int demandProfile, boolean marketShock, int marketShockMonth, int marketShockProfile) {
		this.numAgents = numAgents;
		this.numEachUser = numEachUser;
		this.variance = variance;
		this.learningPeriod = learningPeriod;
		this.k = k;
		this.costFactor = costFactor;
		this.mru = mru;
		this.adapt = adapt;
		this.adaptMomentum = adaptMomentum;
		this.adaptAlpha = adaptAlpha;
		this.demandProfile = demandProfile;
		this.marketShock = marketShock;
		this.marketShockMonth = marketShockMonth;
		this.marketShockProfile = marketShockProfile;
	}
	
	public DemandForecaster getDemandForecaster() {
		return demandForecaster;
	}
	
	/**
	 * Set the DemandForecaster using the name string of the DemandForecaster 
	 * @param name - name string
	 */
	public void setDemandForecaster(String name) {
		this.demandForecaster = DemandForecaster.get(name);
		logger.info("DemandForecaster is now: " + demandForecaster);
	}
	
	public ReservationPeriod getReservationPeriod() {
		return reservationPeriod;
	}
	
	/**
	 * Set the ReservationPeriod using the name string of the ReservationPeriod
	 * @param name - name string
	 */
	public void setReservationPeriod(String name) {
		this.reservationPeriod = ReservationPeriod.get(name);
		logger.info("ReservationPeriod is now: " + reservationPeriod);
	}
	
	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}

	public static BrokerModuleConfigParams getDefault() {
		return new BrokerModuleConfigParams(
				DEFAULT_NUM_AGENTS,
				DEFAULT_NUM_OF_EACH_USER,
				DEFAULT_VARIANCE,
				DEFAULT_LEARNING_PERIOD,
				DEFAULT_K,
				DEFAULT_COST_FACTOR,
				DEFAULT_MRU,
				DEFAULT_ADAPT,
				DEFAULT_ADAPT_MOMENTUM,
				DEFAULT_ADAPT_ALPHA,
				DEFAULT_DEMAND_PROFILE,
				DEFAULT_MARKET_SHOCK,
				DEFAULT_MARKET_SHOCK_MONTH,
				DEFAULT_MARKET_SHOCK_PROFILE);
	}

	@Override
	public Element getXML() {
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);	
		e.setAttribute(NUM_AGENTS_XML_TAG, String.valueOf(numAgents));
		e.setAttribute(NUM_EACH_USER_XML_TAG, String.valueOf(numEachUser));
		e.setAttribute(VARIANCE_XML_TAG, String.valueOf(variance));
		e.setAttribute(LEARING_PERIOD_XML_TAG, String.valueOf(learningPeriod));
		e.setAttribute(K_XML_TAG, String.valueOf(k));
		e.setAttribute(COST_FACTOR_XML_TAG, String.valueOf(costFactor));
		e.setAttribute(MRU_XML_TAG, String.valueOf(mru));
		e.setAttribute(ADAPT_THRESHOLD_XML_TAG, String.valueOf(adapt));
		e.setAttribute(ADAPT_MOMENTUM_XML_TAG, String.valueOf(adaptMomentum));
		e.setAttribute(ADAPT_ALPHA_XML_TAG, String.valueOf(adaptAlpha));
		e.setAttribute(DEMAND_PROFILE_XML_TAG, String.valueOf(demandProfile));
		e.setAttribute(MARKET_SHOCK_XML_TAG, String.valueOf(marketShock));
		e.setAttribute(MARKET_SHOCK_MONTH_XML_TAG, String.valueOf(marketShockMonth));
		e.setAttribute(MARKET_SHOCK_PROFILE_XML_TAG, String.valueOf(marketShockProfile));
		return e;
	}

	@Override
	public void updateUsingXML(Element e) 
	{
		try
		{
			numAgents = Integer.parseInt(e.getAttributeValue(NUM_AGENTS_XML_TAG));
			numEachUser = Integer.parseInt(e.getAttributeValue(NUM_EACH_USER_XML_TAG));
			variance = Double.parseDouble(e.getAttributeValue(VARIANCE_XML_TAG));
			learningPeriod = Integer.parseInt(e.getAttributeValue(LEARING_PERIOD_XML_TAG));
			k = Double.parseDouble(e.getAttributeValue(K_XML_TAG));
			costFactor = Double.parseDouble(e.getAttributeValue(COST_FACTOR_XML_TAG));
			mru = Double.parseDouble(e.getAttributeValue(MRU_XML_TAG));
			demandProfile = Integer.parseInt(e.getAttributeValue(DEMAND_PROFILE_XML_TAG));
			marketShock = Boolean.parseBoolean(e.getAttributeValue(MARKET_SHOCK_XML_TAG));
			marketShockMonth = Integer.parseInt(e.getAttributeValue(MARKET_SHOCK_MONTH_XML_TAG));
			marketShockProfile = Integer.parseInt(e.getAttributeValue(MARKET_SHOCK_PROFILE_XML_TAG));
		}
		catch (NumberFormatException ex)
		{
			logger.warn("Broker Module ConfigParams attribute missing or invalid");
		}
	}
	
	public String toString() {
		String s = "BrokerModuleConfigParams [";
		s+= NUM_AGENTS_XML_TAG+"='" + numAgents + 
				"', ";
		s+= NUM_EACH_USER_XML_TAG+"='" + numEachUser + 
				"', ";
		s+= VARIANCE_XML_TAG+"='" + variance + 
				"', ";
		s+= LEARING_PERIOD_XML_TAG+"='" + learningPeriod + 
				"', ";
		s+= K_XML_TAG+"='" + k + 
				"', ";
		s+= COST_FACTOR_XML_TAG+"='" + costFactor +
				"', ";
		s+= MRU_XML_TAG+"='" + mru +
				"', ";
		s+= ADAPT_THRESHOLD_XML_TAG+"='" + adapt +
				"', ";
		s+= ADAPT_MOMENTUM_XML_TAG+"='" + adaptMomentum +
				"', ";
		s+= ADAPT_ALPHA_XML_TAG+"='" + adaptAlpha +
				"', ";
		s+= DEMAND_PROFILE_XML_TAG+"='" + demandProfile +
				"', ";
		s+= MARKET_SHOCK_XML_TAG+"='" + marketShock +
				"', ";
		s+= MARKET_SHOCK_MONTH_XML_TAG+"='" + marketShockMonth +
				"', ";
		s+= MARKET_SHOCK_PROFILE_XML_TAG+"='" + marketShockProfile +
				"']";
		return s;
	}
	
	@Override
	public void clone(ModuleParamsInterface params) {
		if(params.getClass().equals(this.getClass())) {
			//copy each parameter
			this.numAgents = ((BrokerModuleConfigParams) params).numAgents;
			this.numEachUser = ((BrokerModuleConfigParams) params).numEachUser;
			this.variance = ((BrokerModuleConfigParams) params).variance;
			this.learningPeriod = ((BrokerModuleConfigParams) params).learningPeriod;
			this.k = ((BrokerModuleConfigParams) params).k;
			this.costFactor = ((BrokerModuleConfigParams) params).costFactor;
			this.mru = ((BrokerModuleConfigParams) params).mru;
			this.adapt = ((BrokerModuleConfigParams) params).adapt;
			this.adaptMomentum = ((BrokerModuleConfigParams) params).adaptMomentum;
			this.adaptAlpha = ((BrokerModuleConfigParams) params).adaptAlpha;
			this.demandProfile = ((BrokerModuleConfigParams) params).demandProfile;
			this.marketShock = ((BrokerModuleConfigParams) params).marketShock;
			this.marketShockMonth = ((BrokerModuleConfigParams) params).marketShockMonth;
			this.marketShockProfile = ((BrokerModuleConfigParams) params).marketShockProfile;
			
			this.demandForecaster = ((BrokerModuleConfigParams) params).demandForecaster;
			this.reservationPeriod = ((BrokerModuleConfigParams) params).reservationPeriod;
		} else {
			logger.warn("Ignoring changes: Attempting to clone parameters of incorrect class: " + params.getClass());
		}	
	}	
	
	public int getNumAgents() {
		return numAgents;
	}
	
	public int getNumEachUser()
	{
		return numEachUser;
	}

	public double getVariance()
	{
		return variance;
	}

	/** TODO Remove learning period */
	@Deprecated
	/** TODO Remove learning period  - Replace this with ReservationPeriod.getMonths()*/
	public int getLearningPeriod()
	{
		return learningPeriod;
	}

	public double getK()
	{
		return k;
	}
	
	public double getCostFactor()
	{
		return costFactor;
	}
	
	public double getMRU()
	{
		return mru;
	}
	
	public boolean getAdapt()
	{
		return adapt;
	}
	
	public double getAdaptMomentum()
	{
		return adaptMomentum;
	}
	
	public double getAdaptAlpha()
	{
		return adaptAlpha;
	}
	
	public int getDemandProfile() {
		return demandProfile;
	}
	
	public boolean getMarketShock() {
		return marketShock;
	}
	
	public int getMarketShockMonth() {
		return marketShockMonth;
	}
	
	public int getMarketShockProfile() {
		return marketShockProfile;
	}

	public void setNumAgents(int numAgents)
	{
		this.numAgents = numAgents;
	}

	public void setNumEachUser(int numEachUser)
	{
		this.numEachUser = numEachUser;
	}

	public void setVariance(double variance)
	{
		this.variance = variance;
	}

	public void setLearningPeriod(int learningPeriod)
	{
		this.learningPeriod = learningPeriod;
	}

	public void setK(double k)
	{
		this.k = k;
	}
	
	public void setCostFactor(double costFactor)
	{
		this.costFactor = costFactor;
	}
	
	public void setMRU(double mru) 
	{
		this.mru = mru;
	}
	
	public void setAdapt(boolean adapt)
	{
		this.adapt = adapt;
	}
	
	public void setAdaptMomentum(double adaptMomentum)
	{
		this.adaptMomentum = adaptMomentum;
	}
	
	public void setAdaptAlpha(double adaptAlpha)
	{
		this.adaptAlpha = adaptAlpha;
	}
	
	public void setDemandProfile(int demandProfile)
	{
		this.demandProfile = demandProfile;
	}
	
	public void setMarketShock(boolean marketShock) {
		this.marketShock = marketShock;
	}
	
	public void setMarketShockMonth(int marketShockMonth) {
		this.marketShockMonth = marketShockMonth;
	}
	
	public void setMarketShockProfile(int marketShockProfile) {
		this.marketShockProfile = marketShockProfile;
	}
	
	/** Implement the 'payment bug' in: 
	 * 
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * The 'bug' is described in:
	 * 
	 * J. Cartlidge & P. Clamp (Submitted) "Correcting a financial brokerage model 
	 * for cloud computing: the commercialisation window of opportunity has closed."
	 * Journal of Cloud Computing: Advances, Systems and Applications (2013)
	 * 
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," 
	 * Masters dissertation, Department of Computer Science, University of Bristol, UK.
	 * 
	 * Description of 'the bug':
	 * 
	 * When the number of instances demanded, D, is greater than the reservation capacity, R,
	 * the Broker is charged for the additional on-demand instances D-R. However, the broker
	 * is *not* charged for the use of the reserved instances, hence is *undercharged* by:
	 * R*reservedMonthlyPrice.
	 * 
	 * The 'bug' is included as a switch in the code to enable replication of the results
	 * presented in Rogers & Cliff (2012)
	 */
	public void setRogersCliff2012PaymentBug(boolean setting) {
		logger.warn("Setting Rogers & Cliff (2012) 'payment bug' to " + setting);
		rc_payment_bug = setting;
	};
	
	/** Implement the 'payment bug' in: 
	 * 
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * The 'bug' is described in:
	 * 
	 * J. Cartlidge & P. Clamp (Submitted) "Correcting a financial brokerage model 
	 * for cloud computing: the commercialisation window of opportunity has closed."
	 * Journal of Cloud Computing: Advances, Systems and Applications (2013)
	 * 
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing,"
	 * Masters dissertation, Department of Computer Science, University of Bristol, UK.
	 * 
	 * Description of 'the bug':
	 * 
	 * When the number of instances demanded, D, is greater than the reservation capacity, R,
	 * the Broker is charged for the additional on-demand instances D-R. However, the broker
	 * is *not* charged for the use of the reserved instances, hence is *undercharged* by:
	 * R*reservedMonthlyPrice.
	 * 
	 * The 'bug' is included as a switch in the code to enable replication of the results
	 * presented in Rogers & Cliff (2012)
	 */
	public boolean isOnRogersCliff2012PaymentBug() {
		return rc_payment_bug;
	}
	

	/**
	 * Implement the 'reservations bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2).
	 * 
	 * 'The bug' is described in:
	 * 
	 * J. Cartlidge & P. Clamp (Submitted) "Correcting a financial brokerage model 
	 * for cloud computing: the commercialisation window of opportunity has closed."
	 * Journal of Cloud Computing: Advances, Systems and Applications (2013)
	 * 
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * On page 26, "algorithm 2" presents pseudo-code of the R&C implementation. Pages 27 and 28 then describe the
	 * implementation with a numerical example. Page 33 then explains:
	 * 
	 *  "Analysis of the algorithm reveals what may be a mistake - line 22 (Algorithm 2, Page 26) involves adding a
	 *  'free' reservation upon the purchase of a reservation. However, the model suggests that the reservation being purchased 
	 *  would simply be added to the total capacity and would not be 'free' as suggested. This leads to much fewer reservations
	 *  being purchased than one would have initially expected. While this may indeed by a 'bug', it does not necessarily
	 *  invalidate the results as it simply means that the number of reservations purchased is just a function of the number of
	 *  instances to hedge, most likely offsetting the time at which instances are purchased. Because this is only a minor wrinkle
	 *  and doesn't invalidate the model, the algorithm was kept in the original form for experimentation." Clamp (2013, page 33)
	 *  
	 * Thus, for each new reservation that will be purchased, the "following" unit of demand is also expected to use this reservation.
	 * This is clearly a bug in the work of Rogers & CLiff (2012). For the reasons given above it is also replicated in Clamp 2013.
	 * 
	 * To 'fix' the bug, change line 21 of algorithm 2 on page 26 to only increment reservations for months 2 or more ahead (i.e., *not* next month).
	 * This then stops the next demanded unit from being 'swallowed' by the new instance to reserve.
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_reservations_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012
	 * If 'implement_reservations_bug = false', then *do not* increment next months free reservations when a new instance will be purchased. Only increment 2 or more months ahead.
	 */	
	public void setRogersCliff2012ReservationsBug(boolean setting) {
		logger.warn("Setting Rogers & Cliff (2012) 'reservations bug' to " + setting);
		rc_reservations_bug = setting;
	};
	
	
	/**
	 * Implement the 'reservations bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2).
	 * 
	 * 'The bug' is described in:
	 * 
	 * J. Cartlidge & P. Clamp (Submitted) "Correcting a financial brokerage model 
	 * for cloud computing: the commercialisation window of opportunity has closed."
	 * Journal of Cloud Computing: Advances, Systems and Applications (2013)
	 * 
	 * P. J. Clamp, (2013), "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * On page 26, "algorithm 2" presents pseudo-code of the R&C implementation. Pages 27 and 28 then describe the
	 * implementation with a numerical example. Page 33 then explains:
	 * 
	 *  "Analysis of the algorithm reveals what may be a mistake - line 22 (Algorithm 2, Page 26) involves adding a
	 *  'free' reservation upon the purchase of a reservation. However, the model suggests that the reservation being purchased 
	 *  would simply be added to the total capacity and would not be 'free' as suggested. This leads to much fewer reservations
	 *  being purchased than one would have initially expected. While this may indeed by a 'bug', it does not necessarily
	 *  invalidate the results as it simply means that the number of reservations purchased is just a function of the number of
	 *  instances to hedge, most likely offsetting the time at which instances are purchased. Because this is only a minor wrinkle
	 *  and doesn't invalidate the model, the algorithm was kept in the original form for experimentation." Clamp (2013, page 33)
	 *  
	 * Thus, for each new reservation that will be purchased, the "following" unit of demand is also expected to use this reservation.
	 * This is clearly a bug in the work of Rogers & CLiff (2012). For the reasons given above it is also replicated in Clamp 2013.
	 * 
	 * To 'fix' the bug, change line 21 of algorithm 2 on page 26 to only increment reservations for months 2 or more ahead (i.e., *not* next month).
	 * This then stops the next demanded unit from being 'swallowed' by the new instance to reserve.
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_reservations_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012
	 * If 'implement_reservations_bug = false', then *do not* increment next months free reservations when a new instance will be purchased. Only increment 2 or more months ahead.
	 */	
	public boolean isOnRogersCliff2012ReservationsBug() {
		return rc_reservations_bug;
	}
	
	/**
	 * Implement the 'deficit bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * Pseudo-code of Rogers & CLiff (2012) implementation is detailed in:
	 * "Algorithm 2", page 26, of P. J. Clamp, (2013), 
	 * "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * We see on line 12 that 'deficit' is calculated as the different between forecast demand and future capacity.
	 * However, the algorithm shows that although the new reservations to be purchased are added to the "freeReservations"
	 * array, they are not immediately added to the "capacity" array (this only happens once the new reservations are purchased from 
	 * the provider. Hence, there is a 'bug' on line 12 of Algorithm 2, page 26, where deficit = demand - capacity. 
	 * Actually, this *should* be deficit = demand - freeReservations, since capacity does not vary during the loop
	 * (it is only incremented to equal freeReservations once the loop has completed and reserved instances are purchased from
	 * the provider).
	 * 
	 * Example:
	 * 
	 * numHedge = 4
	 * freeReservations (next month) = 1
	 * demand = [3, 4, 5]
	 * capacity = [3, 3, 3]
	 * freeReservations = capacity  = [3, 3, 3]
	 * MRU = 0.5
	 * 
	 * first iteration:
	 * 	the first unit demanded is covered by the freeReservation available. Now numHedge=3, freeReservations=0
	 * second iteration:
	 *  there are no reservations available for the following month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3> 0.5, the broker will reserve a new instance, so now: 
	 *  freeReservations = [4, 4, 4]
	 * third iteration:
	 *  Again, there are no reservations available next month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3 > 0.5, the broker *will* reserve a new instance.
	 *  
	 *  However, the broker has already decided to purchase a new instance in the second iteration, so in actual fact, to calculate expected marginal
	 *  utilisation of a new reservation purchase, the broker should perform the following calculation:
	 *  Deficit = demand - freeReservations = [3, 4, 5] - [4, 4, 4] = [-1, 0, 1], therefore months deficit = 1/3 
	 *  Since 1/3 < 0.5, the broker *will not* reserve a new instance.
	 * 
	 * fourth iteration:
	 *  once again, using 'capacity' to calculate deficit will lead to another reservation purchase. However, using 'freeReservations' will *not*
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_deficit_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012, i.e., 'deficit = demand - capacity'
	 * If 'implement_deficit_bug = false', then 'deficit' is calculated using 'deficit = demand - freeReservations'. This 
	 * approach considers new reserved instances the broker is going to purchase this month when calculating the marginal resource 
	 * utilisation of further purchases.
	 */
	public void setRogersCliff2012DeficitBug(boolean setting) {
		logger.warn("Setting Rogers & Cliff (2012) deficit 'bug' to " + setting);
		rc_deficit_bug = setting;
	};
	
	/**
	 * Implement the 'deficit bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * Pseudo-code of Rogers & CLiff (2012) implementation is detailed in:
	 * "Algorithm 2", page 26, of P. J. Clamp, (2013), 
	 * "Pricing The Cloud: An Investigation into Financial 
	 * Brokerage for Cloud Computing," Masters dissertation, 
	 * Department of Computer Science, University of Bristol, UK.
	 * 
	 * We see on line 12 that 'deficit' is calculated as the different between forecast demand and future capacity.
	 * However, the algorithm shows that although the new reservations to be purchased are added to the "freeReservations"
	 * array, they are not immediately added to the "capacity" array (this only happens once the new reservations are purchased from 
	 * the provider. Hence, there is a 'bug' on line 12 of Algorithm 2, page 26, where deficit = demand - capacity. 
	 * Actually, this *should* be deficit = demand - freeReservations, since capacity does not vary during the loop
	 * (it is only incremented to equal freeReservations once the loop has completed and reserved instances are purchased from
	 * the provider).
	 * 
	 * Example:
	 * 
	 * numHedge = 4
	 * freeReservations (next month) = 1
	 * demand = [3, 4, 5]
	 * capacity = [3, 3, 3]
	 * freeReservations = capacity  = [3, 3, 3]
	 * MRU = 0.5
	 * 
	 * first iteration:
	 * 	the first unit demanded is covered by the freeReservation available. Now numHedge=3, freeReservations=0
	 * second iteration:
	 *  there are no reservations available for the following month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3> 0.5, the broker will reserve a new instance, so now: 
	 *  freeReservations = [4, 4, 4]
	 * third iteration:
	 *  Again, there are no reservations available next month, so calculate the expected marginal utilisation of a new reservation purchase.
	 *  Deficit = demand - capacity = [3, 4, 5] - [3, 3, 3] = [0, 1, 2], therefore months deficit = 2/3 
	 *  Since 2/3 > 0.5, the broker *will* reserve a new instance.
	 *  
	 *  However, the broker has already decided to purchase a new instance in the second iteration, so in actual fact, to calculate expected marginal
	 *  utilisation of a new reservation purchase, the broker should perform the following calculation:
	 *  Deficit = demand - freeReservations = [3, 4, 5] - [4, 4, 4] = [-1, 0, 1], therefore months deficit = 1/3 
	 *  Since 1/3 < 0.5, the broker *will not* reserve a new instance.
	 * 
	 * fourth iteration:
	 *  once again, using 'capacity' to calculate deficit will lead to another reservation purchase. However, using 'freeReservations' will *not*
	 *  
	 * Thus, here we use a switch: 
	 * If 'implement_deficit_bug = true', then the implementation is an exact replication of Rogers & Cliff 2012, i.e., 'deficit = demand - capacity'
	 * If 'implement_deficit_bug = false', then 'deficit' is calculated using 'deficit = demand - freeReservations'. This 
	 * approach considers new reserved instances the broker is going to purchase this month when calculating the marginal resource 
	 * utilisation of further purchases.
	 */
	public boolean isOnRogersCliff2012DeficitBug() {
		return rc_deficit_bug;
	}
}
