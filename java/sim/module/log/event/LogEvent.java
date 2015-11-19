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
package sim.module.log.event;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.costs.CostsModuleRunner;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.ServiceManager;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.module.thermal.ThermalModuleRunner;
import sim.module.thermal.bo.ThermalGrid;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.Debug;
import utility.time.LengthOfTime;
import utility.time.TimeManager;
import config.SettingsManager;

/**
 * Event to print current statistics to a log file.
 */
public class LogEvent extends Event
{
	public static Logger logger = Logger.getLogger(LogEvent.class);

    // Member variables.
    private final long mLogPeriod;

    public final static int TIME_STRING_COLUMN = 0;
    public final static int FAILURES_COLUMN_PERCENT = 1;
    public final static int COST_COLUMN = 2;
    public final static int UTILISATION_COLUMN_PERCENT = 3;
    public final static int SERVICES_TOTAL_COLUMN = 4;
    public final static int SERVICES_FAILED_COLUMN = 5;
    public final static int SERVICES_COMPLETE_COLUMN = 6;
    public final static int SERVICES_RUNNING_COLUMN = 7;
    public final static int SERVICES_QUEUED_COLUMN = 8; //services that could not be started are queued
    public final static int INCONSISTENCY_COLUMN_PERCENT = 9;
    public final static int NETWORK_LOAD_COLUMN = 10;
    
    public final static String columnTitleString = "Simulation Time, Servers Working (%), Costs, " +
        	"Server Utilisation (%), Services (Total), Services (Failed), Services (Completed), " +
        	"Services (Running), Services (Queued), Inconsistency (%), Network Load (Hops)";
    
    
    
//    private int dc_to_log = -1; //the number of the datacentre to log
    
    /**
     * Constructor.
     * 
     * @param pStartTime
     *            the time at which to execute this event
     */
    protected LogEvent(long pStartTime)
    {
    	//This event is non dc-specific, so passing dcID=-1
        super(pStartTime, -1); 
        
        if(logger.isDebugEnabled()) {
        	logger.debug(TimeManager.log("Created new log event with start time " + TimeManager.getTimeString(pStartTime)));
        	logger.debug("//TODO - make log period a configuration parameter");
        }
  
        // TODO Make passed as parameter from config.
        mLogPeriod = SettingsManager.getInstance().getTimeBetweenLogs();
    }

