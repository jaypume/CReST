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
 * Created on 7 Sep 2011
 */
package sim.module.subscriptions.event;

import org.apache.log4j.Logger;

import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.log.LogManager;
import sim.module.log.event.LogEvent;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.physical.World;
import utility.Debug;
import utility.time.TimeManager;

/**
 * Event to print current statistics to a log file.
 */
public class SubsLogEvent extends LogEvent
{
	public static Logger logger = Logger.getLogger(SubsLogEvent.class);

    // Member variables.
    private final long mLogPeriod;

    public final static int TIME_STRING_COLUMN = 0;
    public final static int FAILURES_COLUMN_PERCENT = 1;
    public final static int INCONSISTENCY_COLUMN_PERCENT = 2;
    public final static int NETWORK_LOAD_COLUMN = 3;
    
    public final static String COLUMN_TITLE_STRING = "Simulation Time, " +
    		"Servers Working (%), Inconsistency (%), Network Load (Hops)";
    
    /**
     * Constructor.
     * 
     * @param pStartTime
     *            the time at which to execute this event
     */
    protected SubsLogEvent(long pStartTime)
    {
    	//This event is non dc-specific, so passing dcID=-1
        super(pStartTime); 

        mLogPeriod = SubscriptionsModuleRunner.TIME_BETWEEN_LOGS;
    }

    /**
     * Create and return a new log event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static SubsLogEvent create(final long pStartTime)
    {
        return new SubsLogEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new SubsLogEvent(mStartTime + mLogPeriod));
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

        logger.debug(TimeManager.log("Performing log event for each datacentre..."));
        
        // Create a log for each datacentre and print it to file via the log manager.
        for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
        	LogManager.writeLog(SubscriptionsModuleRunner.getInstance().getLogWriter(i), toLog(i));
        }
        
        return continueSimulation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#toLog()
     */
    protected Log toLog(int dc_id)
    {
        Log log = new Log();

        //the datacentre to log
        logger.debug("Logging results for dc_number="+dc_id);
        logger.info(Debug.getMemoryUsage());
        
        // Fetch all the values for the various modules we want to log.
        final String timeString = "\""+TimeManager.getTimeString(mStartTime)+"\"";
        final String failures = getFailureString(dc_id);
        final String inconsistencies = getPercentInconsistent(dc_id);
        final String networkLoad = getNetworkLoad(dc_id);

        // Add the vales to the log object and return it.
        log.add(timeString);
        log.add(failures);
        log.add(inconsistencies);
        log.add(networkLoad);

        return log;
    }
}
