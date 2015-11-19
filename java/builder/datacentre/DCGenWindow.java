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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import builder.MainWindow;
import builder.datacentre.layout.LayoutPatternFactory;
import builder.datacentre.layout.LayoutPatternFactory.LayoutPatternType;
import builder.prefs.BuilderPreferences;

import config.DatacentreGenerator;
import config.physical.ConfigAisle;
import config.physical.ConfigDatacentre;
import config.physical.ConfigRack;

public class DCGenWindow extends JDialog
{
	private static final long serialVersionUID = 3542358617167450227L;

	MainWindow mWindow;
	BuilderPreferences mPrefs;
	JTabbedPane mTabs;
	GenOptionsPanel mOptionsPanel;
	GenToolsPanel mToolsPanel;

	// Tabs
	DCGenWindowByServer mTabServer;

	public DCGenWindow(MainWindow pWindow, BuilderPreferences pPrefs)
	{
		// Set this as modal
		super(pWindow.getFrame(), true);

		// Set variables
		mWindow = pWindow;
		mPrefs = pPrefs;

		// Setup Panel
		this.setLayout(new java.awt.BorderLayout());
		this.setTitle("Generate New Datacentre");

		// Setup side panes
		mOptionsPanel = new GenOptionsPanel();
		mToolsPanel = new GenToolsPanel();

		// Setup side holder Panel
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		sidePanel.add(mOptionsPanel);
		sidePanel.add(mToolsPanel);
		sidePanel.add(Box.createVerticalGlue());
		sidePanel.add(new ButtonPanel());

		// add to dialog
		add(sidePanel, java.awt.BorderLayout.WEST);
		// add(new ButtonPanel(), java.awt.BorderLayout.SOUTH);

		// Instantiate Tab Panes
		mTabServer = new DCGenWindowByServer(mPrefs, mWindow.getFrame());

		// Setup tabs
		add(mTabServer, java.awt.BorderLayout.CENTER);
		// mTabs = new JTabbedPane();
		// mTabs.addTab("By Server", mPrefs.getImage("server_small.png"),
		// mTabServer, "Generate a Datacenter by Servers");

		// add(mTabs, java.awt.BorderLayout.CENTER);

		// Set Size
		setSize(new Dimension(1024, 768));

		// Display
		setLocationRelativeTo(mWindow.getFrame());
		setVisible(true);
	}

	/**
	 * Means that this dialog has been instructed to close
	 */
	private void closeGUIOption()
	{
		this.dispose();
	}

	/**
	 * Means that this dialog has been instructed to generate a datacentre
	 */
	private void generateGUIOption()
	{
		// Check for huge
		if (mOptionsPanel.getNumAisles() * mOptionsPanel.getNumRacks() >= 100000)
		{
			int result = JOptionPane
					.showConfirmDialog(
							this,
							"The options you have selected will generated a Datacentre with over 100,000 (approx. 2 million servers, depending on other settings) racks. \n This may cause slowdown or even crashes on less powerful machines. \n Please ensure that you have allocated enough free memory to CloudSIM Builder before you continue.",
							"Very Large Datacentre Warning",
							JOptionPane.OK_CANCEL_OPTION);
			if (result != JOptionPane.OK_OPTION)
			{
				return;
			}
		}

		// Check if the DC is actually big enough...
		int a = mOptionsPanel.getNumAisles();
		int r = mOptionsPanel.getNumRacks();

		int levels = (int) Math.ceil(((double) a * 2)
				/ mOptionsPanel.getDCSizeY());
		int aisleH = r;
		if (mOptionsPanel.getTopAC())
			aisleH++;
		if (mOptionsPanel.getBottomAC())
			aisleH++;
		int totalH = levels * (aisleH + 2);

		if (totalH > (mOptionsPanel.getDCSizeX() + 3)) // +2 because we can ignore the last two spaces, we dont' care if we run into those, +1 because count from 0
		{
			JOptionPane.showMessageDialog(this,
					"The Datacentre is not big enough for that many Aisles",
					"Datacentre Not Big Enough", JOptionPane.ERROR_MESSAGE);
			return;
		}

		DatacentreGenerator gen = new DatacentreGenerator(
				mTabServer.getServerList(), mOptionsPanel.getNumAisles(),
				mOptionsPanel.getNumRacks(), new int[]
				{ mOptionsPanel.getAisleNetDistance(),
						mOptionsPanel.getRackNetDistance(),
						mTabServer.getNetworkDistance() }, new int[]
				{ mOptionsPanel.getDCSizeX(), mOptionsPanel.getDCSizeY() },
				mOptionsPanel.getName(), mOptionsPanel.getTopAC(),
				mOptionsPanel.getBottomAC(),
				LayoutPatternFactory.getLayoutPattern(LayoutPatternType.LONG_AISLES),
				1
				);
		mWindow.generateDCGUIOption(gen);
	}

