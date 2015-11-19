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
 * @created 19 Jul 2011
 */
package sim.event;

import sim.module.event.Event;
import sim.module.log.Log;
import utility.time.TimeManager;

/**
 * Event to stop the simulator from running.
 */
public class StopSimEvent extends Event
{
    /**
     * Constructor.
     * 
     * @param pStartTime
     *            the time at which to execute this event.
     */
    private StopSimEvent(final long pStartTime)
    {
    	//This is not datacentre specific, so set dcID=-1
        super(pStartTime, -1);
    }

    /**
     * Create a stop event which should run at the given start time. The stop
     * event will cause the simulation event loop to exit.
     * 
     * @param pStartTime
     *            The time that this event should happen.
     * 
     * @return The stop event.
     */
    public static StopSimEvent create(final long pStartTime)
    {
        return new StopSimEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#performEvent()
     */
    @Override
    public boolean performEvent()
    {
    	logger.warn(TimeManager.log("Performing StopSimEvent..."));
        // Return false to indicate it is time to finish the simulation.
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents(sim.EventQueue)
     */
    @Override
    protected void generateEvents()
    {
        // No events need to be generated.
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
