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
package sim.module.broker.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sim.module.broker.BrokerModuleRunner;

public class UserAgent {
	public static Logger logger = Logger.getLogger(UserAgent.class);
	
	private List<Integer> executionHistory;
	
	private double balance;
	private double newOrder;

	/**
	 * Create a new user agent
	 * 
	 * @param demandProfile
	 * 			Index of the demand profile in the World arrayList
	 */
	public UserAgent(int demandProfile) {
		this.executionHistory = new ArrayList<Integer>();
		this.balance = 0;
		this.newOrder = 0.0;
		
		for (int i = 0; i < BrokerModuleRunner.getInstance().getSimulationLength(); i++) {
			executionHistory.add(0);
		}
	}
	
	/**
	 * User records market data but does not act upon it.
	 * This gives user chance to learn before submitting probabilities.
	 */
	public void learn() {
		executionHistory.set(BrokerModuleRunner.getInstance().getCurrentMonth(), 1);
	}

	/**
	 * Fetch list of reservations (ie probabilities of option requirement)
	 */
	public double getReservation() {
		List<Integer> tempList = new ArrayList<Integer>();
                
        //Look through demand history and store in tempList
		//Starting from now to 0, move back in 12month increments until no longer possible
		for (int p = BrokerModuleRunner.getInstance().getCurrentMonth(); p > 0; p -= 12) {
        	tempList.add(executionHistory.get(p));
        }
        
    	double avg = 0.0;
    	for (Integer i : tempList) {
    		avg += i;
    	}
    	newOrder = avg / (double)tempList.size();
        
		return newOrder;
	}
	
	/**
	 * Get a list of the options the user wishes to execute
	 * @return List<Double>
	 */
	public double getOrder() {
		executionHistory.set(BrokerModuleRunner.getInstance().getCurrentMonth() + 1, 1);
		return newOrder;
	}
	
	public double debit(double fee) {
		balance -= fee;
		return balance;
	}
	
	public double credit(double fee) {
		balance += fee;
		return balance;
	}

	public int getLatestExecution() {
		return executionHistory.get(BrokerModuleRunner.getInstance().getCurrentMonth());
	}
}

