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
package sim;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.event.configparams.EventsModuleConfigParams;
import sim.module.service.bo.Service;
import sim.module.sim.configparams.SimModuleConfigParams;
import sim.module.userevents.configparams.UserEventsModuleConfigParams;
import sim.physical.AirConditioner;
import sim.physical.World;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;
import config.ConfigParams;
import config.SimulatorConfiguration;
import config.XMLLoader;

/**
 * SimulationRunner is the main controller class for CReST. 
 * 
 * Call run() to start simulation.
 * 
 * Call stopSimulation() to send stop signal.
 */
public class SimulationRunner extends Thread 
{
	private static Logger logger = Logger.getLogger(SimulationRunner.class);
	
    private SimulatorConfiguration mConfig;
    private EventQueue queue;

    private boolean runSimulation = false;
    private int mNumEventsProcessed = 0;
    private int mMaxEventsToProcess = 0;
  
    private boolean stopNowFlag = false;
    private boolean isStopped = true;
    
    //configuration
    private final File configFile;
    private String paramsFileName;
    private boolean paramsFileExists = false;
    
    private String eventsFileName;
    private boolean eventsFileExists = false;
    
    private List<WorldListener>    myWorldListeners = new ArrayList<WorldListener>();
    
    /**
     * Basic constructor.
     * 
     * @param xmlConfigFile
     *            	the config file which the simulator will be initialised with          
     */
    public SimulationRunner(File xmlConfigFile)
    {
        configFile = xmlConfigFile;
        paramsFileName = ""; //there is no params file - ugly code
        
		//Add ConcreteModuleRunners (for each Module) as listeners
        logger.info("Adding world listeners...");
		this.addWorldListeners(AbstractModuleRunner.getWorldListeners());
    }
    
    /**
     * Set the parameters configuration file for the simulator.
     * 
     * Used to over-ride parameters in the xml DC configuration file
     * 
     * @param paramsFileName - parameters configuration file name of over-ride parameter values
     */
    public void setParamsFileName(String paramsFileName) {
    	this.paramsFileName = paramsFileName;
    	setParamsFileExists(true);
    	logger.warn("Setting Params FileName: '" + paramsFileName + "'");
    }
    
    /**
     * Set the user events file for the simulator.
     * 
     * Used to over-ride user events file in the xml DC configuration file
     * 
     * @param eventsFileName - user events file name containing user specified events
     */
    public void setEventsFileName(String eventsFileName) {
    	this.eventsFileName = eventsFileName;
    	eventsFileExists = true;
    	logger.warn("Setting User Events FileName: '" + eventsFileName + "'");
    }   
    
    /**
     * Add world listener that listens for world updated messages
     * 
     * @param listener - the WorldListener
     */
    public void addWorldListener(WorldListener listener)
    {
    	logger.info("Adding world listener: " + listener);
        myWorldListeners.add(listener);
    }
    
    /**
     * Add a List of WorldListeners that listen for worldUpdated message
     *
     * @param listeners - List of WorldListeners
     */
    public void addWorldListeners(List<WorldListener> listeners) {
    	for(WorldListener l: listeners) {
    		addWorldListener(l);
    	}
    }

    /**
     * Method to prepare the simulator before it enters the main event loop.
     */
    private void initialise()
    {
        logger.debug("Initialising config...");
        if(!initialiseConfig()) return;
        //overwrite user events filename if one has been passed on commandline
        initialiseUserEvents();
        initialiseParametersConfig();
        
        
        logger.debug("Initialising variables...");
        initialiseVariables();
        
        if(logger.isInfoEnabled()) {
        	String logMessage = "World now has " + World.getInstance().mDatacentres.size() + 
               		( ( World.getInstance().getNumberOfDatacentres()>1) ? " datacentres": " datacentre" ) + " named: ";
        	        for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
				logMessage+= "(" + World.getInstance().mDatacentres.get(i).getID() + ") " + World.getInstance().mDatacentres.get(i).getName() + " ";
				logMessage+= " Number of servers = " + World.getInstance().getDatacentre(i).getServers().size();
        	}
	        logger.info(logMessage);
        }
        
        logger.debug("Preparing world...");
        prepareWorld();
       
