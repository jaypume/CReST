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
package builder.settings;

import gui.util.GUIFileReader;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import utility.FileUtil;
import builder.prefs.BuilderPreferences;
import builder.settings.panel.BrokerPanel;
import builder.settings.panel.DemandPanel;
import builder.settings.panel.EventsPanel;
import builder.settings.panel.FailuresPanel;
import builder.settings.panel.ModulesPanel;
import builder.settings.panel.ReplacementsPanel;
import builder.settings.panel.SimulationPanel;
import builder.settings.panel.SubscriptionsPanel;
import builder.settings.panel.ThermalPanel;
import builder.settings.panel.UserEventsPanel;
import config.SettingsManager;

public class SimSettingsView extends JScrollPane
{

	public static Logger logger = Logger.getLogger(SimSettingsView.class);
	private static final long serialVersionUID = -8184197110963007189L;

	private final SettingsManager mConfig;
	private final BuilderPreferences mPrefs;

	// Modules
	ModulesPanel modules;

	// Simulation
	SimulationPanel sim;
	
	//Replacements
	ReplacementsPanel replace;
	
	// Event
	EventsPanel events;
		
	// Pricing
	DemandPanel demand;
	
	//Broker
	BrokerPanel broker;
	
	// Failures
	FailuresPanel failures;

	// User Events
	UserEventsPanel userEvents;

	// Subscription data
	SubscriptionsPanel subs;
	
	// Thermal model
	ThermalPanel thermal;
	
	public SimSettingsView(SettingsManager conf, BuilderPreferences prefs)
	{
		// Setup variables
		mConfig = conf;
		mPrefs = prefs;
		JPanel topPanel = new JPanel();

		// Action Listener
		SaveHandler listen = new SaveHandler();
		ActionListener fileChooserListener = new FileChooserHandler();

		//Setup
		//JC, Jan 2012, Use GBC Layout
		
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//module panel, top left, width=1, fill vertically
		gbc.fill = GridBagConstraints.BOTH; //fill vertical height only
		gbc.gridwidth=2;
		gbc.gridheight=5;
		gbc.gridx=0;
		gbc.gridy=0; 
		modules = new ModulesPanel(mConfig, mPrefs);
		topPanel.add(modules.getPanel(listen, fileChooserListener), gbc);

		//sim settings panel, middle col 2, width=3, fill vertically
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx=2;
		gbc.gridy=0;
		gbc.gridheight=2;
		gbc.gridwidth=3;
		sim = new SimulationPanel(mConfig, mPrefs);
		topPanel.add(sim.getPanel(listen, fileChooserListener), gbc);
		
		//subscriptions panel, middle col 4, width=2
		gbc.fill = GridBagConstraints.BOTH; //fill horizontal width only
		gbc.gridx=5;
		gbc.gridy=0;
		gbc.gridwidth=2;
		gbc.gridheight=2;
		subs = new SubscriptionsPanel(mConfig, mPrefs);
		topPanel.add(subs.getPanel(listen, fileChooserListener), gbc);

		//broker panel, row 3
		gbc.fill = GridBagConstraints.BOTH; //fill horizontal width only
		gbc.gridx=2;
		gbc.gridy=2;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		broker = new BrokerPanel(mConfig, mPrefs);
		topPanel.add(broker.getPanel(listen, fileChooserListener), gbc);

		//replacements panel
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx=5;
		gbc.gridy=2;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		replace = new ReplacementsPanel(mConfig, mPrefs);
		topPanel.add(replace.getPanel(listen, fileChooserListener), gbc);
		
		//demand panel, row 4
		gbc.fill = GridBagConstraints.BOTH; //fill horizontal width only
		gbc.gridx=2;
		gbc.gridy=3;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		demand = new DemandPanel(mConfig, mPrefs);
		topPanel.add(demand.getPanel(listen, fileChooserListener), gbc);
		
		//user events panel, row 4
		gbc.fill = GridBagConstraints.BOTH; //fill horizontal width only
		gbc.gridx=5;
		gbc.gridy=3;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		userEvents = new UserEventsPanel(mConfig,mPrefs);
		topPanel.add(userEvents.getPanel(listen, fileChooserListener), gbc);
		
		//failures panel, row 5
		gbc.fill = GridBagConstraints.BOTH; //fill horizontal width only
		gbc.gridx=2;
		gbc.gridy=4;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		failures = new FailuresPanel(mConfig,mPrefs);
		topPanel.add(failures.getPanel(listen, fileChooserListener), gbc);
		
		//event panel, row 5
		gbc.fill = GridBagConstraints.HORIZONTAL; //fill horizontal width only
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx=5;
		gbc.gridy=4;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		events = new EventsPanel(mConfig,mPrefs);
		topPanel.add(events.getPanel(listen, fileChooserListener), gbc);
		
		//Thermal module panel, row 6 
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx=2;
		gbc.gridy=5;
		gbc.gridwidth=3;
		gbc.gridheight=1;
		thermal = new ThermalPanel(mConfig, mPrefs);
		topPanel.add(thermal.getPanel(listen, fileChooserListener), gbc);
		
		//Add panels to JScrollPane
		this.setBorder(BorderFactory.createEmptyBorder());
		this.getViewport().add(topPanel);
	}

