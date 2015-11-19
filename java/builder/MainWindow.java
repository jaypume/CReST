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
package builder;

import gui.util.GUIFileReader;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import builder.datacentre.DCGenWindow;
import builder.datacentre.GenProgressDialog;
import builder.datacentre.SimplePhysicalView;
import builder.datacentre.ViewEditDatacentreView;
import builder.dialog.AboutDialog;
import builder.dialog.LoadingDialog;
import builder.dialog.WelcomeDialog;
import builder.prefs.BuilderPreferences;
import builder.prefs.PreferencesWindow;
import builder.replacements.ReplacementServersPanel;
import builder.settings.SimSettingsView;
import config.DatacentreGenerator;
import config.DatacentreGeneratorProgressListener;
import config.EditorConfiguration;
import config.SettingsManager;
import config.XMLLoader;
import config.physical.ConfigCPU;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRAM;
import config.physical.ConfigServerType;
import config.physical.ConfigWorld;

public class MainWindow
{
	public static Logger logger = Logger.getLogger(MainWindow.class);

	public static int WINDOW_WIDTH = 1400; // 1224;
	public static int WINDOW_HEIGHT = 800; // 768;
	
	// Working Configuration
	private EditorConfiguration mEditConfig = null;
	private File savedTo;
	protected boolean changedSinceLastSave;

	private JFrame mMainFrame;
	private JPanel mMainPanel;
	private BuilderPreferences mPreferences;
	// ToolBar panes

	private MainToolBar mToolBar;
	private MenuBarHandler mMenuBarHandle;

	// Tabs
	private JTabbedPane mTabs;
	private ViewEditDatacentreView mViewEdit;
	private SimplePhysicalView mPhysical;
	
	//Datacentre list
	//private JList mDCList;

	public JFrame getFrame()
	{
		return mMainFrame;
	}
	
	public ViewEditDatacentreView getmViewEdit()
	{
		return mViewEdit;
	}
	
	public SimplePhysicalView getmPhysical()
	{
		return mPhysical;
	}
	
	public EditorConfiguration getEditConfig()
	{
		return mEditConfig;
	}
	
//	public JList getDCList()
//	{
//		return mDCList;
//	}
//
//	public void setDCList(JList mDCList)
//	{
//		this.mDCList = mDCList;
//	}

