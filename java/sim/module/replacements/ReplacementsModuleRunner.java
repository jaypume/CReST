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
package sim.module.replacements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import org.apache.log4j.Logger;

import config.physical.ServerType;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.failure.bo.Failable;
import sim.module.failure.event.FailureEvent;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.failure.event.FailureThreads;
import sim.module.failure.event.FailureEvent.FailType;
import sim.module.replacements.configparams.ReplacementModuleConfigParams;
import sim.module.replacements.event.ReplacementEvent;
import sim.physical.AirConditioner;
import sim.physical.Block;
import sim.physical.Server;
import sim.physical.World;
import utility.time.TimeManager;

public class ReplacementsModuleRunner extends AbstractModuleRunner
{
	public static Logger logger = Logger.getLogger(ReplacementsModuleRunner.class);
	
	private static ReplacementsModuleRunner instance = null;
	private static int counter = 0;
    static ArrayList<ServerType> REPLACEMENT_SERVERS  = new ArrayList<ServerType>();
	
	protected ReplacementsModuleRunner()
	{
		logger.info("Constructing " + this.getClass().getSimpleName());
	}

	public static ReplacementsModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new ReplacementsModuleRunner();
        }
        return instance;
	}
	
	@Override
	public void update(Observable o, Object obj)
	{
		//if the event is a failure event, do something
		if (isFailureEvent(obj))
		{
			FailureEvent event = (FailureEvent) obj;
			logger.info("There has been a Failure event... "+ event);
			
			//check for fix event
			if(event.getFailEventType() == FailType.fix)
			{
				Failable object = null;
				// Get the object that this failure event is targeted at.
		        switch (event.getObjectType())
		        {
		            case aircon:
		            {
		                object = World.getInstance().getAirCon(event.getID());
		                break;
		            }
		            case server:
		            {
		                object = World.getInstance().getServer(event.getID());
		                break;
		            }
		            default:
		            {
		                logger.error("FailureEvent.performEvent(): Object type is not recognised.");
		                break;
		            }
		        }
								
				switch(object.getFailType())
				{
					case soft:
					{
						//if failure was soft, just set server/aircon alive again
						//NOTE: should not get here, since soft fixes are already dealt with in object.performfix()
						object.setIsAlive(true);
						object.setFailType(FailType.fix);
						break;
					}
					case hard:
		            {
						if(((ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams()).isReplacementInBlocks()) //if replace at block level
						{
							blockReplacement(object);					
						}
						else
						{ //replace individual server / aircon now
							if(object instanceof Server)							
								individualReplacement((Server) object);
							else
								individualReplacement((AirConditioner) object);
						}
		                break;
		            }
					default:
		            {
		            	//fix event for an already fixed server/aircon, do nothing
		            	logger.info(object.getClass().getSimpleName() + " " + event.getID() + " not a hard failure, doing nothing");
		                break;
		            }
				}
			}
		}
	}

	@Override
	public void worldUpdated(World w)
	{		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.REPLACEMENTS_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.info("Replacements module is on...");
			
			//do some stuff
		} else {
			//do nothing - module not on
		}
	}

	@Override
	public String getLogFileName()
	{
		return "replacements";
	}

	@Override
	protected String getLogTitleString()
	{		
		return null;
	}

	@Override
	public boolean isActive()
	{
		return Module.REPLACEMENTS_MODULE.isActive();
	}
	
	/**
	 * Check and replace the entire block for a given server/aircon
	 * @param object the server/aircon to check and replace
	 */
	private void blockReplacement(Failable object)
	{
		Block block = World.getInstance().getBlock(object.getIP());
		block.performFixes();
	}
	
	/**
	 * Replace an individual server
	 * @param server the server to replace
	 */
	public void individualReplacement(Server server)
	{
		server.setIsAlive(true);
		server.setFailType(FailType.fix);
		logger.info("Replacing server " + server.getID() + " starting...");
		replaceServer(server);
		
		int dc_id = World.getInstance().getDatacentreID(ObjectType.server, server.getID());
		Event e = ReplacementEvent.create(dc_id, World.getInstance().getTime(), server.getID(), ObjectType.server);
		EventQueue.getInstance().addEvent(e);
        logger.info("New replacement event generated: " + e);
	}
	
	/**
	 * Replaces an individual aircon unit
	 * @param aircon the aircon unit to replace
	 */
	public void individualReplacement(AirConditioner aircon)
	{
		aircon.setIsAlive(true);
    	aircon.setFailType(FailType.fix);
        logger.info("Aircon replaced: " + aircon.getIP());
        
        int dc_id = World.getInstance().getDatacentreID(ObjectType.aircon, aircon.getID());
        Event e = ReplacementEvent.create(dc_id, World.getInstance().getTime(), aircon.getID(), ObjectType.aircon);
        EventQueue.getInstance().addEvent(e);
        logger.info("New replacement event generated: " + e);
	}
	
    /**
     * Replace the hardware side of the given server.
     * @param server 
     */
    public void replaceServer(Server server)
    {
        ServerType newServer = getReplacement(World.getInstance().getTime());

        if (newServer != null)
        {
            doReplacement(newServer, server);
            logger.info(TimeManager.log("Spawning a new failure event for replaced server..."));
            FailureThreads.getInstance().spawnInitialFailure(server.getID());
        }
        else
        {
            logger.warn("Server type is null.");
        }
    }
    	
	 /**
     * Get a replacement server which will be used to replace a broken server.
     * 
     * @param pTime
     *            the current world time.
     * @return a new server.
     */
    public static ServerType getReplacement(final long pTime)
    {
        ServerType newServer = null;

        if (((ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams()).isReplacementViaPresetTypes()
        		&& !areReplacementsObsolete(pTime))
        {
            if (REPLACEMENT_SERVERS.size() > 0)
            {
                for (int i = REPLACEMENT_SERVERS.size() - 1; i >= 0; i--)
                {
                    ServerType server = REPLACEMENT_SERVERS.get(i);

                    if (server.isAvailable(pTime))
                    {
                        newServer = server;
                        break;
                    }
                }
            }
        }

        if (((ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams()).isReplacementViaFunctionTypes()
        		&& newServer == null)
        {
            newServer = Server.generateServerType(pTime);
            // Else, if available, get a new server with specification based on
            // a function over time.
        }

        if (newServer == null)
        {
            // Do nothing. There no information on how to replace the server, so
            // just replace it with the same model.
        	logger.warn("No replacement server found");
        }

        return newServer;
    }

    /**
     * Use the given server type to replace this, broken, server.
     * 
     * @param pNewServer
     *            the new server to replace this one with.
     * @param server 
     * 			  the broken server.
     */
    private void doReplacement(final ServerType pNewServer, Server server)
    {
    	logger.info("Replacing server variables");
    	
        final long oldMeanFailTime = server.getMeanFailTime();
        server.replaceMemberVariables(pNewServer);
        server.updateFailureThreads(oldMeanFailTime);
    }
    
    /**
     * Add the given server type to the current collection.
     * 
     * @param pNewType
     *            the new type to add to the current server type collection.
     */
    public static void addServerType(final ServerType pNewType)
    {
        REPLACEMENT_SERVERS.add(pNewType);
        Collections.sort(REPLACEMENT_SERVERS);
    }
    
    /**
     * Check to see if the preset replacement servers have become obsolete, i.e.
     * if we should start using a mathematical function to determine new
     * power/cost.
     * 
     * @param pTime
     *            the current world time.
     * @return true if the presets are obsolete, else false.
     */
    private static boolean areReplacementsObsolete(final long pTime)
    {
        boolean areObsolete = false;

        if (((ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams()).isReplacementViaFunctionTypes())
        {
            long sumDifference = 0;

            for (int i = 1; i < REPLACEMENT_SERVERS.size(); i++)
            {
                sumDifference += (REPLACEMENT_SERVERS.get(i).getTimeAvailable() - REPLACEMENT_SERVERS.get(i - 1).getTimeAvailable());
            }

            final long averageDifference = sumDifference / REPLACEMENT_SERVERS.size();

            if (pTime > averageDifference + REPLACEMENT_SERVERS.get(REPLACEMENT_SERVERS.size() - 1).getTimeAvailable())
            {
                areObsolete = true;
            }
        }
        return areObsolete;
    }
}
