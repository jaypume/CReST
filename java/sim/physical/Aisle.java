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

/**
 * Class representing an physical aisle of racks within a datacentre.
 */
public class Aisle extends Block
{
	public static Logger logger = Logger.getLogger(Aisle.class);
	
    // Constants.
    //private static final int INTRA_AISLE_NETWORK_DISTANCE = 10;

    // Member variables.
    private String     mName;
    private int        mIntraAisleNetworkDistance;

    /**
     * Basic constructor.
     * 
     * @param pNetworkDistance
     *            the network distance between racks in this aisle.
     * @param pName
     *            the name to assign to this aisle.
     * @param pRelativePosition
     *            the position of the aisle (lower left-hand corner) relative to
     *            its containing block/datacentre.
     * @param pDimensions
     *            the dimensions of the aisle in terms of cells across by cell
     *            up.
     */
    public Aisle(int pNetworkDistance, String pName, final Point pRelativePosition, final Point pDimensions)
    {
        super(Type.aisle, pRelativePosition, pDimensions);
        mIntraAisleNetworkDistance = pNetworkDistance;
        mName = pName;
    }

    /**
     * Method to return useful information about this aisle.
     * 
     * @return A StringBuffer of useful information about this aisle.
     */
    StringBuffer toStringBuffer()
    {
        StringBuffer aisleInfo = new StringBuffer();

        for (int i = 0; i < mBlocks.size(); i++)
        {
            if (mBlocks.get(i).isRack())
            {
                Rack rack = mBlocks.get(i).toRack();

                aisleInfo.append("\n========================\n");
                aisleInfo.append("======== Rack ");
                aisleInfo.append(i);
                aisleInfo.append(" ========");
                aisleInfo.append("\n========================");
                aisleInfo.append(rack.toStringBuffer());

                if (i + 1 < getNumRacks())
                {
                    aisleInfo.append("\n");
                }
            }
        }

        return aisleInfo;
    }

