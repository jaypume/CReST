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
package gui;

import gui.graph.CPUUtilisationGraph;
import gui.graph.ConsistencyTimeGraph;
import gui.graph.CostsTimeGraph;
import gui.graph.FailureTimeGraph;
import gui.graph.ServicesGraph;
import gui.graph.TemperatureTimeGraph;
import gui.map.RackDisplayRenderer;
import gui.map.TabbedMapPane;
import gui.panel.CostPanel;
import gui.panel.LogPane;
import gui.util.GUIFileReader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import sim.SimulationRunner;
import sim.WorldListener;
import sim.module.Module;
import sim.physical.World;
import utility.Copyright;
import utility.time.TimeManager;
import config.SettingsManager;
import config.SettingsManagerAccess;

/**
 * GUI for simulation runner
 */
public class SimGUIMainWindow implements ComponentListener, WorldListener
{
	
	private static Logger logger = Logger.getLogger(SimGUIMainWindow.class);
	
	private static SimGUIMainWindow instance = null;
	
	private static String PROGRAM_NAME = "CReST: Cloud Research Simulation Toolkit"; //TODO: move these

	private static String ICONS_PATH = "resources/img/icons/";
	
	private JFrame mMainFrame;
	private JPanel mMainPanel;
	private JTabbedPane tabbedPane;
	
	private SimStatusBar mStatusBar;

	protected JPanel parentPricePanel; 
	private CostPanel costPanel;

	@SuppressWarnings("unused")
	private JPanel mEventLog;
	
	private File xmlConfigFile;
	private String paramsFileName;
	private boolean paramsFileExists = false;
	private String eventsFileName;
	private boolean eventsFileExists = false;
	
	
	protected SimulationRunner mSimulation;
	
