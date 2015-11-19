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
import utility.physics.Velocity;

/**
 * A very simple advection - diffusion model, written by JPC, Dec 2013. 
 * 
 * Heat diffuses across all 8 neighbouring cells proportional to the simple temperature gradient between cells.
 * Each cell also has a velocity (forced advection). 
 *
 */
public class SimpleConvectionModel extends SimpleDiffusionModel {

	public static Logger logger = Logger.getLogger(SimpleConvectionModel.class);
	
	public SimpleConvectionModel(String name) { super(name); }
	
	@Override
	/**
	 * A very simple advection - diffusion model, written by JPC, Dec 2013. 
	 * 
	 * Each cell also has a velocity (forced advection), to model air flow in the datacentre.
	 * 	 
	 * Heat also diffuses across all 8 neighbouring cells proportional to the simple temperature gradient between cells.
	 * The diffusion model is the same as the SimpleDiffusionModel written by Luke Drury (Summer 2011).
	 * @see sim.module.thermal.model.SimpleDiffusionModel 
	 *
	 */
	public void update(ThermalGrid grid) {
			
		logger.info("Updating thermal grid...");
		logger.debug(grid);
		
		mGrid = grid.getGrid();
        gatherHeatOutput();
        propagateTemperature();
	}

	@Override
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
          
        logger.warn("Propagating temperature using advection and diffusion...");
        