	/**
	 * Creates and manages the buttons on the lower panel
	 * 
	 * @author James Laverack
	 * 
	 */
	private class ButtonPanel extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 4075658498148973669L;

		JButton closeButton;
		JButton closeAndGenerateButton;
		JButton generateButton;

		public ButtonPanel()
		{
			setBorder(new EmptyBorder(10, 10, 10, 10));
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

			closeButton = new JButton("Close");
			closeButton.addActionListener(this);
			add(closeButton);
			add(Box.createHorizontalGlue());
			closeAndGenerateButton = new JButton("Close and Generate");
			closeAndGenerateButton.addActionListener(this);
			add(closeAndGenerateButton);
			add(Box.createHorizontalGlue());
			generateButton = new JButton("Generate");
			generateButton.addActionListener(this);
			add(generateButton);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object s = e.getSource();
			if (s.equals(closeButton))
			{
				closeGUIOption();
			} else if (s.equals(generateButton))
			{
				generateGUIOption();
			} else if (s.equals(closeAndGenerateButton))
			{
				generateGUIOption();
				closeGUIOption();
			}

		}
	}

	/**
	 * Creates and manages the tools box at the side
	 * 
	 * @author James Laverack
	 * 
	 */
	private class GenToolsPanel extends JPanel implements ActionListener
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JSpinner mNumAisles;
		JButton mFixHeight;

		public GenToolsPanel()
		{
			// Set pretty border
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Generator Tools"),
					new EmptyBorder(10, 10, 10, 10)));

			// Set Layout
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			// Setup controls
			mNumAisles = new JSpinner(new SpinnerNumberModel(6, 1,
					Integer.MAX_VALUE, 1));
			mFixHeight = new JButton("Set Width");
			mFixHeight.addActionListener(this);

			// Populate
			JPanel lab = new JPanel();
			lab.add(new JLabel("Fix Datacentre Size"));
			lab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			this.add(lab);
			this.add(Box.createVerticalStrut(10));
			JPanel panSetRows = new JPanel();
			panSetRows
					.setLayout(new BoxLayout(panSetRows, BoxLayout.LINE_AXIS));
			panSetRows.add(mNumAisles);
			panSetRows.add(Box.createHorizontalGlue());
			panSetRows.add(mFixHeight);
			panSetRows.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			this.add(panSetRows);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source == mFixHeight)
			{
				// Get the number of rows we want
				int numRows = ((Number) mNumAisles.getValue()).intValue();

				// We know the width of the isles and how many rows of aisles we
				// want
				// From this we can calcuate the number of aisles in one row of
				// aisles.
				// (Note that this is integer division, so it automaticly floors
				// the result, which is what we want.)
				// Additonally, this assumes that an Aisle is 1 rack wide with 1
				// block of space between aisles.
				int aislesInRows = mOptionsPanel.getDCSizeY() / 2;

				// Now we know how many rows we want, so just multiply to two to
				// get the total number of aisles and then set the control
				mOptionsPanel.setNumAisles(numRows * aislesInRows);

				// Now we can calculate the height of the datacentre based on
				// the height of the aisles
				// First, how tall is an aisle? We have to account for the
				// existance of the AC units, and the 2-block spacing between
				// aisles
				int aisleHeight = mOptionsPanel.getNumRacks();
				if (mOptionsPanel.getTopAC())
					aisleHeight++;
				if (mOptionsPanel.getBottomAC())
					aisleHeight++;
				aisleHeight += 2;
				// Now we can set the control with the correct value
				mOptionsPanel.setDCSizeX(aisleHeight * numRows);

			}

		}
	}

	/**
	 * Creates and manages the options panel at the side
	 * 
	 * @author James Laverack
	 * 
	 */
	private class GenOptionsPanel extends JPanel
	{
		private static final long serialVersionUID = 7104933247258713454L;

		private JSpinner mNumAisles;
		private JSpinner mNumRacks;
		private JCheckBox mTopAC;
		private JCheckBox mBottomAC;
		private JSpinner mAisleNetDistance;
		private JSpinner mRackNetDistance;
		private JSpinner mDCSizeX;
		private JSpinner mDCSizeY;
		private JTextField mDCName;

		/**
		 * Sets up the panel and creates the objects.
		 */
		public GenOptionsPanel()
		{
			// Set pretty border
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Generator Options"),
					new EmptyBorder(10, 10, 10, 10)));

			// Set Layout
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			// Create spinners
			mNumAisles = new JSpinner(new SpinnerNumberModel(24, 1,
					Integer.MAX_VALUE, 1));
			mNumRacks = new JSpinner(new SpinnerNumberModel(8, 1,
					Integer.MAX_VALUE, 1));
			mAisleNetDistance = new JSpinner(new SpinnerNumberModel(ConfigAisle.DEFAULT_NETWORK_DISTANCE, 1,
					Integer.MAX_VALUE, 1));
			mRackNetDistance = new JSpinner(new SpinnerNumberModel(ConfigRack.DEFAULT_NETWORK_DISTANCE, 1,
					Integer.MAX_VALUE, 1));
			mDCSizeX = new JSpinner(new SpinnerNumberModel(ConfigDatacentre.DEFAULT_SIZE_X, 1,
					Integer.MAX_VALUE, 1));
			mDCSizeY = new JSpinner(new SpinnerNumberModel(ConfigDatacentre.DEFAULT_SIZE_Y, 1,
					Integer.MAX_VALUE, 1));

			// Create text area
			mDCName = new JTextField("Generated Datacentre");

			// Create checks
			mTopAC = new JCheckBox();
			mBottomAC = new JCheckBox();

			// create panels
			add(createPanel("Number of Aisles", mNumAisles));
			add(createSpacer());
			add(createPanel("Top AC Unit", mTopAC));
			add(createSpacer());
			add(createPanel("Racks per Aisle", mNumRacks));
			add(createSpacer());
			add(createPanel("Bottom AC Unit", mBottomAC));
			add(Box.createVerticalStrut(10));
			add(createPanel("Aisle Network Distance", mAisleNetDistance));
			add(createSpacer());
			add(createPanel("Rack Network Distance", mRackNetDistance));
			add(Box.createVerticalStrut(10));
			add(createPanel("Datacentre Width", mDCSizeX));
			add(createSpacer());
			add(createPanel("Datacentre Height", mDCSizeY));
			add(Box.createVerticalStrut(10));
			add(createPanel("Datacentre Name", mDCName));
			// add(Box.createVerticalGlue());

		}

		private Component createSpacer()
		{
			return Box.createVerticalStrut(5);
		}

		private JPanel createPanel(String text, JComponent control)
		{
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.LINE_AXIS));
			pan.add(new JLabel(text));
			pan.add(Box.createHorizontalGlue());
			control.setMaximumSize(new Dimension(30, 20));
			pan.add(control);
			pan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			return pan;
		}

		/**
		 * Returns the integer value of the Num Aisles spinner
		 * 
		 * @return
		 */
		public int getNumAisles()
		{
			return ((Number) mNumAisles.getValue()).intValue();
		}
		public void setNumAisles(int num)
		{
			mNumAisles.setValue(num);
		}
		public int getNumRacks()
		{
			return ((Number) mNumRacks.getValue()).intValue();
		}

		public int getAisleNetDistance()
		{
			return ((Number) mAisleNetDistance.getValue()).intValue();
		}

		public int getRackNetDistance()
		{
			return ((Number) mRackNetDistance.getValue()).intValue();
		}

		public int getDCSizeX()
		{
			return ((Number) mDCSizeX.getValue()).intValue();
		}

		public int getDCSizeY()
		{
			return ((Number) mDCSizeY.getValue()).intValue();
		}

		public void setDCSizeX(int size)
		{
			mDCSizeX.setValue(size);
		}

		public String getName()
		{
			return mDCName.getText();
		}

		public boolean getTopAC()
		{
			return mTopAC.isSelected();
		}

		public boolean getBottomAC()
		{
			return mBottomAC.isSelected();
		}
	}
}
