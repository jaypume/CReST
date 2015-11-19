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
package sim.module.sim.configparams;

import java.util.Random;

import org.jdom.Element;

import sim.module.configparams.ModuleParamsInterface;
import utility.time.LengthOfTime;

public class SimModuleConfigParams implements ModuleParamsInterface{

	protected static final String XML_ELEMENT_NAME = "sim";
	
	//XML TAGS FOR CONFIG SETTINGS PARAMETERS
	public static final String SEED_XML_TAG = "seed";
	public static final String WAIT_STEADY_STATE_XML_TAG = "waitSteadyState";
	public static final String START_TIME_XML_TAG = "startTime";
	public static final String END_TIME_XML_TAG = "endTime";
	public static final String END_TIME_UNIT_XML_TAG = "endUnit";
	public static final String MAX_EVENTS_XML_TAG = "maxEvents";
	
	//HUMAN READABLE STRINGS FOR GUI LABALS
	public static final String SEED_GUI_LABEL = "Random Seed";
	public static final String WAIT_STEADY_STATE_GUI_LABEL = "Wait for Steady State";
	public static final String START_TIME_GUI_LABEL = "Start Time";
	public static final String END_TIME_GUI_LABEL = "End Time";
	public static final String END_TIME_UNIT_GUI_LABEL = "endUnit";
	public static final String MAX_EVENTS_GUI_LABEL = "Maximum Events";
	
    protected long prngSeed;
    protected boolean waitForSteadyState;
    protected long startTime;
    protected long endTime = 1;
    protected LengthOfTime endTimeUnit = LengthOfTime.HOUR;
    protected int maxNumberOfEvents;
    
    public static final long DEFAULT_PRNG_SEED = -1;
    public static final boolean DEFAULT_WAIT_FOR_STEADY_STATE = false;
    public static final long DEFAULT_START_TIME = 0;
    public static final long DEFAULT_END_TIME = 1;
    public static final LengthOfTime DEFAULT_END_TIME_UNIT = LengthOfTime.HOUR;
    public static final int DEFAULT_MAX_NUMBER_EVENTS = 1000000;
	
	public SimModuleConfigParams(long seed, boolean waitForSteadyState,
			long startTime, long endTime, LengthOfTime endTimeUnits,
			int maxNumberOfEvents) {
		
		this.prngSeed = seed;
		this.waitForSteadyState = waitForSteadyState;
		this.startTime = startTime;
		this.endTime = endTime;
		this.endTimeUnit = endTimeUnits;
		this.maxNumberOfEvents = maxNumberOfEvents;
	}
	
	public static SimModuleConfigParams getDefault() {
		return new SimModuleConfigParams(DEFAULT_PRNG_SEED,
				DEFAULT_WAIT_FOR_STEADY_STATE, DEFAULT_START_TIME,
				DEFAULT_END_TIME, DEFAULT_END_TIME_UNIT,
				DEFAULT_MAX_NUMBER_EVENTS);
	}
	
	@Override
	public Element getXML() {
		Element e = new Element(ModuleParamsInterface.XML_ELEMENT_NAME_STRING);		
		e.setAttribute(SEED_XML_TAG, String.valueOf(prngSeed));
		e.setAttribute(WAIT_STEADY_STATE_XML_TAG, String.valueOf(waitForSteadyState));
		e.setAttribute(START_TIME_XML_TAG, String.valueOf(startTime));
		e.setAttribute(END_TIME_XML_TAG, String.valueOf(endTime));
		e.setAttribute(END_TIME_UNIT_XML_TAG, String.valueOf(endTimeUnit.name()));
		e.setAttribute(MAX_EVENTS_XML_TAG, String.valueOf(maxNumberOfEvents));
		
		return e;
	}

