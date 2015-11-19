/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
 * Created on 11 Aug 2011
 */
package sim.module.thermal.bo;

import java.awt.Point;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sim.module.log.Log;
import sim.module.thermal.configParams.ThermalModuleConfigParams;
import sim.module.thermal.model.AbstractThermalModel;
import sim.module.thermal.model.ThermalModelFactory;
import sim.physical.AirConditioner;
import sim.physical.Datacentre;
import sim.physical.Server;
import sim.physical.network.IP;
import utility.direction.CompassDirection;
import utility.time.TimeManager;

/**
 * Class to hold the temperature of a datacentre as a 2D grid of cells.
 * 
 * Each cell will hold series of servers which will output heat into the cells.
 */
public class ThermalGrid
{
	public static Logger logger = Logger.getLogger(ThermalGrid.class);
	private DecimalFormat df = new DecimalFormat("##.00");
	
    private static final long ITERATION_PERIOD = TimeManager.secondsToSimulationTime(1);
    public static final double DEFAULT_TEMPERATURE = 20.0;

    protected AbstractThermalModel thermalModel;
    
    /**
     * Enumerated data-type to refer to a particular axis/dimension direction of
     * the cells.
     */
    public enum Dimension
    {
        X, Y
    }

    private ThermalCell[][] mGrid;
    protected final double[]    mCellDimensions;
    private long                mTimeLastUpdate = 0;
    private long                mExcessTime      = 0;

    protected int 				resolution = 1; //TODO - so we dont have a 1-1 mapping.
    
    /**
     * Constructor for creating a server-less temperature grid.
     * 
     * @param pDCDimensions
     *            the 2D physical dimensions of the owning datacentre (metres,
     *            {x,y}).
     *            
     * @param pDCCellDimensions
     *            the arbitrary dimensions of the temperature grid in number of
     *            cells (x,y).
     */
    public ThermalGrid(ThermalModuleConfigParams params, final int[] pDCDimensions, 
    		final double[] pDCCellDimensions, int resolution)
    {
        initialiseGrid(pDCDimensions);
        mCellDimensions = initialiseDimensions(pDCDimensions, pDCCellDimensions);
        
        thermalModel = ThermalModelFactory.getModel(params.getThermalModelType()); 
        
        logger.warn("Configured Thermal Grid using model: '" + thermalModel.name + "'");
    }

