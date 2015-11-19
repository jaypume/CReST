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
package sim.module.demand;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.EventQueue;
import sim.module.failure.FailureModuleRunner;
import sim.module.failure.event.FailureEvent;
import sim.module.failure.event.FailureEvent.FailType;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.service.bo.Service;
import sim.module.service.bo.Service.Status;
import sim.module.service.bo.ServiceManager;
import sim.module.service.event.ServiceLogEvent;
import sim.physical.Datacentre;
import sim.physical.Server;
import sim.physical.World;
import utility.time.TimeManager;

public class DemandModuleRunner extends AbstractModuleRunner{

//	public static boolean NEW_WAY = false;
	public static final long TIME_BETWEEN_LOGS = TimeManager.hoursToSimulationTime(1);
	
	private static int counter = 0;
	
	public static Logger logger = Logger.getLogger(FailureModuleRunner.class);
	
	/** ServiceManagers.  One for each Datacentre. */
	private ArrayList<ServiceManager> serviceManagerList;
	
	private static DemandModuleRunner instance = null;
	
	protected DemandModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static DemandModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new DemandModuleRunner();
        }
        return instance;
	}
	
	public ServiceManager getServiceManager(int datacentre_index) {
		
		if(serviceManagerList == null) {
			logger.fatal("ServiceManagerList is null. Exiting system.");
			System.exit(-1);
			return null; //we wont get here.
		}
		if(datacentre_index>=0 && datacentre_index<serviceManagerList.size()) {
			return serviceManagerList.get(datacentre_index);
		} else {
			logger.fatal("Out of bounds: Requesting datacentre_index = " + datacentre_index + ". Exiting system.");
			System.exit(-1);
			return null; //we wont get here.
		}
	}
	
	@Override
	public void worldUpdated(World w) {
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.SERVICE_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.info("Service module is on...");
			
			//it is important that this is cleared/reset each time world is updated
			serviceManagerList = new ArrayList<ServiceManager>(); 
			
			//for each datacentre, generate a ServiceManager
			for(Datacentre dc: World.getInstance().getDatacentres()) {

				serviceManagerList.add(new ServiceManager(dc)); 			
				logger.info("ServiceManager initialised for datacenter " + dc.getID());
			}	
			
			logger.info("ServiceModuleRunner initialised...");
			
			//What do we need to do? ...anything?
			
		} else {
			//do nothing - module not on
		}
	}
	
	@Override
	public void update(Observable o, Object object) {

		//Services are affected when the server they reside on fail. 
		
		//If the event is a failure, we should do something with it...
		
		if (isFailureEvent(object)) {
			
			FailureEvent event = (FailureEvent) object;
			logger.debug("There has been a failure event... "+ event);
			
			//Check that the object type is a server
			if(event.getObjectType().equals(ObjectType.server)) {
				
				//TODO - JC, May 2012: surely there is an easier way to get a dc_id value?!
				int dc_id = World.getInstance().getDatacentre(World.getInstance().getServer(event.getID()).getIP()).getID();
			
				//is it a failure or a fix?
				if(event.getFailEventType().equals(FailType.fix)) {
					//TODO JC May 2012: do what in the event of a fix???
				} else {
					logger.info("Server failure in datacentre " + dc_id + " updating services on server " + World.getInstance().getServer(event.getID()));
					//we have a hard or soft failure. Update services running on failed server
					updateServicesOnFailedServer(World.getInstance().getServer(event.getID()));
				}
				
				
			} else {
				logger.info("Object is not a server, ignoring..." + event.getObjectType());
			}			
		}

		//we don't care about any other event types, so do nothing else...
		
	}

	

	/**
	 * A server has failed. Update services running on the failed server [stop and re-allocate].
	 * 
	 * @param s - the failed server
	 */
    private void updateServicesOnFailedServer(Server s)
    {
    	
    	logger.info(TimeManager.log("Performing service failures on server="+s));
    	
        // TODO JC, Jan 2012, Recover from errors in a more sensible way...
    	// TODO JC MAY 2012: THis should all be in ServiceManager.  Server should not be dealing with this itself! MOVE...
    	try {
    		
    		int datacentre_index = World.getInstance().getDatacentre(s.getIP().dc()).getID();
    		
    		ServiceManager sm = serviceManagerList.get(datacentre_index);
    		
    		//Iterate through all running services and check if running on *this* server
            for (Service service : sm.getMap().values())
            {
                if (service.getServerID() == s.getIP())
                {
                	logger.debug("We have a match between: " + service.getServerID() + " and " + s.getIP());
                    logger.debug("Service status = " + service.getStatus());
                	if (service.getStatus() == Status.running)
                    {
                    	logger.info("ServerID="+s.getID()+ " failed: failing service = " + service);
                    	
                        service.setState(Status.failed);
                        
                        //Need to re-launch the service and notify GUI
                        //TODO - this would be a lot better if it used a ServiceStartEvent!
                        logger.info(TimeManager.log("ServiceID=" + service.getID() + "." + service.getIssueNumber() + " status=" + service.getStatus() + ".  Relaunching service..."));
                        //Reallocate the service (updates service issue number as side-effect)
                        if(!sm.reallocateTasks(service.getID())) {
                    		logger.warn(" Could not allocate service " +service);
                    		logger.warn("//TODO: Reschedule service when server becomes available");
                        }
                        //TODO: Create a Service Stop event (to notify GUI of failure).                      
                    }
                }
            }
            
    	} catch (NullPointerException e) {
    		logger.warn("No datacentre exists with ID = " + s.getIP().dc() + ".  Cannot access Service Manager...");
    	}
        
    }

	@Override
	public String getLogFileName() {
		return "services";
	}

	@Override
	protected String getLogTitleString() {
		return ServiceLogEvent.COLUMN_TITLE_STRING;
	}

	@Override
	public boolean isActive() {
		return Module.DEMAND_MODULE.isActive();
	}
}
