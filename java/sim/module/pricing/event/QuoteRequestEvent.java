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
package sim.module.pricing.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.module.broker.BrokerModuleRunner;
import sim.module.broker.event.BrokerReceiveQuotesEvent;
import sim.module.event.Event;
import sim.module.event.EventQueue;
import sim.module.pricing.PricingModuleRunner;
import sim.module.pricing.bo.PriceManager;
import sim.module.pricing.bo.PriceType;
import sim.module.pricing.bo.Quote;
import sim.physical.Datacentre;
import sim.physical.World;
import utility.time.TimeManager;

public class QuoteRequestEvent extends Event {
	public static Logger logger = Logger.getLogger(QuoteRequestEvent.class);
	
	private long pStartTime;
	private int instances;
	private PriceType priceType;
	
	private List<Quote> quotes;
	
	public QuoteRequestEvent(long pStartTime, int instances, PriceType priceType) {
		super(pStartTime, -1);
		this.pStartTime = pStartTime;
		this.instances = instances;
		this.priceType = priceType;
		this.quotes = new ArrayList<Quote>();
	}

	@Override
	protected boolean performEvent() {
		for (Datacentre dc : World.getInstance().getDatacentres()) {
			logger.info("Price is being requested from Datacentre " + dc.getID());
			PriceManager pm = PricingModuleRunner.getInstance().getPriceManager(dc.getID());
			quotes.add(pm.requestQuote(instances, priceType));
		}

		return true;
	}

	@Override
	protected void generateEvents() {
		// Generate an event to return the quote to the broker.
		logger.info("Generating a Quote event. Prices = " + quotes);
    	
		if (this.isIgnored())
			return;
    	else {
    		if (BrokerModuleRunner.getInstance().isActive()) {
    			BrokerReceiveQuotesEvent quoteEvent = new BrokerReceiveQuotesEvent(pStartTime + TimeManager.secondsToSimulationTime(1), quotes, priceType);
    	    	EventQueue.getInstance().addEvent(quoteEvent);
    	        numEventsGenerated++;
    		}
    	}
	}
}
