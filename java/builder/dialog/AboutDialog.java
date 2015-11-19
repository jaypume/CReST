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

package builder.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

import builder.prefs.BuilderPreferences;

public class AboutDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String homepageURL = "http://http://www.cs.bris.ac.uk/~cszjpc/crest.html";

	public AboutDialog(JFrame parent, BuilderPreferences prefs)
	{		
		super(parent, "About " + prefs.getProgramName(), true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		ActionListener aL = newAction();
		
		// Set size
		this.setResizable(false);
		this.setSize(250, 400);	
		this.setLocationRelativeTo(parent);
		
		// Set Gradient Panel
		JPanel gPanel = new GradientBackgroundPanel();
		
		// Set Layout
		gPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		/*GridLayout layout = new GridLayout(3, 0);
		gPanel.setLayout(layout);*/
		
		// Icon
		JLabel logo = new JLabel(new ImageIcon(prefs.getProgramIcon().getScaledInstance(200, 200, 0)));
		//logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		logo.setOpaque(false);
		
		//Changed the layout to a GridBagLayout so that it can display buttons correctly- Alex
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		gPanel.add(logo, c);
		
		// Content Pane
		JLabel contentPane = new JLabel();
		contentPane.setText("<HTML>" + prefs.getProgramName() + ", created by the LSCITS Initiative at Bristol University. Icons from FatCow at http://www.fatcow.com/free-icons</HTML>");
		//contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentPane.setOpaque(false);
				
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;
		c.gridx = 0;
		c.gridy = 1;
		gPanel.add(contentPane, c);

			
		/*JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(Color.RED);*/

		//JButton closeButton = new JButton("Close");
		JButton websiteButton = new JButton(prefs.getProgramName() + " Homepage");
		websiteButton.addActionListener(aL);
		//buttonPanel.add(closeButton);
		//buttonPanel.add(websiteButton);
				
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 2;
		gPanel.add(websiteButton, c);

		// Display onscreen
		/*gPanel.add(logo);
		//gPanel.add(contentPane);
		//gPanel.add(buttonPanel);*/
	
		this.add(gPanel);
		this.setVisible(true);
	}

	private ActionListener newAction()
	{
		ActionListener AL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				visitSite();
			}
		};
		return AL;
	}

	private class GradientBackgroundPanel extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g)
		{
			if (!isOpaque())
			{
				super.paintComponent(g);
				return;
			}

			Graphics2D g2d = (Graphics2D) g;
			int w = getWidth();
			int h = getHeight();
			Color color1 = getBackground().brighter();
			Color color2 = getBackground();

			// Paint a gradient from top to bottom
			GradientPaint gp = new GradientPaint(0, 0, color1, 0, h / 2, color2);

			g2d.setPaint(gp);
			g2d.fillRect(0, 0, w, h);

			setOpaque(false);
			super.paintComponent(g);
			setOpaque(true);
		}

	}
	// Arrange
	/*
	 * layout.setHorizontalGroup( layout.createSequentialGroup()
	 * .addContainerGap() .addComponent(logo)
	 * .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	 * .addComponent(contentPane) .addContainerGap() ); layout.setVerticalGroup(
	 * layout.createSequentialGroup() .addContainerGap()
	 * .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	 * .addComponent(logo) .addComponent(contentPane) ) .addContainerGap() );
	 */

	void visitSite()
	{
		try
		{
			Desktop.getDesktop().browse(new URI(homepageURL ));
		} 
		catch (IOException e)
		{
		} 
		catch (URISyntaxException e)
		{
		}
	}
}
