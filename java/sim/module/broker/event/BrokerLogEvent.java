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
import sim.module.event.Event;


public class BrokerLogEvent extends Event {
	public BrokerLogEvent(long pStartTime) {
		super(pStartTime, -1);
	}

	@Override
	protected boolean performEvent() {
		
		//Do not reset the balance if this is the last month of the learning period. This is done automatically from BrokerAgent.
		if( (BrokerModuleRunner.getInstance().getCurrentMonth()+1) != ((BrokerModuleConfigParams) Module.BROKER_MODULE.getParams()).getReservationPeriod().getMonths()) {
			logger.debug("Calling resetBalance() ..");
			BrokerModuleRunner.getInstance().getBroker().resetBalance();
		} else {
			logger.debug("Not calling resetBalance(), since this month is the end of the learning period ..");
		}
		return true;
	}

	@Override
	protected void generateEvents() {
	}
}
