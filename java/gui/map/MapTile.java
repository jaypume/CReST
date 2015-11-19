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

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import utility.direction.CompassDirection;
import utility.physics.Velocity;

/**
 * TODO:- JS Should we make this abstract class and extend for each "view", Temperature, Failure, Power, etc.?
 * Square JPanel which holds values for temperature, failure and power usage. 
 *
 */
public class MapTile extends JPanel
{
	public static Logger logger = Logger.getLogger(MapTile.class);
	
	private static final long serialVersionUID = 1L;
    
	private static final int MAX_COL_VALUE = 255;
	
	private static final int BORDER_THICKNESS = 3;
	
	public enum PhysicalType {
		
		//The Types
		FLOOR("Floor space"), 
		RACK("Rack space"), 
		AIRCON("Aircon vent");

		private String humanReadableString;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 */
		PhysicalType(String humanReadableDescription) {
			this.humanReadableString = humanReadableDescription;
		}
		
		/**
		 * Human-readable Protocol string 
		 */
		public String toString() {
			return humanReadableString;
		}
		
	}
	
//	/**
//	 * Convert PhysicalType to a string
//	 * @param type PhysicalType
//	 * @return string value
//	 */
//	public static String convertToString(PhysicalType type) {
//		switch(type) {
//			case FLOOR: return "Floor space"; 
//			case RACK: return "Rack space";
//			case AIRCON: return "Aircon vent"; 
//			default: return "Error: unknown PhysicalType: " + type;
//		}
//	}
	
	//default type to floor
	private PhysicalType physicalType = PhysicalType.FLOOR;
	
    private double mTemperature       = 0.0;
    private Velocity mVelocity        = new Velocity();
    private double mPercentageFailure = 0.0;
    private double mPercentageServerUtilisation = 0.0;
    private double mPowerUsage        = 0.0;

    private CompassDirection rackDirection = CompassDirection.getDefault(); // default
    
    /**
     * Default constructor: must pass name of tile.
     */
    public MapTile(String name)
    {
    	super.setName(name);
    }
    
    /**
     * Constructor to set physical type of tile.
     */
    public MapTile(String name, PhysicalType type)
    {
    	this(name);
    	physicalType = type;
    }

    public void setPhysicalType(PhysicalType type) {
    	physicalType = type;
    }
    
    public PhysicalType getPhysicalType() {
    	return physicalType;
    }
    
    public void setRackDirection(CompassDirection rackDirection) {
    	this.rackDirection = rackDirection;
    }
    
    public CompassDirection getRackDirection() {
    	return rackDirection;
    }
    
    /**
     * Render the MapTile using a particular MapView
     * @param view - the MapView to render
     */
    public void render(MapViewEnum view){
    	
    	switch(view) { 	
	    	case TEMP: updateColorWithTemperature(); break;
	    	case FAILURE: updateColorWithFailures(); break;
	    	case LAYOUT: updateColorWithPhysical(); break;
	    	case UTIL: updateColorWithUtilisation(); break; 
	    	default: logger.warn("MapTile.render() -- unknown MapView '" + view + "'");
    	}
    }
    
    /**
     * TODO
     * Sets the current temperature to the value passed in & if temperature is the current module, calls a method to re-colour the tile.
     * @param pNewTemperature
     */
    public void setTemperature(final double pNewTemperature, final Velocity velocity, final MapViewEnum currentView)
    {
        mTemperature = pNewTemperature;
        mVelocity = velocity;
        
        if (currentView.equals(MapViewEnum.TEMP))
        {
            updateColorWithTemperature();
        }
    }

    /**
     * TODO
     * Sets the current failure value to the value passed in & if failure is the current module, calls a method to re-colour the tile.
     * @param pNewPercentageFailure
     */
    public void setPercentageFailure(final double pNewPercentageFailure, final MapViewEnum currentView)
    {
        mPercentageFailure = pNewPercentageFailure;
        
        if (currentView.equals(MapViewEnum.FAILURE))
        {
            updateColorWithFailures();
        }
    }
    
    public double getPercentageFailure() {
    	return mPercentageFailure;
    }
    
    /**
     * Sets the current server utilisation value to the value 
     * passed in and if serverUtilisation is the current view, 
     * calls a method to re-colour the tile.
     * 
     * @param newPercentageServerUtilisation - the new percentage server utilisation
     * @param currentView - the current map view
     */
    public void setPercentageServerUtilisation(final double newPercentageServerUtilisation, final MapViewEnum currentView)
    {
        mPercentageServerUtilisation = newPercentageServerUtilisation;
        
        if (currentView.equals(MapViewEnum.UTIL))
        {
            updateColorWithUtilisation();
        }
    }
    
    public double getPercentageServerUtilisation() {
    	return mPercentageServerUtilisation;
    }

