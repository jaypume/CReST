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
package sim.module.costs;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.costs.bo.Costs;
import sim.module.costs.bo.Currency;
import sim.module.event.EventQueue;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.LengthOfTime;

public class CostsModuleRunner extends AbstractModuleRunner{
	
	private static int counter = 0;
	
	public static Logger logger = Logger.getLogger(CostsModuleRunner.class);
	
	private static CostsModuleRunner instance = null;
	
	/** Costs.  One for each Datacentre. */
	private ArrayList<Costs> costsManager;
	
	protected CostsModuleRunner () {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static CostsModuleRunner getInstance() {
		if (instance == null)
        {
            instance = new CostsModuleRunner();
        }
        return instance;
	}

	@Override
	public void worldUpdated(World w) {
		// TODO Auto-generated method stub
		
		logger.info("worldUpdated... #" + (++counter));
		
		if(Module.COSTS_MODULE.isActive()) {
			
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			
			logger.info("Costs module is on...");
				
			//TODO - JC May 2012, what needs to be done here?
			//What does costs module actually do?
			
			//do something!
			
			//it is important that this is cleared/reset each time world is updated
			costsManager = new ArrayList<Costs>(); 
			
			//for each datacentre, instantiate a Costs object
			for(Datacentre dc: World.getInstance().getDatacentres()) {

				Costs costs =  new Costs(dc);
				
				
				logger.info("Costs initialised for datacenter " + dc.getID());
						
		    	costsManager.add(costs);	
			}	
			
		} else {
			//do nothing - module not on
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		//What is this module interested in?  What does it listen to?  Are there cost events?
	}
	
    /**
     * Return the Costs object associated with this datacentre
     * @return - Costs
     */
    public Costs getCosts(int datacentre_index) {
    	
    	if(datacentre_index>=0 && datacentre_index<costsManager.size()) {
			return costsManager.get(datacentre_index);
		} else {
			logger.fatal("Out of bounds: Requesting datacentre_index = " + datacentre_index + ". Exiting system.");
			System.exit(-1);
			return null; //we wont get here.
		}
    }  
    
    /**
     * Dump costs data to the screen
     * 
     * @param datacentre_index - the datacente ID
     */
    public void dumpCosts(int datacentre_index) {
    	
        // TODO: tidy this
        NumberFormat nf = Currency.getNumberFormat();

        LengthOfTime time = LengthOfTime.HOUR;
        System.out.println("\nCost for Datacenter " + datacentre_index + " for a " + time.name());
        System.out.println("Physical Space Cost: " + nf.format(costsManager.get(datacentre_index).PhysicalSpaceCost(time.getTimeInSeconds())));
        System.out.println("Hardware Power Cost: " + nf.format(costsManager.get(datacentre_index).HardwarePowerCost(time.getTimeInSeconds())));
        System.out.println("Cooling Power Cost: " + nf.format(costsManager.get(datacentre_index).CoolingPowerCost(time.getTimeInSeconds())));

        System.out.println("Personnel Cost: " + nf.format(costsManager.get(datacentre_index).PersonnelCost(time.getTimeInSeconds())));
        System.out.println("Hardware Depreciation Cost: " + nf.format(costsManager.get(datacentre_index).HardwareDepreciationCost(time.getTimeInSeconds())));
        System.out.println("License Cost: " + nf.format(costsManager.get(datacentre_index).getSoftwareLicenseCost()));

        System.out.println("TOTAL Cost: " + nf.format(costsManager.get(datacentre_index).totalCost(time.getTimeInSeconds())));

        time = LengthOfTime.DAY;
        System.out.println("\nCost for Datacenter " + datacentre_index + " for a " + time.name());
        System.out.println("Physical Space Cost: " + nf.format(costsManager.get(datacentre_index).PhysicalSpaceCost(time.getTimeInSeconds())));
        System.out.println("Hardware Power Cost: " + nf.format(costsManager.get(datacentre_index).HardwarePowerCost(time.getTimeInSeconds())));
        System.out.println("Cooling Power Cost: " + nf.format(costsManager.get(datacentre_index).CoolingPowerCost(time.getTimeInSeconds())));

        System.out.println("Personnel Cost: " + nf.format(costsManager.get(datacentre_index).PersonnelCost(time.getTimeInSeconds())));
        System.out.println("Hardware Depreciation Cost: " + nf.format(costsManager.get(datacentre_index).HardwareDepreciationCost(time.getTimeInSeconds())));
        System.out.println("License Cost: " + nf.format(costsManager.get(datacentre_index).getSoftwareLicenseCost()));

        System.out.println("TOTAL Cost: " + nf.format(costsManager.get(datacentre_index).totalCost(time.getTimeInSeconds())));

        time = LengthOfTime.MONTH;
        System.out.println("\nCost for Datacenter " + datacentre_index + " for a " + time.name());
        System.out.println("Physical Space Cost: " + nf.format(costsManager.get(datacentre_index).PhysicalSpaceCost(time.getTimeInSeconds())));
        System.out.println("Hardware Power Cost: " + nf.format(costsManager.get(datacentre_index).HardwarePowerCost(time.getTimeInSeconds())));
        System.out.println("Cooling Power Cost: " + nf.format(costsManager.get(datacentre_index).CoolingPowerCost(time.getTimeInSeconds())));

        System.out.println("Personnel Cost: " + nf.format(costsManager.get(datacentre_index).PersonnelCost(time.getTimeInSeconds())));
        System.out.println("Hardware Depreciation Cost: " + nf.format(costsManager.get(datacentre_index).HardwareDepreciationCost(time.getTimeInSeconds())));
        System.out.println("License Cost: " + nf.format(costsManager.get(datacentre_index).getSoftwareLicenseCost()));

        System.out.println("TOTAL Cost: " + nf.format(costsManager.get(datacentre_index).totalCost(time.getTimeInSeconds())));

        time = LengthOfTime.YEAR;
        System.out.println("\nCost for Datacenter " + datacentre_index + " for a " + time.name());
        System.out.println("Physical Space Cost: " + nf.format(costsManager.get(datacentre_index).PhysicalSpaceCost(time.getTimeInSeconds())));
        System.out.println("Hardware Power Cost: " + nf.format(costsManager.get(datacentre_index).HardwarePowerCost(time.getTimeInSeconds())));
        System.out.println("Cooling Power Cost: " + nf.format(costsManager.get(datacentre_index).CoolingPowerCost(time.getTimeInSeconds())));

        System.out.println("Personnel Cost: " + nf.format(costsManager.get(datacentre_index).PersonnelCost(time.getTimeInSeconds())));
        System.out.println("Hardware Depreciation Cost: " + nf.format(costsManager.get(datacentre_index).HardwareDepreciationCost(time.getTimeInSeconds())));
        System.out.println("License Cost: " + nf.format(costsManager.get(datacentre_index).getSoftwareLicenseCost()));

        System.out.println("TOTAL Cost: " + nf.format(costsManager.get(datacentre_index).totalCost(time.getTimeInSeconds())));
    }

	@Override
	public String getLogFileName() {
		return "costs";
	}

	@Override
	protected String getLogTitleString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActive() {
		return Module.COSTS_MODULE.isActive();
	}
}
