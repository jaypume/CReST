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
package sim.module.thermal;

import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.EventQueue;
import sim.module.thermal.bo.ThermalGrid;
import sim.module.thermal.configParams.ThermalModuleConfigParams;
import sim.module.thermal.event.ThermalLogEvent;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

public class ThermalModuleRunner extends AbstractModuleRunner{

	public static final long TIME_BETWEEN_LOGS = TimeManager.secondsToSimulationTime(1);
	
	private static int counter = 0;
	
	public static Logger logger = Logger.getLogger(ThermalModuleRunner.class);
	
	private static ThermalModuleRunner instance = null;
	
	/** Thermal grids.  One for each datacentre. */
	private ArrayList<ThermalGrid> thermalGridManager;
	
	protected ThermalModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static ThermalModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new ThermalModuleRunner();
        }
        return instance;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		//JC May 2012: Are temperatures affected by any other thread events?
		//At the moment no, but perhaps they could be calculated
		//on the fly based on new failures/increased output, etc.
	}

	@Override
	public void worldUpdated(World w) {
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.THERMAL_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.info("Thermal module is on.  Initialising thermal grids...");
			
			//it is important that this is cleared/reset each time world is updated
			thermalGridManager = new ArrayList<ThermalGrid>(); 
			
			//for each datacentre, generate a thermal grid
			for(Datacentre dc: World.getInstance().getDatacentres()) {

				int resolution = 2; // how many temp cells to grid cells
				
				ThermalGrid grid =  new ThermalGrid(
						(ThermalModuleConfigParams) Module.THERMAL_MODULE.getParams(),
						new int[] { dc.getGridWidth(), dc.getGridHeight() }, 
						new double[] { Datacentre.GRID_SQUARE_LENGTH, Datacentre.GRID_SQUARE_LENGTH },
						resolution);
				grid.fill(dc);
				
				logger.info("ThermalGrid initialised for datacenter " + dc.getID());
						
		    	thermalGridManager.add(grid);	
			}	
		} else {
			//do nothing - module not on
		}
	}
	
	public ThermalGrid getThermalGrid(int datacentre_index) {
		if(datacentre_index>=0 && datacentre_index<thermalGridManager.size()) {
			return thermalGridManager.get(datacentre_index);
		} else {
			logger.fatal("Out of bounds: Requesting datacentre_index = " + datacentre_index + ". Exiting system.");
			System.exit(-1);
			return null; //we wont get here.
		}
	}
	
    /**
     * Get all the thermal grids in this world (one for each datacentre).
     * 
     * @return array of thermal grids.
     */
    public ThermalGrid[] getThermalGrids()
    {
        ThermalGrid tempGrids[] = new ThermalGrid[thermalGridManager.size()];
        
        for (int i = 0; i < thermalGridManager.size(); i++)
        {
            tempGrids[i] = thermalGridManager.get(i);
        }
        
        return tempGrids;
    }
    
    /**
     * Update all thermal grids
     * 
     * @param timeNow - the current simulation time.
     */
    public void updateTemperature(final long timeNow)
    {
    	for(ThermalGrid g: thermalGridManager) {
    		g.update(timeNow);
    	}
    }

	@Override
	public String getLogFileName() {
		return "thermal";
	}

	@Override
	protected String getLogTitleString() {
		return ThermalLogEvent.COLUMN_TITLE_STRING;
	}

	@Override
	public boolean isActive() {
		return Module.THERMAL_MODULE.isActive();
	}
}
