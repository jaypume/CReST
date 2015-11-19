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
package sim.module.gui.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.log.Log;
import utility.time.TimeManager;
import config.SettingsManager;

/**
 * Event to update the Graphical User Interface
 */
public class UpdateGUIEvent extends Event
{
	public static Logger logger = Logger.getLogger(UpdateGUIEvent.class);

    // Member variables.
    private final long period;

    /**
     * Constructor.
     * 
     * @param pStartTime
     *            the time at which to execute this event
     */
    private UpdateGUIEvent(long pStartTime)
    {
    	//This event is non dc-specific, so passing dcID=-1
        super(pStartTime, -1); 
        
        if(logger.isDebugEnabled()) {
        	logger.debug(TimeManager.log("Created new GUI Update event with start time " + TimeManager.getTimeString(pStartTime)));
        }
  
        // TODO Make passed as parameter from config.
        period = SettingsManager.getInstance().getTimeBetweenGUIUpdates();
    }

    /**
     * Create and return a new GUI Update event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static UpdateGUIEvent create(final long pStartTime)
    {
        return new UpdateGUIEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new UpdateGUIEvent(mStartTime+period));
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#performEvent()
     */
    @Override
    protected boolean performEvent()
    {
        boolean continueSimulation = true;

        logger.debug(TimeManager.log("Performing GUI event..."));
        //do nothing.  This event is just a flag for the GUI

        return continueSimulation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#toLog()
     */
    protected Log toLog()
    {
        Log log = new Log();

        //do nothing
        return log;
    }
}
