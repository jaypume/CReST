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

import sim.module.failure.bo.Failable;
import sim.module.failure.event.FailureEvent.FailType;
import sim.physical.network.IP;

/**
 * Class to represent an air conditioning unit in a datacentre, for extracting
 * heat from the environment.
 */
public class AirConditioner extends Block implements Failable
{
	private static Logger logger = Logger.getLogger(AirConditioner.class);
	
    // Constants //
    private static final double DESIRED_TEMPERATURE   = 25.0;
    private static final double TEMPERATURE_POTENTIAL = -500.0;

    // Member variables //
    private IP                  mIP;
    private int                 mID;
    private final double        mDesiredTemperture;
    private final double        mExtractionPotential;
    private ArrayList<Point>    mExtractionVents      = new ArrayList<Point>();
    private long                mMeanFailTime;
    
    // Failure
    private boolean              mIsAlive                         = true;
    private FailType             mFailureType                     = FailType.fix;

    /**
     * Basic constructor.
     */
    public AirConditioner(final Point pRelativePosition, final long pMeanFailTime)
    {
        super(Type.aircon, pRelativePosition, new Point(1, 1));

        // TODO Read this in from config.
        mDesiredTemperture = DESIRED_TEMPERATURE;
        mExtractionPotential = TEMPERATURE_POTENTIAL;
        mMeanFailTime = pMeanFailTime;
    }

