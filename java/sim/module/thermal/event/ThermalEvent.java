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
 * Created on 15 Aug 2011
 */
package sim.module.thermal.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.thermal.ThermalModuleRunner;
import utility.time.TimeManager;

/**
 * Event to update the temperature of all the datacentres.
 */
public class ThermalEvent extends Event
{
	public static Logger logger = Logger.getLogger(ThermalEvent.class);
	
	 // TODO: Updates are fixed to be at every simulated second. This makes
    // the simulation very slow so a better solution is needed.
	public static final long TIME_TO_NEXT_EVENT = TimeManager.secondsToSimulationTime(1);

    /**
     * Constructor
     * 
     * @param pStartTime
     *            the time at which to execute this event.
     */
    private ThermalEvent(long pStartTime)
    {
    	//TemperatureEvents are non datacentre-specific.  Pass dc_index = -1
        super(pStartTime, -1);
    }

    /**
     * Create and return a new TemperatureEvent object.
     * 
     * @param pStartTime
     *            the time at which to execute the event.
     * @return the new event object.
     */
    public static ThermalEvent create(final long pStartTime)
    {
        return new ThermalEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
		//TODO AS 5.9.12- finish
       	//logger.info("Adding Thermal Grid update Event to the event queue...");
        //EventQueue.getInstance().addEvent(ThermalGridEvent.create(mStartTime));
        
        EventQueue.getInstance().addEvent(ThermalEvent.create(mStartTime + ThermalEvent.TIME_TO_NEXT_EVENT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#performEvent()
     */
    @Override
    protected boolean performEvent()
    {
        final boolean continueSimulation = true;

       	ThermalModuleRunner.getInstance().updateTemperature(time());
        
        return continueSimulation;
    }
}
