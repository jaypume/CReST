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
package utility.direction;

public enum CompassDirection
{
	//Stores direction and opposite
	NORTH("North", "NORTH"), 
	SOUTH("South", "SOUTH"), 
	EAST("East", "EAST"),
	WEST("West", "WEST");

	private String humanReadableString;
	private String direction;
	
	CompassDirection(String humanReadableString, String direction)
	{
		this.humanReadableString = humanReadableString;
		this.direction = direction;
	}
	
	/**
	 * A short label description of the Enum class
	 * 
	 * @return description label
	 */
	public static String getLabel() {
		return "Direction";
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
		return direction;
	}
	
	public CompassDirection getOpposite()
	{
		switch(this)
		{
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		}
		
		return getDefault();
	}
	
	public static CompassDirection getDefault()
	{
		return NORTH;
	}
}