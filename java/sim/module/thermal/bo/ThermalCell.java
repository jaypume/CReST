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
 * Created on 18 Aug 2011
 */
package sim.module.thermal.bo;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import sim.module.Module;
import sim.physical.AirConditioner;
import sim.physical.Server;
import sim.physical.World;
import sim.physical.network.IP;
import utility.direction.CompassDirection;
import utility.physics.Velocity;

/**
 * Class to represent a single cell of a temperature grid. Each cell contains a
 * number of servers which are used to periodically update the temperature of the
 * cell, based on their heat output.
 */
public class ThermalCell
{

	public static Logger logger = Logger.getLogger(ThermalCell.class);
	
    double         mTemperature;
    Velocity velocity;  
    
    ArrayList<IP>  mIPs    = new ArrayList<IP>();
    AirConditioner mAirCon = null;

    boolean rackWall = false; //If true, cell contains a rack and acts as a wall.
    
    
    /**
     * Constructor.
     */
    private ThermalCell()
    {
        mTemperature = 0.0;
        velocity = new Velocity();
    }

    /**
     * Create and return a new temperature cell object.
     * 
     * @return the new temperature cell object.
     */
    public static ThermalCell create()
    {
        return new ThermalCell();
    }


    /**
     * Set this thermal cell to be a rack-wall
     */
    public void setRackWall(boolean isRackWall) {
    	rackWall = isRackWall;
    }
    
    /**
     * Is the thermal cell a rack wall?
     * @return
     */
    public boolean isRackWall() {
    	return rackWall;
    }
    
    /**
     * Add a server to this cell via a reference to it (its IP). The output
     * temperature of the server will then be used to update the temperature of
     * this cell.
     * 
     * @param pIP
     *            the IP of the server being added to this cell.
     */
    public void addIP(final IP pIP)
    {
        mIPs.add(pIP);
    }

    /**
     * TODO
     * 
     * @param pAirCon
     */
    public void addAirConditioner(final AirConditioner pAirCon)
    {
        if (mAirCon == null)
        {
            mAirCon = pAirCon;
        }
        else
        {
            logger.error("Error: Attempting to add an additional Aircon to a temperature cell that already has one.");
        }
    }

    /**
     * Set the current temperature of this cell.
     * 
     * @param pTemperature
     *            the new temperature.
     */
    public void setTemperature(final double pTemperature)
    {
        mTemperature = pTemperature;
    }

    /**
     * Get the current temperature of this cell.
     * 
     * @return the current temperature of this cell.
     */
    public double getTemperature()
    {
        return mTemperature;
    }

    public Velocity getVelocity() {
    	return velocity;
    }
    
    public void setVelocity(Velocity v) {
    	velocity = v;
    }
    
    public void addVelocity(Velocity v) {
    	velocity.add(v);
    }
    
    /**
     * Increment the temperature of this cell by the given amount.
     * 
     * @param pIncrement
     *            the amount to increment the current temperature by.
     */
    public void incrementTemperature(final double pIncrement)
    {
        mTemperature += pIncrement;
    }

    /**
     * Decrement the temperature of this cell by the given amount.
     * 
     * @param pDecrement
     *            the amount to decrement the current temperature by.
     */
    public void decrementTemperature(final double pDecrement)
    {
        mTemperature -= pDecrement;
    }

    /**
     * Update the temperature in this cell by gathering the output heat from
     * each server contained within it.
	 *
     */
    public void updateTemperature()
    {
        doHeatGeneration();
        doExhaustVents();
    }

    /**
     * Perform heat generation for this cell from the heat output of each server
     * contained within it.
     * 
     * @param pDuration
     *            the duration of time that has passed since the last update.
     */
    private void doHeatGeneration()
    {
        final int numServers = mIPs.size();

        if (numServers > 0)
        {
            World world = World.getInstance();
            double oldTotal = 0;
            double newTotal = 0;

            for (IP ip : mIPs)
            {
                Server server = world.getServer(ip);
                oldTotal += server.getTemperature();
                newTotal += server.updateTemperature();
            }

            final double averageServerTemp = (oldTotal + newTotal) / (2 * mIPs.size());

            // System.out.println("--TemperatureCell: doHeatGeneration()");
            // System.out.println("Old Temp: " + oldTotal);
            // System.out.println("New Temp: " + newTotal);
            // System.out.println("Average Temp: " + averageServerTemp);

            final double transfer = calcTransfer(mTemperature, averageServerTemp);

            if (Double.valueOf(averageServerTemp).isNaN())
            {
                logger.error("TemperatureCell.doHeatGeneration(): averageServerTemp is NaN.");
                System.exit(-1);
            }

            if (Double.valueOf(transfer).isNaN())
            {
                logger.error("TemperatureCell.doHeatGeneration(): transfer is NaN.");
                System.exit(-1);
            }

            mTemperature += transfer;

            for (IP ip : mIPs)
            {
                Server server = world.getServer(ip);
                server.incrementTemperature(-transfer);

                if (mTemperature > server.getFailureTemperature() && Module.FAILURE_MODULE.isActive())
                {
                    server.spawnFailureEvent();
                }
            }
        }
    }

