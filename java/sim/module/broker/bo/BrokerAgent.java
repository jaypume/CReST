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
package sim.module.broker.bo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import sim.event.StopSimEvent;
import sim.module.Module;
import sim.module.broker.BrokerModuleRunner;
import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.broker.configparams.BrokerModuleConfigParams.DemandForecaster;
import sim.module.broker.configparams.BrokerModuleConfigParams.ReservationPeriod;
import sim.module.broker.event.BrokerExecuteEvent;
import sim.module.broker.event.BrokerReserveEvent;
import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.log.LogManager;
import sim.module.pricing.bo.PriceType;
import sim.module.pricing.bo.Quote;
import sim.module.pricing.configparams.PricingModuleConfigParams;
import sim.module.pricing.event.QuoteRequestEvent;
import sim.module.service.bo.Service;
import sim.module.service.event.ServiceStartEvent;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

public class BrokerAgent {
	public static Logger logger = Logger.getLogger(BrokerAgent.class);
	
	DecimalFormat format = new DecimalFormat("#.###");
	boolean firstReservation = true; 
	
	/** Method used to forecast future demand **/
	private DemandForecaster demandForecaster;
			
	private boolean AGGRESSIVE;

	private boolean ADAPT;
	private double momentum = 0.3;
	private double alpha = 0.25;
	private double delta;
	private double maxTarget;
	private double minTarget;
	private ArrayList<Double> previousThresholds;
	
	private String name;
	private double balance; //monetary balance 
//	private double assets;  //assets value ('retail value' of reserved instances owned)
	private double k;
	private double mruThreshold;
	private double costFactor;
	private int yearlySummedCapacity;
	/** TODO Replace learningPeriod with ReservationPeriod.getMonths() */
	private int learningPeriod;
	private double variance;
	
	//private int reservationMonths; //reserved instance length in months
	private ReservationPeriod reservationPeriod; //reserved instance reservation period
	
	private List<Double> previousDemand;
	private List<Integer> futureCapacity;
	private List<Integer> freeReservations;
	/**
	 * Purchase history (per month) of reservations
	 */
	private List<Integer> reservationPurchases;  
	
	private double yearlyProbability;
	private int yearlyReservationsMade;
	private int yearlyReservationsRequired;
	private int yearlyOnDemand;
	
