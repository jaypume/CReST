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
package config.physical;

import builder.datacentre.LayoutMethod;
import builder.datacentre.layout.AbstractLayoutPattern;
import builder.datacentre.layout.LayoutPatternFactory;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to be used in the configuration builder tool ONLY. It's a large
 * dump of information that can be sent to a Saver object and saved.
 * 
 * @author James Laverack
 * 
 */
public class ConfigDatacentre extends Element {
	private static final long serialVersionUID = 1L;
	/*
	 * Fields
	 */
	private String mName;
	private int mCount;
	private int mNetworkDistance;
	private int mDimX;
	private int mDimY;
	private int mNumServers;
	private double mRatio;
	private LayoutMethod mLayoutMethod;
	private boolean mAircon;
	protected AbstractLayoutPattern mLayoutPattern;
	private String mTypeOfBusiness;
	private double mSuppliedPower;
	private double mGridPowerCost;
	private double mMaintenancePowerCost;
	private double mMaintenanceCoolingCost;
	private double mEmployeesPerRack;
	private double mCostofEmployees;
	private int mHardwareLifetime;
	private double mRent;
	
	/*
	 * List of Aisles in this datacentre
	 */
	private List<ConfigAisle> aisles = new ArrayList<ConfigAisle>();

	/*
	 * Default values
	 */
	public static final String DEFAULT_NAME = "Datacentre";
	public static final int DEFAULT_COUNT = 1;
	public static final int DEFAULT_NETWORK_DISTANCE = 100;
	public static final int DEFAULT_SIZE_X = 100;
	public static final int DEFAULT_SIZE_Y = 100;
	public static final int DEFAULT_NUM_SERVERS = 2000; //TODO AS 11.7.12: change value to something sensible
	public static final double DEFAULT_RATIO = 1.0;
	public static final boolean DEFAULT_AIRCON = true;
	public static final String DEFAULT_TYPE_OF_BUSINESS = "RS!: Specialist Food Stores (val nsa) All Business Index";
	public static final double DEFAULT_SUPPLIED_POWER = 10000000;
	public static final double DEFAULT_GRID_POWER_COST = 0.072;
	public static final double DEFAULT_MAINTENANCE_POWER_COST = 0.072;
	public static final double DEFAULT_MAINTENANCE_COOLING_COST = 0.072 / 2;
	public static final double DEFAULT_EMPLOYEES_PER_RACK = 1.0 / 20;
	public static final double DEFAULT_COST_OF_EMPLOYEES = 10000;
	public static final int DEFAULT_HARDWARE_LIFETIME = 36;
	public static final double DEFAULT_RENT = 50;

	/**
	 * Apply defaults
	 */
	public ConfigDatacentre() {
		mName = DEFAULT_NAME;
		mCount = DEFAULT_COUNT;
		mNetworkDistance = DEFAULT_NETWORK_DISTANCE;
		mDimX = DEFAULT_SIZE_X;
		mDimY = DEFAULT_SIZE_Y;
		mNumServers = DEFAULT_NUM_SERVERS;
		mRatio = DEFAULT_RATIO;
		mLayoutMethod = LayoutMethod.getDefault();
		mAircon = DEFAULT_AIRCON;
		mLayoutPattern = LayoutPatternFactory.getLayoutPattern(LayoutPatternType.getDefault());
		mTypeOfBusiness = DEFAULT_TYPE_OF_BUSINESS;
		mSuppliedPower = DEFAULT_SUPPLIED_POWER;
		mGridPowerCost = DEFAULT_GRID_POWER_COST;
		mMaintenancePowerCost = DEFAULT_MAINTENANCE_POWER_COST;
		mMaintenanceCoolingCost = DEFAULT_MAINTENANCE_COOLING_COST;
		mEmployeesPerRack = DEFAULT_EMPLOYEES_PER_RACK;
		mCostofEmployees = DEFAULT_COST_OF_EMPLOYEES;
		mHardwareLifetime = DEFAULT_HARDWARE_LIFETIME;
		mRent = DEFAULT_RENT;
	}

	@Override
	public List<Attribute> getAttributes() {
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("name", mName));
		list.add(new Attribute("networkDistance", String
				.valueOf(mNetworkDistance)));
		list.add(new Attribute("count", String.valueOf(mCount)));
		
		//View and Edit values
		list.add(new Attribute("dimensions", String.valueOf(mDimX) + " " + String.valueOf(mDimY)));
		list.add(new Attribute("numservers", String.valueOf(mNumServers) + " " + String.valueOf(mRatio)));
		list.add(new Attribute("layoutmethod", mLayoutMethod.getNameString()));
		list.add(new Attribute("aircon", String.valueOf(mAircon)));
		list.add(new Attribute("layoutpattern", mLayoutPattern.getLayoutPatternType().getNameString()));
		
		// Pricing values
		list.add(new Attribute("pricing.typeOfBusiness", String
				.valueOf(mTypeOfBusiness)));
		list.add(new Attribute("pricing.suppliedPower", String
				.valueOf(mSuppliedPower)));
		list.add(new Attribute("pricing.gridPowerCost", String
				.valueOf(mGridPowerCost)));
		list.add(new Attribute("pricing.maintenancePowerCost", String
				.valueOf(mMaintenancePowerCost)));
		list.add(new Attribute("pricing.maintenanceCoolingCost", String
				.valueOf(mMaintenanceCoolingCost)));
		list.add(new Attribute("pricing.employeesPerRack", String
				.valueOf(mEmployeesPerRack)));
		list.add(new Attribute("pricing.costOfEmployees", String
				.valueOf(mCostofEmployees)));
		list.add(new Attribute("pricing.hardwareLifetime", String
				.valueOf(mHardwareLifetime)));
		list.add(new Attribute("pricing.rent", String.valueOf(mRent)));

