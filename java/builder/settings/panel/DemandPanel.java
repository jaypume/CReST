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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.demand.configparams.DemandModuleConfigParams;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class DemandPanel extends AbstractSimPanel{
	
	public JTextField mTxtDemandFile;
	public JButton mBtnDemandFile;
	
	public DemandPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf,prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen, ActionListener fileChooserListener)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Demand Settings"),
				new EmptyBorder(10, 10, 10, 10)));

		JPanel panDemandFile = new JPanel();
		panDemandFile.setLayout(new BoxLayout(panDemandFile,
				BoxLayout.LINE_AXIS));
		panDemandFile.add(new JLabel("Demand Data File"));
		panDemandFile.add(Box.createHorizontalStrut(10));

		mTxtDemandFile = new JTextField();
		mTxtDemandFile.setText(((DemandModuleConfigParams) Module.DEMAND_MODULE.getParams()).getFilename());
		mTxtDemandFile.getDocument().addDocumentListener(listen);
		panDemandFile.add(mTxtDemandFile);

		panDemandFile.add(Box.createHorizontalStrut(10));
		mBtnDemandFile = new JButton(mPrefs.getImage("open_demandfile.png"));
		mBtnDemandFile.setPreferredSize(new Dimension(20, 20));
		mBtnDemandFile.addActionListener(fileChooserListener);
		panDemandFile.add(mBtnDemandFile);
		panDemandFile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panDemandFile);
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	@Override
	public void resaveElements() {
		
//		mConf.setDemandDataFile(mTxtDemandFile.getText());
		DemandModuleConfigParams params = new DemandModuleConfigParams(mTxtDemandFile.getText(), Module.BROKER_MODULE.isActive());
		Module.DEMAND_MODULE.setParams(params); 
		
		System.out.println("Resaved: " + Module.DEMAND_MODULE.getFullDescriptionString()); 
		
	}
}