	private int demandCounter;
	private double reserveMonthlyCost;
//	private double lastestReservationUpfrontCost = 0;
	//private double onDemandMonthlyCost;
	
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
	 * Description of 'the bug': (Clamp, 2013: page 38, section 3.5, bullet point 2)
	 * 
	 * When the number of instances demanded, D, is greater than the reservation capacity, R,
	 * the Broker is charged for the additional on-demand instances D-R. However, the broker
	 * is *not* charged for the use of the reserved instances, hence is *undercharged* by:
	 * R*reservedMonthlyPrice.
	 * 
	 * The 'bug' is included as a switch in the code to enable replication of the results
	 * presented in Rogers & Cliff (2012)
	 */
	private boolean implement_payment_bug = false;
	
	
	/**
	 * Implement the 'deficit bug' in:
	 *
	 * Rogers & Cliff (2012) "A financial brokerage for cloud computing", 
	 * Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)
	 * 
	 * ** NOTE: THIS IS A P. CLAMP 'BUG', NOT A ROGERS & CLIFF BUG **
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
	private boolean implement_deficit_bug = false;
		
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
	private boolean implement_reservations_bug = false;
	
	
	public BrokerAgent(String name) {
		logger.info("Building BrokerAgent " + name);
		this.name = name;
		this.AGGRESSIVE = false;
		this.balance = 0.0;
//		this.assets = 0;
		this.yearlySummedCapacity = 0;
		this.ADAPT = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getAdapt();
		this.momentum = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getAdaptMomentum();
		this.alpha = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getAdaptAlpha();
		this.k = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getK();
		this.costFactor = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getCostFactor();
		this.mruThreshold = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getMRU();
		//this.learningPeriod = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getLearningPeriod();

		this.variance = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getVariance();
		this.maxTarget = 1.0;
		this.minTarget = 0.0;
		this.delta = 0.0;
		this.previousThresholds = new ArrayList<Double>();
		previousThresholds.add(mruThreshold);
		this.previousDemand = new ArrayList<Double>();
		this.futureCapacity = new ArrayList<Integer>();
		this.freeReservations = new ArrayList<Integer>();
		this.reservationPurchases = new ArrayList<Integer>(); // history of reservation purchases
		this.yearlyReservationsRequired = 0;
		this.yearlyOnDemand = 0;
		this.yearlyReservationsMade = 0;
		this.demandCounter = 0;
		this.reservationPeriod = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getReservationPeriod();
		this.learningPeriod = reservationPeriod.getMonths();
		for (int i = 0; i < BrokerModuleRunner.getInstance().getSimulationLength() + learningPeriod; i++) {
			previousDemand.add(0.0);
			futureCapacity.add(0);
			freeReservations.add(0);
			reservationPurchases.add(0);
		}
		
		// Do we implement the 'payment bug' of Rogers & Cliff (2012)? i.e., where the Broker fails to pay for monthly usage of some reserved instances
		this.implement_payment_bug = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).isOnRogersCliff2012PaymentBug();
		
		// Do we implement the 'reservations bug' of Rogers & Cliff (2012)? i.e., where the latest reservation swallows two demand units
		this.implement_reservations_bug = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).isOnRogersCliff2012ReservationsBug();
		
		// Do we implement the 'free reservations bug' of Rogers & Cliff (2012)? i.e., where capacity is used to calculate deficit, not freeReservations
		this.implement_deficit_bug = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).isOnRogersCliff2012DeficitBug();
		
		// The method used to forecast future demand based on historical demand
		this.demandForecaster = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getDemandForecaster();
		
		logger.info("Created new BrokerAgent: " + this);
	}
	
	public String toString() {
		String s = "Broker Agent " + name + ": ";
		s+= "{ k=" + k + ", costFactor=" + costFactor + ", theta=" + mruThreshold + " } ";
		s+= "DemandForecaster = " + demandForecaster.getName() + ", ";
		s+= "ReservationPeriod = " + reservationPeriod + ", ";
		if(implement_payment_bug) {
			s+=" Implementing the 'payment bug' in: Rogers & Cliff (2012) 'A financial brokerage for cloud computing', "  
				+"Journal of Cloud Computing: Advances, Systems and Applications, 2012, 1 (2)";
		}
		return s;
	}
	
	/**
	 * Approaches each user and asks for their list of options requirements for next period
	 * Charges users for options
	 */
	public void chargeReservations() {
		if (firstReservation)
			firstReservation = false; //This causes us to double-log at the start of broker trading... (i.e., after the learning period)
		else
			BrokerModuleRunner.getInstance().advanceMonth();

		int month = BrokerModuleRunner.getInstance().getCurrentMonth();
		logger.debug("Month is now: " + month);
		
		if (BrokerModuleRunner.getInstance().getCurrentMonth() >= BrokerModuleRunner.getInstance().getSimulationLength() - 1) {
			logger.warn(TimeManager.log("No more demand - sending StopSimEvent..."));
			EventQueue.getInstance().addEvent(StopSimEvent.create(TimeManager.getTimeNow()));
		}
		
		logger.info("Broker is charging for reservations...");
		double totalProbability;
		
//		int numEachAgent = BrokerModuleRunner.getInstance().getNumEachAgent();
		totalProbability = 0.0;
		for (UserAgent user: BrokerModuleRunner.getInstance().getUserAgents()) {
			double p = user.getReservation();
			double fee = costFactor * (k * Math.pow(p, 2) / 2);
			user.debit(fee);
			balance += fee;
			totalProbability += p;
		}
		yearlyProbability += totalProbability;
		
		int numHedge = (int)Math.round(totalProbability);
		int reservationsMade = 0;
		
		if (ADAPT && month >= learningPeriod) {
			determineAggressiveness(month, numHedge);
			adaptThreshold(numHedge, freeReservations.get(month + 1));
			previousThresholds.add(mruThreshold);
		}
		
		// Calculate future demand forecasts...
		List<Double> forecastDemand = new ArrayList<Double>();
		if(month >= learningPeriod-1) { 
			//Get forecast of future demand...
			
			// get recent historic demand (last 36 months, including this month) subList(from index inclusive, to index exclusive)
			List<Double> historicDemand = previousDemand.subList(month-learningPeriod+1, month+1); //NOTE: The PLUS 1 is very important!!!
			
			// Now forecast future demand using historic demand data...
			switch(demandForecaster) {

			case REGRESSION_DEMAND_FORECASTER:
			{
				// TODO WARNING: REGRESSION_DEMAND_FORECASTER currently only allows 36 months historic demand, so hard code this value
				historicDemand = previousDemand.subList(month-36+1, month+1); //NOTE: The PLUS 1 is very important!!!
				forecastDemand = getForecastDemandUsingLinearRegression(historicDemand);
				break;
			}
			case ROGERS_AND_CLIFF_2012_DEMAND_FORECASTER:
			{
				forecastDemand = getForecastDemandUsingRogersAndCliff2012(historicDemand);
				break;
			}
			default:
			{
				// we should *never* get here, unless there are demand forecasters in the enumeration that we haven't caught in the switch statement
				logger.fatal("We have an unknown Demand Forecaster: " + demandForecaster + ". Exiting system.");
				System.exit(-1);
				break; //redundant
			}
			}

			logger.debug("Future capacity = " + futureCapacity.subList(month+1, month+1+learningPeriod));
		}
		
		/**
		 * This is the implementation of the Reservations Hedging algorithm 2, presented in:
		 *  P J Clamp (2013) "Pricing the cloud", masters thesis, page 26.
		 */
		logger.info("Total number to hedge = " + numHedge);
		
	
		// Do we have enough free reservations to cover demand?
		if(numHedge <= freeReservations.get(month + 1)) {
			// we have enough free reservations to cover demand, do nothing...
			logger.info("NumHedge = " + numHedge + " can be covered by free reservations = " + freeReservations.get(month + 1) + " so not reserving any more...");
		} else {
			// we do not have enough free reservations to cover demand, so decide whether we need to reserve new instances...
			
			//firstly, fill up all free reservations...
			logger.info("Free reservations next month = " + freeReservations.get(month + 1) + ", using these for hedging...");
			numHedge -= freeReservations.get(month + 1);
			freeReservations.set(month + 1, 0);
			
			for (int i = 0; i < numHedge; i++) {
				logger.debug("Hedging " + i + "/" + numHedge);
				logger.debug("Free reservations \t= " + freeReservations.subList(month+1, month+1+learningPeriod));
				logger.debug("Future capacity   \t= " + futureCapacity.subList(month+1, month+1+learningPeriod));
				if (freeReservations.get(month + 1) >= 1) {
					logger.debug("Free reservations next month = " + freeReservations.get(month + 1));
					freeReservations.set(month + 1, freeReservations.get(month + 1) - 1);
					logger.debug("Using one of next months free reservations for current demand unit. Now free reservations next month = " + freeReservations.get(month + 1));
				} else {
					logger.debug("Free reservations next month = " + freeReservations.get(month + 1));
					double mru = 0.0;
					if (month >= learningPeriod-1) {
						
						// Calculate expected monthly deficits using a demand forecast... 
						
						int monthsDeficit = 0;
						
						if(implement_deficit_bug) {
							
							// Rogers & Cliff (2012) Calculate 'deficit' using future capacity (i.e., reservations already owned)...
							monthsDeficit = this.getMonthsWithForecastDeficit(forecastDemand, futureCapacity.subList(month+1, month+1+learningPeriod));						
							logger.debug("MRU using capacity (the 'deficit bug') = " + monthsDeficit/(double)learningPeriod);
						} else {
	
							// The new way - calculate 'deficit' using freeReservations array rather than capacity, (i.e., include reservations that we intend to purchase)...
							monthsDeficit = this.getMonthsWithForecastDeficit(forecastDemand, freeReservations.subList(month+1, month+1+learningPeriod));
							logger.debug("MRU using freeReservations      = " + monthsDeficit / (double)learningPeriod);
						}
	
						mru = monthsDeficit / (double)learningPeriod;
						
	
	//					//THE OLD LOGIC -- TODO REMOVE
	//					int monthsDeficit = 0;
	//					if (use_RC_2012_demand_forecast) {
	//
	//						// Use Rogers & Cliff (2012) method, such that: 
	//						// Forecast_demand(month t) - Actual_demand(month t-36)
	//						for (int j = month - learningPeriod + 1; j < month + 1; j++) {
	//							int deficit = (int)(numEachAgent * previousDemand.get(j)) - futureCapacity.get(j + learningPeriod);
	//							if (deficit > 0) {
	//								monthsDeficit++;
	//							}
	//						}
	//						logger.info("Old way: deficit = " + monthsDeficit);
	//						
	//						if(monthsDeficit != this.getMonthsWithForecastDefecit(forecastDemand, futureCapacity.subList(month+1, month+1+learningPeriod))) {
	//							logger.error("Old way different answer to new way!");
	//							logger.error("New way answer = " + this.getMonthsWithForecastDefecit(forecastDemand, futureCapacity.subList(month+1, month+1+learningPeriod)));
	//							System.exit(-1);
	//						}
	//						monthsDeficit = this.getMonthsWithForecastDefecit(forecastDemand, futureCapacity.subList(month+1, month+1+learningPeriod));
	//						logger.info("New way: deficit = " + monthsDeficit);
	//						
	//						
	//						mru = monthsDeficit / (double)learningPeriod;
	//						
	//					} else {
	//						// Use linear regression for trend forecast...
	//						//TODO monthsDeficit = getForecastDeficitUsingLinearRegression();
	//					}
					}
					if (mru > mruThreshold) {
						reservationsMade += 1;
						logger.debug("Reservations made is now = " + reservationsMade);
						
						if(implement_reservations_bug) { //the 'buggy' implementation of Rogers & CLiff (2012)
							for (int k = month + 1; k < month + learningPeriod + 1; k++) { 
								//This is the bug... since k = month + 1, we increment *next* month's free reservations, 
								//which will be 'swallowed' by the next unit of demand (and hence we 'allocate it twice')
								freeReservations.set(k, freeReservations.get(k) + 1);   
								
								// Phil Clamp's (2013, page 33) masters thesis suggests this is a bug in Rogers & Cliff (2012) implementation
								// "adding a 'free' reservation upon the purchase of a reservation. However,
								// the model suggests that the free reservation being purchased would simply be added to the total capacity 
								// and would not be 'free' as suggested. While this may indeed be a 'bug', it does not necessarily invalidate the results 
								// as it simply means that the number of reservations purchased is just a function of the number of instances to hedge, most 
								// likely off-setting the time at which instances are purchased. "
							}
							logger.debug("Next month's free reservations incremented. (the 'reservations bug')");
							
						} else { //the *correct way* Do not increment 'free' reservation for *next* month since we are going to purchase this for the demand unit we expect...
							
							/** 
							 * alternative method, to fix the original 'bug'
							 * 
							 * only add to free reservations in 2 months time (i.e., we do not increment next month's free reservations since 
							 * we already know it will be used.) Actually, what are these 'free reservations'? Shouldn't we instead be incrementing capacity?
							 * 
							 * */
							for (int k = month + 2; k < month + learningPeriod + 1; k++) { //k = month + 2 (i.e., do *not* increment a freeReservation for *next* month
								freeReservations.set(k, freeReservations.get(k) + 1);   	
							}
						}
	
						logger.debug("Free reservations have been incremented...");
	
					} else {
						logger.info("MRU = " + mru + " is <= mruThreshold = " + mruThreshold + ", so *no longer reserving more instances*");
						break;
					}
				}
			}
		}
		yearlyReservationsMade += reservationsMade;
		
		if (reservationsMade > 0) {
			logger.info("Month #" + month + " Creating " + reservationsMade + " price requests, yearly reservations made is now: " + yearlyReservationsMade);
			EventQueue.getInstance().addEvent(new QuoteRequestEvent(World.getInstance().getTime() + TimeManager.secondsToSimulationTime(1), reservationsMade, PriceType.RESERVED));
		}
			
		long executeTime = World.getInstance().getTime() + TimeManager.daysToSimulationTime(28);
		EventQueue.getInstance().addEvent(new BrokerExecuteEvent(executeTime));
	}
	