    /**
     * TODO
     * 
     * @param pPosition
     */
    public void addExtractionVent(final Point pPosition)
    {
        mExtractionVents.add(pPosition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#addBlock(sim.physical.Block)
     */
    @Override
    public boolean addBlock(Block pBlock)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#calcNetworkDistance(int)
     */
    @Override
    public int calcNetworkDistance(int pServerID)
    {
        // TODO Should these exist?
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#calcNetworkDistance(int, int)
     */
    @Override
    public int calcNetworkDistance(int pServerIDA, int pServerIDB)
    {
        // TODO Should these exist?
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#contains(sim.physical.Server)
     */
    @Override
    public boolean contains(Server pServer)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getNumServers()
     */
    @Override
    public int getNumFailedServers()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getNumFailedServers()
     */
    @Override
    public int getNumRacks()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getNumRacks()
     */
    @Override
    public int getNumServers()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getPower()
     */
    @Override
    public double getPower()
    {
        // TODO Code for calculating the power cost of this air conditioning
        // unit.
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getPurchaserCost()
     */
    @Override
    public double getPurchaserCost()
    {
        // TODO Code for calculating purchase costs associated with this aircon
        // unit.
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getServer(int)
     */
    @Override
    public Server getServer(int pID)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getServer(sim.physical.IP)
     */
    @Override
    public Server getServer(IP pIP)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getSoftwareCost()
     */
    @Override
    public double getSoftwareCost()
    {
        return 0.0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#distributeIPs(int, int)
     */
    @Override
    public int distributeIPs(final int pBlock, final int pDC, final int pNumServersSeen)
    {
        distributeIPs(0, pBlock, pDC, pNumServersSeen);

        return pNumServersSeen;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#distributeIPs(int, int, int, int)
     */
    @Override
    int distributeIPs(final int pSubBlock, final int pBlock, final int pDC, final int pNumServersSeen)
    {
        setIP(pSubBlock, pBlock, pDC);

        return pNumServersSeen;
    }

    /**
     * Set the IP of this air conditioning unit given the various block levels.
     * 
     * @param pSubBlock
     *            the sub-block that the air conditioning unit is contained in
     *            (may not be needed).
     * @param pBlock
     *            the block that the air conditioning unit is contained in.
     * @param pDC
     *            the datacentre that the air conditioning unit is contained in.
     */
    private void setIP(final int pSubBlock, final int pBlock, final int pDC)
    {
        mIP = IP.create(0, pSubBlock, pBlock, pDC);
    }
    
    /**
     * Sets the unique ID of this air conditioning unit.
     * @param id
     */
    public void setID(int id)
    {
    	mID = id;
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

    /**
     * TODO
     */
    @Override
    public boolean contains(final IP pIP)
    {
        boolean doesContain = mIP.equals(pIP);

        return doesContain;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.physical.Block#getAbsolutePosition()
     */
    @Override
    public Point getAbsolutePosition()
    {
        Datacentre dc = World.getInstance().getDatacentre(mIP);
        Point absPosition = dc.calcAbsolutePosition(this);

        return absPosition;
    }

    /**
     * Get the IP of this air conditioning unit.
     * 
     * @return the IP of this air conditioning unit.
     */
    @Override
    public IP getIP()
    {
        return mIP;
    }
    
	/**
     * Get the unique ID of this air conditioning unit.
     * 
     * @return the unique ID of this air conditioning unit.
     */
    public int getID()
    {
        return mID;
    }

    /**
	 * Get the extraction temperature of the aircon unit
	 * 
     * @param localTemperture - the local temperature
     * 
     * @return the extraction temperature
     */
    public double getExtractionTemperature(final double localTemperture)
    {
        double extractionTemp;

        if (localTemperture > mDesiredTemperture)
        {
            final double difference = localTemperture - mDesiredTemperture;
            final double desiredExtractionTemp = mDesiredTemperture - (10 * difference);

            if (desiredExtractionTemp < mExtractionPotential)
            {
                extractionTemp = mExtractionPotential;
            }
            else
            {
                extractionTemp = desiredExtractionTemp;
            }
        }
        else
        {
            extractionTemp = localTemperture;
        }

        return extractionTemp;
    }

    /**
	 * Is the air con unit on?
	 * 
     * @param localTemperture - the local temperature
     * @return true if air con unit is on, false otherwise
     */
    public boolean isOn(final double localTemperture)
    {
        final boolean isOn;

        if (localTemperture > mDesiredTemperture && isAlive())
        {
            isOn = true;
        }
        else
        {
            isOn = false;
        }

        return isOn;
    }

    /**
     * TODO
     * 
     * @return
     */
    ArrayList<AirConditioner> getAirCons()
    {
        ArrayList<AirConditioner> airCons = new ArrayList<AirConditioner>();

        airCons.add(this);

        return airCons;
    }

    /**
     * TODO
     */
    public void performExtraction()
    {

    }

    /**
     * Fetches the mean failure time of this air conditioner, as used for
     * failure probability distribution calculations.
     * 
     * @return the mean failure time of this air conditioner.
     */
    public long getMeanFailTime()
    {
        return mMeanFailTime;
    }
    
    /**
     * Sets IsAlive of the aircon
     * @param state
     */
    public void setIsAlive(boolean state)
    {
        mIsAlive = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.failure.Failable#isAircon()
     */
    @Override
    public boolean isAircon()
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.failure.Failable#isServer()
     */
    @Override
    public boolean isServer()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#isAlive()
     */
    @Override
    public boolean isAlive()
    {
        return mIsAlive;
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#isBroken()
     */
    @Override
    public boolean isBroken()
    {
        return !isAlive();
    }

    /**
     * Returns the failure type of the aircon
     */
	@Override
	public FailType getFailType()
	{
		return mFailureType;
	}
    
    /**
     * Sets the failure type of the aircon
     * @param failType the failure type of the aircon
     */
    public void setFailType(FailType failType)
    {
    	mFailureType = failType;
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#performFailure(sim.event.FailureEvent.Type)
     */
    @Override
    public void performFailure(final FailType pFailureType)
    {
    	mFailureType = pFailureType;
        mIsAlive = false;
    }

    /*
     * (non-Javadoc)
     * @see sim.failure.Failable#performFix()
     */
    @Override
    public void performFix()
    {
        if (mFailureType == FailType.soft)
        {
	        mIsAlive = true;
	        mFailureType = FailType.fix;
	        logger.info("Aircon fixed: " + this);
        }
    }
    
    public String toString() {
    	return "[AirCon: " + this.mIP + " ID: " + this.mID + " status=" + ((this.mIsAlive)?"alive":"broken" + "]");  
    }
}
