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

import sim.module.costs.bo.Costs;
import sim.physical.network.IP;

/**
 * Class representing a physical datacentre within the simulator.
 */
public class Datacentre
{
	
	public static Logger logger = Logger.getLogger(Datacentre.class);
	
    /**
     * The default setup to initialise the datacentre with.
     */
    enum Type
    {
        defaultType, random, google, small
    }

    // Constants.
    //private static final int   INTRA_DC_NETWORK_DISTANCE = 100;
    
    //Racks are 800mm square
    public static final double GRID_SQUARE_LENGTH        = 0.8;                   
    
    // Member variables.
    private ArrayList<Block>   mBlocks                   = new ArrayList<Block>();
    private String             name;
    private int 			   id;	
    
    //Costs
    private Costs mCosts = new Costs(this);
    
    // In metres.
    protected int              mDCGridHeight;
    protected int              mDCGridWidth;

    private int                mNumFailedServers         = 0;
    private final int          mIntraDCNetworkDistance;
    
    /**
     * Constructor for creating a new custom datacentre.
     * 
     * @param pNetworkDistance
     *            the network distance between all aisles in this datacentre.
     * @param pName
     *            the name of this datacentre.
     * @param id
     * 			  the id/index of this datacentre
     * @param width 
     * 			  the width of the datacentre
     * @param height
     * 			  the height of the datacentre
     */
    public Datacentre(int pNetworkDistance, String pName, int id, 
    		final int width, final int height)
    {
        mDCGridWidth = width;
        mDCGridHeight = height;
        mIntraDCNetworkDistance = pNetworkDistance;
        name = pName;
        this.id = id;
    }
    
    /**
     * Gets the dimensions of this datacentre, as (x,y,z) coordinates.
     * 
     * @return the dimensions of this datacentre.
     */
    public double[] getDatacentreDimensions()
    {
        return new double[]
        { mDCGridWidth * GRID_SQUARE_LENGTH, mDCGridHeight * GRID_SQUARE_LENGTH };
    }

    public int getID() {
    	return id;
    }
    
    public int getGridHeight()
    {
        return mDCGridHeight;
    }

    public int getGridWidth()
    {
        return mDCGridWidth;
    }

    /**
     * Returns the number of aisles in this datacentre.
     * 
     * @return Number of aisles in this datacentre.
     */
    public int getNumAisles()
    {
        return mBlocks.size();
    }

    /**
     * Add the given aisle to the datacentre.
     * 
     * @param pA
     *            the aisle to add to the datacentre.
     */
    public void addAisle(Aisle pA)
    {
        this.mBlocks.add(pA);
    }

    /**
     * Remove the given aisle from the datacentre.
     * 
     * @param pA
     *            the aisle to remove from the datacentre.
     */
    public void removeAisle(Aisle pA)
    {
        this.mBlocks.remove(pA);
    }

    /**
     * Returns the number of racks in the datacentre.
     * 
     * @return The number of racks in the datacentre.
     */
    public int getNumRacks()
    {
        int numRacks = 0;

        for (int i = 0; i < getNumAisles(); i++)
        {
            numRacks += mBlocks.get(i).getNumRacks();
        }
        logger.debug("\nNumber of racks: " + numRacks);
        return numRacks;
    }

    
    
    /**
     * Method to fetch the number of servers in this datacentre.
     * 
     * @return the number of servers in this datacentre.
     */
    public int getNumServers()
    {
        int num = 0;

        for (Block block : mBlocks)
        {
            num += block.getNumServers();
        }

        return num;
    }

    /**
     * Get the servers in this datacentre
     * 
     * @return ArrayList of servers
     */
    public ArrayList<Server> getServers()
    {
        ArrayList<Server> servers = new ArrayList<Server>();

        for (Block block : mBlocks)
        {
        	servers.addAll(block.getmServers());
        }

        return servers;
    }

    /**
     * Get an array of IPs of servers in this datacentre
     * 
     * @return the IPs array
     */
    public IP[] getServerIPs()
    {
    	ArrayList<Server> servers = this.getServers();
    	IP[] IPs = new IP[servers.size()];
    	
    	for(int i=0; i<servers.size(); i++) {
    		IPs[i] = servers.get(i).getIP();
    	}
    	return IPs;
    }
    
