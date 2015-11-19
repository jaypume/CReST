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
 * @created 4 Jul 2011
 */
package sim.physical;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.physical.network.IP;
import utility.direction.CompassDirection;

/**
 * Class representing a physical rack of servers within a datacentre.
 */
public class Rack extends Block
{
	public static Logger logger = Logger.getLogger(Rack.class);
	
    // Constants.
	//private static final int INTRA_RACK_NETWORK_DISTANCE = 1;

	private ArrayList<Server> mServers = new ArrayList<Server>();

	// How many of the 44U spaces are used.
	private int U = 0;
	private int maxU = 42;
	private int mIntraRackNetworkDistance;
	private String mName;

    private CompassDirection mDirection = CompassDirection.NORTH; //Direction Rack faces...
    
    /**
     * Constructor.
     * 
     * @param pNetworkDistance
     * @param pName
     * @param pRelativePosition
     */
    public Rack(int pNetworkDistance, String pName, final Point pRelativePosition)
    {
        super(Type.rack, pRelativePosition, new Point(1, 1));
        mIntraRackNetworkDistance = pNetworkDistance;
        mName = pName;
    }
    
    /**
     * Get the direction the front of this server faces
     * 
     * @return the direction the front of this server faces
     */
    public CompassDirection getDirection()
	{
		return mDirection;
	}

    public void setDirection(CompassDirection direction) {
    	logger.warn("Setting rack direction = " + direction);
    	mDirection = direction;
    }
    
    /**
     * TODO
     * 
     * @return
     */
	String getName()
	{
		return mName;
	}

	/**
	 * TODO
	 * 
	 * @param mName
	 */
	void setName(String mName)
	{
		this.mName = mName;
	}

	/**
	 * Method to return useful information about this rack.
	 * 
	 * @return A StringBuffer of useful information about this rack.
	 */
	StringBuffer toStringBuffer()
	{
		StringBuffer rackInfo = new StringBuffer();

		for (int i = 0; i < getNumServers(); i++)
		{
			rackInfo.append("\n==== Server ");
			rackInfo.append(i);
			rackInfo.append(" ====\n");
			rackInfo.append(mServers.get(i).toString());
		}

		return rackInfo;
	}

	/**
	 * Method to fetch the number of servers in this rack.
	 * 
	 * @return the number of servers in this rack.
	 */
	public int getNumServers()
	{
		return mServers.size();
	}
	
	/**
	 * Method to fetch the number of CPUs in this rack.
	 * 
	 * @return the number of CPUs in this rack.
	 */
	public int getNumCPUs()
	{
		int numCPUs = 0;

		for (Server server : mServers)
		{
			numCPUs += server.getCPUs().length;
		}

		return numCPUs;
	}

	/**
	 * Added a server to this rack.
	 * 
	 * @param pServer
	 *            the server to add to the rack.
	 */
	public void addServer(Server pServer) throws Exception
	{
		if (U + pServer.getHeight() > maxU)
		{
			throw new Exception("Rack is full");
		} else
		{
			this.mServers.add(pServer);
		}
	}

	/**
	 * Remove a given server from this rack.
	 * 
	 * @param pServer
	 *            the server to remove from this rack.
	 */
	void removeServer(Server pServer)
	{
		this.mServers.remove(pServer);
	}

	/**
	 * Added some extra 'U's of available height to the rack (to hold extra
	 * servers).
	 * 
	 * @param mHeight
	 *            the number of 'U's to add to the available rack space.
	 */
	void addU(int mHeight)
	{
		U += mHeight;
	}

	/**
	 * Get the total number of 'U's available in this rack.
	 * 
	 * @return the total number of 'U's in this rack.
	 */
	int getU()
	{
		return U;
	}

	/**
	 * Method to calculate the number of failed servers in this rack.
	 * 
	 * @return the number of failed servers in this rack.
	 */
	public int getNumFailedServers()
	{
		int numFailed = 0;

		for (Server server : mServers)
		{
			if (!server.isAlive())
			{
				numFailed += 1;
			}
		}

		return numFailed;
	}

	/**
	 * Method to calculate the average cpu utilisation of servers in this rack.
	 * 
	 * @return the averate cpu utilisation of servers in this rack in range [0,1]
	 */
	public double getMeanCPUutilisation()
	{
		double cpuUtil = 0;
		
		for (Server server : mServers)
		{
			cpuUtil += server.getCPUUtilisation();
		}
		
		cpuUtil/=getNumServers();
		
		return cpuUtil;
	}
	
	
	/**
	 * Returns the server with the given ID, null if it does not exist. ID
	 * represents server location as if all servers were rolled out into a line.
	 * 
	 * @param pID
	 *            The ID of the server to fetch.
	 * @return The server corresponding the to given ID, or null if it does not
	 *         exist.
	 */
	public Server getServer(final int pID)
	{
		return mServers.get(pID);
	}

	/**
	 * Gets the server objects from this rack.
	 * 
	 * @return all the server objects in this rack.
	 */
	public Server[] getServers()
	{
		return this.mServers.toArray(new Server[mServers.size()]);
	}

	/**
	 * 
	 * Get the server objects from this rack
	 * 
	 * @return all the server objects in this rack.
	 */
	public ArrayList<Server> getmServers()
	{
		return mServers;
	}

	/**
	 * Get the speed of communicating between servers in this racks.
	 * 
	 * @return the speed of communication between servers in this racks.
	 */
	int getIntraNetworkSpeed()
	{
		return mIntraRackNetworkDistance;
	}

