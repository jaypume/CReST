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
package sim.module.failure.event;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.failure.bo.Failable;
import sim.module.failure.configparams.FailureModuleConfigParams;
import sim.module.log.Log;
import sim.physical.World;
import sim.probability.RandomSingleton;
import utility.time.TimeManager;
import cern.jet.random.Normal;

/**
 * Event to model a failure within the simulation. Could be a hard or soft
 * failure or a fix instead.
 */
public class FailureEvent extends Event
{
	public static Logger logger = Logger.getLogger(FailureEvent.class);
	
    // Type of failures that could occur.
    public enum FailType
    {
        soft, hard, fix
    }

    // What type of object this failure is targeted at.
    public enum ObjectType
    {
        server, aircon
    }

    private final int         mObjectID;
    private final FailType    mFailType;
    private final int         mThread;
    private final ObjectType  mObjectType;

	/**
	 * Constructor for making a failure event of type 'fix'
	 * 
	 * This creates an event to un-fail a server.
	 * 
	 * @param dc_id - the datacentre ID
	 * @param pStartTime - the time event will happen
	 * @param pObjectID - the ID of the object to fix
	 * @param pObjectType - the type of the object to fix
	 */
    private FailureEvent(int dc_id, final long pStartTime, final int pObjectID, final ObjectType pObjectType)
    {
    	//get the ID of the datacentre associated with this event.  
    	//TODO JC: Dec 2011 - would it not be cleaner to pass the DC_id externally, rather than calculate each time?
        super(pStartTime, World.getInstance().getDatacentre(World.getInstance().getServer(pObjectID).getIP()).getID());
        mFailType = FailType.fix;
        mObjectID = pObjectID;
        mThread = -1;
        mObjectType = pObjectType;
    }
    
    /**
     * Constructor for creating a failure event to break a server.
     * 
     * @param dc_id - datacentre ID
     * @param pType - the type of break to perform, 'hard' or 'soft'.
     * @param pStartTime - the time event will happen
     * @param pObjectID - the ID of the object
     * @param pObjectType - the object type, 'aircon' or 'server'
     * @param pThread - the failure distribution thread that the object is from
     */
    private FailureEvent(final int dc_id, final FailType pType, final long pStartTime, final int pObjectID, final ObjectType pObjectType, final int pThread)
    {
    	super(pStartTime, dc_id); 
        mFailType = pType;
        mObjectID = pObjectID;
        mThread = pThread;
        mObjectType = pObjectType;
    }

    /**
     * Create a failure event to break a server. Will randomly choose a server
     * from the given thread.
     * 
     * @param pType
     *            the type of break to perform, hard or soft.
     * @param pStartTime
     *            the time at which this event should happen.
     * @param pThread
     *            the server failure distribution thread that this server is
     *            from.
     */
    public static Event create(int dc_id, final FailType pType, final long pStartTime, final int pObjectID, final ObjectType pObjectType, final int pThread)
    {
		return new FailureEvent(dc_id, pType, pStartTime, pObjectID, pObjectType, pThread);
    }

