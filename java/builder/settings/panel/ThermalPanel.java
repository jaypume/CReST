/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012, 2013 John Cartlidge 
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
import sim.module.thermal.configParams.ThermalModuleConfigParams;
import sim.module.thermal.model.ThermalModelFactory.ThermModel;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class ThermalPanel extends AbstractSimPanel{
	
	protected JComboBox model;
	protected JSpinner period;
	
	public ThermalPanel (SettingsManager conf, BuilderPreferences prefs) {
		super(conf,prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen, ActionListener fileChooserListener) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thermal Model"),
				new EmptyBorder(10, 10, 10, 10)));
		
		//Get Thermal configuration parameters
		ThermalModuleConfigParams params = (ThermalModuleConfigParams) Module.THERMAL_MODULE.getParams();
		
		//Thermal Model
		JPanel thermalPanel = new JPanel();
		thermalPanel.setLayout(new BoxLayout(thermalPanel,
				BoxLayout.LINE_AXIS));
		thermalPanel.add(new JLabel(ThermModel.getLabel()));
		thermalPanel.add(Box.createHorizontalStrut(10));
		model = new JComboBox(ThermModel.values());
		model.setSelectedIndex(params.getThermalModelType().ordinal());
		
		model.addActionListener(listen);
		thermalPanel.add(model);
		panel.add(thermalPanel);
		panel.add(Box.createVerticalStrut(10));
		
		//Time period
		JPanel periodPanel = new JPanel();
		periodPanel.setLayout(new BoxLayout(periodPanel, BoxLayout.LINE_AXIS));
		periodPanel.add(new JLabel("Update Period (seconds)"));
		periodPanel.add(Box.createHorizontalStrut(10));
		period = new JSpinner(new SpinnerNumberModel(
				params.getEventPeriod(), 0,
				Integer.MAX_VALUE, 1));
		period.addChangeListener(listen);
		periodPanel.add(period);
		panel.add(periodPanel);
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	@Override
	public void resaveElements() {

		ThermalModuleConfigParams params = new ThermalModuleConfigParams(
				(ThermModel) model.getSelectedItem(),
				((Number) period.getValue()).longValue()
				);
			
		Module.THERMAL_MODULE.setParams(params); 
		
		System.out.println("Resaved: " + Module.THERMAL_MODULE.getFullDescriptionString()); 
	}

}