		return list;
	}

	public List<Element> getChildren() {
		List<Element> list = new ArrayList<Element>();
		list.addAll(aisles);
		return list;
	}

	public List<?> getContent() {
		return getChildren();
	}

	public String getQualifiedName() {
		return "datacentre";
	}

	public Namespace getNamespace() {
		return Namespace.NO_NAMESPACE;
	}

	public void addAisle(ConfigAisle pAisle) {
		aisles.add(pAisle);
	}

	public void removeAisle(ConfigAisle pAisle) {
		aisles.remove(pAisle);
	}

	public ConfigAisle[] getAisles() {
		return aisles.toArray(new ConfigAisle[aisles.size()]);
	}

	public String getDatacentreName() {
		return mName;
	}

	/**
	 * This is an overload of an XML org.jdom.Element method and DOES NOT RETURN
	 * THE DATACENTRE'S NAME. For that you want getDatacentreName();
	 */
	public String getName() {
		return "datacentre";
	}

	public void setDatacentreName(String mName) {
		this.mName = mName;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int mCount) {
		this.mCount = mCount;
	}

	public int getNetworkDistance() {
		return mNetworkDistance;
	}

	public void setNetworkDistance(int mNetworkDistance) {
		this.mNetworkDistance = mNetworkDistance;
	}

	public int getDimX() {
		return mDimX;
	}

	public void setDimX(int mDimX) {
		this.mDimX = mDimX;
	}

	public int getDimY() {
		return mDimY;
	}

	public void setDimY(int mDimY) {
		this.mDimY = mDimY;
	}
	
	public int getNumServers() {
		return mNumServers;
	}

	public void setNumServers(int mNumServers) {
		this.mNumServers = mNumServers;
	}
	
	public double getRatio() {
		return mRatio;
	}

	public void setRatio(double mRatio) {
		this.mRatio = mRatio;
	}
	
	public LayoutMethod getLayoutMethod() {
		return mLayoutMethod;
	}

	public void setLayoutMethod(LayoutMethod mLayoutMethod) {
		this.mLayoutMethod = mLayoutMethod;
	}
	
	public boolean getAircon() {
		return mAircon;
	}

	public void setAircon(boolean mAircon) {
		this.mAircon = mAircon;
	}
	
	public AbstractLayoutPattern getLayoutPattern() {
		return mLayoutPattern;
	}

	public void setLayoutPattern(AbstractLayoutPattern abstractLayoutPattern) {
		this.mLayoutPattern = abstractLayoutPattern;
	}

	public void setTypeOfBusiness(String mTypeOfBusiness) {
		this.mTypeOfBusiness = mTypeOfBusiness;
	}

	public String getTypeOfBusiness() {
		return mTypeOfBusiness;
	}

	public void setSuppliedPower(double mTxtSuppliedPower) {
		this.mSuppliedPower = mTxtSuppliedPower;
	}

	public double getSuppliedPower() {
		return mSuppliedPower;
	}

	public void setGridPowerCost(double mTxtGridPowerCost) {
		this.mGridPowerCost = mTxtGridPowerCost;
	}

	public double getGridPowerCost() {
		return mGridPowerCost;
	}

	public void setMaintenancePowerCost(double mTxtMaintenancePowerCost) {
		this.mMaintenancePowerCost = mTxtMaintenancePowerCost;
	}

	public double getMaintenancePowerCost() {
		return mMaintenancePowerCost;
	}

	public void setMaintenanceCoolingCost(double mTxtMaintenanceCoolingCost) {
		this.mMaintenanceCoolingCost = mTxtMaintenanceCoolingCost;
	}

	public double getMaintenanceCoolingCost() {
		return mMaintenanceCoolingCost;
	}

	public void setEmployeesPerRack(double mTxtEmployeesPerRack) {
		this.mEmployeesPerRack = mTxtEmployeesPerRack;
	}

	public double getEmployeesPerRack() {
		return mEmployeesPerRack;
	}

	public void setCostofEmployees(double mTxtCostofEmployees) {
		this.mCostofEmployees = mTxtCostofEmployees;
	}

	public double getCostofEmployees() {
		return mCostofEmployees;
	}

	public void setHardwareLifetime(int mTxtHardwareLifetime) {
		this.mHardwareLifetime = mTxtHardwareLifetime;
	}

	public int getHardwareLifetime() {
		return mHardwareLifetime;
	}

	public void setRent(double mTxtRent) {
		this.mRent = mTxtRent;
	}

	public double getRent() {
		return mRent;
	}

	public String[] getAisleNames() {
		String[] names = new String[aisles.size()];

		for (int i = 0; i < aisles.size(); i++) {
			names[i] = aisles.get(i).getAisleName();
		}

		return names;
	}

	public int getID(ConfigAisle ID) {
		int index = -1;
		for (int i = 0; i < aisles.size(); i++) {
			if (aisles.get(i).equals(ID)) {
				index = i;
			}
		}
		return index;
	}

}
