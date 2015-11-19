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
/**
 * Determines the total cost for running the datacentre
 * @author Alex Sheppard
 */

package sim.module.costs.bo;

import org.apache.log4j.Logger;

import sim.physical.Datacentre;
import utility.time.LengthOfTime;
import utility.time.TimeManager;

public class Costs
{
	public static Logger logger = Logger.getLogger(Costs.class);
	
	private Datacentre mDatacentre;

	private double mSuppliedPower; // Power supplied to the datacenter (Watts)
	private double mPowerUsed; // Power actually used (Watts)
	private double mMaintenanceCostPowerSystem; // Cost of maintaining the power system
	private double mGridPowerCost; // Cost of power from the grid (per MWh or W)
	private double mMaintenanceCostCoolingSystem; // Cost of maintaining the cooling system
	
	private double mEmployeesPerRack ; // Number of employees to maintain/administer a rack
	private double mCostOfFullyLoadedEmployees; // Cost of employees
	
//	private double mPurchaseCost; // Cost to purchase the hardware
	private int mHardwareLifetime; // Lifetime of the hardware in months
	
//	private double mTotalLicenseCost; // Total cost of software licenses in the datacenter
	private int mNumRacks; // Number of racks in the datacenter
	
	private double mRent; //Rent per m2 of floorspace

	/**
	 * Creates a pricing object for the input datacentre and sets default values
	 * 
	 * @param pDatacentre the datacentre requiring a pricing object
	 */
	public Costs (Datacentre pDatacentre)
	{
		this.mDatacentre = pDatacentre;
	}

	/** Cost per sq m floorspace (height not included)
	 * 
	 * @param i length of time
	 * @return cost for the physical space of the datacenter
	 */
	public double PhysicalSpaceCost(double i)
	{	
		double space;
		
		logger.debug("Width: " + mDatacentre.getDatacentreDimensions()[0] + ", Length: " + mDatacentre.getDatacentreDimensions()[1]);
		space = mRent * mDatacentre.getDatacentreDimensions()[0] *  mDatacentre.getDatacentreDimensions()[1];
		
		return (i/LengthOfTime.MONTH.getTimeInSeconds()) * space;
	}

	/** Total cost for the power system for the datacenter
	 * 
	 * @param i length of time
	 * @return cost for the power system running the datacenter
	 */
	public double HardwarePowerCost(double i)
	{
		mPowerUsed = mDatacentre.getPower();
		
		double J1 = mSuppliedPower / mPowerUsed; // Capacity utilisation

		double K1 = J1 * mMaintenanceCostPowerSystem / mGridPowerCost; // Maintenance costs of the power system

		return (i/LengthOfTime.MONTH.getTimeInSeconds()) * (1 + K1) * mGridPowerCost  * mPowerUsed; 
	}

	/** Total cost for the cooling system of the datacenter
	 * 
	 * @param i length of time
	 * @return cost for the cooling of the datacenter
	 */
	public double CoolingPowerCost(double i)
	{
		mPowerUsed = mDatacentre.getPower();
		
		double L1 = 0.8; // Cooling load factor
		
		double J1 = mSuppliedPower / mPowerUsed; // Capacity utilisation
		
		double K2 = J1 * mMaintenanceCostCoolingSystem / mGridPowerCost;

		return (i/LengthOfTime.MONTH.getTimeInSeconds()) * (1 + K2) * L1 * mGridPowerCost  * mPowerUsed;
	}

	/** Total cost for administration (based on the number of racks 1 employee can maintain)
	 * 
	 * @param i length of time
	 * @return cost for administration
	 */
	public double PersonnelCost(double i)
	{
		return (i/LengthOfTime.MONTH.getTimeInSeconds()) * mEmployeesPerRack * mCostOfFullyLoadedEmployees * mDatacentre.getNumRacks();
	}

	/** 
	 * Depreciation of the hardware per month
	 * 
	 * @param timeInSeconds - time in seconds
	 * 
	 * @return Depreciation of the hardware per month
	 */
	public double HardwareDepreciationCost(double timeInSeconds)
	{
		return (timeInSeconds/LengthOfTime.MONTH.getTimeInSeconds()) * getPurchaseCost() / mHardwareLifetime;
	}
	
