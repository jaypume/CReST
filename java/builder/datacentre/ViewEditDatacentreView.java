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

import gui.map.DatacentreMap;
import gui.map.RackDisplayRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import sim.physical.World;
import builder.MainWindow;
import builder.prefs.BuilderPreferences;
import builder.replacements.ReplacementServers;
import config.DatacentreGenerator;
import config.XMLLoader;
import config.physical.ConfigAisle;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRack;
import config.physical.ConfigWorld;
import builder.datacentre.layout.LayoutPatternFactory;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;

public class ViewEditDatacentreView extends JPanel
{
	private static final long serialVersionUID = 5615033399653325237L;
	public static Logger logger = Logger.getLogger(ViewEditDatacentreView.class);
	
	@SuppressWarnings("unused")
	private Frame thisPanel;
	private MainWindow MainWindow;
	
	private ConfigWorld mWorkingWorld;
	private BuilderPreferences mPrefs;
	private JSplitPane mSplitPane;
	private JPanel mLeftPanel;
	
	//Available Datacentres Panel
	private JPanel mAvailableDCPanel;
	private JList mDCList;
	private JButton mNewDCButton;
	private JButton mDeleteDCButton;
	private JButton mAgeDCButton;
	private JButton mUndoButton;
	private boolean mAgeRunning;
	
	//Edit Datacentre Panel
	private JPanel mEditDCPanel;
	private JTextField mDCName;
	private LayoutMethod mLayoutMethod = LayoutMethod.getDefault();
	private JRadioButton mSelectDCSizeButton;
	private JTextField mDCWidth;
	private JTextField mDCLength;
	private JRadioButton mSelectNumServersButton;
	private JTextField mDCServers;
	private JTextField mDCRatio;
	private JCheckBox mAirconCheckbox;
	private JComboBox mSelectPatternCombo;
	private JButton mUpdateButton;
	
	//View Datacentre Panel;
	private JScrollPane mViewDCScrollPane;
	private JPanel mViewDCPanel;
	private DatacentreMap mDCmap;
	private RackDisplayRenderer newRenderer;
	
	public boolean mBackgroundSelect; //whether the user selected the JList item or in the background
	
	public ViewEditDatacentreView(ConfigWorld pWorld, BuilderPreferences pPrefs, Frame parentFrame, MainWindow mainWindow)
	{
		// Setup this so that contained classes can access it
		this.thisPanel = parentFrame;
		this.MainWindow = mainWindow;
		
		mWorkingWorld = pWorld; // Setup world object
				
		mPrefs = pPrefs;
		this.setLayout(new BorderLayout());

		// /////////////////
		// Create Panels //
		// /////////////////

		//Panel
		setupAvailableDCPanel();
		setupEditDCPanel();
		setupViewDCPanel();

		// Add to main panel
		mLeftPanel = new JPanel();
		mLeftPanel.setLayout(new BoxLayout(mLeftPanel, BoxLayout.Y_AXIS));
		mLeftPanel.add(mAvailableDCPanel);
		mLeftPanel.add(mEditDCPanel);
		
		//Create a split pane with the two scroll panes in it.
		mSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mLeftPanel, mViewDCScrollPane);		
		mSplitPane.setOneTouchExpandable(true);
		mSplitPane.setDividerLocation(225);
		
		this.add(mSplitPane, BorderLayout.CENTER);
				