    /**
     * Initialise the grid by created a new cell object in each of the 2D cells.
     * 
     * @param pTempGridDimensions
     *            the dimensions of the grid in number of cells along each axis
     *            (x,y).
     */
    private void initialiseGrid(final int[] pDCDimensions)
    {
        final int tempX = pDCDimensions[0];
        final int tempY = pDCDimensions[1];

        mGrid = new ThermalCell[tempX][tempY];

        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                mGrid[x][y] = ThermalCell.create();
                mGrid[x][y].setTemperature(DEFAULT_TEMPERATURE);
            }
        }
    }
    
    public ThermalCell[][] getGrid() {
    	return mGrid;
    }

    public void setGrid(ThermalCell[][] grid) {
    	mGrid = grid;
    }
    
    /**
     * Calculate the desired dimension of each cell (metres, {x,y}).
     * 
     * @param pDCDimensions
     *            the 2D physical dimensions of the owning datacentre (metres,
     *            {x,y}).
     * @param pTempGridDimensions
     *            the arbitrary dimensions of the grid in number of cells per
     *            axis (x,y).
     * 
     * @return the dimensions of a cell along each axis (metres, {x,y}).
     */
    private static double[] initialiseDimensions(final int[] pDCDimensions, final double[] pDCCellDimensions)
    {
        final double x = pDCDimensions[0] * pDCCellDimensions[0];
        final double y = pDCDimensions[1] * pDCCellDimensions[1];

        return new double[]
        { x, y };
    }

    /**
     * Create a log of the data from this temperature grid. Only logs the 2D
     * representation of the grid.
     * 
     * @return a log of the data from within this grid.
     */
    public Log toLog()
    {
        Log log = new Log();

        // log.add(String.valueOf(World.getInstance().getTime()));

        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                log.add(String.valueOf(mGrid[x][y].getTemperature()));
            }
        }

        return log;
    }

    /**
     * Perform a temperature update up to the current time.
     * 
     * @param pTime
     *            the current world time.
     */
    public void update(final long pTime)
    {
        final long duration = pTime - mTimeLastUpdate;
        final int numIterations = calcNumIterations(duration);
        
        logger.debug("Updating. numIterations = " + numIterations);
        
//        System.out.println("==========================================");
//        System.out.println("--TemperatureGrid:update()");
//        System.out.println("--Num iterations: " + numIterations);
        
        for (int i = 0; i < numIterations; i++)
        {
        	thermalModel.update(this); //TODO - this should replace the following two lines. Check. !!! 
            //gatherHeatOutput();
            //propagateTemperature();
        }
        
        mTimeLastUpdate = pTime;
    }
    
    private int calcNumIterations(final long pDuration)
    {
        mExcessTime += pDuration % ITERATION_PERIOD;
        final int numIterations = (int) ((pDuration + mExcessTime) / ITERATION_PERIOD);
        mExcessTime = mExcessTime % ITERATION_PERIOD;
        
        return numIterations;
    }

    /**
     * TODO
     * 
     * @param pDC
     */
    public void fill(Datacentre pDC)
    {
        fillWithServers(pDC);
        fillWithAirCons(pDC);
    }

    /**
     * Fill this temperature grid with servers from the given datacentre.
     * 
     * @param pDC
     *            the datacentre from which to added the servers from.
     */
    void fillWithServers(Datacentre pDC)
    {
        final int numServers = pDC.getNumServers();

        for (int i = 0; i < numServers; i++)
        {
            addServerToGrid(pDC.getServer(i));
        }
    }

    /**
     * TODO
     * 
     * @param pDC
     */
    void fillWithAirCons(Datacentre pDC)
    {
        final AirConditioner[] airCons = pDC.getAirCons().toArray(new AirConditioner[0]);

        for (int i = 0; i < airCons.length; i++)
        {
            addAirConToGrid(airCons[i]);
        }

        // System.out.println("Done adding #" + airCons.length +
        // " aircon units.");
    }

    /**
     * Add the given server to the temperature grid, determining which cell it
     * belongs in.
     * 
     * @param pServer
     *            the server to add to the temperature grid.
     * @return the coordinates of the cell that the server was added to (x,y).
     */
    private void addServerToGrid(Server pServer)
    {
    	
    	// The old way. Now, we add a wall where the server exists and put the server name on the rack exhaust
    	// NOTE: If rack faces North, exhaust is South.
    	
    	logger.debug("Server position is: " + pServer.getAbsolutePosition());
    	
        final Point cellID = determine2DCellFromPosition(pServer.getAbsolutePosition());
        final IP ip = pServer.getIP();

        //mGrid[cellID.x][cellID.y].addIP(ip); //TODO -  DO NOT DO THIS, use exhaust cell instead
        
        // set the cell to be a rack-wall
        mGrid[cellID.x][cellID.y].setRackWall(true);
        
        // add the IPs to the thermal cell at the REAR of the rack
        CompassDirection rear = pServer.getDirection().getOpposite();       
        final Point exhaustCellID = getNeighbouringCellID(cellID, rear);
        mGrid[exhaustCellID.x][exhaustCellID.y].addIP(ip);
        
        logger.info("Server cell location is; " + cellID + ", Server faces: " + pServer.getDirection() +
        		", Server exhaust: " + rear + ", Server exhaust cell is: " + exhaustCellID);
    }
    
    /**
     * Get cell ID of a neighbouring cell using compass direction, e.g, get cell id to the North.
     * NOTE: if original cell has a wall to the North, then asking for neighbour to the North will return
     * the neighbour to the south. 
     * 
     * @param cell - the origin cell
     * @param dir - the direction
     * @return cell id of neighbouring cell
     */
    private Point getNeighbouringCellID(Point cell, CompassDirection dir) {
    	
    	int x=0,y=0;
    	
    	logger.debug("Original cell id = " + cell);
    	
    	switch (dir) {

    	case NORTH: 
    		logger.debug("Getting neighbour to the north");
    		x = cell.x;
    		if(cell.y > 0) {
    			y=cell.y-1;
    		} else {
    			y =cell.y+1;
    			logger.debug("Wall to the North of cell (" + cell + "). Returning exhaust to South.");
    		}
    		break;
    	case SOUTH:
    		logger.debug("Getting neighbour to the south");
    		x = cell.x;
    		if(cell.y < (mGrid[cell.x].length-1)) {
    			y=cell.y+1;
    		} else {
    			y =cell.y-1;
    			logger.debug("Wall to the North for cell (" + cell + "). Returning exhaust to North.");
    		}
    		break;   	
    	case EAST:
    		logger.debug("Getting neighbour to the east");
    		y = cell.y;
       		if(cell.x > 0) {
    			x=cell.x-1;
    		} else {
    			x =cell.x+1;
    			logger.debug("Wall to the East of cell (" + cell + "). Returning exhaust to West.");
    		}
    		break;	
    	case WEST:
    		logger.debug("Getting neighbour to the west");
    		y = cell.y;
    		if(cell.x < (mGrid.length-1)) {
    			x=cell.x+1;
    		} else {
    			x =cell.x-1;
    			logger.debug("Wall to the West for cell (" + cell + "). Returning exhaust to East.");
    		}
    		break; 
    	}
    	Point neighbour = new Point(x,y);
    	logger.debug("Neighbour is " + neighbour);
    	return neighbour;
    }
    
    /**
     * TODO
     * 
     * @param pAirCon
     */
    private void addAirConToGrid(AirConditioner pAirCon)
    {
        final Point cellID = determine2DCellFromPosition(pAirCon.getAbsolutePosition());

        mGrid[cellID.x][cellID.y].addAirConditioner(pAirCon);
    }

    /**
     * Determine which cell a point on a 2D grid belongs to.
     * 
     * @param pPosition - the point within the grid
     * 
     * @return an integer array indicating cell location [x,y]
     */
    public Point determine2DCellFromPosition(final Point pPosition)
    {
        final int x = pPosition.x;
        final int y = pPosition.y;
        int[] cellID = new int[2];

        cellID[0] = determine1DCellFromPosition(x, mGrid.length);
        cellID[1] = determine1DCellFromPosition(y, mGrid[x].length);

        return new Point(cellID[0],cellID[1]);
    }

    /**
     * Determine which cell a point belongs to, on a 1D line of cells.
     * 
     * @param pPosition
     *            the point.
     * @param pCellDimension
     *            the dimensions of each cell in the line.
     * @return the cell the point lies on.
     */
    static int determine1DCellFromPosition(final int pPosition, final int pGridLength)
    {
        int ID = -1;

        if (pPosition >= 0 && pPosition < pGridLength)
        {
            ID = pPosition;
        }

        return ID;
    }

    /**
     * Get the average temperature of the thermal grid
     * 
     * @return - average temperature
     */
    public double getAverageTemperature()
    {
        double total = 0;
        int numCells = 0;

        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                total += mGrid[x][y].getTemperature();
                numCells++;
            }
        }
        logger.debug("Total = " + total + " Cells = " + numCells + " Mean = " + total/numCells);
        return total / numCells;
    }

    /**
     * Calculate the cumulative temperature within the thermal grid
     * 
     * @return - cumulative temperature
     */
    public double calcCumulativeTemperature()
    {
        double total = 0;

        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                total += mGrid[x][y].getTemperature();
            }
        }

        return total;
    }

    /**
     * Get the width of the thermal grid
     * 
     * @return width (number of cells)
     */
    public int getWidth()
    {
        return mGrid.length;
    }

    /**
     * Get the height of the thermal grid
     * 
     * @return height (number of cells)
     */
    public int getHeight()
    {
        return mGrid[0].length;
    }

    /**
     * Get a cell within the thermal grid
     * 
     * @param x - x location
     * @param y - y location
     * @return the cell at location (x,y)
     */
    public ThermalCell getCell(final int x, final int y)
    {
        return mGrid[x][y];
    }
    
    public String toString() {
    	
    	String s = "\nTemperature grid: \n";
    	
    	for (int y = 0; y < mGrid[0].length; y++)
        {
    		for (int x = 0; x < mGrid.length; x++)
            {
                s+= df.format(mGrid[x][y].getTemperature()) + " ";
            }
    		s += "\n";
        }
    	
    	s += "\nVelocity grid: \n";
    	
    	for (int y = 0; y < mGrid[0].length; y++)
        {
    		for (int x = 0; x < mGrid.length; x++)
            {
                s+= mGrid[x][y].getVelocity() + " ";
            }
    		s += "\n";
        }
    	return s;
    }
}