    /**
     * Get an array of IDs of servers in this datacentre
     * 
     * @return array of server ids
     */
    public int[] getServerIDs()
    {
    	ArrayList<Server> servers = this.getServers();
    	int[] IDs = new int[servers.size()];
    	
    	for(int i=0; i<servers.size(); i++) {
    		IDs[i] = servers.get(i).getID();
    	}
    	return IDs;
    }
    
    /**
     * Method to fetch the number of CPUs in this datacentre.
     * 
     * @return the number of CPUs in this datacentre.
     */
    public int getNumCPUs()
    {
        int num = 0;

        for (Block block : mBlocks)
        {
            num += block.getNumCPUs();
        }

        return num;
    }

    /**
     * Method to calculate the number of failed servers in this datacentre.
     * 
     * @return the number of failed servers in this datacentre.
     */
    public int getNumFailedServers()
    {
        mNumFailedServers = 0;

        for (Block block : mBlocks)
        {
            mNumFailedServers += block.getNumFailedServers();
        }

        return mNumFailedServers;
    }

    /**
     * Calculates the power used by the servers of the data centre
     * 
     * @return power used
     */
    public double getPower()
    {
        double power = 0;

        for (int i = 0; i < getNumAisles(); i++)
        {
            double aislePower = mBlocks.get(i).getPower();
            logger.debug("Aisle power " + i + ": " + aislePower + "\n--------------");
            power += aislePower;
        }
        logger.debug("\nPower Used by Servers: " + power + " Watts");
        return power;
    }
    
    /**
     * Return the Costs object associated with this datacentre
     * @return - Costs
     */
    public Costs getCosts() {
    	return mCosts;
    }

    /**
     * Completely clears the datacentre to a 'blank-sheet'.
     */
    protected void clear()
    {
        mBlocks.clear();
    }

    /**
     * Method to add a given aisle to the datacentre.
     * 
     * @param pAisle
     *            The aisle to added to the datacentre.
     */
    protected void add(Aisle pAisle)
    {
        mBlocks.add(pAisle);
    }

    /**
     * Method to remove a given aisle from the datacentre.
     * 
     * @param pAisle
     *            The aisle to remove from the datacentre.
     */
    protected void remove(Aisle pAisle)
    {
        mBlocks.remove(pAisle);
    }

    /**
     * @return the mAisles
     */
    public ArrayList<Aisle> getmAisles()
    {
        ArrayList<Aisle> aisles = new ArrayList<Aisle>();

        for (Block block : mBlocks)
        {
            if (block.getClass().getName() == Aisle.class.getName())
            {
                aisles.add((Aisle) block);
            }
        }

        return aisles;
    }

    /**
     * Gets the Aisles in this datacentre.
     * 
     * @return the array of aisles
     */
    public Aisle[] getAisles()
    {
        return this.mBlocks.toArray(new Aisle[mBlocks.size()]);
    }
    
    public Block[] getBlocks()
    {
        return this.mBlocks.toArray(new Block[mBlocks.size()]);
    }

    /**
     * Return the speed of network communications between aisles within this
     * datacentre.
     * 
     * @return the speed of communicating between aisles within this datacentre.
     */
    public int getIntraNetworkSpeed()
    {
        return mIntraDCNetworkDistance;
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
        int distance = 0;
        int seenServers = 0;
        boolean serverAFound = false, serverBFound = false;

        for (Block hardware : mBlocks)
        {
            final int localIDA = pServerIDA - seenServers;
            final int localIDB = pServerIDB - seenServers;

            seenServers += hardware.getNumServers();

            if (pServerIDA < seenServers && pServerIDB < seenServers && !(serverAFound || serverBFound))
            {
                distance = hardware.calcNetworkDistance(localIDA, localIDB);
                break;
            }
            else if (pServerIDA < seenServers && !serverAFound)
            {
                final int temp = hardware.calcNetworkDistance(localIDA);

                if (temp == -1)
                {
                    distance = temp;
                    break;
                }
                else
                {
                    distance += temp;
                    distance += getIntraNetworkSpeed();
                    serverAFound = true;
                }
            }
            else if (pServerIDB < seenServers && !serverBFound)
            {
                final int temp = hardware.calcNetworkDistance(localIDB);

                if (temp == -1)
                {
                    distance = temp;
                    break;
                }
                else
                {
                    distance += temp;
                    distance += getIntraNetworkSpeed();
                    serverBFound = true;
                }
            }

            if (serverAFound && serverBFound)
            {
                break;
            }
        }

        if (!serverAFound || !serverBFound)
        {
            distance = -1;
        }

        return distance;
    }

