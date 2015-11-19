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
package sim.module.broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.broker.bo.BrokerAgent;
import sim.module.broker.bo.UserAgent;
import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.broker.event.MarketShockEvent;
import sim.module.demand.bo.Demand;
import sim.module.event.EventQueue;
import sim.module.service.ServiceModuleRunner;
import sim.physical.World;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;

public class BrokerModuleRunner extends AbstractModuleRunner {
	
	private static BrokerModuleRunner instance = null;
	public static Logger logger = Logger.getLogger(BrokerModuleRunner.class);
	private static int counter = 0;
	
	int NUM_OF_AGENTS;
    int NUM_OF_EACH_USER;
	
    //TODO: Read "total months" from the demand file, do not set as a constant!
	private int totalMonths = 277; //Note: This is number of months (i.e., rows) in the demand file. 
	private int currentMonth;
	
	private List<UserAgent> userAgents;
    private BrokerAgent broker;
    private List<Demand> demandList;
    
    private boolean MARKET_SHOCK;
    private int MARKET_SHOCK_MONTH;
    private int MARKET_SHOCK_PROFILE;
    
	private BrokerModuleRunner() {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static BrokerModuleRunner getInstance() {
		if (instance == null) {
            instance = new BrokerModuleRunner();
        }
        return instance;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
	}

	@Override
	public void worldUpdated(World w) {
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.BROKER_MODULE.isActive()) {
			//NOTE: If Broker Module is Active, Demand Module is set to inactive 
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			logger.info("Broker module is on...");
			
			currentMonth = -1;
		    userAgents = new ArrayList<UserAgent>();
		    demandList = new ArrayList<Demand>();
		    broker = new BrokerAgent("Broker_1");  
		    
		    logger.info("Broker module is on. Setting Services to log every month...");
		    ServiceModuleRunner.getInstance().setLogPeriod(TimeManager.monthsToSimulationTime(1));
		    this.NUM_OF_AGENTS = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getNumAgents();
		    this.NUM_OF_EACH_USER = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getNumEachUser();
		    this.MARKET_SHOCK = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getMarketShock();
		    this.MARKET_SHOCK_MONTH = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getMarketShockMonth();
		    this.MARKET_SHOCK_PROFILE = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getMarketShockProfile();
		}
	}
	
	@Override
	public void initLogs(String dirName) {
		if(!Module.SERVICE_MODULE.isActive()) {
			return;
		} else {
			super.initLogs(dirName);
		}
	}
	
    public List<UserAgent> getUserAgents() {
    	if (userAgents==null) {
    		logger.info("UserAgents List is null, creating new ...");
    		userAgents = new ArrayList<UserAgent>();
    	}
    	
    	return userAgents; 
    }
    
    public UserAgent getAgent(int idx) {
    	if (userAgents==null) {
    		logger.info("UserAgents List is null, creating new ...");
    		userAgents = new ArrayList<UserAgent>();
    	}
    	
    	return userAgents.get(idx);
    }
    
    public BrokerAgent getBroker() {
    	if (broker==null) {
    		logger.info("BrokerAgent is null, creating new BrokerAgent");
    		broker = new BrokerAgent("Broker_1");
    	}
    	
    	return broker;   
    }
    
    public List<Demand> getDemandList() {
    	if (demandList==null) {
    		logger.info("DemandList is null, creating new ...");
    		demandList = new ArrayList<Demand>();
    	}
    	
    	return demandList;
    }
    
    public void advanceDemand() {
    	if(demandList==null) {
			logger.fatal("DemandList is null.  Exiting system...");
			System.exit(-1);
    	}
    	
    	for (int y = 0; y <= demandList.size() - 1; y++) {
    		demandList.get(y).advanceDemand();
    	}
    }
    
    public void reverseDemand(int months) {
    	if(demandList==null) {
			logger.fatal("DemandList is null.  Exiting system...");
			System.exit(-1);
    	}
    	
    	for (int y = 0; y <= demandList.size() - 1; y++) {
    		demandList.get(y).reverseDemand(months);
    	}
    }
    
    public int getNumAgents() {
    	return NUM_OF_AGENTS;
    }
    
    public int getNumEachAgent() {
    	return NUM_OF_EACH_USER;
    }
    
    public int getSimulationLength() {
    	return totalMonths;
    }
    
    public int getCurrentMonth() {
    	return currentMonth;
    }
    
    public void advanceMonth() {
    	++this.currentMonth;
    	
    	if (MARKET_SHOCK) {
    		if (currentMonth == MARKET_SHOCK_MONTH) {
    			EventQueue.getInstance().addEvent(new MarketShockEvent(-1, MARKET_SHOCK_PROFILE));
    			MARKET_SHOCK = false;
    		}
    	}
    	logger.info("Month has been advanced. Month is now " + currentMonth);
    }
    
    public void reverseMonth(int months) {
    	this.currentMonth -= months;
    }
    
	public List<Integer> getChosen(int numToPick) {
		List<Integer> userList = new ArrayList<Integer>();
		for (int i = 0; i < NUM_OF_EACH_USER; i++) {
			userList.add(i);
		}
		Collections.shuffle(userList, RandomSingleton.getInstance().getJavaUtilRandom());
		
		return userList.subList(0, numToPick);
	}

	@Override
	public String getLogFileName() {
		return "broker";
	}

	@Override
	protected String getLogTitleString() {
		return "Date, Summed Capacity, Summed Probability, Reservations Purchased, Instances Required, On Demand Purchased, Balance, Avg Threshold, Assets, Assets+Balance";
	}

	@Override
	public boolean isActive() {
		return Module.BROKER_MODULE.isActive();
	}
}
