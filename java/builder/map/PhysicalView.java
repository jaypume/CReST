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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import builder.map.canvas.Canvas;
import builder.prefs.BuilderPreferences;
import config.EditorConfiguration;

public class PhysicalView extends JPanel {
	
	private static final long serialVersionUID = 2154044096128484206L;

	private BuilderPreferences mPreferences;
	
	private JPanel mZoomBar;
	private BuilderStatusBar mStatusBar;
	private JPanel mSidePanel;
	private DatacentreMap mMap;
	private EditorConfiguration mConfig;
	
	public PhysicalView(BuilderPreferences prefs, EditorConfiguration pConfig)
	{
		// Set preferances object
		mPreferences = prefs;
		mConfig = pConfig;
		
		// Setup Panel layout
		this.setLayout(new BorderLayout());
		
		// Create canvas
		mMap = new DatacentreMap();
		Canvas datacenterView = new Canvas(800, 600, mMap);
		this.add(datacenterView, BorderLayout.CENTER);
		
		// Setup Canvas Drag and Drop.
		//CanvasTransferHandler cth = new CanvasTransferHandler();
		//new DropTarget(datacenterView, DnDConstants.ACTION_MOVE, cth);
		
		// Setup toolbars
		mSidePanel = new SidePanel(mPreferences, mConfig, mMap);
		mZoomBar = new ZoomBar(datacenterView);
		this.add(mSidePanel, BorderLayout.EAST);
		this.add(mZoomBar, BorderLayout.WEST);
		
		// Status Bar
		mStatusBar = new BuilderStatusBar(datacenterView, datacenterView, mMap, mConfig);
		this.add(mStatusBar, BorderLayout.SOUTH);
	}
	
}
