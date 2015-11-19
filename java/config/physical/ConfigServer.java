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

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import utility.direction.CompassDirection;

public class ConfigServer extends org.jdom.Element
{
	public static Logger logger = Logger.getLogger(ConfigServer.class);
	
	private static final long serialVersionUID = 1L;
	
	private String mModel;
	private int mSize;
	private CompassDirection mDirection;
	private ConfigRAM mRAM;
	private long mMeanFailTime;
	private List<ConfigCPU> cpus = new ArrayList<ConfigCPU>();

	public static final String DEFAULT_MODEL = "Default Server";
	public static final int DEFAULT_SIZE = 2;
	public static final CompassDirection DEFAULT_DIRECTION = CompassDirection.getDefault();
	public static final long DEFAULT_MEAN_FAIL_TIME = 200;

	/**
	 * Creates the default ConfigServer containing NO CPUs
	 */
	public ConfigServer()
	{
		mModel = DEFAULT_MODEL;
		mSize = DEFAULT_SIZE;
		mDirection = DEFAULT_DIRECTION;
		mRAM = new ConfigRAM();
		mMeanFailTime = DEFAULT_MEAN_FAIL_TIME;
	}
	
	/**
	 * Creates one of the three available ConfigServer objects
	 * @param i 1-3 for Cheap, Midrange, or High End server
	 */
	public ConfigServer(int i)
	{
		switch (i)
		{
			case 1:
			{
				ConfigServer1();
				break;
			}
			case 2:
			{
				ConfigServer2();
				break;
			}
			case 3:
			{
				ConfigServer3();
				break;
			}
			default:
			{
				logger.warn("Specified ConfigServer type id [" + i + "] out of bounds - creating type 1");
				ConfigServer1();
				break;
			}
		}
	}
	
	/**
	 * Cheap Server
	 */
	private void ConfigServer1()
	{
		// Server 1
		this.setModel("Cheap Server");
		this.setMeanFailTimeInDays(10);
		this.setSize(2);
		this.setDirection(CompassDirection.SOUTH);
		ConfigCPU c1a = new ConfigCPU();
		c1a.setCores(2);
		c1a.setSpeed(1400);
		c1a.setModel("Cheap CPU");
		this.addCPU(c1a);
		this.addCPU(c1a.duplicate());
		ConfigRAM r1 = new ConfigRAM();
		r1.setModel("Cheap RAM");
		r1.setSize(1024);
		r1.setSpeed(667);
		this.setRAM(r1);
	}
	
	/**
	 * Midrange Server
	 */
	private void ConfigServer2()
	{
		// Server 2
		this.setModel("Midrange Server");
		this.setMeanFailTimeInDays(100);
		this.setSize(2);
		this.setDirection(CompassDirection.SOUTH);
		ConfigCPU c2 = new ConfigCPU();
		c2.setCores(4);
		c2.setSpeed(2800);
		c2.setModel("Midrange CPU");
		this.addCPU(c2);
		this.addCPU(c2.duplicate());
		ConfigRAM r2 = new ConfigRAM();
		r2.setModel("Midrange RAM");
		r2.setSize(2048);
		r2.setSpeed(1066);
		this.setRAM(r2);
	}
	
	/**
	 * High end Server
	 */
	private void ConfigServer3()
	{
		// Server 3
		this.setModel("High-End Server");
		this.setMeanFailTimeInDays(1000);
		this.setSize(2);
		this.setDirection(CompassDirection.SOUTH);
		ConfigCPU c3 = new ConfigCPU();
		c3.setCores(6);
		c3.setSpeed(3800);
		c3.setModel("High-End CPU");
		this.addCPU(c3);
		this.addCPU(c3.duplicate());
		this.addCPU(c3.duplicate());
		this.addCPU(c3.duplicate());
		ConfigRAM r3 = new ConfigRAM();
		r3.setModel("High-End RAM");
		r3.setSize(4096);
		r3.setSpeed(1066);
		this.setRAM(r3);
	}

	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("model", mModel));
		list.add(new Attribute("size", String.valueOf(mSize)));
		list.add(new Attribute("direction", mDirection.getNameString()));
		list.add(new Attribute("probMean", String.valueOf(mMeanFailTime)));
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
	 * Duplicate this ConfigServer
	 * 
	 * @return a deep copy duplicate
	 */
	public ConfigServer duplicate()
	{
		ConfigServer newServer = new ConfigServer();
		
		// Set Values
		newServer.setModel(this.getModel());
		newServer.setMeanFailTimeInDays(this.getMeanFailTime());
		newServer.setSize(this.getSize());
		newServer.setDirection(this.getDirection());
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
	
	/**
	 * Set mean fail time
	 * 
	 * @param pMeanFailTimeInDays 
	 */
	public void setMeanFailTimeInDays(final long pMeanFailTimeInDays)
	{
		mMeanFailTime = pMeanFailTimeInDays;
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

	public CompassDirection getDirection()
	{
		return mDirection;
	}

	public void setDirection(CompassDirection mDirection)
	{
		this.mDirection = mDirection;
	}
}