    /**
     * TODO
     * Sets power usage to the value passed in & if power is the current module, calls a method to re-colour the tile.
     * @param 	pNewPowerUsage
     * 							Power usage value.
     * 			pCurrent Module
     * 							Which module is currently being displayed, if it's power then this change needs to be shown.
     */
    public void setPowerUsage(final double pNewPowerUsage, final MapViewEnum currentView)
    {
        setmPowerUsage(pNewPowerUsage);
        
        if (currentView.equals(MapViewEnum.LAYOUT))
        {
            // TODO
        }
    }

    /**
     * Colour the tile based on proportion failed
     * 
     */
    private void updateColorWithFailures()
    {
    	
    	//first check if its a floor - colour appropriately and return
    	if(physicalType==PhysicalType.FLOOR) {
    		this.setBackground(RackDisplayRenderer.DEFAULT_EMPTY_COLOUR);
    		//update tile borders to be the same as the background
        	updateBorderToBackgroundColor();
    		this.setToolTipText(physicalType.toString());
    		return;
    	}
    	
    	//check if its a working AIRCON - colour appropriately and return
    	if(physicalType==PhysicalType.AIRCON) {
    		this.setBorderBlack(); //black border for AIRCON vents
    		if(mPercentageFailure==0) {
    			this.setBackground(RackDisplayRenderer.DEFAULT_AC_COLOUR);
    			this.setToolTipText(physicalType.toString());
    			return;
    		} else { //aircon is failed
    			this.setBackground(RackDisplayRenderer.DEFAULT_AC_FAIL_COLOUR);
    			this.setToolTipText(physicalType.toString() + " failed");
    			return;
    		}
    	}
    	
        //use colour map to display failure.  Green = min, Red = max.
		final int fail_val = ((int) (MapTile.MAX_COL_VALUE * mPercentageFailure));
        
		try {
            Color fail_col = new Color(fail_val, MapTile.MAX_COL_VALUE-fail_val, 0); //failures from green to red
            this.setBackground(fail_col);
            DecimalFormat df = new DecimalFormat("#.#");
            this.setToolTipText("Server: " + String.valueOf(df.format(100* mPercentageFailure)) + "% failed");
            
          //update tile borders to be the same as the background
        	updateBorderToBackgroundColor();
		} catch (IllegalArgumentException e) {
			logger.error("Update border with failures: " + e.getMessage());
		}
    }

    /**
     * Colour the tile based on mean utilisation (of servers)s
     * 
     * Colour blue (unused = 0%) to red (full usage = 100%)
     */
    private void updateColorWithUtilisation()
    {  	
    	//mark all floor tiles
    	if(physicalType==PhysicalType.FLOOR) {
    		this.setBackground(RackDisplayRenderer.DEFAULT_EMPTY_COLOUR);
    		//update tile borders to be the same as the background
        	updateBorderToBackgroundColor();
    		this.setToolTipText(physicalType.toString());
    	} else if ( physicalType==PhysicalType.AIRCON) {
    		this.setBackground(RackDisplayRenderer.DEFAULT_EMPTY_COLOUR);
    		//update tile borders to be the same as the background
        	updateBorderToBackgroundColor();
    		this.setToolTipText(physicalType.toString());
    	} else {
    		
    		//use colour map to display utilisation.  Blue = min, Red = max.
    		final int val = ((int) (MapTile.MAX_COL_VALUE * mPercentageServerUtilisation));
            
    		try {
	            Color col = new Color(val, 0, MapTile.MAX_COL_VALUE-val);
	            this.setBackground(col);
	
	            DecimalFormat df = new DecimalFormat("#.###");
	            this.setToolTipText("Server: " + String.valueOf(df.format(100* this.mPercentageServerUtilisation)) + "% utilisation");
    		} catch (IllegalArgumentException e) {
    			logger.error("Updating colour with utilisation: " + e.getMessage());
    		}
    		
    		//TODO: - update border with failures --- but *only* when we are viewing utilisation
    		updateBorderWithFailures();
		}
//    	System.out.println("MapTile.updateColourWithUtilisation(): " + mPercentageServerUtilisation);

    }
    
    
    /**
     * Colour the tile based on the physical type of the cell
     */
    private void updateColorWithPhysical()
    {
    	
    	String tip_extension = "";
    	
    	switch(physicalType) {
    	
	    	case FLOOR: {
	    		this.setBackground(RackDisplayRenderer.DEFAULT_EMPTY_COLOUR);
	    		//update tile borders to be the same as the background
	        	updateBorderToBackgroundColor();
	        	break;
	    	}
	    	case AIRCON: {
	    		this.setBackground(RackDisplayRenderer.DEFAULT_AC_COLOUR);
	    		this.setBorderBlack(); //black border for AIRCON vents
	    		break;
	    	}
	    	case RACK: {
	    		
	    		tip_extension += ": facing " + this.getRackDirection().getNameString();
	    		if(this.getRackDirection().equals(CompassDirection.NORTH)) {
	    			this.setBackground(RackDisplayRenderer.DEFAULT_RACK_NORTH_COLOUR);
	    		} else {
	    			this.setBackground(RackDisplayRenderer.DEFAULT_RACK_SOUTH_COLOUR);
	    		}
	    		//this.setBackground(RackDisplayRenderer.DEFAULT_RACK_OK_COLOUR);
	    		//update tile borders to be the same as the background
	        	updateBorderToBackgroundColor();
	        	break;
	    	}
	    	default: {
	    		logger.error("MapTileupdateColorWithPhysical(): unknown physical type " + physicalType);
	    	    
	    	}
    	}

    	//set tooltip physical type
    	this.setToolTipText(physicalType.toString() + "" + tip_extension);
    }
    