	private void initLookAndFeel()
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
	}

	public MainWindow(BuilderPreferences prefs, File toLoad) throws IOException
	{
		this(prefs);
		initLookAndFeel();
		load(toLoad);
	}

	/**
	 * Setup the window and display on screen.
	 */

	public MainWindow(BuilderPreferences prefs)
	{
		initLookAndFeel();
		
		// Store a reference to the preferences object
		mPreferences = prefs;
		// Create the frame we will use
		this.mMainFrame = new JFrame();

		// Setup frame options
		this.mMainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mMainFrame.addWindowListener(new WindowHandler());
		// Menu Bar
		mMenuBarHandle = new MenuBarHandler();
		mMainFrame.setJMenuBar(mMenuBarHandle.setupMenuBar());
		// Set starting size
		
		mMainFrame.setSize(MainWindow.WINDOW_WIDTH, MainWindow.WINDOW_HEIGHT);

		// Set program Icon
		mMainFrame.setIconImage(mPreferences.getProgramIcon());

		// Toolbars
		mToolBar = new MainToolBar(mPreferences, this);
		mMainFrame.add(mToolBar, BorderLayout.NORTH);

		// Show frame
		mMainFrame.setLocationByPlatform(true);
		this.mMainFrame.setVisible(true);

		// Set name
		refreshWindowTitle();

		// set visible
		refreshPanel();

		// Display welcome dialog
		if (mPreferences.getShowWelcomeScreen())
		{
			showWelcomeDialog();
		}
	}

	/**
	 * Refreshes the workspace when
	 */
	private void refreshPanel()
	{
		refreshWindowTitle();
		if (mMainPanel != null & !isLoaded())
		{
			// DISPOSE!
			destroyPanel();
		} else if (mMainPanel == null & isLoaded())
		{
			// LOAD!
			setupPanel();
		}
		mToolBar.editorUpdated();
		mMenuBarHandle.editorUpdated();
		mMainFrame.repaint();
	}

	/**
	 * Creates and instantiates the MainPanel. Run when a file is loaded.
	 */
	private void setupPanel()
	{
		this.mMainPanel = new JPanel();
		mMainPanel.setLayout(new BorderLayout());
		mMainFrame.add(mMainPanel, BorderLayout.CENTER);

		//Setup DC list
//		mDCList = new DCListPanel();
		
		// Setup tabs
		mTabs = new JTabbedPane();
		//mTabs.addChangeListener(new TabListener());
		mMainPanel.add(mTabs, BorderLayout.CENTER);
		
		mViewEdit = new ViewEditDatacentreView(mEditConfig.getConfWorld(), mPreferences, mMainFrame, this);

		mPhysical = new SimplePhysicalView(mEditConfig.getConfWorld(),
				mPreferences, mMainFrame, this);
		
		// Tabs
		mTabs.addTab("View & Edit", mPreferences.getImage("physical_tab.png"),
				mViewEdit, "View and Edit the Datacentres");
		mTabs.addTab("Physical", mPreferences.getImage("physical_tab.png"),
				mPhysical, "Configure the physical layout");
		mTabs.addTab("Simulation", mPreferences.getImage("simulation_tab.png"),
				new SimSettingsView(mEditConfig.getConfig(), mPreferences),
				"Configure the simulation options");
//		mTabs.addTab("Datacentre View", mPreferences.getImage("map_tab.png"),
//				new PhysicalView(mPreferences, mEditConfig),
//				"View the layout of a Datacentre");
		mTabs.addTab("Replacements", mPreferences
				.getImage("replacement_tab.png"), new ReplacementServersPanel(
				mPreferences, mMainFrame, mEditConfig.getReplacements()),
				"Configure Server Replacements");
		// mTabs.addTab("Map", mPreferences.getImage("physical_tab.png"), new
		// MapView(mEditConfig), "View Datacentre Map");
	}
	
	@SuppressWarnings("unused")
	private class TabListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{ 
			JTabbedPane pane = (JTabbedPane)e.getSource();			
			int sel = pane.getSelectedIndex(); //TODO AS 31.7.12- Replace tab index with something better
			//System.out.println("tab " + sel + " " + pane.getSelectedComponent().getClass());
			
			//If going to Physical, use selected DC from ViewEdit
//			if(sel == 1)
//			{
//				mPhysical.getWorldDCList().setListData(mEditConfig.getConfWorld().getDatacentreNames());
//				int i = mViewEdit.getDCList().getSelectedIndex();
//				mPhysical.getWorldDCList().setSelectedIndex(i);
//			}
//			//If going to ViewEdit, use selected DC from Physical
//			else if (sel == 0)
//			{
//				mViewEdit.updateAvailableDCList();
//				int i = mPhysical.getWorldDCList().getSelectedIndex();
//				mViewEdit.getDCList().setSelectedIndex(i);
//			}
			
		}	
	}

	private void destroyPanel()
	{
		mMainPanel.removeAll();
		mMainFrame.remove(mMainPanel);
		mMainPanel = null;
	}

	/**
	 * Informs us if a file is loaded into the editior right now.
	 * 
	 * @return true if a file is loaded, false if not.
	 */
	public boolean isLoaded()
	{
		return mEditConfig != null;
	}

	/**
	 * Lets parts of the UI know if the "run" option should be enabled
	 * 
	 * @return true if the file has been saved, false otherwise
	 */
	public boolean isRunOptionOn()
	{
		return (savedTo != null);
	}

	private void refreshWindowTitle()
	{
		String configName;
		if (mEditConfig == null)
		{
			configName = "";
		} else
		{
			configName = " - " + mEditConfig.getName();
		}
		mMainFrame.setTitle(mPreferences.getProgramName() + configName);
	}

	private void showWelcomeDialog()
	{
		new WelcomeDialog(mMainFrame, this, mPreferences);
	}

	/**
	 * This method is triggered when the GUI indicates that we want to quit.
	 */
	private void quitGUIOption()
	{
		// We've asked to quit. Make sure this is the desired action before we
		// close the program.
		int result = JOptionPane.showConfirmDialog(
				this.mMainFrame,
				"Are you sure you want to quit "
						+ mPreferences.getProgramName() + "?", "Quit "
						+ mPreferences.getProgramName(),
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(mPreferences.getIconResourcePath()
						+ "quit_big.png"));
		if (result == 0)
			System.exit(0);
	}

	/**
	 * This method is triggered when the GUI indicates that we want to view the
	 * About dialog.
	 */
	public void aboutGUIOption()
	{
		new AboutDialog(this.mMainFrame, mPreferences);
	}

	/**
	 * Triggered when the GUI indicates that we want to view the in-program
	 * help.
	 */
	public void helpGUIOption()
	{
		System.out.println("Help");
	}

	public void generateWindowGUIOption()
	{
		new DCGenWindow(this, mPreferences);
	}

	/**
	 * Passed an instantiated Generator, attaches a progress bar to it then runs
	 * it.
	 * 
	 * @param gen
	 *            The created generator
	 */
	public void generateDCGUIOption(DatacentreGenerator gen)
	{
		new GenProgressDialog(mMainFrame, gen);
		gen.addGeneratorListener(new GenListener());
		gen.start();
	}

	private class GenListener implements DatacentreGeneratorProgressListener
	{

		@Override
		public void updated(double progress)
		{
			// Don't care

		}

		@Override
		public void done(ConfigDatacentre pDatacentre)
		{
			int selected;
			
			if (DatacentreGenerator.mCalledFromView == 0)
			{
				//View Edit View generate- update selected DC
				logger.info("Generating and updating selected datacentre");
				selected = getmViewEdit().getDCList().getSelectedIndex();
				mEditConfig.getConfWorld().setDatacentre(pDatacentre, selected);
			}
			else
			{
				//Simple Physical View generate- add new DC
				logger.info("Generating and creating a new datacentre");
				mEditConfig.getConfWorld().addDatacentre(pDatacentre);
				selected = mEditConfig.getConfWorld().getDCs().length - 1;
			}
			
			mPhysical.populateWorld();
			mPhysical.getWorldDCList().setSelectedIndex(selected);
		}

		@Override
		public void canceled()
		{
			// Don't care

		}

	}

	/**
	 * Triggered when the GUI indicates that we want to create a new
	 * configuration
	 */
	public void newGUIOption(Window parent)
	{
		String name;

		do
		{
			// Prompt for name
			name = (String) JOptionPane.showInputDialog(parent,
					"Name of new configuration", "New Configuration",
					JOptionPane.QUESTION_MESSAGE,
					new ImageIcon(mPreferences.getIconResourcePath()
							+ "new_big.png"), null, null);
			// Check to see if we hit cancel?
			if (name == null)
				return;
			// Check name is valid
			if (name.equals(""))
			{
				JOptionPane.showMessageDialog(parent,
						"Please enter a name for the new configuration",
						"Invalid Name", JOptionPane.ERROR_MESSAGE,
						new ImageIcon(mPreferences.getIconResourcePath()
								+ "page_error_big.png"));
			}
		} while (name.equals(""));

		// Get rid of any dialog boxes we've used
		if (!parent.equals(mMainFrame))
		{
			parent.dispose();
		}

		// Check for existing file
		if (isLoaded())
		{
			int result = JOptionPane
					.showConfirmDialog(
							mMainFrame,
							"Are you sure you want to create a new configuration? This will close your current one and any unsaved work will be lost.",
							"Close Current Configuration",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							mPreferences.getImage("question_big.png"));
			if (result != JOptionPane.OK_OPTION)
			{
				return;
			}
		}

		closeFile();

		// Example Servers
		java.util.List<ConfigServerType> mServers = new ArrayList<ConfigServerType>();

		ConfigServerType[] servers = new ConfigServerType[3];

		// Server 1
		servers[0] = new ConfigServerType();
		servers[0].setModel("Cheap Server");
		servers[0].setMeanFailTime(10);
		servers[0].setSize(2);
		servers[0].setTimeAvailableFrom(0);
		ConfigCPU c1a = new ConfigCPU();
		c1a.setCores(2);
		c1a.setSpeed(1400);
		c1a.setModel("Cheap CPU");
		servers[0].addCPU(c1a);
		servers[0].addCPU(c1a.duplicate());
		ConfigRAM r1 = new ConfigRAM();
		r1.setModel("Cheap RAM");
		r1.setSize(1024);
		r1.setSpeed(667);
		servers[0].setRAM(r1);

		// Server 2
		servers[1] = new ConfigServerType();
		servers[1].setModel("Midrange Server");
		servers[1].setMeanFailTime(100);
		servers[1].setSize(2);
		servers[1].setTimeAvailableFrom(50);
		ConfigCPU c2 = new ConfigCPU();
		c2.setCores(4);
		c2.setSpeed(2800);
		c2.setModel("Midrange CPU");
		servers[1].addCPU(c2);
		servers[1].addCPU(c2.duplicate());
		ConfigRAM r2 = new ConfigRAM();
		r2.setModel("Midrange RAM");
		r2.setSize(2048);
		r2.setSpeed(1066);
		servers[1].setRAM(r2);

		// Server 3
		servers[2] = new ConfigServerType();
		servers[2].setModel("High-End Server");
		servers[2].setMeanFailTime(1000);
		servers[2].setSize(2);
		servers[2].setTimeAvailableFrom(100);
		ConfigCPU c3 = new ConfigCPU();
		c3.setCores(6);
		c3.setSpeed(3800);
		c3.setModel("High-End CPU");
		servers[2].addCPU(c3);
		servers[2].addCPU(c3.duplicate());
		servers[2].addCPU(c3.duplicate());
		servers[2].addCPU(c3.duplicate());
		ConfigRAM r3 = new ConfigRAM();
		r3.setModel("High-End RAM");
		r3.setSize(4096);
		r3.setSpeed(1066);
		servers[2].setRAM(r3);

		mServers.add(servers[0]);
		mServers.add(servers[1]);
		mServers.add(servers[2]);

		mEditConfig = new EditorConfiguration(name, new ConfigWorld(),
				new SettingsManager(), mServers);
		changedSinceLastSave = true;
		savedTo = null;
		refreshPanel();
	}

	void closeGUIOption()
	{
		int result = JOptionPane
				.showConfirmDialog(
						mMainFrame,
						"Are you sure you want to close this configuration? Any unsaved work will be lost.",
						"Close Configuration", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						mPreferences.getImage("question_big.png"));
		if (result == JOptionPane.OK_OPTION)
		{
			closeFile();
		}
	}

	public void redrawToolbar()
	{		
		this.mToolBar.populate();
		refreshPanel();
	}

	private void closeFile()
	{
		mEditConfig = null;
		changedSinceLastSave = false;
		savedTo = null;
		refreshPanel();
		mMainFrame.repaint();
	}

	/**
	 * Triggered when the GUI indicates that we want to open an existing, saved
	 * configuration
	 */
	public void openGUIOption()
	{
		JFileChooser fc = GUIFileReader.getFileChooser("Select Configuration File To Open", "/resources/config", 
				new FileNameExtensionFilter(mPreferences.getProgramName() + " file (compressed)", "gz"));

		int rVal = fc.showOpenDialog(null);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{

			// Check for existing file
			if (isLoaded())
			{
				int result = JOptionPane
						.showConfirmDialog(
								mMainFrame,
								"Are you sure you want to open this configuration? This will close your current one and any unsaved work will be lost.",
								"Close Current Configuration",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								mPreferences.getImage("question_big.png"));
				if (result != JOptionPane.OK_OPTION)
				{
					return;
				}
			}

			closeFile();
			try
			{
				load(fc.getSelectedFile());
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(
						mMainFrame,
						"Could not load file. Failed with error: "
								+ e.getMessage());
			}
		}
	}

	private void load(File toLoad) throws IOException
	{
		// Load the file
		// JOptionPane.showMessageDialog(mMainFrame, "Loading, please wait.",
		// "Loading Configuration", JOptionPane.INFORMATION_MESSAGE);
		LoadingDialog loading = new LoadingDialog(mMainFrame, mPreferences);
		try
		{
			mEditConfig = XMLLoader.loadConfig(toLoad);
		} catch (OutOfMemoryError e)
		{
			JOptionPane.showMessageDialog(mMainFrame, "Out of Memory, please re-run CloudSIM builder with more memory to load that configuration.", "Out Of Memory", JOptionPane.ERROR_MESSAGE, mPreferences.getImage("out_of_memory.png"));
		}
		changedSinceLastSave = false;
		savedTo = toLoad;
		refreshPanel();
		loading.dispose();
	}