	/**
	 * Log data and reset balance if end of year. 
	 * NOTE: This should be called *first* when the month is incremented.
	 */
	public void resetBalance() {
		int month = BrokerModuleRunner.getInstance().getCurrentMonth();
		
		logger.info("Month #" + month + " Running totals for the year: {Balance="+this.balance+", "
				+ "capacity="+this.yearlySummedCapacity + ", "
				+ "reservations="+this.yearlyReservationsMade + ", "
				+ "reservationsRequired="+this.yearlyReservationsRequired + ", "
				+ "onDemand="+this.yearlyOnDemand +"}");
		
		if ((month+1) % 12 == 0 ) {

			// want to log here. 
			logPerformance();


			this.balance = 0.0;
			this.yearlySummedCapacity = 0;
			this.yearlyReservationsMade = 0;	
			this.yearlyProbability = 0.0;
			this.yearlyReservationsRequired = 0;
			this.yearlyOnDemand = 0;
			if (ADAPT && month >= learningPeriod) {
				previousThresholds.clear();
				//maxTarget = 1.0;
			}
			logger.info("Month #" + month + " Log output written and yearly counters zeroed.");
		} else {
			logger.info("Month #" + month + " Not logging this month.");
		}		
	}
	
	/**
	 * Forecast future demand using the method in Rogers & Cliff (2012): 
	 * 
	 * For each month, t, do: Forecast(t) = Demand(t-36)
	 * 
	 * i.e., forecast demand = actual demand lagged 36 months.
	 *
	 * @param - recent historical demand over the last 36 months (inclusive of *this* month)
	 * @return  demand forecast list for the next [learning period = 36] months 
	 */
	public List<Double> getForecastDemandUsingRogersAndCliff2012(List<Double> historicDemand) {
		
		List<Double> forecastDemand = new ArrayList<Double>();
		
		if(historicDemand.size()!=learningPeriod) {
			logger.warn("*** Historic data list is incorrect size. Returning zero forecast list. *** Size = " + historicDemand.size() + ", Size should be = " + learningPeriod + ", historicData: " + historicDemand);
			for(int i=0; i<learningPeriod; i++) {
				forecastDemand.add(0.0);
			}
			return forecastDemand;
		} else {		
			for (double d: historicDemand) {
				//forecast demand is equal to historic demand (1 or 3 years lag)
				forecastDemand.add(d*BrokerModuleRunner.getInstance().getNumEachAgent());
			}

			logger.debug("Forecast demand: " + forecastDemand);
			logger.debug("Previous demand: " + historicDemand);

			return forecastDemand;
		}
	}
	
