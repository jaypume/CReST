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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.subscriptions.configparams.SubscriptionsModuleConfigParams;
import sim.module.subscriptions.protocol.UpdateProtocolFactory.Protocol;
import sim.module.subscriptions.topology.NetworkTopologyFactory.NetworkTopology;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class SubscriptionsPanel extends AbstractSimPanel{
	
	protected JComboBox topology;
	protected JComboBox protocol;
	
	protected JSpinner miu;
	protected JSpinner maxSubs;
	protected JSpinner rewire;
	protected JCheckBox singleNodeUpdate;
	
	public SubscriptionsPanel (SettingsManager conf, BuilderPreferences prefs) {
		super(conf,prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen, ActionListener fileChooserListener) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Subscriptions"),
				new EmptyBorder(10, 10, 10, 10)));
		
		//Get subscription configuration parameters
		SubscriptionsModuleConfigParams params = (SubscriptionsModuleConfigParams) Module.SUBSCRIPTION_MODULE.getParams();
		
		//Protocols
		JPanel protocolsPanel = new JPanel();
		protocolsPanel.setLayout(new BoxLayout(protocolsPanel,
				BoxLayout.LINE_AXIS));
		protocolsPanel.add(new JLabel(Protocol.getLabel()));
		protocolsPanel.add(Box.createHorizontalStrut(10));
		protocol = new JComboBox(Protocol.values());
		protocol.setSelectedIndex(params.getProtocolType().ordinal());
		
		protocol.addActionListener(listen);
		protocolsPanel.add(protocol);
		// panTypes.setMaximumSize(new Dimension(100, 20));
		panel.add(protocolsPanel);
		panel.add(Box.createVerticalStrut(10));
		
		//Topologies
		JPanel topologiesPanel = new JPanel();
		topologiesPanel.setLayout(new BoxLayout(topologiesPanel, BoxLayout.LINE_AXIS));
		topologiesPanel.add(new JLabel(NetworkTopology.getLabel()));
		topologiesPanel.add(Box.createHorizontalStrut(10));
		topology = new JComboBox(NetworkTopology.values());
		topology.setSelectedIndex(params.getTopologyType().ordinal());
		topology.addActionListener(listen);
		topologiesPanel.add(topology);
		// panTypes.setMaximumSize(new Dimension(100, 20));
		panel.add(topologiesPanel);
		panel.add(Box.createVerticalStrut(10));

		//Maximum subscriptions
		JPanel panelMaxSubs = new JPanel();
		panelMaxSubs.setLayout(new BoxLayout(panelMaxSubs, BoxLayout.LINE_AXIS));
		panelMaxSubs.add(new JLabel("Maximum Subscriptions"));
		panelMaxSubs.add(Box.createHorizontalStrut(10));
		maxSubs = new JSpinner(new SpinnerNumberModel(
				params.getMaxSubscriptions(), 0,
				Integer.MAX_VALUE, 1));
		maxSubs.addChangeListener(listen);
		panelMaxSubs.add(maxSubs);
		// panMiu.setMaximumSize(new Dimension(100, 20));
		panel.add(panelMaxSubs);
		panel.add(Box.createVerticalStrut(10));
				
		//Miu Value
		JPanel panelMiuValue = new JPanel();
		panelMiuValue.setLayout(new BoxLayout(panelMiuValue, BoxLayout.LINE_AXIS));
		panelMiuValue.add(new JLabel("Miu Value"));
		panelMiuValue.add(Box.createHorizontalStrut(10));
		miu = new JSpinner(new SpinnerNumberModel(
				params.getMiu(), 0,
				1, 0.01));
		miu.addChangeListener(listen);
		panelMiuValue.add(miu);
		// panMiu.setMaximumSize(new Dimension(100, 20));
		panel.add(panelMiuValue);
		panel.add(Box.createVerticalStrut(10));	

		//Rewiring value
		JPanel panelRewiring = new JPanel();
		panelRewiring.setLayout(new BoxLayout(panelRewiring, BoxLayout.LINE_AXIS));
		panelRewiring.add(new JLabel("Rewiring Value"));
		panelRewiring.add(Box.createHorizontalStrut(10));
		rewire = new JSpinner(new SpinnerNumberModel(
				params.getRewire(), 0,
				1, 0.01));
		rewire.addChangeListener(listen);
		panelRewiring.add(rewire);
		// panMiu.setMaximumSize(new Dimension(100, 20));
		panel.add(panelRewiring);
		panel.add(Box.createVerticalStrut(10));	
		
		//singlenodeupdate value
		JPanel panelSingleNodeUpdate = new JPanel();
		panelSingleNodeUpdate.setLayout(new BoxLayout(panelSingleNodeUpdate, BoxLayout.LINE_AXIS));
		//panelSingleNodeUpdate.add(new JLabel("Single Node Update"));
		//panelSingleNodeUpdate.add(Box.createHorizontalStrut(10));
		singleNodeUpdate = new JCheckBox("Single Node Update (Unchecked- All Nodes)", params.getSingleNodeUpdate());
		singleNodeUpdate.addActionListener(listen);
		panelSingleNodeUpdate.add(singleNodeUpdate);
		panelSingleNodeUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panelSingleNodeUpdate);

		return panel;
	}

	@Override
	public void resaveElements() {
		
		SubscriptionsModuleConfigParams params = new SubscriptionsModuleConfigParams(
				(Protocol) protocol.getSelectedItem(),
				(NetworkTopology) topology.getSelectedItem(),
				((Number) maxSubs.getValue()).intValue(),
				((Number) miu.getValue()).doubleValue(),
				((Number) rewire.getValue()).doubleValue(),
				singleNodeUpdate.isSelected()
				);
		
		Module.SUBSCRIPTION_MODULE.setParams(params); 
		
		System.out.println("Resaved: " + Module.SUBSCRIPTION_MODULE.getFullDescriptionString()); 
	}

}
