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
package builder.map.canvas;


import java.awt.*;

import javax.swing.*;

import builder.prefs.BuilderPreferences;

public class ComponentTile extends DraggablePanel
{

	/**
	 * Auto-Generated Serial ID
	 */
	private static final long serialVersionUID = -3166771260800250196L;

	public ComponentTile(String pName, BuilderPreferences prefs)
	{
		this(pName, prefs.getIconResourcePath() + "server.png");
	}

	ComponentTile(String pName, String pIconPath)
	{
		this.setLayout(new GridLayout(2, 0));

		// Setup Icon
		// This code right here.... horrible...
		JLabel imgIcon = new JLabel(new ImageIcon(new ImageIcon(pIconPath)
				.getImage()));
		imgIcon.setOpaque(false);
		this.add(imgIcon);

		// Setup Text
		JLabel text = new JLabel(pName, JLabel.CENTER);
		text.setOpaque(false);
		this.add(text);

		this.setOpaque(true);
		this.setPreferredSize(new Dimension(100, 84));
	}

}