	/**
	 * Forecast future deficit using linear regression forecast.
	 * 
	 * Forecast: Using the last 3 years data (36 months):
	 * 	1. Use linear regression to estimate linear trend line.
	 *  2. Calculate seasonal index (monthly residual from trend line) 
	 *  3. Use trend line to produce linear forecast
	 *  4. Add seasonal (monthly) effects to forecast demand for each future month, t: forecast(t).
	 *  
	 * @param - recent historical demand over the last 36 months (inclusive of *this* month)
	 * @return demand forecast list for the next [learning period = 36] months 
	 */
	public List<Double> getForecastDemandUsingLinearRegression(List<Double> historicDemand) {
		
		List<Double> forecastDemand = new ArrayList<Double>();
		List<Double> trendLine = new ArrayList<Double>();
		List<Double> residuals = new ArrayList<Double>();
		List<Double> monthlyResiduals = new ArrayList<Double>();
		
		if(historicDemand.size()!=learningPeriod) {
			logger.warn("*** Historic data list is incorrect size. Returning zero forecast list. *** Size = " + historicDemand.size() + ", Size should be = " + learningPeriod + ", historicData: " + historicDemand);
			for(int i=0; i<learningPeriod; i++) {
				forecastDemand.add(0.0);
			}
			return forecastDemand;
		} else {		
			
			//convert List into 2d array
			double[][] historicDataArray = new double[historicDemand.size()][2];
			int index = 0;
			for (double h: historicDemand) {
				historicDataArray[index][0] = index;
				historicDataArray[index][1] = h;
				index++;
			}
			
			logger.debug("Historic array: " + Arrays.deepToString(historicDataArray));
			
			// now do the regression...
			SimpleRegression regression = new SimpleRegression();
			regression.addData(historicDataArray);
			logger.debug("Regression: m=" + regression.getSlope() + ", c=" + regression.getIntercept() + ", cStdErr=" + regression.getInterceptStdErr());
			
			// now get the trend line & residuals
			for (int i=0; i<historicDemand.size(); i++) {
				trendLine.add( (i*regression.getSlope()) + regression.getIntercept());
				residuals.add (historicDemand.get(i) - trendLine.get(i));
			}
			logger.debug("Trend line = " + trendLine);
			logger.debug("Residuals = " + residuals);
			
			// get mean residuals per month
			for (int i=0; i<12; i++) {
				logger.debug("We are assuming that learning period is 36 months. Make this generic code!!!");
				//TODO use %12 and iterate through to make this generic
				monthlyResiduals.add( (residuals.get(i)+residuals.get(i+12)+residuals.get(i+24)) / 3 );
			}
		
			logger.debug("mean monthly residuals: " + monthlyResiduals);
			
			// now forecast using monthly residuals and trend line
			// first set forecast using trendline
			for(double t: trendLine) {
				forecastDemand.add(t);
			}
			
			// forecast using trend line plus monthly adjustment
			for(int i=0; i<12; i++) {
				//TODO: Make this code generic not fixed to 36 months
				forecastDemand.set(i, trendLine.get(i) + monthlyResiduals.get(i));
				forecastDemand.set(i+12, trendLine.get(i+12) + monthlyResiduals.get(i));
				forecastDemand.set(i+24, trendLine.get(i+24) + monthlyResiduals.get(i));
			}

			// finally, multiply normalised demand by number of agents
			
			for(int i=0; i< forecastDemand.size(); i++) {
				forecastDemand.set(i, Math.floor(forecastDemand.get(i)*BrokerModuleRunner.getInstance().getNumEachAgent()));
			}
			
			logger.debug("final forecast with seasonal adjustment: " + forecastDemand);
		
			logger.debug("RForecastDemand: " + forecastDemand);
			logger.debug("Previous demand: " + historicDemand);

			return forecastDemand;
		}
	}

	
	/**
	 * Get the number of months that have a forecast deficit, i.e., SUM_{t=1 to learningPeriod}: forecast(t) < capacity(t)
	 *
	 * @param forecastDemand - the demand forecast for each future month
	 * @param futureCapacity - the capacity available for each future month 
	 * 							[NOTE: It is better to use freeReservations rather than future capacity, since this includes
	 * 									reservations that the Broker does not *yet* own, but has already decided to reserve]
	 * 
	 * @return the number of months with an estimated deficit
	 */
	public int getMonthsWithForecastDeficit(List<Double> forecastDemand, List<Integer> futureCapacity) {
		
		//logger.debug(forecastDemand);
		//logger.debug(futureCapacity);
		
		int monthsDeficit = 0;
		//check array lengths equal (and equal to learning period)
		if(forecastDemand.size()==futureCapacity.size() && futureCapacity.size()==learningPeriod) {
			
			for(int i=0; i<learningPeriod; i++) {
				int deficit = (int)(forecastDemand.get(i) - futureCapacity.get(i));
				if(deficit > 0) {
					monthsDeficit++;
				}
			}
			
			return monthsDeficit;
		} else {
			logger.error("Array sizes unequal: forecastDemand.size()=" + forecastDemand.size() + ", futureCapacity.size()=" + futureCapacity.size() + ", learningPeriod=" + learningPeriod);
			return -1;
		}
	}
		
