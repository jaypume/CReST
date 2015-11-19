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
package sim.module.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.log4j.Logger;

import sim.module.AbstractModuleRunner;
import sim.module.Module;
import sim.module.event.EventQueue;
import sim.module.pricing.bo.PriceManager;
import sim.module.service.ServiceModuleRunner;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

public class PricingModuleRunner extends AbstractModuleRunner {
	private static PricingModuleRunner instance = null;
	
	protected static final long TIME_BETWEEN_LOGS = TimeManager.hoursToSimulationTime(1);
	
	protected long logPeriod = PricingModuleRunner.TIME_BETWEEN_LOGS;
	
	private static int counter = 0;
	public static Logger logger = Logger.getLogger(PricingModuleRunner.class);
	
	// Each Datacentre has its own pricing.
	private List<PriceManager> priceManagerList;
	
	private PricingModuleRunner() {
		logger.info("Constructing " + this.getClass().getSimpleName());
	}
	
	public static PricingModuleRunner getInstance() {
		if (instance == null) {
            instance = new PricingModuleRunner();
        }
		
        return instance;
	}
	
	public PriceManager getPriceManager(int dc_idx) {
		if (priceManagerList == null) {
			logger.fatal("PriceManagerList is null. Exiting system.");
			return null;
		}
		
		if (dc_idx >= 0 && dc_idx < priceManagerList.size()) {
			return priceManagerList.get(dc_idx);
		} else {
			logger.fatal("Out of bounds: Requesting datacentre_index = " + dc_idx + ". Exiting system.");
			return null;
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
	}

	@Override
	public void worldUpdated(World w) {
		logger.info("worldUpdated... #" + (++counter));
		
		if (Module.PRICING_MODULE.isActive()) {
			logger.info("Set as EventQueue observer...");
			EventQueue.getInstance().addObserver(this);
			logger.info("Pricing module is on...");
			
			priceManagerList = new ArrayList<PriceManager>();
			
			for (Datacentre dc : World.getInstance().getDatacentres()) {
				priceManagerList.add(new PriceManager(dc));
			}
			
			ServiceModuleRunner.getInstance().setLogPeriod(TimeManager.monthsToSimulationTime(1));
		}
	}

	@Override
	public String getLogFileName() {
		return "pricing";
	}

	@Override
	protected String getLogTitleString() {
		return null;
	}

	@Override
	public boolean isActive() {
		return Module.PRICING_MODULE.isActive();
	}
}
