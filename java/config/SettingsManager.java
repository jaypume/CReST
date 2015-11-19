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
package config;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.pricing.configparams.PricingModuleConfigParams;
import sim.module.sim.configparams.SimModuleConfigParams;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.module.subscriptions.topology.NetworkTopologyFactory.NetworkTopology;
import sim.module.thermal.ThermalModuleRunner;
import utility.time.LengthOfTime;
import utility.time.TimeManager;
import utility.time.TimeManager.UnitTime;

/**
 * Class to act as an interface between the config files and the simulator
 * itself.
 */
public class SettingsManager implements SettingsManagerAccess
{
	public static Logger logger = Logger.getLogger(SettingsManager.class);
	
	//TODO: JC Dec, 2011 - integrate these into configuration file eventually.  For now, these are variables that can be hardcoded.
	//Make each variable private static and add a public getter method (also need to add to SettingsManagerAccess interface)	
	//Logging	
	private static final long FIRST_LOG_EVENT_TIME = TimeManager.millisecondsToSimulationTime(1); //time of the first log
//	private static final long TIME_BETWEEN_LOGS_CONSISTENCY = TimeManager.secondsToSimulationTime(1); //every 100s
//	private static final long TIME_BETWEEN_LOGS_THERMAL = TimeManager.secondsToSimulationTime(1); //every 100s
  	private static final long TIME_BETWEEN_LOGS_NORMAL = TimeManager.daysToSimulationTime(1); //every day
	
	//GUI Updates
	private static final long TIME_OF_FIRST_GUI_UPDATE = TimeManager.millisecondsToSimulationTime(1);
	private static final long TIME_BETWEEN_GUI_UPDATES_CONSISTENCY = TimeManager.secondsToSimulationTime(1);
	private static final long TIME_BETWEEN_GUI_UPDATES_NORMAL = TimeManager.daysToSimulationTime(1);

	//Time Series Graphs -- time units (most specific to most general)
	private static final TimeManager.UnitTime TIME_UNITS_THERMAL_TIME_SERIES_GRAPH = TimeManager.UnitTime.SECOND;
	private static final TimeManager.UnitTime TIME_UNITS_CONSISTENCY_GRAPH = TimeManager.UnitTime.SECOND;
	private static final TimeManager.UnitTime TIME_UNITS_TIME_SERIES_GRAPH = TimeManager.UnitTime.DAY;

	
	///// END OF LATEST SETTINGS MANAGER HARD-CODED VALUES ///
	

    // Singleton instance.
    private static SettingsManagerAccess instance         = null;

    //TODO JC, Jan 2012 - clean this up with more smaller ConfigParam classes, such as SubscriptionsConfigparams
    //One Params class for each module would make the most sense, I think
    //JC, May 2012 -- There is now a structure in place for multiple Config classes for each Module...
    //JC, June 2012 -- Deprecate this class when possible...
    /**
     * This constructor allows passing in all the of the variables.
     * 
     * WARNING: Calling this constructor will replace the existing
     *           ConfigManger, accessed by calling ConfigManager.getInstance()
     */
    public SettingsManager()
    {
        // Set as the instance.
        instance = this;
    }

