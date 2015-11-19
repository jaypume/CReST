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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.replacements.configparams.ReplacementModuleConfigParams;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class ReplacementsPanel extends AbstractSimPanel {

	protected JCheckBox mChkBoxReplaceInBlocks;
	protected JCheckBox mChkBoxReplacementFunction;
	protected JCheckBox mChkBoxPresetServerTypes;
	protected JSpinner mReplacementThresholdAisle;
	protected JSpinner mReplacementThresholdContainer;
	protected JSpinner mReplacementThresholdRack;
	
	public ReplacementsPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf, prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen,
			ActionListener fileChooserListener) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Server Replacements"),
				new EmptyBorder(10, 10, 10, 10)));

		// Setup controls
		
		//Get sim configuration parameters
		ReplacementModuleConfigParams params = 
				(ReplacementModuleConfigParams) Module.REPLACEMENTS_MODULE.getParams();
		
		// Replace In Blocks
		JPanel panReplaceInBlocks = new JPanel();
		panReplaceInBlocks.setLayout(new BoxLayout(panReplaceInBlocks,
				BoxLayout.LINE_AXIS));
		mChkBoxReplaceInBlocks = new JCheckBox("Block Replacements (Unchecked- Individual Replacements)");
		mChkBoxReplaceInBlocks.setSelected(params.isReplacementInBlocks());
		mChkBoxReplaceInBlocks.addActionListener(listen);
		panReplaceInBlocks.add(mChkBoxReplaceInBlocks);
		panReplaceInBlocks.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				20));
		panel.add(panReplaceInBlocks);
		//panel.add(Box.createVerticalStrut(10));
		
		// Replacement Function
		JPanel panReplacementFunction = new JPanel();
		panReplacementFunction.setLayout(new BoxLayout(panReplacementFunction,
				BoxLayout.LINE_AXIS));
		mChkBoxReplacementFunction = new JCheckBox("Use Replacement Function");
		mChkBoxReplacementFunction.setSelected(params.isReplacementViaFunctionTypes());
		mChkBoxReplacementFunction.addActionListener(listen);
		panReplacementFunction.add(mChkBoxReplacementFunction);
		panReplacementFunction.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				20));
		panel.add(panReplacementFunction);
		//panel.add(Box.createVerticalStrut(10));

		// Preset Server Types
		JPanel panPresetServerTypes = new JPanel();
		panPresetServerTypes.setLayout(new BoxLayout(panPresetServerTypes,
				BoxLayout.LINE_AXIS));
		mChkBoxPresetServerTypes = new JCheckBox("Use Preset Server Types");
		mChkBoxPresetServerTypes.setSelected(params.isReplacementViaPresetTypes());
		mChkBoxPresetServerTypes.addActionListener(listen);
		panPresetServerTypes.add(mChkBoxPresetServerTypes);
		panPresetServerTypes.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				20));
		panel.add(panPresetServerTypes);
		panel.add(Box.createVerticalStrut(10));
		
		//Replacement Threshold Aisle		
		JPanel panelReplacementThresholdAisle = new JPanel();
		panelReplacementThresholdAisle.setLayout(new BoxLayout(panelReplacementThresholdAisle, BoxLayout.LINE_AXIS));
		panelReplacementThresholdAisle.add(new JLabel("Aisle Replacement Threshold"));
		panelReplacementThresholdAisle.add(Box.createHorizontalStrut(10));
		mReplacementThresholdAisle = new JSpinner(new SpinnerNumberModel(
				params.getReplacementThresholdAisle(), 0,
				1, 0.01));
		panelReplacementThresholdAisle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mReplacementThresholdAisle.addChangeListener(listen);
		panelReplacementThresholdAisle.add(mReplacementThresholdAisle);
		panel.add(panelReplacementThresholdAisle);
		panel.add(Box.createVerticalStrut(10));	
		
	    //Replacement Threshold Container
		JPanel panelReplacementThresholdContainer = new JPanel();
		panelReplacementThresholdContainer.setLayout(new BoxLayout(panelReplacementThresholdContainer, BoxLayout.LINE_AXIS));
		panelReplacementThresholdContainer.add(new JLabel("Container Replacement Threshold"));
		panelReplacementThresholdContainer.add(Box.createHorizontalStrut(10));
		mReplacementThresholdContainer = new JSpinner(new SpinnerNumberModel(
				params.getReplacementThresholdContainer(), 0,
				1, 0.01));
		panelReplacementThresholdContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mReplacementThresholdContainer.addChangeListener(listen);
		panelReplacementThresholdContainer.add(mReplacementThresholdContainer);
		panel.add(panelReplacementThresholdContainer);
		panel.add(Box.createVerticalStrut(10));	
		
	    //Replacement Threshold Rack
		JPanel panelReplacementThresholdRack = new JPanel();
		panelReplacementThresholdRack.setLayout(new BoxLayout(panelReplacementThresholdRack, BoxLayout.LINE_AXIS));
		panelReplacementThresholdRack.add(new JLabel("Rack Replacement Threshold"));
		panelReplacementThresholdRack.add(Box.createHorizontalStrut(10));
		mReplacementThresholdRack = new JSpinner(new SpinnerNumberModel(
				params.getReplacementThresholdRack(), 0,
				1, 0.01));
		panelReplacementThresholdRack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		mReplacementThresholdRack.addChangeListener(listen);
		panelReplacementThresholdRack.add(mReplacementThresholdRack);
		panel.add(panelReplacementThresholdRack);

		return panel;
	}

	@Override
	public void resaveElements() {
		
		ReplacementModuleConfigParams params = new ReplacementModuleConfigParams(
				mChkBoxReplaceInBlocks.isSelected(),
				mChkBoxPresetServerTypes.isSelected(),
				mChkBoxReplacementFunction.isSelected(),
				((Number) mReplacementThresholdAisle.getValue()).doubleValue(),
				((Number) mReplacementThresholdContainer.getValue()).doubleValue(),
				((Number) mReplacementThresholdRack.getValue()).doubleValue()
				);
		
		Module.REPLACEMENTS_MODULE.setParams(params);
		
		System.out.println("Resaved: " + Module.REPLACEMENTS_MODULE.getFullDescriptionString()); 
	}
}
