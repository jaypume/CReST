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
package builder.map;

import gui.map.DatacentreMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import sim.physical.World;
import builder.map.canvas.ZoomFactorListener;
import builder.map.canvas.ZoomFactorReporter;
import builder.map.canvas.ZoomLevel;
import builder.map.canvas.ZoomLevelListener;
import builder.map.canvas.ZoomLevelReporter;
import builder.util.StatusBar;
import config.EditorConfiguration;
import config.XMLLoader;

public class BuilderStatusBar extends StatusBar implements ZoomLevelListener,
		ZoomFactorListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 90601602593833524L;

	JLabel mZoomLevelLabel;
	JLabel mZoomFactorLabel;
	@SuppressWarnings("unused")
	private DatacentreMap mMap;
	private EditorConfiguration mConfig;

	BuilderStatusBar(ZoomLevelReporter rReporter, ZoomFactorReporter fReporter,
			DatacentreMap pMap, EditorConfiguration pConfig) {
		super();
		// Register
		mMap = pMap;
		mConfig = pConfig;
		rReporter.addZoomLevelListener(this);
		fReporter.addZoomFactorListener(this);
		mZoomLevelLabel = new JLabel();
		mZoomFactorLabel = new JLabel();
		setZoomLevelLabel(rReporter.getZoomLevel());
		setZoomFactorLabel(fReporter.getZoomFactor());
		add(mZoomLevelLabel);
		add(mZoomFactorLabel);
		JButton but = new JButton("Render");
		but.addActionListener(this);

		// TODO: Remove references to this button, it was used to just render
		// the first datacenture but has since been replaced by a more useful
		// interface in the SideBar.

		// add(but);
	}

	private void setZoomLevelLabel(ZoomLevel pZoom) {
		mZoomLevelLabel.setText("Zoom Level: " + pZoom.getName());
	}

	private void setZoomFactorLabel(double pZoom) {
		mZoomFactorLabel.setText("Zoom Factor: " + pZoom);
	}

	@Override
	public void zoomLevelChanged(ZoomLevel pNewZoomLevel) {
		setZoomLevelLabel(pNewZoomLevel);
	}

	@Override
	public void updatedZoomLevel(double pZoomLevel) {
		setZoomFactorLabel(pZoomLevel);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// XMLLoader.loadWorld(confWorld, confMang)
		World w = XMLLoader.loadWorld(mConfig.getConfWorld(), mConfig
				.getConfig(), mConfig.getReplacements());
		w.distributeIPs();
//		mMap.renderPhysicalLayout(w.getDatacentres()[0], new gui.map.RackDisplayRenderer());

	}
}
