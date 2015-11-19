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
package sim.module.log;

import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.physical.World;
import utility.time.TimeManager;

public class LogModuleRunner extends AbstractModuleRunner{
	
	public static final long TIME_BETWEEN_LOGS = TimeManager.daysToSimulationTime(1);

	private static int counter = 0;
	
	public static Logger logger = Logger.getLogger(LogModuleRunner.class);
	
	private static LogModuleRunner instance = null;
	
	protected LogModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static LogModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new LogModuleRunner();
        }
        return instance;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void worldUpdated(World w) {
		// TODO Auto-generated method stub
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.LOG_MODULE.isActive()) {
			
			//The log module just sets up the log files when world is updated.
			//It does nothing else as yet.  Don't add as observer to eventqueue.
			
			//Switch log module off for now.  It has served its purpose,
			//until we decide that we may want to do something else with it.
			Module.LOG_MODULE.setActive(false);
			
			World.getInstance().getLogManager().prepareLogFiles();
			
			logger.info("Logging module status is now " + Module.LOG_MODULE.isActive());
			
		} else {
			//do nothing - module not on
		}
	}
	
	@Override
	public String getLogFileName() {
		return null;
	}

	@Override
	protected String getLogTitleString() {
		return null;
	}

	@Override
	public boolean isActive() {
		return Module.LOG_MODULE.isActive();
	}
}
