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
 * Created on 2 Sep 2011
 */
package sim.physical;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.module.replacements.ReplacementsModuleRunner;
import sim.module.replacements.configparams.ReplacementModuleConfigParams;
import sim.physical.network.IP;
import utility.direction.CompassDirection;

/**
 * Class representing a collection of racks and air-conditioning units to make
 * up a block.
 * 
 * These blocks could be a single rack/air-con or a selection of them.
 */
public abstract class Block
{
    public enum Type
    {
        container, aisle, rack, aircon
    }

    public static Logger logger = Logger.getLogger(Block.class);
    
    // Member variables.
    ArrayList<Block> mBlocks           = new ArrayList<Block>();
    final Point      mRelativePosition;
    final Point      mDimensions;
    final Type       mType;

    ArrayList<IP>    mDeadServerList   = new ArrayList<IP>();
    int              mNumServers       = -1;
    int              mNumFailedServers = -1;

    CompassDirection direction = CompassDirection.NORTH;
    
    /**
     * Basic constructor.
     * 
     * @param type
     *            the type of block to create.
     * @param relativePosition
     *            the position of the block (lower left-hand corner).
     * @param dimensions
     *            the dimensions of the block in terms of cells across by cell
     *            up.
     */
    public Block(final Type type, final Point relativePosition, final Point dimensions)
    {
        mType = type;
        mRelativePosition = relativePosition;
        mDimensions = dimensions;
    }

    /**
     * Get the relative position of this block, with the position being the
     * lower-left corner of the block (relative to its parent datacentre/block).
     * 
     * @return the position of this block.
     */
    public Point getRelativePosition()
    {
        return mRelativePosition;
    }

    /**
     * Returns the absolute position of this block (in relation to its parent
     * datacentre).
     * 
     * @return the absolute position of this block.
     */
    public abstract Point getAbsolutePosition();

    /**
     * Get the (x, y) dimensions of this block.
     * 
     * @return the dimensionss of this block.
     */
    public Point getDimensions()
    {
        return mDimensions;
    }

    /**
     * Get the number of other blocks that this block contains.
     * 
     * @return the number of blocks contained within this block.
     */
    public int size()
    {
        return mBlocks.size();
    }

    public Block[] getBlocks()
    {
        return mBlocks.toArray(new Block[mBlocks.size()]);
    }

    /**
     * Check if this block is an individual rack.
     * 
     * @return true if this block is a single rack, else false.
     */
    public boolean isRack()
    {
        boolean isRack = false;

        if (mType == Type.rack)
        {
            isRack = true;
        }

        return isRack;
    }

    /**
     * Check if this block is an individual aisle.
     * 
     * @return true if this block is a single aisle, else false.
     */
    public boolean isAisle()
    {
        boolean isAisle = false;

        if (mType == Type.aisle)
        {
            isAisle = true;
        }

        return isAisle;
    }

    /**
     * Check if this block is an individual container.
     * 
     * @return true if this block is a single container, else false.
     */
    public boolean isContainer()
    {
        boolean isContainer = false;

        if (mType == Type.container)
        {
            isContainer = true;
        }

        return isContainer;
    }

    /**
     * Check if this block is an individual air conditioning unit.
     * 
     * @return true if this block is a single air conditioning unit, else false.
     */
    public boolean isAirCon()
    {
        boolean isAirCon = false;

        if (mType == Type.aircon)
        {
            isAirCon = true;
        }

        return isAirCon;
    }

    /**
     * Cast this block to a rack, if it is a single rack.
     * 
     * @return this block as a rack if it is a single rack, else null if this
     *         block is not a rack.
     */
    public Rack toRack()
    {
        Rack rack = null;

        if (isRack())
        {
            rack = (Rack) this;
        }

        return rack;
    }

    public Rack getRack(IP mIP)
    {
        return mBlocks.get(mIP.subBlock()).getRack(mIP);
    }

    /**
     * Cast this block to a aisle, if it is a single aisle.
     * 
     * @return this block as a aisle if it is a single aisle, else null if this
     *         block is not a aisle.
     */
    public Aisle toAisle()
    {
        Aisle aisle = null;

        if (isAisle())
        {
            aisle = (Aisle) this;
        }

        return aisle;
    }

