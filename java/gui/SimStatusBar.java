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
package gui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import sim.event.StartSimEvent;
import sim.event.StopSimEvent;
import sim.physical.World;
import utility.time.TimeManager;
import builder.util.StatusBar;

public class SimStatusBar extends StatusBar implements Observer {

	public static Logger logger = Logger.getLogger(SimStatusBar.class);
	
	private static final long serialVersionUID = 7311722140805445968L;

	private final JLabel configFileLabel; 
	private final JLabel simulationTimeLabel;
	private long currentTime = 0;
	
	/**
	 * Sets up the Panel
	 */
	public SimStatusBar(String filename)
	{
		this.setLayout(new BorderLayout());
		
		configFileLabel = new JLabel();
		filenameUpdated(filename);
		this.add(configFileLabel, BorderLayout.WEST);
		
		simulationTimeLabel = new JLabel();
		simStateUpdated(false);
		this.add(simulationTimeLabel, BorderLayout.EAST);
	}
	
	public void simStateUpdated(boolean running)
	{
		String runText;
		if(running)
		{
			runText = "initialising...  ";
		} else
		{
			runText = "not running  ";
		}
		simulationTimeLabel.setText("Simulation " + runText);
	}
	
    public void filenameUpdated(String filename)
	{
		configFileLabel.setText("Configuration File: " + filename);
	}
	
    /**
     * Update the simulation time string
     * @param simulationTime
     */
    public void simTimeUpdated(String simulationTime) {
    	simulationTimeLabel.setText(simulationTime + "  ");
    }

	//This overrides the Observer.update method.  And is called when
	//observable class changes (in this case the eventQueue)
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {

		if(arg.getClass().getName().equals(StopSimEvent.class.getName())) { //stop sim?
//			simStateUpdated(false);
			simulationTimeLabel.setText("Simulation ended   ");
		} else if (arg.getClass().getName().equals(StartSimEvent.class.getName())) { //start sim?
			simStateUpdated(true);
		} else if(currentTime!=World.getInstance().getTime()) {
			//Update current time if necessary, otherwise ignore the event
			currentTime = World.getInstance().getTime();
			simTimeUpdated(TimeManager.getTimeString());
		} else {
			//do nothing - the event has the same time as previous
		}	
	}
}