	/**
	 * Get the value of assets owned by the BrokerAgent
	 * 
	 * Assets value is calculated as the retail price of the reserved instances owned, multiplied by the proportion of life left on the instance
	 * 
	 * Example: A 36 month instance costs $300.00 retail. The Broker owns 1 instance with 36 months of life left and 2 instances with 12 months left.
	 * The brokers' assets are then: ( 1 * 300 * 36/36 ) + ( 2 * 300 * 12/36 ) = ( 300 ) + ( 200 ) = $500
	 * 
	 * @return The sum value of the BrokerAgent's assets
	 */
	public double getAssetsValue() {
		
		int month = BrokerModuleRunner.getInstance().getCurrentMonth();
		double assetsValue = 0.0;
		
		List<Integer> recentPurchases;
		
		// Get recent purchases
		if (month >= learningPeriod) {
			recentPurchases=reservationPurchases.subList(month-learningPeriod+1, month+1);
		} else {
			recentPurchases=reservationPurchases.subList(0, month+1);
		}
	
		logger.info("Recent purchases: " + recentPurchases);
		
		//TODO - this is hacky - we should have a better way of pricing the current assets. Ideally by asking the "market" for current value
		double latestReservedUpfrontPrice = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getReservedUpfrontPrice();
		
		// Traverse the list in reverse order
		for(int i=recentPurchases.size()-1; i>=0; i--) {
			
			int reservationLength = learningPeriod; //TODO - careful! learningPeriod does not necessarily have to be equal to reservation length
			int monthsAgo = recentPurchases.size()-i;
			int monthsRemaining = reservationLength - monthsAgo;
			double proportionRemaining = monthsRemaining / (double) reservationLength;
			// TODO NOTE: reserveMonthlyCost may vary, but for now, just use last value stored
			double valueRemaining = latestReservedUpfrontPrice * proportionRemaining * recentPurchases.get(i);
			assetsValue += valueRemaining;
			
			if(recentPurchases.get(i)>0) {
				logger.debug("months ago = "+ monthsAgo +", months remaining = "+monthsRemaining+ ", proportionRemaining = " + proportionRemaining +
					" #owned = " + recentPurchases.get(i) + ", reservedUpFrontPrice = " + latestReservedUpfrontPrice + ", value remaining = " + valueRemaining + ", assets = " + assetsValue);
			}
		}
		
		return assetsValue;
		
	}
	
