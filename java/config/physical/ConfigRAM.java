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

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

public class ConfigRAM extends Element
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mModel;
	private double mSpeed; // MHz
	private int mSize; // MB

	public static final String DEFAULT_MODEL = "SimRAM";
	public static final double DEFAULT_SPEED = 1333;
	public static final int DEFAULT_SIZE = 2048;

	public ConfigRAM()
	{
		mModel = DEFAULT_MODEL;
		mSpeed = DEFAULT_SPEED;
		mSize = DEFAULT_SIZE;
	}
	
	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("model", mModel));
		list.add(new Attribute("size", String.valueOf(mSize)));
		list.add(new Attribute("speed", String.valueOf(mSpeed)));
		return list;
	}
	public String getQualifiedName()
	{
		return "ram";
	}
	public String getName()
	{
		return "ram";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}
	
	public ConfigRAM duplicate()
	{
		ConfigRAM newRAM = new ConfigRAM();
		
		newRAM.setModel(this.getModel());
		newRAM.setSize(this.getSize());
		newRAM.setSpeed(this.getSpeed());
		
		return newRAM;
	}

	public String getModel()
	{
		return mModel;
	}

	public void setModel(String mModel)
	{
		this.mModel = mModel;
	}

	public double getSpeed()
	{
		return mSpeed;
	}

	public void setSpeed(double mSpeed)
	{
		this.mSpeed = mSpeed;
	}

	public int getSize()
	{
		return mSize;
	}

	public void setSize(int mSize)
	{
		this.mSize = mSize;
	}

}
