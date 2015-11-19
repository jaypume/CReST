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
 * @created 02 Aug 2011
 */
package sim.module.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.event.StopSimEvent;
import sim.module.Module;
import sim.module.event.configparams.EventsModuleConfigParams;
import sim.module.log.Log;
import sim.module.sim.configparams.SimModuleConfigParams;
import sim.module.userevents.bo.EventFileReader;
import sim.module.userevents.configparams.UserEventsModuleConfigParams;
import utility.time.TimeManager;

/**
 * Event class which is used as an initial event to generate all other starting
 * events. After execution generates another future EventSpawner which will
 * generate another set of events.
 */
public class EventSpawner extends Event
{
	
	public static Logger logger = Logger.getLogger(EventSpawner.class);
	
    // Member variables
    private final boolean    isInitialEvent;
    private final long       mSpawnerPeriod;
 
    
    /**
     * Constructor
     * 
     * @param pStartTime
     *            the time when this event should happen.
     * @param pIsInitialEvent
     *            this should be true if this is the first event spawner to be
     *            generated.
     * @param pSpawnerPeriod
     *            the period in between event spawner events.
     */
    private EventSpawner(final long pStartTime, final boolean pIsInitialEvent, final long pSpawnerPeriod)
    {
    	//This is not datacentre specific, so set dcID=-1
        super(pStartTime, -1);
        mSpawnerPeriod = pSpawnerPeriod;
        isInitialEvent = pIsInitialEvent;
        
        logger.info(TimeManager.log("New EventSpawner Event"));
    }

    /**
     * Method to create a new instance of an event spawner. This event can be
     * added to the event queue and will generate a number of random events
     * following a set distribution.
     * 
     * @param pStartTime
     *            The time after which the generated events will start occuring.
     * @param pIsInitialEvent
     *            True if this is the first event to be put into the queue (i.e.
     *            needs to generate a final event).
     * @return The event spawner event.
     */
    public static Event create(final long pStartTime, final boolean pIsInitialEvent, final long pSpawnerPeriod)
    {
        return new EventSpawner(pStartTime, pIsInitialEvent, pSpawnerPeriod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {

        // If the pseudorandom aspect of the simulator are on (i.e. have not
        // been disabled) then create the necessary events for simulation.
        if (Module.EVENTS_MODULE.isActive()) {
            EventQueue queue = EventQueue.getInstance();

            // Generate another EventSpawner in the future.
            queue.addEvent(EventSpawner.create(time() + mSpawnerPeriod, false, TimeManager.daysToSimulationTime(((EventsModuleConfigParams) Module.EVENTS_MODULE.getParams()).getSpawnerFrequency())));
            numEventsGenerated++;

            // Generate an end simulation event if one has not already been
            // created, and if the user has specified an end time.
            if (isInitialEvent) {
            	final long endTime = ((SimModuleConfigParams) Module.SIM_MODULE.getParams()).getSimulationEndTime();
       
                if (endTime != 0)
                {
                    queue.addEvent(StopSimEvent.create(endTime));
                    numEventsGenerated++;
                }
            }
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#performEvent()
     */
    @Override
    protected boolean performEvent()
    {
        boolean continueSimulation = true;

        // If user events are enabled and this is the first event spawner to be
        // executed, then create an event to immediately read in user events
        // from file.
        if (Module.USER_EVENTS_MODULE.isActive() && isInitialEvent)
        {
            EventQueue.getInstance().addEvent(EventFileReader.create(0, 
            		((UserEventsModuleConfigParams) Module.USER_EVENTS_MODULE.getParams()).getFilename()));
            numEventsGenerated++;
        }

        //TODO - set user events only by hand here for debugging
        //This doesnt seem to work! [JC May '12: what doesn't work? - who wrote this comment, it seems to work to me]
        if (Module.EVENTS_MODULE.isActive()) 	
        {
            if (isInitialEvent)
            {
                // Generate all the module event threads if this is the first
                // time an event spawner has been executed
                
                //Get all active module threads to generate events
                List<AbstractModuleEventThread> threads = new ArrayList<AbstractModuleEventThread>();
                threads.addAll(ModuleEventThreadFactory.getModuleEventThreads());
                //Generate events for all active modules
                for(AbstractModuleEventThread t: threads) {
                	numEventsGenerated += t.generateEvents();	
                }
            }
        }

        return continueSimulation;
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
}
