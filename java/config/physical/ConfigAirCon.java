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

public class ConfigAirCon extends Element implements ConfigBlock
{
	private static final long serialVersionUID = 9101935331602049369L;
	private Point mLocation;

	public static final Point DEFAULT_LOCATION = new Point(0,0);

	public ConfigAirCon()
	{
		mLocation = DEFAULT_LOCATION;
	}
	
	public String getQualifiedName()
	{
		return "aisle";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}
	public Point getLocation()
	{
		return mLocation;
	}

	public void setLocation(Point mLocation)
	{
		this.mLocation = mLocation;
	}
	/**
	 * This is an overload of an XML org.jdom.Element method and DOES NOT RETURN
	 * THE AIR CON'S NAME. Mostly because Air Con units don't have names.
	 */
	public String getName(){
		return "airConditioner";
	}
	
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("position", String.valueOf(mLocation.x) + " " + String.valueOf(mLocation.y)));
		return list;
	}
	
}
