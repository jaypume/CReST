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
package sim.module.failure;

import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.EventQueue;
import sim.module.failure.bo.FailureGroups;
import sim.module.failure.event.FailureLogEvent;
import sim.module.failure.event.FailureThreads;
import sim.module.thermal.event.ThermalGridEvent;
import sim.physical.World;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;

public class FailureModuleRunner extends AbstractModuleRunner{
	
	public static final long TIME_BETWEEN_LOGS = TimeManager.daysToSimulationTime(1);

	private static int counter = 0;
	
	public static Logger logger = Logger.getLogger(FailureModuleRunner.class);
	
	private static FailureModuleRunner instance = null;
	
	protected FailureModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static FailureModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new FailureModuleRunner();
        }
        return instance;
	}

	@Override
	public void update(Observable observable, Object obj) 
	{
		//TODO AS 5.9.12- finish
		//If the event is a ThermalGrid event, do something
		if (isThermalGridEvent(obj)) 
		{
			ThermalGridEvent event = (ThermalGridEvent) obj;
			logger.debug("There has been a ThermalGrid event... "+ event);

//			
//			Failable object = null;
//
//	        // Get the object that this failure event is targeted at.
//			ObjectType mObjectType = FailureEvent.ObjectType.server;
//	        switch (mObjectType)
//	        {
//	            case server:
//	            {
//	                object = World.getInstance().getServer(mObjectID);
//	                break;
//	            }
//	            default:
//	            {
//	                logger.error("FailureEvent.performEvent(): Object type is not recognised.");
//	                break;
//	            }
//	        }
//////      Check local temperature
//			
////        shut down, restart servers
//	        
//	        
//	        // Perform a failure or fix based on what failure type this event is.
//	        FailType mFailType = FailureEvent.FailType.soft;
//	        switch (mFailType)
//	        {
//	            case soft:
//	            {
//	            	logger.info("Performing a soft fail event for object: " + object);
//	                object.performFailure(FailType.soft);
//	                break;
//	            }
//	            case fix:
//	            {
//	            	logger.info("Performing a fix event for object: " + object);
//	                object.performFix();
//	                break;
//	            }
//	        }
		}
	}

	@Override
	public void worldUpdated(World w) {
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.FAILURE_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.debug("Failure module is on...");
			
			
			
			//TODO - JC May 2012, what needs to be done here?
			
			//do something!
			
			logger.debug("Creating Failure Threads...");
			//Create the failure threads used to simulate server failures
			FailureThreads.create(RandomSingleton.getInstance(), this.createFailureGroups());
			
		} else {
			//do nothing - module not on
		}
	}
	
    /**
	 * Create and initialise the failure groups for the world.
	 * 
	 * WARNING This needs to be called after the world has been fully
	 *          initialised, otherwise the returned groups may not be complete.
	 * 
	 * @return the failure groups for the world.
	 */
	public FailureGroups createFailureGroups()
	{
	    FailureGroups failureGroups = new FailureGroups();
		final int numServers = World.getInstance().getNumServers();
		
		for (int i = 0; i < numServers; i++)
		{
			failureGroups.add(World.getInstance().getServer(i));
		}
		
		return failureGroups;
	}

	@Override
	public String getLogFileName() {
		return "failures";
	}

	@Override
	protected String getLogTitleString() {
		return FailureLogEvent.COLUMN_TITLE_STRING;
	}

	@Override
	public boolean isActive() {
		return Module.FAILURE_MODULE.isActive();
	}
}
