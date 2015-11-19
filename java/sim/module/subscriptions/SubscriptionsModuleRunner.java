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
package sim.module.subscriptions;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.EventQueue;
import sim.module.failure.event.FailureEvent;
import sim.module.failure.event.FailureEvent.FailType;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.subscriptions.bo.SubscriptionNetworkManager;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.subscriptions.event.SubsLogEvent;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

//TODO - JC May 2012: Do something clever.  If we have a timer that updates every (1 minute, say).  Then
//if there has been no failure event after that time, set TRUE. Only do subscription update events
//if value is false.  This will allow us to skip forward to the next failure/fix 
//if there are no failures for a long period.
public class SubscriptionsModuleRunner extends AbstractModuleRunner
{
	public static Logger logger = Logger.getLogger(SubscriptionsModuleRunner.class);	

	private static SubscriptionsModuleRunner instance = null;
	public static final long TIME_BETWEEN_LOGS = TimeManager.secondsToSimulationTime(1);
	private ArrayList<SubscriptionNetworkManager> datacentreNetworkManager; //Subscription network managers. One for each datacentre
	private long timeOfLastUpdate = 0; //simulation time of last subscription update
	private long timeOfLastFailOrFix = 0; //simulation time of last failure or fix event	
	private static int counter = 0;

	protected SubscriptionsModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static SubscriptionsModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new SubscriptionsModuleRunner();
        }
        return instance;
	}

	@Override
	public void worldUpdated(World w) {
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.SUBSCRIPTION_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.info("Subscriptions module is on.  Initialising subscription networks...");
			
			long timestamp = 0; //TODO - maybe use the current world time?
			
			//it is important that this is cleared/reset each time world is updated
			datacentreNetworkManager = new ArrayList<SubscriptionNetworkManager>(); 
			
			//for each datacentre, generate a subscription network manager
			for(Datacentre dc: World.getInstance().getDatacentres()) {

		    	SubscriptionNetworkManager subsNetwork = new SubscriptionNetworkManager(dc.getID());
		    	subsNetwork.initialise(dc.getServerIPs(), dc.getServerIDs(), 
		    			(SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams(), timestamp);
		    	logger.debug("Subscription network initialised for datacenter " + dc.getID());
		    	datacentreNetworkManager.add(subsNetwork);	
			}	
		} else {
			//do nothing - module not on
		}
	}
	
	public SubscriptionNetworkManager getSubscriptionNetwork(int datacentre_index) {
		if(datacentre_index>=0 && datacentre_index<datacentreNetworkManager.size()) {
			return datacentreNetworkManager.get(datacentre_index);
		} else {
			logger.fatal("Out of bounds: Requesting datacentre_index = " + datacentre_index + ". Exiting system.");
			System.exit(-1);
			return null; //we wont get here.
		}
	}

	@Override
	public void update(Observable observable, Object object) {
		
		//If the event is a failure, we should do something with it...
		
		if (isFailureEvent(object)) {
			
			FailureEvent event = (FailureEvent) object;
			logger.debug("There has been a failure event... "+ event);		
			
			//Check that the object type is a server
			if(event.getObjectType().equals(ObjectType.server)) 
			{
				//Set time that the failure of fix happened
				timeOfLastFailOrFix = World.getInstance().getTime();
				
				//TODO - JC, May 2012: surely there is an easier way to get a dc_id value?!
				int dc_id = World.getInstance().getDatacentre(World.getInstance().getServer(event.getID()).getIP()).getID();
				
				logger.debug("Server failure in datacentre " + dc_id + " updating node status from within SubscriptionModuleRunner...");
				
				if(event.getFailEventType().equals(FailType.hard) || event.getFailEventType().equals(FailType.soft)){
					//if it is a hard or soft fail, update node status to false
            		getSubscriptionNetwork(dc_id).updateNodeStatus(event.getID(),false);
				} else if(event.getFailEventType().equals(FailType.fix)) {
					//if it is a fix, update node status to true
					getSubscriptionNetwork(dc_id).updateNodeStatus(event.getID(),true);
				} else {
					//we should never get here, throw an error
					logger.fatal("Unknown failure type: " + event);
					logger.fatal("Exiting System");
					System.exit(-1);
				}
				
			} else {
				logger.debug("Object is not a server, ignoring..." + event.getObjectType());
			}			
		}

		//we don't care about any other event types, so do nothing else...
	}

	@Override
	public String getLogFileName() {
		return "consistency";
	}

	@Override
	protected String getLogTitleString() {
		return SubsLogEvent.COLUMN_TITLE_STRING;
	}

	@Override
	public boolean isActive() {
		return Module.SUBSCRIPTION_MODULE.isActive();
	}
	
	/**
	 * Simulation time of last subscription update
	 * @return time
	 */
	public long getTimeOfLastUpdate()
	{
		return timeOfLastUpdate;
	}

	/**
	 * Simulation time of last failure or fix event
	 * @return time
	 */
	public long getTimeOfLastFailOrFix()
	{
		return timeOfLastFailOrFix;
	}

	/**
	 * Sets simulation time of last subscription update
	 * @param timeOfLastUpdate
	 */
	public void setTimeOfLastUpdate(long timeOfLastUpdate)
	{
		this.timeOfLastUpdate = timeOfLastUpdate;
	}
}