    /**
     * Cast this block to a container, if it is a single container.
     * 
     * @return this block as a container if it is a single container, else null
     *         if this block is not a container.
     */
    public Container toContainer()
    {
        Container container = null;

        if (isContainer())
        {
            container = (Container) this;
        }

        return container;
    }

    /**
     * Cast this block to an air conditioning unit, if it is a single air
     * conditioning unit.
     * 
     * @return this block as a air conditioning unit if it is a single air
     *         conditioning unit, else null if this block is not a air
     *         conditioning unit.
     */
    public AirConditioner toAirCon()
    {
        AirConditioner aircon = null;

        if (isAirCon())
        {
            aircon = (AirConditioner) this;
        }

        return aircon;
    }

    // /**
    // * Get the temperature of this block.
    // *
    // * @return the temperature of this block.
    // */
    // public double getTemperature()
    // {
    // double temperature = 0;
    //
    // for (Block block : mBlocks)
    // {
    // temperature += block.getTemperature();
    // }
    //
    // return temperature;
    // }

    /**
     * Get the server corresponding to given IP.
     * 
     * @param pIP
     *            the IP of the server to fetch.
     * @return the server object corresponding to the given IP, else null if it
     *         does not exist.
     */
    public Server getServer(final IP pIP)
    {
        Server server = null;

        server = mBlocks.get(pIP.subBlock()).getServer(pIP);

        return server;
    }

    /**
     * Distribute IPs to each server in this block based on the given datacentre
     * and block network locations.
     * 
     * @param pBlock
     *            the block level IP corresponding to this block.
     * @param pDC
     *            the datacentre level IP corresponding to this block.
     * @param pNumServersSeen
     *            the number of servers that have already had an IP/ID allocated
     *            to them.
     */
    public int distributeIPs(final int pBlock, final int pDC, int pNumServersSeen)
    {
        for (int i = 0; i < mBlocks.size(); i++)
        {
            pNumServersSeen = mBlocks.get(i).distributeIPs(i, pBlock, pDC, pNumServersSeen);
        }

        return pNumServersSeen;
    }

    /**
     * Calculate the absolute position of the object with the given IP in this
     * block.
     * 
     * @param pIP
     *            the IP of the object to calculate the position of.
     * @return the absolute position of the object with the given IP.
     */
    public Point calcAbsolutePosition(final IP pIP)
    {
        Block block = mBlocks.get(pIP.subBlock());
        Point abs = block.calcAbsolutePosition(pIP);
        Point rel = block.getRelativePosition();

        Point absolutePosition = new Point(abs.x + rel.x, abs.y + rel.y);

        return absolutePosition;
    }

