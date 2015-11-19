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
 * 
 */
package sim.module.subscriptions.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import utility.time.TimeManager;

/**
 * @author Callum
 *
 */
public class InconsistencyUpdateEvent extends Event
{
	public static Logger logger = Logger.getLogger(InconsistencyUpdateEvent.class);
	
	private final static long UPDATE_PERIOD = TimeManager.secondsToSimulationTime(100);
	
	final long mUpdatePeriod;
	
	/**
	 * @param pStartTime
	 */
	public InconsistencyUpdateEvent(long pStartTime)
	{
		//TODO For now make this event DC non-specific, passing dcID=-1
		super(pStartTime, -1);
		mUpdatePeriod = UPDATE_PERIOD;
		
		logger.info("Creating InconsistencyUpdateEvent with dcID=-1. //TODO - should this be DC non-specific event?");
	}
	
	/**
	 * Create a new Inconsistency Update Event
	 * 
	 * @param pStartTime - the start time of the event
	 * 
	 * @return new event
	 */
    public static InconsistencyUpdateEvent create(final long pStartTime)
    { 
    	return new InconsistencyUpdateEvent(pStartTime);
    }

	/* (non-Javadoc)
	 * @see sim.event.Event#performEvent()
	 */
	@Override
	protected boolean performEvent() 
	{
		logger.info(TimeManager.log("Performing inconsistency update..."));
		
        boolean continueSimulation = true;
        
        // TODO        // TODO
//      double Inconsistencies = SubscriptionGen.measureInconsistencies();

        return continueSimulation;
	}

	/* (non-Javadoc)
	 * @see sim.event.Event#generateEvents()
	 */
	@Override
	protected void generateEvents() {
        EventQueue.getInstance().addEvent(InconsistencyUpdateEvent.create(mStartTime + mUpdatePeriod));
        numEventsGenerated++;
	}
}
