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
package builder.datacentre;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import builder.MainWindow;
import builder.prefs.BuilderPreferences;
import builder.settings.NonPhysicalDatacentreRelatedConfigurationStuffs;

import sim.physical.bo.ServerWontFitException;
import utility.direction.CompassDirection;
import config.DatacentreGeneratorProgressListener;
import config.physical.ConfigAisle;
import config.physical.ConfigCPU;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRAM;
import config.physical.ConfigRack;
import config.physical.ConfigServer;
import config.physical.ConfigWorld;

public class SimplePhysicalView extends JScrollPane
{
	public static Logger logger = Logger.getLogger(SimplePhysicalView.class);
	private static final long serialVersionUID = 3894929033447967304L;

	private Frame thisPanel;
	private MainWindow MainWindow;

	private ConfigWorld mWorkingWorld;
	private ConfigDatacentre mWorkingDC;
	private ConfigDatacentre mWorkingDCOld;
	private ConfigAisle mWorkingAisle;
	private ConfigRack mWorkingRack;
	private ConfigServer mWorkingServer;
	private ConfigCPU mWorkingCPU;

	private BuilderPreferences mPrefs;

	// Update Varabiles
	private boolean propagateChanges; // Specifies whether panels should update
										// panels after them or not.
	// World Panel
	private JPanel mWorldPanel;
	private JList mWorldDCList;
	private JButton mWorldDCAddBtn;
	private JButton mWorldDCGenBtn;
	private JButton mWorldDCDelBtn;
	private JSpinner mWorldDCNetworkDistance;

	// DC Panel
	private JPanel mDCPanel;
	private JTextField mDCName;
	private JList mDCAisleList;
	private JButton mDCAisleAddBtn;
	private JButton mDCAisleDelBtn;
	private JSpinner mDCAisleNetworkDistance;
	private JSpinner mDCsizeX;
	private JSpinner mDCsizeY;
	private boolean mDCModifiying;
	private JButton mDCPricingBtn;

	// Aisle Panel
	private JPanel mAislePanel;
	private JTextField mAisleName;
	private JList mAisleRackList;
	private JButton mAisleRackAddBtn;
	private JButton mAisleRackDelBtn;
	private JSpinner mAisleRackNetworkDistance;
	private JSpinner mAisleLocX;
	private JSpinner mAisleLocY;
	private boolean mAisleModifiying;

	// Rack Panel
	private JPanel mRackPanel;
	private JTextField mRackName;
	private JList mRackServerList;
	private JButton mRackServerAddBtn;
	private JButton mRackServerDelBtn;
	private JComboBox mServerDirectionBox;
	private JSpinner mRackServerNetworkDistance;
	private JSpinner mRackPos;
	private JSpinner mRackcount;
	private boolean mRackModifiying;

	// Server Panel
	private JPanel mServerPanel;
	private JTextField mServerModel;
	private JList mServerCPUList;
	private JButton mServerCPUAddBtn;
	private JButton mServerCPUDelBtn;
	private JSpinner mServerSize;
	private JSpinner mServerMeanFailureTimeInDays;
	private JTextField mServerRAMModel;
	private JSpinner mServerRAMSpeed;
	private JSpinner mServerRAMSize;
	private boolean mServerModifiying;

	// CPU Panel
	private JPanel mCPUPanel;
	private JTextField mCPUModel;
	private JSpinner mCPUSpeed;
	private JSpinner mCPUCores;
	private boolean mCPUModifiying;
	
	// //////////// //
	// CONSTRUCTORS //
	// //////////// //

	public SimplePhysicalView(ConfigWorld pWorld, BuilderPreferences pPrefs, Frame parentFrame, MainWindow mainWindow)
	{
		// Setup this so that contained classes can access it
		this.thisPanel = parentFrame;
		this.MainWindow = mainWindow;

		// Setup world object
		mWorkingWorld = pWorld;

		mPrefs = pPrefs;

		// /////////////////
		// Create Panels //
		// /////////////////
		JPanel topPanel = new JPanel();

		propagateChanges = true;

		// mFrameWorld.add(Box.createVerticalGlue());
		// Datacentre Panel
		setupWorldPanel();
		setupDCPanel();
		setupAislePanel();
		setupRackPanel();
		setupServerPanel();
		setupCPUPanel();

		// Add to main panel
		topPanel.setLayout(new GridLayout(0, 6));
		topPanel.setPreferredSize(new Dimension(builder.MainWindow.WINDOW_WIDTH-150, builder.MainWindow.WINDOW_HEIGHT-150)); //TODO AS 30.7.12- replace gridlayout with gridbag to fix this 'hack'
		topPanel.add(mWorldPanel);
		topPanel.add(mDCPanel);
		topPanel.add(mAislePanel);
		topPanel.add(mRackPanel);
		topPanel.add(mServerPanel);
		topPanel.add(mCPUPanel);
		
		//Add panels to JScrollPane
		this.setBorder(BorderFactory.createEmptyBorder());
		this.getViewport().add(topPanel);

		// Run setup
		populateWorld();

	}

	// ///////////// //
	// SETUP METHODS //
	// ///////////// //

	private void setupWorldPanel()
	{
		// Button Handler
		WorldPanelEventHandler handle = new WorldPanelEventHandler();

		// World Panel
		mWorldPanel = new JPanel();
		mWorldPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("World"), new EmptyBorder(10, 10, 10, 10)));
		mWorldPanel.setLayout(new BoxLayout(mWorldPanel, BoxLayout.PAGE_AXIS));

		// Datacentre list
		mWorldDCList = new JList();
		mWorldDCList.setModel(new DefaultListModel());
		mWorldDCList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// mWorldDCList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mWorldDCList.setVisibleRowCount(-1);
		mWorldDCList.addListSelectionListener(new DatacentreListListener());
		//mWorldDCList = MainWindow.getDCList();
		JScrollPane dcListScroller = new JScrollPane(mWorldDCList);
		dcListScroller.setPreferredSize(new Dimension(250, 80));
		mWorldPanel.add(dcListScroller);

		// Datacentre controls
		JPanel dcControlPanel = new JPanel();
		dcControlPanel.setLayout(new BoxLayout(dcControlPanel,
				BoxLayout.LINE_AXIS));
		mWorldDCAddBtn = new JButton("Add", mPrefs.getImage("datacentre_add.png"));
		mWorldDCAddBtn.setToolTipText("Add Datacentre");
		mWorldDCAddBtn.addActionListener(handle);
		dcControlPanel.add(mWorldDCAddBtn);
		dcControlPanel.add(Box.createHorizontalGlue());
