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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import sim.physical.World;

public class TabbedMapPane extends JPanel {

	public static Logger logger = Logger.getLogger(TabbedMapPane.class);
    public static String TAB_STRING = "Map";
    
	private static final long serialVersionUID = 1L;

	public enum MapViewEnum{
		
		//The Views
		LAYOUT("Datacentre Layout", "LAYOUT"),
		UTIL("Server Utilisation", "UTIL"),
		FAILURE("Failures", "FAILURE"),
		TEMP("Temperature", "TEMP");
		
		private String humanReadableString;
		private String nameString;
		
		/**
		 * Constructor for Enum Class
		 * 
		 * @param humanReadableDescription - a description of the enum type
		 * @param nameString - must be exactly the same as the enum type name
		 */
		MapViewEnum(String humanReadableDescription, String nameString) {
			this.humanReadableString = humanReadableDescription;
			this.nameString = nameString;
		}
		
		/**
		 * Human-readable Protocol string 
		 */
		public String toString() {
			return humanReadableString;
		}
		
		/**
		 * Enumeration name as string
		 */
		public String getNameString() {
			return nameString;
		}
		
		/**
		 * A short label description of the Enum class
		 * @return the label
		 */
		public static String getLabel() {
			return "Select visualisation: ";
		}
		
		public static String getComboBoxString() {
			return "dataMenu";
		}
	}

	private JComboBox dataMenu;
	private JLabel dataLabel;

	private JPanel optionPanel; // for storing combo boxes
	private JTabbedPane tabbedPane; // for storing each of the map views
	private JComponent panel[]; // for each map

	private DatacentreMap[] maps; // array of datacentre maps
	
	private static TabbedMapPane ref; //the Singleton reference
	
	private TabbedMapPane() {
		super(new GridLayout(1, 2));

		//no more code here.
	}

	public static TabbedMapPane getSingletonObject()
	{
		if (ref == null)
			ref = new TabbedMapPane();		
		return ref;
	}
	
	/**
	 * Clean the pane, remove all internal panels
	 */
	public void clean() {
		
		logger.debug("Cleaning internal panels...");
		if(optionPanel!=null) {
			this.remove(optionPanel);
		}
		if(tabbedPane!=null) {
			this.remove(tabbedPane);
		}
	}
	
	/**
	 * Initialise the pane with all internal DC maps
	 */
	public void initialise() {

		logger.debug("Initialising TabbedMapPane...");
		
		setLayout(new BorderLayout());
		
		optionPanel = new JPanel();
		optionPanel.setLayout(new GridLayout());
		
		// Combo box for selecting the data to visualise
		dataLabel = new JLabel(MapViewEnum.getLabel());
		dataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		optionPanel.add(dataLabel);
		dataMenu = new JComboBox(MapViewEnum.values());
		dataMenu.setSelectedIndex(0);
		optionPanel.add(dataMenu);
//		add(optionPanel, BorderLayout.SOUTH); //place option panel at bottom 
		add(optionPanel, BorderLayout.NORTH); //place option panel at top
		
		// set up the DC tabs and add to panel
		createDCTabs();
		add(tabbedPane, BorderLayout.CENTER);
		
	}
	

	// Cant do this right at the start since the world has yet to be created!!!!
	protected void createDCTabs() {

		int numDatacentres = World.getInstance().mDatacentres.size();
		logger.debug("There are " + numDatacentres + " Datacentres");
		
		logger.debug("Creating DC Tabs...");
		tabbedPane = new JTabbedPane();
		panel = new JComponent[numDatacentres];
		
		logger.debug("Creating DC Maps...");
		maps = new DatacentreMap[numDatacentres];
		
		// Populate tabs with datacentre maps
		String dcName = "unknown";
		for (int i = 0; i < numDatacentres; i++) {
			dcName = World.getInstance().mDatacentres.get(i).getName();
			panel[i] = new JPanel(true); //true = double-buffering for flicker-free updates (uses more memory)
			maps[i] = new DatacentreMap(dcName); //create new map for this datacentre
			dataMenu.addActionListener(maps[i]); //add the map as a listener to the dataMenu
			panel[i].add(dcName, maps[i]); //add the map to the panel
			panel[i].setLayout(new GridLayout(1, 1));
			tabbedPane.addTab(dcName, panel[i]);
			tabbedPane.setMnemonicAt(i, KeyEvent.VK_1);
		}
		
		logger.debug("DC Tabs created");
	}
	
	 /**
     * Returns a JComboBox, for determining the source of events in DatacentreMap.
     * @return the JComboBox
     */
	public JComboBox getComboBox(String name) {
		logger.debug("TabbedMapPane.getComboBox(" + name + ")");
		if(name.equals(MapViewEnum.getComboBoxString())) { return ref.dataMenu;}
		else { logger.warn("Unknown combo box name: " + name); return null; }
	}
	
    /**
     * Returns the map for a given datacentre number .
     * @return the DatacentreMap
     */
	public DatacentreMap getMap(int dcNumber) {
		if(dcNumber>=0 && dcNumber < maps.length) {
			return maps[dcNumber];
		} else {
			logger.warn("Requesting map for datacentre with number out of bounds [" + dcNumber + "]. Exit system //TODO - something more sensible");
			System.exit(0);
			return null;
		}
	}
}
