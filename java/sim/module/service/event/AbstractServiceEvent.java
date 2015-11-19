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
package sim.module.service.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.service.bo.Service;

public abstract class AbstractServiceEvent extends Event {
	
	public static Logger logger = Logger.getLogger(AbstractServiceEvent.class);
	

    protected int mServiceID;  	      //ID of service 
    protected int issueNumber = -1;   //Issue number of the service
    protected Service mService; 	  //the service
    
    /**
     * Service Start Event constructor
     * 
     * @param eventStartTime - the start time for the service start event
     * @param serviceID - the ID of the service
     * @param issueNumber - the issue number of the service (Default 0) //TODO make this internal
     * @param dc_number - the number of the Datacentre
     */
    protected AbstractServiceEvent(final long eventStartTime, final int serviceID, final int issueNumber, final int dc_number)
    {
        super(eventStartTime, dc_number);

        this.mServiceID = serviceID;
        this.issueNumber = issueNumber;
    }
    
    /**
     * Return service ID of this service event
     * 
     * @return service ID
     */
    public int getServiceID() {
    	return mServiceID;
    }
    
    /**
     * Get the service of this service event
     * 
     * @return service
     */
    public Service getService() {
    	return mService;
    }
    
    /** Set the service of this service event */
    public void setService(Service s) {
    	mService = s;
    }
}
