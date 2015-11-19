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

import sim.physical.CPU;
import sim.physical.Harddisk;
import sim.physical.RAM;

/**
 * TODO
 */
public class ServerType implements Comparable<ServerType>
{
    // ///**** Member variables ****\\\ \\

    // Server specification variables.
    private final CPU[]    mCPUs;
    private final RAM      mRAM;
    private final Harddisk mDisk;
	private final long     mSize;
    private final long     mMeanFailTime;
    private final String   mModel;
    private final double   mDefaultPowerConsumed;
    private final double   mMaxPowerConsumed;
    private final double   mPurchaseCost;

    // Other variables.
    private final long     mTimeAvailableFrom;

    /**
     * TODO
     * 
     * @param pCPUs
     * @param pRAM
     * @param pHarddisk
     * @param pMeanFailTime
     * @param pSize 
     * @param pModel
     * @param pDefaultPowerConsumption
     * @param pMaxPowerConsumed
     * @param pPurchaseCost
     * @param pTimeAvailableFrom
     */
    public ServerType(final CPU[] pCPUs, final RAM pRAM, final Harddisk pHarddisk, final long pMeanFailTime, long pSize, final String pModel, final double pDefaultPowerConsumption, final double pMaxPowerConsumed, final double pPurchaseCost, final long pTimeAvailableFrom)
    {
        mCPUs = pCPUs;
        mRAM = pRAM;
        mDisk = pHarddisk;
        mSize = pSize;
        mMeanFailTime = pMeanFailTime;
        mModel = pModel;
        mDefaultPowerConsumed = pDefaultPowerConsumption;
        mMaxPowerConsumed = pMaxPowerConsumed;
        mPurchaseCost = pPurchaseCost;
        mTimeAvailableFrom = pTimeAvailableFrom;
    }

    /**
     * 
     * Check to see if this server type is available
     * 
     * @param pCurrentTime - the current simulation time
     * 
     * @return true if this server type is available, false otherwise
     */
    public boolean isAvailable(final long pCurrentTime)
    {
        boolean isAvailable = false;

        if (pCurrentTime >= mTimeAvailableFrom)
        {
            isAvailable = true;
        }

        return isAvailable;
    }

    /**
     * Get all CPUs for this server type
     * 
     * @return array of CPUs
     */
    public CPU[] getCPUs()
    {
        return mCPUs;
    }

    /**
     * Get the RAM for this server type
     * 
     * @return - RAM
     */
    public RAM getRAM()
    {
        return mRAM;
    }

    /**
     * Get the harddist for this server type
     * 
     * @return the Harddisk
     */
    public Harddisk getHarddisk()
    {
        return mDisk;
    }
    
    /**
     * Get the size of this server type
     * @return the size
     */
    public long getSize()
    {
        return mSize;
    }

    /**
     * Get the mean fail time of this server type
     * 
     * @return the mean fail time
     */
    public long getMeanFailTime()
    {
        return mMeanFailTime;
    }

    /**
     * Get the model name of this server type
     * 
     * @return name
     */
    public String getModelName()
    {
        return mModel;
    }

    /**
     * Get the default power consumption for this server type
     * 
     * @return the default power consumption
     */
    public double getDefaultPowerCunsumption()
    {
        return mDefaultPowerConsumed;
    }

    /**
     * Get the maximum power consumption for this server type
     * 
     * @return max power consumption
     */
    public double getMaxPowerConsumption()
    {
        return mMaxPowerConsumed;
    }

    /**
     * Get the purchase cost of this ServerType
     * 
     * @return purchase cost
     */
    public double getPurchaseCost()
    {
        return mPurchaseCost;
    }

    /**
     * Get the time that this server type is available from
     * 
     * @return the time that this server type is available
     */
    public long getTimeAvailable()
    {
        return mTimeAvailableFrom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ServerType pType)
    {
        final int result;

        if (this.mTimeAvailableFrom < pType.mTimeAvailableFrom)
        {
            result = -1;
        }
        else if (this.mTimeAvailableFrom > pType.mTimeAvailableFrom)
        {
            result = 1;
        }
        else
        {
            result = 0;
        }

        return result;
    }
}