    /**
     * Method to fetch the number of servers in this aisle.
     * 
     * @return the number of servers in this aisle.
     */
    public int getNumServers()
    {
        int num = 0;

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                num += block.toRack().getNumServers();
            }
        }

        return num;
    }

    /**
     * Get an arraylist of all servers in this block
     * 
     * @return array list of servers
     */
    public ArrayList<Server> getmServers() {
        
    	ArrayList<Server> servers = new ArrayList<Server>();

        for (Block block : mBlocks)
        {
            if (block.isRack())
            {
                servers.addAll(block.toRack().getmServers());
            }
        }

        return servers;
    }
    
    /**
     * TODO Check if this block contains the given server.
     * 
     * @param pIP
     *            the object to look for.
     * @return true if this block contains the given server, else false.
     */
    public boolean contains(final IP pIP)
    {
        boolean doesContain = false;

        if (pIP.subBlock() >= 0 && pIP.subBlock() < mBlocks.size())
        {
            doesContain = mBlocks.get(pIP.subBlock()).contains(pIP);
        }

        return doesContain;
    }

    /**
     * TODO
     * 
     * @return
     */
    ArrayList<AirConditioner> getAirCons()
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();

        for (Block block : mBlocks)
        {
            airCons.addAll(block.getAirCons());
        }

        return airCons;
    }

    /**
     * Adds the given server IP to the list of dead (hard fail) servers
     * 
     * @param pIP the server to add
     */
    void setDead(final IP pIP, final long pTime)
    {
        if (!mDeadServerList.contains(pIP))
        {
            mDeadServerList.add(pIP);
        }
    }

    /**
     * Replace the block if proportion of dead servers is greater than a replacement threshold
     * 
     */
    public void performFixes()
    {
        if (mNumServers == -1)
        {
            mNumServers = getNumServers();
        }

        if ((((double) mDeadServerList.size()) / mNumServers) > getReplacementThreshold()) {
        	logger.warn("Proportion of broken servers in block is greater than threshold " + getReplacementThreshold() + ". Replacing block...");
        	replaceBlock();
        } else {
        	//do nothing ... not enough failures
        	logger.warn("Proportion of broken servers in block is less than threshold " + getReplacementThreshold() + ". Doing nothing...");
        }
    }

    /**
     * Get the replacement threshold for this block (the proportion of servers that
     * need to fail before block is replaced
     * @return
     */
    private double getReplacementThreshold()
    {
        return ((ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams()).getReplacementThreshold(mType);
    }

    /**
     * Replace the servers and aircon units in this block
     */
    private void replaceBlock()
    {
    	//Replace servers
        for (IP ip : mDeadServerList)
        {
        	ReplacementsModuleRunner.getInstance().individualReplacement(getServer(ip));
//            getServer(ip).forceFix();
        }
        mDeadServerList.clear();
        
        //Replace aircon
        for(AirConditioner aircon : getAirCons())
        {
        	ReplacementsModuleRunner.getInstance().individualReplacement(aircon);
        }

        // TODO Add code to add cost of replacing the container, in addition to
        // contents.
    }

    /**
     * Distrbute IPs to each server in this block based on the given datacentre
     * and block network locations.
     * 
     * @param pSubBlock
     *            the sub-block (e.g. rack, aircon) level IP corresponding to
     *            this block.
     * @param pBlock
     *            the block (e.g. aisle, container, rack, aisle) level IP
     *            corresponding to this block.
     * @param pDC
     *            the datacentre level IP corresponding to this block.
     * @param pNumServersSeen
     *            the number of servers that have already had an IP/ID allocated
     *            to them.
     * 
     * @return the number of servers that have already had an IP/ID allocated to
     *         them.
     */
    abstract int distributeIPs(final int pSubBlock, final int pBlock, final int pDC, int pNumServersSeen);

    /**
     * Add a new block to this block.
     * 
     * @param pBlock
     *            the block to add to this block.
     * @return true if the new block was successfully added, else false.
     */
    public abstract boolean addBlock(Block pBlock);

    /**
     * Returns the number of racks in this block.
     * 
     * @return The number of racks in this block.
     */
    public abstract int getNumRacks();

    /**
     * Method to fetch the number of CPUs in this block.
     * 
     * @return the number of CPUs in this block.
     */
    public abstract int getNumCPUs();

    /**
     * Method to calculate the number of failed servers in this block.
     * 
     * @return the number of failed servers in this block.
     */
    public abstract int getNumFailedServers();

    /**
     * Calculate the power used by the servers in the block
     * 
     * @return the power used by the block
     */
    public abstract double getPower();

    /**
     * Calculates the purchase cost of the hardware in the block.
     * 
     * @return the purchase cost the block.
     */
    public abstract double getPurchaserCost();

    /**
     * TODO Calculate the network distance between the given server and the
     * switch of this block.
     * 
     * @param serverID
     *            the server to check the network distance of.
     * @return the network distance between the server and the switch of this
     *         block.
     */
    public abstract int calcNetworkDistance(final int serverID);

    /**
     * TODO Calculate the network distance between the two given servers.
     * 
     * @param pServerIDA
     *            the first server.
     * @param pServerIDB
     *            the second server.
     * @return the network between the servers.
     */
    public abstract int calcNetworkDistance(final int pServerIDA, final int pServerIDB);

    /**
     * TODO Check if this block contains the given server.
     * 
     * @param pServer
     *            the server to look for.
     * @return true if this block contains the given server, else false.
     */
    public abstract boolean contains(final Server pServer);

    /**
     * Returns the server with the given ID, null if it does not exist. ID
     * represents server location as if all servers in all racks were rolled out
     * into a line.
     * 
     * @param pID
     *            The ID of the server to fetch.
     * @return The server corresponding the to given ID, or null if it does not
     *         exist.
     */
    public abstract Server getServer(final int pID);

    /**
     * Returns the cost of the software running on this hardware.
     * 
     * @return the cost of the software running on the hardware.
     */
    public abstract double getSoftwareCost();
}
