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

public class ConfigServerType extends org.jdom.Element
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mModel;
	private int mSize;
	private ConfigRAM mRAM;
	private long mMeanFailTime;
	private long mTimeAvailableFrom;

	private List<ConfigCPU> cpus = new ArrayList<ConfigCPU>();

	public static final String DEFAULT_MODEL = "Default Server";
	public static final int DEFAULT_SIZE = 2;
	public static final long DEFAULT_MEAN_FAIL_TIME = 200;
	public static final long DEFAULT_TIME_AVAILABLE_FROM = 0;

	public ConfigServerType()
	{
		mModel = DEFAULT_MODEL;
		mSize = DEFAULT_SIZE;
		mRAM = new ConfigRAM();
		mMeanFailTime = DEFAULT_MEAN_FAIL_TIME;
		mTimeAvailableFrom = DEFAULT_TIME_AVAILABLE_FROM;
	}

	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("model", mModel));
		list.add(new Attribute("size", String.valueOf(mSize)));
		list.add(new Attribute("probMean", String.valueOf(mMeanFailTime)));
		list.add(new Attribute("availableFrom", String.valueOf(mTimeAvailableFrom)));
		return list;
	}
	
	public List<Element> getChildren()
	{
		List<Element> list = new ArrayList<Element>();
		list.addAll(cpus);
		list.add(mRAM);
		return list;
	}
	public List<?> getContent()
	{
		return getChildren();
	}
	public String getQualifiedName()
	{
		return "server";
	}
	public String getName()
	{
		return "server";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}
	
	/**
	 * Duplicates the ConfigServerType
	 * 
	 * @return A deep copy duplicate
	 */
	public ConfigServerType duplicate()
	{
		ConfigServerType newServer = new ConfigServerType();
		
		// Set Values
		newServer.setModel(this.getModel());
		newServer.setMeanFailTime(this.getMeanFailTime());
		newServer.setSize(this.getSize());
		newServer.setRAM(this.getRAM().duplicate());
		
		// set CPU's
		for(ConfigCPU c : cpus)
		{
			newServer.addCPU(c.duplicate());
		}
		
		return newServer;
	}
	public void addCPU(ConfigCPU pCPU)
	{
		cpus.add(pCPU);
	}

	public void removeCPU(ConfigCPU pCPU)
	{
		cpus.remove(pCPU);
	}

	public ConfigCPU[] getCPUs()
	{
		return cpus.toArray(new ConfigCPU[cpus.size()]);
	}

	public String getModel()
	{
		return mModel;
	}

	public void setModel(String pName)
	{
		this.mModel = pName;
	}

	public int getSize()
	{
		return mSize;
	}

	public void setSize(int pSize)
	{
		this.mSize = pSize;
	}

	public ConfigRAM getRAM()
	{
		if (mRAM == null)
		{
			mRAM = new ConfigRAM();
		}
		return mRAM;
	}

	public void setRAM(ConfigRAM pRAM)
	{
		this.mRAM = pRAM;
	}
	
	public long getMeanFailTime()
	{
		return mMeanFailTime;
	}
	
	public void setMeanFailTime(final long pMeanFailTimeInDays)
	{
		mMeanFailTime = pMeanFailTimeInDays;
	}
	
	public long getTimeAvailableFrom()
	{
		return mTimeAvailableFrom;
	}
	
	public void setTimeAvailableFrom(final long pTimeAvailableFrom)
	{
		mTimeAvailableFrom = pTimeAvailableFrom;
	}

	public String[] getCPUModels()
	{
		String[] names = new String[cpus.size()];
		
		for (int i=0;i<cpus.size();i++)
		{
			names[i] = cpus.get(i).getModel();
		}
		
		return names;
	}
	public int getID(ConfigCPU ID)
	{
		int index = -1;
		for (int i = 0; i < cpus.size(); i++)
		{
			if (cpus.get(i).equals(ID))
			{
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Returns a ConfigServer object from this ConfigServerType instance
	 * @return a ConfigServer object
	 */
	public ConfigServer getConfigServerObject()
	{
		ConfigServer newServer = new ConfigServer();
		
		// Set Values
		newServer.setModel(this.getModel());
		newServer.setMeanFailTimeInDays(this.getMeanFailTime());
		newServer.setSize(this.getSize());
		newServer.setRAM(this.getRAM().duplicate());
		
		// set CPU's
		for(ConfigCPU c : cpus)
		{
			newServer.addCPU(c.duplicate());
		}
		
		return newServer;
	}
}
