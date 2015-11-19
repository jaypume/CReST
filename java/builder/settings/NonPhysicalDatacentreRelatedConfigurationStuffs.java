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
package builder.settings;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import sim.module.Module;
import sim.module.demand.bo.Demand;
import sim.module.demand.configparams.DemandModuleConfigParams;
import builder.prefs.BuilderPreferences;
import config.physical.ConfigDatacentre;

/**
 * This class deals with the pricing criteria that is independent of the physical datacenter and hardware
 *  
 * @author Alex Sheppard
 */
public class NonPhysicalDatacentreRelatedConfigurationStuffs implements FocusListener
{
	private JFrame mFrame;
	private JComboBox cbTypeOfBusiness;
	private JTextField txtSuppliedPower;
	private JTextField txtGridPowerCost;
	private JTextField txtMaintenancePowerCost;
	private JTextField txtMaintenanceCoolingCost;
	private JTextField txtEmployeesPerRack;
	private JTextField txtCostofEmployees;
	private JTextField txtHardwareLifetime;
	private JTextField txtRent;

	private BuilderPreferences mPreferences;
	private ConfigDatacentre mWorkingDC;

	public NonPhysicalDatacentreRelatedConfigurationStuffs(Frame pParent, ConfigDatacentre pWorkingDC, BuilderPreferences pPrefs, String pName)
	{
		ActionListener aL = newAction();

		// Set preferences
		mPreferences = pPrefs;
		this.mWorkingDC = pWorkingDC;
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e)
		{
		} 
		catch (InstantiationException e)
		{
		} 
		catch (IllegalAccessException e)
		{
		} 
		catch (UnsupportedLookAndFeelException e)
		{
		}

		mFrame = new JFrame(mPreferences.getProgramName() + "- " + pName);
		mFrame.setIconImage(mPreferences.getProgramIcon());
		mFrame.isDisplayable();		
		mFrame.setResizable(false);

		Box box = Box.createVerticalBox();

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//Intro text label
		JLabel description = new JLabel("blah blah blah, a description of these options");
		description.setAlignmentX(Component.CENTER_ALIGNMENT);
		description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		box.add(description);