	/**
	 * Constructor for the simulation runner GUI
	 * 
	 * @author Alex Sheppard
	 */
	private SimGUIMainWindow(File xmlFile)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}

		// Set file
		xmlConfigFile = xmlFile;        
		initialiseWindow();
	}

	/**
	 * Set the parameters configuration file for the Simulation
	 * 
	 * @param paramsConfigFileName
	 */
	public void setParamsFileName(String paramsConfigFileName) {
		paramsFileName = paramsConfigFileName;
		paramsFileExists = true;
	}
	
	/**
	 * Set the user events file for the Simulation
	 * 
	 * @param userEventsFileName
	 */
	public void setEventsFileName(String userEventsFileName) {
		eventsFileName = userEventsFileName;
		eventsFileExists = true;
	}
	
	protected void initialiseWindow() {
		
		logger.debug("Initialising GUI window...");
		
		// Create frame
		mMainFrame = new JFrame(PROGRAM_NAME);
		mMainFrame.setSize(1150, 768);
		//Stop application if GUI is exited
		mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mMainFrame.setIconImage(new ImageIcon(ICONS_PATH + "logo.png")
				.getImage());
		mMainFrame.addComponentListener(this);

		// Create panel
		mMainPanel = new JPanel();
		mMainPanel.setLayout(new BorderLayout());
		mMainFrame.add(mMainPanel, BorderLayout.CENTER);

		// Create menu bar
		MenuBarHandler muHandle = new MenuBarHandler();
		mMainFrame.setJMenuBar(muHandle.setupMenuBar());
		
		// Create status bar
		mStatusBar = new SimStatusBar(xmlConfigFile.getAbsolutePath());
		
		// Create tabs
		tabbedPane = new JTabbedPane();
		createTabs(true);
		
		//Select the map as default
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);

		// Add + show window
		mMainPanel.add(tabbedPane, BorderLayout.CENTER);
		mMainPanel.add(mStatusBar, BorderLayout.SOUTH);
		mMainFrame.add(mMainPanel);
		mMainFrame.setLocationByPlatform(true);
		mMainFrame.setVisible(true);
		
		logger.info("GUI window initialised");
	}
	
	public static void create(File xmlConfigFile)
	{
		if (instance == null)
		{
			logger.debug("Simulation GUI window is null, creating new simulation window...");
			instance = new SimGUIMainWindow(xmlConfigFile);
		}
	}
	
	public static SimGUIMainWindow getInstance()
	{
		return instance;
	}
	
	private void initTabbedMapPane(boolean state)
	{
		if (state)
		{
			tabbedPane.addTab(TabbedMapPane.TAB_STRING, TabbedMapPane.getSingletonObject());
		}
		else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("TabbedMapPane"));
		}
	}
	
	private void initPricingTab(Boolean state)
	{
		if (state)
		{
			parentPricePanel = new JPanel();
			parentPricePanel.setLayout(new BorderLayout());
			
			costPanel = new CostPanel();

			parentPricePanel.add(new JLabel("Costs of running the datacentres  heizi"), BorderLayout.NORTH);
			parentPricePanel.add(costPanel,BorderLayout.CENTER);
			
			tabbedPane.addTab(CostPanel.TAB_STRING, parentPricePanel);
			
		} else
		{
			tabbedPane.remove(tabbedPane.indexOfTab(CostPanel.TAB_STRING));
		}
	}
	
	private void initPriceTimeGraph(Boolean state)
	{
		if (state)
		{			
			CostsTimeGraph.create();
			
			tabbedPane.addTab(CostsTimeGraph.TAB_STRING, CostsTimeGraph.getInstance());
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		} else
		{
			tabbedPane.remove(tabbedPane.indexOfTab(CostsTimeGraph.TAB_STRING));
		}
	}
	
	private void initServicesGraph(Boolean state)
	{
		if (state)
		{			
			ServicesGraph.create();
			
			tabbedPane.addTab(ServicesGraph.TAB_STRING, ServicesGraph.getInstance());
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		} else
		{
			tabbedPane.remove(tabbedPane.indexOfTab(ServicesGraph.TAB_STRING));
		}
	}
	
    private void initTemperatureGraph(Boolean state)
    {
        if (state)
        {           
            TemperatureTimeGraph.create();
            
            tabbedPane.addTab(TemperatureTimeGraph.TAB_STRING, TemperatureTimeGraph.getInstance());
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        } else
        {
            tabbedPane.remove(tabbedPane.indexOfTab(TemperatureTimeGraph.TAB_STRING));
        }
    }
	
	private void initCPUUtilisationGraph(Boolean state)
	{
		if (state)
		{			
			CPUUtilisationGraph.create();
			
			tabbedPane.addTab(CPUUtilisationGraph.TAB_STRING, CPUUtilisationGraph.getInstance());
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		} else
		{
			tabbedPane.remove(tabbedPane.indexOfTab(CPUUtilisationGraph.TAB_STRING));
		}
	}
	
	private void initConsistencyGraph(Boolean state)
	{
		if (state)
		{			
			ConsistencyTimeGraph.create();
			
			tabbedPane.addTab(ConsistencyTimeGraph.TAB_STRING, ConsistencyTimeGraph.getInstance());
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		} else
		{
			try {
				tabbedPane.remove(tabbedPane.indexOfTab(ConsistencyTimeGraph.TAB_STRING));
			} catch (Exception e) {
				logger.warn(e);
			}
		}
	}
	

	@SuppressWarnings("unused")
	private void eventLog(Boolean state)
	{
		if (state)
		{
			LogPane log1pane = new LogPane();
			tabbedPane.addTab("Event Log", log1pane);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		} else
		{
			tabbedPane.remove(tabbedPane.indexOfTab("Event Log"));
		}
	}

    private void initFailureTimeGraph(Boolean state)
    {
        if (state)
        {           
            FailureTimeGraph.create();
            
            tabbedPane.addTab( FailureTimeGraph.TAB_STRING, FailureTimeGraph.getInstance());
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        } else
        {
            tabbedPane.remove(tabbedPane.indexOfTab(FailureTimeGraph.TAB_STRING));
        }
    }

	private class MenuBarHandler implements ActionListener
	{
		JMenuBar bar;
		JMenu fileMenu;
		JMenu runMenu;
		JMenu helpMenu;
		// File menu
		JMenuItem openItem;
		JMenuItem saveItem;
		JMenuItem quitItem;
		// Run menu
		JMenuItem runItem;
		JMenuItem stopItem;
		// Help menu
		JMenuItem helpItem;
		JMenuItem aboutItem;

		/**
		 * Creates a populated JMenuBar object and returns it, ready to be added
		 * to a JFrame (or other Swing object).
		 * 
		 * @return the constructed JMenuBar object
		 */
		private JMenuBar setupMenuBar()
		{
			// Setup and declare variables
			bar = new JMenuBar();
			fileMenu = new JMenu("File");
			runMenu = new JMenu("Run");
			helpMenu = new JMenu("Help");

			// File menu
			fileMenu.setMnemonic(KeyEvent.VK_F);

			openItem = new JMenuItem("Open", KeyEvent.VK_O);
			openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					KeyEvent.CTRL_MASK));
			openItem.addActionListener(this);

			saveItem = new JMenuItem("Save", KeyEvent.VK_S);
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					KeyEvent.CTRL_MASK));
			saveItem.addActionListener(this);

			quitItem = new JMenuItem("Quit", KeyEvent.VK_Q);
			quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					KeyEvent.CTRL_MASK));
			quitItem.addActionListener(this);

			fileMenu.add(openItem);
			fileMenu.add(saveItem);
			fileMenu.addSeparator();
			fileMenu.add(quitItem);

			// Tools menu
			runMenu.setMnemonic(KeyEvent.VK_R);
		
			runItem = new JMenuItem("Run Simulator");
			runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			runItem.addActionListener(this);
			
			stopItem = new JMenuItem("Stop Simulator");
			stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
			stopItem.addActionListener(this);

			runMenu.add(runItem);
			runMenu.add(stopItem);

			// Help menu
			helpMenu.setMnemonic(KeyEvent.VK_H);

			helpItem = new JMenuItem(PROGRAM_NAME + " Help", KeyEvent.VK_H);
			helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			helpItem.addActionListener(this);

			aboutItem = new JMenuItem("About " + PROGRAM_NAME);
			aboutItem.addActionListener(this);

			helpMenu.add(helpItem);
			helpMenu.add(aboutItem);

			// Add menus to bar and return
			bar.add(fileMenu);
			bar.add(runMenu);
			bar.add(helpMenu);
			return bar;
		}

		public void actionPerformed(ActionEvent e)
		{
			// Listen to events
			Object source = e.getSource();
			// Test for each source
			if (source.equals(runItem))
			{
				logger.info("[Run simulator button press]");
				runSimulationOption();
			} else if (source.equals(stopItem))
			{
				stopSimulationOption();
			} else if (source.equals(openItem))
			{
				openGUIOption();
			} else if (source.equals(saveItem))
			{
				saveGUIOption();
			} else if (source.equals(quitItem))
			{
				quitGUIOption();
			} else if (source.equals(aboutItem))
			{
				aboutGUIOption();
			} else if (source.equals(helpItem))
			{
				//TODO:
				logger.info("[Help button press] //TODO: show help dialog...");
			} 
		}
	}

	/**
	 * This method is triggered when user selects the 'About' option.
	 */
	private void aboutGUIOption()
	{
		//TODO FIXME //new AboutDialog(null); FIXME:
		logger.info("[About button press] opening about dialog...");
		JOptionPane.showMessageDialog(this.mMainFrame, Copyright.info, "About CReST", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * This method is triggered when the GUI indicates that we want to quit.
	 */
	private void quitGUIOption()
	{
		// We've asked to quit. Make sure this is the desired action before we close the program.
		int result = JOptionPane.showConfirmDialog(this.mMainFrame,
				"Are you sure you want to quit " + PROGRAM_NAME + "?\n" +
						"This will stop any running simulation.", "Quit "
						+ PROGRAM_NAME, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, new ImageIcon(ICONS_PATH
						+ "quit_big.png"));
		
		if (result == 0)
		{
			mMainFrame.dispose();

			if(mSimulation!=null && mSimulation.isRunning()) {
				logger.info("[Quit GUI button press] sending stop signal to SimulationRunner...");
				mSimulation.stopSimulation();
				//Wait until SimulationRunner has stopped, before continuing
				waitUntilSimulationStops();
				logger.info("Simultion has now stopped.  Closing GUI.");
			} else {
				logger.info("[Quit GUI button press] no simulation running.  Closing GUI.");
			}
		} else {
			logger.info("You have selected not to quit GUI...");
		}
	}

	/**
	 * Starts a new instance of the simulator
	 */
	public void runSimulationOption()
	{
		//First, check there is not a simulation running already
		if(mSimulation!=null && mSimulation.isRunning()) {
			logger.info("SimMainWindow: Can't start new simulation.  Simulation already running.");
		} else {
			
			logger.debug("Clearing price panel...");
			costPanel.removeAll();
			logger.debug(TimeManager.log("Cleaning TabbedMapPane..."));
			TabbedMapPane.getSingletonObject().clean();
			
			logger.debug("About to create new simulation runner...");
			mSimulation = new SimulationRunner(xmlConfigFile);
			if(paramsFileExists) {
				mSimulation.setParamsFileName(paramsFileName);
			} 
			if(eventsFileExists) {
				mSimulation.setEventsFileName(eventsFileName);
			} 
			logger.debug("Adding GUI as listener...");
			mSimulation.addWorldListener(this);
			
			logger.info("Starting simulation...");
			mSimulation.start();
		} 
	}

	/**
	 * Send a stop signal to a running simulation and clean up GUI
	 */
	private void stopSimulationOption()
	{		
		if(mSimulation!=null && mSimulation.isRunning()) {
			// We've asked to stop simulation. Make sure this is the desired action before we close the program.
			int result = JOptionPane.showConfirmDialog(this.mMainFrame,
					"Are you sure you want to stop simulation?\n", "Quit "
							+ PROGRAM_NAME, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, new ImageIcon(ICONS_PATH
							+ "quit_big.png"));
		
			if (result == 0)
			{
				logger.info(TimeManager.log("[Stop simulation button press] sending stop signal to SimulationRunner..."));
				mSimulation.stopSimulation();
				
				logger.info(TimeManager.log("Waiting until SimulationRunner has stopped..."));
				
				//Wait until SimulationRunner has stopped, before continuing
				waitUntilSimulationStops();

				logger.info(TimeManager.log("Simulation has now finished."));
			} 
		} else {
			logger.info("[Stop simulator button press] SimMainWindow: No simulation to stop");			
		}		
	}
		
	/**
	 * Wait until SimulationRunner has stopped
	 */
	public void waitUntilSimulationStops() {
		
		//Wait until conditionToFull is true, before continuing
		while(mSimulation.isRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Create / Remove tabs
	 * @param status - true to create, false to remove
	 */
	public void createTabs(boolean status) {
		
		initPricingTab(status);
		initPriceTimeGraph(status);
		initFailureTimeGraph(status);
		initConsistencyGraph(status);
		initCPUUtilisationGraph(status);
		initServicesGraph(status);
		initTemperatureGraph(status);
		initTabbedMapPane(status);
	}
	
	/**
	 * Triggered when the GUI indicates that we want to open a configuration to
	 * run
	 */
	void openGUIOption()
	{
		JFileChooser fc = GUIFileReader.getFileChooser("Select Configuration File To Open", "/resources/config", 
				new FileNameExtensionFilter(PROGRAM_NAME + " file (compressed)", "gz"));

		int rVal = fc.showOpenDialog(null);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			logger.info("Setting " + fc.getSelectedFile().getAbsolutePath() + " as the active configuration.");
			this.xmlConfigFile = new File(fc.getSelectedFile().getAbsolutePath());
			mStatusBar.filenameUpdated(fc.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Save the current state of the simulation as a CSV file
	 */
	void saveGUIOption()
	{
		JFileChooser fc = GUIFileReader.getFileChooser("Save simulation", "/resources/sim", 
				new FileNameExtensionFilter(PROGRAM_NAME + " file",	"csv"));

		File f = new File("Simulation.csv");
		fc.setSelectedFile(f);

		int chosenOption = fc.showSaveDialog(mMainFrame);
		if (chosenOption != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		logger.warn("TODO: This action will save the currrent simulation state to file.\nNot yet implemented!");
	}
	
	/**
	 * Prints String passed in to event log pane or prints out "event log is null" if there is no pane.
	 * 
	 * @param pLog
	 */
	public void logEvent(final String pLog)
	{
		final int index = tabbedPane.indexOfTab("Event Log");
		LogPane logPane = (LogPane) tabbedPane.getComponentAt(index);
		
		if (logPane == null)
		{
			System.out.println("========= EVENT LOG IS NULL =========");
		}else
		{
			logPane.println(pLog);
		}
	}
	
	/**
	 * Prints string passed in to failure log pane or prints out "failure log is null" if there is no pane.
	 * 
	 * @param pLog
	 */
	public void logFailure(final String pLog)
	{
		final int index = tabbedPane.indexOfTab("Failure Log");
		LogPane logPane = (LogPane) tabbedPane.getComponentAt(index);
		
		if (logPane == null)
		{
			System.out.println("========= FAILURE LOG IS NULL =========");
		}else
		{
			logPane.println(pLog);
		}
	}

	/**
	 * Returns the tabbed pane
	 * 
	 * @return the tabbed pane
	 */
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{	
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

    @Override
    public void componentResized(ComponentEvent e)
    {
    	//Check that chart panels are not null 
    	//Panels are null before simulation is started
    	
    	if(CostsTimeGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(CostsTimeGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
	        CostsTimeGraph.getInstance().getChartPanel().setPreferredSize(d);
	        CostsTimeGraph.getInstance().getChartPanel().repaint();
	        CostsTimeGraph.getInstance().getChartPanel().revalidate();
        }

    	if(TemperatureTimeGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(TemperatureTimeGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
            TemperatureTimeGraph.getInstance().getChartPanel().setPreferredSize(d);
            TemperatureTimeGraph.getInstance().getChartPanel().repaint();
            TemperatureTimeGraph.getInstance().getChartPanel().revalidate();
        }
        
    	if(FailureTimeGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(FailureTimeGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
            FailureTimeGraph.getInstance().getChartPanel().setPreferredSize(d);
            FailureTimeGraph.getInstance().getChartPanel().repaint();
            FailureTimeGraph.getInstance().getChartPanel().revalidate();

        }
        
    	if(CPUUtilisationGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(CPUUtilisationGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
            CPUUtilisationGraph.getInstance().getChartPanel().setPreferredSize(d);
            CPUUtilisationGraph.getInstance().getChartPanel().repaint();
            CPUUtilisationGraph.getInstance().getChartPanel().revalidate();
        }
        
    	if(ServicesGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(ServicesGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
            ServicesGraph.getInstance().getChartPanel().setPreferredSize(d);
            ServicesGraph.getInstance().getChartPanel().repaint();
            ServicesGraph.getInstance().getChartPanel().revalidate();           
        }
        
    	if(ConsistencyTimeGraph.getInstance().getChartPanel()!=null)
        {
            int index = tabbedPane.indexOfTab(ConsistencyTimeGraph.TAB_STRING);
            Dimension d = tabbedPane.getComponentAt(index).getSize();
            ConsistencyTimeGraph.getInstance().getChartPanel().setPreferredSize(d);
            ConsistencyTimeGraph.getInstance().getChartPanel().repaint();
            ConsistencyTimeGraph.getInstance().getChartPanel().revalidate();
        }
    }

	@Override
	public void componentShown(ComponentEvent e)
	{
	}

	/**
	 * World is updated. Initialise GUI using new world object.
	 */
	@Override
	public void worldUpdated(World world)
	{
		logger.debug("Updating world...");
		
		String switchedOffStatement = "This component is switched off.";
	
		SettingsManagerAccess sm = SettingsManager.getInstance();
		
    	//Initialise TimeSeries graphs and set as observers
		
    	//Costs
		if(Module.COSTS_MODULE.isActive()) {
			CostsTimeGraph.getInstance().initialise();
			CostsTimeGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());
        	mSimulation.addEventObserver(CostsTimeGraph.getInstance());
		} else {
			CostsTimeGraph.getInstance().removeAll();
			CostsTimeGraph.getInstance().repaint();
			CostsTimeGraph.getInstance().add(new JLabel(switchedOffStatement));
		}
		
        //Failures
		if(Module.FAILURE_MODULE.isActive()) {
	    	FailureTimeGraph.getInstance().initialise();
	    	FailureTimeGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());
	    	mSimulation.addEventObserver(FailureTimeGraph.getInstance());
		}else {
			FailureTimeGraph.getInstance().removeAll();
			FailureTimeGraph.getInstance().repaint();
			FailureTimeGraph.getInstance().add(new JLabel(switchedOffStatement));
		}
        
        //Services
        if(Module.SERVICE_MODULE.isActive()) {
        	ServicesGraph.getInstance().initialise();
        	ServicesGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());
        	mSimulation.addEventObserver(ServicesGraph.getInstance());
        } else {
        	ServicesGraph.getInstance().removeAll();
        	ServicesGraph.getInstance().repaint();
        	ServicesGraph.getInstance().add(new JLabel(switchedOffStatement));
        }
        
        //Utilisation
        CPUUtilisationGraph.getInstance().initialise();
        CPUUtilisationGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());
        mSimulation.addEventObserver(CPUUtilisationGraph.getInstance());
        
        //Temperature
        if(Module.THERMAL_MODULE.isActive()) {
            TemperatureTimeGraph.getInstance().initialise();
            TemperatureTimeGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());    
            mSimulation.addEventObserver(TemperatureTimeGraph.getInstance());
        } else {
        	TemperatureTimeGraph.getInstance().removeAll();
        	TemperatureTimeGraph.getInstance().repaint();
        	TemperatureTimeGraph.getInstance().add(new JLabel(switchedOffStatement));
        }

        //Subscriptions
        if(Module.SUBSCRIPTION_MODULE.isActive()) {
        	 ConsistencyTimeGraph.getInstance().initialise();
        	 ConsistencyTimeGraph.getInstance().setUnitTime(sm.getUnitTimeForGraphs());
        	 mSimulation.addEventObserver(ConsistencyTimeGraph.getInstance());
        } else {
        	ConsistencyTimeGraph.getInstance().removeAll();
        	ConsistencyTimeGraph.getInstance().repaint();
        	ConsistencyTimeGraph.getInstance().add(new JLabel(switchedOffStatement));
        }
        
        TabbedMapPane.getSingletonObject().initialise();  

		logger.debug("Setting renderers for each datacentre map...");
		for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
			RackDisplayRenderer newRenderer = new RackDisplayRenderer(i);
			TabbedMapPane.getSingletonObject().getMap(i).renderPhysicalLayout(world.getDatacentre(i), newRenderer);
		}
		
		logger.debug("Setting event queue observers...");
		//Set 2D maps as observers
		for(int i=0; i< World.getInstance().getNumberOfDatacentres(); i++) { 
    		mSimulation.addEventObserver(TabbedMapPane.getSingletonObject().getMap(i)); 
    	}
		
		//Set status bar as observer
    	mSimulation.addEventObserver(mStatusBar); 
    	
    	//Set Pricing/Costs panel as observer
    	mSimulation.addEventObserver(costPanel);
	}
}
