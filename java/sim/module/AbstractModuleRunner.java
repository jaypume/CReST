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
package sim.module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import sim.WorldListener;
import sim.module.broker.BrokerModuleRunner;
import sim.module.costs.CostsModuleRunner;
import sim.module.failure.FailureModuleRunner;
import sim.module.failure.event.FailureEvent;
import sim.module.log.LogModuleRunner;
import sim.module.pricing.PricingModuleRunner;
import sim.module.replacements.ReplacementsModuleRunner;
import sim.module.service.ServiceModuleRunner;
import sim.module.subscriptions.SubscriptionsModuleRunner;
import sim.module.thermal.ThermalModuleRunner;
import sim.module.thermal.event.ThermalGridEvent;
import sim.physical.World;

/**
 * WARNING: When creating a concrete sub-class of AbstractModuleRunner, don't forget to
 * add the ConcreteModuleRunner instance to the AbstractModuleRunner.moduleRunners array 
 */
public abstract class AbstractModuleRunner implements WorldListener, Observer{

	//Extend this class for each Module using Singleton ConcreteModuleRunner.
	
	private static AbstractModuleRunner[] moduleRunners = new AbstractModuleRunner[] {
		
		//All Concrete subclasses of AbstractModuleRunner must be added to array, below...
		FailureModuleRunner.getInstance(),       //Failure
		SubscriptionsModuleRunner.getInstance(), //Subscriptions
		ThermalModuleRunner.getInstance(),	     //Thermal
		ServiceModuleRunner.getInstance(),       //Service
		CostsModuleRunner.getInstance(),         //Costs
		BrokerModuleRunner.getInstance(),        //Broker
		PricingModuleRunner.getInstance(),
		LogModuleRunner.getInstance(),           //Logging
		ReplacementsModuleRunner.getInstance()	 //Replacements
	};
	
	/** Results logs for the module*/
    protected ArrayList<BufferedWriter> resultsLogs;
	
    public static Logger logger = Logger.getLogger(AbstractModuleRunner.class);
 
    
	/**
	 * Get a list of all WorldListeners 
	 * 
	 * ConcreteModuleRunner objects are returned as a list of WorldListeners
	 * 
	 * @return List of WorldListeners
	 */
	public static List<WorldListener> getWorldListeners() {
		
		List<WorldListener> listeners = new ArrayList<WorldListener>();

		for(WorldListener module: moduleRunners) {
			listeners.add(module);
		}
		return listeners;
	}
	
	/**
	 * Get a list of all concrete ModuleRunners
	 * 
	 * @return List of AbstractModuleRunners
	 */
	public static List<AbstractModuleRunner> getModuleRunners() {
		List<AbstractModuleRunner> modulesList = new ArrayList<AbstractModuleRunner>();
		for(AbstractModuleRunner m: moduleRunners) {
			modulesList.add(m);
		}
		return modulesList;
	}
	
	/**
	 * Called from EventQueue when there has been a new event. Act on event...
	 */
	@Override
	public abstract void update(Observable o, Object arg);

	/**
	 * Is the arg Object a FailureEvent?
	 * @param arg - the Object to check
	 * @return - true if arg Object is a FailureEvent, false otherwise
	 */
	public boolean isFailureEvent(Object arg) {
		return arg.getClass().getName().equals(FailureEvent.class.getName());
	}
	
	public boolean isThermalGridEvent(Object arg) {
		return arg.getClass().getName().equals(ThermalGridEvent.class.getName());
	}
	
	/**
	 * Called from SimulationRunner when the world has been updated/reset.  
	 * 
	 * Initialise the ModuleRunner...
	 */
	@Override
	public abstract void worldUpdated(World w);

	/**
	 * Return the log file name for the ModuleRunner
	 * @return name
	 */
	public abstract String getLogFileName();
	
	/**
	 * Initialise the LogManager for the module using the passed directory name.
	 * 
	 * @param dirName - the directory name of the module log file
	 */
	public void initLogs(String dirName) {
		
		resultsLogs = new ArrayList<BufferedWriter>();
		
		try {
			//Create a log for each datacentre
			for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
				String fileName = dirName+"_" + getLogFileName() + "_dc"+ i + "_"+World.getInstance().getDatacentre(i).getName() + ".csv";
				File file = new File(fileName);
				
				// Try to create/open a file with the given filename.
		        // Write column headers to log file
		        try
		        {
		            resultsLogs.add(new BufferedWriter(new FileWriter(file)));
		            writeLogColumnHeaders(resultsLogs.get(i));
		            logger.info("Created new results log: '" + fileName + "'");
		        } catch (IOException e)
	            {
	                logger.error("Could not find a suitable filename for the results logging module: " + e.getMessage());
	            }
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/** Get the column title string for the log files for this module runner */
	protected abstract String getLogTitleString();
	

	/**
	 * Write column headers to logfile.
	 * 
	 * @param pBW
	 *            the buffered writer to use to write to file.
	 */
	protected void writeLogColumnHeaders(BufferedWriter pBW)
	{
	    try
	    {
	        pBW.append(getLogTitleString() + "\n");
	        pBW.flush();
	    }
	    catch (IOException e)
	    {
	        System.err.println();
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Return the Log Writer for a datacenter
	 * @param dc_id - the ID of the datacenter to log
	 * @return BufferedWriter for writing the log
	 */
	public BufferedWriter getLogWriter(int dc_id) {
		return resultsLogs.get(dc_id);
	}
	
	public abstract boolean isActive();
	
//	/**
//	 * Is Module active?
//	 * @return
//	 */
//	public boolean isActive() {
//		return myModule.isActive();
//	}
}
