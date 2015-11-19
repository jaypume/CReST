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

import gui.map.MapTile.PhysicalType;
import gui.map.TabbedMapPane.MapViewEnum;

import java.awt.Color;
import java.awt.Point;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sim.module.event.Event;
import sim.module.failure.event.FailureEvent;
import sim.module.failure.event.FailureEvent.ObjectType;
import sim.module.replacements.event.ReplacementEvent;
import sim.module.service.ServiceModuleRunner;
import sim.module.service.bo.Service;
import sim.module.service.event.ServiceStartEvent;
import sim.module.service.event.ServiceStopEvent;
import sim.module.subscriptions.event.InconsistencyUpdateEvent;
import sim.module.thermal.ThermalModuleRunner;
import sim.module.thermal.bo.ThermalGrid;
import sim.module.thermal.event.ThermalEvent;
import sim.physical.AirConditioner;
import sim.physical.Block;
import sim.physical.Datacentre;
import sim.physical.Rack;
import sim.physical.Server;
import sim.physical.World;
import sim.physical.network.IP;
import utility.direction.CompassDirection;
import utility.time.TimeManager;

public class RackDisplayRenderer extends AbstractMapRenderer
{
	
	public static Logger logger = Logger.getLogger(RackDisplayRenderer.class);
	
	// Defaults
	public static final Color DEFAULT_RACK_OK_COLOUR = Color.GREEN;
	public static final Color DEFAULT_RACK_FAIL_COLOUR = Color.RED;
	public static final Color DEFAULT_AC_COLOUR = Color.BLUE;
	public static final Color DEFAULT_AC_FAIL_COLOUR = Color.RED;	
	public static final Color DEFAULT_EMPTY_COLOUR = Color.GRAY;
	public static final Color DEFAULT_RACK_NORTH_COLOUR = Color.DARK_GRAY;
	public static final Color DEFAULT_RACK_SOUTH_COLOUR = Color.LIGHT_GRAY;
	
    // Member variables //
	// Colours
    protected Color mRackOKColour;
    protected Color mRackFailColour;
    protected Color mACColour;
    protected Color mEmptyColour;

    /**
     * TODO
     */
	public RackDisplayRenderer()
	{
		//Set default DC_ID = -1
		super("RackDisplayRenderer", -1);
		mRackOKColour = DEFAULT_RACK_OK_COLOUR;
		mRackFailColour = DEFAULT_RACK_FAIL_COLOUR;
		mACColour = DEFAULT_AC_COLOUR;
		mEmptyColour = DEFAULT_EMPTY_COLOUR;
	}
	
    /**
     * TODO
     */
	public RackDisplayRenderer(int dcID)
	{
		super("DC#"+dcID+"_RackDisplayRenderer", dcID);
		mRackOKColour = DEFAULT_RACK_OK_COLOUR;
		mRackFailColour = DEFAULT_RACK_FAIL_COLOUR;
		mACColour = DEFAULT_AC_COLOUR;
		mEmptyColour = DEFAULT_EMPTY_COLOUR;
	}
	
	/*
	 * (non-Javadoc)
	 * @see gui.map.MapRenderer#setup()
	 */
	@Override
	protected void setup()
	{	
		logger.debug("RackDisplayRenderer.setup() -- set all tiles grey");
		// Make everything grey
		for(MapTile[] ta : mGrid)
		{
			for(MapTile t : ta)
			{
				t.setBackground(mEmptyColour);
			}
		}
	}
	
	/**
	 * Sets current data view, i.e. which data is to be displayed
	 */
	public void setDataView(MapViewEnum view) {
			
		logger.debug("DC#"+ this.getDatacentreNumber() + " RackDisplayRenderer.setDataView(" + view + ")");
		
		mCurrentView = view;

//		printAllTileData();
	}
	
