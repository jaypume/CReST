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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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

import builder.prefs.BuilderPreferences;

import config.physical.ConfigCPU;
import config.physical.ConfigRAM;
import config.physical.ConfigServer;

/**
 * Note that this class copies a lot of code from SimplePhysicalView.java, this
 * is because it's *slighty* different so abstracting it out so that the code
 * was shared would be a nightmare.
 * 
 * @author James Laverack
 * 
 */

public class DCGenWindowByServer extends JPanel
{
	private static final long serialVersionUID = -4597919346434571369L;

	// Fields
	private BuilderPreferences mPrefs;
	private JFrame thisPanel;

	// Update variables
	private boolean propagateChanges;

	// Working things
	private java.util.List<ConfigServer> mServers = new ArrayList<ConfigServer>();
	private int mNetDistance;
	private ConfigServer mWorkingServer;
	private ConfigCPU mWorkingCPU;

	// Main Panel
	private JPanel mMainPanel;
	private JList mMainServerList;
	private JButton mMainServerAddBtn;
	private JButton mMainServerDelBtn;
	private JSpinner mMainServerNetworkDistance;

	// Server Panel
	private JPanel mServerPanel;
	private JTextField mServerModel;
	private JList mServerCPUList;
	private JButton mServerCPUAddBtn;
	private JButton mServerCPUCpyBtn;
	private JButton mServerCPUDelBtn;
	private JSpinner mServerSize;
	private JSpinner mServerMeanFailureTime;
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

	public ConfigServer[] getServerList()
	{
		return mServers.toArray(new ConfigServer[mServers.size()]);
	}

	public int getNetworkDistance()
	{
		return mNetDistance;
	}

	public DCGenWindowByServer(BuilderPreferences pPrefs, JFrame parent)
	{
		mPrefs = pPrefs;
		thisPanel = parent;

		// /////////////////
		// Create Panels //
		// /////////////////

		propagateChanges = true;

		// mFrameWorld.add(Box.createVerticalGlue());
		// / Datacentre Panel
		setupMainPanel();
		setupServerPanel();
		setupCPUPanel();

		// Add to main panel
		this.setLayout(new GridLayout(0, 3));
		this.add(mMainPanel);
		this.add(mServerPanel);
		this.add(mCPUPanel);

		// Set defaults
		setDefaults();
		
		// Run setup
		populateMain();

	}

	private String[] getServerModels()
	{
		String[] models = new String[mServers.size()];

		for (int i = 0; i < mServers.size(); i++)
		{
			models[i] = mServers.get(i).getModel();
		}

		return models;
	}

