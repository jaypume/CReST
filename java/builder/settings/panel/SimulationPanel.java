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
package builder.settings.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.sim.configparams.SimModuleConfigParams;
import utility.time.LengthOfTime;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class SimulationPanel extends AbstractSimPanel {

	protected JSpinner mSpnSeed;
	protected JButton randomiseSeedBttn;
	protected JCheckBox mChkBoxWaitSteadyState;
	protected JSpinner mSpnStartTime;
	protected JSpinner mSpnEndTime;
	protected JComboBox endTimeUnitCombo;
	protected JSpinner mSpnMaxEvents;
	protected JCheckBox mChkBoxReplacementFunction;
	protected JCheckBox mChkBoxPresetServerTypes;
	
	public SimulationPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf, prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen,
			ActionListener fileChooserListener) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Simulation Settings"),
				new EmptyBorder(10, 10, 10, 10)));

		// Setup controls
		
		//Get sim configuration parameters
		SimModuleConfigParams params = 
				(SimModuleConfigParams) Module.SIM_MODULE.getParams();
		
		// Random Seed
		JPanel seedPanel = new JPanel();
		seedPanel.setLayout(new BoxLayout(seedPanel, BoxLayout.LINE_AXIS));
		seedPanel.add(new JLabel(SimModuleConfigParams.SEED_GUI_LABEL));
		seedPanel.add(Box.createHorizontalStrut(10));
		mSpnSeed = new JSpinner(new SpinnerNumberModel(params.getSeed(),
				Long.MIN_VALUE, Long.MAX_VALUE, 1));
		mSpnSeed.addChangeListener(listen);
		seedPanel.add(mSpnSeed);
		seedPanel.add(Box.createHorizontalStrut(10)); //was 10
		randomiseSeedBttn = new JButton("Randomise Seed");
		randomiseSeedBttn.addActionListener(new Randomiser());
		seedPanel.add(randomiseSeedBttn);
		seedPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(seedPanel);
		panel.add(Box.createVerticalStrut(10));

		// Wait Steady State
		JPanel waitSteadyStatePanel = new JPanel();
		waitSteadyStatePanel.setLayout(new BoxLayout(waitSteadyStatePanel,
				BoxLayout.LINE_AXIS));
		mChkBoxWaitSteadyState = new JCheckBox(SimModuleConfigParams.WAIT_STEADY_STATE_GUI_LABEL);
		mChkBoxWaitSteadyState.setSelected(params.isWaitForSteadyState());
		mChkBoxWaitSteadyState.addActionListener(listen);
		waitSteadyStatePanel.add(mChkBoxWaitSteadyState);
		waitSteadyStatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(waitSteadyStatePanel);
		panel.add(Box.createHorizontalStrut(10));

		// Start Time
		JPanel startTimePanel = new JPanel();
		startTimePanel
				.setLayout(new BoxLayout(startTimePanel, BoxLayout.LINE_AXIS));
		startTimePanel.add(new JLabel(SimModuleConfigParams.START_TIME_GUI_LABEL));
		startTimePanel.add(Box.createHorizontalStrut(10));
		mSpnStartTime = new JSpinner(new SpinnerNumberModel(
				params.getStartTime(), Long.MIN_VALUE, Long.MAX_VALUE, 1));
		mSpnStartTime.addChangeListener(listen);
		startTimePanel.add(mSpnStartTime);
		startTimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(startTimePanel);
		panel.add(Box.createVerticalStrut(10));

		// End Time
		JPanel endTimePanel = new JPanel();
		endTimePanel.setLayout(new BoxLayout(endTimePanel, BoxLayout.LINE_AXIS));
		endTimePanel.add(new JLabel(SimModuleConfigParams.END_TIME_GUI_LABEL));
		endTimePanel.add(Box.createHorizontalStrut(10));
		mSpnEndTime = new JSpinner(new SpinnerNumberModel(params.getEndTime(),
				Long.MIN_VALUE, Long.MAX_VALUE, 1));
		mSpnEndTime.addChangeListener(listen);
		endTimePanel.add(mSpnEndTime);
		endTimePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

		endTimePanel.add(Box.createHorizontalStrut(10));
		
		endTimeUnitCombo = new JComboBox(LengthOfTime.values());
		endTimeUnitCombo.setSelectedItem(params.getEndTimeUnit());
		endTimeUnitCombo.addActionListener(listen);
		
		endTimePanel.add(endTimeUnitCombo);
		
		panel.add(endTimePanel);
		panel.add(Box.createVerticalStrut(10));
		
		// Max Events
		JPanel panMaxEvents = new JPanel();
		panMaxEvents
				.setLayout(new BoxLayout(panMaxEvents, BoxLayout.LINE_AXIS));
		panMaxEvents.add(new JLabel(SimModuleConfigParams.MAX_EVENTS_GUI_LABEL));
		panMaxEvents.add(Box.createHorizontalStrut(10));
		mSpnMaxEvents = new JSpinner(
				new SpinnerNumberModel(params.getMaxNumEvents(),
						Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		mSpnMaxEvents.addChangeListener(listen);
		panMaxEvents.add(mSpnMaxEvents);
		panMaxEvents.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panMaxEvents);
		panel.add(Box.createHorizontalStrut(10));

		return panel;
	}

	private class Randomiser implements ActionListener
	{
		private Random rand;

		Randomiser()
		{
			rand = new Random();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			mSpnSeed.setValue(rand.nextInt());
		}

	}

	@Override
	public void resaveElements() {
		
		SimModuleConfigParams params = new SimModuleConfigParams(
				((Number) mSpnSeed.getValue()).longValue(),
				mChkBoxWaitSteadyState.isSelected(),
				((Number) mSpnStartTime.getValue()).longValue(),
				((Number) mSpnEndTime.getValue()).longValue(),
				(LengthOfTime) endTimeUnitCombo.getSelectedItem(),
				((Number) mSpnMaxEvents.getValue()).intValue()
				);

		
		Module.SIM_MODULE.setParams(params); 
	}
}
