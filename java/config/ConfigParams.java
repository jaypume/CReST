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
package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class for reading in simulation parameters from a parameters file.
 * 
 * These parameters over-ride any parameters set in the
 * XML configuration file.  Used for multiple simulation runs with 
 * the same datacente.
 * 
 * @author cszjpc
 *
 */
public class ConfigParams {

	public static Logger logger = Logger.getLogger(ConfigParams.class);
	public static String filename;

	protected static Properties prop;

	/**
	 * Initialise the properties of the config parameters"|"
	 * 
	 * @param paramsFilename
	 */
	public static void init(String paramsFilename) {
		filename = paramsFilename;
		
		//Create a new Properties instance
		prop = new Properties();
		
		try {
			//Read in properties from file
			prop.load(new FileInputStream(filename));
			
			logger.info("Configuration parameters file loaded: '" + filename + "'");
		
			logger.info(prop);
		} catch (IOException e) {
			logger.error("Error opening parameters file: '" + filename + "'");
		}
	}
	
	/**
	 * Return the properties of the configuration parameters
	 * @return
	 * 		the configuration properties
	 */
	public static Properties getProperties() {
		return prop;
	}

	/**
	 * Avoid unnecessary file access by using the instance method for properties
	 * that are queried more frequently
	 */

	/**
	 * @param the
	 *            name of the property in the configuration file
	 * @return the String-value in the property, or null if doesn't exist
	 * 
	 * Use this static method for properties that are not queried frequently
	 */
	protected static String getParameter(String param) {

		prop = new Properties();

		try {
			prop.load(new FileInputStream(filename));
		} catch (IOException e) {
			logger.error("Error opening parameters file: " + filename);
		}

		return prop.getProperty(param);
	}

	/**
	 * Load the parameter values from the properties file
	 * 
	 * @return true
	 */
	public static SettingsManagerAccess update(SettingsManagerAccess settings) {
				
		logger.info("Updating settings using config parameters: " + prop);
		
		for(Entry<Object, Object> entry: prop.entrySet()) {
		
			if (settings.setValue(entry.getKey().toString(), entry.getValue().toString()) ) {
				logger.info("Config value updated: " + entry.getKey().toString() + "=" +  entry.getValue().toString());
			}
		}
		
		return settings;
	}
}
