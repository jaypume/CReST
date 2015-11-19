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
import sim.module.broker.configparams.BrokerModuleConfigParams;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class BrokerPanel extends AbstractSimPanel
{
	protected JSpinner numAgents;
	protected JSpinner numEachUser;
	protected JSpinner maxReq;
	protected JSpinner variance;
	protected JSpinner learningPeriod;
	protected JSpinner k;
	protected JSpinner costFactor;
	protected JSpinner mruThreshold;
	protected JCheckBox adapt;
	protected JSpinner adaptMomentum;
	protected JSpinner adaptAlpha;
	protected JSpinner demandProfile;
	protected JCheckBox marketShock;
	protected JSpinner marketShockMonth;
	protected JSpinner marketShockProfile;
	
	public BrokerPanel (SettingsManager conf, BuilderPreferences prefs)
	{
		super(conf,prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen, ActionListener fileChooserListener)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Broker Settings"),
				new EmptyBorder(10, 10, 10, 10)));
		
		//Get broker configuration parameters
		BrokerModuleConfigParams params = (BrokerModuleConfigParams) Module.BROKER_MODULE.getParams();
		
		//numAgents subscriptions
		JPanel panelNumAgents = new JPanel();
		panelNumAgents.setLayout(new BoxLayout(panelNumAgents, BoxLayout.LINE_AXIS));
		panelNumAgents.add(new JLabel("Number of Agents"));
		panelNumAgents.add(Box.createHorizontalStrut(10));
		numAgents = new JSpinner(new SpinnerNumberModel(
				params.getNumAgents(), 0,
				Integer.MAX_VALUE, 1));
		numAgents.addChangeListener(listen);
		panelNumAgents.add(numAgents);
		panel.add(panelNumAgents);
		panel.add(Box.createVerticalStrut(10));
				
		//numEachUser Value
		JPanel panelNumEachUser = new JPanel();
		panelNumEachUser.setLayout(new BoxLayout(panelNumEachUser, BoxLayout.LINE_AXIS));
		panelNumEachUser.add(new JLabel("Number of Each User"));
		panelNumEachUser.add(Box.createHorizontalStrut(10));
		numEachUser = new JSpinner(new SpinnerNumberModel(
				params.getNumEachUser(), 0,
				Integer.MAX_VALUE, 1));
		numEachUser.addChangeListener(listen);
		panelNumEachUser.add(numEachUser);
		panel.add(panelNumEachUser);
		panel.add(Box.createVerticalStrut(10));	

		//variance Value
		JPanel panelVariance = new JPanel();
		panelVariance.setLayout(new BoxLayout(panelVariance, BoxLayout.LINE_AXIS));
		panelVariance.add(new JLabel("Variance Randomness from Demand Profile"));
		panelVariance.add(Box.createHorizontalStrut(10));
		variance = new JSpinner(new SpinnerNumberModel(
				params.getVariance(), 0,
				Integer.MAX_VALUE, 0.1));
		variance.addChangeListener(listen);
		panelVariance.add(variance);
		panel.add(panelVariance);
		panel.add(Box.createVerticalStrut(10));	
		
		//learningPeriod Value
		JPanel panelLearningPeriod = new JPanel();
		panelLearningPeriod.setLayout(new BoxLayout(panelLearningPeriod, BoxLayout.LINE_AXIS));
		panelLearningPeriod.add(new JLabel("Learning Period"));
		panelLearningPeriod.add(Box.createHorizontalStrut(10));
		learningPeriod = new JSpinner(new SpinnerNumberModel(
				params.getLearningPeriod(), 0,
				Integer.MAX_VALUE, 1));
		learningPeriod.addChangeListener(listen);
		panelLearningPeriod.add(learningPeriod);
		panel.add(panelLearningPeriod);
		panel.add(Box.createVerticalStrut(10));	
		
		//k Value
		JPanel panelK = new JPanel();
		panelK.setLayout(new BoxLayout(panelK, BoxLayout.LINE_AXIS));
		panelK.add(new JLabel("k"));
		panelK.add(Box.createHorizontalStrut(10));
		k = new JSpinner(new SpinnerNumberModel(
				params.getK(), 0,
				Integer.MAX_VALUE, 0.1));
		k.addChangeListener(listen);
		panelK.add(k);
		panel.add(panelK);
		panel.add(Box.createVerticalStrut(10));	
		
		//cost factor Value
		JPanel panelCostFactor = new JPanel();
		panelCostFactor.setLayout(new BoxLayout(panelCostFactor, BoxLayout.LINE_AXIS));
		panelCostFactor.add(new JLabel("Cost Factor"));
		panelCostFactor.add(Box.createHorizontalStrut(10));
		costFactor = new JSpinner(new SpinnerNumberModel(
				params.getCostFactor(), 0,
				Integer.MAX_VALUE, 1.0));
		costFactor.addChangeListener(listen);
		panelCostFactor.add(costFactor);
		panel.add(panelCostFactor);
		panel.add(Box.createVerticalStrut(10));	

		//mru threshold Value
		JPanel panelMRU = new JPanel();
		panelMRU.setLayout(new BoxLayout(panelMRU, BoxLayout.LINE_AXIS));
		panelMRU.add(new JLabel("MRU Threshold"));
		panelMRU.add(Box.createHorizontalStrut(10));
		mruThreshold = new JSpinner(new SpinnerNumberModel(
				params.getMRU(), 0,
				1, 0.01));
		mruThreshold.addChangeListener(listen);
		panelMRU.add(mruThreshold);
		panel.add(panelMRU);
		panel.add(Box.createVerticalStrut(10));
		
		// adapt
		JPanel panelAdapt = new JPanel();
		panelAdapt.setLayout(new BoxLayout(panelAdapt, BoxLayout.LINE_AXIS));
		panelAdapt.add(new JLabel("Adapt Threshold"));
		panelAdapt.add(Box.createHorizontalStrut(10));
		adapt = new JCheckBox();
		adapt.setSelected(params.getAdapt());
		adapt.addActionListener(listen);
		panelAdapt.add(adapt);
		panel.add(panelAdapt);
		panel.add(Box.createHorizontalStrut(10));
		
		// Adapt Momentum Value
		JPanel panelAdaptMomentum = new JPanel();
		panelAdaptMomentum.setLayout(new BoxLayout(panelAdaptMomentum, BoxLayout.LINE_AXIS));
		panelAdaptMomentum.add(new JLabel("Adapt Momentum Parameter"));
		panelAdaptMomentum.add(Box.createHorizontalStrut(10));
		adaptMomentum = new JSpinner(new SpinnerNumberModel(params.getAdaptMomentum(), 0, 1, 0.01));
		adaptMomentum.addChangeListener(listen);
		panelAdaptMomentum.add(adaptMomentum);
		panel.add(panelAdaptMomentum);
		panel.add(Box.createVerticalStrut(10));
		
		// Adapt Momentum Value
		JPanel panelAdaptAlpha = new JPanel();
		panelAdaptAlpha.setLayout(new BoxLayout(panelAdaptAlpha, BoxLayout.LINE_AXIS));
		panelAdaptAlpha.add(new JLabel("Adapt Alpha Parameter"));
		panelAdaptAlpha.add(Box.createHorizontalStrut(10));
		adaptAlpha = new JSpinner(new SpinnerNumberModel(params.getAdaptAlpha(), 0, 1, 0.01));
		adaptAlpha.addChangeListener(listen);
		panelAdaptAlpha.add(adaptAlpha);
		panel.add(panelAdaptAlpha);
		panel.add(Box.createVerticalStrut(10));
		
		//demand Profile Value
		JPanel panelDemandProfile = new JPanel();
		panelDemandProfile.setLayout(new BoxLayout(panelDemandProfile, BoxLayout.LINE_AXIS));
		panelDemandProfile.add(new JLabel("Demand Profile"));
		panelDemandProfile.add(Box.createHorizontalStrut(10));
		demandProfile = new JSpinner(new SpinnerNumberModel(
				params.getDemandProfile(), 0,
				50, 1));
		demandProfile.addChangeListener(listen);
		panelDemandProfile.add(demandProfile);
		panel.add(panelDemandProfile);
		panel.add(Box.createVerticalStrut(10));
		
		// market shock?
		JPanel marketShockPanel = new JPanel();
		marketShockPanel.setLayout(new BoxLayout(marketShockPanel, BoxLayout.LINE_AXIS));
		marketShockPanel.add(new JLabel("Market Shock"));
		marketShockPanel.add(Box.createHorizontalStrut(10));
		marketShock = new JCheckBox();
		marketShock.setSelected(params.getMarketShock());
		marketShock.addActionListener(listen);
		marketShockPanel.add(marketShock);
		panel.add(marketShockPanel);
		panel.add(Box.createHorizontalStrut(10));

		// Market Shock Month
		JPanel marketShockMonthPanel = new JPanel();
		marketShockMonthPanel.setLayout(new BoxLayout(marketShockMonthPanel, BoxLayout.LINE_AXIS));
		marketShockMonthPanel.add(new JLabel("Market Shock Month"));
		marketShockMonthPanel.add(Box.createHorizontalStrut(10));
		marketShockMonth = new JSpinner(new SpinnerNumberModel(params.getMarketShockMonth(), 0, 276, 1));
		marketShockMonth.addChangeListener(listen);
		marketShockMonthPanel.add(marketShockMonth);
		panel.add(marketShockMonthPanel);
		panel.add(Box.createVerticalStrut(10));
		
		// Market Shock Profile
		JPanel marketShockProfilePanel = new JPanel();
		marketShockProfilePanel.setLayout(new BoxLayout(marketShockProfilePanel, BoxLayout.LINE_AXIS));
		marketShockProfilePanel.add(new JLabel("Market Shock Profile"));
		marketShockProfilePanel.add(Box.createHorizontalStrut(10));
		marketShockProfile = new JSpinner(new SpinnerNumberModel(params.getMarketShockProfile(), 0, 50, 1));
		marketShockProfile.addChangeListener(listen);
		marketShockProfilePanel.add(marketShockProfile);
		panel.add(marketShockProfilePanel);
		
		return panel;
	}

	@Override
	public void resaveElements()
	{
		BrokerModuleConfigParams params = new BrokerModuleConfigParams(
				((Number) numAgents.getValue()).intValue(),
				((Number) numEachUser.getValue()).intValue(),
				((Number) variance.getValue()).doubleValue(),
				((Number) learningPeriod.getValue()).intValue(),
				((Number) k.getValue()).doubleValue(),
				((Number) costFactor.getValue()).doubleValue(),
				((Number) mruThreshold.getValue()).doubleValue(),
				adapt.isSelected(),
				((Number) adaptMomentum.getValue()).doubleValue(),
				((Number) adaptAlpha.getValue()).doubleValue(),
				((Number) demandProfile.getValue()).intValue(),
				marketShock.isSelected(),
				((Number) marketShockMonth.getValue()).intValue(),
				((Number) marketShockProfile.getValue()).intValue()
		);
		
		Module.BROKER_MODULE.setParams(params);
				
		System.out.println("Resaved: " + Module.BROKER_MODULE.getFullDescriptionString()); 
	}
}