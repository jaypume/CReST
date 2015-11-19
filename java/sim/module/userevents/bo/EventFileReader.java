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
 * Created on 17 Aug 2011
 */
package sim.module.userevents.bo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sim.event.StopSimEvent;
import sim.module.Module;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.failure.event.FailureEvent;
import sim.module.failure.event.FailureEvent.FailType;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.service.event.ServiceStartEvent;
import sim.module.service.event.ServiceStopEvent;
import sim.module.thermal.event.ThermalEvent;
import sim.physical.World;
import utility.time.TimeManager;
import utility.time.TimeManager.UnitTime;

/**
 * Special event that reads in a file of other events and adds them to the
 * queue.
 */
public class EventFileReader extends Event
{
	
	public static Logger logger = Logger.getLogger(EventFileReader.class);
	
    private final String mFilename;

    //TODO - JC Jan 2012, Make Enumeration
    public static final int EVENT_TYPE_COLUMN = 0;
    public static final int OBJECT_TYPE_COLUMN = 1;
    public static final int TIME_UNIT_COLUMN = 2;
    public static final int START_TIME_COLUMN = 3;
    public static final int OBJECT_ID_COLUMN = 4;
    public static final int FAIL_TYPE_COLUMN = 5;
    
    
    /**
     * Constructor
     * 
     * @param pStartTime
     *            the time at which to execute this event.
     * @param pFilename
     *            the full file path name of the user events file 
     */
    private EventFileReader(final long pStartTime, final String pFilename)
    {
    	//This is not datacentre specific, so set dcID=-1
        super(pStartTime, -1);
        mFilename = pFilename;
    }