	//TODO: This is really bad example of RunTimeTypeIdentification.
	//Can we use some polymorphism?
	/*
	 * (non-Javadoc)
	 * @see gui.map.AbstractMapRenderer#update(sim.event.Event)
	 */
	@Override
	public void update(Event e)
	{	
		//should the event be ignored?
		if(e.isIgnored()) return;
		
		//get datacentre ID of this event
		int dcID = e.getDatacentreIndex();
		
	    final String className = e.getClass().getName();
	    
	    // Update the colours of the map grid with values from the given event.
		if (className.equals(FailureEvent.class.getName()))
		{
		    /////////////////////////////////////
		    /////////// FAILURE EVENTS //////////
		    /////////////////////////////////////
			
			FailureEvent fail = (FailureEvent) e;
				
        	if(logger.isDebugEnabled()) {
				logger.debug(TimeManager.log(this + " Received failure event: (DC#=" + dcID +")"));
			}
		    
		    Point position;
		    final double ratio;

            // Calculate values to update the grid with depending on whether the
            // object to fail is a server or an air conditioning unit.
            if (fail.getObjectType() == ObjectType.server)
            {
                position = World.getInstance().getServer(fail.getID()).getAbsolutePosition();
                Rack r = World.getInstance().getRack(World.getInstance().getServer(fail.getID()).getIP());
                
                ratio = (double) r.getNumFailedServers() / r.getNumServers();
                    
                mGrid[position.x][position.y].setPercentageFailure(ratio, mCurrentView);
                //also, update this tile's new server utilisation based on failure
                mGrid[position.x][position.y].setPercentageServerUtilisation(r.getMeanCPUutilisation(), mCurrentView);

                
              //TODO - clean this up .... a failure event can restart lots of services all over DC, so need to re-draw all utilisation tiles.
                if(r.getMeanCPUutilisation()>0) outputAllUtilisationValues(0);

            }
            else
            {
                AirConditioner aircon = World.getInstance().getAirCon(fail.getID());
                position = aircon.getAbsolutePosition();
                
                if (aircon.isAlive())
                {
                    ratio = 0.0;
                }
                else
                {
                    ratio = 1.0;
                }
                
                mGrid[position.x][position.y].setPercentageFailure(ratio, mCurrentView);
            }
        }
		
		
		if (className.equals(ReplacementEvent.class.getName()))
		{
		    /////////////////////////////////////
		    ///////// REPLACEMENT EVENTS ////////
		    /////////////////////////////////////
			
			ReplacementEvent replace = (ReplacementEvent) e;
				
        	if(logger.isDebugEnabled()) {
				logger.debug(TimeManager.log(this + " Received replacement event: (DC#=" + dcID +")"));
			}
		    
		    Point position;
		    final double ratio;

            // Calculate values to update the grid with depending on whether the
            // object to fail is a server or an air conditioning unit.
            if (replace.getObjectType() == ObjectType.server)
            {
                position = World.getInstance().getServer(replace.getID()).getAbsolutePosition();
                Rack r = World.getInstance().getRack(World.getInstance().getServer(replace.getID()).getIP());
                
                ratio = (double) r.getNumFailedServers() / r.getNumServers();
                    
                mGrid[position.x][position.y].setPercentageFailure(ratio, mCurrentView);
                //also, update this tile's new server utilisation based on failure
                mGrid[position.x][position.y].setPercentageServerUtilisation(r.getMeanCPUutilisation(), mCurrentView);

                
              //TODO - clean this up .... a failure event can restart lots of services all over DC, so need to re-draw all utilisation tiles.
                if(r.getMeanCPUutilisation()>0) outputAllUtilisationValues(0);

            }
            else
            {
                AirConditioner aircon = World.getInstance().getAirCon(replace.getID());
                position = aircon.getAbsolutePosition();
                
                if (aircon.isAlive())
                {
                    ratio = 0.0;
                }
                else
                {
                    ratio = 1.0;
                }
                
                mGrid[position.x][position.y].setPercentageFailure(ratio, mCurrentView);
            }
        }
		
        else if (className.equals(ThermalEvent.class.getName()))
        {
    	    /////////////////////////////////////////
    	    /////////// TEMPERATURE EVENTS //////////
    	    /////////////////////////////////////////
        	
        	//All temperature values are updated at once
            updateTemperatureValues();
        }
        else if (className.equals(InconsistencyUpdateEvent.class.getName()))
        {
    	    /////////////////////////////////////
    	    //////// CONSISTENCY EVENTS /////////
    	    /////////////////////////////////////
        	
        	//JC, Jan 2012 - Consistency now fixed, try to plot on map view
        	//JC: 13/12/2011 NOT USED
        	
        	//TODO try to get datacentre ID - ignore event if incorrect DC
        	
//        	@SuppressWarnings("unused")
//			InconsistencyUpdateEvent inc = (InconsistencyUpdateEvent) e;
        	
        	//TODO JC Dec 2011: - We dont currently seem to be using InconsistencyUpdateEvents
        	//If we were, we could then display the percentage inconsistent per Rack, maybe?
        	//for now its a waste of time.  
//        	sim.middleware.subscription.SubscriptionGen.percentinconsistent();
            // TODO
        }
        else if (className.equals(ServiceStartEvent.class.getName()))
        {
    	    /////////////////////////////////////
    	    ////// SERVICE START EVENTS /////////
    	    /////////////////////////////////////

        	//where is this service located?
        	Service s = ((ServiceStartEvent)e).getService();
        	
		    Point position;
		    final double ratio;
        	position = World.getInstance().getServer(s.getServerID()).getAbsolutePosition();
            Rack r = World.getInstance().getRack(World.getInstance().getServer(s.getServerID()).getIP());
            ratio = r.getMeanCPUutilisation();
            DecimalFormat df = new DecimalFormat("#.##");
            logger.debug("Mean CPU utilisation is now: " + df.format(ratio));
            
            mGrid[position.x][position.y].setPercentageServerUtilisation(ratio, mCurrentView);
        }
        else if (className.equals(ServiceStopEvent.class.getName()))
        {
    	    /////////////////////////////////////
    	    ////// SERVICE STOP EVENTS  /////////
    	    /////////////////////////////////////
        	
        	if(e.isIgnored()) { //we should never get here!
        		logger.warn("DC#"+ this.getDatacentreNumber() + " This EventShouldBeIgnored!");
        	}
        	
        	//where is this service located?
        	Service s = ((ServiceStopEvent)e).getService();
        	
        	if(logger.isDebugEnabled()) {
        		int server_ID = World.getInstance().getDatacentre(dcID).getServer(s.getServerID()).getID();
        		Server server = World.getInstance().getDatacentre(dcID).getServer(s.getServerID());
        		logger.debug("Server status =" + server.isAlive() + ", ServerID="+server_ID+ " Service = " + s);
        	}
        		
        	//Check whether this stop event refers to an old issuenumber...
//        	logger.warn("ServiceStopEvent: Service =  " + s);
        	
        	
        	Service runningService = ServiceModuleRunner.getInstance().getServiceManager(dcID).getServiceWithID(s.getID());
        	if(runningService!=null) {
        		@SuppressWarnings("unused")
				int currentServiceIssueNumber = runningService.getIssueNumber();
//        		logger.warn("Current Service issue number = " + currentServiceIssueNumber);
        	} else {
//        		logger.warn("Current Service no longer exists...");
        	}
        	
        	
        	
        	//check service hasn't already stopped on a failed machine
        	//TODO: Its really bad to have these 'backward' references to the GUI.  Info should be in event
        	if(World.getInstance().getDatacentre(dcID).getServer(s.getServerID()).isBroken()) {;
        		logger.fatal("DC#"+ this.getDatacentreNumber() + " Running on a broken server!!!!! Event: " + e + " Server: " + World.getInstance().getDatacentre(dcID).getServer(s.getServerID()));
        		System.exit(0);
        	}
		    Point position;
		    final double ratio;
        	position = World.getInstance().getServer(s.getServerID()).getAbsolutePosition();
            Rack r = World.getInstance().getRack(World.getInstance().getServer(s.getServerID()).getIP());
            ratio = r.getMeanCPUutilisation();
            DecimalFormat df = new DecimalFormat("#.##");
            logger.debug("Mean CPU utilisation is now: " + df.format(ratio));
            mGrid[position.x][position.y].setPercentageServerUtilisation(ratio, mCurrentView);
        }
	}
	
