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

import sim.module.Module;
import sim.module.broker.BrokerModuleRunner;
import sim.module.broker.configparams.BrokerModuleConfigParams;
import sim.module.demand.bo.Demand;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import utility.time.TimeManager;

public class MarketShockEvent extends Event {
	
	private int newProfile;
	
	public MarketShockEvent(long pStartTime, int newProfile) {
		super(pStartTime, -1);
		this.newProfile = newProfile;
	}

	@Override
	protected boolean performEvent() {
		int NUM_OF_AGENTS = BrokerModuleRunner.getInstance().getNumAgents();

		// Delete old profile
		BrokerModuleRunner.getInstance().getDemandList().clear();
		for (int i = 0; i < NUM_OF_AGENTS; i++) {
    		Demand d = new Demand(((DemandModuleConfigParams)Module.DEMAND_MODULE.getParams()).getFilename(), newProfile);
    		d.setCounter(BrokerModuleRunner.getInstance().getCurrentMonth());
    		d.advanceDemand();
    		BrokerModuleRunner.getInstance().getDemandList().add(d);
    	}
		
		return true;
	}

	@Override
	protected void generateEvents() {
		int LEARNING_PERIOD = ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getLearningPeriod();
		
		BrokerModuleRunner.getInstance().reverseMonth(LEARNING_PERIOD);
		BrokerModuleRunner.getInstance().reverseDemand(LEARNING_PERIOD);

		// Need to make users relearn for the new profile
		long learnEventPeriod = TimeManager.millisecondsToSimulationTime(1);
        //Adds events to the queue for users to learn from current demand
        for (int p = 0; p < LEARNING_PERIOD; p++) {
        	EventQueue.getInstance().addEvent(new UserLearnEvent(learnEventPeriod * p));
        }
	}
}
