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
package sim.module.thermal.model;

import org.apache.log4j.Logger;

import sim.module.thermal.bo.ThermalCell;
import sim.module.thermal.bo.ThermalGrid;
import sim.module.thermal.bo.ThermalGrid.Dimension;

/**
 * A very simple diffusion model, originally written by Luke Drury (Summer 2011).
 * 
 * Heat diffuses across all 8 neighbouring cells proportional to the simple temperature gradient between cells.
 *
 */
public class SimpleDiffusionModel extends AbstractThermalModel {

	public static Logger logger = Logger.getLogger(SimpleDiffusionModel.class);
	
	ThermalCell[][] mGrid;

	public SimpleDiffusionModel(String name) { super(name); }
	
	@Override
	/**
	 * A very simple diffusion model, originally written by Luke Drury (Summer 2011).
	 * 
	 * Heat diffuses across all 8 neighbouring cells proportional to the simple temperature gradient between cells.
	 * 
	 */
	public void update(ThermalGrid grid) {
		
		mGrid = grid.getGrid();
        gatherHeatOutput();
        propagateTemperature();
	}

    /**
     * Gather the heat generated in each cell of the grid by the servers present
     * in each of the cells.
     * 
     * @param pDuration
     *            the duration of time that has passed since the last update.
     */
    protected void gatherHeatOutput()
    {
        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                mGrid[x][y].updateTemperature();
            }
        }
    }
    
    /**
     * Perform a propagation step to distribute existing heat across the grid.
     * 
     * @TODO Only does 2D propagation.
     * 
     * @param pDuration
     *            the duration of time that has passed since the last update.
     */
    protected void propagateTemperature()
    {
        final ThermalCell[][] newGrid = mGrid.clone();
        
        
        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
                final int yNorth = wrapToGrid(ThermalGrid.Dimension.Y, y + 1);
                final int xEast = wrapToGrid(ThermalGrid.Dimension.X, x + 1);
                final int ySouth = wrapToGrid(ThermalGrid.Dimension.Y, y - 1);
                final int xWest = wrapToGrid(ThermalGrid.Dimension.X, x - 1);

                final double changeN = mGrid[x][y].calcTransfer(mGrid[x][yNorth]);
                final double changeE = mGrid[x][y].calcTransfer(mGrid[xEast][y]);
                final double changeS = mGrid[x][y].calcTransfer(mGrid[x][ySouth]);
                final double changeW = mGrid[x][y].calcTransfer(mGrid[xWest][y]);
                final double changeNE = mGrid[x][y].calcTransfer(mGrid[xEast][yNorth]);
                final double changeSE = mGrid[x][y].calcTransfer(mGrid[xEast][ySouth]);
                final double changeSW = mGrid[x][y].calcTransfer(mGrid[xWest][ySouth]);
                final double changeNW = mGrid[x][y].calcTransfer(mGrid[xWest][yNorth]);

                newGrid[x][y].incrementTemperature(changeN);
                newGrid[x][y].incrementTemperature(changeE);
                newGrid[x][y].incrementTemperature(changeS);
                newGrid[x][y].incrementTemperature(changeW);
                newGrid[x][y].incrementTemperature(changeNE);
                newGrid[x][y].incrementTemperature(changeSE);
                newGrid[x][y].incrementTemperature(changeSW);
                newGrid[x][y].incrementTemperature(changeNW);

                newGrid[x][yNorth].decrementTemperature(changeN);
                newGrid[xEast][y].decrementTemperature(changeE);
                newGrid[x][ySouth].decrementTemperature(changeS);
                newGrid[xWest][y].decrementTemperature(changeW);
                newGrid[xEast][yNorth].decrementTemperature(changeNE);
                newGrid[xEast][ySouth].decrementTemperature(changeSE);
                newGrid[xWest][ySouth].decrementTemperature(changeSW);
                newGrid[xWest][yNorth].decrementTemperature(changeNW);
            }
        }

        mGrid = newGrid.clone();
    }
    
    /**
     * Given a coordinate that is out of the bounds of this grid, wrap it around
     * to the other side of the grid.
     * 
     * @param pDimension
     *            the axis along which we are wrapping the given point, i.e. X,
     *            or Y.
     * @param pCoordinate
     *            the coordinate to wrap.
     * @return the wrapped coordinate.
     */
    protected int wrapToGrid(final Dimension pDimension, final int pCoordinate)
    {
        int wrapped = pCoordinate;

        switch (pDimension)
        {
            case X:
            {
                if (pCoordinate >= mGrid.length)
                {
                    wrapped = mGrid.length - 1;
                }
                else if (pCoordinate < 0)
                {
                    wrapped = 0;
                }

                break;
            }
            case Y:
            {
                if (pCoordinate >= mGrid[0].length)
                {
                    wrapped = mGrid[0].length - 1;
                }
                else if (pCoordinate < 0)
                {
                    wrapped = 0;
                }

                break;
            }
            default:
            {
                logger.error("Dimensions type is not valid: it should be X or Y.");
                wrapped = -1;
                break;
            }
        }

        return wrapped;
    }
}