    /**
     * Calculates the purchase cost of the servers in the data centre
     * 
     * @return purchase costs
     */
    public double getPurchaseCost()
    {
        double purchaseCost = 0;

        for (int i = 0; i < mDatacentre.getNumAisles(); i++)
        {
            double aislePurchaseCost = mDatacentre.getBlocks()[i].getPurchaserCost();
            logger.debug("Aisle purchase cost  " + i + ": £ " + aislePurchaseCost + "\n--------------");
            purchaseCost += aislePurchaseCost;
        }
        logger.debug("Purchase Cost of Servers: £ " + purchaseCost);

        return purchaseCost;
    }

	/** Total cost of the software licenses for the whole datacentre
	 * 
	 * @return cost of the software licenses for the whole datacentre
	 */
	public double getSoftwareLicenseCost()
	{
        double softwareCost = 0;

        for (int i = 0; i < mDatacentre.getNumAisles(); i++)
        {
            double aisleSoftwareCost = mDatacentre.getBlocks()[i].getSoftwareCost();
            logger.debug("Aisle software cost  " + i + ": £ " + aisleSoftwareCost + "\n--------------");
            softwareCost += aisleSoftwareCost;
        }
        logger.debug("Purchase Cost of Software: £ " + softwareCost);

        return softwareCost;
    }

	/**Grand Total cost of operations and runnings of the datacenter
	 * 
	 * @param i length of time
	 * @return Grand Total cost of operations and runnings of the datacenter
	 */
	public double totalCost(double i)
	{
		//TODO: Quick fix JC: Dec 2011 Removed demand multiplication .... does this fix demand?  Probably not! Check this.
		logger.debug(TimeManager.log("Total cost = " + PhysicalSpaceCost(i) + HardwarePowerCost(i) + CoolingPowerCost(i) + getSoftwareLicenseCost() + mNumRacks* (PersonnelCost(i) + HardwareDepreciationCost(i))));

		return ( PhysicalSpaceCost(i) + HardwarePowerCost(i) + CoolingPowerCost(i) + getSoftwareLicenseCost() + mNumRacks* (PersonnelCost(i) + HardwareDepreciationCost(i)));
		//		return demands()*( PhysicalSpaceCost(i) + HardwarePowerCost(i) + CoolingPowerCost(i) + LicenseCost() + mNumRacks* (PersonnelCost(i) + HardwareDepreciationCost(i)));
		//return demands()*( PhysicalSpaceCost(i) + HardwarePowerCost(i) + CoolingPowerCost(i) + mNumRacks* (PersonnelCost(i) + HardwareDepreciationCost(i) + LicenseCost()));
	}

	/**
	 * @param  pSuppliedPower the mSuppliedPower to set
	 */
	public void setmSuppliedPower(double pSuppliedPower)
	{
		mSuppliedPower = pSuppliedPower;
	}

	/**
	 * @param pMaintenanceCostPowerSystem the mMaintenanceCostPowerSystem to set
	 */
	public void setmMaintenanceCostPowerSystem(double pMaintenanceCostPowerSystem)
	{
		mMaintenanceCostPowerSystem = pMaintenanceCostPowerSystem;
	}

	/**
	 * @param pGridPowerCost the mGridPowerCost to set
	 */
	public void setmGridPowerCost(double pGridPowerCost)
	{
		mGridPowerCost = pGridPowerCost;
	}

	/**
	 * @param pMaintenanceCostCoolingSystem the mMaintenanceCostCoolingSystem to set
	 */
	public void setmMaintenanceCostCoolingSystem(double pMaintenanceCostCoolingSystem)
	{
		mMaintenanceCostCoolingSystem = pMaintenanceCostCoolingSystem;
	}

	/**
	 * @param pEmployeesPerRack the mEmployeesPerRack to set
	 */
	public void setmEmployeesPerRack(double pEmployeesPerRack)
	{
		mEmployeesPerRack = pEmployeesPerRack;
	}

	/**
	 * @param pCostOfFullyLoadedEmployees the mCostOfFullyLoadedEmployees to set
	 */
	public void setmCostOfFullyLoadedEmployees(double pCostOfFullyLoadedEmployees)
	{
		mCostOfFullyLoadedEmployees = pCostOfFullyLoadedEmployees;
	}

	/**
	 * @param pHardwareLifetime the mHardwareLifetime to set
	 */
	public void setmHardwareLifetime(int pHardwareLifetime)
	{
		mHardwareLifetime = pHardwareLifetime;
	}

	/**
	 * @param pRent the mRent to set
	 */
	public void setmRent(double pRent)
	{
		mRent = pRent;
	}
}