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
package sim.module.subscriptions.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;


public class PollerUpdateEvent extends Event {

	public static Logger logger = Logger.getLogger(PollerUpdateEvent.class);
	
	private final static long PERIOD_MEAN_MILLISECONDS = 10000;
	private final static int PERIOD_MAX_VARIANCE = 10; // % variance of mean 
	
    private PollerUpdateEvent(final long pStartTime, int dcID) 
    {
        super(pStartTime, dcID);
        logger.debug(TimeManager.log("new PollerUpdateEvent(startTime="+TimeManager.getTimeString(pStartTime) + " For DC=" + dcID));
    }

    /**
     * Create a new poller update event
     * 
     * @param pStartTime - the time for the event to start
     * @param dcID - ID of datacentre to be polled
     * 
     * @return new event
     */
    public static PollerUpdateEvent create(final long pStartTime, int dcID)
    { 
    	return new PollerUpdateEvent(pStartTime, dcID);
    }
    
    /**
     * Get the period until the next update event
     * 
     * Time is randomly generated with fixed mean and maximum variance
     * 
     * @return time in simulation time
     */
    public long getNextUpdatePeriod() {
    	double variance = RandomSingleton.getInstance().randomDouble() * (PERIOD_MEAN_MILLISECONDS * PERIOD_MAX_VARIANCE / 100);
    	if(RandomSingleton.getInstance().randomDouble() < 0.5) {
    		variance = -variance;
    	}
    	return TimeManager.millisecondsToSimulationTime(PERIOD_MEAN_MILLISECONDS + (long) variance);
    }
    
    /**
     * makes a new event at the given timestep with a random component
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(PollerUpdateEvent.create(mStartTime + this.getNextUpdatePeriod(), datacentre_index));
        numEventsGenerated++;
    }
 
    /**
     * updates all of the nodes
     */
    @Override
    protected boolean performEvent()
    {
    	
    	logger.debug(TimeManager.log("Performing PollerUpdateEvent on DC=" + datacentre_index + "..."));
        boolean continueSimulation = true;

    	SubscriptionsModuleRunner.getInstance().getSubscriptionNetwork(datacentre_index).pollSubNodes();

        return continueSimulation;
    }

    /** 
     * not used logging is done in a different way
     */
    public Log toLog()
    {
        Log log = new Log();
        return log;
    }
}