		init();
	}

	private void setupAvailableDCPanel()
	{
		//Button Handler
		AvailableDCPanelEventHandler handle = new AvailableDCPanelEventHandler();
		
		mAvailableDCPanel = new JPanel();
		mAvailableDCPanel.setLayout(new BoxLayout(mAvailableDCPanel, BoxLayout.PAGE_AXIS));
		mAvailableDCPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Datacentres Available"), new EmptyBorder(10, 10, 10, 10)));		
		
		// Datacentre list
		mDCList = new JList();
		mDCList.setModel(new DefaultListModel());
		mDCList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mDCList.setVisibleRowCount(-1);
		mDCList.addListSelectionListener(new DatacentreListListener());
//		mDCList = MainWindow.getDCList();
		JScrollPane dcListScroller = new JScrollPane(mDCList);
		mAvailableDCPanel.add(dcListScroller);
		mAvailableDCPanel.add(Box.createVerticalStrut(10));
		
		//Datacentre controls
		JPanel dcControlPanel = new JPanel();
		dcControlPanel.setLayout(new BoxLayout(dcControlPanel, BoxLayout.LINE_AXIS));
		mNewDCButton = new JButton("New", mPrefs.getImage("datacentre_add.png"));
		mNewDCButton.addActionListener(handle);
		dcControlPanel.add(mNewDCButton);
		dcControlPanel.add(Box.createHorizontalGlue());
		
		mDeleteDCButton = new JButton("Remove", mPrefs.getImage("datacentre_delete.png"));		
		mDeleteDCButton.addActionListener(handle);
		dcControlPanel.add(mDeleteDCButton);
		mAvailableDCPanel.add(dcControlPanel);
		mAvailableDCPanel.add(Box.createVerticalStrut(10));
		
		JPanel dcAgePanel = new JPanel();
		dcAgePanel.setLayout(new BoxLayout(dcAgePanel, BoxLayout.LINE_AXIS));
//		mAgeDCButton = new JButton("Age " + ReplacementServers.getAgeTime() + " " + ReplacementServers.getAgeTimeUnit(), mPrefs.getImage("run.png"));		
		mAgeDCButton = new JButton("Start Ageing", mPrefs.getImage("run.png"));		
		mAgeDCButton.addActionListener(handle);
		dcAgePanel.add(mAgeDCButton);		

//		dcAgePanel.add(Box.createHorizontalGlue());
//		mUndoButton = new JButton("Undo", mPrefs.getImage("toolbar_del.png"));
//		mUndoButton.addActionListener(handle);
//		dcAgePanel.add(mUndoButton);
		
		mAvailableDCPanel.add(dcAgePanel);
	}
	
	private void setupEditDCPanel()
	{
		//Button Handler
		EditDCPanelEventHandler handle = new EditDCPanelEventHandler();
		
		mEditDCPanel = new JPanel();
		mEditDCPanel.setLayout(new BoxLayout(mEditDCPanel, BoxLayout.PAGE_AXIS));
		
		//Name
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.LINE_AXIS));
		
		JLabel dcNameLabel = new JLabel("Name");
		mDCName = new JTextField(10);
		
		namePanel.add(dcNameLabel);
		namePanel.add(Box.createHorizontalStrut(10));
		namePanel.add(mDCName);				
		namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(namePanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		
		//DC Size
		mSelectDCSizeButton = new JRadioButton(LayoutMethod.DATACENTRE_SIZE.toString());
	    mSelectDCSizeButton.setSelected(true);
	    mSelectDCSizeButton.addActionListener(handle);
	    
	    mEditDCPanel.add(mSelectDCSizeButton);
	    mEditDCPanel.add(Box.createVerticalStrut(10));
		
	    JPanel dcWidthPanel = new JPanel();
	    dcWidthPanel.setLayout(new BoxLayout(dcWidthPanel, BoxLayout.LINE_AXIS));
		JLabel dcWidthLabel = new JLabel("Width");
		mDCWidth = new JTextField(10);
		JPanel dcLengthPanel = new JPanel();
		dcLengthPanel.setLayout(new BoxLayout(dcLengthPanel, BoxLayout.LINE_AXIS));
		JLabel dcLengthLabel = new JLabel("Length");
		mDCLength = new JTextField(10);
		
		dcWidthPanel.add(dcWidthLabel);
		dcWidthPanel.add(Box.createHorizontalStrut(10));
		dcWidthPanel.add(mDCWidth);
		dcWidthPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(dcWidthPanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		dcLengthPanel.add(dcLengthLabel);
		dcLengthPanel.add(Box.createHorizontalStrut(10));
		dcLengthPanel.add(mDCLength);
		dcLengthPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(dcLengthPanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		
		//Num Servers
		mSelectNumServersButton = new JRadioButton(LayoutMethod.NUMBER_SERVERS.toString());
		mSelectNumServersButton.addActionListener(handle);
		
		mEditDCPanel.add(mSelectNumServersButton);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		
		JPanel dcServersPanel = new JPanel();
		dcServersPanel.setLayout(new BoxLayout(dcServersPanel, BoxLayout.LINE_AXIS));
		JLabel dcServersLabel = new JLabel("Servers");
		mDCServers = new JTextField(10);
		
		JPanel dcRatioPanel = new JPanel();
		dcRatioPanel.setLayout(new BoxLayout(dcRatioPanel, BoxLayout.LINE_AXIS));
		JLabel dcRatioLabel = new JLabel("Ratio");
		mDCRatio = new JTextField(10);
		
		dcServersPanel.add(dcServersLabel);
		dcServersPanel.add(Box.createHorizontalStrut(10));
		dcServersPanel.add(mDCServers);
		dcServersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(dcServersPanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		dcRatioPanel.add(dcRatioLabel);
		dcRatioPanel.add(Box.createHorizontalStrut(10));
		dcRatioPanel.add(mDCRatio);
		dcRatioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(dcRatioPanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		
		//Radio button groups
		ButtonGroup group = new ButtonGroup();
	    group.add(mSelectDCSizeButton);
	    group.add(mSelectNumServersButton);
		
		//Air Con
	    mAirconCheckbox = new JCheckBox("Air Conditioning");
	    mAirconCheckbox.setSelected(true);

		mEditDCPanel.add(mAirconCheckbox);
		mEditDCPanel.add(Box.createVerticalStrut(10));
		
		//Pattern
		JPanel patternPanel = new JPanel();
		patternPanel.setLayout(new BoxLayout(patternPanel, BoxLayout.LINE_AXIS));
		
		JLabel patternLabel = new JLabel(LayoutPatternType.getLabel());
		mSelectPatternCombo = new JComboBox(LayoutPatternType.values());
		mSelectPatternCombo.setEditable(false);
		
		patternPanel.add(patternLabel);
		patternPanel.add(Box.createHorizontalStrut(10));
		patternPanel.add(mSelectPatternCombo);
		patternPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mEditDCPanel.add(patternPanel);
		mEditDCPanel.add(Box.createVerticalStrut(10));
				
		//Update
		mUpdateButton = new JButton("Update", mPrefs.getImage("datacentre_gen.png"));
		mUpdateButton.addActionListener(handle);
		
		mEditDCPanel.add(mUpdateButton);
	}
	
	private void setupViewDCPanel()
	{		
		mViewDCPanel = new JPanel();
		mViewDCPanel.setLayout(new GridLayout(1, 1));
		
		// Create map
		mDCmap = new DatacentreMap();
		//mViewDCPanel.add(mDCmap, BorderLayout.CENTER);
		newRenderer = new RackDisplayRenderer();
//		for(int i=0; i<World.getInstance().getNumberOfDatacentres(); i++) {
//			RackDisplayRenderer newRenderer = new RackDisplayRenderer(i);
//			TabbedMapPane.getSingletonObject().getMap(i).renderPhysicalLayout(World.getInstance().getDatacentre(i), newRenderer);
//		}
		
		mViewDCScrollPane = new JScrollPane();
		mViewDCScrollPane.setBorder(BorderFactory.createEmptyBorder());
		mViewDCScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		mViewDCScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mViewDCScrollPane.getViewport().add(mViewDCPanel);
	}
	
	/**
	 * Initialises fields to be disabled
	 */
	private void init()
	{
		//There could be working datacentres if loaded from file, so set list items
		mBackgroundSelect = true;
		mDCList.setListData(mWorkingWorld.getDatacentreNames());
		mBackgroundSelect = false;
		
		//View DC Panel
		mDeleteDCButton.setEnabled(false);
		mAgeDCButton.setEnabled(false);
		
		 //Edit DC Panel
		mDCName.setEnabled(false);
		mDCName.setText("");
		mSelectDCSizeButton.setEnabled(false);
		mDCServers.setEnabled(false);
		mDCServers.setText("");
		mDCRatio.setEnabled(false);
		mDCRatio.setText("");
		mSelectNumServersButton.setEnabled(false);
    	mDCWidth.setEnabled(false);
    	mDCWidth.setText("");
		mDCLength.setEnabled(false);
		mDCLength.setText("");
		mAirconCheckbox.setEnabled(false);
		mSelectPatternCombo.setEnabled(false);
		mUpdateButton.setEnabled(false);
		
		setPanelTitles();
	}
	
	//Populate datacentre fields
	public void populateDC()
	{
		if(mWorkingDC() != null)
		{
			//View DC Panel
			mDeleteDCButton.setEnabled(true);
			mAgeDCButton.setEnabled(true);
			
			 //Edit DC Panel
			mDCName.setEnabled(true);
			mDCName.setText(mWorkingDC().getDatacentreName());			
			
			mSelectDCSizeButton.setEnabled(true);
			mSelectNumServersButton.setEnabled(true);			

			mDCWidth.setText(Integer.toString(mWorkingDC().getDimX()));
			mDCLength.setText(Integer.toString(mWorkingDC().getDimY()));
			mDCServers.setText(Integer.toString(mWorkingDC().getNumServers()));
			mDCRatio.setText(Double.toString(mWorkingDC().getRatio()));
			mLayoutMethod = mWorkingDC().getLayoutMethod();
			
		    if(mLayoutMethod == LayoutMethod.DATACENTRE_SIZE)
		    {
		    	mSelectDCSizeButton.setSelected(true);
		    	mDCWidth.setEnabled(true);
				mDCLength.setEnabled(true);
				mDCServers.setEnabled(false);
				mDCRatio.setEnabled(false);
		    }
		    else
		    {
		    	mSelectNumServersButton.setSelected(true);
		    	mDCWidth.setEnabled(false);
				mDCLength.setEnabled(false);
				mDCServers.setEnabled(true);
				mDCRatio.setEnabled(true);
		    }			
		
			mAirconCheckbox.setEnabled(true);
			mAirconCheckbox.setSelected(mWorkingDC().getAircon());
			
			mSelectPatternCombo.setEnabled(true);
			mSelectPatternCombo.setSelectedItem(mWorkingDC().getLayoutPattern());
			
			mUpdateButton.setEnabled(true);
			
			setPanelTitles();
		}
		else
		{
			init();
		}
	}
	
	/**
	 * Sets JPanel Titles
	 */
	private void setPanelTitles()
	{
		mEditDCPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Edit: " + mDCName.getText()), new EmptyBorder(10, 10, 10, 10)));
		mViewDCPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("View of: " + mDCName.getText()), new EmptyBorder(10, 10, 10, 10)));
	}
	
	/**
	 * Generate the datacentre based on the layout method selected
	 */
	private void generateDC()
	{
		DatacentreGenerator gen;
		
		if(mLayoutMethod == LayoutMethod.DATACENTRE_SIZE)
	    {
			gen = new DatacentreGenerator(
					mWorkingDC().getDimX(),
					mWorkingDC().getDimY(),
					mWorkingDC().getAircon(),
					mWorkingDC().getLayoutPattern(),
					new int[] {	mWorkingDC().getNetworkDistance(),
								ConfigAisle.DEFAULT_NETWORK_DISTANCE,
								ConfigRack.DEFAULT_NETWORK_DISTANCE},
					mWorkingDC().getDatacentreName(),
					0
					);
	    }
		else
		{
			gen = new DatacentreGenerator(
					mWorkingDC().getNumServers(),
					mWorkingDC().getRatio(),
					mWorkingDC().getAircon(),
					mWorkingDC().getLayoutPattern(),
					new int[] {	mWorkingDC().getNetworkDistance(),
								ConfigAisle.DEFAULT_NETWORK_DISTANCE,
								ConfigRack.DEFAULT_NETWORK_DISTANCE},
					mWorkingDC().getDatacentreName(),
					0
					);
		}
		
		MainWindow.generateDCGUIOption(gen);
	}
	
	private void updateDC()
	{
		if (mDCList.getSelectedIndex() == -1)
		{
			logger.error("No datacentre selected to update");
			return;
		}
		
		System.out.println("set old");
		mWorkingDCOld(mWorkingDC()); //Set current working datacentre as old (for undo)
		
		mWorkingDC().setDatacentreName(mDCName.getText());
		
		if(mSelectDCSizeButton.isSelected())
		{
			mLayoutMethod = LayoutMethod.DATACENTRE_SIZE;			
		}
		else
		{
			mLayoutMethod = LayoutMethod.NUMBER_SERVERS;
		}
		mWorkingDC().setLayoutMethod(mLayoutMethod);

		try
		{
			mWorkingDC().setDimX(Integer.parseInt(mDCWidth.getText()));
		}
		catch(NumberFormatException e)
		{
			logger.error("Invalid width entered " + e.getMessage());
			return;
		}
		try
		{
			mWorkingDC().setDimY(Integer.parseInt(mDCLength.getText()));
		}
		catch(NumberFormatException e)
		{
			logger.error("Invalid length entered " + e.getMessage());
			return;
		}

		try
		{
			mWorkingDC().setNumServers(Integer.parseInt(mDCServers.getText()));
		}
		catch(NumberFormatException e)
		{
			logger.error("Invalid number of servers entered " + e.getMessage());
			return;
		}
		try
		{
			mWorkingDC().setRatio(Double.parseDouble(mDCRatio.getText()));
		}
		catch(NumberFormatException e)
		{
			logger.error("Invalid ratio entered " + e.getMessage());
			return;
		}		
		
		mWorkingDC().setAircon(mAirconCheckbox.isSelected());
		mWorkingDC().setLayoutPattern(LayoutPatternFactory.getLayoutPattern((LayoutPatternType) mSelectPatternCombo.getSelectedItem()));
		
		generateDC();
	}
	
	private void drawMap()
	{
		if(mWorkingDC() != null && mDCList.getSelectedIndex() != -1)
		{
			ConfigAisle[] a = mWorkingDC().getAisles();
			if(a.length > 0) //Check if there are actually any aisles in the datacentre to render
				//TODO: perhaps change to .lengthx  or .length y
			{
				logger.info("Rendering map");
				World w = XMLLoader.loadWorld(MainWindow.getEditConfig().getConfWorld(), MainWindow.getEditConfig().getConfig(), MainWindow.getEditConfig().getReplacements());
				w.distributeIPs();			
				mDCmap.renderPhysicalLayout(w.getDatacentres()[mDCList.getSelectedIndex()], newRenderer);
				
//				mDCmap.getmRender().setDataView(MapViewEnum.LAYOUT);
//				mDCmap.renderView(mDCmap.getmRender().getCurrentDataView());
				mViewDCPanel.add(mDCmap, BorderLayout.CENTER);
				mViewDCPanel.revalidate();
				return;
			}
		}

		logger.info("Nothing to render, clear map pane");
		mViewDCPanel.remove(mDCmap);
	}
	
	/**
	 * Get the datacentre JList items
	 * @return JList
	 */
	public JList getDCList()
	{
		return mDCList;
	}
	
	/**
	 * Gets the Simple Physical View
	 * @return SimplePhysicalView
	 */
	private SimplePhysicalView getPhysical()
	{
		return MainWindow.getmPhysical();
	}
	
	/**
	 * Gets the current working datacentre
	 * @return the current working datacentre
	 */
	private ConfigDatacentre mWorkingDC()
	{
		return MainWindow.getmPhysical().getWorkingDC();
	}
	
	/**
	 * Gets the last working datacentre
	 * @return the current working datacentre
	 */
	private ConfigDatacentre mWorkingDCOld()
	{
		return MainWindow.getmPhysical().getWorkingDCOld();
	}
	
	/**
	 * Sets the current working datacentre
	 * @param mWorkingDC the datacentre to save
	 */
	private void mWorkingDC(ConfigDatacentre mWorkingDC)
	{
		MainWindow.getmPhysical().setWorkingDC(mWorkingDC);
	}
	
	/**
	 * Sets the last working datacentre
	 * @param mWorkingDCOld the datacentre to save
	 */
	private void mWorkingDCOld(ConfigDatacentre mWorkingDCOld)
	{
		MainWindow.getmPhysical().setWorkingDCOld(mWorkingDCOld);
	}

	private class AvailableDCPanelEventHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mNewDCButton))
			{
				getPhysical().addDCButtonSelect();
			}
			else if (source.equals(mDeleteDCButton))
			{
				getPhysical().deleteDCButtonSelect();
			}
			else if(source.equals(mAgeDCButton))
			{
				if (!mAgeRunning)
				{
					startAgeing();
				}
				else
				{
					stopAgeing();
				}
			}
			else if(source.equals(mUndoButton))
			{
				mWorkingDC(mWorkingDCOld());
//				populateDC();
			}
		}
	}
	
	/**
	 * Start the datacentre ageing
	 * TODO AS 14.9.12- start simulation
	 */
	private void startAgeing()
	{
		mAgeRunning = true;
		mAgeDCButton.setText("Stop Ageing ");

		ReplacementServers.update(mWorkingDC(), MainWindow);
	}
	
	/**
	 * Stop the datacentre ageing
	 * TODO AS 14.9.12- stop simulation and save physical dc layout
	 */
	private void stopAgeing()
	{
		mAgeRunning = false;
		mAgeDCButton.setText("Start Ageing");
	}
	
	private class DatacentreListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (! e.getValueIsAdjusting())
			{
				if(mBackgroundSelect)
				{
					logger.info("background selected list item");
					populateDC();
				
					drawMap();
				}
				else
				{
					logger.info("user selected list item");
					getPhysical().getWorldDCList().setSelectedIndex(mDCList.getSelectedIndex());
				}
			}
		}
	}
	
	private class EditDCPanelEventHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mUpdateButton))
			{
				updateDC();
				
				int sel  = mDCList.getSelectedIndex();
				getPhysical().getWorldDCList().setListData(mWorkingWorld.getDatacentreNames());
				getPhysical().getWorldDCList().setSelectedIndex(sel);
			}
			else if (source.equals(mSelectDCSizeButton))
			{
				mLayoutMethod = LayoutMethod.DATACENTRE_SIZE;
				mDCWidth.setEnabled(true);
				mDCLength.setEnabled(true);
				mDCServers.setEnabled(false);
				mDCRatio.setEnabled(false);
			}
			else if (source.equals(mSelectNumServersButton))
			{
				mLayoutMethod = LayoutMethod.NUMBER_SERVERS;
				mDCWidth.setEnabled(false);
				mDCLength.setEnabled(false);
				mDCServers.setEnabled(true);
				mDCRatio.setEnabled(true);
			}
		}
	}
}
