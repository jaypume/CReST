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

import sim.module.event.Event;
import sim.module.log.Log;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;

public abstract class AbstractSubscriptionsEvent extends Event
{
	protected long PERIOD_MEAN_MILLISECONDS;
	protected int PERIOD_MAX_VARIANCE; // % variance of mean 
	
//	int SubscriptionID;
//	boolean status;

	/**
	 * 
	 * @param pStartTime - the time you want the update to happen
	 * @param datacentreID - the datacentreID of the event
	 * @param nodeID - the unique nodeID of the node you want to update
	 */
	protected AbstractSubscriptionsEvent(final long pStartTime, int datacentreID)
	{
    	super(pStartTime, datacentreID);
	}
    
	/**
     * Get the period until the next update event
     * 
     * Time is randomly generated with fixed mean and maximum variance
     * 
     * @return time in simulation time
     */
    public long getNextUpdatePeriod()
    {
    	double variance = RandomSingleton.getInstance().randomDouble() * (PERIOD_MEAN_MILLISECONDS * PERIOD_MAX_VARIANCE / 100);
    	if(RandomSingleton.getInstance().randomDouble() < 0.5) {
    		variance = -variance;
    	}
    	return TimeManager.millisecondsToSimulationTime(PERIOD_MEAN_MILLISECONDS + (long) variance);
    }
    
   /**
    * generates a single node subscription update at a set time with a random time component
    */
    protected abstract void generateEvents();

    /**
     * performs an update on the specified node 
     */
    protected abstract boolean performEvent();

	/**
	 * log files are dealt with in a different manner
	 */
	public Log toLog()
	{
		Log log = new Log();

		return log;
	}
}
