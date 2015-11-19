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

public class Quote {
	private int dcID;
	private int instances;
	
	private PriceType priceType;
	private double upFrontCost;
	private double totalMonthlyCost;
	private int duration;
	
	public Quote() {
		this.dcID = -1;
		this.priceType = PriceType.NONE;
		this.upFrontCost = 0.0;
		this.totalMonthlyCost = 0.0;
		this.duration = 0;
	}
	
	public Quote(int dcID, int instances, double upFrontCost, double totalMonthlyCost, int duration, PriceType priceType) {
		this.dcID = dcID;
		this.instances = instances;
		this.upFrontCost = upFrontCost;
		this.totalMonthlyCost = totalMonthlyCost;
		this.priceType = priceType;
		this.duration = duration;
	}

	public int getDcID() {
		return dcID;
	}

	public void setDcID(int dcID) {
		this.dcID = dcID;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public double getUpFrontCost() {
		return upFrontCost;
	}

	public void setUpFrontCost(double upFrontCost) {
		this.upFrontCost = upFrontCost;
	}
	
	public double getMonthlyCostPerInstance() {
		return totalMonthlyCost / instances;
	}

	public double getTotalMonthlyCost() {
		return totalMonthlyCost;
	}

	public void setTotalMonthlyCost(double monthlyCost) {
		this.totalMonthlyCost = monthlyCost;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Quote [dcID=" + dcID + ", instances=" + instances
				+ ", priceType=" + priceType + ", upFrontCost=" + upFrontCost
				+ ", totalMonthlyCost=" + totalMonthlyCost + ", duration="
				+ duration + "]";
	}

}