    /**
     * Returns the number of racks in this aisle.
     * 
     * @return The number of racks in this aisle.
     */
    public int getNumRacks()
    {
        int numRacks = 0;

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                numRacks++;
            }
        }
        return numRacks;
    }

	/**
	 * Add a given air conditioner to this aisle.
	 * 
	 * @param aircon
	 *            the rack to add to the aisle.
	 */
	public void addAirCon(AirConditioner aircon)
	{
		mBlocks.add(aircon);
	}
	

    /**
     * Add a given rack to this aisle.
     * 
     * @param pR
     *            the rack to add to the aisle.
     */
    public void addRack(Rack pR)
    {
        mBlocks.add(pR);
    }

    /**
     * Remove a rack from this aisle.
     * 
     * @param pR
     *            the rack to remove from the aisle.
     */
    public void removeRack(Rack pR)
    {
        mBlocks.remove(pR);
    }

    /**
     * Method to calculate the number of failed servers in this aisle.
     * 
     * @return the number of failed servers in this aisle.
     */
    public int getNumFailedServers()
    {
        int numFailed = 0;

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                numFailed += block.toRack().getNumFailedServers();
            }
        }

        return numFailed;
    }

    /**
     * Aisles now contain racks and air-conditions. This cost algorithm has
     * been refactored to still work after blocks/contains were introduced.
     * However, it still only calculates cost from racks, not air-conditioner
     * units.
     * 
     * Calculates the power used by the servers in the aisle
     * 
     * @return the power used by the aisle
     */
    public double getPower()
    {
        double aislePower = 0;

        for (int i = 0; i < mBlocks.size(); i++)
        {
            double rackPower = mBlocks.get(i).getPower();
            logger.debug("Rack power " + i + ": " + rackPower + "\n");
            aislePower += rackPower;
        }

        return aislePower;
    }

    /**
     * Calculates the purchase cost of the hardware in the aisle.
     * 
     * @return the purchase cost the aisle.
     */
    public double getPurchaserCost()
    {
        double aislePurchaseCost = 0;

        for (int i = 0; i < mBlocks.size(); i++)
        {
            double purchaseCost = mBlocks.get(i).getPurchaserCost();
            logger.debug("Aisle hardware purchase cost   " + i + ": £ " + purchaseCost + "\n");
            aislePurchaseCost += purchaseCost;
        }

        return aislePurchaseCost;
    }

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#getSoftwareCost()
     */
    public double getSoftwareCost()
    {
        double aisleSoftwareCost = 0;
        
        for (int i = 0; i < getNumRacks(); i++)
        {
            double rackSoftwareCost = mBlocks.get(i).getSoftwareCost();
            logger.debug("Rack software cost   " + i + ": £ " + rackSoftwareCost + "\n");
            aisleSoftwareCost += rackSoftwareCost;
        }
        
        return aisleSoftwareCost;
    }

    /**
     * TODO
     * This method may be is broken, as it relied upon the symmetry of the
     * DC, aisle, rack, server setup to return the right server.
     * 
     * Returns the server with the given ID, null if it does not exist. ID
     * represents server location as if all servers in all racks were rolled out
     * into a line.
     * 
     * @param pID
     *            The ID of the server to fetch.
     * @return The server corresponding the to given ID, or null if it does not
     *         exist.
     */
    public Server getServer(final int pID)
    {
        Server server = null;
        int seenServers = 0;

        for (int i = 0; i < mBlocks.size(); i++)
        {
            if (mBlocks.get(i).isRack())
            {
                Rack rack = mBlocks.get(i).toRack();
                final int localID = pID - seenServers;
                seenServers += rack.getNumServers();

                if (pID < seenServers)
                {
                    server = rack.getServer(localID);
                    break;
                }
            }
        }

        return server;
    }

    public Rack[] getRacks()
    {
        return mBlocks.toArray(new Rack[mBlocks.size()]);
    }

    /**
     * Get all the racks present in this aisle.
     * 
     * @return all the racks in this aisle.
     */
    public ArrayList<Rack> getmRacks()
    {
        ArrayList<Rack> racks = new ArrayList<Rack>();

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                racks.add(block.toRack());
            }
        }

        return racks;
    }

    /**
     * Get the speed of communicating between racks in this aisle.
     * 
     * @return the speed of communication between racks in this aisle.
     */
    public int getIntraNetworkSpeed()
    {
        return mIntraAisleNetworkDistance;
    }

    public void setIntraNetworkSpeed(int pSpeed)
    {
        mIntraAisleNetworkDistance = pSpeed;
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

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                Rack hardware = block.toRack();

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
        }

        if (!serverAFound || !serverBFound)
        {
            distance = -1;
        }

        return distance;
    }

    /**
     * Calculate the network distance between the given server and the switch of
     * this aisle.
     * 
     * @param pServerID
     *            the server to check the network distance of.
     * @return the network distance between the server and the switch of this
     *         aisle.
     */
    public int calcNetworkDistance(final int pServerID)
    {
        int distance = -1;
        int seenServers = 0;

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                Rack rack = block.toRack();

                final int localID = pServerID - seenServers;
                seenServers += rack.getNumServers();

                if (pServerID < seenServers)
                {
                    distance = rack.calcNetworkDistance(localID);

                    if (distance == -1)
                    {
                        break;
                    }
                    else
                    {
                        distance += mIntraAisleNetworkDistance;
                        break;
                    }
                }
            }
        }

        return distance;
    }

    /**
     * Check if this aisle contains the given server.
     * 
     * @param pServer
     *            the server to look for.
     * @return true if this aisle contains the given server, else false.
     */
    public boolean contains(final Server pServer)
    {
        boolean contains = false;

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                Rack rack = block.toRack();

                if (rack.contains(pServer))
                {
                    contains = true;
                    break;
                }
            }
        }

        return contains;
    }

    @Override
    public boolean addBlock(Block pBlock)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Get the name of this aisle.
     * 
     * @return the name of this aisle.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Set the name of this aisle to the given one.
     * 
     * @param name
     *            the new name for this aisle.
     */
    public void setName(String name)
    {
        mName = name;
    }

    /**
     * Method to fetch the number of CPUs in this aisle.
     * 
     * @return the number of CPUs in this aisle.
     */
	@Override
	public int getNumCPUs()
	{
		int num = 0;

		for (Block block : mBlocks)
		{
			if (block.isRack())
			{
				num += block.toRack().getNumCPUs();
			}
		}

		return num;
	}

	/*
	 * (non-Javadoc)
	 * @see sim.physical.Block#distributeIPs(int, int, int, int)
	 */
    @Override
    int distributeIPs(final int pSubBlock, final int pBlock, final int pDC, final int pNumServersSeen)
    {
        logger.error("WARNING: A aisle has been found inside another aisle/container.");
        return 0;
    }
    
    /**
     * Calculate the absolute position of an aircon unit within an aisle
     * 
     * @param aircon - the aircon unit
     * 
     * @return the position
     */
    public Point calcAbsolutePosition(AirConditioner aircon)
    {
        Block block = mBlocks.get(aircon.getIP().subBlock());
        Point absolutePosition;
        
        if (block.isAirCon())
        {
            absolutePosition = block.toAirCon().getRelativePosition();
        }
        else
        {
            absolutePosition = new Point(-1, -1);
        }
        
        return absolutePosition;
    }

    /*
     * (non-Javadoc)
     * @see sim.physical.Block#getAbsolutePosition()
     */
    @Override
    public Point getAbsolutePosition()
    {
        return mRelativePosition;
    }
    
    public String toString() {
    	
    	String s = mName + "\n";
        	
    	for(int i=0; i<getRacks().length; i++) {
    		s+= getRacks()[i];
    	}

    	return s;
    }
    
    /**
     * Return a test aisle (for debugging)
     * @return the test aisle
     * @throws Exception 
     */
    public static Aisle getTestAisle(int aisleNumber, int numRacks, int numServers) throws Exception {
    	
    	int networkDistance = 100;
    	
    	Aisle a = new Aisle(networkDistance, "Aisle"+aisleNumber, new Point(0,0), new Point(1,1));
    	
    	for(int i=0; i<numRacks; i++) {
    		a.addRack(Rack.getTestRack(i, numServers));
    	}
    	
    	return a;
    }
}