    /**
     * Create and return an EventFileReader object.
     * 
     * @param pStartTime
     *            the time at which to execute this event.
     * @param pFilename
     *            the full filepath of the event file to read user events in
     *            from.
     * @return the new event file reader object.
     */
    public static EventFileReader create(final long pStartTime, final String pFilename)
    {
        return new EventFileReader(pStartTime, pFilename);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#generateEvents()
     */
    @Override
    protected void generateEvents()
    {
        // Generate zero 'consequence' events.
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

        processAllEventsFromFile();

        return continueSimulation;
    }

    /**
     * Convert all events from the event file associated with this reader into
     * simulation readable events.
     */
    private void processAllEventsFromFile()
    {
    	
    	logger.warn("Processing events from file: '" + mFilename + "' ...");
        try
        {
            // Read file in to memory and process each event line in turn.
            BufferedReader BR = new BufferedReader(new FileReader(mFilename));

            EventQueue queue = EventQueue.getInstance();
            String line;

            while ((line = BR.readLine()) != null)
            {
                processSingleEventLine(line, queue);
            }
            
            BR.close(); // close to avoid a resource leak
            
        }
        catch (FileNotFoundException e)
        {
            logger.error("EventFileReader Error: File not found " + e.getMessage() + " filename: '" + mFilename + "'");
            logger.error("Turning off User Events module...");
            Module.USER_EVENTS_MODULE.setActive(false);
        }
        catch (IOException e)
        {
            logger.error("EventFileReader Error: IO Error " + e.getMessage());
            logger.error("Turning off User Events module...");
            Module.USER_EVENTS_MODULE.setActive(false);
        }
        catch (Exception e)
        {
        	logger.error("EventFileReader Error: While trying to read event file: '" + mFilename + "'");
            logger.error("Error: " + e.getClass() + " : " + e.getMessage());
            e.printStackTrace();
            logger.error("Turning off User Events module...");
            Module.USER_EVENTS_MODULE.setActive(false);
        }
        logger.info("Event file processed cleanly.");
    }

    /**
     * Process a single given event line and add the event to the given event
     * queue.
     * 
     * @param pLine
     *            the line to create an event from.
     * @param pQueue
     *            the queue to add the new event to.
     */
    private void processSingleEventLine(final String pLine, EventQueue pQueue)
    {
        // Ignore comments.
        if (!pLine.startsWith("#"))
        {
            Event event = createEventFromLine(pLine);
            pQueue.addEvent(event);
        }
    }

    /**
     * Convert the given line to an event object.
     * 
     * @param pLine
     * @return
     */
    private Event createEventFromLine(String pLine)
    {
        Event event = null;

        // Split the line into tokens and then create an event based on what the
        // first token is.
        String[] tokens = pLine.split(" ");
        String type = tokens[EventFileReader.EVENT_TYPE_COLUMN];
        logger.debug("Reading event line: " + Arrays.toString(tokens));
        if (type.equals("failure"))
        {
        	//TODO: JC, Jan 2012 - Subclass EventReaderExceptions (failure,temperature,service start, etc)
        	try {
        		event = createFailureEvent(tokens);
        	} catch (EventReaderException e) {
        		logger.warn("Error reading failure event from file.  Ignoring. " + e);
        	}
        }
        else if (type.equals("temperature"))
        {
            event = createTemperatureEvent(tokens);
        }
        else if (type.equals("serviceStart"))
        {
            event = createServiceStartEvent(tokens);
        }
        else if (type.equals("serviceStop"))
        {
            event = createServiceStopEvent(tokens);
        }
        else if (type.equals("simStop"))
        {
            event = createSimStopEvent(tokens);
        }

        return event;
    }

    /**
     * Create a stop simulation event.
     * 
     * @param pTokens
     *            the tokens of the event.
     * @return the new stop simulation event.
     */
    private Event createSimStopEvent(String[] pTokens)
    {
        Event event;
        final long startTime = Long.valueOf(pTokens[1]);

        event = StopSimEvent.create(startTime);

        return event;
    }

    /**
     * Create a stop services event.
     * 
     * @param pTokens
     *            the tokens of the event.
     * @return the new stop simulation event.
     */
    private Event createServiceStopEvent(String[] pTokens)
    {
        Event event;
        final long startTime = Long.valueOf(pTokens[1]);
        final int serviceID = Integer.valueOf(pTokens[2]);
        final int datacentreNo = Integer.valueOf(pTokens[3]);

        //default issue number is 0
        event = ServiceStopEvent.create(startTime, serviceID, 0, datacentreNo);

        return event;
    }

    /**
     * Create a start services event.
     * 
     * @param pTokens
     *            the tokens of the event.
     * @return the new stop simulation event.
     */
    private Event createServiceStartEvent(String[] pTokens)
    {
        Event event;
        final long startTime = Long.valueOf(pTokens[1]);
        final long serviceLength = Long.valueOf(pTokens[2]);
        final int serviceID = Integer.valueOf(pTokens[3]);
        final int datacentreNo = Integer.valueOf(pTokens[4]);

        //default issueNumber to 0
        event = ServiceStartEvent.create(startTime, serviceLength, serviceID, 0, datacentreNo);

        return event;
    }

    /**
     * Create a temperature event.
     * 
     * @param pTokens
     *            the tokens of the event.
     * @return the new stop simulation event.
     */
    private Event createTemperatureEvent(String[] pTokens)
    {
        Event event;
        final long startTime = Long.valueOf(pTokens[1]);

        event = ThermalEvent.create(startTime);

        return event;
    }

    /**
     * Create a failure event.
     * 
     * @param pTokens
     *            the tokens of the event.
     * @return the new failure event.
     * @throws EventReaderException - if problem creating event
     */
    private Event createFailureEvent(String[] pTokens) throws EventReaderException
    {
        Event event;

        //TODO - Make custom Exception for Event formatting 
        try {
	        final ObjectType objectType = objectTypeFromToken(pTokens[EventFileReader.OBJECT_TYPE_COLUMN]);
	        final UnitTime timeUnit = TimeManager.getUnitTime(pTokens[EventFileReader.TIME_UNIT_COLUMN]);
	        final long startTime = TimeManager.convertToSimultionTime(timeUnit, Long.valueOf(pTokens[EventFileReader.START_TIME_COLUMN]));
	        final int objectID = Integer.valueOf(pTokens[EventFileReader.OBJECT_ID_COLUMN]);
	        final FailType type = failTypeFromToken(pTokens[EventFileReader.FAIL_TYPE_COLUMN]);
	        final int thread = -1;
        
        	//get datacentre ID - will throw exceptions if objectID does not exist
        	final int dc_id = World.getInstance().getDatacentreID(objectType, objectID);
        	
        	switch (type)
            {
                case soft:
                case hard:
                {
                    event = FailureEvent.create(dc_id, type, startTime, objectID, objectType, thread);
                    break;
                }
                case fix:
                default:
                {
                    event = FailureEvent.create(dc_id, startTime, objectID, objectType);
                    break;
                }
            }
        
//          System.out.println("Creating failure event from file: " + event);
            return event;
            
        } catch (EventReaderException e) {
        	throw e;
        } catch (NullPointerException e) {
        	throw new EventReaderException("Object does not exist: '" + Arrays.toString(pTokens) + "'.  ");
        } catch (ArrayIndexOutOfBoundsException e) {
        	throw new EventReaderException("Object does not exist: '" + Arrays.toString(pTokens) + "'.  ");
        }
    }

    /**
     * Check the given token to see which fail type it contains.
     * 
     * @param pToken
     *            the token to examine.
     * @return the FailType that the token represents.
     */
    private FailType failTypeFromToken(final String pToken)
    {
        FailType type;

        if (pToken.equals("soft"))
        {
            type = FailType.soft;
        }
        else if (pToken.equals("hard"))
        {
            type = FailType.hard;
        }
        else
        {
            type = FailType.fix;
        }

        return type;
    }

    /**
     * Check the given token to see which object type (server or aircon) it
     * represents.
     * 
     * @param pToken
     *            the token to examine.
     * @return the object type that the given token represents.
     */
    private ObjectType objectTypeFromToken(final String pToken)
    {
        ObjectType type;

        if (pToken.equals("server"))
        {
            type = ObjectType.server;
        }
        else if (pToken.equals("aircon"))
        {
            type = ObjectType.aircon;
        }
        else
        {
            type = ObjectType.server;
        }

        return type;
    }
}