	/**
     * Set a configuration parameter using <kev,value> pair
     * 
     * @param key - the name of the parameter
     * @param value - the new value of the parameter
     * @return true if new value has been set, false otherwise
     */
	public boolean setValue(String key, String value) {

		//TODO - JC May 2012, update Strings to use Module XMLLabels
		boolean valueChanged = true;
		
		if(key.equals(SimModuleConfigParams.SEED_XML_TAG)) {
			((SimModuleConfigParams) Module.SIM_MODULE.getParams()).setSeed(Long.parseLong(value));
		} else if(key.equals(SimModuleConfigParams.END_TIME_XML_TAG)) {
			((SimModuleConfigParams) Module.SIM_MODULE.getParams()).setEndTime(Long.parseLong(value));
		} else if(key.equals(LengthOfTime.END_TIME_UNIT_XML_TAG)) {
			((SimModuleConfigParams) Module.SIM_MODULE.getParams()).setEndTimeUnit(LengthOfTime.valueOf(value));
		} 
		//isOn settings
		else if(key.equals("isOnSubscriptions")) {
			Module.SUBSCRIPTION_MODULE.setActive(Boolean.parseBoolean(value));
		} else if(key.equals("isOnBroker")) { //if broker is on, services must be on			
			if(Boolean.parseBoolean(value) == true) {
				logger.info("Broker requires Service module- turning on Services");
				Module.SERVICE_MODULE.setActive(true);
				Module.BROKER_MODULE.setActive(true);
				((DemandModuleConfigParams)Module.DEMAND_MODULE.getParams()).setBrokerActive(true);				
			}else {
				Module.BROKER_MODULE.setActive(false);
				((DemandModuleConfigParams)Module.DEMAND_MODULE.getParams()).setBrokerActive(false);
			}
		} else if(key.equals("isOnServices")) {
			if(Module.BROKER_MODULE.isActive()) { //if broker is already on, services must be on
				logger.info("Broker already on, Services module must be on");
				Module.SERVICE_MODULE.setActive(true);
			}else {
				Module.SERVICE_MODULE.setActive(Boolean.parseBoolean(value));
			}
		} else if(key.equals("isOnTemperature")) {
			Module.THERMAL_MODULE.setActive(Boolean.parseBoolean(value));
		} else if(key.equals("isOnCosts")) {
			Module.COSTS_MODULE.setActive(Boolean.parseBoolean(value));
		} else if(key.equals("isOnFailure")) {
			Module.FAILURE_MODULE.setActive(Boolean.parseBoolean(value));
		} else if(key.equals("isOnReplacements")) {
			Module.REPLACEMENTS_MODULE.setActive(Boolean.parseBoolean(value));
		} else if(key.equals("isOnUserEvents")) {
			Module.USER_EVENTS_MODULE.setActive(Boolean.parseBoolean(value));
		} 
		//Subscription settings
		else if(key.equals(SubscriptionsModuleConfigParams.TOPOLOGY_XML_TAG)) {
			try {
				((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setTopology(NetworkTopology.valueOf(value));
			} catch (Exception e) {
				logger.warn("Invalid format for " + SubscriptionsModuleConfigParams.TOPOLOGY_XML_TAG + ": '"+ value +"'.  Leaving unchanged='" + ((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).getTopologyType() + "'");
				valueChanged = false;
			}
		} else if(key.equals(SubscriptionsModuleConfigParams.PROTOCOL_XML_TAG)) {
			try {
				((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setProtocol(Protocol.valueOf(String.valueOf(value)));
			} catch (Exception e) {
				logger.warn("Invalid format for " + SubscriptionsModuleConfigParams.PROTOCOL_XML_TAG+ ": '"+ value +"'.  Leaving unchanged='" + ((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).getProtocolType() + "'");
				valueChanged = false;
			}
		} else if(key.equals(SubscriptionsModuleConfigParams.MAX_SUBSCRIPTIONS_XML_TAG)) {
			((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setMaxSubscriptions(Integer.parseInt(value));
		} else if(key.equals(SubscriptionsModuleConfigParams.MIU_XML_TAG)) {
			((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setMiu(Double.parseDouble(value));
		} else if(key.equals(SubscriptionsModuleConfigParams.REWIRE_XML_TAG)) {
			((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setRewire(Double.parseDouble(value));
		} else if(key.equals(SubscriptionsModuleConfigParams.SINGLE_NODE_UPDATE_XML_TAG)) {
			((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).setSingleNodeUpdate(Boolean.parseBoolean(value));
		}
		//Broker settings		
		else if(key.equals(BrokerModuleConfigParams.NUM_AGENTS_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setNumAgents(Integer.parseInt(value));
		}
		else if(key.equals(BrokerModuleConfigParams.NUM_EACH_USER_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setNumEachUser(Integer.parseInt(value));
		}
		else if(key.equals(BrokerModuleConfigParams.VARIANCE_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setVariance(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.LEARING_PERIOD_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setLearningPeriod(Integer.parseInt(value));
		}
		else if(key.equals(BrokerModuleConfigParams.K_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setK(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.COST_FACTOR_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setCostFactor(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.MRU_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setMRU(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.ADAPT_THRESHOLD_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setAdapt(Boolean.parseBoolean(value));
		}
		else if(key.equals(BrokerModuleConfigParams.ADAPT_MOMENTUM_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setAdaptMomentum(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.ADAPT_ALPHA_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setAdaptAlpha(Double.parseDouble(value));
		}
		else if(key.equals(BrokerModuleConfigParams.DEMAND_PROFILE_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setDemandProfile(Integer.parseInt(value));
		}
		else if(key.equals(BrokerModuleConfigParams.MARKET_SHOCK_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setMarketShock(Boolean.parseBoolean(value));
		}
		else if(key.equals(BrokerModuleConfigParams.MARKET_SHOCK_MONTH_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setMarketShockMonth(Integer.parseInt(value));
		}
		else if(key.equals(BrokerModuleConfigParams.MARKET_SHOCK_PROFILE_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setMarketShockProfile(Integer.parseInt(value));
		} 
		else if(key.equals(BrokerModuleConfigParams.DemandForecaster.DEMAND_FORECASTER_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setDemandForecaster(value);
		}
		else if(key.equals(BrokerModuleConfigParams.ReservationPeriod.RESERVATION_PERIOD_XML_TAG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setReservationPeriod(value);
		}
		else if(key.equals(BrokerModuleConfigParams.ROGERS_CLIFF_2012_PAYMENT_BUG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setRogersCliff2012PaymentBug(Boolean.parseBoolean(value));
		}
		else if(key.equals(BrokerModuleConfigParams.ROGERS_CLIFF_2012_DEFICIT_BUG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setRogersCliff2012DeficitBug(Boolean.parseBoolean(value));
		}
		else if(key.equals(BrokerModuleConfigParams.ROGERS_CLIFF_2012_RESERVATIONS_BUG)) {
			((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).setRogersCliff2012ReservationsBug(Boolean.parseBoolean(value));
		}
		//Pricing settings
		else if(key.equals(PricingModuleConfigParams.ON_DEMAND_PRICE_UPFRONT_XML_TAG)) {
			((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).setOnDemandUpfrontPrice(Double.parseDouble(value));
		}
		else if(key.equals(PricingModuleConfigParams.ON_DEMAND_PRICE_MONTHLY_XML_TAG)) {
			((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).setOnDemandMonthlyPrice(Double.parseDouble(value));
		}
		else if(key.equals(PricingModuleConfigParams.RESERVED_PRICE_UPFRONT_XML_TAG)) {
			((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).setReservedUpfrontPrice(Double.parseDouble(value));
		}
		else if(key.equals(PricingModuleConfigParams.RESERVED_PRICE_MONTHLY_XML_TAG)) {
			((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).setReservedHourlyPrice(Double.parseDouble(value));
		}
		else if(key.equals(PricingModuleConfigParams.RESERVATION_LENGTH_XML_TAG)) {
			((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).setReservationLength(Integer.parseInt(value));
		}
		else {
			logger.info("No parameter key: " + key);
			valueChanged = false;
		}		
		return valueChanged;
	}

	@Deprecated
    public static void setInstance(SettingsManagerAccess instance)
    {
        SettingsManager.instance = instance;
    }
    
    /**
     * Get the singleton ConfigManager, instantiating an empty one if it does
     * not already exist.
     * 
     * @return the singleton config manager.
     */
    public static SettingsManagerAccess getInstance()
    {
        if (instance == null)
        {
            instance = new SettingsManager();
        }

        return instance;
    }    
    
	/**
	 * Get the time of the first log event
	 */
	public long getFirstLogTime() {
		return SettingsManager.FIRST_LOG_EVENT_TIME;
	}
	
	/**
	 * Get the time period between log events
	 */
	public long getTimeBetweenLogs() {
		if(Module.SUBSCRIPTION_MODULE.isActive()) { 
			return SubscriptionsModuleRunner.TIME_BETWEEN_LOGS; //very short period if subscriptions on
		} else if (Module.THERMAL_MODULE.isActive()) {
			return ThermalModuleRunner.TIME_BETWEEN_LOGS; //very short period if temperature on
		} else {
			return SettingsManager.TIME_BETWEEN_LOGS_NORMAL; //else, much longer log period
		}
	}
	
	/**
	 * Get the time of the first GUI Update event
	 */
	public long getFirstGUIUpdateTime() {
		return SettingsManager.TIME_OF_FIRST_GUI_UPDATE;
	}
	
	/**
	 * Get the time between GUI Update events
	 */
	public long getTimeBetweenGUIUpdates() {
		if(Module.SUBSCRIPTION_MODULE.isActive() ||
				Module.THERMAL_MODULE.isActive()) { 
			return SettingsManager.TIME_BETWEEN_GUI_UPDATES_CONSISTENCY; //more regular updates is consistency on
		} else {
			return SettingsManager.TIME_BETWEEN_GUI_UPDATES_NORMAL;
		}
	}
	
//	/**
//	 * Get the time unit for the time series graphs
//	 */
//	public UnitTime getUnitTimeForTimeSeriesGraphs() {
//		return TIME_UNITS_TIME_SERIES_GRAPH;
//	}
//
//	/**
//	 * Get the time unit for the time series graphs
//	 */
//	public UnitTime getUnitTimeForThermalTimeSeriesGraphs() {
//		return TIME_UNITS_THERMAL_TIME_SERIES_GRAPH;
//	}
//	
//	/**
//	 * Get the time unit for the failures time series graph
//	 */
//	public UnitTime getUnitTimeForFailuresTimeSeriesGraphs() {
//		
//		//if subscriptions is on, then plot failures on same timescale as consistency graph
//		if(Module.SUBSCRIPTION_MODULE.isActive()) { 
//			logger.debug("Units for failures time series graph: " + TIME_UNITS_CONSISTENCY_GRAPH);
//			return TIME_UNITS_CONSISTENCY_GRAPH;
//		} else { //else, plot on regular timescale
//			logger.debug("Units for failures time series graph: " + TIME_UNITS_TIME_SERIES_GRAPH);
//			return TIME_UNITS_TIME_SERIES_GRAPH;
//		}
//	}
//	
//	/**
//	 * Get the time unit for the consistency time series graph
//	 */
//	public UnitTime getUnitTimeForConsistencyTimeSeriesGraphs() {
//		return TIME_UNITS_CONSISTENCY_GRAPH;
//	}
	
	/**
	 * Gets the time unit for all graphs
	 * based on most module with smallest log period
	 */
	public UnitTime getUnitTimeForGraphs() 
	{
		if(Module.THERMAL_MODULE.isActive()) 
		{ 
			logger.debug("Units for all graphs: " + TIME_UNITS_THERMAL_TIME_SERIES_GRAPH);
			return TIME_UNITS_THERMAL_TIME_SERIES_GRAPH;
		}
		if(Module.SUBSCRIPTION_MODULE.isActive()) 
		{ 
			logger.debug("Units for all graphs: " + TIME_UNITS_CONSISTENCY_GRAPH);
			return TIME_UNITS_CONSISTENCY_GRAPH;
		}
		else 
		{ //else, plot on regular timescale
			logger.debug("Units for all graphs: " + TIME_UNITS_TIME_SERIES_GRAPH);
			return TIME_UNITS_TIME_SERIES_GRAPH;
		}		
	}
}
