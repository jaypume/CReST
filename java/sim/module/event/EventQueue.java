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
package sim.module.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.event.StartSimEvent;
import sim.event.StopSimEvent;

/**
 * Queue class which will hold all events to be processed by the simulator. New
 * events can be added to the queue with easy and also easily popped from the
 * queue.
 * 
 * The queue is designed as two separate queues, an ordered queue (the active
 * queue) and an unordered queue (the secondary queue). The active queue holds a
 * number of events upon a maximum which are to be executed soonest. The
 * secondary queue holds all other event and is only sorted which more events
 * need to be pulled out for the active queue.
 */
public class EventQueue extends Observable
{
	public static Logger logger = Logger.getLogger(EventQueue.class);
	
    // Debug
    public static final boolean USE_DOUBLE_QUEUE   = true;

    // Constants
    private static final double ACTIVE_LOWER_BOUND = 0.1;
    private static final double ACTIVE_UPPER_BOUND = 0.4;
    private static final int    MIN_NUM_EVENTS     = 10;

    // Singleton instance
    private static EventQueue   instance           = null;

    // Member variables
    private ArrayList<Event>    mEvents            = new ArrayList<Event>();
    private ArrayList<Event>    mActiveEvents      = new ArrayList<Event>();
    private ArrayList<Event>    mRestEvents        = new ArrayList<Event>();

    private boolean             mIsSorted          = false;
    private int                 mNumTimesSorted    = 0;

    /**
     * Basic constructor.
     */
    private EventQueue()
    {

    }

    /**
     * Gets the singleton event queue, creating a new one if it has not yet been
     * created.
     * 
     * @return the singleton event queue.
     */
    public static EventQueue getInstance()
    {
        if (instance == null)
        {
        	logger.debug("Creating new EventQueue() object");
            instance = new EventQueue();
        }

        return instance;
    }
    
    /**
     * Destroy singleton EventQueue object.
     * 
     * WARNING This method will destroy the EventQueue object.  Use with care!!!
     *
     * @return true if EventQueue is destroyed, false if no world object exists to destroy
     */
    public boolean destroy(){
 
    	if(instance != null) {
    		logger.debug("Clearing event queue...");
    	    mEvents.clear();
    	    mActiveEvents.clear(); 
    	    mRestEvents.clear();
    	    mIsSorted = false;
    	    mNumTimesSorted = 0;
    	    logger.debug("Size is now " + size());
    	    
    		instance = null; //does this do anything?
    		
    		//notify observers of simulation end
    		setChanged();
    		notifyObservers(StopSimEvent.create(-1));
    		
    		return true;   		
    	} else {
    		logger.info("No EventQueue object exists to destroy...");
    		return false;
    	} 		
    }

    /**
     * Clears the queue and pushes an initial event to it.
     */
    public void reset(final long pSpawnerPeriod)
    {
        mEvents.clear();
        addEvent(EventSpawner.create(0, true, pSpawnerPeriod));
    }

    /**
     * Gets the next event from the queue. If there are no events left then it
     * will return an event to halt the simulation.
     * 
     * @return The next event in the queue.
     */
    public Event nextEvent()
    {
        Event event;

        // If there are events in the queue then get the one at the from, else
        // create and return a stop sim event, to finish the simulation.
        if (size() > 0)
        {
            event = pop();
        }
        else
        {
            event = StopSimEvent.create(-1);
        }

        return event;
    }

    /**
     * Get and remove the event from the front of the queue.
     * 
     * @return The event from the front of the queue.
     */
    private Event pop()
    {
        Event event;

        // TODO: Complete the double queue implementation.
        if (USE_DOUBLE_QUEUE)
        {
            // Double event queue code.
            event = mActiveEvents.get(0);
            mActiveEvents.remove(0);

            balanceQueues();
        }
        else
        {
            // Single event queue code.
            event = mEvents.get(0);
            mEvents.remove(0);
        }

        //Mark this observable object as changed.
        //Then notify all observers by call to their update() method
        logger.debug("Popping Event. Setting EventQueue changed and notifying observers...");
        setChanged();   
        notifyObservers(event);
        logger.debug("Finished notifying observers");

        return event;
    }