	/**
	 * Class to handle the check boxes for the mode of the simulator.
	 * 
	 * @author James Laverack
	 * 
	 */
	public class SaveHandler implements ActionListener, ChangeListener,
			DocumentListener
	{

		/**
		 * Called whenever an element is updated, save everything to the config
		 * object
		 */
		private void reSaveAllElements()
		{
			// Modules
			modules.resaveElements();
			
			// Simulation Settings
			sim.resaveElements();
			
			// Event Settings
			events.resaveElements();
			
			// Pricing settings
			demand.resaveElements();
		
			// user Events
			userEvents.resaveElements();

			// Subscriptions settings			
			subs.resaveElements();
			
			// replacements
			replace.resaveElements();
			
			//broker
			broker.resaveElements();
			
			//failures
			failures.resaveElements();
			
			//thermal
			thermal.resaveElements();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			reSaveAllElements();
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			reSaveAllElements();
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			reSaveAllElements();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			reSaveAllElements();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			reSaveAllElements();
		}

	}

	/**
	 * Used for linking to demand and user event files
	 * @author cszjpc
	 *
	 */
	private class FileChooserHandler implements ActionListener
	{
		/**
		 * Called by a button when we want to open a file chooser
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Just which button are we?
			if (e.getSource().equals(demand.mBtnDemandFile))
			{
				JFileChooser fc = GUIFileReader.getFileChooser("Open Demand Data File", "/resources/demand", 
						new FileNameExtensionFilter("Demand Data File", "csv"));

				int rVal = fc.showOpenDialog(null);

				if (rVal == JFileChooser.APPROVE_OPTION)
				{
					//JC Jan 2012.  Modified to use relative path names for files
					//mTxtDemandFile.setText(fc.getSelectedFile().getPath());
					try {
						String filePath = FileUtil.findRelativePath(".", fc.getSelectedFile().getPath());
						logger.info("Selected Demand File: '" + filePath + "'");
						demand.mTxtDemandFile.setText(filePath);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			} else if (e.getSource().equals(userEvents.mBtnUserEvents))
			{
				
				JFileChooser fc = GUIFileReader.getFileChooser("Open User Events File", "/resources/event", 
						new FileNameExtensionFilter("User Events File", "event"));

				int rVal = fc.showOpenDialog(null);

				if (rVal == JFileChooser.APPROVE_OPTION)
				{
					//JC Jan 2012.  Modified to use relative path names for files
					try {
						String filePath = FileUtil.findRelativePath(".", fc.getSelectedFile().getPath());
						logger.info("Selected User Events File: '" + filePath + "'");
						userEvents.mTxtUserEvents.setText(filePath);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				
				}
			}
		}
	}
}
