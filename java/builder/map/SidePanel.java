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

import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import sim.physical.World;
import builder.datacentre.DatacentreListChangedListener;
import builder.map.canvas.CanvasTransferHandler;
import builder.map.canvas.ComponentTile;
import builder.prefs.BuilderPreferences;
import config.EditorConfiguration;
import config.XMLLoader;
import config.physical.ConfigDatacentre;

public class SidePanel extends JPanel implements ActionListener, DatacentreListChangedListener
{

	private BuilderPreferences mPreferances;
	private EditorConfiguration mConfig;
	private DatacentreMap mMap;
	
	private JComboBox mComboDC;
	private JToggleButton mReplacementsButton;
	private JButton mRenderButton;
	
	private static final long serialVersionUID = 1L;

	SidePanel(BuilderPreferences prefs, EditorConfiguration pConfig, DatacentreMap pMap)
	{
		mPreferances = prefs;
		mConfig = pConfig;
		mMap = pMap;
		//JTabbedPane tabs = new JTabbedPane();
		//tabs.addTab("Standard Components", getStandardComponentsTab());
		//this.add(tabs);
		
		
		
		// Setup Panel
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		//this.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Settings"), new EmptyBorder(10, 10, 10, 10)));
		//this.setAlignmentX(CENTER_ALIGNMENT);
		
		// Setup dropdown and button
		//this.add(Box.createVerticalStrut(10));
		
		mComboDC = new JComboBox(mConfig.getConfWorld().getDatacentreNames());
		mComboDC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		mComboDC.setEditable(false);
		this.add(mComboDC);
		this.add(Box.createVerticalStrut(5));
		
		mRenderButton = new JButton("Render");
		mRenderButton.addActionListener(this);
		this.add(mRenderButton);
		this.add(Box.createVerticalStrut(5));

		mReplacementsButton = new JToggleButton("Start Replacements");
		//this.add(mReplacementsButton);
		mReplacementsButton.addActionListener(this);
		

		
		
		this.add(Box.createVerticalGlue());
		
		// add self as a listener
		mConfig.getConfWorld().addDCListListener(this);
	}

	private void populateDCList()
	{
		mComboDC.setModel(new DefaultComboBoxModel(mConfig.getConfWorld().getDatacentreNames()));
	}
	
	protected JComponent getStandardComponentsTab()
	{

		// Internal Panel we use
		JPanel stdPanel = new JPanel();

		// Load in tiles
		ComponentTile[] tiles = getStandardTiles();

		// Canvas Drag and Drop code
		CanvasTransferHandler cth = new CanvasTransferHandler();
		DragSource ds = new DragSource();

		// Populate
		for (ComponentTile c : tiles)
		{
			stdPanel.add(c);
			ds.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE,
					cth);
		}

		// Scroll pane to contain the elements
		JScrollPane scrollPane = new JScrollPane(stdPanel);
		return scrollPane;
	}

	/**
	 * Returns a list of tiles that Correspond to the standard server/equipment
	 * setups available
	 * 
	 * @return
	 */
	private ComponentTile[] getStandardTiles()
	{
		// TODO make this load this infomation from a file

		ComponentTile[] stdComps =
		{ new ComponentTile("Uber server", mPreferances), new ComponentTile("Cheap Server", mPreferances) };
		return stdComps;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(mRenderButton))
		{
			World w = XMLLoader.loadWorld(mConfig.getConfWorld(), mConfig.getConfig(), mConfig.getReplacements());
			w.distributeIPs();
			mMap.renderPhysicalLayout(w.getDatacentres()[mComboDC.getSelectedIndex()], new gui.map.RackDisplayRenderer());
		} else if (source.equals(mReplacementsButton))
		{
			// Run replacements?
		}
		
		
		
	}
	
	@Override
	public void listUpdated(ConfigDatacentre[] dc)
	{
		System.out.println("Got Event");
		populateDCList();
		
	}
}
