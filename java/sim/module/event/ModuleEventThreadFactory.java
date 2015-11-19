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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.broker.event.BrokerModuleEventThread;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.demand.event.DemandModuleEventThread;
import sim.module.failure.event.FailureModuleEventThread;
import sim.module.gui.event.GUIUpdateModuleEventThread;
import sim.module.subscriptions.event.SubscriptionModuleEventThread;
import sim.module.thermal.event.ThermalModuleEventThread;

public class ModuleEventThreadFactory {
	
	public static Logger logger = Logger.getLogger(ModuleEventThreadFactory.class);
	
	/**
	 * Do not instantiate an object of this type (hence, private)
	 */
	private ModuleEventThreadFactory() {}
	
	public static List<AbstractModuleEventThread> getModuleEventThreads() {
		
		logger.debug("Creating ModuleEventThreads...");
		
		List<AbstractModuleEventThread> eventThreads = new ArrayList<AbstractModuleEventThread>();
		
		//TODO - JC Jun 2012 - this is untidy, clean it up with a loop: for each module type
		//TODO - in the loop we can check if they are user editable and use default value if not
		
		//we always have logging on
//		logger.infon("Adding Logging Events Module...");
//		eventThreads.add(new LoggingModuleEventThread());
		
		//we always have GUI updates on (actually, do we need these events? - are they redundant?)
		logger.info("Adding GUI Update Events Module...");
		eventThreads.add(new GUIUpdateModuleEventThread());
		
		if (Module.FAILURE_MODULE.isActive()) {
            logger.info("Adding Failure Events Module...");
            eventThreads.add(new FailureModuleEventThread());
        }
		
		if (Module.THERMAL_MODULE.isActive()) {
        	logger.info("Adding Thermal Events Module...");
            eventThreads.add(new ThermalModuleEventThread());
        }

		if (Module.SUBSCRIPTION_MODULE.isActive()) {
        	logger.info("Adding Subscriptions Events Module...");
        	eventThreads.add(new SubscriptionModuleEventThread());
        }

		//TODO - JC, may 2012: There is some confusion/crossover with SERVICE & DEMAND modules
		
		//If service module is on, do we use a broker or not to generate demand?
		if(Module.SERVICE_MODULE.isActive()) {
			if(((DemandModuleConfigParams) Module.DEMAND_MODULE.getParams()).isOnBroker()) {
				logger.info("Creating Broker Module Event Thread...");
				eventThreads.add(new BrokerModuleEventThread());
			} else {
				logger.info("Creating Demand Module Event Thread (no broker)...");
				eventThreads.add(new DemandModuleEventThread());
			}
		}
//		if(Module.BROKER_MODULE.isActive()) { //note: cannot have Demand & Broker on, only one or the other (or neither)
//        	logger.info("Adding Broker Events Module...");
//        	eventThreads.add(new BrokerModuleEventThread());
//        } else if (Module.SERVICE_MODULE.isActive()) {
//        	logger.info("Adding Demand Events Module...");
//        	eventThreads.add(new DemandModuleEventThread());
//        } 
		
		return eventThreads;
	}
}
