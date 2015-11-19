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

package app;

import gui.SimGUIMainWindow;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sim.SimulationRunner;
import utility.Copyright;
import utility.time.TimeManager;
import app.bo.CReSTCommandlineParser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;


public class CReSTApp
{
	
	private static Logger logger = Logger.getLogger(CReSTApp.class);
	
	private static Boolean useGUI = true;
	
	private static boolean use_params_file = false;
	
	private static boolean use_events_file = false;

	/**
	 * Method which starts the simulator.
	 * 
	 */
	public static void main(String[] args)
	{
		
		//Configure log4j using configuration properties file
		PropertyConfigurator.configure("resources/log4j_config/CReSTApp-log4j.properties");
		
		logger.info("*** Starting CReST App ***"); 
	    	
		//Parse command line parameters: exit if incorrect
		CReSTCommandlineParser parser = new CReSTCommandlineParser();
		try {
			new JCommander(parser, args);
		} catch (ParameterException e) {
			logger.error("CReST: command line parameter exception: " + e.getMessage());
			System.err.println("CReST: command line parameter exception: " + e.getMessage() +"\n");
			new JCommander(parser).usage(); //output usage to screen
			System.exit(1);
		}
		
		if(parser.information) {
			System.out.println(Copyright.info);
		}
		
		//output help to console and exit
		if(parser.help) {
			System.out.println("Usage: java CReST [options]\n");
			new JCommander(parser).usage();
			System.exit(0);
		}

		//output version to console and exit
		if(parser.version) {
			logger.info(TimeManager.log(Copyright.versionString));
			System.out.println(Copyright.versionString);
			System.exit(0);
		}

		//get config filename
		String configFilename = parser.configFileName;
		logger.info("Using config file: " + configFilename);
		
		
		//get params filename
		String paramsFilename;
		if(parser.paramsFileName.equals("none")) {
			//no params file
			use_params_file = false;
			paramsFilename = parser.paramsFileName;
			logger.warn("No params config file");
		} else {
			use_params_file = true;
			paramsFilename = parser.paramsFileName;
			logger.warn("Using params config file: " + paramsFilename);
		}
		
		//get user events filename
		String eventsFilename;
		if(parser.eventsFileName.equals("none")) {
			//no events file
			use_events_file = false;
			eventsFilename = parser.eventsFileName;
			logger.warn("No user events file passed on command line: " + eventsFilename);
		} else {
			use_events_file = true;
			eventsFilename = parser.eventsFileName;
			logger.warn("Using user events file passed on command line: " + eventsFilename);
		}

		//Check the ./log directory exists.  Create if not.
		File logDir = new File("log");
		if (!logDir.exists())
		{
			System.err.println("Warning: log directory does not exist.  Creating directory: " + logDir.getName());
			logger.error("Warning: log directory does not exist.  Creating directory: " + logDir.getName());
			logDir.mkdir();
		}
		
		//use GUI?
		if(parser.nogui) {
			useGUI = false;
			logger.info("Command line argument 'no gui' selected");
		}
		
		//create GUI if necessary
		if (useGUI)
		{
			logger.info("Creating GUI...");
			SimGUIMainWindow.create(new File(configFilename));
			
			if(use_params_file) {
				SimGUIMainWindow.getInstance().setParamsFileName(paramsFilename);
			}
			if(use_events_file) {
				SimGUIMainWindow.getInstance().setEventsFileName(eventsFilename);
			}
		}
		else
		{
			logger.info("Running without GUI.  Creating new SimulationRunner...");
			SimulationRunner simulator = new SimulationRunner(new File(configFilename));
			if(use_params_file) {
				simulator.setParamsFileName(paramsFilename);
			}
			if(use_events_file) {
				simulator.setEventsFileName(eventsFilename);
			}
			logger.info("Starting SimulationRunner...");
			simulator.run();
			logger.info("Closing CReSTApp.");
		}
	}
}
