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
/**
 * @author Luke Drury (ld8192)
 * @created 20 Jul 2011
 */
package sim.module.service.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.log.Log;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.Service;
import sim.module.service.bo.ServiceManager;
import utility.time.TimeManager;

/**
 * Event class to represent a service that is finishing execution.
 */
public class ServiceStopEvent extends AbstractServiceEvent
{
	
	public Logger logger = Logger.getLogger(ServiceStopEvent.class);
    
    /**
     * Basic constructor. Creates an empty event with start time initialised to
     * the given value.
     * 
     * @param pStartTime
     *            the time at which this event should happen.
     */
    public ServiceStopEvent(final long pStartTime, final int pServiceID, final int issueNumber, final int pDatacentreNo)
    {
    	super(pStartTime, pServiceID, issueNumber, pDatacentreNo);
        logger.debug("New Service Stop Event: " + this);
    }

    
    /**
     * Method to create an instance of a stop service event.
     * 
     * @param pStartTime
     *            the time a which this event will happen in the simulation.
     * @return the newly created event.
     */
    public static Event create(final long pStartTime, final int pServiceID, final int issueNumber, final int pDatacentreNo)
    {
        return new ServiceStopEvent(pStartTime, pServiceID, issueNumber, pDatacentreNo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#performEvent()
     */
    @Override
    public boolean performEvent()
    {
        boolean continueSimulation = true;
        
        logger.debug(TimeManager.log("ServiceStopEvent.performEvent(): " + this));

        ServiceManager sm = ServiceModuleRunner.getInstance().getServiceManager(datacentre_index);

        Service s = sm.getServiceWithID(mServiceID);
        //get issue number of currently running service
        if(s == null) {
        	logger.warn(TimeManager.log(this + " Service is null..."));
   	
        }
        int currentIssueNumber = s.getIssueNumber();

        if(currentIssueNumber != issueNumber) {
        	logger.debug(TimeManager.log("Ignoring ServiceStopEvent: Info: ServiceID="+mServiceID+": Current.issue#=" + currentIssueNumber + " is not equal to ServiceStopEvent.issue#=" + issueNumber));
        	this.setIgnored();
        	
        } else if (s.getStatus()==Service.Status.failed){
        	logger.debug(TimeManager.log("Ignoring ServiceStopEvent: Info: Service Status is failed"));
        	this.setIgnored();
        } else {
        	sm.completeService(mServiceID);  //complete the service
        }
        
        //set the location of the service
        
        return continueSimulation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#toLog()
     */
    public Log toLog()
    {
        Log log = new Log();

        return log;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String string = "ServiceStopEvent{ serviceID: " + mServiceID + ", issueNum=" + issueNumber +
        	", stopTime: " + TimeManager.getTimeString(mStartTime) + " Service = " + mService + "}";
        return string;
    }
}