	@Override
	public void updateUsingXML(Element e) {
		prngSeed = Long.parseLong(e.getAttributeValue(SEED_XML_TAG));
		waitForSteadyState = Boolean.parseBoolean(e.getAttributeValue(WAIT_STEADY_STATE_XML_TAG));
		startTime = Long.parseLong(e.getAttributeValue(START_TIME_XML_TAG));
		endTime = Long.parseLong(e.getAttributeValue(END_TIME_XML_TAG));
		try {
			endTimeUnit = LengthOfTime.valueOf(e.getAttributeValue(END_TIME_UNIT_XML_TAG));
		} catch (NullPointerException exception) {
			logger.warn("No attribute: '" + LengthOfTime.END_TIME_UNIT_XML_TAG + "'.  Defaulting to value = " + endTimeUnit);
		}
		maxNumberOfEvents = Integer.parseInt(e.getAttributeValue(MAX_EVENTS_XML_TAG));
		
	}

	@Override
	public void clone(ModuleParamsInterface params) {
		this.prngSeed = ((SimModuleConfigParams) params).prngSeed;
		this.waitForSteadyState = ((SimModuleConfigParams) params).waitForSteadyState;
		this.startTime = ((SimModuleConfigParams) params).startTime;
		this.endTime = ((SimModuleConfigParams) params).endTime;
		this.endTimeUnit = ((SimModuleConfigParams) params).endTimeUnit;
		this.maxNumberOfEvents = ((SimModuleConfigParams) params).maxNumberOfEvents;
	}

	@Override
	public String getXMLElementNameString() {
		return XML_ELEMENT_NAME;
	}

	public String toString() {
		
		String s = "SimModuleConfigParams [";
		s+= "prngSeed='" + this.prngSeed + 
				"', waitSteadyState='" + this.waitForSteadyState + 
				"', startTime='"+ this.startTime +
				"', endTime='"+ this.endTime+
				"', endTimeUnit='"+ this.endTimeUnit.toString()+
				"', maxEvents='" + this.maxNumberOfEvents + "']";
		return s;
	}
	
	public long getSeed() {
		return this.prngSeed;
	}
	
    /**
     * Set random seed for simulation.  If seed == -1, randomize seed automatically.
     * @param seed
     */
	public void setSeed(long seed) {

	    if(seed == -1) {
    		long randomSeed = new Random().nextLong();
    		logger.warn("Seed is -1.  Randomizing new seed: " + randomSeed);
    		this.prngSeed = randomSeed;
    	} else {
    		this.prngSeed = seed;
    	}
	}
	
	public boolean isWaitForSteadyState() {
		return this.waitForSteadyState;
	}
	
	public void setWaitForSteadyState(boolean wait) {
		this.waitForSteadyState = wait;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public void setStartTime(long start) {
		this.startTime = start;
	}
	
	/** Return end time in EndTimeUnits.
	 * 
	 *  Note: Use #getSimulationEndTime() to get the
	 *  end time in simulation time units (microseconds) **/
	public long getEndTime() {
		return this.endTime;
	}
	
	public void setEndTime(long end) {
		logger.debug("Updating end time = " + end);
		this.endTime = end; 
	}

	public LengthOfTime getEndTimeUnit() {
		return this.endTimeUnit;
	}
	
	public void setEndTimeUnit(LengthOfTime unit) {
		logger.debug("Updating end time unit = " + unit);
		this.endTimeUnit = unit;
	}
	
    public String getEndTimeString() {   	
    	return this.endTime + ((this.endTime>1)?" ":"s ") + this.endTimeUnit;
    }
    
    /** Return the end time in simulation time (microseconds) **/
    public long getSimulationEndTime() {
        long time = this.endTime;
        time *= endTimeUnit.getTimeInSeconds(); //end time in seconds
        time *= 1000000; //microseconds
        return time;
    }
    
	public int getMaxNumEvents() {
		return this.maxNumberOfEvents;
	}
	
	public void setMaxNumEvents(int max) {
		this.maxNumberOfEvents = max;
	}

}
