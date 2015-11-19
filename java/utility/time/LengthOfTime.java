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
 * Defines time periods in seconds
 * 
 * @author Alex Sheppard
 */
package utility.time;

public enum LengthOfTime
{
	//The time units
	SECOND("Seconds", "SECOND", 1), 
	MINUTE("Minutes", "MINUTE", 60), 
	HOUR("Hours", "HOUR", 3600), 
	DAY("Days", "DAY", 86400), 
	MONTH("Months", "MONTH", 2629744), 
	YEAR("Years", "YEAR", 31536000);

	public static String END_TIME_UNIT_XML_TAG = "endTimeUnit";
	private final int timeInSeconds;

	private String humanReadableString;
	private String nameString;
	
	LengthOfTime(String humanReadableDescription, String nameString, int numberOfSeconds)
	{
		this.timeInSeconds = numberOfSeconds;
		this.humanReadableString = humanReadableDescription;
		this.nameString = nameString;
	}

	/** Return number of seconds in time unit **/
	public int getTimeInSeconds()
	{
		return timeInSeconds;
	}
	
	/**
	 * A short label description of the Enum class
	 * 
	 * @return description label
	 */
	public static String getLabel() {
		return "Time Unit";
	}
	
	/**
	 * Human-readable name string 
	 */
	public String toString() {
		return humanReadableString;
	}
	
	/**
	 * Enumeration name as string
	 */
	public String getNameString() {
		return nameString;
	}
}