	private void setupMainPanel()
	{
		// / DC Panel
		mMainPanel = new JPanel();
		mMainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Server List"),
				new EmptyBorder(10, 10, 10, 10)));
		mMainPanel.setLayout(new BoxLayout(mMainPanel, BoxLayout.PAGE_AXIS));

		// / Listener
		MainPanelEventHandler event = new MainPanelEventHandler();

		// Name
		mMainPanel.add(getPanelOfText(new JLabel("Servers")));
		// Aisle list
		mMainServerList = new JList();
		mMainServerList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// mRackServerList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mMainServerList.setVisibleRowCount(-1);
		mMainServerList.addListSelectionListener(new ServerListListener());
		JScrollPane svListScroller = new JScrollPane(mMainServerList);
		svListScroller.setPreferredSize(new Dimension(250, 80));
		mMainPanel.add(svListScroller);

		// Aisle controls
		JPanel svControlPanel = new JPanel();
		svControlPanel.setLayout(new BoxLayout(svControlPanel,
				BoxLayout.LINE_AXIS));
		mMainServerAddBtn = new JButton(mPrefs.getImage("server_add.png"));
		mMainServerAddBtn.addActionListener(event);
		svControlPanel.add(mMainServerAddBtn);
		svControlPanel.add(Box.createHorizontalGlue());
		mMainServerDelBtn = new JButton(mPrefs.getImage("server_delete.png"));
		mMainServerDelBtn.addActionListener(event);
		svControlPanel.add(mMainServerDelBtn);
		mMainPanel.add(Box.createVerticalStrut(10));
		mMainPanel.add(svControlPanel);

		mMainPanel.add(Box.createVerticalStrut(20));

		// Datacentre network distance
		mMainPanel.add(getPanelOfText(new JLabel("Network Distance")));
		mMainPanel.add(Box.createVerticalStrut(10));
		mMainServerNetworkDistance = new JSpinner(new SpinnerNumberModel(0, 0,
				Integer.MAX_VALUE, 1));
		mMainServerNetworkDistance.setMaximumSize(new Dimension(
				Integer.MAX_VALUE, 15));
		mMainServerNetworkDistance.addChangeListener(event);
		mMainPanel.add(mMainServerNetworkDistance);

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
		mServerCPUAddBtn = new JButton(mPrefs.getImage("cpu_add.png"));
		mServerCPUAddBtn.addActionListener(event);
		cpControlPanel.add(mServerCPUAddBtn);
		cpControlPanel.add(Box.createHorizontalGlue());

		mServerCPUCpyBtn = new JButton(mPrefs.getImage("cpu_copy.png"));
		mServerCPUCpyBtn.addActionListener(event);
		cpControlPanel.add(mServerCPUCpyBtn);
		cpControlPanel.add(Box.createHorizontalGlue());

		mServerCPUDelBtn = new JButton(mPrefs.getImage("cpu_delete.png"));
		mServerCPUDelBtn.addActionListener(event);
		cpControlPanel.add(mServerCPUDelBtn);
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerPanel.add(cpControlPanel);

		mServerPanel.add(Box.createVerticalStrut(10));

		mServerPanel.add(getPanelOfText(new JLabel("Mean Fail Time")));
		mServerPanel.add(Box.createVerticalStrut(10));
		mServerMeanFailureTime = new JSpinner(new SpinnerNumberModel(0, 0,
				Long.MAX_VALUE, 1));
		mServerMeanFailureTime.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				15));
		mServerMeanFailureTime.addChangeListener(event);
		((JSpinner.NumberEditor) mServerMeanFailureTime.getEditor())
				.getTextField().getDocument().addDocumentListener(event);
		mServerPanel.add(mServerMeanFailureTime);

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

	private int getID(ConfigServer s)
	{
		for (int i = 0; i < mServers.size(); i++)
		{
			if (mServers.get(i).equals(s))
			{
				return i;
			}
		}
		return -1;
	}
	
	private void setDefaults()
	{
		mNetDistance = 1;
		mServers.add(new ConfigServer(1));
		mServers.add(new ConfigServer(2));
		mServers.add(new ConfigServer(3));
	}

	// //////////////// //
	// POPULATE METHODS //
	// //////////////// //

	private void populateMain()
	{

		ConfigServer current = mWorkingServer;
		ConfigCPU currentCPU = mWorkingCPU;
		mMainServerList.setListData(getServerModels());
		mMainServerNetworkDistance.setValue(mNetDistance);
		mWorkingServer = current;
		if (mWorkingServer != null)
			mMainServerList.setSelectedIndex(getID(mWorkingServer));
		mWorkingCPU = currentCPU;
		
		// Populate other panles
		if (propagateChanges)
			populateServer();
	}

	private void populateServer()
	{
		if (mWorkingServer != null)
		{
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
			mWorkingCPU = current;
			if (mWorkingCPU != null)
				mServerCPUList.setSelectedIndex(mWorkingServer
						.getID(mWorkingCPU));
			mServerCPUAddBtn.setEnabled(true);
			mServerCPUCpyBtn.setEnabled(true);
			mServerCPUDelBtn.setEnabled(true);
			mServerMeanFailureTime.setValue(mWorkingServer.getMeanFailTime());
			mServerMeanFailureTime.setEnabled(true);
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
			mServerCPUCpyBtn.setEnabled(false);
			mServerCPUDelBtn.setEnabled(false);
			mServerMeanFailureTime.setValue(0);
			mServerMeanFailureTime.setEnabled(false);
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

	// ////////////// //
	// EVENT HANDLERS //
	// ////////////// //

	private class MainPanelEventHandler implements ActionListener,
			ChangeListener, DocumentListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();
			if (source.equals(mMainServerAddBtn))
			{
				mServers.add(new ConfigServer());
				populateMain();

			} else if (source.equals(mMainServerDelBtn))
			{
				int result = JOptionPane.showConfirmDialog(thisPanel,
						"Are you sure you want to delete this Server?");
				if (result == JOptionPane.OK_OPTION)
				{
					mServers.remove(mWorkingServer);
					mWorkingServer = null;
					if (propagateChanges)
						populateMain();
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

			mNetDistance = ((Number) mMainServerNetworkDistance.getValue())
					.intValue();

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
			} else if (source.equals(mServerCPUCpyBtn))
			{
				mWorkingServer.addCPU(mWorkingCPU.duplicate());
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
				mWorkingServer.setMeanFailTimeInDays(((Number) mServerMeanFailureTime
						.getValue()).longValue());
				ConfigRAM ram = mWorkingServer.getRAM();
				ram.setModel(mServerRAMModel.getText());
				ram.setSize(((Number) mServerRAMSize.getValue()).intValue());
				ram.setSpeed(((Number) mServerRAMSpeed.getValue())
						.doubleValue());

				propagateChanges = false;
				populateMain();
				propagateChanges = true;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e)
		{
			updated();
		}

	}

	private class CPUPanelEventHandler implements ChangeListener,
			DocumentListener
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
				mWorkingCPU
						.setCores(((Number) mCPUCores.getValue()).intValue());
				mWorkingCPU
						.setSpeed(((Number) mCPUSpeed.getValue()).intValue());

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
	// /////////////// //

	private class ServerListListener implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (mMainServerList.getSelectedIndex() != -1)
			{
				mWorkingServer = mServers.get(mMainServerList
						.getSelectedIndex());
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
