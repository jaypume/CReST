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
 * @author Callum Muir (cm9757)
 * @created 11 Aug 2011
 */
package sim.module.subscriptions.event;

import org.apache.log4j.Logger;

import sim.module.event.EventQueue;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

public class SubscriptionUpdateAllEvent extends AbstractSubscriptionsEvent
{
	public static Logger logger = Logger.getLogger(SubscriptionUpdateAllEvent.class);

	private SubscriptionUpdateAllEvent(long pStartTime)
	{
		// TODO - JC: Dec 2011. Passing non-dc-specific -1, This is a hack. Fix this.
		super(pStartTime, -1);
		PERIOD_MEAN_MILLISECONDS = 10000;
		PERIOD_MAX_VARIANCE = 10;
	}

	/**
	 * Create a subscription update all event
	 * 
	 * @param pStartTime
	 *            - the time for the event to start
	 * @return new event
	 */
	public static SubscriptionUpdateAllEvent create(final long pStartTime)
	{
		return new SubscriptionUpdateAllEvent(pStartTime);
	}

	/**
	 * makes a new event at the given timestep with a random component
	 */
	@Override
	protected void generateEvents()
	{
		EventQueue.getInstance().addEvent(SubscriptionUpdateAllEvent.create(mStartTime + this.getNextUpdatePeriod()));
		numEventsGenerated++;
	}

	/**
	 * updates all of the nodes
	 */
	@Override
	protected boolean performEvent()
	{
		logger.debug("Performing SubscriptionUpdateAllEvent...");

		boolean continueSimulation = true;
		for (Datacentre d : World.getInstance().getDatacentres())
		{
			long timeOfLastFailOrFix = SubscriptionsModuleRunner.getInstance().getTimeOfLastFailOrFix();
			long timeOfLastUpdate = SubscriptionsModuleRunner.getInstance().getTimeOfLastUpdate();
			
			////for debugging breakpoints////
			if(timeOfLastFailOrFix!=0)
			{
				//int i = 0;
			}
			if(timeOfLastUpdate!=0)
			{
				//int i = 0;
			}
			////////////////////
			
			if(timeOfLastFailOrFix >= timeOfLastUpdate)
			{
				logger.debug(TimeManager.getTimeString(World.getInstance().getTime()) + ": FailFix event " + TimeManager.getTimeString(timeOfLastFailOrFix) + " since last subscription update " + TimeManager.getTimeString(timeOfLastUpdate) + "- updating");
				SubscriptionsModuleRunner.getInstance().getSubscriptionNetwork(d.getID()).updateNodeSubscription();
			}
			else
			{
				logger.debug(TimeManager.getTimeString(World.getInstance().getTime()) + ": NO FailFix event since last subscription update " + TimeManager.getTimeString(timeOfLastUpdate) + "- not updating");
			}
		}

		return continueSimulation;
	}
}