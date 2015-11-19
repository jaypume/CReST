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
package gui.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import sim.event.StartSimEvent;
import sim.module.Module;
import sim.module.costs.CostsModuleRunner;
import sim.module.costs.bo.Costs;
import sim.module.costs.bo.Currency;
import sim.module.log.event.LogEvent;
import sim.physical.World;
import utility.time.LengthOfTime;
import utility.time.TimeManager;

/**
 * Creates a pricing panel for a given datacentre and time period
 * @author Alex Sheppard
 */
public class CostPanel extends JPanel implements Observer
{
	public static final String TAB_STRING = "Costs Panel";
	
	public static Logger logger = Logger.getLogger(CostPanel.class);
	private static final long serialVersionUID = 1L;
	private TitledBorder mHeader;
	private JLabel mPhysicalSpace;
	private JLabel mHardwarePowerCost;
	private JLabel mCoolingPowerCost;
	private JLabel mPersonnelCost;
	private JLabel mHardwareDepreciationCost;
	private JLabel mLicenseCost;
	private JLabel mTotalCost;

	private int dayCounter; //count the days to know when to update on new events
	
	public CostPanel() {
		
		resetDayCounter();
	}
	
	public CostPanel(int i)
	{
		resetDayCounter();
		
		mHeader = BorderFactory.createTitledBorder("");
		this.setBorder(mHeader);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;

		Font font = new Font("SansSerif", Font.BOLD, 16);

		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 1;
		c.ipady = 0;
		JLabel lblphysicalSpace = new JLabel("Physical Space Cost");
		c.gridy = 1;
		this.add(lblphysicalSpace, c);

		JLabel lblhardwarePowerCost = new JLabel("Hardware Power Cost");
		c.gridy = 2;
		this.add(lblhardwarePowerCost, c);

		JLabel lblcoolingPowerCost = new JLabel("Cooling Power Cost");
		c.gridy = 3;
		this.add(lblcoolingPowerCost, c);

		JLabel lblpersonnelCost = new JLabel("Personnel Cost");
		c.gridy = 4;
		this.add(lblpersonnelCost, c);

		JLabel lblhardwareDepreciationCost = new JLabel("Hardware Depreciation Cost");
		c.gridy = 5;
		this.add(lblhardwareDepreciationCost, c);

		JLabel lbllicenseCost = new JLabel("License Cost");
		c.gridy = 6;
		this.add(lbllicenseCost, c);

		JLabel lbltotalCost = new JLabel("TOTAL Cost");
		font = new Font("SansSerif", Font.BOLD, 12);
		lbltotalCost.setFont(font);
		c.gridy = 7;
		c.insets = new Insets(20,0,0,0);
		this.add(lbltotalCost, c);


		c.gridx = 1;
		c.insets = new Insets(0,0,0,0);

		mPhysicalSpace = new JLabel();
		c.gridy = 1;
		this.add(mPhysicalSpace, c);

		mHardwarePowerCost = new JLabel();
		c.gridy = 2;
		this.add(mHardwarePowerCost, c);

		mCoolingPowerCost = new JLabel();
		c.gridy = 3;
		this.add(mCoolingPowerCost, c);

		mPersonnelCost = new JLabel();
		c.gridy = 4;
		this.add(mPersonnelCost, c);

		mHardwareDepreciationCost = new JLabel();
		c.gridy = 5;
		this.add(mHardwareDepreciationCost, c);

		mLicenseCost = new JLabel();
		c.gridy = 6;
		this.add(mLicenseCost, c);

		mTotalCost = new JLabel();
		mTotalCost.setFont(font);
		c.gridy = 7;
		//c.ipady = 20;
		c.insets = new Insets(20,0,0,0);
		//c.weighty = 1.0; 
		this.add(mTotalCost, c);
	}
			
	/**
	 * Update the costs in the dialog as the simulator runs
	 * 
	 * @param p The Pricing instance
	 * @param i datacenter number
	 * @param lengthOfTime time period (DAY, MONTH, etc)
	 */
	public void updates(Costs p, int i, LengthOfTime lengthOfTime)
	{		
		mHeader.setTitle("Datacentre " + i + ", per " + lengthOfTime.name());

		NumberFormat nf = Currency.getNumberFormat();
		
		mPhysicalSpace.setText(nf.format(p.PhysicalSpaceCost(lengthOfTime.getTimeInSeconds())));
		mHardwarePowerCost.setText(nf.format(p.HardwarePowerCost(lengthOfTime.getTimeInSeconds())));
		mCoolingPowerCost.setText(nf.format(p.CoolingPowerCost(lengthOfTime.getTimeInSeconds())));
		mPersonnelCost.setText(nf.format(p.PersonnelCost(lengthOfTime.getTimeInSeconds())));
		mHardwareDepreciationCost.setText(nf.format(p.HardwareDepreciationCost(lengthOfTime.getTimeInSeconds())));
		mLicenseCost.setText(nf.format(p.getSoftwareLicenseCost()));
		mTotalCost.setText(nf.format(p.totalCost(lengthOfTime.getTimeInSeconds())));
	}

	private void resetDayCounter() {
		dayCounter = -1; //make it negative so first day events are registered
	}
	
	//TODO: JC Dec 2011, This code needs cleaning up
	/**
	 * Update the Costs Panel 
	 * 
	 * Costs panel updates if object is a LogEvent
	 * and panel has not been updated during this day
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		//only update if costs are on
		if(!Module.COSTS_MODULE.isActive()) {
			removeAll();
			this.add(new JLabel("This component is switched off."));
			return;
		}
		
		//Have we started the simulation?  
		if(arg instanceof StartSimEvent) {
			logger.debug("Started the simulation, resetting day counter...");
			resetDayCounter(); //reset the day counter
		} else if (arg instanceof LogEvent) { //Is it a log event?
			//Have we updated today?
			if(dayCounter<TimeManager.simulationTimeToDays(World.getInstance().getTime())) {
				dayCounter = (int) TimeManager.simulationTimeToDays(World.getInstance().getTime());
				logger.debug(TimeManager.log("Day " + dayCounter + ", updating costs panel..."));
	            removeAll();
	            CostPanel cPanel;
	            for (int i = 0; i < World.getInstance().getNumberOfDatacentres(); i++)
	            {
	            	//add new internal panels for each datacentre and each time period
	            	cPanel = new CostPanel(i);
	            	this.add(cPanel);
	            	
	            	//get costs for the datacentre
	            	Costs costs = CostsModuleRunner.getInstance().getCosts(i);
	            	
	            	cPanel.updates(costs, i, LengthOfTime.HOUR);
	            	cPanel = new CostPanel(i);
	            	this.add(cPanel);
	            	cPanel.updates(costs, i, LengthOfTime.DAY);
	            	cPanel = new CostPanel(i);
	            	this.add(cPanel);
	            	cPanel.updates(costs, i, LengthOfTime.MONTH);
	            	cPanel = new CostPanel(i);
	            	this.add(cPanel);
	            	cPanel.updates(costs, i, LengthOfTime.YEAR);
	            }
			}
		} //else ignore
	}
}
