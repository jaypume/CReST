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

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import sim.market.trader.BaseTrader;
import sim.market.trader.InstanceTrader;
import sim.module.log.Log;
import sim.module.log.LogManager;

public class InstanceTraderPopLogger extends AbstractMarketLogger{

	public static Logger logger = Logger.getLogger(InstanceTraderPopLogger.class);
	
	protected static InstanceTraderPopLogger singleton;
	
	protected InstanceTrader trader;
	
	private InstanceTraderPopLogger() {
		super("population");
	}
	
	public static InstanceTraderPopLogger getSingleton() {
		if(singleton == null) {
			singleton = new InstanceTraderPopLogger();
		}
		return singleton;
	}

	@Override
	protected String getLogTitleString() {
		return InstanceTrader.getLogTitleString();
	}
	
	@Override
	public void resetValues() {

		// do nothing?
		logger.warn("resetValues() function does nothing...");
	}
	
	/**
	 * Add instance trader to log file
	 * @param t - instance trader
	 */
	public void logTrader(InstanceTrader t) {
		
		logger.info("Adding trader to log file: " + t);
		trader = t;
		writeLog();
	}
	
	public void writeLog(List<BaseTrader> population) {
		
		Collections.sort(population); // order by id
		
		for(BaseTrader t: population) {
			
			if(t instanceof InstanceTrader) {
				logTrader((InstanceTrader) t);
			} else {
				logger.warn("Cannot log trader of type: " + t.getClass());
			}
		}
	}
	
	@Override
	public void writeLog() {
       
		Log log = new Log();
    	log.add(trader.toLogString());
    	
    	LogManager.writeLog(resultsLog,log);
    	logger.info("Log written: " + log);
	}
}