        for (int x = 0; x < mGrid.length; x++)
        {
            for (int y = 0; y < mGrid[x].length; y++)
            {
            	boolean northIsWall=false, southIsWall=false, eastIsWall=false, westIsWall=false;
            	
            	logger.debug("Calculating diffusion for cell: (" + x + "," + y + ")");
                final int yNorth = wrapToGrid(ThermalGrid.Dimension.Y, y - 1);
                if (yNorth == y || mGrid[x][yNorth].isRackWall()) northIsWall=true;
                final int xEast = wrapToGrid(ThermalGrid.Dimension.X, x + 1);
                if(xEast == x || mGrid[xEast][y].isRackWall()) eastIsWall=true;
                final int ySouth = wrapToGrid(ThermalGrid.Dimension.Y, y + 1);
                if(ySouth == y || mGrid[x][ySouth].isRackWall()) southIsWall=true;
                final int xWest = wrapToGrid(ThermalGrid.Dimension.X, x - 1);
                if(xWest == x ||  mGrid[xWest][y].isRackWall()) westIsWall=true;

                // Calculate diffusion values using temp gradient between grids.
                
                double changeN = mGrid[x][y].calcTransfer(mGrid[x][yNorth]);
                double changeE = mGrid[x][y].calcTransfer(mGrid[xEast][y]);
                double changeS = mGrid[x][y].calcTransfer(mGrid[x][ySouth]);
                double changeW = mGrid[x][y].calcTransfer(mGrid[xWest][y]);
                double changeNE = mGrid[x][y].calcTransfer(mGrid[xEast][yNorth]);
                double changeSE = mGrid[x][y].calcTransfer(mGrid[xEast][ySouth]);
                double changeSW = mGrid[x][y].calcTransfer(mGrid[xWest][ySouth]);
                double changeNW = mGrid[x][y].calcTransfer(mGrid[xWest][yNorth]);

                
                // Calculate thermal gradient velocity...
                Velocity thermalVelocity = new Velocity();
                double ratio = 1/10.0;
                thermalVelocity.subtractY(changeN*ratio);
                thermalVelocity.subtractX(changeE*ratio);
                thermalVelocity.subtractY(-changeS*ratio);
                thermalVelocity.subtractX(-changeW*ratio);
                
                // Calculate velocities...
                
                final double deflect = 0.25;
                
                logger.debug("Origin (" + x + "," + y + "). Calculating output velocity...");
                Velocity deflectionVelocity = getDeflectionVelocity(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                Velocity inputVelocity = getInputVelocity(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                logger.debug("adding deflection velocity to input velocity...");
                inputVelocity.add(deflectionVelocity);
                logger.debug("total input velocity = " + inputVelocity);
                
                
                logger.debug("Origin (" + x + "," + y + "). Getting neighbouring velocities...");
                Velocity vNorth = getVelNorth(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                Velocity vEast = getVelEast(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                Velocity vSouth = getVelSouth(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                Velocity vWest = getVelWest(mGrid[x][y], mGrid[x][yNorth], mGrid[xEast][y], mGrid[x][ySouth], mGrid[xWest][y],
                		northIsWall,eastIsWall,southIsWall,westIsWall, deflect);
                
                // Use velocity to alter temperature gradient...
                // And pass on some velocity
                double velocity_ratio = 0.4;
                if(vNorth.getY()>0) {
                	logger.debug("Multiplying temp gradient north by: " + vNorth.getY());
                	changeN *= vNorth.getY();
                	mGrid[x][yNorth].getVelocity().addY((vNorth.getY() - mGrid[x][yNorth].getVelocity().getY()) * velocity_ratio);
                }
                if(vEast.getY()>0) {
                	logger.debug("Multiplying temp gradient east by: " + vEast.getY());
                	changeE *= vEast.getY();
                	if(vEast.getY() > mGrid[xEast][y].getVelocity().getX()) {
                		logger.debug("Vel diff = " + (vEast.getY() - mGrid[xEast][y].getVelocity().getX()));
                	}
                	mGrid[xEast][y].getVelocity().addX((vEast.getY() - mGrid[xEast][y].getVelocity().getX()) * velocity_ratio);
                }
                if(vSouth.getY()<0) {
                	logger.debug("Multiplying temp gradient south by: " + vSouth.getY());
                	changeS *= vSouth.getY();
                	mGrid[x][ySouth].getVelocity().addY((-vSouth.getY() - mGrid[x][ySouth].getVelocity().getY()) * velocity_ratio);
                }
                if(vWest.getY()<0) {
                	logger.debug("Multiplying temp gradient west by: " + vWest.getY());
                	changeW *= vWest.getY();
                	mGrid[xWest][y].getVelocity().addX((-vWest.getY() - mGrid[xWest][y].getVelocity().getX()) * velocity_ratio);
                }
                
               // mGrid[x][y].getVelocity().add(thermalVelocity); //TODO - do this later...
                mGrid[x][y].getVelocity().scale(0.99); //TODO - scale (simple friction?)
                

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
                
                if(mGrid[x][y].hasAirCon()) {
                	mGrid[x][y].setVelocity(new Velocity(10,0));
                }
            }
        }

        mGrid = newGrid.clone();
    }
    
	Velocity getInputVelocity(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		Velocity input = new Velocity(0,0);
		
		logger.debug("Calculating input velocity... Origin velocity = " + origin.getVelocity());
		
		if(northIsWall) {
			logger.debug("North is wall, ignoring...");
		} else {
			input.add(north.getVelocity());
		}
		
		if(eastIsWall) {
			logger.debug("East is wall, ignoring...");
		} else {
			input.add(east.getVelocity());
		}
		
		if(southIsWall) {
			logger.debug("South is wall, ignoring...");
		} else {
			input.add(south.getVelocity());
		}
		
		if(westIsWall) {
			logger.debug("West is wall, ignoring...");
		} else {
			input.add(west.getVelocity());
		}
		
		logger.debug("Input velocity = " + input);
		
		return input;
	}
	
	Velocity getDeflectionVelocity(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		Velocity output = origin.getVelocity().copy();
		
		logger.debug("Calculating output velocity... Origin velocity = " + origin.getVelocity());
		
		if(northIsWall && eastIsWall && (output.getY()<0 || output.getX()>0)) {
			
			logger.debug("we have walls to north and west to deflect...");
			output.reflectY();
			output.reflectX();
			
		} else if (eastIsWall && southIsWall && (output.getX()>0 || output.getY()>0)) {
			
			logger.debug("we have walls to south and east to deflect...");
			output.reflectY();
			output.reflectX();
			
		} else if (southIsWall && westIsWall && (output.getX()<0 || output.getY()>0)) {
			
			logger.debug("we have walls to south and west to deflect...");
			output.reflectY();
			output.reflectX();
			
		} else if (westIsWall && northIsWall && (output.getX()<0 || output.getY()<0)) {
			
			logger.debug("we have walls to south and west to deflect...");
			output.reflectY();
			output.reflectX();
			
		} else if(northIsWall && output.getY()<0) {
			logger.debug("we have wall to north to deflect...");
			output.reflectY();
			
		} else if (eastIsWall && output.getX()>0) {
			logger.debug("we have wall to east to deflect...");
			output.reflectX();
			
		} else if (southIsWall && output.getY()>0) {
			logger.debug("we have wall to south to deflect...");
			output.reflectY();
			
		} else if(westIsWall && output.getX()<0) {
			logger.debug("we have wall to west to deflect...");
			output.reflectX();
		}
		
		logger.debug("Deflection velocity = " + output);
		return output;

	}
	
	
	/**
	 * Calculate the velocity to be transferred to the cell immediately North
	 * @param origin - origin cell
	 * @param north - cell north
	 * @param east - cell east
	 * @param south  - cell south
	 * @param west - cell west
	 * @param northIsWall - true if north is a wall, false otherwise
	 * @param eastIsWall - true if east is a wall, false otherwise
	 * @param southIsWall - true if south is a wall, false otherwise
	 * @param westIsWall - true if west is a wall, false otherwise
	 * @param deflect - the proportion of velocity that is deflected due to wall
	 * @return Velocity change due to cell North
	 */
	protected Velocity getVelNorth(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		logger.debug("Getting velocity to be transferred to cell immediately North...");
		
		double forward = origin.getVelocity().getY();
		double right = origin.getVelocity().getX();
		
		Velocity f = getVelocityForNeighbour(origin, north, 
				east, south, west, 
				northIsWall, eastIsWall, southIsWall, 
				westIsWall,forward, right, deflect);

		Velocity v = new Velocity(f.getX(),f.getY());
		logger.debug("Velocity north: " + v);
		
	//	diff = new Velocity(v.getX()+east.getX()
		return v;
	}
	
	/**
	 * Calculate the change in velocity due to the cell to the East
	 * @param origin - origin cell
	 * @param north - cell north
	 * @param east - cell east
	 * @param south  - cell south
	 * @param west - cell west
	 * @param northIsWall - true if north is a wall, false otherwise
	 * @param eastIsWall - true if east is a wall, false otherwise
	 * @param southIsWall - true if south is a wall, false otherwise
	 * @param westIsWall - true if west is a wall, false otherwise
	 * @param deflect - the proportion of velocity that is deflected due to wall
	 * @return Velocity change due to cell North
	 */
	protected Velocity getVelEast(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		logger.debug("Getting velocity for cell facing East...");
		
		double forward = origin.getVelocity().getX();
		double right = -origin.getVelocity().getY();
		
		Velocity f = getVelocityForNeighbour(origin,   
				east, south, west, north,
				eastIsWall, southIsWall, westIsWall, northIsWall, 
				forward, right, deflect);

		Velocity v = new Velocity(-f.getY(),f.getX()); //(right, forward)
		logger.debug("Velocity east: " + v);
		
		return v;
	}
	
	/**
	 * Calculate the change in velocity due to the cell to the South
	 * @param origin - origin cell
	 * @param north - cell north
	 * @param east - cell east
	 * @param south  - cell south
	 * @param west - cell west
	 * @param northIsWall - true if north is a wall, false otherwise
	 * @param eastIsWall - true if east is a wall, false otherwise
	 * @param southIsWall - true if south is a wall, false otherwise
	 * @param westIsWall - true if west is a wall, false otherwise
	 * @param deflect - the proportion of velocity that is deflected due to wall
	 * @return Velocity change due to cell North
	 */
	protected Velocity getVelSouth(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		logger.debug("Getting velocity for cell facing South...");
		
		double forward = -origin.getVelocity().getY();
		double right = -origin.getVelocity().getX();
		
		Velocity f = getVelocityForNeighbour(origin,   
				south, west, north, east, 
				southIsWall, westIsWall, northIsWall, eastIsWall, forward, right, deflect);

		Velocity v = new Velocity(-f.getX(),-f.getY()); //(right, forward)
		logger.debug("Velocity south: " + v);
		
		return v;
	}
	
	/**
	 * Calculate the change in velocity due to the cell to the West
	 * @param origin - origin cell
	 * @param north - cell north
	 * @param east - cell east
	 * @param south  - cell south
	 * @param west - cell west
	 * @param northIsWall - true if north is a wall, false otherwise
	 * @param eastIsWall - true if east is a wall, false otherwise
	 * @param southIsWall - true if south is a wall, false otherwise
	 * @param westIsWall - true if west is a wall, false otherwise
	 * @param deflect - the proportion of velocity that is deflected due to wall
	 * @return Velocity change due to cell North
	 */
	protected Velocity getVelWest(ThermalCell origin, ThermalCell north, 
			ThermalCell east, ThermalCell south, ThermalCell west, 
			boolean northIsWall, boolean eastIsWall, boolean southIsWall, 
			boolean westIsWall, final double deflect) {
		
		logger.debug("Getting velocity for cell facing West...");
		
		double forward = -origin.getVelocity().getX();
		double right = origin.getVelocity().getY();
		
		Velocity f = getVelocityForNeighbour(origin,   
				west, north, east, south, 
				westIsWall, northIsWall, eastIsWall, southIsWall, forward, right, deflect);

		Velocity v = new Velocity(f.getY(),-f.getX()); //(right, forward)
		logger.debug("Velocity west: " + v);
		
		return v;
	}
	
	protected Velocity getVelocityForNeighbour(ThermalCell origin, ThermalCell forward, 
			ThermalCell right, ThermalCell back, ThermalCell left, 
			boolean forwardIsWall, boolean rightIsWall, boolean backIsWall, 
			boolean leftIsWall, double velocityForward, double velocityRight, final double deflect) {
		
		Velocity v = origin.getVelocity().copy();
		
		logger.debug("Origin velocity: " + v);
		
		if(origin.isRackWall()) {
			logger.debug("Origin is rack wall... No change in velocity");
		} else if(forwardIsWall) {
			logger.debug("Forward is a wall. Reflecting...");
			if(velocityForward>0) v.reflectY();
		} else {
			if(velocityRight>0 && rightIsWall) {
				logger.debug("Right is a wall, deflecting Forward...");
				double y = velocityRight*deflect + velocityForward;
				double x = velocityRight - velocityRight*deflect;
				v = new Velocity(x,y);
			} else if (velocityRight<0 && leftIsWall) {
				logger.debug("Left is a wall, deflecting Forward...");
				logger.debug("velocityRight = " + velocityRight);
				double y = -velocityRight*deflect + velocityForward;
				double x = velocityRight - velocityRight*deflect;
				v = new Velocity(x,y);
			} else {
				logger.debug("No walls. Velocity unchanged.");
				if(velocityForward<0) v = new Velocity(0,0);
			}
		}

		logger.debug("Velocity forward: " + v);
		
		return v;	
		
	}
	
	
//	/**
//	 * Perform a propagation step to distribute existing heat across the grid.
//	 * 
//	 * @TODO Only does 2D propagation.
//	 * 
//	 * @param pDuration
//	 *            the duration of time that has passed since the last update.
//	 */
//	protected void propagateVelocity()
//	{
//		final ThermalCell[][] newGrid = mGrid.clone();
//
//		for (int x = 0; x < mGrid.length; x++)
//		{
//			for (int y = 0; y < mGrid[x].length; y++)
//			{
//				final int yNorth = wrapToGrid(Dimension.Y, y + 1);
//				final int xEast = wrapToGrid(Dimension.X, x + 1);
//				final int ySouth = wrapToGrid(Dimension.Y, y - 1);
//				final int xWest = wrapToGrid(Dimension.X, x - 1);
//
//				final double changeN = mGrid[x][y].calcVelocityTransfer(mGrid[x][yNorth]);
//				final double changeE = mGrid[x][y].calcVelocityTransfer(mGrid[xEast][y]);
//				final double changeS = mGrid[x][y].calcVelocityTransfer(mGrid[x][ySouth]);
//				final double changeW = mGrid[x][y].calcVelocityTransfer(mGrid[xWest][y]);
//				final double changeNE = mGrid[x][y].calcVelocityTransfer(mGrid[xEast][yNorth]);
//				final double changeSE = mGrid[x][y].calcVelocityTransfer(mGrid[xEast][ySouth]);
//				final double changeSW = mGrid[x][y].calcVelocityTransfer(mGrid[xWest][ySouth]);
//				final double changeNW = mGrid[x][y].calcVelocityTransfer(mGrid[xWest][yNorth]);
//
//				newGrid[x][y].incrementTemperature(changeN);
//				newGrid[x][y].incrementTemperature(changeE);
//				newGrid[x][y].incrementTemperature(changeS);
//				newGrid[x][y].incrementTemperature(changeW);
//				newGrid[x][y].incrementTemperature(changeNE);
//				newGrid[x][y].incrementTemperature(changeSE);
//				newGrid[x][y].incrementTemperature(changeSW);
//				newGrid[x][y].incrementTemperature(changeNW);
//
//				newGrid[x][yNorth].decrementTemperature(changeN);
//				newGrid[xEast][y].decrementTemperature(changeE);
//				newGrid[x][ySouth].decrementTemperature(changeS);
//				newGrid[xWest][y].decrementTemperature(changeW);
//				newGrid[xEast][yNorth].decrementTemperature(changeNE);
//				newGrid[xEast][ySouth].decrementTemperature(changeSE);
//				newGrid[xWest][ySouth].decrementTemperature(changeSW);
//				newGrid[xWest][yNorth].decrementTemperature(changeNW);
//			}
//		}
//
//		mGrid = newGrid.clone();
//	}
}