    /**
     * TODO
     * Method which figures out what colour the tile should be based on the current temperature value & sets it to that.
     */
    private void updateColorWithTemperature()
    {
//    	System.out.println("MapTile.updateColorWithTemperature()");
//    	System.out.println("Temperature is: " + mTemperature);
        // TODO Make this neat and legible.
        // TODO Colours to be between blue for cool and red for hot.
        double ratio = mTemperature / 100.0;
        
//        System.out.println("Temperature: " + mTemperature);
//        System.out.println("ratio: " + ratio);
        //if temperature is out of range, cap to limit.
        if (ratio > 1.0)
        {
            ratio = 1.0;
        }
        
        if (ratio < 0.0)
        {
            ratio = 0.0;
        }

        Color col = new Color((int) (MapTile.MAX_COL_VALUE * ratio), 0, (int) (MapTile.MAX_COL_VALUE * (1 - ratio)));
        this.setBackground(col);
    	
        if(physicalType.equals(PhysicalType.AIRCON)) {
        	setBorder(Color.gray); //border colours for aircon vents
        } else if(physicalType.equals(PhysicalType.RACK)) {
        	setBorder(Color.black); //border colour for racks
        	setBackground(Color.black);
        } else {
	        //update tile borders to be the same as the background
	    	updateBorderToBackgroundColor();
        }
    	
        this.setToolTipText(String.valueOf((int) mTemperature) + " \u00B0C" + ", " + mVelocity);
    }

    /**
     * Colour border using failures.
     */
    protected void updateBorderWithFailures() {
    	
        //use colour map to display failure.  Green = min, Red = max.
		final int fail_val = ((int) (MapTile.MAX_COL_VALUE * mPercentageFailure));
        
		try {
            Color fail_col = new Color(fail_val, MapTile.MAX_COL_VALUE-fail_val, 0); //failures from green to red
            this.setBorder(BorderFactory.createMatteBorder(MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, fail_col));
		} catch (IllegalArgumentException e) {
			logger.error("Update border with failures: " + e.getMessage());
		}
    }
    
    /**
     * Update border colour to be set the same as tile background colour 
     */
    protected void updateBorderToBackgroundColor() {
		try {
            this.setBorder(BorderFactory.createMatteBorder(MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, this.getBackground()));
		} catch (IllegalArgumentException e) {
			logger.error("Update border with background colour: " + e.getMessage());
		}
    }
    
    protected void setBorderBlack() {
    	this.setBorder(BorderFactory.createMatteBorder(MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, Color.BLACK));
    }
    
    protected void setBorder(Color col) {
    	this.setBorder(BorderFactory.createMatteBorder(MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, MapTile.BORDER_THICKNESS, col));
    }
    
    /**
    * Method which draws a coloured border around the cell to show temperature as secondary data.
    */
    protected void updateBorderWithTemperature() {
    	double ratio = mTemperature / 100.0;

    	//if temperature is out of range, cap to limit.
    	if (ratio > 1.0)
    	{
    		ratio = 1.0;
    	}

    	if (ratio < 0.0)
    	{
    		ratio = 0.0;
    	}

    	Color col = new Color((int) (254 * ratio), 0, (int) (254 * (1 - ratio)));
    	this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, col));
    }

    protected void noBorder() {
    	this.remove((Component) this.getBorder()); //Unnecessary hack?
    }

	public double getmPowerUsage() {
		return mPowerUsage;
	}

	public void setmPowerUsage(double mPowerUsage) {
		this.mPowerUsage = mPowerUsage;
	}
    
	public String toString(){
		String s = "MapTile: " + this.getName() + "{";
		s+="PhysicalType="+physicalType+", ";
		s+="Temp="+mTemperature+", %Fail="+mPercentageFailure+", " +
				"Power=" + mPowerUsage + "Utilisation=" + mPercentageServerUtilisation + "}";
		return s;
	}
}
