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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public class ConfigAisle extends Element implements ConfigBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mName;
	private int mCount;
	private int mNetworkDistance;
	private Point mLocation;
	private Point mDimensions;
	
	private List<ConfigRack> racks = new ArrayList<ConfigRack>();
	private List<ConfigAirCon> cooling = new ArrayList<ConfigAirCon>();
	
	public static final String DEFAULT_NAME = "Aisle";
	public static final int DEFAULT_COUNT = 1;
	public static final int DEFAULT_NETWORK_DISTANCE = 10;
	public static final Point DEFAULT_LOCATION = new Point(0,0);
	public static final Point DEFAULT_DIMENSIONS = new Point(1, 10);

	public ConfigAisle()
	{
		mName = DEFAULT_NAME;
		mCount = DEFAULT_COUNT;
		mNetworkDistance = DEFAULT_NETWORK_DISTANCE;
		mLocation = DEFAULT_LOCATION;
		mDimensions = DEFAULT_DIMENSIONS;
		
		// TODO: Make dimensions and position be set from config file.
	}
	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("name", mName));
		list.add(new Attribute("networkDistance", String.valueOf(mNetworkDistance)));
		list.add(new Attribute("count", String.valueOf(mCount)));
		list.add(new Attribute("position", String.valueOf(mLocation.x) + " " + String.valueOf(mLocation.y)));
		return list;
	}
	
	public List<Element> getChildren()
	{
		List<Element> list = new ArrayList<Element>();
		list.addAll(racks);
		list.addAll(cooling);
		return list;
	}
	public List<?> getContent()
	{
		return getChildren();
	}
	public String getQualifiedName()
	{
		return "aisle";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}
	public void addRack(ConfigRack pRack)
	{
		racks.add(pRack);
	}
	public void removeRack(ConfigRack pRack)
	{
		racks.remove(pRack);
	}
	public ConfigRack[] getRacks()
	{
		return racks.toArray(new ConfigRack[racks.size()]);
	}
	public void addAirCon(ConfigAirCon pAC)
	{
		cooling.add(pAC);
	}
	public void removeAirCon(ConfigAirCon pAC)
	{
		cooling.remove(pAC);
	}
	public ConfigAirCon[] getAirCons()
	{
		return cooling.toArray(new ConfigAirCon[cooling.size()]);
	}
	/**
	 * This is an overload of an XML org.jdom.Element method and DOES NOT RETURN
	 * THE AISLE'S NAME. For that you want getAisleName();
	 */
	public String getName(){
		return "aisle";
	}
	public String getAisleName() {
		return mName;
	}

	public void setAisleName(String mName) {
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
	public String[] getRackNames()
	{
		String[] names = new String[racks.size()];
		
		for (int i=0;i<racks.size();i++)
		{
			names[i] = racks.get(i).getRackName();
		}
		
		return names;
	}
	public int getID(ConfigRack ID)
	{
		int index = -1;
		for (int i = 0; i < racks.size(); i++)
		{
			if (racks.get(i).equals(ID))
			{
				index = i;
			}
		}
		return index;
	}
	public Point getLocation()
	{
		return mLocation;
	}
	public void setLocation(Point pLocation)
	{
		mLocation = pLocation;
	}
	public void setDimensions(Point pDimensions)
	{
	    mDimensions = pDimensions;
	}
	public Point getDimensions()
	{
	    return mDimensions;
	}
}
