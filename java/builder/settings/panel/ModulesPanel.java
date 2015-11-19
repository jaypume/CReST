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
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sim.module.Module;
import builder.prefs.BuilderPreferences;
import builder.settings.SimSettingsView.SaveHandler;
import config.SettingsManager;

public class ModulesPanel extends AbstractSimPanel {
	
	protected JCheckBox[] boxes;
	protected Module[] mods;
	
	public ModulesPanel(SettingsManager conf, BuilderPreferences prefs) {
		super(conf, prefs);
	}

	@Override
	public JPanel getPanel(SaveHandler listen,
			ActionListener fileChooserListener) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Module.getGUILabel()),
				new EmptyBorder(10, 10, 10, 10)));

		int structHeight = 10;
				
		mods = Module.getGUIEditableModules();
		boxes = new JCheckBox[mods.length];
		int counter = 0;
		for(Module m: mods) {
			System.out.println(m);
			
			//setup checkboxes
			boxes[counter] = new JCheckBox(mods[counter].toString());
			boxes[counter].addActionListener(listen);
			boxes[counter].setSelected(m.isActive());
			panel.add(boxes[counter]);
			panel.add(Box.createVerticalStrut(structHeight));
			counter++;
		}
		
		return panel;
	}

	@Override
	public void resaveElements() {

		//the new way
		for(int i=0; i<mods.length; i++) {
			mods[i].setActive(boxes[i].isSelected());
			System.out.println(mods[i].getFullDescriptionString());
		}
	}

}
