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

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import builder.MainWindow;
import builder.prefs.BuilderPreferences;

public class CReSTBuilderApp
{
	public static Logger logger = Logger.getLogger(CReSTBuilderApp.class);
	public static void main(String[] args)
	{
		//Configure log4j using configuration properties file
		PropertyConfigurator.configure("resources/log4j_config/CReSTBuilderApp-log4j.properties");
		
		logger.info("*** Starting CReST Builder App ***"); 
		
		// Have we been passed any arguments?
		if (args.length == 0)
		{
			// Launch builder
			new MainWindow(new BuilderPreferences());
		} else
		{
			File loadMe;
			try{
				loadMe = new File(args[0]);
				new MainWindow(new BuilderPreferences(), loadMe);
			}catch(Exception e)
			{
				System.err.println("Sorry, '" + args[0] + "' could not be loaded. Failed with: " + e.getMessage());
			}
		}
			
		
	}
}