	/**
	 * Approach users asking if they require resources in this period
	 * First, which options do they want to use. Secondly, how many on-demand?
	 * 
	 * The log for the broker should not be time-based but should be called from the
	 * second stage of provisioning. This is so that logs compare predicted performance
	 * (in previous period) to achieved performance (in current time period)
	 */
	public void chargeExecutions() {
		logger.info("Calling resetBalance() ..");
		resetBalance();
		BrokerModuleRunner.getInstance().advanceDemand();
		int month = BrokerModuleRunner.getInstance().getCurrentMonth();
		
		int numAgents = BrokerModuleRunner.getInstance().getNumAgents();
		int numEachAgent = BrokerModuleRunner.getInstance().getNumEachAgent();
		double fee = 0.0;
		int reservationsRequired = 0;
		int onDemandRequired = 0;
		
		logger.info("Charging for Executions...");

		for (int profile = 0; profile < numAgents; profile++) {
			double demand = BrokerModuleRunner.getInstance().getDemandList().get(profile).getCurrentDemand();
			demand = BrokerModuleRunner.getInstance().getDemandList().get(profile).randomiseDemand(demand, variance);
			updateDemand(demand);
			int numToPick = (int)(demand * numEachAgent);

			List<Integer> chosen = new ArrayList<Integer>();
			chosen.addAll(BrokerModuleRunner.getInstance().getChosen(numToPick));
			
			for (Integer i : chosen) {
				UserAgent user = BrokerModuleRunner.getInstance().getAgent((profile * numEachAgent) + i);
				double p = user.getOrder();
				fee = costFactor * (1 + (k / 2) - (k * p));
				user.debit(fee);
				balance += fee;
				reservationsRequired++;
				//TODO JPC Jul 2013: we need to start a one month instance for each user requiring it...
				//we need a Reservation class that is bought from the DC provider and stored by the broker, 
				//with a start and stop time, and a use() class
			}
		}
		yearlyReservationsRequired += reservationsRequired;
		
		int surplusReservations = futureCapacity.get(month + 1) - reservationsRequired;
		onDemandRequired = (surplusReservations < 0)? -surplusReservations : 0;
		yearlyOnDemand += onDemandRequired;
		
		if (onDemandRequired > 0) {
			logger.info("Month #" + month + ": Next months capacity = " + futureCapacity.get(month + 1) + ", Next month demand = " + reservationsRequired + ", Creating " + onDemandRequired + " on-demand Price Requests");
			//get a quote price for each on-demand instance required
			EventQueue.getInstance().addEvent(new QuoteRequestEvent(World.getInstance().getTime() + TimeManager.millisecondsToSimulationTime(1), onDemandRequired, PriceType.ONDEMAND));
		}

		logger.debug("Payment bug is: " + ((implement_payment_bug)?"on":"off"));
		
		// Implement the 'payment bug' of Rogers & Cliff (2012). Used for replication experiments. Otherwise, switch off!
		if(implement_payment_bug) {
	        if (reservationsRequired <= futureCapacity.get(month + 1)) {
	        	balance -= (reservationsRequired * reserveMonthlyCost);
	        } else {
	        	//do nothing:- We *should* be charging for all the reserved instances used this month, but 'payment bug' in Rogers & Cliff (2012) doesn't do this.
	        	logger.debug("Not charging for reserved instances used this month (the 'payment bug')");
	        }			
	        yearlySummedCapacity += futureCapacity.get(month + 1);
		} else {
			// This is the 'Fix' of the 'payment bug' - charge for all reservations used.
			// This is what we *should* be doing
	        balance -= (reservationsRequired - onDemandRequired) * reserveMonthlyCost;
			yearlySummedCapacity += futureCapacity.get(month + 1);
		}
		
		long executeTime = World.getInstance().getTime() + TimeManager.daysToSimulationTime(1);
		EventQueue.getInstance().addEvent(new BrokerReserveEvent(executeTime));
	}
	
