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
package sim.module.event;

import sim.module.service.event.ServiceLogEvent;
import config.SettingsManager;

public abstract class AbstractBrokerDemandModuleEventThread extends AbstractModuleEventThread
{
	protected static long START_TIME_DISTRIBUTION;
	
	public AbstractBrokerDemandModuleEventThread() {}
	
	/**
	 * Generate all 'demand threads' and add them to the event queue. 
	 *  
	 * Return the number of new events
	 */
	public int generateEvents()
	{
		int newEventsGenerated = 0;
		
        logger.info("Adding Demand Events to the event queue...");
        logger.info("Generating initial demand events...");
        
        newEventsGenerated = generateDemandEvents();

        logger.info(newEventsGenerated + " new Demand Event(s) added");
        logger.info("Adding Logging event for services. First log time = " + SettingsManager.getInstance().getFirstLogTime());
        EventQueue.getInstance().addEvent(ServiceLogEvent.create(SettingsManager.getInstance().getFirstLogTime()));
		return ++newEventsGenerated;
	}
	
	 protected abstract int generateDemandEvents();
}