    /**
     * Create and return a new log event.
     * 
     * @param pStartTime
     *            the time at which to execute the log event.
     * @return the new log event.
     */
    public static LogEvent create(final long pStartTime)
    {
        return new LogEvent(pStartTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.event.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        EventQueue.getInstance().addEvent(new LogEvent(mStartTime + mLogPeriod));
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

        
        logger.info(TimeManager.log("Performing log event for each datacentre..." + this.getClass()));
        
        // Create a log and print it to file via the log manager.
        for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
        	World.getInstance().getLogManager().log(i, toLog(i));
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
        logger.info("Logging results for dc_number="+dc_id);
        logger.info(Debug.getMemoryUsage());
        
        // Fetch all the values for the various modules we want to log.
        final String timeString = "\""+TimeManager.getTimeString(mStartTime)+"\"";
        final String failures = getFailureString(dc_id);
        final String pricing = getCostsString(dc_id);
        final String utilisation = getUtilisationString(dc_id);
        final String services = getServiceString(dc_id);
        final String inconsistencies = getPercentInconsistent(dc_id);
        final String networkLoad = getNetworkLoad(dc_id);

        // Add the vales to the log object and return it.
        log.add(timeString);
        log.add(failures);
        log.add(pricing);
        log.add(utilisation);
        log.add(services);
        log.add(inconsistencies);
        log.add(networkLoad);

        return log;
    }

    /**
     * Get a string representation of the utilisation statistics
     * 
     * @return a string of utilisation values.
     */
    protected String getUtilisationString(int dcNumber)
    {
    	DecimalFormat df = new DecimalFormat("###.000");
    	return df.format(World.getInstance().getDatacentre(dcNumber).getUtilisation());
    }

    /**
     * Get a string representation of the cost statistics for each
     * datacentre.
     * 
     * @return a string of costs.
     */
    protected String getCostsString(int dcNumber)
    {
    	if(Module.COSTS_MODULE.isActive()) {
    		DecimalFormat df = new DecimalFormat("#####.00");
        	return df.format(CostsModuleRunner.getInstance().getCosts(dcNumber).totalCost(LengthOfTime.HOUR.getTimeInSeconds()));
    	} else {
    		logger.warn("Costs module is not active...costs are null");
    		return "na";
    	}
    }

    /**
     * Get a string representation of the failure statistics for a datacentre
     * 
     * @param dcNumber - the number of the dc to log
     * 
     * @return a string of failure values.
     */
    protected String getFailureString(int dcNumber)
    {
    	DecimalFormat df = new DecimalFormat("###.000");
    	return df.format(calcPercentageAliveServers(dcNumber));
    }

    /**
     * Get a string representation of the failure statistics for a datacentre
     * 
     * @param dcNumber - the number of the dc to log
     * 
     * @return a string of failure values.
     */
    protected String getTemperatureString(int dcNumber)
    {
    	DecimalFormat df = new DecimalFormat("###.000");
    	return df.format(calcTemperature(dcNumber));
    }
    
    /**
     * Get a string representation of the inconsistency statistics.
     * 
     * @return a string of failure values.
     */
    protected String getPercentInconsistent(int dcNumber)
    {
    	logger.info(TimeManager.log("Calc inconsistent (1) " + Debug.getMemoryUsage()));
    	
    	if(Module.SUBSCRIPTION_MODULE.isActive()) {
	    	DecimalFormat df = new DecimalFormat("###.000");
	    	return df.format(SubscriptionsModuleRunner.getInstance().
	    			getSubscriptionNetwork(dcNumber).percentinconsistent()); 

	    } else {
    		return "-";
    	}
    }

    /**
     * Get a string representation of the network load statistics.
     * 
     * @return a string of failure values.
     */
    protected  String getNetworkLoad(int dcNumber)
    {
    	logger.info(TimeManager.log("Get network load " + Debug.getMemoryUsage()));
    	
    	int networkLoad = 0;
    	
    	if(Module.SUBSCRIPTION_MODULE.isActive()) {
	    	DecimalFormat df = new DecimalFormat("###");
	    	
	    	//get network load since last log and reset to zero
    		networkLoad = SubscriptionsModuleRunner.getInstance().getSubscriptionNetwork(dcNumber).getNetworkLoad();
    		SubscriptionsModuleRunner.getInstance().getSubscriptionNetwork(dcNumber).resetNetworkLoad();

	    	return df.format(networkLoad);
	    	
    	} else {
    		return "-";
    	}
    }
    
    /**
     * Get a string representation of the service statistics.
     * 
     * @return a string of service values.
     */
    protected String getServiceString(int dcNumber)
    {   
        int[] services = new int[5];

        ServiceManager sm = ServiceModuleRunner.getInstance().getServiceManager(dcNumber);
   
        services[0] = sm.getRunningServices(); //running services 
        services[1] = sm.getFailedServices();//failed services    
        services[2] = sm.getQueuedServices(); //queued services
        services[3] = sm.getCompletedServices(); //completed services
        services[4] = sm.getTotalServices(); //total services

        logger.info(TimeManager.log("Logging services: DC#=" + dcNumber+ " [total=" + services[0] + ", failed=" + services[1] + ", complete=" + services[2] + ", running=" + services[3] + ", queued=" + services[4] +"]"));
        
        if(logger.isInfoEnabled()) {

	        logger.info(TimeManager.log(Debug.getMemoryUsage()));
	        
	        //lets output some data on subscriptions
	        if(Module.SUBSCRIPTION_MODULE.isActive()) {
	        	for(Datacentre d: World.getInstance().getDatacentres()) {

        			logger.info(SubscriptionsModuleRunner.getInstance().
        					getSubscriptionNetwork(d.getID()).getDebugData());
	        	}
	        }
	        
//	        logger.info(World.getInstance().getSubscriptionGen().getDebugData());
	        
	        //lets output sime data on the event queue
	        logger.info("Size of event queue: " + EventQueue.getInstance().size());
        }
        return utilArrayToString(services);
    }

    /**
     * Calculate the percentage of servers that are currently alive in datacentre
     * 
     * @return the percentage of servers that are currently alive 
     */
    protected double calcPercentageAliveServers(int dcNumber)
    {

        double numFailed = World.getInstance().getDatacentre(dcNumber).getNumFailedServers();
        double numServers = World.getInstance().getDatacentre(dcNumber).getNumServers();
        logger.info(TimeManager.log("Calc alive servers (2) " + Debug.getMemoryUsage()));
        return (1.0 - ((numFailed) / numServers)) * 100;
    }
    
    /**
     * Calculate the teperature of a datacentre
     * 
     * @return the tempearture of the datacentre 
     */
    protected double calcTemperature(int dcNumber)
    {

    	ThermalGrid grid = ThermalModuleRunner.getInstance().getThermalGrid(dcNumber);
        return grid.getAverageTemperature();
    }
    
    protected String utilArrayToString(final int[] values_array)
    {
        String string = "";
	    for (int i = 0; i < values_array.length; i++)
	    {
	    	if (i !=0) string += ",";
	        string += values_array[i];
	    }
        return string;
    }
    
    protected String utilArrayToString(final double[] values_array, DecimalFormat df)
    {
        String string = "";
	    for (int i = 0; i < values_array.length; i++)
	    {
	    	if (i !=0) string += ",";
	        string += df.format(values_array[i]);
	    }
        return string;
    }
}
