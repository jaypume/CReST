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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import config.EditorConfiguration;
import config.XMLLoader;

public class MapView extends JPanel
{

	private static final long serialVersionUID = -7994042921840607418L;
	private DatacentreMap map;
	private EditorConfiguration mConf;
	
	MapView(EditorConfiguration editConf)
	{
		// Set layout
		this.setLayout(new BorderLayout());
		
		// Add sidebar
		this.add(new SideBar(), BorderLayout.WEST);
		
		// Create map
		map = new DatacentreMap();
		
		// Add map
		this.add(map, BorderLayout.CENTER);
		
		// Set world
		mConf = editConf;
		
		this.setVisible(true);
	}
	
	
	private class SideBar extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SideBar()
		{
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			
			JButton button = new JButton("1");
			button.addActionListener(this);
			this.add(button);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			map.renderPhysicalLayout(XMLLoader.buildDatacentre(mConf.getConfWorld().getDCs()[0], -1, mConf.getConfig()), new gui.map.RackDisplayRenderer());
		}
	}
}