    /**
     * Air condition this cell if it has an exhaust vent in it.
     * 
     * @param pDuration
     *            the duration of time that has passed since the last update.
     */
    private void doExhaustVents()
    {
        if (this.hasAirCon())
        {
            if (mAirCon.isOn(mTemperature))
            {
                mTemperature += calcTransfer(mTemperature, mAirCon.getExtractionTemperature(mTemperature));
            }
        }
    }

    /**
     * Does this thermal cell contain an air con unit?
     * 
     * @return true if it does, false otherwise
     */
    public boolean hasAirCon()
    {
        boolean hasAirCon = false;

        if (mAirCon != null)
        {
            hasAirCon = true;
        }

        return hasAirCon;
    }

    /**
     * Calculate the transfer of heat from the adjacent cell to this cell.
     * 
     * @param pAdjacentTemp
     *            the adjacent cell.
     * @param pDuration
     *            the time that has passed since the last temperature update.
     * 
     * @return the transfer of heat from adjacent cell to local cell.
     */
    public double calcTransfer(final ThermalCell pAdjacentCell)
    {
    	if(this.isRackWall()) {
    		logger.debug("This cell is rack wall. Not transferring heat...");
    		return 0;
    	} else {
    		if(pAdjacentCell.isRackWall()) {
    			logger.debug("Adjacent cell is rack wall  - not transferring heat...");
    			return 0;
    		} else {
    			return calcTransfer(mTemperature, pAdjacentCell.getTemperature());
    		}
    	}
    }
    
//    /**
//     * Calculate the transfer of velocity from the adjacent cell to this cell.
//     * 
//     * @param pAdjacentTemp
//     *            the adjacent cell.
//     * @param relativeAdjacentLocation
//     *            the location of the adjacent cell relative to this cell.  
//     * 
//     * @return the transfer of velocity from adjacent cell to local cell.
//     */
//    Velocity calcVelocityTransfer(final ThermalCell pAdjacentCell, CompassDirection relativeAdjacentLocation)
//    {
//    	
//    	Velocity velTransfer = velocity.copy();
//    	
//    	if(this.isRackWall()) {
//    		logger.warn("This cell is rack wall. Not transferring velocity...");
//    		return new Velocity();
//    	} else {
//    		if(pAdjacentCell.isRackWall() || pAdjacentCell == null) {
//    			logger.warn("Adjacent cell is wall ? Relative location is " + relativeAdjacentLocation);
//    			
//    			if(relativeAdjacentLocation != null) {
//        			switch(relativeAdjacentLocation) {
//        			
//        			case NORTH:
//        			case SOUTH:
//        				velTransfer.reflectY();
//        				break;
//        			case EAST:
//        			case WEST:
//        				velTransfer.reflectX();
//        				break;
//        			}
//    			} else {
//    				logger.warn("Adjacent cell is diagonal, so ignoring...");
//    			}
//
//    			return velocity;
//    		} else {
//    			return calcTransfer(mTemperature, pAdjacentCell.getTemperature());
//    		}
//    	}
//    }

    /**
     * Calculate the transfer of heat from the adjacent cell to this cell.
     * 
     * @param pLocalTemp
     *            the local cell's temperature.
     * @param pAdjacentTemp
     *            the adjacent cell's temperature.
     * @param pDuration
     *            the time that has passed since the last temperature update.
     * 
     * @return the transfer of heat from adjacent cell to local cell.
     */
    private static double calcTransfer(final double pLocalTemp, final double pAdjacentTemp)
    {
        // final double ratio = (0.1 / TimeManager.secondsToSimulationTime(1)) *
        // TimeManager.simulationTimeToSeconds(pDuration);
        // final double ratio = 1.0 / TimeManager.secondsToSimulationTime(1);
        final double ratio = 1.0 / 60.0;
        final double difference = pAdjacentTemp - pLocalTemp;
        final double transfer = ratio * difference;

        // if (pAdjacentTemp > 30)
        // {
        // System.out.println("==================");
        // System.out.println("--pDuration: " + pDuration);
        // System.out.println("--pLocalTemp: " + pLocalTemp);
        // System.out.println("--pAdjacentTemp: " + pAdjacentTemp);
        // System.out.println("--ratio: " + ratio);
        // System.out.println("--difference: " + difference);
        // System.out.println("--transfer: " + transfer);
        // }

        return transfer;
    }
}
