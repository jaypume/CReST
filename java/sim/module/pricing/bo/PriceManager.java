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
package sim.module.pricing.bo;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.pricing.configparams.PricingModuleConfigParams;
import sim.physical.Datacentre;

public class PriceManager {

	public static Logger logger = Logger.getLogger(PriceManager.class);
	
	private Datacentre dc;
	
	private double onDemandUpFront;
	private double onDemandMonthly;
	private double reserveMonthly;
	private double reserveUpFront;
	private int reservationLength;
	
	public PriceManager(Datacentre dc) {
		logger.debug("Creating new PriceManager for datacentre " + dc.getName());
		this.dc = dc;
		this.onDemandUpFront = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getOnDemandUpfrontPrice();
		this.onDemandMonthly = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getOnDemandMonthlyPrice();
		this.reserveMonthly = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getReservedHourlyPrice();
		this.reserveUpFront = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getReservedUpfrontPrice();
		this.reservationLength = ((PricingModuleConfigParams) Module.PRICING_MODULE.getParams()).getReservationLength();
	}
	
	public Quote requestQuote(int instancesRequired, PriceType type) {
		double upFront = 0.0, monthly = 0.0;
		int duration = 0;
		
		logger.info(type + " " + instancesRequired);
		
		if (type == PriceType.RESERVED) {
			upFront = reserveUpFront * (double)instancesRequired;
			monthly = reserveMonthly * (double)instancesRequired;
			duration = reservationLength;
		} else if (type == PriceType.ONDEMAND) {
			upFront = onDemandUpFront * (double)instancesRequired;
			monthly = onDemandMonthly * (double)instancesRequired;
			duration = 1;
		}
		return new Quote(dc.getID(), instancesRequired, upFront, monthly, duration, type);
	}
}