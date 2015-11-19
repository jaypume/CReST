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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import sim.module.failure.configparams.FailureModuleConfigParams;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class FailuresPanel extends AbstractSimPanel 
{
	protected JSpinner mMeanHardFixTime;
	protected JSpinner mStdDevHardFixTime;
	protected JSpinner mMeanSoftFixTime;
	protected JSpinner mStdDevSoftFixTime;
	
	public FailuresPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf, prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen,
			ActionListener fileChooserListener) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Failure Settings"),
				new EmptyBorder(10, 10, 10, 10)));
		
		//Get broker configuration parameters
		FailureModuleConfigParams params = (FailureModuleConfigParams) Module.FAILURE_MODULE.getParams();

		// Mean Hard Fix Time
		JPanel panMeanHardFixTime = new JPanel();
		panMeanHardFixTime.setLayout(new BoxLayout(panMeanHardFixTime, BoxLayout.LINE_AXIS));
		panMeanHardFixTime.add(new JLabel("Mean Hard Fix Time (days)"));
		panMeanHardFixTime.add(Box.createHorizontalStrut(10));
		mMeanHardFixTime = new JSpinner(new SpinnerNumberModel(
				params.getMeanHardFixTime(), 0, Long.MAX_VALUE, 0.5));
		mMeanHardFixTime.addChangeListener(listen);
		panMeanHardFixTime.add(mMeanHardFixTime);
		panMeanHardFixTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panMeanHardFixTime);
		panel.add(Box.createVerticalStrut(10));
		
		// StdDev Hard Fix Time
		JPanel panStdDevHardFixTime = new JPanel();
		panStdDevHardFixTime.setLayout(new BoxLayout(panStdDevHardFixTime, BoxLayout.LINE_AXIS));
		panStdDevHardFixTime.add(new JLabel("Std Dev Hard Fix Time (days)"));
		panStdDevHardFixTime.add(Box.createHorizontalStrut(10));
		mStdDevHardFixTime = new JSpinner(new SpinnerNumberModel(
				params.getStdDevHardFixTime(), 0, Long.MAX_VALUE, 0.25));
		mStdDevHardFixTime.addChangeListener(listen);
		panStdDevHardFixTime.add(mStdDevHardFixTime);
		panStdDevHardFixTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panStdDevHardFixTime);
		panel.add(Box.createVerticalStrut(10));
		
		// Mean Soft Fix Time
		JPanel panMeanSoftFixTime = new JPanel();
		panMeanSoftFixTime.setLayout(new BoxLayout(panMeanSoftFixTime, BoxLayout.LINE_AXIS));
		panMeanSoftFixTime.add(new JLabel("Mean Soft Fix Time (minutes)"));
		panMeanSoftFixTime.add(Box.createHorizontalStrut(10));
		mMeanSoftFixTime = new JSpinner(new SpinnerNumberModel(
				params.getMeanSoftFixTime(), 0, Long.MAX_VALUE, 1));
		mMeanSoftFixTime.addChangeListener(listen);
		panMeanSoftFixTime.add(mMeanSoftFixTime);
		panMeanSoftFixTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panMeanSoftFixTime);
		panel.add(Box.createVerticalStrut(10));
		
		// StdDev Hard Fix Time
		JPanel panStdDevSoftFixTime = new JPanel();
		panStdDevSoftFixTime.setLayout(new BoxLayout(panStdDevSoftFixTime, BoxLayout.LINE_AXIS));
		panStdDevSoftFixTime.add(new JLabel("Std Dev Soft Fix Time (seconds)"));
		panStdDevSoftFixTime.add(Box.createHorizontalStrut(10));
		mStdDevSoftFixTime = new JSpinner(new SpinnerNumberModel(
				params.getStdDevSoftFixTime(), 0, Long.MAX_VALUE, 1));
		mStdDevSoftFixTime.addChangeListener(listen);
		panStdDevSoftFixTime.add(mStdDevSoftFixTime);
		panStdDevSoftFixTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel.add(panStdDevSoftFixTime);

		return panel;
	}

	@Override
	public void resaveElements() 
	{				
		FailureModuleConfigParams params = new FailureModuleConfigParams(
				((Number) mMeanHardFixTime.getValue()).doubleValue(),
				((Number) mStdDevHardFixTime.getValue()).doubleValue(),
				((Number) mMeanSoftFixTime.getValue()).doubleValue(),
				((Number) mStdDevSoftFixTime.getValue()).doubleValue()
				);
		
		Module.FAILURE_MODULE.setParams(params);
				
		System.out.println("Resaved: " + Module.FAILURE_MODULE.getFullDescriptionString()); 
	}
}
