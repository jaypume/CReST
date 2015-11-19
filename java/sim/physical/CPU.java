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
 * @created 7 Jul 2011
 */
package sim.physical;

/**
 * Class representing a CPU.
 */
public class CPU
{
    private final String mModel;
    private final int    mSpeed;       // MHz
    private final int    mNumCores;
    private double       mUtilisation; // between 0-1

    /**
     * Default constructor to create a new preset CPU.
     */
    public CPU()
    {
        mModel = "default";
        mSpeed = 2000;
        mNumCores = 4;
        mUtilisation = 0;
    }

    /**
     * Create a CPU with the given parameters.
     * 
     * @param pModel
     *            the model to create.
     * @param pSpeed
     *            the speed of each core.
     * @param pCores
     *            the number of cores this CPU has.
     */
    public CPU(String pModel, int pSpeed, int pCores)
    {
        mModel = pModel;
        mSpeed = pSpeed;
        mNumCores = pCores;
        mUtilisation = 0;
    }

    /**
     * Method to return useful information about this CPU.
     * 
     * @return A String of useful information about this CPU.
     */
    public String toString()
    {
        String string;

        string = "Model: " + mModel + ", Speed: " + mSpeed + " MHz, Cores: " + mNumCores + ", Utilisation: " + mUtilisation;

        return string;
    }

    public String getModel()
    {
    	return mModel;
    }
    
    public int getSpeed()
    {
    	return mSpeed;
    }
        
    /**
	 * Checks whether the service can run on this CPU or not. Returns true if it
	 * can or returns false if the CPU is at full utilisation already
	 * 
	 * @param utilIncrease
	 *            the percentage CPU utilisation for this service
	 * @return whether the service will run on this CPU
	 */
    public boolean serviceWillRun(double utilIncrease)
    {
    	 if (mUtilisation + utilIncrease > 1)
         {
             return false;
         }
    	 else if (mUtilisation + utilIncrease < 0)
         {
             return false;
         }
    	 else
    	 {
    		 return true;
    	 }
    }
    
    /**
     * Adjusts the CPU utilisation when a service is started or stopped
     * 
     * @param change
     *            the percentage CPU utilisation for this service
     */
    public void adjustUtilisation(double change)
    {
        mUtilisation += change;
    }

	/**
	 * Sets the CPU utilisation to a specific value
	 * NOTE: you will normally require the method adjustUtilisation()
	 * This method s only used when a server dies and everything must be stopped.
	 * 
	 * see adjustUtilisation
	 * 
	 * @param value the value to set the CPU utilisation at
	 */
    public void setUtilisation(double value)
    {
    	mUtilisation = value;
    }

    /**
     * Returns the number of cores in this CPU
     * 
     * @return the number of cores in this CPU
     */
    public int getNumCores()
    {
        return mNumCores;
    }

	/**
	 * Returns the CPU Utilisation
	 * 
	 * @return the CPU Utilisation
	 */
	public double getCPUUtilisation()
	{
		return mUtilisation;
	}
}
