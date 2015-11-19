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

import sim.module.Module;
import sim.module.event.AbstractModuleEventThread;
import sim.module.event.EventQueue;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.physical.World;
import config.SettingsManager;

public class SubscriptionModuleEventThread extends AbstractModuleEventThread {

	public SubscriptionModuleEventThread() {
		logger = Logger.getLogger(SubscriptionModuleEventThread.class);
	}
	
	@Override
    /**
     * Generates the subscription update events
     * 
     * To change this to working with single node updates change
     * SingleNodeUpdate to true
     * 
     */
	public int generateEvents() {
	
		int newEventsGenerated = 0;
        EventQueue queue = EventQueue.getInstance();
        
        logger.info("Adding Subscription Events to the event queue...");
        
        if(((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).getSingleNodeUpdate()) //do we update subscriptions per node?
        {
        	logger.info("Adding SingleNodeUpdate Subscription Event(s)...");
            
        	for (int dcID=0; dcID < World.getInstance().getNumberOfDatacentres(); dcID++) {
        		for (int serverID: World.getInstance().getDatacentre(dcID).getServerIDs()) {
        			//create initial update event:- pass serverID as timestamp (rather than 0), so each node update performed in order
        			queue.addEvent(SubscriptionUpdateSingleNodeEvent.create(0, dcID, serverID)); //update single nodes
                         			
        			newEventsGenerated++;
        		}
        	}
        }
        else //update node subscriptions all at once
        {
        	logger.info("Adding AllNodesUpdate Subscription Event(s)...");
            
            queue.addEvent(SubscriptionUpdateAllEvent.create(0)); //update all nodes at once
        }
                  		
		//If we are using a DC-Poller, add first Poller event...
        if(((SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams()).getProtocolType().equals(Protocol.CENTRAL)) {
        	for (int dcID=0; dcID < World.getInstance().getNumberOfDatacentres(); dcID++) {
				queue.addEvent(PollerUpdateEvent.create(0, dcID));
				newEventsGenerated++;
			}
        }	
		
        logger.info(newEventsGenerated + " new Subscription Event(s) added");
        
        //Adding Logging event for subscriptions module...
        logger.info("Adding Logging event for subscriptions. First log time = " + SettingsManager.getInstance().getFirstLogTime());
        queue.addEvent(SubsLogEvent.create(SettingsManager.getInstance().getFirstLogTime()));
        newEventsGenerated++;
        
        logger.info("New Logging Event added");       
		
		return newEventsGenerated;        
	}
}
