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
package sim.module.demand.event;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.demand.bo.Demand;
import sim.module.demand.configparams.DemandModuleConfigParams;
import sim.module.event.AbstractBrokerDemandModuleEventThread;
import sim.module.event.EventQueue;
import sim.physical.World;
import utility.time.TimeManager;

public class DemandModuleEventThread extends AbstractBrokerDemandModuleEventThread
{    
	public DemandModuleEventThread() 
	{
		logger = Logger.getLogger(DemandModuleEventThread.class);
	    START_TIME_DISTRIBUTION = 1000000;
	}

	/**
     * This creates demand events by inputting daily resource demands from external file.
     * The demand varies throughout the day according to square wave
     */
    protected int generateDemandEvents()
    {
    	int newEventsGenerated = 0;
    	
		logger.info("Creating initial demand events...");
		
        EventQueue queue = EventQueue.getInstance();

        //For each datacentre
        for (int i = 0; i < World.getInstance().mDatacentres.size(); i++)
        {
            //Create demand
        	Demand demand = new Demand(((DemandModuleConfigParams) Module.DEMAND_MODULE.getParams()).getFilename());
        	// Schedule event to update demand in 24hours
            //UpdateDemandEvent eventMan = new UpdateDemandEvent(UpdateDemandEvent.FIRST_UPDATE_DEMAND_EVENT, i, demand);
            
            Double currentDemand = demand.nextD(); //Get demand for next period (1 day)
            int position = demand.getCounter();
            double endDemand = demand.getDemand(position+1);

        	double range = endDemand - currentDemand;
        	double increment = range/3.0;

            Double dev = 0.02;
            //Produce square wave for day
            //eventMan.squareWaveEvent(0, currentDemand, dev, queue);
            
            //Add weekly events to start demands for the coming week
            queue.addEvent(new WeeklyDemandUpdate(TimeManager.daysToSimulationTime(0),i,currentDemand+(increment*0),dev));
            queue.addEvent(new WeeklyDemandUpdate(TimeManager.daysToSimulationTime(7),i,currentDemand+(increment*1),dev));
            queue.addEvent(new WeeklyDemandUpdate(TimeManager.daysToSimulationTime(14),i,currentDemand+(increment*2),dev));
            queue.addEvent(new WeeklyDemandUpdate(TimeManager.daysToSimulationTime(21),i,currentDemand+(increment*3),dev));
            
            //Create weekly update events using UpdateDemandEvent which is launched every month
            queue.addEvent(new UpdateDemandEvent(UpdateDemandEvent.FIRST_UPDATE_DEMAND_EVENT+TimeManager.daysToSimulationTime(28),i,demand));
            
            newEventsGenerated+=5;
        }
        
    	return newEventsGenerated;
    }
}