	public void purchaseInstances(List<Quote> quotes) {
		Quote quote = getBestQuote(quotes);
		try {
			if (quote.getInstances() > 0) {
				// Charge the up-front cost.
				balance -= quote.getUpFrontCost();
				int totalReserved = 0;
				for (int instance = 0; instance < quote.getInstances(); instance++) {
					if (quote.getPriceType() == PriceType.RESERVED) {
						totalReserved++;
						reserveMonthlyCost = quote.getMonthlyCostPerInstance();
						//TODO:- Don't actually start the service in the data centre. It is just a reservation. Start when used.
						//For now, start it here - we assume that it will be running for the entire 3 year period
						ServiceStartEvent serviceEvent = ServiceStartEvent.create(World.getInstance().getTime() + TimeManager.daysToSimulationTime(28),
							    TimeManager.daysToSimulationTime(28)*learningPeriod,
							    Service.getNextServiceID(),
							    0,
							    quote.getDcID());
						
						EventQueue.getInstance().addEvent(serviceEvent);
						logger.debug("New reserved instance: " + serviceEvent);
					}
					else if (quote.getPriceType() == PriceType.ONDEMAND) {
						
						balance -= quote.getMonthlyCostPerInstance();
						ServiceStartEvent serviceEvent = ServiceStartEvent.create(World.getInstance().getTime(),
								TimeManager.daysToSimulationTime(28),
								Service.getNextServiceID(),
								0,
								quote.getDcID());
						EventQueue.getInstance().addEvent(serviceEvent);
						logger.debug("New on demand instance: " + serviceEvent);
					}
				}
				
				// Update Future Capacity.
				int month = BrokerModuleRunner.getInstance().getCurrentMonth();
				logger.debug("Going to update future capacity with the newly reserved instances = " + totalReserved);
				logger.debug("Original capacity: " + futureCapacity);
				for (int i = month + 1; i <= month + learningPeriod; i++) {
					futureCapacity.set(i, futureCapacity.get(i) + totalReserved);
				}
				logger.debug("New capacity: " + futureCapacity);
				// Update reservation purchases
				reservationPurchases.set(month+1, reservationPurchases.get(month+1) + totalReserved);
				logger.debug("Month #" + month + " - Reservations purchased history: " + reservationPurchases);
				if (quote.getPriceType() == PriceType.RESERVED) logger.debug(getReservationPurchaseHistoryAsMatrixString());
			
			}
		} catch (NullPointerException e) {
			logger.info("No quotes were chosen to start");
		}
	}
	
