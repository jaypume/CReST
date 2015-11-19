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
package gui.map;

import gui.map.TabbedMapPane.MapViewEnum;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.physical.Block;

public abstract class AbstractMapRenderer
{

	public static Logger logger = Logger.getLogger(AbstractMapRenderer.class);
	
	private int datacentre_number = -1; //the index of the datacentre in the world
    
    protected MapViewEnum mCurrentView = MapViewEnum.FAILURE;
    
	protected MapTile[][] mGrid;
	protected String name = "AbstractMapRenderer";
	
	public AbstractMapRenderer(String name, int dcID){
		this.name = name;
		datacentre_number = dcID;
		logger.debug("*** New AbstractMapRenderer: *** " + this);
	}
	
	public void setGrid(MapTile[][] pGrid)
	{
		logger.debug(this + " Setting grid...");
		mGrid = pGrid;
		setup();
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * This is the setup method that the MapRenderer should use to populate the Map Tiles.
	 */
	protected abstract void setup();
	
	/**
	 * Renders a single block
	 * @param b
	 */
	public abstract void renderPhysicalBlock(Block b);
	
	/**
	 * Calls a new event and tells the MapRenderer to update the map
	 * @param e
	 */
	public abstract void update(Event e);
	
	/**
	 * Set the data view we want to display.
	 * @param view
	 */
	public abstract void setDataView(MapViewEnum view);
	
	/** 
	 * Get the current data that should be rendered
	 * @return MapView currentView
	 */
	public MapViewEnum getCurrentDataView(){
		return mCurrentView;
	}
	
	/**
	 * For debugging!  Print out all data for all MapTiles to screen
	 */
	public void printAllTileData(){
		
		logger.debug(this.getName() + ".printAllTileData()");
		for(MapTile[] ta : mGrid)
		{
			for(MapTile t : ta)
			{
				logger.debug(t);
			}
		}
	}
	
	/**
	 * Get the number of the datacentre associated with this renderer
	 * 
	 * @return the datacentre ID number
	 */
	public int getDatacentreNumber() {
		return datacentre_number;
	}
	
	public String toString() {
		return "[" + this.getName() + ", DC#=" + this.datacentre_number + "]";
	}
}
