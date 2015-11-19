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
 * Created on 30 Aug 2011
 */
package utility.time;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sim.module.userevents.bo.EventReaderException;
import sim.physical.World;

/**
 * Class for handling time conversions.
 * 
 * All simulation time is done in microseconds. 1 second = 1,000,000
 * microseconds (10^6).
 */
public class TimeManager
{
	
	public static enum UnitTime { MICROSECOND, MILLISECOND, SECOND, MINUTE, HOUR, DAY };
	
	private static Logger logger = Logger.getLogger(TimeManager.class);
	
    protected final static long   MICROSECONDS_PER_MILLISECOND = 1000;
    protected final static long   MILLISECONDS_PER_SECOND      = 1000;
    protected final static long   SECONDS_PER_MINUTE           = 60;
    protected final static long   MINUTES_PER_HOUR             = 60;
    protected final static long   HOURS_PER_DAY                = 24;
    protected final static long   DAYS_PER_MONTH               = 28;
    
    private final static String[] MICROSECOND_STRINGS_ARRAY = {"microsecond", "micros", "micro"};
    private final static String[] MILLISECOND_STRINGS_ARRAY = {"millisecond", "millis", "milli"};
    private final static String[] SECOND_STRINGS_ARRAY = {"second", "seconds", "sec", "s"};
    private final static String[] MINUTE_STRINGS_ARRAY = {"minute", "minutes", "min", "mins"};
    private final static String[] HOUR_STRINGS_ARRAY = {"hour", "hours", "hr", "h"};
    private final static String[] DAY_STRINGS_ARRAY = {"day", "days", "d"};
    
    
    public static UnitTime getUnitTime(String unitTimeString) throws EventReaderException {
    	
    	unitTimeString.toLowerCase();
    	
    	//microsecond
    	for(int i=0; i<MICROSECOND_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(MICROSECOND_STRINGS_ARRAY[i])) return UnitTime.MICROSECOND;
    	}
    	//millisecond
    	for(int i=0; i<MILLISECOND_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(MILLISECOND_STRINGS_ARRAY[i])) return UnitTime.MILLISECOND;
    	}
    	//second
    	for(int i=0; i<SECOND_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(SECOND_STRINGS_ARRAY[i])) return UnitTime.SECOND;
    	}
    	
    	//minute
    	for(int i=0; i<MINUTE_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(MINUTE_STRINGS_ARRAY[i])) return UnitTime.MINUTE;
    	}
    	//hour
    	for(int i=0; i<HOUR_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(HOUR_STRINGS_ARRAY[i])) return UnitTime.HOUR;
    	}
    	
    	//day
    	for(int i=0; i<DAY_STRINGS_ARRAY.length; i++) {
    		if(unitTimeString.equals(DAY_STRINGS_ARRAY[i])) return UnitTime.DAY;
    	}
    	
    	//else, unit time not recognised - throw a warning
    	String message = "UnitTime not recognised: '" + unitTimeString + "'";
    	logger.warn(message);
    	throw new EventReaderException(message);
    	
    }
    
    /**
     * Convert the given number of milliseconds into simulation time units.
     * 
     * @param time_in_milliseconds
     *            the number of milliseconds to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long millisecondsToSimulationTime(final long time_in_milliseconds)
    {
        return time_in_milliseconds * MICROSECONDS_PER_MILLISECOND;
    }

    /**
     * Convert the given number of milliseconds into simulation time units.
     * 
     * @param time_in_milliseconds
     *            the number of milliseconds to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long millisecondsToSimulationTime(final double time_in_milliseconds)
    {
        return (int) (time_in_milliseconds * MICROSECONDS_PER_MILLISECOND);
    }
    
    /**
     * Convert the given number of seconds into simulation time units.
     * 
     * @param time_in_seconds
     *            the number of seconds to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long secondsToSimulationTime(final long time_in_seconds)
    {
        return millisecondsToSimulationTime(time_in_seconds * MILLISECONDS_PER_SECOND);
    }

    /**
     * Convert the given number of seconds into simulation time units.
     * 
     * @param time_in_seconds
     *            the number of seconds to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long secondsToSimulationTime(final double time_in_seconds)
    {
        return millisecondsToSimulationTime(time_in_seconds * MILLISECONDS_PER_SECOND);
    }
    
    /**
     * Convert the given number of minutes into simulation time units.
     * 
     * @param time_in_minutes
     *            the number of minutes to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long minutesToSimulationTime(final long time_in_minutes)
    {
        return secondsToSimulationTime(time_in_minutes * SECONDS_PER_MINUTE);
    }
    
    /**
     * Convert the given number of minutes into simulation time units.
     * 
     * @param time_in_minutes
     *            the number of minutes to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long minutesToSimulationTime(final double time_in_minutes)
    {
        long minutes = (long) time_in_minutes; //integer part is time in minutes
        double minutes_fraction = time_in_minutes - minutes; //fractional part of a minute
        long microseconds = (long) (minutes_fraction * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND * MICROSECONDS_PER_MILLISECOND);

        return minutesToSimulationTime(minutes) + microseconds;
    }

    /**
     * Convert the given number of hours into simulation time units.
     * 
     * @param time_in_hours
     *            the number of hours to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long hoursToSimulationTime(final long time_in_hours)
    {
        return minutesToSimulationTime(time_in_hours * MINUTES_PER_HOUR);
    }

    /**
     * Convert the given number of hours into simulation time units.
     * 
     * @param time_in_hours
     *            the number of hours to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long hoursToSimulationTime(final double time_in_hours)
    {
    	long hours = (long) time_in_hours; //integer part is time in hours
        double hours_fraction = time_in_hours - hours; //fractional part of an hour
        long microseconds = (long) (hours_fraction * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND * MICROSECONDS_PER_MILLISECOND);
        
        return hoursToSimulationTime(hours) + microseconds;
    }
    
    /**
     * Convert the given number of days into simulation time units.
     * 
     * @param time_in_days
     *            the number of days to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long daysToSimulationTime(final long time_in_days)
    {
        return hoursToSimulationTime(time_in_days * HOURS_PER_DAY);
    }

    /**
     * Convert the given number of days into simulation time units.
     * 
     * @param time_in_days
     *            the number of days to convert.
     * 
     * @return the number of simulation time units.
     */
    public static long daysToSimulationTime(final double time_in_days)
    {
    	long days = (long) time_in_days; //integer part is time in days
        double days_fraction = time_in_days - days; //fractional part of a day
        long microseconds = (long) (days_fraction * HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND * MICROSECONDS_PER_MILLISECOND);

        return daysToSimulationTime(days) + microseconds;
    }
    
    /**
     * Convert the given number of months into simulation time units.
     * 
     * @param time_in_months
     *            the number of months to convert.
     * 
     * @return the number of simulation time units (microseconds).
     */
    public static long monthsToSimulationTime(final long time_in_months)
    {
        return daysToSimulationTime(time_in_months * DAYS_PER_MONTH);
    }

    /**
     * Convert the given number of months into simulation time units.
     * 
     * @param time_in_months
     *            the number of months to convert.
     * 
     * @return the number of simulation time units (microseconds).
     */
    public static long monthsToSimulationTime(final double time_in_months)
    {
    	long months = (long) time_in_months; //integer part is time in months
        double months_fraction = time_in_months - months; //fractional part of a month
        long microseconds = (long) (months_fraction * DAYS_PER_MONTH * HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND * MICROSECONDS_PER_MILLISECOND);
        
        return monthsToSimulationTime(months) + microseconds;
    }
    
    /**
     * Convert the given simulation time into number of milliseconds.
     * 
     * @param simulationTime
     *            the simulation time units.
     * 
     * @return the simulation time in milliseconds.
     */
    public static long simulationTimeToMilliseconds(final long simulationTime)
    {
        return simulationTime / MICROSECONDS_PER_MILLISECOND;
    }

    /**
     * Convert the given simulation time into number of seconds.
     * 
     * @param simulationTime
     *            the simulation time units.
     * 
     * @return the simulation time in seconds.
     */
    public static long simulationTimeToSeconds(final long simulationTime)
    {
        return simulationTimeToMilliseconds(simulationTime) / MILLISECONDS_PER_SECOND;
    }

    /**
     * Convert the given simulation time into number of minutes.
     * 
     * @param simulationTime
     *            the simulation time units.
     * 
     * @return the simulation time in minutes.
     */
    public static long simulationTimeToMinutes(final long simulationTime)
    {
        return simulationTimeToSeconds(simulationTime) / SECONDS_PER_MINUTE;
    }

    /**
     * Convert the given simulation time into number of hours.
     * 
     * @param simulationTime
     *            the simulation time units.
     * 
     * @return the simulation time in hours.
     */
    public static long simulationTimeToHours(final long simulationTime)
    {
        return simulationTimeToMinutes(simulationTime) / MINUTES_PER_HOUR;
    }

    /**
     * Convert the given simulation time into number of days.
     * 
     * @param simulationTime
     *            the simulation time units.
     * 
     * @return the simulation time in days.
     */
    public static long simulationTimeToDays(final long simulationTime)
    {
        return simulationTimeToHours(simulationTime) / HOURS_PER_DAY;
    }
    
    /**
     * Return the current simulation time rounded down to the nearest time unit
     * 
     * @param timeUnit - the TimeManager.UnitTime (e.g., SECOND, HOUR, DAY...)
     * 
     * @return simulation time rounded down to the nearest unit time
     */
    public static long getTime(TimeManager.UnitTime timeUnit) {
    	switch(timeUnit) {
    	case MICROSECOND: return World.getInstance().getTime(); 
    	case MILLISECOND: return simulationTimeToMilliseconds(World.getInstance().getTime()); 
    	case SECOND: return simulationTimeToSeconds(World.getInstance().getTime()); 
    	case MINUTE: return simulationTimeToMinutes(World.getInstance().getTime()); 
    	case HOUR: return simulationTimeToHours(World.getInstance().getTime());  
    	case DAY: return simulationTimeToDays(World.getInstance().getTime()); 
    	default: logger.warn("Unknown timeUnit="+timeUnit+", returning -1"); return -1;
    	}
    } 
    
    /**
     * Convert to simulation time (milliseconds)
     * 
     * e.g., 10 seconds (timeUnit = UnitTime.SECOND, time = 10) returns long 10000000 milliseconds
     * 
     * @param time - the time to convert
     * @param timeUnit - the TimeManager.UnitTime (e.g., SECOND, HOUR, DAY...)
     * 
     * @return simulation time in milliseconds
     */
    public static long convertToSimultionTime(TimeManager.UnitTime timeUnit, long time) {
    	switch(timeUnit) {
    	case MICROSECOND: return time;
    	case MILLISECOND: return TimeManager.millisecondsToSimulationTime(time); 
    	case SECOND: return TimeManager.secondsToSimulationTime(time);
    	case MINUTE: return TimeManager.minutesToSimulationTime(time); 
    	case HOUR: return TimeManager.hoursToSimulationTime(time);  
    	case DAY: return TimeManager.daysToSimulationTime(time);
    	default: logger.warn("Unknown timeUnit="+timeUnit+", returning -1"); return -1;
    	}
    } 
    
    /**
     * Return a string describing a time unit
     * 
     * @param timeUnit - the time unit to describe
     * 
     * @return a String description of timeUnit
     */
    public static String getTimeUnitString(TimeManager.UnitTime timeUnit) {
    	switch(timeUnit) {
    	case MICROSECOND: return "microseconds"; 
    	case MILLISECOND: return "milliseconds"; 
    	case SECOND: return "seconds"; 
    	case MINUTE: return "minutes"; 
    	case HOUR: return "hours";  
    	case DAY: return "days"; 
    	default: logger.warn("Unknown timeUnit="+timeUnit+", returning 'unkown'"); return "unkown time unit";
    	}
    }
    
    /**
     * Return a time stamped log message
     * @param message
     * @return message with tisim time preprended
     */
    public static String log(String message) {
//    	logger.debug("Getting log srtring.  Calling getTimeString...");
    	return getTimeString() + " " + message;
    }
    
    /**
     * Return the current simulation time as a human readable string
     * 
     * @return 'day D [ hh:mm:ss] ms (simulationTime)'
     */
    public static String getTimeString() {
//    	logger.debug("Getting time string.  calling World...");
    	return getTimeString(World.getInstance().getTime());
    }
    
    /**
     * Return the current simulation time as a verbose human readable string
     * 
     * @return 'year Y day D [ hh:mm:ss] ms (simulationTime)'
     */
    public static String getTimeStringVerbose() {
//    	logger.debug("Getting time string.  calling World...");
    	return getTimeString(World.getInstance().getTime());
    }
    
    /**
     * Return the current simulation time (simulation readable)
     */
    public static long getTimeNow() {
    	return World.getInstance().getTime();
    }
    
    /** Return a time as a human readable string
     *
     * @param time  - time to convert to string
     * 
     * @return 'day D [ hh:mm:ss] ms (simulationTime)'
     */
    public static String getTimeString(long time) {

    	DecimalFormat df2 = new DecimalFormat("00");
    	DecimalFormat df3 = new DecimalFormat("000");
    	long days = TimeManager.simulationTimeToDays(time);
    	long hours = TimeManager.simulationTimeToHours(time) % 24;
    	long mins = TimeManager.simulationTimeToMinutes(time) % 60;
    	long secs = TimeManager.simulationTimeToSeconds(time) % 60;
    	long millis = TimeManager.simulationTimeToMilliseconds(time) % 1000;
    	String timeString = "["+ days + "," + df2.format(hours) + ":" + df2.format(mins) + ":" + df2.format(secs) + "," + df3.format(millis) + "]";
    	return timeString;
    	
    }
}