	/**
	 * Return reservationPurchases as a yearly matrix string - for debugging output
	 * @return
	 */
	private String getReservationPurchaseHistoryAsMatrixString() {
		
		String output = "Reservation purchase history by year and month:\n";
		int monthCounter = 1;
		int yearCounter = 1;
		int yearlyTotal = 0;
		for (int i=0; i<reservationPurchases.size(); i++) {
			if(i%12==0) {
				//next year
				if (i!=0) output += " \t[Total = " + yearlyTotal + "] \n";
				yearlyTotal=0;
				monthCounter=1;
				output += "Year " + yearCounter + " ";
				yearCounter++;
			}
			output += "["+monthCounter+"] " + reservationPurchases.get(i) + " ";
			yearlyTotal+=reservationPurchases.get(i);
			monthCounter++;
		}
		output += " \t[Total = " + yearlyTotal + "] \n";
		
		return output;
	}
	
	private void adaptThreshold(double demand, double reservations) {
		double target = reservations / (demand + 1);
		if (target > maxTarget) {
			maxTarget = target;
		} else if (target < minTarget) {
			minTarget = target;
		}
		target = (target - minTarget) / (maxTarget - minTarget);
		target = (AGGRESSIVE)? target / 2.0 : target;
		double newDelta = momentum * delta + alpha * ((1 - momentum) * (target - mruThreshold));
		double newThreshold = mruThreshold + newDelta;
		this.delta = newDelta;
		this.mruThreshold = newThreshold;
	}
	
	private void determineAggressiveness(int month, double demand) {
		int growthMonths = 0;
		double priorMonth = Double.MAX_VALUE;
		double yearAgoDemand = 0;
		for (int i = month - 11; i <= month; i++) {
			double currentMonth = previousDemand.get(i);
			if (currentMonth > priorMonth) {
				growthMonths++;
			}
			priorMonth = currentMonth;
			if (i == month - 11) {
				yearAgoDemand = currentMonth;
			}
		}
		
		if (growthMonths >= 6 && yearAgoDemand < priorMonth) {
			AGGRESSIVE = true;
		} else {
			AGGRESSIVE = false;
		}
	}
	
	private Quote getBestQuote(List<Quote> quotes) {
		Quote chosenQuote = null;
		double bestPrice = Double.MAX_VALUE;
		for (Quote quote : quotes) {
			double total = quote.getUpFrontCost() + quote.getDuration() * quote.getTotalMonthlyCost();
			if (total < bestPrice) {
				bestPrice = total;
				chosenQuote = quote;
			}
		}
		return chosenQuote;
	}
	
	public void logPerformance() {
        for (Datacentre dc : World.getInstance().getDatacentres()) {
        	Log log = new Log();
        	log.add(String.valueOf(BrokerModuleRunner.getInstance().getDemandList().get(0).getCurrentDate()));
        	log.add(String.valueOf(yearlySummedCapacity));
        	log.add(String.valueOf(format.format(yearlyProbability)));
        	log.add(String.valueOf(yearlyReservationsMade));
        	log.add(String.valueOf(yearlyReservationsRequired));
        	log.add(String.valueOf(yearlyOnDemand));
        	log.add(String.valueOf(format.format(balance)));
        	
			double tAvg = 0.0;
			for (double t : previousThresholds) {
				tAvg += t;
			}
			tAvg /= previousThresholds.size();
			
			log.add(String.valueOf(format.format(tAvg)));
			
			double assets = this.getAssetsValue();
			logger.info("Logging assets value = " + assets);
        	log.add(String.valueOf(format.format(assets)));
        	log.add(String.valueOf(format.format(assets+balance)));
        	
        	LogManager.writeLog(BrokerModuleRunner.getInstance().getLogWriter(dc.getID()), log);
        	logger.info("Log written: " + log);
        }
	}
	
	public String getName() {
		return name;
	}
	
	public void updateDemand(double demand) {
		previousDemand.set(demandCounter, demand);
		demandCounter++;
	}
}