	/**
	 * Calculate the network distance between the two given servers.
	 * 
	 * @param pServerIDA
	 *            the first server.
	 * @param pServerIDB
	 *            the second server.
	 * @return the network between the servers.
	 */
	public int calcNetworkDistance(final int pServerIDA, final int pServerIDB)
	{
		int distance = -1;

		if (inRange(pServerIDA) && inRange(pServerIDB))
		{
			distance = calcNetworkDistance(pServerIDA)
					+ calcNetworkDistance(pServerIDB);
		}

		return distance;
	}

	/**
	 * Calculate the network distance between the given server and the switch of
	 * this rack.
	 * 
	 * @param pServerID
	 *            the id of the server to check the network distance of.
	 * @return the network distance between the server and the switch of this
	 *         rack.
	 */
	public int calcNetworkDistance(final int pServerID)
	{
		int distance = -1;

		if (inRange(pServerID))
		{
			distance = getIntraNetworkSpeed();
		}

		return distance;
	}

	/**
	 * Check if given server ID is within the range of server IDs present within
	 * this rack.
	 * 
	 * @param pServerID
	 *            the server ID to look for.
	 * @return true if the ID is within the range of this rack, else false.
	 */
	private boolean inRange(final int pServerID)
	{
		boolean inRange = false;

		if (pServerID >= 0 && pServerID < mServers.size())
		{
			inRange = true;
		}

		return inRange;
	}

	/**
	 * Check if the given server is in this rack.
	 * 
	 * @param pServer
	 *            the server to look for.
	 * @return true if the server is in this rack, else false.
	 */
	public boolean contains(final Server pServer)
	{
		return mServers.contains(pServer);
	}

	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#getServer(sim.physical.IP)
	 */
	public Server getServer(final IP pIP)
	{
		return mServers.get(pIP.server());
	}

	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#addBlock(sim.physical.Block)
	 */
	@Override
	public boolean addBlock(Block pBlock)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Rack getRack(IP mIP)
    {
    	return this;
    }
	
	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#getNumRacks()
	 */
	@Override
	public int getNumRacks()
	{
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#getPower()
	 */
	@Override
	public double getPower()
	{
		double rackPower = 0;
		for (int i = 0; i < getNumServers(); i++)
		{
			// TODO: change to the current power being used, not max
			double serverPower = mServers.get(i).getServerPower();
			logger.debug("Server power " + i + ": " + serverPower);
			rackPower += serverPower;
		}

		return rackPower;
	}

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#getPurchaserCost()
     */
    @Override
    public double getPurchaserCost()
    {
        double rackPurchaseCost = 0;
        
        for (int i = 0; i < getNumServers(); i++)
        {
            double serverPurchaseCost = mServers.get(i).getPurchaseCost();
            logger.debug("Server purchase cost " + i + ": £ " + serverPurchaseCost);
            rackPurchaseCost += serverPurchaseCost;
        }
        
		return rackPurchaseCost;
	}

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#getSoftwareCost()
     */
	@Override
	public double getSoftwareCost()
	{
		double rackSoftwareCost = 0;

		for (int i = 0; i < getNumServers(); i++)
		{
			double serverSoftwareCost = mServers.get(i).getSoftwareCost();
			logger.debug("Server software cost " + i + ": £ "
						+ serverSoftwareCost);
			rackSoftwareCost += serverSoftwareCost;
		}

		return rackSoftwareCost;
	}

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#distributeIPs(int, int)
     */
	@Override
	public int distributeIPs(final int pBlock, final int pDC, int pNumServersSeen)
	{
	    pNumServersSeen = distributeIPs(0, pBlock, pDC, pNumServersSeen);
		
		return pNumServersSeen;
	}

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#distributeIPs(int, int, int, int)
     */
	@Override
	int distributeIPs(final int pRack, final int pBlock, final int pDC, int pNumServersSeen)
	{
		for (int i = 0; i < mServers.size(); i++)
		{
		    pNumServersSeen = mServers.get(i).setIP(i, pRack, pBlock, pDC, pNumServersSeen);
		}
		
		return pNumServersSeen;
	}
	
	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#calcAbsolutePosition(sim.physical.IP)
	 */
	@Override
	public Point calcAbsolutePosition(final IP pIP)
	{
	    // Absolute position of the server, relative to this rack, is (0,0).
        Point absolutePosition = new Point(0, 0);
        
        return absolutePosition;
	}
	
    /**
     * TODO
     */
    @Override
    public boolean contains(final IP pIP)
    {
        boolean doesContain = false;
        
        if (pIP.server() >= 0 && pIP.server() < mServers.size())
        {
            doesContain = pIP.equals(mServers.get(pIP.server()).getIP());
        }
        
        return doesContain;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.physical.Block#getAbsolutePosition()
     */
    @Override
    public Point getAbsolutePosition()
    {
        Server server = mServers.get(0);
        final Point absServer = server.getAbsolutePosition();
        
        return absServer;
    }
    
    /**
     * TODO
     * 
     * @return
     */
    ArrayList<AirConditioner> getAirCons()
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();
        //TODO Return an empty arraylist because this rack contains zero air conditioners.
        
        return airCons;
    }
    
    public String toString() {
    	
    	String s = mName + ": [ ";
    	
    	for(int i=0; i<getServers().length; i++) {
    		s+= "'" + getServers()[i] + "' ";
    	}
    	s+="]\n";
    	return s;
    }
    
    /**
     * Return a test rack (for debugging)
     * @return the test rack
     * @throws Exception 
     */
    public static Rack getTestRack(int rackNumber, int numServers) throws Exception {
    	
    	int networkDistance = 100;

    	Rack r = new Rack(networkDistance, "TestRack"+rackNumber, new Point(0,0)); //(networkDistance, "Test Aisle", new Point(0,0), new Point(1,1));
    	
    	for(int i=0; i<numServers; i++) {
    		r.addServer(Server.getTestServer());
    	}
    	
    	return r;
    }
}
