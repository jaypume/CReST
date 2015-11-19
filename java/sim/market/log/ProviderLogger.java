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
package sim.market.log;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sim.module.log.Log;
import sim.module.log.LogManager;

public class ProviderLogger extends AbstractMarketLogger{

	public static Logger logger = Logger.getLogger(ProviderLogger.class);

	protected DecimalFormat format = new DecimalFormat("#.##");
	
	protected static ProviderLogger singleton;
	
	protected double balance;			// total balance for the provider
	protected double comms;				// total commissions for the provider
	protected double sales;    			// total sales income for the provider
	protected int onDemandSales; 		// total on demand instance sales
	protected int RISales; 				// total reserved instance sales
	
	private ProviderLogger() {
		super("provider");
	}
	
	public static ProviderLogger getSingleton() {
		if(singleton == null) {
			singleton = new ProviderLogger();
		}
		return singleton;
	}

	@Override
	protected String getLogTitleString() {
		return "Month, Balance, Commissions, Sales Income, #On Demand, #RIs";
	}
	
	@Override
	public void resetValues() {
		
		balance = 0;
		comms = 0;
		sales = 0;
		onDemandSales = 0;
		RISales = 0;
	}
	
	public void addODSale(double price) {
		onDemandSales ++; 
		sales += price;
		balance += price;
		
		logger.debug("New OD sale...");
		logger.debug("#units="+ onDemandSales + ", totalIncome=" + sales);
	}
	
	public void addRISale(double price) {
		RISales ++; 
		sales += price;
		balance += price;
		
		logger.debug("New RI sale...");
		logger.debug("#units="+ RISales + ", totalIncome=" + sales);
	}
	
	public void addCommission(double charge) {
		comms += charge;
		balance += charge;
	}
	
	@Override
	public void writeLog() {
       
		Log log = new Log();
    	log.add(String.valueOf(month));
    	log.add(String.valueOf(balance));
    	log.add(String.valueOf(comms));
    	log.add(String.valueOf(sales));
    	log.add(String.valueOf(onDemandSales));
    	log.add(String.valueOf(RISales));
    	
    	LogManager.writeLog(resultsLog,log);
    	logger.info("Log written: " + log);
	}
}
