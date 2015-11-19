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

package builder.datacentre;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import config.DatacentreGenerator;
import config.DatacentreGeneratorProgressListener;
import config.physical.ConfigDatacentre;

public class GenProgressDialog extends JDialog implements DatacentreGeneratorProgressListener, ActionListener
{

	private static final long serialVersionUID = -2907954749344052069L;

	private JProgressBar bar;
	private JButton cancelButton;
	private DatacentreGenerator mGen;
	
	public GenProgressDialog(Frame upper, DatacentreGenerator gen)
	{
		// Setup Dialog
		super(upper);
		this.setSize(400, 100);
		this.setTitle("Generator Progress...");
		
		// Setup JPanel
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
		content.setBorder(new EmptyBorder(10,10,10,10));
		
		// Add Jlabel
		content.add(new JLabel("Generating..."));
		content.add(Box.createHorizontalStrut(10));
		
		// Add Progress Bar
		bar = new JProgressBar(0,100);
		content.add(bar);
		content.add(Box.createHorizontalStrut(10));
		
		// Add cancel Button
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		content.add(cancelButton);
		
		// Final Setup
		this.add(content);
		this.setVisible(true);
		mGen = gen;
		mGen.addGeneratorListener(this);
	}
	
	@Override
	public void updated(double progress)
	{
		bar.setValue((int) (progress*100));
	}

	@Override
	public void done(ConfigDatacentre pDatacentre)
	{
		this.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		mGen.stopGen();
	}

	@Override
	public void canceled()
	{
		this.dispose();
	}

}