//	private class LoadingDialog extends JDialog
//	{
//		private static final long serialVersionUID = 4711396100311235960L;
//		JLabel textLabel;
//
//		LoadingDialog(JFrame modal)
//		{
//			// Basic setup
//			super(modal, true);
//			this.setTitle("Loading Configuration");
//
//			// Content
//			this.setLayout(new BorderLayout());
//			textLabel = new JLabel("Loading, Please Wait.");
//			this.add(textLabel);
//		}
//
//		void setTextLabel(String label)
//		{
//			textLabel.setText(label);
//		}
//	}

	/**
	 * Run when the open configuration needs saving
	 * 
	 * @author Alex Sheppard
	 */
	void saveGUIOption()
	{

		JFileChooser fc = GUIFileReader.getFileChooser("Save Configuration File", "/resources/config/", 
				new FileNameExtensionFilter(
						mPreferences.getProgramName() + " file (compressed)", "gz"));
		
		File f = new File(mEditConfig.getName() + ".xml.gz");
		fc.setSelectedFile(f);

		int chosenOption = fc.showSaveDialog(mMainFrame);
		if (chosenOption != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		try
		{
			config.XMLSaver.save(new File(fc.getSelectedFile()
					.getAbsolutePath()), mEditConfig);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Saved '" + fc.getSelectedFile().getName() + "'.");
		savedTo = (new File(fc.getSelectedFile()
				.getAbsolutePath()));
		refreshPanel();
	}
	

	void runGUIOption()
	{
		// Pass in the correct arguments
		String args = savedTo.getAbsolutePath();
		
//		CReSTApp.main(new String[]{"-config", args, "-params", "resources/config//prop/params.properties"});
//		SimGUIMainWindow.getInstance().runSimulationOption();
//		String s = World.getInstance().getDatacentre(0).getName();
		
		try
		{
			//Check if CReST dist .jar file exists
			File f = new File(mPreferences.getJarFileLocation());
			if(!f.exists())
			{
				String message = "CReST .jar file does not exist";
				logger.error(message);
				throw new IOException(message);
			}
			
			//Execute .jar file with parameters
			String[] command =
			{ this.mPreferences.getJavaCommand(), mPreferences.getJavaArgs(),
					"-jar", mPreferences.getJarFileLocation(), "-c", args };
			Runtime.getRuntime().exec(command);
		} catch (IOException e)
		{
			JOptionPane.showMessageDialog(mMainFrame,
					"CReST failed to run with error: " + e.getMessage(),
					"Failed to Run", JOptionPane.ERROR_MESSAGE);
		}
	}

	void preferencesGUIOption(String card)
	{
		new PreferencesWindow(mPreferences, this, card);
	}

	public void preferencesGUIOption()
	{
		new PreferencesWindow(mPreferences, this,
				PreferencesWindow.DEFAULT_STARTING_CARD);
	}

	public void renameGUIOption()
	{
		String newName = JOptionPane.showInputDialog(mMainFrame,
				"Rename Configuration", mEditConfig.getName());
		if (newName != null && !newName.equals(""))
		{
			mEditConfig.setName(newName);
			refreshPanel();
		} else if (newName != null)
		{
			JOptionPane.showMessageDialog(mMainFrame,
					"That is not a valid name", "Invalid Name",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This class is used to create the Menu bar, listen for it's events and
	 * trigger the GUIOption methods in the main class.
	 * 
	 * @author james
	 * 
	 */
	private class MenuBarHandler implements ActionListener
	{

		JMenuBar bar;
		JMenu fileMenu;
		JMenu viewMenu;
		JMenu configMenu;
		JMenu toolsMenu;
		JMenu helpMenu;
		// File menu
		JMenuItem newItem;
		JMenuItem openItem;
		JMenuItem saveItem;
		JMenuItem closeItem;
		JMenuItem welcomeScreenItem;
		JMenuItem quitItem;
		// View Menu
		JCheckBoxMenuItem cbToolBarItem;
		// Simulation menu
		JMenuItem runItem;
		JMenuItem renameItem;
//		JMenuItem generateItem;
		// Tools Menu
		// JMenuItem nonPhysicalDatacentreRelatedConfigurationStuffs;
		JMenuItem preferencesItem;
		JMenuItem runPreferencesItem;
		JMenuItem toolbarPreferencesItem;
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
			viewMenu = new JMenu("View");
			configMenu = new JMenu("Simulation");
			toolsMenu = new JMenu("Tools");
			helpMenu = new JMenu("Help");

			// File menu
			fileMenu.setMnemonic(KeyEvent.VK_F);

			newItem = new JMenuItem("New", new ImageIcon(
					mPreferences.getIconResourcePath() + "new.png"));
			newItem.setMnemonic(KeyEvent.VK_N);
			newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					KeyEvent.CTRL_MASK));
			newItem.addActionListener(this);

			openItem = new JMenuItem("Open", new ImageIcon(
					mPreferences.getIconResourcePath() + "open.png"));
			openItem.setMnemonic(KeyEvent.VK_O);
			openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					KeyEvent.CTRL_MASK));
			openItem.addActionListener(this);

			saveItem = new JMenuItem("Save", new ImageIcon(
					mPreferences.getIconResourcePath() + "save.png"));
			saveItem.setMnemonic(KeyEvent.VK_S);
			saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					KeyEvent.CTRL_MASK));
			saveItem.addActionListener(this);

			closeItem = new JMenuItem("Close",
					mPreferences.getImage("close.png"));
			closeItem.setMnemonic(KeyEvent.VK_C);
			closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
					KeyEvent.CTRL_MASK));
			closeItem.addActionListener(this);

			welcomeScreenItem = new JMenuItem("Show Welcome Screen");
			welcomeScreenItem.setMnemonic(KeyEvent.VK_W);
			welcomeScreenItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_H, KeyEvent.CTRL_MASK));
			welcomeScreenItem.addActionListener(this);

			quitItem = new JMenuItem("Quit", new ImageIcon(
					mPreferences.getIconResourcePath() + "quit.png"));
			quitItem.setMnemonic(KeyEvent.VK_Q);
			quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					KeyEvent.CTRL_MASK));
			quitItem.addActionListener(this);

			fileMenu.add(newItem);
			fileMenu.add(openItem);
			fileMenu.add(saveItem);
			fileMenu.addSeparator();
			fileMenu.add(closeItem);
			fileMenu.addSeparator();
			fileMenu.add(welcomeScreenItem);
			fileMenu.addSeparator();
			fileMenu.add(quitItem);

			// View menu
			viewMenu.setMnemonic(KeyEvent.VK_V);

			cbToolBarItem = new JCheckBoxMenuItem("Show toolbar");
			cbToolBarItem.setState(true);
			cbToolBarItem.addActionListener(this);

			viewMenu.add(cbToolBarItem);

			// Simulation menu
			configMenu.setMnemonic(KeyEvent.VK_C);
			
			runItem = new JMenuItem("Run Simulator", new ImageIcon(
					mPreferences.getIconResourcePath() + "run.png"));
			runItem.setMnemonic(KeyEvent.VK_R);
			runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			runItem.addActionListener(this);

			renameItem = new JMenuItem("Rename Configuration",
					mPreferences.getImage("config_rename.png"));
			renameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
					KeyEvent.CTRL_MASK));
			renameItem.addActionListener(this);