//		mWorldDCGenBtn = new JButton(mPrefs.getImage("datacentre_gen.png"));
//		mWorldDCGenBtn.addActionListener(handle);
//		dcControlPanel.add(mWorldDCGenBtn);
//		dcControlPanel.add(Box.createHorizontalGlue());
		mWorldDCDelBtn = new JButton("Remove", mPrefs.getImage("datacentre_delete.png"));
		mWorldDCDelBtn.addActionListener(handle);
		dcControlPanel.add(mWorldDCDelBtn);
		mWorldPanel.add(Box.createVerticalStrut(10));
		mWorldPanel.add(dcControlPanel);
		mWorldPanel.add(Box.createVerticalStrut(20));
		
		//Generate
		JPanel generatePanel = new JPanel();
		generatePanel.setLayout(new BoxLayout(generatePanel, BoxLayout.LINE_AXIS));
		generatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		generatePanel.add(Box.createHorizontalGlue());
		mWorldDCGenBtn = new JButton("Generate", mPrefs.getImage("datacentre_gen.png"));
		mWorldDCGenBtn.addActionListener(handle);
		generatePanel.add(mWorldDCGenBtn);
		generatePanel.add(Box.createHorizontalGlue());
		mWorldPanel.add(generatePanel);
		mWorldPanel.add(Box.createVerticalStrut(20));

		// Datacentre network distance
		JLabel dcNetworkDistanceLabel = new JLabel("Network Distance");
		JPanel dcNetworkDistanceLabelPanel = new JPanel();
		dcNetworkDistanceLabelPanel.setMaximumSize(new Dimension(
				Integer.MAX_VALUE, 10));
		dcNetworkDistanceLabelPanel.add(dcNetworkDistanceLabel);
		mWorldPanel.add(dcNetworkDistanceLabelPanel);
		mWorldPanel.add(Box.createVerticalStrut(10));
		mWorldDCNetworkDistance = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mWorldDCNetworkDistance.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				15));
		mWorldPanel.add(mWorldDCNetworkDistance);
	}

	private void setupDCPanel()
	{
		// / DC Panel
		mDCPanel = new JPanel();
		mDCPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Datacentre"),
				new EmptyBorder(10, 10, 10, 10)));
		mDCPanel.setLayout(new BoxLayout(mDCPanel, BoxLayout.PAGE_AXIS));

		// / Listener
		DatacentrePanelEventHandler event = new DatacentrePanelEventHandler();

		// Name
		mDCPanel.add(getPanelOfText(new JLabel("Name")));
		mDCName = new JTextField();
		mDCName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mDCName.getDocument().addDocumentListener(event);
		mDCPanel.add(mDCName);

		mDCPanel.add(getPanelOfText(new JLabel("Aisles")));
		// Aisle list
		mDCAisleList = new JList();
		mDCAisleList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// mDCAisleList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mDCAisleList.setVisibleRowCount(-1);
		mDCAisleList.addListSelectionListener(new AisleListListener());
		JScrollPane dcListScroller = new JScrollPane(mDCAisleList);
		dcListScroller.setPreferredSize(new Dimension(250, 80));
		mDCPanel.add(dcListScroller);

		// Aisle controls
		JPanel dcControlPanel = new JPanel();
		dcControlPanel.setLayout(new BoxLayout(dcControlPanel,
				BoxLayout.LINE_AXIS));
		mDCAisleAddBtn = new JButton("Add", mPrefs.getImage("aisle_add.png"));
		mDCAisleAddBtn.addActionListener(event);
		dcControlPanel.add(mDCAisleAddBtn);
		dcControlPanel.add(Box.createHorizontalGlue());
		mDCAisleDelBtn = new JButton("Remove", mPrefs.getImage("aisle_delete.png"));
		mDCAisleDelBtn.addActionListener(event);
		dcControlPanel.add(mDCAisleDelBtn);
		mDCPanel.add(Box.createVerticalStrut(10));
		mDCPanel.add(dcControlPanel);

		mDCPanel.add(Box.createVerticalStrut(20));

		// Datacentre network distance
		mDCPanel.add(getPanelOfText(new JLabel("Network Distance")));
		mDCPanel.add(Box.createVerticalStrut(10));
		mDCAisleNetworkDistance = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mDCAisleNetworkDistance.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				15));
		mDCAisleNetworkDistance.addChangeListener(event);
		mDCPanel.add(mDCAisleNetworkDistance);

		// mDCPanel.add(Box.createVerticalStrut(20));
		mDCPanel.add(Box.createVerticalStrut(20));
		mDCPanel.add(getPanelOfText(new JLabel("Datacentre Size")));
		mDCPanel.add(Box.createVerticalStrut(10));
		mDCsizeX = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE,
				1));
		mDCsizeX.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mDCsizeX.addChangeListener(event);
		mDCPanel.add(mDCsizeX);
		mDCPanel.add(Box.createVerticalStrut(5));
		mDCsizeY = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE,
				1));
		mDCsizeY.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mDCsizeY.addChangeListener(event);
		mDCPanel.add(mDCsizeY);

		mDCPanel.add(Box.createVerticalStrut(20));	
		
		// NonPhysicalDatacentreRelatedConfigurationStuffs //TODO:Code tidy
		JPanel dcPricingPanel = new JPanel();
		dcPricingPanel.setLayout(new BoxLayout(dcPricingPanel, BoxLayout.LINE_AXIS));
		dcPricingPanel.setMaximumSize(new Dimension(
				Integer.MAX_VALUE, 10));
		
		dcPricingPanel.add(Box.createHorizontalGlue());
		mDCPricingBtn = new JButton("Pricing", mPrefs.getImage("datacentre_pricing.png"));
		mDCPricingBtn.addActionListener(event);
		dcPricingPanel.add(mDCPricingBtn);
		dcPricingPanel.add(Box.createHorizontalGlue());
		mDCPanel.add(dcPricingPanel);
	}

	private void setupAislePanel()
	{
		// Aisle Panel
		mAislePanel = new JPanel();
		mAislePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Aisle"), new EmptyBorder(10, 10, 10, 10)));
		mAislePanel.setLayout(new BoxLayout(mAislePanel, BoxLayout.PAGE_AXIS));

		// Listener
		AislePanelEventHandler event = new AislePanelEventHandler();

		// Name
		mAislePanel.add(getPanelOfText(new JLabel("Name")));
		mAisleName = new JTextField();
		mAisleName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mAisleName.getDocument().addDocumentListener(event);
		mAislePanel.add(mAisleName);

		

		mAislePanel.add(getPanelOfText(new JLabel("Racks")));
		// Aisle list
		mAisleRackList = new JList();
		mAisleRackList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// mAisleRackList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mAisleRackList.setVisibleRowCount(-1);
		mAisleRackList.addListSelectionListener(new RackListListener());
		JScrollPane dcListScroller = new JScrollPane(mAisleRackList);
		dcListScroller.setPreferredSize(new Dimension(250, 80));
		mAislePanel.add(dcListScroller);

		// Aisle controls
		JPanel dcControlPanel = new JPanel();
		dcControlPanel.setLayout(new BoxLayout(dcControlPanel,
				BoxLayout.LINE_AXIS));
		mAisleRackAddBtn = new JButton("Add", mPrefs.getImage("rack_add.png"));
		mAisleRackAddBtn.addActionListener(event);
		dcControlPanel.add(mAisleRackAddBtn);
		dcControlPanel.add(Box.createHorizontalGlue());
		mAisleRackDelBtn = new JButton("Remove", mPrefs.getImage("rack_delete.png"));
		mAisleRackDelBtn.addActionListener(event);
		dcControlPanel.add(mAisleRackDelBtn);
		mAislePanel.add(Box.createVerticalStrut(10));
		mAislePanel.add(dcControlPanel);

		mAislePanel.add(Box.createVerticalStrut(20));

		mAislePanel.add(getPanelOfText(new JLabel("Position")));
		mAisleLocX = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mAisleLocX.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mAisleLocX.addChangeListener(event);
		mAislePanel.add(mAisleLocX);
		
		mAislePanel.add(Box.createVerticalStrut(10));
		
		mAisleLocY = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mAisleLocY.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mAisleLocY.addChangeListener(event);
		mAislePanel.add(mAisleLocY);
		
		mAislePanel.add(Box.createVerticalStrut(20));
		
		// Datacentre network distance
		mAislePanel.add(getPanelOfText(new JLabel("Network Distance")));
		mAislePanel.add(Box.createVerticalStrut(10));
		mAisleRackNetworkDistance = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mAisleRackNetworkDistance.setMaximumSize(new Dimension(
				Integer.MAX_VALUE, 15));
		mAisleRackNetworkDistance.addChangeListener(event);
		mAislePanel.add(mAisleRackNetworkDistance);
	}

	private void setupRackPanel()
	{
		// / DC Panel
		mRackPanel = new JPanel();
		mRackPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Rack"), new EmptyBorder(10, 10, 10, 10)));
		mRackPanel.setLayout(new BoxLayout(mRackPanel, BoxLayout.PAGE_AXIS));

		// / Listener
		RackPanelEventHandler event = new RackPanelEventHandler();

		// Name
		mRackPanel.add(getPanelOfText(new JLabel("Name")));
		mRackName = new JTextField();
		mRackName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mRackName.getDocument().addDocumentListener(event);
		mRackPanel.add(mRackName);

		mRackPanel.add(getPanelOfText(new JLabel("Count")));
		mRackcount = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mRackcount.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mRackcount.addChangeListener(event);
		mRackPanel.add(mRackcount);

		mRackPanel.add(getPanelOfText(new JLabel("Servers")));
		// Aisle list
		mRackServerList = new JList();
		mRackServerList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// mRackServerList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mRackServerList.setVisibleRowCount(-1);
		mRackServerList.addListSelectionListener(new ServerListListener());
		JScrollPane svListScroller = new JScrollPane(mRackServerList);
		svListScroller.setPreferredSize(new Dimension(250, 80));
		mRackPanel.add(svListScroller);

		// Aisle controls
		JPanel svControlPanel = new JPanel();
		svControlPanel.setLayout(new BoxLayout(svControlPanel,
				BoxLayout.LINE_AXIS));
		mRackServerAddBtn = new JButton("Add", mPrefs.getImage("server_add.png"));
		mRackServerAddBtn.addActionListener(event);
		svControlPanel.add(mRackServerAddBtn);
		svControlPanel.add(Box.createHorizontalGlue());
		mRackServerDelBtn = new JButton("Remove", mPrefs.getImage("server_delete.png"));
		mRackServerDelBtn.addActionListener(event);
		svControlPanel.add(mRackServerDelBtn);
		mRackPanel.add(Box.createVerticalStrut(10));
		mRackPanel.add(svControlPanel);

		mRackPanel.add(Box.createVerticalStrut(20));
		
		//Server Direction
		mRackPanel.add(getPanelOfText(new JLabel("Front Direction of All Servers")));
		mRackPanel.add(Box.createVerticalStrut(10));
		mServerDirectionBox = new JComboBox(CompassDirection.values());
		mServerDirectionBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mServerDirectionBox.addActionListener(event);
		mRackPanel.add(mServerDirectionBox);
		
		mRackPanel.add(Box.createVerticalStrut(20));

		// Datacentre network distance
		mRackPanel.add(getPanelOfText(new JLabel("Network Distance")));
		mRackPanel.add(Box.createVerticalStrut(10));
		mRackServerNetworkDistance = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mRackServerNetworkDistance.setMaximumSize(new Dimension(
				Integer.MAX_VALUE, 15));
		mRackServerNetworkDistance.addChangeListener(event);
		mRackPanel.add(mRackServerNetworkDistance);

		mRackPanel.add(Box.createVerticalStrut(20));
		
		mRackPanel.add(getPanelOfText(new JLabel("Rack Position")));
		mRackPanel.add(Box.createVerticalStrut(10));
		mRackPos = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE,
				1));
		mRackPos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mRackPos.addChangeListener(event);
		mRackPanel.add(mRackPos);

	}

	private void setupServerPanel()
	{
		// / DC Panel
		mServerPanel = new JPanel();
		mServerPanel
				.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Server"),
						new EmptyBorder(10, 10, 10, 10)));
		mServerPanel
				.setLayout(new BoxLayout(mServerPanel, BoxLayout.PAGE_AXIS));

		// / Listener
		ServerPanelEventHandler event = new ServerPanelEventHandler();

		// Name
		mServerPanel.add(getPanelOfText(new JLabel("Model")));
		mServerModel = new JTextField();
		mServerModel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mServerModel.getDocument().addDocumentListener(event);
		mServerPanel.add(mServerModel);

		mServerPanel.add(getPanelOfText(new JLabel("Size")));
		mServerSize = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mServerSize.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mServerSize.addChangeListener(event);
		mServerPanel.add(mServerSize);

		mServerPanel.add(getPanelOfText(new JLabel("CPUs")));
		// Aisle list
		mServerCPUList = new JList();
		mServerCPUList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mServerCPUList.setVisibleRowCount(-1);
		mServerCPUList.addListSelectionListener(new CPUListListener());
		JScrollPane cpListScroller = new JScrollPane(mServerCPUList);
		cpListScroller.setPreferredSize(new Dimension(250, 80));
		mServerPanel.add(cpListScroller);

		// Aisle controls
		JPanel cpControlPanel = new JPanel();
		cpControlPanel.setLayout(new BoxLayout(cpControlPanel,
				BoxLayout.LINE_AXIS));
		mServerCPUAddBtn = new JButton("Add", mPrefs.getImage("cpu_add.png"));
		mServerCPUAddBtn.addActionListener(event);
		cpControlPanel.add(mServerCPUAddBtn);
		cpControlPanel.add(Box.createHorizontalGlue());
		mServerCPUDelBtn = new JButton("Remove", mPrefs.getImage("cpu_delete.png"));
		mServerCPUDelBtn.addActionListener(event);
		cpControlPanel.add(mServerCPUDelBtn);
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerPanel.add(cpControlPanel);

		mServerPanel.add(Box.createVerticalStrut(10));
		
		mServerPanel.add(getPanelOfText(new JLabel("Mean Fail Time (Days)")));
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerMeanFailureTimeInDays = new JSpinner(new SpinnerNumberModel(0, 0,
				Long.MAX_VALUE, 1));
		mServerMeanFailureTimeInDays.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mServerMeanFailureTimeInDays.addChangeListener(event);
		((JSpinner.NumberEditor) mServerMeanFailureTimeInDays.getEditor()).getTextField().getDocument().addDocumentListener(event);
		mServerPanel.add(mServerMeanFailureTimeInDays);
		
		mServerPanel.add(Box.createVerticalStrut(20));

		mServerPanel.add(getPanelOfText(new JLabel("Memory Model")));
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerRAMModel = new JTextField();
		mServerRAMModel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mServerRAMModel.getDocument().addDocumentListener(event);
		mServerPanel.add(mServerRAMModel);

		mServerPanel.add(Box.createVerticalStrut(10));
		mServerPanel.add(getPanelOfText(new JLabel("Memory Size")));
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerRAMSize = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mServerRAMSize.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mServerRAMSize.addChangeListener(event);
		mServerPanel.add(mServerRAMSize);

		mServerPanel.add(Box.createVerticalStrut(10));
		mServerPanel.add(getPanelOfText(new JLabel("Memory Speed")));
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerRAMSpeed = new JSpinner(new SpinnerNumberModel(0, 0,
				Double.MAX_VALUE, 1));
		mServerRAMSpeed.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mServerRAMSpeed.addChangeListener(event);
		mServerPanel.add(mServerRAMSpeed);

	}

	private void setupCPUPanel()
	{
		// / DC Panel
		mCPUPanel = new JPanel();
		mCPUPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("CPU"), new EmptyBorder(10, 10, 10, 10)));
		mCPUPanel.setLayout(new BoxLayout(mCPUPanel, BoxLayout.PAGE_AXIS));

		// / Listener
		CPUPanelEventHandler event = new CPUPanelEventHandler();

		// Name
		mCPUPanel.add(getPanelOfText(new JLabel("Model")));
		mCPUModel = new JTextField();
		mCPUModel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		mCPUModel.getDocument().addDocumentListener(event);
		mCPUPanel.add(mCPUModel);
		
		mCPUPanel.add(getPanelOfText(new JLabel("Cores")));
		mCPUCores = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mCPUCores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mCPUCores.addChangeListener(event);
		mCPUPanel.add(mCPUCores);
		
		mCPUPanel.add(getPanelOfText(new JLabel("Speed")));
		mCPUSpeed = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mCPUSpeed.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mCPUSpeed.addChangeListener(event);
		mCPUPanel.add(mCPUSpeed);
		
		mCPUPanel.add(Box.createVerticalGlue());

	}

	private JPanel getPanelOfText(JLabel lab)
	{
		JPanel pan = new JPanel();
		pan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		pan.add(lab);
		return pan;
	}

	// //////////////// //
	// POPULATE METHODS //
	// //////////////// //

	/**
	 * Populate the world with all the Datacentres
	 */
	public void populateWorld()
	{

		// Set the Network Distance
		mWorldDCNetworkDistance.setValue(mWorkingWorld.getNetworkDistance());

		// Populate List of Datacentres
		ConfigCPU currentCPU = mWorkingCPU;
		ConfigServer current = mWorkingServer;
		ConfigRack rack = mWorkingRack;
		ConfigAisle currentA = mWorkingAisle;
		ConfigDatacentre working = mWorkingDC;
		mWorldDCList.setListData(mWorkingWorld.getDatacentreNames());
		mWorkingDC = working;
		mWorkingAisle = currentA;
		mWorkingRack = rack;
		mWorkingServer = current;
		mWorkingCPU = currentCPU;

		// Sort the selected element
		if (mWorkingDC == null)
		{
			mWorldDCList.setSelectedIndex(-1);
		} else
		{
			int index = -1;
			ConfigDatacentre[] dcs = mWorkingWorld.getDCs();
			for (int i = 0; i < dcs.length; i++)
			{
				if (dcs[i].equals(mWorkingDC))
				{
					index = i;
				}
			}
			// Check index
			if (index != -1)
			{
				// Found index, set it to that.
				mWorldDCList.setSelectedIndex(index);
			}
		}

		mWorkingDC = working;
		mWorkingAisle = currentA;
		mWorkingRack = rack;
		mWorkingServer = current;
		mWorkingCPU = currentCPU;
		
		// activate/deativate buttons
		mWorldDCDelBtn.setEnabled(mWorkingDC != null);
		
		// Populate other panles
		if (propagateChanges)
			populateDC();
	}

	private void populateDC()
	{
		if (mWorkingDC != null)
		{
			// Stop the Action/Event/Document listener from saveing values as we
			// change them
			mDCModifiying = true;
			// Basic values
			mDCName.setText(mWorkingDC.getDatacentreName());
			mDCName.setEnabled(true);
			mDCAisleList.setEnabled(true);
			ConfigCPU currentCPU = mWorkingCPU;
			ConfigServer current = mWorkingServer;
			ConfigRack rack = mWorkingRack;
			ConfigAisle currentA = mWorkingAisle;
			mDCAisleList.setListData(this.mWorkingDC.getAisleNames());
			mWorkingAisle = currentA;
			mWorkingRack = rack;
			mWorkingServer = current;
			mWorkingCPU = currentCPU;
			if (mWorkingAisle != null)
				mDCAisleList.setSelectedIndex(mWorkingDC.getID(mWorkingAisle));
			mWorkingAisle = currentA;
			mWorkingRack = rack;
			mWorkingServer = current;
			mWorkingCPU = currentCPU;
			
			mDCAisleAddBtn.setEnabled(true);
			mDCAisleDelBtn.setEnabled(mWorkingAisle != null);
			mDCAisleNetworkDistance.setValue(mWorkingDC.getNetworkDistance());
			mDCAisleNetworkDistance.setEnabled(true);
			mDCsizeX.setValue(mWorkingDC.getDimX());
			mDCsizeX.setEnabled(true);
			mDCsizeY.setValue(mWorkingDC.getDimY());
			mDCsizeY.setEnabled(true);
			mDCModifiying = false;
			mDCPricingBtn.setEnabled(true);
		} else
		{
			// Disable everything
			mDCPricingBtn.setEnabled(false);
			mDCName.setText("");
			mDCName.setEnabled(false);
			mDCAisleList.setListData(new String[] {});
			mDCAisleList.setEnabled(false);
			mDCAisleAddBtn.setEnabled(false);
			mDCAisleDelBtn.setEnabled(false);
			mDCAisleNetworkDistance.setValue(0);
			mDCAisleNetworkDistance.setEnabled(false);
			mDCsizeX.setValue(0);
			mDCsizeX.setEnabled(false);
			mDCsizeY.setValue(0);
			mDCsizeY.setEnabled(false);
		}
		// Populate other panles
		if (propagateChanges)
			populateAisle();
	}

	private void populateAisle()
	{
		if (mWorkingAisle != null)
		{
			// Stop the Action/Event/Document listener from saveing values as we
			// change them
			mAisleModifiying = true;
			// Basic values
			mAisleName.setText(mWorkingAisle.getAisleName());
			mAisleName.setEnabled(true);
			mAisleRackList.setEnabled(true);
			ConfigCPU currentCPU = mWorkingCPU;
			ConfigServer current = mWorkingServer;
			ConfigRack rack = mWorkingRack;
			mAisleRackList.setListData(this.mWorkingAisle.getRackNames());
			if (mWorkingRack != null)
				mAisleRackList.setSelectedIndex(mWorkingAisle
						.getID(mWorkingRack));
			mWorkingRack = rack;
			mWorkingServer = current;
			mWorkingCPU = currentCPU;
			
			mAisleRackAddBtn.setEnabled(true);
			mAisleRackDelBtn.setEnabled(true);
			mAisleRackNetworkDistance.setValue(mWorkingAisle
					.getNetworkDistance());
			mAisleRackNetworkDistance.setEnabled(true);
			
			mAisleLocX.setValue(mWorkingAisle.getLocation().x);
			mAisleLocX.setEnabled(true);
			mAisleLocY.setValue(mWorkingAisle.getLocation().y);
			mAisleLocY.setEnabled(true);
			mAisleModifiying = false;
			
			
			
		} else
		{
			// Basic values
			mAisleName.setText("");
			mAisleName.setEnabled(false);
			mAisleRackList.setEnabled(false);
			mAisleRackList.setListData(new String[] {});
			mAisleRackAddBtn.setEnabled(false);
			mAisleRackDelBtn.setEnabled(false);
			mAisleRackNetworkDistance.setValue(0);
			mAisleRackNetworkDistance.setEnabled(false);
			mAisleLocX.setEnabled(false);
			mAisleLocY.setEnabled(false);
		}
		// Populate other panles
		if (propagateChanges)
			populateRack();
	}

	private void populateRack()
	{
		if (mWorkingRack != null)
		{
			// Stop the Action/Event/Document listener from saveing values as we
			// change them
			mRackModifiying = true;
			// Basic values
			mRackName.setText(mWorkingRack.getRackName());
			mRackName.setEnabled(true);
			mRackcount.setValue(mWorkingRack.getCount());
			mRackcount.setEnabled(true);
			mRackServerList.setEnabled(true);
			ConfigCPU currentCPU = mWorkingCPU;
			ConfigServer current = mWorkingServer;
			mRackServerList.setListData(this.mWorkingRack.getServerModels());
			if (mWorkingServer != null)
				mRackServerList.setSelectedIndex(mWorkingRack
						.getID(mWorkingServer));
			mWorkingServer = current;
			mWorkingCPU = currentCPU;
			
			mRackServerAddBtn.setEnabled(true);
			mRackServerDelBtn.setEnabled(true);
			mServerDirectionBox.setEnabled(true);
			mServerDirectionBox.setSelectedItem(CompassDirection.getDefault());
			mRackServerNetworkDistance.setValue(mWorkingRack
					.getNetworkDistance());
			mRackServerNetworkDistance.setEnabled(true);
			mRackPos.setValue(mWorkingRack.getLocation());
			mRackPos.setEnabled(true);
			mRackModifiying = false;

		} else
		{
			// Disable everything
			mRackName.setText("");
			mRackName.setEnabled(false);
			mRackcount.setValue(0);
			mRackcount.setEnabled(false);
			mRackServerList.setListData(new String[] {});
			mRackServerList.setEnabled(false);
			mRackServerAddBtn.setEnabled(false);
			mRackServerDelBtn.setEnabled(false);
			mServerDirectionBox.setEnabled(false);
			mServerDirectionBox.setSelectedItem(CompassDirection.getDefault());
			mRackServerNetworkDistance.setValue(0);
			mRackServerNetworkDistance.setEnabled(false);
			mRackPos.setValue(0);
			mRackPos.setEnabled(false);

		}
		// Populate other panles
		if (propagateChanges)
			populateServer();
	}

	private void populateServer()
	{
		if (mWorkingServer != null)
		{
			logger.info("Direction: " + mWorkingServer.getDirection());
			mServerDirectionBox.setSelectedItem(mWorkingServer.getDirection());
			// Stop the Action/Event/Document listener from saveing values as we
			// change them
			mServerModifiying = true;
			// Basic values
			mServerModel.setText(mWorkingServer.getModel());
			mServerModel.setEnabled(true);
			mServerSize.setValue(mWorkingServer.getSize());
			mServerSize.setEnabled(true);
			mServerCPUList.setEnabled(true);
			ConfigCPU current = mWorkingCPU;
			mServerCPUList.setListData(this.mWorkingServer.getCPUModels());
			if (mWorkingCPU != null)
				mServerCPUList.setSelectedIndex(mWorkingServer
						.getID(mWorkingCPU));
			mWorkingCPU = current;
			
			mServerCPUAddBtn.setEnabled(true);
			mServerCPUDelBtn.setEnabled(true);
			mServerMeanFailureTimeInDays.setValue(mWorkingServer.getMeanFailTime());
			mServerMeanFailureTimeInDays.setEnabled(true);
			// RAM
			ConfigRAM workingRAM = mWorkingServer.getRAM();
			mServerRAMModel.setText(workingRAM.getModel());
			mServerRAMModel.setEnabled(true);
			mServerRAMSize.setValue(workingRAM.getSize());
			mServerRAMSize.setEnabled(true);
			mServerRAMSpeed.setValue(workingRAM.getSpeed());
			mServerRAMSpeed.setEnabled(true);

			mServerModifiying = false;

		} else
		{
			// Basic values
			mServerModel.setText("");
			mServerModel.setEnabled(false);
			mServerSize.setValue(0);
			mServerSize.setEnabled(false);
			mServerCPUList.setEnabled(false);
			mServerCPUList.setListData(new String[] {});
			mServerCPUAddBtn.setEnabled(false);
			mServerCPUDelBtn.setEnabled(false);
			mServerMeanFailureTimeInDays.setValue(0);
			mServerMeanFailureTimeInDays.setEnabled(false);
			// RAM
			mServerRAMModel.setText("");
			mServerRAMModel.setEnabled(false);
			mServerRAMSize.setValue(0);
			mServerRAMSize.setEnabled(false);
			mServerRAMSpeed.setValue(0);
			mServerRAMSpeed.setEnabled(false);

		}
		// Populate other panles
		if (propagateChanges)
			populateCPU();
	}

	private void populateCPU()
	{
		if (mWorkingCPU != null)
		{
			// Stop the Action/Event/Document listener from saveing values as we
			// change them
			mCPUModifiying = true;
			// Basic values
			mCPUModel.setText(mWorkingCPU.getModel());
			mCPUModel.setEnabled(true);
			mCPUCores.setValue(mWorkingCPU.getCores());
			mCPUCores.setEnabled(true);
			mCPUSpeed.setValue(mWorkingCPU.getSpeed());
			mCPUSpeed.setEnabled(true);

			mCPUModifiying = false;
		} else
		{
			// Basic values
			mCPUModel.setText("");
			mCPUModel.setEnabled(false);
			mCPUCores.setValue(0);
			mCPUCores.setEnabled(false);
			mCPUSpeed.setValue(0);
			mCPUSpeed.setEnabled(false);

		}
	}
	
	public ConfigDatacentre getWorkingDC()
	{
		return mWorkingDC;
	}

	public void setWorkingDC(ConfigDatacentre pWorkingDC)
	{
		this.mWorkingDC = pWorkingDC;
	}
	
	public ConfigDatacentre getWorkingDCOld()
	{
		return mWorkingDCOld;
	}

	public void setWorkingDCOld(ConfigDatacentre pWorkingDCOld)
	{
		//TODO AS 3.8.12- need to create mWorkingDCOld object
		//this.mWorkingDCOld = new ConfigDatacentre();
		this.mWorkingDCOld = pWorkingDCOld;
	}

	public JList getWorldDCList()
	{
		return mWorldDCList;
	}
	
	/**
	 * When the new datacentre button is selected
	 */
	public void addDCButtonSelect()
	{
		mWorkingWorld.addDatacentre(new ConfigDatacentre());
		populateWorld();
		mWorldDCList.setSelectedIndex(mWorkingWorld.getDCs().length - 1);
	}
	
	public void deleteDCButtonSelect()
	{
		int result = JOptionPane.showConfirmDialog(thisPanel, "Are you sure you want to delete this Datacentre?");
		if (result == JOptionPane.OK_OPTION)
		{
			mWorkingWorld.removeDatacentre(mWorkingDC);
			mWorkingDC = null;
			populateWorld();
		}
	}

	// ////////////// //
	// EVENT HANDLERS //
	// ////////////// //
	private class WorldPanelEventHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mWorldDCAddBtn))
			{
				addDCButtonSelect();
			} 
			else if (source.equals(mWorldDCDelBtn))
			{
				deleteDCButtonSelect();
			} 
			else if (source.equals(mWorldDCGenBtn))
			{
				MainWindow.generateWindowGUIOption();
				
//				// Example Servers
//				ConfigServer[] servers = new ConfigServer[3];
//				
//				// Example Servers
//				
//				// Server 1
//				servers[0] = new ConfigServer();
//				servers[0].setModel("Crappy Server");
//				servers[0].setMeanFailTimeInDays(10);
//				servers[0].setSize(2);
//				ConfigCPU c1a = new ConfigCPU();
//				c1a.setCores(2);
//				c1a.setSpeed(1400);
//				c1a.setModel("Crappy CPU");
//				servers[0].addCPU(c1a);
//				servers[0].addCPU(c1a.duplicate());
//				ConfigRAM r1 = new ConfigRAM();
//				r1.setModel("Crappy RAM");
//				r1.setSize(1024);
//				r1.setSpeed(667);
//				servers[0].setRAM(r1);
//				
//				// Server 2
//				servers[1] = new ConfigServer();
//				servers[1].setModel("Moderate Server");
//				servers[1].setMeanFailTimeInDays(100);
//				servers[1].setSize(2);
//				ConfigCPU c2 = new ConfigCPU();
//				c2.setCores(4);
//				c2.setSpeed(2800);
//				c2.setModel("Moderate CPU");
//				servers[1].addCPU(c2);
//				servers[1].addCPU(c2.duplicate());
//				ConfigRAM r2 = new ConfigRAM();
//				r2.setModel("Moderate RAM");
//				r2.setSize(2048);
//				r2.setSpeed(1066);
//				servers[1].setRAM(r2);
//				
//				// Server 3
//				servers[2] = new ConfigServer();
//				servers[2].setModel("Awesome Server");
//				servers[2].setMeanFailTimeInDays(1000);
//				servers[2].setSize(2);
//				ConfigCPU c3 = new ConfigCPU();
//				c3.setCores(6);
//				c3.setSpeed(3800);
//				c3.setModel("Awesome CPU");
//				servers[2].addCPU(c3);
//				servers[2].addCPU(c3.duplicate());
//				servers[2].addCPU(c3.duplicate());
//				servers[2].addCPU(c3.duplicate());
//				ConfigRAM r3 = new ConfigRAM();
//				r3.setModel("Awesome RAM");
//				r3.setSize(4096);
//				r3.setSpeed(1066);
//				servers[2].setRAM(r3);
				
				//DatacentreGenerator gen = new DatacentreGenerator(servers, 4096, 8, new int[] {100, 10, 1}, new int[] {200,200,10});
				
//				new GenProgressDialog(thisPanel, gen);
//				gen.addGeneratorListener(new GeneratorProgress());
//				gen.start();
			}
		}
	}

	protected class GeneratorProgress implements DatacentreGeneratorProgressListener
	{

		@Override
		public void updated(double progress)
		{
			//System.out.println("Genrating " + progress*100 + "%...");
		}

		@Override
		public void done(ConfigDatacentre pDatacentre)
		{
			//System.out.println("Genrating 100%...");
			//System.out.println("Done");
			mWorkingWorld.addDatacentre(pDatacentre);
			populateWorld();
		}

		@Override
		public void canceled()
		{
			// Do nothing.
			
		}
	}
	
	private class DatacentrePanelEventHandler implements ActionListener,
			ChangeListener, DocumentListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mDCAisleAddBtn))
			{
				mWorkingDC.addAisle(new ConfigAisle());
				populateDC();

			} else if (source.equals(mDCAisleDelBtn))
			{
				int result = JOptionPane.showConfirmDialog(thisPanel,
						"Are you sure you want to delete this Aisle?");
				if (result == JOptionPane.OK_OPTION)
				{
					mWorkingDC.removeAisle(mWorkingAisle);
					mWorkingAisle = null;
					if (propagateChanges)
						populateDC();
				}
			} else if (source.equals(mDCPricingBtn))
			{
				new NonPhysicalDatacentreRelatedConfigurationStuffs(thisPanel, mWorkingDC, mPrefs,mWorkingDC.getName());				
			}

		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updated();
		}

		/**
		 * The panel controls have been changed,
		 */
		private void updated()
		{
			if (mWorkingDC != null && !mDCModifiying)
			{
				mWorkingDC.setNetworkDistance(((Number) mDCAisleNetworkDistance
						.getValue()).intValue());
				mWorkingDC.setDatacentreName(mDCName.getText());
				mWorkingDC.setDimX(((Number) mDCsizeX.getValue()).intValue());
				mWorkingDC.setDimY(((Number) mDCsizeY.getValue()).intValue());
				propagateChanges = false;
				populateWorld();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	private class AislePanelEventHandler implements ActionListener,
			ChangeListener, DocumentListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mAisleRackAddBtn))
			{
				mWorkingAisle.addRack(new ConfigRack());
				populateAisle();

			} else if (source.equals(mAisleRackDelBtn))
			{
				int result = JOptionPane.showConfirmDialog(thisPanel,
						"Are you sure you want to delete this Rack?");
				if (result == JOptionPane.OK_OPTION)
				{
					mWorkingAisle.removeRack(mWorkingRack);
					mWorkingRack = null;
					if (propagateChanges)
						populateAisle();
				}
			}

		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updated();
		}

		/**
		 * The panel controls have been changed,
		 */
		private void updated()
		{
			if (mWorkingAisle != null && !mAisleModifiying)
			{

				mWorkingAisle
						.setNetworkDistance(((Number) mAisleRackNetworkDistance
								.getValue()).intValue());
				mWorkingAisle.setAisleName(mAisleName.getText());
				mWorkingAisle.setLocation(new Point(((Number) mAisleLocX
								.getValue()).intValue(), ((Number) mAisleLocY
								.getValue()).intValue()));

				propagateChanges = false;
				populateDC();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	private class RackPanelEventHandler implements ActionListener,
			ChangeListener, DocumentListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mRackServerAddBtn))
			{
				try
				{
					ConfigServer server = new ConfigServer();
					server.setDirection((CompassDirection) mServerDirectionBox.getSelectedItem());
					mWorkingRack.addServer(server);
					
				} catch (ServerWontFitException e1)
				{
					JOptionPane.showMessageDialog(thisPanel, "The rack doesn't have enough space for that server", "Server Won't Fit", JOptionPane.ERROR_MESSAGE);
				}
				populateRack();

			} else if (source.equals(mRackServerDelBtn))
			{
				int result = JOptionPane.showConfirmDialog(thisPanel,
						"Are you sure you want to delete this Server?");
				if (result == JOptionPane.OK_OPTION)
				{
					mWorkingRack.removeServer(mWorkingServer);
					mWorkingServer = null;
					if (propagateChanges)
						populateRack();
				}
			}
			else if (source.equals(mServerDirectionBox))
			{
				updated();
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updated();
		}

		/**
		 * The panel controls have been changed,
		 */
		private void updated()
		{
			if (mWorkingRack != null && !mRackModifiying)
			{

				mWorkingRack.setCount(((Number) mRackcount.getValue())
						.intValue());
				mWorkingRack
						.setNetworkDistance(((Number) mRackServerNetworkDistance
								.getValue()).intValue());
				mWorkingRack.setRackName(mRackName.getText());

				mWorkingRack.setLocation(((Number) mRackPos.getValue()).intValue());
				
				mWorkingRack.setServersDirection((CompassDirection) mServerDirectionBox.getSelectedItem());

				propagateChanges = false;
				populateAisle();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	private class ServerPanelEventHandler implements ActionListener,
			ChangeListener, DocumentListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mServerCPUAddBtn))
			{
				mWorkingServer.addCPU(new ConfigCPU());
				populateServer();

			} else if (source.equals(mServerCPUDelBtn))
			{
				int result = JOptionPane.showConfirmDialog(thisPanel,
						"Are you sure you want to delete this CPU?");
				if (result == JOptionPane.OK_OPTION)
				{
					mWorkingServer.removeCPU(mWorkingCPU);
					mWorkingCPU = null;
					if (propagateChanges)
						populateServer();
				}
			}

		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updated();
		}

		/**
		 * The panel controls have been changed,
		 */
		private void updated()
		{
			if (mWorkingServer != null && !mServerModifiying)
			{
				mWorkingServer.setModel(mServerModel.getText());
				mWorkingServer.setSize(((Number) mServerSize.getValue())
						.intValue());
				mWorkingServer.setMeanFailTimeInDays(((Number) mServerMeanFailureTimeInDays.getValue()).longValue());
				ConfigRAM ram = mWorkingServer.getRAM();
				ram.setModel(mServerRAMModel.getText());
				ram.setSize(((Number) mServerRAMSize.getValue()).intValue());
				ram.setSpeed(((Number) mServerRAMSpeed.getValue())
						.doubleValue());

				propagateChanges = false;
				populateRack();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	private class CPUPanelEventHandler implements
			ChangeListener, DocumentListener
	{

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			updated();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			updated();
		}

		/**
		 * The panel controls have been changed,
		 */
		private void updated()
		{
			if (mWorkingCPU != null && !mCPUModifiying)
			{
				mWorkingCPU.setModel(mCPUModel.getText());
				mWorkingCPU.setCores(((Number) mCPUCores.getValue())
						.intValue());
				mWorkingCPU.setSpeed(((Number) mCPUSpeed.getValue())
						.intValue());

				propagateChanges = false;
				populateServer();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	// ////////////// //
	// LIST LISTENERS //
	// ////////////// //

	private class DatacentreListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (! e.getValueIsAdjusting())
			{
				if (mWorldDCList.getSelectedIndex() != -1)
				{
					mWorkingDC = mWorkingWorld.getDCs()[mWorldDCList
							.getSelectedIndex()];
					mWorkingAisle = null;
					mWorkingRack = null;
					mWorkingServer = null;
					mWorkingCPU = null;
					mWorldDCDelBtn.setEnabled(true);
				} else
				{
					mWorkingDC = null;
					mWorldDCDelBtn.setEnabled(false);
				}
				if (propagateChanges)
					populateDC();
				
				MainWindow.getmViewEdit().mBackgroundSelect = true;
				MainWindow.getmViewEdit().getDCList().setListData(mWorkingWorld.getDatacentreNames());
				MainWindow.getmViewEdit().getDCList().setSelectedIndex(mWorldDCList.getSelectedIndex());
				MainWindow.getmViewEdit().mBackgroundSelect = false;
			}
		}
	}

	private class AisleListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (mDCAisleList.getSelectedIndex() != -1)
			{
				mWorkingAisle = mWorkingDC.getAisles()[mDCAisleList
						.getSelectedIndex()];
				mWorkingRack = null;
				mWorkingServer = null;
				mWorkingCPU = null;
			} else
			{
				mWorkingAisle = null;
			}
			if (propagateChanges)
				populateAisle();
		}

	}

	private class RackListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (mAisleRackList.getSelectedIndex() != -1)
			{
				mWorkingRack = mWorkingAisle.getRacks()[mAisleRackList
						.getSelectedIndex()];
				mWorkingServer = null;
				mWorkingCPU = null;
			} else
			{
				mWorkingRack = null;
			}
			if (propagateChanges)
				populateRack();
		}

	}

	private class ServerListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (mRackServerList.getSelectedIndex() != -1)
			{
				mWorkingServer = mWorkingRack.getServers()[mRackServerList
						.getSelectedIndex()];
				mWorkingCPU = null;
			} else
			{
				mWorkingServer = null;
			}
			if (propagateChanges)
				populateServer();
		}

	}

	private class CPUListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (mServerCPUList.getSelectedIndex() != -1)
			{
				mWorkingCPU = mWorkingServer.getCPUs()[mServerCPUList
						.getSelectedIndex()];
			} else
			{
				mWorkingCPU = null;
			}
			if (propagateChanges)
			  populateCPU();
		}

	}
}
