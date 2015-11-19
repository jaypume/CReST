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
package sim.market.Instance;

import org.apache.log4j.Logger;

import sim.market.log.ProviderLogger;

public class InstanceProvider {

	public static Logger logger = Logger.getLogger(InstanceProvider.class);

	protected static InstanceProvider singleton;
	
	protected double onDemandPrice;
	protected double reservedInstancePrice;
	protected int reservedInstanceTermInMonths;
	protected double commissionRate;
	
	protected int onDemandUnitsSold;
	protected int reservedInstanceUnitsSold;
	protected double commission;
	protected double sales;
	
	
	protected InstanceProvider(double onDemandInstancePrice, double reservedInstancePrice, int reservedInstanceTermInMonths, double commissionRate) {
		this.reservedInstanceTermInMonths = reservedInstanceTermInMonths;
		this.onDemandPrice = onDemandInstancePrice;
		this.reservedInstancePrice = reservedInstancePrice;
		this.commissionRate = commissionRate;
		
		this.onDemandUnitsSold = 0;
		this.reservedInstanceUnitsSold = 0;
		this.commission = 0;
		this.sales = 0;
	}
	
	public static InstanceProvider getSingleton() {	
			return InstanceProvider.singleton;
	}
	
	/**
	 * Create a new cloud provider that sells cloud instances
	 * @param onDemandInstancePrice - the price of an on demand instance
	 * @param reservedInstancePrice - the price of a reserved instance
	 * @param reservedInstanceTermInMonths - the term (in months) of a reserved instance
	 * @param commissionRate - the commission rate the provider charges for sales on the secondary market
	 */
	public static void createInstanceProvider(double onDemandInstancePrice, double reservedInstancePrice, int reservedInstanceTermInMonths, double commissionRate) {
		singleton = new InstanceProvider(onDemandInstancePrice, reservedInstancePrice, reservedInstanceTermInMonths, commissionRate);
	}
	
	public double getOnDemandPrice() {
		return onDemandPrice;
	}
	
	public Instance getOnDemandInstance() {
		sales += onDemandPrice;
		onDemandUnitsSold++;
		
		ProviderLogger.getSingleton().addODSale(onDemandPrice);
		return new OnDemandInstance("On Demand", 1,  onDemandPrice);
	}
	
	public double getReservedInstancePrice() {
		return reservedInstancePrice;
	}
	
	public Instance getReservedInstance() {
		sales += reservedInstancePrice;
		reservedInstanceUnitsSold++;
		
		ProviderLogger.getSingleton().addRISale(reservedInstancePrice);
		return new ReservedInstance("Reserved", reservedInstanceTermInMonths, reservedInstancePrice);
	}

	public void payCommission(double comms) {
		logger.info("Provider receiving commission=$" + comms);
		commission+=comms;
		
		ProviderLogger.getSingleton().addCommission(comms);
	}
	
	public double getCommissionRate() {
		return commissionRate;
	}
	
	public double getBalance() {
		return commission+sales;
	}
	
	public int getReservedInstancesSold() {
		return reservedInstanceUnitsSold;
	}
	
	public int getOnDemandInstancesSold() {
		return onDemandUnitsSold;
	}
	
	@Override
	public String toString() {
		return "\nInstanceProvider: " 
				+ "\nAccounts: [balance=$" + getBalance() + ", comms=$" + commission + ", sales=$" + sales
				+ ", onDemandSales=" + onDemandUnitsSold + ", reservedInstanceSales=" + reservedInstanceUnitsSold +"]"
				+ "\nPrice List: [onDemandPrice=" + onDemandPrice
				+ ", reservedInstancePrice=" + reservedInstancePrice
				+ ", reservedInstanceTermInMonths=" + reservedInstanceTermInMonths 
				+ ", commissionRate=" + commissionRate + "]";
	}	
}
