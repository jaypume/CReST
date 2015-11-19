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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.module.broker.BrokerModuleRunner;
import sim.module.broker.bo.UserAgent;
import sim.module.event.Event;

/**
 * Learn event - called when user needs to remember past performance but not act upon it
 *
 */
public class UserLearnEvent extends Event {

	public static Logger logger = Logger.getLogger(UserLearnEvent.class);
	
	private int numAgents;
	private int numEachAgent;
	
	public UserLearnEvent(long pStartTime) {
		super(pStartTime, -1);
		this.numAgents = BrokerModuleRunner.getInstance().getNumAgents();
		this.numEachAgent = BrokerModuleRunner.getInstance().getNumEachAgent();
	}

	@Override
	protected boolean performEvent() {
		//Advance demand and update all users
		logger.info("Advancing demand and advancing month...");
		BrokerModuleRunner.getInstance().advanceDemand();
		BrokerModuleRunner.getInstance().advanceMonth();
		
		// For each type of user, Pick number of users to demand instance.
		for (int profile = 0; profile < numAgents; profile++) {
			double demand = BrokerModuleRunner.getInstance().getDemandList().get(profile).getCurrentDemand();
			BrokerModuleRunner.getInstance().getBroker().updateDemand(demand);
			
			int numToPick = (int)Math.ceil(demand * numEachAgent);
			List<Integer> chosen = new ArrayList<Integer>();
			chosen.addAll(BrokerModuleRunner.getInstance().getChosen(numToPick));
			for (Integer i : chosen) {
				UserAgent user = BrokerModuleRunner.getInstance().getAgent((profile * numEachAgent) + i);
				user.learn();
			}
		}
		return true;
	}

	@Override
	protected void generateEvents() {
	}
}
