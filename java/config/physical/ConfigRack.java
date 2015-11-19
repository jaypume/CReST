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

import sim.physical.bo.ServerWontFitException;
import utility.direction.CompassDirection;

public class ConfigRack extends Element implements ConfigBlock
{

	public static Logger logger = Logger.getLogger(ConfigRack.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mName;
	private int mCount;
	private int mNetworkDistance;
	private int mLocation;
	private int mMaxSize;
	private long mMeanFailTime;

	private List<ConfigServer> servers = new ArrayList<ConfigServer>();

	private CompassDirection serversDirection = CompassDirection.NORTH; //Default 
	
	public static final String DEFAULT_NAME = "Rack";
	public static final int DEFAULT_COUNT = 1;
	public static final int DEFAULT_NETWORK_DISTANCE = 10;
	public static final int DEFAULT_MAX_SIZE = 42; //TODO - we have made this 40 from 42 to be a round number
	public static final int DEFAULT_LOCATION = 0;
	public static final long DEFAULT_MEAN_FAILTIME = -1;

	public ConfigRack()
	{
		mName = DEFAULT_NAME;
		mCount = DEFAULT_COUNT;
		mNetworkDistance = DEFAULT_NETWORK_DISTANCE;
		mMaxSize = DEFAULT_MAX_SIZE;
		mLocation = DEFAULT_LOCATION;
		mMeanFailTime = DEFAULT_MEAN_FAILTIME;
		serversDirection = CompassDirection.NORTH; //default
	}
	/**
	 * Duplicate this ConfigRack
	 * 
	 * @param pDeep whether to duplicate servers contained within the rack
	 * @return a duplicate
	 */
	public ConfigRack duplicate(boolean pDeep)
	{
		ConfigRack newRack = new ConfigRack();
		
		// Set Values
		//newRack.setName(this.getRackName());
		newRack.setRackName(this.getRackName());
		newRack.setNetworkDistance(this.getNetworkDistance());
		newRack.setLocation(this.getLocation());
		newRack.setMaxSize(this.getMaxSize());
		newRack.setServersDirection(this.getServersDirection());
		
		// set CPU's
		if(pDeep)
		{
			for(ConfigServer c : this.servers)
			{
				try
				{
					newRack.addServer(c.duplicate());
				} catch (ServerWontFitException e)
				{
					System.err.println("Error: Rack invalid.");
				}
			}
		}
		
		return newRack;
	}
	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("name", mName));
		list.add(new Attribute("networkDistance", String
				.valueOf(mNetworkDistance)));
		list.add(new Attribute("count", String.valueOf(mCount)));
		list.add(new Attribute("position", String.valueOf(mLocation)));
		list.add(new Attribute("serversDirection", serversDirection.getNameString()));
		return list;
	}

	public List<Element> getChildren()
	{
		List<Element> list = new ArrayList<Element>();
		list.addAll(servers);
		return list;
	}
	public List<?> getContent()
	{
		return getChildren();
	}
	public String getQualifiedName()
	{
		return "rack";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}
	public void addServer(ConfigServer pServer) throws ServerWontFitException
	{
		// check ammount
		if (getTotalServerHeight() + pServer.getSize() > mMaxSize)
		{
			throw new ServerWontFitException();
		}
		servers.add(pServer);
	}
	public int getTotalServerHeight()
	{
		int h = 0;
		for (ConfigServer s : servers)
		{
			h += s.getSize();
		}
		return h;
	}
	public void removeServer(ConfigServer pServer)
	{
		servers.remove(pServer);
	}

	public ConfigServer[] getServers()
	{
		return servers.toArray(new ConfigServer[servers.size()]);
	}
	/**
	 * This is an overload of an XML org.jdom.Element method and DOES NOT RETURN
	 * THE RACKS' NAME. For that you want getRackName();
	 */
	public String getName()
	{
		return "rack";
	}
	public String getRackName()
	{
		return mName;
	}

	public Element setRackName(String mName)
	{
		this.mName = mName;
		return this;
	}

	public int getCount()
	{
		return mCount;
	}

	public void setCount(int mCount)
	{
		this.mCount = mCount;
	}

	public int getNetworkDistance()
	{
		return mNetworkDistance;
	}

	public void setNetworkDistance(int mNetworkDistance)
	{
		this.mNetworkDistance = mNetworkDistance;
	}

	public int getLocation()
	{
		return mLocation;
	}

	public void setLocation(int mLocation)
	{
		this.mLocation = mLocation;
	}
	
	public int getMaxSize()
	{
		return mMaxSize;
	}
	
	public void setMaxSize(int mMaxSize)
	{
		this.mMaxSize = mMaxSize;
	}
	
	public long getMeanFailTime()
	{
		return mMeanFailTime;
	}
	
	public void setMeanFailTime(long pMeanFailTime)
	{
		this.mMeanFailTime = pMeanFailTime;
	}
	
	public String[] getServerModels()
	{
		String[] names = new String[servers.size()];

		for (int i = 0; i < servers.size(); i++)
		{
			names[i] = servers.get(i).getModel();
		}

		return names;
	}
	
	public int getID(ConfigServer ID)
	{
		int index = -1;
		for (int i = 0; i < servers.size(); i++)
		{
			if (servers.get(i).equals(ID))
			{
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Sets the front direction of all contained servers
	 * @param direction
	 */
	public void setServersDirection(CompassDirection direction)
	{
		serversDirection = direction;
		logger.debug("Setting rack direction: " + direction);
		for(ConfigServer server : servers)
		{
			server.setDirection(direction);
		}
	}
	
	public CompassDirection getServersDirection() {
		return serversDirection;
	}
}
