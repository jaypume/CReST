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

import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.ServiceManager;
import utility.time.TimeManager;

//TODO - make this extend ServiceEvent (also ServiceStopEvent can extend ServiceEvent)
/**
 * Event to represent the starting of a service.
 */
public class ServiceStartEvent extends AbstractServiceEvent
{

	public static Logger logger = Logger.getLogger(ServiceStartEvent.class);
    protected long duration;       //service run duration

    
    protected ServiceStopEvent stopEvent; //the stop event associated with this start event
    

    /**
     * Service Start Event constructor
     * 
     * @param eventStartTime - the start time for the service start event
     * @param duration - the duration of the service
     * @param serviceID - the ID of the service
     * @param issueNumber - the issue number of the service (Default 0) //TODO make this internal
     * @param dc_number - the number of the Datacentre
     */
    public ServiceStartEvent(final long eventStartTime, final long duration, final int serviceID, final int issueNumber, final int dc_number)
    {
    	super(eventStartTime, serviceID, issueNumber, dc_number);
        this.duration = duration;
    }
    
	/**
	 * Create and return and instance of a start service event.
	 * 
     * @param eventStartTime - the start time for the service start event
     * @param duration - the duration of the service
     * @param serviceID - the ID of the service
     * @param issueNumber - the issue number of the service (Default 0) //TODO make this internal
     * @param dc_number - the number of the Datacentre
	 */
    public static ServiceStartEvent create(final long eventStartTime, final long duration, final int serviceID, final int issueNumber, final int dc_number)
    {
        return new ServiceStartEvent(eventStartTime, duration, serviceID, issueNumber, dc_number);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents()
     */
    /**
     * A service requires a stop event to tell the simulator when the service has finished.
     */
    @Override
    protected void generateEvents()
    {   
    	//Do not generate a stop event if start event is ignored
    	if(this.isIgnored()) return;
    	else {
	    	EventQueue queue = EventQueue.getInstance();
	    	
	        stopEvent = (ServiceStopEvent) ServiceStopEvent.create(mStartTime+duration, mServiceID, issueNumber, datacentre_index);
	        stopEvent.setService(this.getService());
	        queue.addEvent(stopEvent);
	        
	        numEventsGenerated++;
    	}
    }

    /**
     * Get the ServiceStopEvent associated with this StartEvent
     * 
     * @return the stop event
     */
    public ServiceStopEvent getStopEvent() {
    	return stopEvent;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#perform()
     */
    @Override
    protected boolean performEvent()
    {
//    	System.out.println(TimeManager.getTimeString() +" StartServiceEvent\t"+mServiceID+ " issueNumber=" + issueNumber);
        boolean continueSimulation = true;
        
        ServiceManager sm = ServiceModuleRunner.getInstance().getServiceManager(getDatacentreIndex());
        
        if (sm.startService(mServiceID, this) == false)
        {
        	//TODO reschedule
        	logger.debug(TimeManager.log("Could not start service... Queuing service..."));
        	sm.queueService(mServiceID,this);
        	this.setIgnored();
        }
        return continueSimulation;
    }

    /**
     * Get the desired stop time of this service.
     * 
     * @return the time at which this service should stop running.
     */
    public long getStopTime()
    {
    	return mStartTime + duration;
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
        String string = "ServiceStartEvent{ serviceID: " + 
        	mServiceID + ", issueNum=" + issueNumber + ", dcID=" + datacentre_index + ", startTime: " + TimeManager.getTimeString(mStartTime) +
        	", Duration = " + TimeManager.getTimeString(duration) + " Service = " + mService + "}";
       
        return string;
    }
    
    /**
     * Get the duration of the service
     * 
     * @return the duration
     */
    public long getDuration() {
    	return duration;
    }
}