	//TODO this is a bit ugly.  Im sure there is a more optimal way 
	/**
	 * Update all utilisation values
	 *
	 * @param dc_ID
	 */
	public void outputAllUtilisationValues(int dc_ID) {
		
		Datacentre dc = World.getInstance().getDatacentre(dc_ID);
		
	    IP serverIP;
	    Rack r;
	    Point p;
	    double util = 0;

	    for (int j = 0; j < dc.getNumAisles(); j++)
	    {
	        for (int k = 0; k < dc.getmAisles().get(j).getNumRacks(); k++)
	        {
            	//get server IP
            	serverIP = dc.getmAisles().get(j).getmRacks().get(k).getmServers().get(0).getIP();
            	r = dc.getRack(serverIP);
                p = r.getAbsolutePosition();
                util = r.getMeanCPUutilisation();
                           
                //set all values
                mGrid[p.x][p.y].setPercentageServerUtilisation(util, mCurrentView);
	        }
	    }
	    
	    //re-calc all utilisation values:
	    
	}
	
	/**
	 * Renders blocks as their physical layout colours
	 * Floor, Aircon, Rack (type)
	 * 
	 * see gui.map.MapRenderer#renderPhysicalBlock(sim.physical.Block)
	 */
	@Override
	public void renderPhysicalBlock(Block b)
	{	
		// Find location of block
	    final Point absPosition = b.getAbsolutePosition();
		MapTile tile = mGrid[absPosition.x][absPosition.y];
		
		tile.removeAll();
		
		if(b != null && b.isAirCon())
		{
			tile.setPhysicalType(PhysicalType.AIRCON);
			tile.setBackground(mACColour);
		}else if(b != null && b.isRack())
		{
			tile.setPhysicalType(PhysicalType.RACK);
			//tile.setBackground(mRackOKColour);	
			tile.setRackDirection(b.toRack().getDirection());
			
			if(b.toRack().getDirection().equals(CompassDirection.SOUTH)) {
				tile.setBackground(DEFAULT_RACK_SOUTH_COLOUR);
			} else {
				tile.setBackground(DEFAULT_RACK_NORTH_COLOUR);
				
			}
		}else
		{
			tile.setPhysicalType(PhysicalType.FLOOR);
			tile.setBackground(mEmptyColour);
		}
	}
	
	/**
	 * Fetch the latest values from the temperature grid.
	 */
	private void updateTemperatureValues()
	{
	
		//get the thermal grid for this datacentre
		ThermalGrid grid = ThermalModuleRunner.getInstance().getThermalGrid(getDatacentreNumber());
		
	    // Run through the entire temperature grid and fetch the up-to-date values.
	    for (int x = 0; x < grid.getWidth(); x++)
	    {
	        for (int y = 0; y < grid.getHeight(); y++)
	        {
	            mGrid[x][y].setTemperature(grid.getCell(x, y).getTemperature(), grid.getCell(x, y).getVelocity(), mCurrentView);
	        }
	    }
	}
}
