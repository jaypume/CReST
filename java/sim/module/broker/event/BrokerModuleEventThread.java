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
package sim.module.broker.event;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.broker.BrokerModuleRunner;
import sim.module.broker.bo.UserAgent;
import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.demand.bo.Demand;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.event.AbstractBrokerDemandModuleEventThread;
import sim.module.event.EventQueue;
import utility.time.TimeManager;

public class BrokerModuleEventThread extends AbstractBrokerDemandModuleEventThread {
	
	public BrokerModuleEventThread() {
		logger = Logger.getLogger(BrokerModuleEventThread.class);
		logger.info("Creating BrokerModuleEventThread");
	    START_TIME_DISTRIBUTION = 1000000;
	}
	
    /**
     * Creates initial events where a broker receives probabilities from multiple users for
     * month long options
     */
    protected int generateDemandEvents()
    {
    	int newEventsGenerated = 0;
    	
    	int NUM_OF_AGENTS = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getNumAgents();
        int NUM_OF_EACH_USER = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getNumEachUser();
        int LEARNING_PERIOD = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getReservationPeriod().getMonths();
        int DEMAND_PROFILE = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getDemandProfile();
         
        EventQueue queue = EventQueue.getInstance();

        // Sets up demand profiles for each agent using different columns from same file
    	for (int i = 0; i < NUM_OF_AGENTS; i++) {
    		Demand d = new Demand(((DemandModuleConfigParams)Module.DEMAND_MODULE.getParams()).getFilename(), DEMAND_PROFILE);
    		BrokerModuleRunner.getInstance().getDemandList().add(d);
    	}
    	
    	// Create NUM_OF_EACH_USER users for each profile
		for (int i = 0; i < NUM_OF_EACH_USER; i++) {
			for (int j = 0; j < NUM_OF_AGENTS; j++) {
				BrokerModuleRunner.getInstance().getUserAgents().add(new UserAgent(DEMAND_PROFILE));
			}
        }
        
		//1. INITIAL LEARNING PERIOD.  (JC May 2012. Code modified to have initial learning periods of 1ms each, originally 28 days each.)
		long learnEventPeriod = TimeManager.secondsToSimulationTime(1);
    	
    	
        //Adds events to the queue for users to learn from current demand
        for (int p = 0; p < LEARNING_PERIOD; p++) {
            logger.info("Adding new BrokerLogEvent at time " + TimeManager.getTimeString(learnEventPeriod * p + TimeManager.millisecondsToSimulationTime(1)) + "...");
        	queue.addEvent(new BrokerLogEvent(learnEventPeriod * p + TimeManager.millisecondsToSimulationTime(1)));
        	newEventsGenerated++;
        	logger.info("Adding new UserLearnEvent at time " + TimeManager.getTimeString(learnEventPeriod * p + TimeManager.millisecondsToSimulationTime(2)) + "...");
        	queue.addEvent(new UserLearnEvent(learnEventPeriod * p + TimeManager.millisecondsToSimulationTime(2)));
        	newEventsGenerated++;
        }
        
        //2. LEARNING PERIOD OVER. ADD RESERVE/EXECUTE EVENTS.
        long firstTime = learnEventPeriod * LEARNING_PERIOD + TimeManager.millisecondsToSimulationTime(3);
        logger.info("Adding new BrokerReserveEvent at time " + TimeManager.getTimeString(firstTime) + "...");
        queue.addEvent(new BrokerReserveEvent(firstTime));
        
    	return ++newEventsGenerated;
    }
}