    /**
     * Calculate the network distance between the given server and the switch of
     * this datacentre.
     * 
     * @param pServerID
     *            the server id to check the network distance of.
     * @return the network distance between the server and the switch of this
     *         datacentre.
     */
    public int calcNetworkDistance(final int pServerID)
    {
        int distance = -1;
        int seenServers = 0;

        for (Block block : mBlocks)
        {
            final int localID = pServerID - seenServers;
            seenServers += block.getNumServers();

            if (pServerID < seenServers)
            {
                distance = block.calcNetworkDistance(localID);

                if (distance == -1)
                {
                    break;
                }
                else
                {
                    distance += mIntraDCNetworkDistance;
                    break;
                }
            }
        }

        return distance;
    }

    /**
     * Check if this datacentre contains the given server.
     * 
     * @param pServer
     *            the server to look for.
     * @return true if this datacentre contains the given server, else false.
     */
    public boolean contains(final Server pServer)
    {
        boolean contains = false;

        for (Block block : mBlocks)
        {
            if (block.contains(pServer))
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    /**
     * Method to calculate the physical distance between two servers in a
     * straight line.
     * 
     * @param aServer
     *            The first server.
     * @param bServer
     *            The second server.
     * @return The straight-line distance between the two given servers.
     */
    public double calcPhysicalDistance(Server aServer, Server bServer)
    {
        double distance = aServer.getAbsolutePosition().distance(bServer.getAbsolutePosition());
        ;

        return distance;
    }

    /**
     * Returns the server with the given index, null if it does not exist. 
     * 
     * Index represents server location as if all servers in all racks in all aisles
     * were rolled out into a line, starting with index=0 for each datacentre
     * 
     * Note: Server indexes start at 0 for each datacentre.  This is *not* the same
     * as server IDs, which are unique across all datacentres
     * 
     * @param serverIndex
     *            The index of the server to fetch.
     * @return The server corresponding the to given index, or null if it does not
     *         exist.
     */
    public Server getServer(final int serverIndex)
    {
    	logger.debug("DC" + this.id + ": Getting server with id: " + serverIndex);
    	
        Server server = null;
        int seenServers = 0;

        for (Block block : mBlocks)
        {
            final int localID = serverIndex - seenServers;
            seenServers += block.getNumServers();

            if (serverIndex < seenServers)
            {
                server = block.getServer(localID);
                break;
            }
        }

        return server;
    }

    /**
     * Calculate and distribute IPs to all of the servers in this datacentre.
     * 
     * @param pDC
     *            the datacentre level IP of this datacentre.
     * @param pNumServersSeen
     *            the number of servers that have already had an IP/ID allocated
     *            to them.
     */
    public int distributeIPs(final int pDC, int pNumServersSeen)
    {
        for (int i = 0; i < mBlocks.size(); i++)
        {
            pNumServersSeen = mBlocks.get(i).distributeIPs(i, pDC, pNumServersSeen);
        }

        return pNumServersSeen;
    }
    
    /**
     * Gets the server from the datacentre with the given IP.
     * 
     * @param pIP
     *            the IP of the server to fetch.
     * @return the server with the given IP.
     */
    public Server getServer(final IP pIP)
    {
        return mBlocks.get(pIP.block()).getServer(pIP);
    }

    public Rack getRack(final IP pIP)
    {
        return mBlocks.get(pIP.block()).getRack(pIP);
    }

    /**
     * Gets the name of the datacentre.
     * 
     * @return the name of the datacentre.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of the datacentre to the given string.
     * 
     * @param name
     *            the new name for the datacentre.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the current utilisation of the datacentre.
     * 
     * @return the current utilisation of the datacentre.
     */
    public double getUtilisation()
    {
        // int total = 0;
        double total = 0;
        int cpus = 0;
        for (int j = 0; j < getNumAisles(); j++)
        {
            for (int k = 0; k < getmAisles().get(j).getNumRacks(); k++)
            {
                for (int l = 0; l < getmAisles().get(j).getmRacks().get(k).getNumServers(); l++)
                {
                    /*
                     * int a = (int)
                     * Math.round(getmAisles().get(j).getmRacks().get
                     * (k).getmServers().get(l).getCPUUtilisation() * 100); a *=
                     * getmAisles
                     * ().get(j).getmRacks().get(k).getmServers().get(l
                     * ).getCPUs().length; total+=a;
                     */

                    total += getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getCPUUtilisation() * getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getCPUs().length;

                    cpus += getmAisles().get(j).getmRacks().get(k).getmServers().get(l).getCPUs().length;
                }
            }
        }
        
        //TODO: Major botch to prevent CPU Utilisation going to negative values.
        if (total < 0)
        {
        	logger.fatal("WARNING: DataCentre.getUtilisation()  We have a negative value.  Exiting system...");
        	System.exit(0);
            total = 0;
        }
        
        return (total / cpus * 100);
        // return (Math.round((float)total / cpus));
    }

    /**
     * Calculate the absolute position of the object pointed to by the given IP
     * (in this datacentre).
     * 
     * @param pIP
     *            the IP of the object to calculate the absolute position of.
     * @return the absolute position of the object pointed to by the given IP.
     */
    public Point calcAbsolutePosition(final IP pIP)
    {
        Block block = mBlocks.get(pIP.block());
        Point abs = block.calcAbsolutePosition(pIP);
        Point rel = block.getRelativePosition();

        Point absolutePosition = new Point(abs.x + rel.x, abs.y + rel.y);

        return absolutePosition;
    }

    /**
     * Get a block that contains an IP address
     * 
     * @param ip - the IP address
     * 
     * @return the corresponding block
     */
    public Block getBlock(final IP ip)
    {
        return mBlocks.get(ip.block());
    }

    /**
     * Calculate the position of an air conditioner 
     * 
     * @param aircon - the aircon unit  
     * 
     * @return the position of the aircon unit
     */
    public Point calcAbsolutePosition(AirConditioner aircon)
    {
        Block block = mBlocks.get(aircon.getIP().block());
        Point abs;
        Point rel;

        if (block.isAirCon())
        {
            abs = block.getRelativePosition();
            rel = new Point(0, 0);
        }
        else if (block.isAisle())
        {
            Aisle aisle = block.toAisle();
            abs = aisle.calcAbsolutePosition(aircon);
            rel = aisle.getRelativePosition();
        }
        else if (block.isContainer())
        {
            Container container = block.toContainer();
            abs = container.calcAbsolutePosition(aircon);
            rel = container.getRelativePosition();
        }
        else
        {
            abs = new Point(-1, -1);
            rel = new Point(0, 0);
        }

        Point absolutePosition = new Point(abs.x + rel.x, abs.y + rel.y);

        return absolutePosition;
    }

    /**
     * Get all air conditioner units in this datacentre
     * 
     * @return ArrayList of aircons
     */
    public ArrayList<AirConditioner> getAirCons()
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();

        for (Block block : mBlocks)
        {
            airCons.addAll(block.getAirCons());
        }
        
        return airCons;
    }
    
    /**
     * For testing purposes: Only usable for very small DCs.
     * 
     * Otherwise, java.lang.ArrayStoreException is thrown
     */
    public String toString() {
    	
    	String s = name + ":\n";
    	
    	for(int i=0; i<getAisles().length; i++) {
    		s+= getAisles()[i];
    	}
    	s+="-----------\n";
    	return s;
    }
    
    /**
     * Return a new test datacentre (for debugging)
     * @return the test Datacentre
     */
    public static Datacentre getTestDatacentre(int dcNumber, int numAisles, int numRacksPerAisle, int numServersPerRack) {
    	
    	int networkDistance = 100;
    	int dcID = dcNumber;
    	String name = "TestDatacentre"+dcNumber;
    	int width = 100;
    	int height = 100;
    	
    	Datacentre dc = new Datacentre(networkDistance, name, dcID, width, height);
    	
    	try {
	    	for(int i=0; i<numAisles; i++) {
	    		dc.addAisle(Aisle.getTestAisle(i, numRacksPerAisle, numServersPerRack));
	    	}
    	} catch (Exception e) {
    		System.out.println("Error creating test aisles for test datacentre");
    		e.printStackTrace();
    	}
    	
    	
    	//Distribute IPs to all servers in the DC
        dc.distributeIPs(dcID, 0);
 	
    	return dc;
    }
}
