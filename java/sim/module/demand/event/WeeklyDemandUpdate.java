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

package sim.module.demand.event;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.log.Log;
import sim.module.service.bo.Service;
import sim.module.service.event.ServiceStartEvent;
import sim.physical.World;
import utility.time.TimeManager;


public class WeeklyDemandUpdate extends Event
{
	public static Logger logger = Logger.getLogger(WeeklyDemandUpdate.class);
	
	public static final long FIRST_UPDATE_DEMAND_EVENT = TimeManager.minutesToSimulationTime(0);
    public static final long SERVICE_RUN_DURATION = TimeManager.minutesToSimulationTime(59) + TimeManager.secondsToSimulationTime(59);
//    public static final long SERVICE_RUN_DURATION = TimeManager.hoursToSimulationTime(23) + TimeManager.minutesToSimulationTime(59) + TimeManager.secondsToSimulationTime(59);
    public static final int MAX_NUM_SERVICES = 10000; //10000;
    
    //TODO - this has now been subsumed by Event.getDCID.  REMOVE THIS
    private int mDatacentreNo; //currently not used locally - remove?
    private double lowerDemand;
    private int start;
    private double dev;
    
    /**
     * Constructor 
     * This places a weeks worth of new demand events in the queue
     * 
     * @param pStartTime - start time of week
     * @param pDatacentreNo - data centre ID number
     * @param pLowerD - current demand to create events using
     * @param pDev - random deviation to apply to demand +/-
     */
    public WeeklyDemandUpdate(final long pStartTime, final int pDatacentreNo, double pLowerD,double pDev)
    {
        super(pStartTime, pDatacentreNo);

        start = (int)TimeManager.simulationTimeToHours(pStartTime);
        logger.info(TimeManager.log("Creating new UpdateDemandEvent for DC#=" + pDatacentreNo + " with start time " + TimeManager.getTimeString(pStartTime)));
        mDatacentreNo = pDatacentreNo;
        lowerDemand = pLowerD;
        dev = pDev;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.Event#perform()
     */
    @Override
    protected boolean performEvent()
    {
    	logger.info(TimeManager.log("performing WeeklyUpdate..."));
    	
        boolean continueSimulation = true;
        EventQueue queue = EventQueue.getInstance();
        
        //Everyday this week, create a square wave
    	for (int x=0;x<7;x++)
    	{
    		squareWaveDay(start+(24*x),lowerDemand,dev,queue);
    	}
        
        //stop simulation manually
        
//        if(World.getInstance().getTime() > TimeManager.daysToSimulationTime(5)) {
//        	logger.info(TimeManager.log("update demand event.. now ending sim"));
//        	System.exit(0);
        
        
        return continueSimulation;
    }
    
    /**
     * //TODO - Creates a daily square wave
     * @param startHour - start hour
     * @param currentDemand - current demand
     * @param demandDeviation - demand deviation
     * @param pQueue - the event queue
     */    
    public void squareWaveDay(int startHour, double currentDemand, double demandDeviation, EventQueue pQueue)
    {
    	
    	logger.debug(TimeManager.log("SquareWaveEvent: start=" + startHour + ", currentDemand=" + currentDemand + ", demandDeviation=" + demandDeviation));
    			
        int numCPUs = World.getInstance().mDatacentres.get(mDatacentreNo).getNumCPUs();
        
        double averageCPUUtilisation = 1;

        EventQueue queue = pQueue;
        
        //Work out upper and lower demand for square wave
    	double upperDemand = currentDemand + demandDeviation;
    	double lowerDemand = currentDemand - demandDeviation;
        
        if (upperDemand>1){ upperDemand=1.0;}
        int upperServices = (int) Math.ceil(numCPUs / averageCPUUtilisation * upperDemand);
        if (lowerDemand<0){ lowerDemand=0.0;}
        int lowerServices = (int) Math.ceil(numCPUs / averageCPUUtilisation * lowerDemand);
        
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.###");
        
        logger.info(TimeManager.log("DC#=" + mDatacentreNo + ", Generating new square wave event with Demand[low=" + df.format(lowerDemand) + ", high=" + df.format(upperDemand) + "]"));
        
        int serviceID=-1;
        
        int numServices=0;
        
        //Create demand for the next 24 hour period
        for(int k=startHour; k<(startHour+24); k++) {
        	
        	//Calculate number of services for when demand is low and high
        	if(k<(startHour+6)) numServices = lowerServices;  //low, hours 0-5
        	else if(k>=(startHour+6) && k<(startHour+18)) numServices = upperServices;  //high, hours 6-17
        	else if(k>=(startHour+18)) numServices = lowerServices; //low, hours 18-23
        	
        	//generate start service events
        	for(int j=0; (j<numServices && j < MAX_NUM_SERVICES); j++) {
        		 serviceID = Service.getNextServiceID();
        		 queue.addEvent(new ServiceStartEvent(TimeManager.hoursToSimulationTime(k), WeeklyDemandUpdate.SERVICE_RUN_DURATION, serviceID, 0, mDatacentreNo));
                 numEventsGenerated++;
        	}
        }
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
    
    protected void generateEvents()
    {
    
    }
    
}