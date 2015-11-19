package sim.module.thermal.model;

import org.apache.log4j.Logger;

/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
public class ThermalModelFactory {

	public static Logger logger = Logger.getLogger(ThermalModelFactory.class);
	
	/**
	 * Enumeration class for Thermal Models
	 * 
	 * @author cszjpc
	 *
	 */
	public enum ThermModel { 
		
		//The Thermal Models
		DIFF("Simple Diffusion", "DIFF"), // The original model written by Luke Drury (Summer 2011)
		DIFF_ADV("Simple Convection", "DIFF_ADV"); // New model with advection (JPC, Dec 2013)
		
		private String humanReadableString;
		private String nameString;
		public static ThermModel DEFAULT = DIFF;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 * @param nameString - must be exactly the same as the enum type name
		 */
		ThermModel(String humanReadableDescription, String nameString) {
			this.humanReadableString = humanReadableDescription;
			this.nameString = nameString;
		}
		
		/**
		 * Human-readable Protocol string 
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
		
		/**
		 * A short label description of the Enum class
		 * 
		 * @return name label
		 */
		public static String getLabel() {
			return "Thermal Model";
		}
	}
	
	/**
	 * Do not instantiate an object of this type (hence, private)
	 */
	private ThermalModelFactory() {}
	
	public static AbstractThermalModel getModel(ThermModel modelType) {
		
		logger.debug("Creating AbstractThermalModel of type: " + modelType);
		
		//Return Correct Topology Type
		switch(modelType) {
		
			case DIFF: return (AbstractThermalModel) new SimpleDiffusionModel(modelType.humanReadableString);
			case DIFF_ADV: return (AbstractThermalModel) new SimpleConvectionModel(modelType.humanReadableString);
			
			default: {
				logger.fatal("Uknown Thermal Model type: " + modelType + " Exiting System...");
				System.exit(-1);
				return null;				
			}	
		}
	}
}
