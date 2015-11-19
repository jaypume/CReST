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
package builder.datacentre.layout;

import org.apache.log4j.Logger;

public class LayoutPatternFactory
{
	public static Logger logger = Logger.getLogger(LayoutPatternFactory.class);
	
	/**
	 * Enumerated class containing the different datacentre layout patterns
	 */
	public enum LayoutPatternType
	{
		OLD_METHOD("Old method", "OLD_METHOD"),
		LONG_AISLES("Long Aisles", "LONG_AISLES"),
		SPLIT_AISLES("Split Aisles", "SPLIT_AISLES"),
		RANDOM("Random", "RANDOM");
		
		private String mHumanReadableString;
		private String mNameString;
		
		LayoutPatternType(String pHumanReadableString, String pNameString)
		{
			mHumanReadableString = pHumanReadableString;
			mNameString = pNameString;
		}
		
		public String toString() 
		{
			return mHumanReadableString;
		}
		
		public String getNameString()
		{
			return mNameString;
		}
		
		public static LayoutPatternType getDefault()
		{
			return OLD_METHOD;
		}
		
		/**
		 * A short label description of the Enum class
		 * 
		 * @return name label
		 */
		public static String getLabel() {
			return "Layout Pattern";
		}
	}
	
	private LayoutPatternFactory() {}
	
	public static AbstractLayoutPattern getLayoutPattern(LayoutPatternType layoutPatternType)
	{
		//Return Correct layout pattern Type
		switch(layoutPatternType) 
		{
			case OLD_METHOD: return (AbstractLayoutPattern) new OldLayoutPattern();
			case LONG_AISLES: return (AbstractLayoutPattern) new LongAislesLayoutPattern();
			case SPLIT_AISLES: return (AbstractLayoutPattern) new SplitAislesLayoutPattern();
			case RANDOM: return (AbstractLayoutPattern) new RandomLayoutPattern();
			
			default:
			{
				logger.fatal("Unknown layoutPattern type: " + layoutPatternType + " Exiting System...");
				System.exit(0);
				return null;	
			}	
		}	
	}
}