    /**
     * Add an event to the queue.
     * 
     * @param pEvent
     *            the event to add to the queue.
     */
    private void push(final Event pEvent)
    {
        if (USE_DOUBLE_QUEUE)
        {
            // Double event queue code.
            final int numEvents = mActiveEvents.size() + mRestEvents.size();

            // Push an event to the queue and resize it as necessary.
            if (numEvents < MIN_NUM_EVENTS)
            {
                addInOrder(mActiveEvents, pEvent);
            }
            else if (pEvent.time() > mActiveEvents.get(mActiveEvents.size() - 1).time())
            {
                mRestEvents.add(pEvent);
            }
            else
            {
                addInOrder(mActiveEvents, pEvent);
            }

            balanceQueues();
        }
        else
        {
            // Single event queue code.
            mIsSorted = false;
            mEvents.add(pEvent);
        }
    }

    /**
     * Order the queue.
     */
    public void sort()
    {
        if (USE_DOUBLE_QUEUE)
        {
            // Double queue code.
            Collections.sort(mRestEvents);
            mNumTimesSorted++;
        }
        else
        {
            // Single queue code.
            if (!isSorted())
            {
                Collections.sort(mEvents);
                mIsSorted = true;
                mNumTimesSorted++;
            }
        }

    }

    /**
     * Return the number of events in the queue.
     * 
     * @return the number of events in the queue.
     */
    public int size()
    {
        if (USE_DOUBLE_QUEUE)
        {
            // Double queue code.
            return mActiveEvents.size() + mRestEvents.size();
        }
        else
        {
            // Single queue code.
            return mEvents.size();
        }
    }

    /**
     * Resizes both the active and secondary queues if the number of events in
     * the active queue is not within suitable bounds.
     */
    private void balanceQueues()
    {
        final int numEvents = mActiveEvents.size() + mRestEvents.size();

        if (numEvents < MIN_NUM_EVENTS)
        {
            shiftAllToActiveQueue();
        }
        else
        {
            final double percentActive = ((double) mActiveEvents.size()) / numEvents;
            final int desiredNumActive = (int) (ACTIVE_UPPER_BOUND * numEvents);

            if (percentActive < ACTIVE_LOWER_BOUND)
            {
                // System.out.println("==================================");
                // System.out.println("MIN_EVENTS: " + MIN_NUM_EVENTS);
                // System.out.println("LOWER_BOUND: " + ACTIVE_LOWER_BOUND);
                // System.out.println("UPPER_BOUND: " + ACTIVE_UPPER_BOUND);
                // System.out.println("Number of events: " + numEvents);
                // System.out.println("Active events: " + mActiveEvents.size());
                // System.out.println("Other events: " + mRestEvents.size());
                // System.out.println("% active: " + percentActive);
                // System.out.println("% non-active: " + (1-percentActive));
                // System.out.println("==================================");

                sort();

                while (mActiveEvents.size() < desiredNumActive)
                {
                    addInOrder(mActiveEvents, mRestEvents.get(0));
                    mRestEvents.remove(0);
                }
            }
            else if (percentActive > ACTIVE_UPPER_BOUND)
            {
                while (mActiveEvents.size() > desiredNumActive)
                {
                    mRestEvents.add(mActiveEvents.get(mActiveEvents.size() - 1));
                    mActiveEvents.remove(mActiveEvents.size() - 1);
                }
            }
        }
    }

    /**
     * Moves all events from the secondary queue into the active queue.
     */
    private void shiftAllToActiveQueue()
    {
        while (mRestEvents.size() > 0)
        {
            Event event = mRestEvents.get(0);
            mRestEvents.remove(0);
            addInOrder(mActiveEvents, event);
        }
    }

    /**
     * Add an event to the given queue while maintaining the queue's order.
     * Assumes the given queue is already ordered.
     * 
     * @param pEvents
     *            the queue to add an event to.
     * @param pEvent
     *            the event to add to the queue.
     */
    private void addInOrder(ArrayList<Event> pEvents, Event pEvent)
    {
        if (pEvents.isEmpty())
        {
            pEvents.add(pEvent);
        }
        else
        {
            pEvents.add(pEvent);
            Collections.sort(pEvents);
        }
    }

    /**
     * Pushes a new event to the queue.
     * 
     * @param pEvent
     *            The event to add to the queue.
     */
    public void addEvent(final Event pEvent)
    {
        if (pEvent != null)
        {
            push(pEvent);
        }
    }

    /**
     * Check if the queue is currently sorted.
     * 
     * @return True if the queue is sorted, else false.
     */
    public boolean isSorted()
    {
        return mIsSorted;
    }

    /**
     * Get the number of times the queue has been sorted so far.
     * 
     * @return the number of times the queue has been sorted.
     */
    public int getNumTimesSorted()
    {
        return mNumTimesSorted;
    }
    
    /**
     * Send a start event to all observers
     */
    public void sendStartEvent() {
    	setChanged();
    	notifyObservers(StartSimEvent.create());
    }
}
