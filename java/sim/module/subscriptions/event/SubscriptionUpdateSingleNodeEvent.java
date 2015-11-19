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
import sim.physical.World;
import utility.time.TimeManager;

public class SubscriptionUpdateSingleNodeEvent extends AbstractSubscriptionsEvent
{
	public static Logger logger = Logger.getLogger(SubscriptionUpdateSingleNodeEvent.class);
	
	private int updatenode;

	private SubscriptionUpdateSingleNodeEvent(final long pStartTime, int datacentreID, int nodeID)
	{
		super(pStartTime, datacentreID);
		PERIOD_MEAN_MILLISECONDS = 10000;
		PERIOD_MAX_VARIANCE = 10;

		updatenode = nodeID;

		logger.debug(TimeManager.log("new SubscriptionUpdateSingleNodeEvent(startTime=" + TimeManager.getTimeString(pStartTime) + ", node=" + nodeID + ", dc=" + datacentreID + ")"));
		logger.debug(this.getNextUpdatePeriod());
	}

	/**
	 * Creates the event
	 * 
	 * @param pStartTime
	 *            - the time you want the event to start
	 * @param datacentreID
	 *            - the datacentre ID of the event
	 * @param nodeID
	 *            - the unique ID of the node you want to update
	 * @return new event
	 */
	public static SubscriptionUpdateSingleNodeEvent create(long pStartTime, int datacentreID, int nodeID)
	{
		return new SubscriptionUpdateSingleNodeEvent(pStartTime, datacentreID, nodeID);
	}

	/**
	 * generates a single node subscription update at a set time with a random time component
	 */
	@Override
	protected void generateEvents()
	{
		EventQueue.getInstance().addEvent(SubscriptionUpdateSingleNodeEvent.create(mStartTime + this.getNextUpdatePeriod(), datacentre_index, updatenode));
		numEventsGenerated++;
	}

	/**
	 * performs an update on the specified node
	 */
	@Override
	protected boolean performEvent()
	{
		logger.debug("Performing SubscriptionUpdateSingleNodeEvent...");
		boolean continueSimulation = true;

		// update node subscriptions
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
			SubscriptionsModuleRunner.getInstance().getSubscriptionNetwork(datacentre_index).updateNodeSubscription(updatenode);
		}
		else
		{
			logger.debug(TimeManager.getTimeString(World.getInstance().getTime()) + ": NO FailFix event since last subscription update " + TimeManager.getTimeString(timeOfLastUpdate) + "- not updating");
		}
		

		return continueSimulation;
	}
}