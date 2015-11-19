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
package sim.market.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.market.trader.BaseTrader;

public class DemandSchedule extends PriceVolumeSchedule{

	/**
	 * Construct an empty Demand Schedule for a commodity
	 * @param commodity
	 */
	public DemandSchedule(Commodity commodity) {
		super("Demand Schedule ("+commodity.getName()+")", commodity, Direction.BUY);
	}
	
	/**
	 * Construct a demand schedule from a list of Buy Assignments
	 * @param assignments
	 */
	public DemandSchedule(List<Assignment> assignments) {
		super(assignments);
	}
	
	/**
	 * Construct a demand schedule for a given commodity using a population of traders. 
	 * @param population
	 * @param commodity
	 */
	public DemandSchedule(List<BaseTrader> population, Commodity commodity) {
		
		this(commodity);
		
		List<Assignment> demand = new ArrayList<Assignment>();
		for(BaseTrader t: population) {			
			demand.addAll(t.getDemandAssignments(c));
		}
		logger.info("Population demand: " + demand);
		Collections.sort(demand);

		setSchedule(demand);

		logger.info("Demand schedule = " + this);
	}
}