        logger.debug("Initialising Observers...");
        initialiseWorldListeners();
    }

    /**
     * Add an observer to the event queue
     * 
     * @param o - the observer 
     */
    public void addEventObserver(Observer o) {
    	queue.addObserver(o);
    }
    
    /**
     * Prepare the world for the main simulation loop.
     * 
     * @WARNING This must be called after initialiseConfig().
     */
    private void prepareWorld()
    {
    	//following line is new
    	logger.info("World is " + World.getInstance());
    	
    	World.getInstance().distributeIPs();
    	
//    	logger.warn("Preparing world = " + mWorld);
//        mWorld.distributeIPs();

        // TODO Remove this call once aircon vents have been added to config.
        prepareAirCons();
    }

    /**
     * TODO Remove this method once aircon vents have been added to config.
     */
    private void prepareAirCons()
    {
        // TODO Add vent allocation to config, remove this from here.
    	// JC 21/05/2012 -- are these in config now?
    	AirConditioner[] airCons = World.getInstance().getAirCons();
//        AirConditioner[] airCons = mWorld.getAirCons();

        for (int i = 0; i < airCons.length; i++)
        {
        	airCons[i].setID(i);
            final Point acPosition = airCons[i].getAbsolutePosition();
            final int ventOffset = 2;

            for (int j = 0; j < 3; j++)
            {
                airCons[i].addExtractionVent(new Point(acPosition.x, (ventOffset * j) + acPosition.y));
            }
        }
    }
    
    /**
     * Initialise the configuration loader and read in all configurable data
     * from file.
     * @return true if successful, false if error
     */
    private boolean initialiseConfig()
    {
    	logger.debug("Attempting to read in configuration file...");
        try
        {
            mConfig = XMLLoader.load(configFile);
        }
        catch (FileNotFoundException e)
        {
        	logger.error("Configuration Error: While trying to load configuration file: '" + configFile.toString() +"'");
        	logger.fatal("Error: " + e.getMessage() + ". Exiting system.");
        	stopSimulation();
        	return false;
        }
        catch (IOException e)
        {
        	logger.error("Configuration Error: While trying to load configuration file: '" + configFile.toString() +"'");
        	logger.fatal("Error: " + e.getMessage() + ". Exiting system.");
        	stopSimulation();
        	return false;
        }
        catch (Exception e)
        {
        	logger.error("Configuration Error: While trying to load configuration file: '" + configFile.toString() +"'");
        	logger.fatal("Error: " + e.getMessage() + ". Exiting system.");
            e.printStackTrace(System.err);
            stopSimulation();
        	return false;
        }
        logger.info("Configuration file successfully read in");
        return true;
    }

    /**
     * Overwrite the user events filename if one has been passed on command line.
     *
     */
    private void initialiseUserEvents() {
    	
    	if(this.eventsFileExists) {
    		UserEventsModuleConfigParams params = (UserEventsModuleConfigParams) (Module.USER_EVENTS_MODULE.getParams());
    		params.setFilename(this.eventsFileName);
    	}
    }
    
    
    /**
	 * Initialise parameters from the params config file.
	 * 
	 * Any parameter values defined in this file overwrite those 
	 * in the xml config file.
	 */
	private void initialiseParametersConfig()
	{
		logger.info("Attempting to read in params file...");
		
		logger.info("Seed is " + ((SimModuleConfigParams) Module.SIM_MODULE.getParams()).getSeed());
		ConfigParams.init(paramsFileName);
		logger.info(ConfigParams.getProperties());
		ConfigParams.update(mConfig.getConfig());
		logger.info("Seed is " + ((SimModuleConfigParams) Module.SIM_MODULE.getParams()).getSeed());
		
	    logger.info("Configuration params file successfully read in"); 
	}

    /**
	 * Set all configurable variables by fetching data from the ConfigManager.
	 * 
	 * @WARNING This must be called after initialiseConfig().
	 */
	private void initialiseVariables()
	{
	    runSimulation = true;
	    stopNowFlag = false;
	    
	    //reset seed
	    RandomSingleton.getInstance().reset(((SimModuleConfigParams) Module.SIM_MODULE.getParams()).getSeed());
	    queue = EventQueue.getInstance();
	    queue.reset(TimeManager.minutesToSimulationTime(((EventsModuleConfigParams) Module.EVENTS_MODULE.getParams()).getSpawnerFrequency()));

	    //reset seed
	    RandomSingleton.getInstance().reset(((SimModuleConfigParams) Module.SIM_MODULE.getParams()).getSeed());
	
	}

	/**
	 * Initialise all worldListeners that the world has been updated.
	 * 
	 * Note: This should be called *after* the world has been fully initialised
	 */
	private void initialiseWorldListeners()
	{
	    // Notify world listeners (modules such as the GUI, SubscriptionsModule, etc.) of the new world update
	    for (WorldListener l : myWorldListeners)
	    {
	        l.worldUpdated(mConfig.getWorld());
	    }
	}

	/**
     * Method to clean-up after the main event loop is finished and produce
     * useful information.
     */
    private void finalise()
    {   
        //Destroy the SingltonObjects, in case SimulationRunner is restarted.
        logger.debug("Cleaning Singltons...");
        logger.debug("Destroying World object...");
		World.getInstance().destroy();
		logger.debug("Destroying EventQueue object...");
		EventQueue.getInstance().destroy();
		//reset Server iterator
		logger.debug("Resetting next Service ID to 0!");
		Service.resetNextServiceIDToZero();
		
		logger.debug("Singletons now clean.");
    }

    /**
     * Pop the next event from the event queue and execute it, notifying the GUI
     * (if it exists) of any changes.
     */
    private Event handleEvent()
    {
        final Event event = queue.nextEvent();

        runSimulation = event.perform();  //some events return false (which stops SimRunner)

        if (!EventQueue.USE_DOUBLE_QUEUE)
        {
            queue.sort();
        }

        mNumEventsProcessed++;

        return event;
    }

    /**
     * Check to see if we need to keep running the simulation based off maximum
     * number of events.
     */
    private void loopDecision()
    {
        if ((mMaxEventsToProcess != 0) && (mNumEventsProcessed > mMaxEventsToProcess))
        {
            runSimulation = false;
        }
    }

    /**
     * Event loop where the simulator will spend most of its time.
     * 
     * The simulator will run through the event queue until either there are no
     * events remaining, or an event is reached which tells the simulator to
     * stop.
     * 
     * When an event is performed it is immediately removed from the queue. Each
     * event may produce many, one or no additional events (which are
     * subsequently added to the queue).
     */
    private void eventLoop()
    {
    	logger.debug("Description of module settings...:" + Module.describeAllModules());
        while (runSimulation && !stopNowFlag)
        {
            handleEvent();
            loopDecision();
        }
        logger.info(TimeManager.log("Exiting eventLoop..."));
    }

    /**
     * Main simulator method which contains the event loop.
     */
    public void run()
    {
    	isStopped = false;
    	logger.info("Initialising...");
        initialise();
        logger.info("Finished Initialising");
        
        if(paramsFileExists) {   
        	logger.debug("Copy params file to log directory...");
        	try {
        	String dest = World.getInstance().getLogManager().getLogResultsDirName()+"/"+new File(paramsFileName).getName();
        	File fOrig = new File(paramsFileName); //Strip directory path from filename
        	File fDest = new File(dest);
        	FileUtils.copyFile(fOrig,fDest);
        	logger.info("Params file copied to: '" + fDest.getAbsolutePath() + "'");
        	} catch (IOException e) {
        		logger.error(e);
        	}
        }
        
        logger.debug("Total number of servers = " + World.getInstance().getNumServers());
        
        logger.info("Starting simulation...");
        eventLoop();
        logger.debug("Exited event loop.");

        logger.info("Finalising...");
        finalise();
        logger.debug("Finished finalising.");
        
        logger.warn("Simulation has now finished.  Exiting SimulationRunner.");
        isStopped = true;
    }
    
    /**
     * Send stop signal to the simulation runner
     */
    public void stopSimulation() {
    	if(isRunning()) {
    		logger.info(TimeManager.log("SimulationRunner has received stop signal..."));
    	} else {
    		logger.info(TimeManager.log("SimulationRunner has received stop signal.  No simulation is running.  Ignoring..."));
    	}

    	stopNowFlag = true;
    }
    
    public boolean isRunning() {
    	return !isStopped;
    }

	public boolean isParamsFileExists() {
		return paramsFileExists;
	}

	public void setParamsFileExists(boolean paramsFileExists) {
		this.paramsFileExists = paramsFileExists;
	}
}