		//Fields
		int textFieldLength = 20;
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);

		GroupLayout.SequentialGroup topBottom = layout.createSequentialGroup();
		GroupLayout.ParallelGroup leftColumn = layout.createParallelGroup();
		
		JLabel lblTypeOfBusiness = new JLabel("Type of Business");
		lblTypeOfBusiness.setVerticalAlignment(JLabel.BOTTOM);
		leftColumn.addComponent(lblTypeOfBusiness);

		JLabel lblSuppliedPower = new JLabel("Supplied power (" + mPreferences.getCurrency() + ")");
		leftColumn.addComponent(lblSuppliedPower);

		JLabel lblGridPowerCost = new JLabel("Grid power cost (" + mPreferences.getCurrency() + ")");
		leftColumn.addComponent(lblGridPowerCost);

		JLabel lblMaintenancePowerCost = new JLabel("Maintenence cost of power system (" + mPreferences.getCurrency() + ")");       
		leftColumn.addComponent(lblMaintenancePowerCost);

		JLabel lblMaintenanceCoolingCost = new JLabel("Maintenence cost of cooling system (" + mPreferences.getCurrency() + ")");
		leftColumn.addComponent(lblMaintenanceCoolingCost);

		JLabel lblEmployeesPerRack = new JLabel("Employees per rack");
		leftColumn.addComponent(lblEmployeesPerRack);

		JLabel lblCostofEmployees = new JLabel("Cost of employees (" + mPreferences.getCurrency() + ")");
		leftColumn.addComponent(lblCostofEmployees);

		JLabel lblHardwareLifetime = new JLabel("Hardware lifetime (months)");
		leftColumn.addComponent(lblHardwareLifetime);
		
		JLabel lblRent = new JLabel("Rent per month per m2 (" + mPreferences.getCurrency() + ")");
		leftColumn.addComponent(lblRent);
		

		GroupLayout.ParallelGroup rightColumn = layout.createParallelGroup();
		cbTypeOfBusiness = new JComboBox();
		rightColumn.addComponent(cbTypeOfBusiness);
		
		txtSuppliedPower = new JTextField(textFieldLength);
		rightColumn.addComponent(txtSuppliedPower);

		txtGridPowerCost = new JTextField(textFieldLength);
		rightColumn.addComponent(txtGridPowerCost);

		txtMaintenancePowerCost = new JTextField(textFieldLength);
		rightColumn.addComponent(txtMaintenancePowerCost);

		txtMaintenanceCoolingCost = new JTextField(textFieldLength);
		rightColumn.addComponent(txtMaintenanceCoolingCost);

		txtEmployeesPerRack = new JTextField(textFieldLength);
		rightColumn.addComponent(txtEmployeesPerRack);

		txtCostofEmployees = new JTextField(textFieldLength);
		rightColumn.addComponent(txtCostofEmployees);

		txtHardwareLifetime = new JTextField(textFieldLength);
		rightColumn.addComponent(txtHardwareLifetime);
		
		txtRent = new JTextField(textFieldLength);
		rightColumn.addComponent(txtRent);


		topBottom.addGroup(leftColumn);
		topBottom.addGroup(rightColumn);


		GroupLayout.SequentialGroup leftToRight = layout.createSequentialGroup();
		GroupLayout.ParallelGroup row0 = layout.createParallelGroup();
		row0.addComponent(lblTypeOfBusiness);
		row0.addComponent(cbTypeOfBusiness);		

		GroupLayout.ParallelGroup topRow = layout.createParallelGroup();
		topRow.addComponent(lblSuppliedPower);
		topRow.addComponent(txtSuppliedPower);

		GroupLayout.ParallelGroup row2 = layout.createParallelGroup();
		row2.addComponent(lblGridPowerCost);
		row2.addComponent(txtGridPowerCost);

		GroupLayout.ParallelGroup row3 = layout.createParallelGroup(); 
		row3.addComponent(lblMaintenancePowerCost);
		row3.addComponent(txtMaintenancePowerCost);

		GroupLayout.ParallelGroup row4 = layout.createParallelGroup();
		row4.addComponent(lblMaintenanceCoolingCost);
		row4.addComponent(txtMaintenanceCoolingCost);

		GroupLayout.ParallelGroup row5 = layout.createParallelGroup();
		row5.addComponent(lblEmployeesPerRack);
		row5.addComponent(txtEmployeesPerRack);

		GroupLayout.ParallelGroup row6 = layout.createParallelGroup();
		row6.addComponent(lblCostofEmployees);
		row6.addComponent(txtCostofEmployees);

		GroupLayout.ParallelGroup row7 = layout.createParallelGroup();
		row7.addComponent(lblHardwareLifetime);
		row7.addComponent(txtHardwareLifetime);
		
		GroupLayout.ParallelGroup row8 = layout.createParallelGroup();
		row8.addComponent(lblRent);
		row8.addComponent(txtRent);


		leftToRight.addGroup(row0);
		leftToRight.addGroup(topRow);
		leftToRight.addGroup(row2);
		leftToRight.addGroup(row3);
		leftToRight.addGroup(row4);
		leftToRight.addGroup(row5);
		leftToRight.addGroup(row6);
		leftToRight.addGroup(row7);
		leftToRight.addGroup(row8);


		layout.setHorizontalGroup(topBottom);
		layout.setVerticalGroup(leftToRight);

		box.add(panel);


		//Submit button
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setMargin(new Insets(7, 10, 7, 10));
		btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSubmit.addActionListener(aL);
		box.add(btnSubmit);

		//Listeners
		txtGridPowerCost.addFocusListener(this);

		btnSubmit.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					System.out.println("boom done");
				}
			}
		});

		mFrame.add(box);
		mFrame.pack();
		mFrame.setVisible(true);
		mFrame.setLocationRelativeTo(pParent);

		load();
	}

	private ActionListener newAction()
	{
		ActionListener AL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		};
		return AL;
	}

	/**Loads values from config or if not found, the defaults
	 * 
	 * @author Alex Sheppard
	 */
	private void load()
	{
		Demand d = new Demand(((DemandModuleConfigParams) Module.DEMAND_MODULE.getParams()).getFilename());
		for(int i = 0; i < d.csvHeader.size(); i++)
		{
			cbTypeOfBusiness.addItem(d.csvHeader.get(i));
		}		
		
		txtSuppliedPower.setText(Double.toString(mWorkingDC.getSuppliedPower()));
		txtGridPowerCost.setText(Double.toString(mWorkingDC.getGridPowerCost()));
		txtMaintenancePowerCost.setText(Double.toString(mWorkingDC.getMaintenancePowerCost()));
		txtMaintenanceCoolingCost.setText(Double.toString(mWorkingDC.getMaintenanceCoolingCost()));
		txtEmployeesPerRack.setText(Double.toString(mWorkingDC.getEmployeesPerRack()));
		txtCostofEmployees.setText(Double.toString(mWorkingDC.getCostofEmployees()));
		txtHardwareLifetime.setText(Integer.toString(mWorkingDC.getHardwareLifetime()));
		txtRent.setText(Double.toString(mWorkingDC.getRent()));
	}

	/**Save the values to the config file
	 * 
	 * @author Alex Sheppard
	 */
	private void save()
	{
		try
		{	
			mWorkingDC.setTypeOfBusiness(cbTypeOfBusiness.getSelectedItem().toString());
			mWorkingDC.setSuppliedPower(Double.parseDouble(txtSuppliedPower.getText()));
			mWorkingDC.setGridPowerCost(Double.parseDouble(txtGridPowerCost.getText()));
			mWorkingDC.setMaintenancePowerCost(Double.parseDouble(txtMaintenancePowerCost.getText()));
			mWorkingDC.setMaintenanceCoolingCost(Double.parseDouble(txtMaintenanceCoolingCost.getText()));
			mWorkingDC.setEmployeesPerRack(Double.parseDouble(txtEmployeesPerRack.getText()));
			mWorkingDC.setCostofEmployees(Double.parseDouble(txtCostofEmployees.getText()));
			mWorkingDC.setHardwareLifetime(Integer.parseInt(txtHardwareLifetime.getText()));
			mWorkingDC.setRent(Double.parseDouble(txtRent.getText()));
			
			mFrame.dispose();
		}

		catch(NumberFormatException e)
		{
			System.err.println("Invalid entry " + e.getMessage());
		}
	}

	@Override
	public void focusGained(FocusEvent e)
	{
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		try
		{
			txtMaintenancePowerCost.setText(Double.toString(Double.parseDouble(txtGridPowerCost.getText())));
			txtMaintenanceCoolingCost.setText(Double.toString(Double.parseDouble(txtGridPowerCost.getText())/2));
		}
		catch(NumberFormatException j)
		{
			System.err.println("Invalid entry " + j.getMessage());
		}		
	}
}
