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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.physical.Block;
import sim.physical.Datacentre;

/**
 * Renders a 2D view of a Datacentre. Note that this renders a
 * sim.physical.Datacentre object, NOT a config.physical.ConfigDatacentre
 * object.
 * 
 * @author James Laverack
 * 
 */

public class DatacentreMap extends JPanel implements Observer, ActionListener
{
	
	private static Logger logger = Logger.getLogger(DatacentreMap.class);
	
	// Serial ID
	private static final long serialVersionUID = -27242579759176732L;

	// Member Variables
	private Datacentre mDC;
	private MapTile grid[][];
	private int tileSize = 10; //Minimum size in pixels of a tile
	private AbstractMapRenderer mRender;	
	
	/**
	 * Creates a new map
	 */
	public DatacentreMap()
	{
		//this.setPreferredSize(new Dimension(1, 1));
	};

	public DatacentreMap(String name)
	{
		this();
		setName(name);
		logger.debug("New Datacentre map created: " + this);
	};
	
	public String getName(){
		if(mDC!=null) {
			return mDC.getName();
		} else {
			return "no datacentre attached";
		}
	}
	
	public int getNumber() {
		if(mDC!=null) {
			return mDC.getID();
		} else {
			return -1;
		}
	}
	
	public String toString() {
		return "DatacentreMap: [DC#=" + this.getNumber() + ", DCName=" + this.getName() + ", " + mRender + "]";
	}
	/**
	 * 
	 * TODO: JC Dec2011 - THIS IS NEVER USED!
	 * 
	 * Creates a new map, then renders the passed datacentre
	 * 
	 * @param pDC
	 */
	@Deprecated
	public DatacentreMap(Datacentre pDC, AbstractMapRenderer pTile)
	{
		mDC = pDC;
		mRender = pTile;
		renderPhysicalLayout(pDC, pTile);
	}

	/**
	 * Re-render the map using the selected data view
	 * 
	 * @param newView - the new view to render
	 */
	public void renderView(MapViewEnum newView) {
		
		logger.debug(this + ": Re-rendering map with view ("+newView+")");
		for (int j = 0; j < mDC.getGridHeight(); j++)
		{
			for (int i = 0; i < mDC.getGridWidth(); i++)
			{
				grid[i][j].render(newView);
			}
		}
	}
	
	/**
	 * Render the physical layout of a datacentre
	 * 
	 * @param pDC - the datacentre to render
	 * @param renderer - the map renderer to use
	 */
	public void renderPhysicalLayout(Datacentre pDC, AbstractMapRenderer renderer)
	{
		mDC = pDC;
		mRender = renderer;
		
		logger.debug(this + " DatacentreMap.renderPhysicalLayout('"+pDC.getName()+"', MapRenderer '" + renderer.getName() +"')");
		//TODO - Careful here, when we reset everything!
		// Clear the panel first
		this.removeAll();
		// Set an appropriate layout
		setLayout(new GridLayout(mDC.getGridHeight(), mDC.getGridWidth(), 1, 1));
		grid = new MapTile[mDC.getGridWidth()][mDC.getGridHeight()];
		// Set Grid on renderer

		// Set generated visuals
		// ServerTypeMapTile.addServerType("Crappy Server", Color.red);
		// ServerTypeMapTile.addServerType("Moderate Server", Color.yellow);
		// ServerTypeMapTile.addServerType("Awesome Server", Color.green);
		// Populate with blank tiles
		for (int j = 0; j < mDC.getGridHeight(); j++)
		{
			for (int i = 0; i < mDC.getGridWidth(); i++)
			{
				grid[i][j] = new MapTile(new String("["+i+"]["+j+"]"));
				this.add(grid[i][j]);
			}
		}

		//TODO - should this really be setting the local variable renderer?  Surely it should be the class variable?
		// Pass the renderer the grid
		mRender.setGrid(grid);
		
		//TODO - we do not want to call physical render every time!
		//TODO - We should be able to REMOVE this routine all together!
		logger.debug("THIS SHOULD ONLY BE CALLED ONCE AT THE START!");
		
		logger.debug(this + " DatacentreMap.renderPhysicalLayout('"+pDC.getName()+"', MapRenderer '" + 
		renderer.getName() +"' -- rendering physical layout .. calling renderPhysicalBlock");
		// Render all physical blocks in the datacentre
		for (Block b : mDC.getBlocks())
		{
			renderPhysicalBlock(b);
		}

		Dimension dim = new Dimension(tileSize * mDC.getGridWidth(),
				tileSize * mDC.getGridHeight());
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);

		this.revalidate();

		//set layout view and re-render
		mRender.setDataView(MapViewEnum.LAYOUT);
		renderView(MapViewEnum.LAYOUT);
	}

	public void newEvent(Event e)
	{

		if(logger.isDebugEnabled()) {
			logger.debug("New event received: " + e.getClass().getName() + " with dcID " + e.getDatacentreIndex() + " @ " + this);
		}
		
		//Ignore event if it *only* applies to a different DatacentreMap
		//Note: if DatacentreIndex == -1, event applies to all datacentres
		if(e.getDatacentreIndex() >= 0 && e.getDatacentreIndex()!=mDC.getID()) {
			//ignore this event
			logger.debug("Event does not apply to this DatacentreMap.  Ignoring this event...");
			return;
		} else {
			//this event applies, pass on to renderer
			logger.debug("Event applies to this datacentreMap.  Passing on to renderer...");
			mRender.update(e);
			revalidate();
			return;
		}
	}

	/**
	 * Renders a physical block on the map
	 * (Blocks may contain multiple blocks, 
	 * this recursive method renders all)
	 * 
	 * @param b
	 */
	private void renderPhysicalBlock(Block b)
	{
//		System.out.println("DatacentreMap.renderBlock()");
		//if block is rack or aircon, render as is
		if (b.isAirCon() | b.isRack())
		{
			mRender.renderPhysicalBlock(b);
		} else //block is an aisle containing (aircons and racks)
		{
			//Render each block of the containing aisle
			for (Block bl : b.getBlocks())
			{
				renderPhysicalBlock(bl);
			}
		}
	}

	//This overrides the Observer.update method.  And is called when
	//observable class changes (in this case the eventQueue)
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable pEventQueue, Object pEvent)
	{
		logger.debug(this + " DatacentreMap.update(.pEvent , .)" + pEvent);
		// TODO: Better code (checks to ensure it is an event?).
		Event event = (Event) pEvent;
		newEvent(event);
	}

	/**
	 * Handles events, sets the "data view" - data to display, then re-renders the
	 * view. 
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{ 
		logger.debug("DatacentreMap.actionPerformed() Event: " + e);
		
		if (e.getSource() == TabbedMapPane.getSingletonObject().getComboBox(MapViewEnum.getComboBoxString()))
		{
			//set selected data view
			logger.debug("Setting data view: " + (MapViewEnum)((JComboBox) e.getSource()).getSelectedItem());
			mRender.setDataView((MapViewEnum)((JComboBox) e.getSource()).getSelectedItem());
		} else {
			
			//Then where is it coming from?
			logger.warn(this + " e.getSource() is neither 'data view combo' or 'dc combo' ... so where is it from? " + e.getSource());
		}
		
		
		//Re-render the map with the current view
		renderView(mRender.getCurrentDataView());
	}

	public AbstractMapRenderer getmRender()
	{
		return mRender;
	}
}
