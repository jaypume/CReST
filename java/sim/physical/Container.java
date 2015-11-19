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
 * Created on 5 Sep 2011
 */
package sim.physical;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.physical.network.IP;

/**
 * TODO
 */
public class Container extends Block
{
	public static Logger logger = Logger.getLogger(Container.class);
	
    // Member variables.
    ArrayList<IP>        mDeadServerList       = new ArrayList<IP>();

    /**
     * Constructor.
     * 
     * @param pRelativePosition
     *            the position of the container (lower left-hand corner)
     *            relative to its containing block/datacentre.
     * @param pDimensions
     *            the dimensions of the container in terms of cells across by
     *            cell up.
     */
    public Container(final Point pRelativePosition, final Point pDimensions)
    {
        super(Type.container, pRelativePosition, pDimensions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#addBlock(sim.physical.Block)
     */
    @Override
    public boolean addBlock(Block pBlock)
    {
        final boolean success;

        switch (pBlock.mType)
        {
            case rack:
            {
                success = addRack(pBlock.toRack());
                break;
            }
            case aircon:
            {
                success = addAirCon(pBlock.toAirCon());
                break;
            }
            default:
            {
                // Don't add the block if it isn't a rack or an air conditioner.
                success = false;
                break;
            }
        }

        return success;
    }

    /**
     * Add the given rack to this container.
     * 
     * @param pAirCon
     *            the rack to add to this container.
     * @return true if the rack was successfully added, else false.
     */
    private boolean addRack(Rack pRack)
    {
        boolean success = checkPositioning(pRack.getRelativePosition());

        if (success)
        {
            mBlocks.add(pRack);
        }

        return success;
    }

    /**
     * Add the given air conditioner to this container.
     * 
     * @param pAirCon
     *            the air condition to add to this container.
     * @return true if the air condition was successfully added, else false.
     */
    private boolean addAirCon(AirConditioner pAirCon)
    {
        boolean success = checkPositioning(pAirCon.getRelativePosition());

        if (success)
        {
            mBlocks.add(pAirCon);
        }

        return success;
    }

    /**
     * Check that the given position is free and within the bounds of this
     * container.
     * 
     * @param pPosition
     *            the position to check for correctness.
     * @return true if the position is free and within the bounds of this
     *         container.
     */
    private boolean checkPositioning(final Point pPosition)
    {
        boolean success = true;

        success &= checkFree(pPosition);
        success &= checkBounds(pPosition);

        return success;
    }

    /**
     * Check that a given position is not already being used within the
     * container.
     * 
     * @param pPosition
     *            the relative position to check is free.
     * @return true if the position is free, else false.
     */
    private boolean checkFree(final Point pPosition)
    {
        boolean isFree = true;

        for (Block block : mBlocks)
        {
            if (block.getRelativePosition() == pPosition)
            {
                isFree = false;
                break;
            }
        }

        return isFree;
    }

    /**
     * Check that a given position is within the bounds of this container.
     * 
     * @param pPosition
     *            the position, relative to this container, to check if is
     *            within the bounds.
     * @return true if the position is within the bounds, else false.
     */
    private boolean checkBounds(final Point pPosition)
    {
        boolean isWithinBounds = true;

        if (pPosition.x < 0 || pPosition.x > mDimensions.x)
        {
            isWithinBounds = false;
        }

        if (pPosition.y < 0 || pPosition.y > mDimensions.y)
        {
            isWithinBounds = false;
        }

        return isWithinBounds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getSoftwareCost()
     */
    @Override
    public double getSoftwareCost()
    {
        double cost = 0;

        for (Block block : mBlocks)
        {
            cost += block.getSoftwareCost();
        }

        return cost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getNumCPUs()
     */
    @Override
    public int getNumCPUs()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#distributeIPs(int, int, int, int)
     */
    @Override
    int distributeIPs(final int pSubBlock, final int pBlock, final int pDC, final int pNumServersSeen)
    {
        logger.error("WARNING: A container has been found inside another aisle/container.");
        return 0;
    }

    @Override
    public int calcNetworkDistance(int pServerID)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int calcNetworkDistance(int pServerIDA, int pServerIDB)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean contains(Server pServer)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getNumFailedServers()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getNumRacks()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getPower()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getPurchaserCost()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Server getServer(int pID)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Calculate the position of an air conditioner unit within a container
     * 
     * @param aircon - the air con unit
     * 
     * @return - the position of the aircon unit
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
     * 
     * @see sim.physical.Block#getAbsolutePosition()
     */
    @Override
    public Point getAbsolutePosition()
    {
        return mRelativePosition;
    }
}