//			generateItem = new JMenuItem("Generate Datacentre",
//					mPreferences.getImage("datacentre_gen.png"));
//			generateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
//					KeyEvent.CTRL_MASK));
//			generateItem.addActionListener(this);

			configMenu.add(runItem);
			configMenu.add(renameItem);
//			configMenu.add(generateItem);

			// Tools menu
			toolsMenu.setMnemonic(KeyEvent.VK_T);

			/*
			 * nonPhysicalDatacentreRelatedConfigurationStuffs = new JMenuItem(
			 * "nonPhysicalDatacentreRelatedConfigurationStuffs",
			 * KeyEvent.VK_N); //
			 * nonPhysicalDatacentreRelatedConfigurationStuffs
			 * .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, //
			 * KeyEvent.CTRL_MASK));
			 * nonPhysicalDatacentreRelatedConfigurationStuffs
			 * .addActionListener(this);
			 */			

			preferencesItem = new JMenuItem("Preferences", new ImageIcon(
					mPreferences.getIconResourcePath() + "preferences.png"));
			preferencesItem.setMnemonic(KeyEvent.VK_P);
			preferencesItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F2, 0));
			preferencesItem.addActionListener(this);

			runPreferencesItem = new JMenuItem("Run Preferences",
					mPreferences.getImage("run_prefs.png"));
			runPreferencesItem.setMnemonic(KeyEvent.VK_R);
			runPreferencesItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F3, 0));
			runPreferencesItem.addActionListener(this);

			toolbarPreferencesItem = new JMenuItem("Toolbar Preferences",
					mPreferences.getImage("toolbar_prefs.png"));
			toolbarPreferencesItem.setMnemonic(KeyEvent.VK_T);
			toolbarPreferencesItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F4, 0));
			toolbarPreferencesItem.addActionListener(this);

			// toolsMenu.add(nonPhysicalDatacentreRelatedConfigurationStuffs);
			toolsMenu.add(preferencesItem);
			toolsMenu.add(runPreferencesItem);
			toolsMenu.add(toolbarPreferencesItem);

			// Help menu
			helpMenu.setMnemonic(KeyEvent.VK_H);

			helpItem = new JMenuItem(mPreferences.getProgramName() + " Help",
					new ImageIcon(mPreferences.getIconResourcePath()
							+ "help.png"));
			helpItem.setMnemonic(KeyEvent.VK_H);
			helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			helpItem.addActionListener(this);

			aboutItem = new JMenuItem("About " + mPreferences.getProgramName(),
					new ImageIcon(mPreferences.getIconResourcePath()
							+ "about.png"));
			aboutItem.addActionListener(this);

			helpMenu.add(helpItem);
			helpMenu.add(aboutItem);

			// Add menus to bar and return
			bar.add(fileMenu);
			bar.add(viewMenu);
			bar.add(configMenu);
			bar.add(toolsMenu);
			bar.add(helpMenu);
			return bar;
		}

		public void editorUpdated()
		{
			saveItem.setEnabled(isLoaded());
			closeItem.setEnabled(isLoaded());
			renameItem.setEnabled(isLoaded());
//			generateItem.setEnabled(isLoaded());
			runItem.setEnabled(isRunOptionOn());
		}

		public void actionPerformed(ActionEvent e)
		{
			// Listen to events
			Object source = e.getSource();
			// Test for each source
			if (source.equals(newItem))
			{
				newGUIOption(mMainFrame);
			} else if (source.equals(openItem))
			{
				openGUIOption();
			} else if (source.equals(saveItem))
			{
				saveGUIOption();
			} else if (source.equals(closeItem))
			{
				closeGUIOption();
			} else if (source.equals(quitItem))
			{
				quitGUIOption();
			} else if (source.equals(helpItem))
			{
				helpGUIOption();
			} else if (source.equals(aboutItem))
			{
				aboutGUIOption();
			} else if (source.equals(cbToolBarItem))
			{
				mToolBar.setVisible(cbToolBarItem.getState());
			} else if (source.equals(runItem))
			{
				runGUIOption();
			} else if (source.equals(preferencesItem))
			{
				preferencesGUIOption();
			} else if (source.equals(runPreferencesItem))
			{
				preferencesGUIOption(PreferencesWindow.RUN_PREFS_NAME);
			} else if (source.equals(toolbarPreferencesItem))
			{
				preferencesGUIOption(PreferencesWindow.TOOLBAR_PREFS_NAME);
			} else if (source.equals(welcomeScreenItem))
			{
				showWelcomeDialog();
			} else if (source.equals(renameItem))
			{
				renameGUIOption();
//			} else if (source.equals(generateItem))
//			{
//				generateWindowGUIOption();
			}
		}
	}

	/**
	 * Looks after the events triggered by the window itself.
	 * 
	 * @author jl9804
	 * 
	 */
	private class WindowHandler implements WindowListener
	{

		@Override
		public void windowActivated(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e)
		{
			quitGUIOption();
		}

		@Override
		public void windowDeactivated(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent e)
		{
			// TODO Auto-generated method stub

		}
	}
}