    /**
     * Create a failure event of type 'fix', i.e. this will create an event to
     * un-fail a server.
     * 
     * @param dc_id
     * 			  the ID of the datacentre
     * @param startTime
     *            the time at which this event should happen.
     * @param serverID
     *            the ID of the server to fix.
     * @param objectType
     *            the object type for the event
     */
    public static Event create(final int dc_id, final long startTime, final int serverID, final ObjectType objectType)
    {
        return new FailureEvent(dc_id, startTime, serverID, objectType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String string = ((mFailType==FailType.fix)? "Fix event on ":((mFailType==FailType.hard)?"Hard":"Soft") + " Fail event on ") + failedOn() + " ObjectType="+mObjectType;

        return string;
    }

    /**
     * Return a string of the format "server x/y", where x is the ID of the
     * server the event is related to and y is the total number of servers in
     * the world.
     * 
     * @return a string of the format "server x/y", as above.
     */
    private String failedOn()
    {    	
    	String s = (getObjectType()==ObjectType.server) ? "Server: " + mObjectID + "/" + World.getInstance().getNumServers() : "Aircon: " + mObjectID + "/" + World.getInstance().getAirCons().length;
        //s += " " + mObjectID + "/" + World.getInstance().getNumServers();

        return s;
    }

    /**
     * Calculate and return the time that it will take until this server is
     * fixed again.
     * 
     * Uses a normal distribution with mean times MEAN_HARD_FIX_TIME and MEAN_SOFT_FIX_TIME
     * 
     * @return the time at which this server will be fixed.
     */
    private long genFixTime()
    {
        long fixTime = -1;
        Normal norm;
        FailureModuleConfigParams params = ((FailureModuleConfigParams) Module.FAILURE_MODULE.getParams());
        
        if(mFailType == FailType.hard) {
        	//hardfix
        	norm = new Normal(params.getMeanHardFixTime(), params.getStdDevHardFixTime(), RandomSingleton.getInstance().getEngine());	
        	double days_until_fix = norm.nextDouble();
        	
        	fixTime = mStartTime + TimeManager.daysToSimulationTime(days_until_fix);
        	logger.debug(TimeManager.log(days_until_fix + " days until fixtime"));
        	logger.debug(TimeManager.log("Hard FixTime will be " + TimeManager.getTimeString(fixTime) ));
        } else if(mFailType == FailType.soft) {
        	//softfix
        	norm = new Normal(params.getMeanSoftFixTime()*60, params.getStdDevSoftFixTime(), RandomSingleton.getInstance().getEngine());
        	double seconds_until_fix = norm.nextDouble();
        	fixTime = mStartTime + TimeManager.secondsToSimulationTime(seconds_until_fix);
        	logger.debug(TimeManager.log("Fail , " + mObjectType + " " + mObjectID + ", " + seconds_until_fix + " seconds until fixtime"));
        	logger.debug(TimeManager.log("Soft FixTime will be " + TimeManager.getTimeString(fixTime) ));
        } else {
        	//warning - no other type of fix
        	logger.fatal("No fix type available for FailType=" + mFailType + " Exiting system...");
        	System.exit(0);
        }
        
        return fixTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents(sim.EventQueue)
     */
    @Override
    protected void generateEvents()
    {
        EventQueue queue = EventQueue.getInstance();

    	//get datacentre ID - will throw exceptions if objectID does not exist
    	final int dc_id = World.getInstance().getDatacentreID(mObjectType, mObjectID);
        
        // If this is a hard or soft failure, then create a corresponding fix
        // event.
        switch (mFailType)
        {
            case soft:
            case hard:
            {
                Event event = FailureEvent.create(dc_id, genFixTime(), mObjectID, mObjectType);
                queue.addEvent(event);
                numEventsGenerated++;
                
                logger.info("New failure event generated: " + event);
                break;
            }
            default:
            {
            	//do nothing on a fix event
            	logger.info("This fix event does not generate any new failure events...");
                break;
            }
        }

        // If this is a hard or soft failure and is part of a failure thread,
        // then create the next failure event.
        if ((mFailType != FailType.fix) && (mThread != -1))
        {
        	Event e = FailureThreads.getInstance().nextFailure(mThread);
            queue.addEvent(e);
            numEventsGenerated++;
            logger.info("New failure thread event generated: " + e);
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
    	
    	logger.info(TimeManager.log(this.toString()));
    	
        boolean continueSimulation = true;
        // Server server = World.getInstance().getServer(mObjectID);
        Failable object = null;

        // Get the object that this failure event is targeted at.
        switch (mObjectType)
        {
            case aircon:
            {
                object = World.getInstance().getAirCon(mObjectID);
                break;
            }
            case server:
            {
                object = World.getInstance().getServer(mObjectID);
                break;
            }
            default:
            {
                logger.error("FailureEvent.performEvent(): Object type is not recognised.");
                break;
            }
        }

        // Perform a failure or fix based on what failure type this event is.
        switch (mFailType)
        {
            case soft:
            {
            	logger.info("Performing a soft fail event for object: " + object);
                object.performFailure(FailType.soft);
                break;
            }
            case hard:
            {
            	logger.info("Performing a hard fail event for object: " + object);
                object.performFailure(FailType.hard);
                break;
            }
            case fix:
            {
            	logger.info("Performing a fix event for object: " + object);
                object.performFix();
                break;
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

        log.add(String.valueOf(mStartTime));
        log.add(String.valueOf(mObjectID));
        log.add(World.getInstance().getIP(mObjectID).toString());

        String type;

        switch (mFailType)
        {
            case hard:
            {
                type = "hard";
                break;
            }
            case soft:
            {
                type = "soft";
                break;
            }
            case fix:
            {
                type = "fix";
                break;
            }
            default:
            {
                type = "UNKNOWN_FAILURE_TYPE";
                break;
            }
        }

        log.add(type);

        return log;
    }

    /**
     * Get object ID of object associated with this failure event
     * 
     * @return objectID
     */
    public int getID()
    {
        return mObjectID;
    }

    /**
     * Get the type of failure event that this is.
     * 
     * @return the type of failure event that this is.
     */
    public FailType getFailEventType()
    {
        return mFailType;
    }

    /**
     * Get the object type of the object that this failure is targeted at.
     * 
     * @return the type of the targeted object.
     */
    public ObjectType getObjectType()
    {
        return mObjectType;
    }
}
