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
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.event.configparams.EventsModuleConfigParams;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.ProbabilityDistribution;
import config.SettingsManager;

//TODO - JC May 2012: This is not doing what it should.
//SpawnerFrequency and distribution is used currently by event spawner /eventqueue
//However, *every* module should actually have a distribution and mean associated with it for events

public class EventsPanel extends AbstractSimPanel {

	protected JSpinner mSpnSpawnFreq;
	protected JComboBox probabilityDistributionComboBox;
	
	public EventsPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf, prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen,
			ActionListener fileChooserListener) {
		
		//Get configuration parameters
		EventsModuleConfigParams params = (EventsModuleConfigParams) Module.EVENTS_MODULE.getParams();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Events Settings"),
				new EmptyBorder(10, 10, 10, 10)));

		// Spawner Frequency
		JPanel panSpawnerFrequency = new JPanel();
		panSpawnerFrequency.setLayout(new BoxLayout(panSpawnerFrequency,
				BoxLayout.LINE_AXIS));
		panSpawnerFrequency.add(new JLabel("Spawner Frequency"));
		panSpawnerFrequency.add(Box.createHorizontalStrut(10));
		mSpnSpawnFreq = new JSpinner(new SpinnerNumberModel(
				params.getSpawnerFrequency(), Long.MIN_VALUE, Long.MAX_VALUE,
				1));
		mSpnSpawnFreq.addChangeListener(listen);
		panSpawnerFrequency.add(mSpnSpawnFreq);
		panSpawnerFrequency
				.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panSpawnerFrequency);
		panel.add(Box.createVerticalStrut(10));

		// Type Distribution Panel
		JPanel distributionPanel = new JPanel();
		distributionPanel.setLayout(new BoxLayout(distributionPanel, BoxLayout.LINE_AXIS));
		distributionPanel.add(new JLabel("Type Distribution"));
		distributionPanel.add(Box.createHorizontalStrut(10));
		probabilityDistributionComboBox = new JComboBox(ProbabilityDistribution.getValues());
		probabilityDistributionComboBox.setSelectedItem(params.getProbabilityDistribution().toString());
		probabilityDistributionComboBox.addActionListener(listen);
		distributionPanel.add(probabilityDistributionComboBox);
		distributionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(distributionPanel);
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	@Override
	public void resaveElements() {
		
		//The new way - set the file value directly in the module
		
		EventsModuleConfigParams params = new EventsModuleConfigParams(((Number) mSpnSpawnFreq.getValue()).longValue(), 
				ProbabilityDistribution.parseProb((String) probabilityDistributionComboBox.getSelectedItem()));
		
		Module.EVENTS_MODULE.setParams(params); 
		System.out.println("Resaved: " + Module.EVENTS_MODULE.getFullDescriptionString());
	}
}
