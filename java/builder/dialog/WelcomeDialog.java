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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import builder.MainWindow;
import builder.prefs.BuilderPreferences;

public class WelcomeDialog extends JDialog
{
	MainWindow mParent;
	BuilderPreferences mPrefs;
	ActionListener aL = newAction();

	private JButton mNewButton;
	private JButton mOpenButton;
	private JButton mGoToWorkspaceButton;
	private JCheckBox mShowEveryTimeButton;

	private static final long serialVersionUID = -5581641302513050849L;

	/**
	 * The Constructor attaches it (with 'modal' behaviour enabled) to the
	 * MainWindow.
	 * 
	 * @param parent
	 *            the JFrame that we are to attach to
	 */
	public WelcomeDialog(JFrame parent, MainWindow w, BuilderPreferences prefs)
	{
		super(parent, true);	
		mParent = w;
		mPrefs = prefs;
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Welcome To " + mPrefs.getProgramName());
		// Setup two JPanels
		this.setLayout(new BorderLayout());
		// Buttons
		JPanel buttonHolderPanel = new JPanel();
		buttonHolderPanel.add(setupButtonPanel());
		this.add(buttonHolderPanel, BorderLayout.CENTER);
		// Previous configurations
		//this.add(setupPreviousConfigPanel(), BorderLayout.CENTER);
		// Show every time?
		this.add(setupShowEveryTimePanel(), BorderLayout.SOUTH);
		
		// Do final setup and display to the user
		setSize(new Dimension(350, 200));
		this.setLocationRelativeTo(parent);
		setVisible(true);
	}

	private JPanel setupShowEveryTimePanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		mShowEveryTimeButton = new JCheckBox("Show on startup", mPrefs.getShowWelcomeScreen());
		mShowEveryTimeButton.addActionListener(aL);
		pan.setBorder(new EmptyBorder(0,10,10,10));
		pan.add(mShowEveryTimeButton, BorderLayout.WEST);
		return pan;
	}
	
	protected JPanel setupPreviousConfigPanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		// Add Label
		JLabel lab = new JLabel("Load a recent configuration");
		lab.setBorder(new EmptyBorder(0,0,10,0)); // pad it on the bottom
		pan.add(lab, BorderLayout.NORTH);
		// Last config panel
		String[] configs = {"Test 1", "Test 2", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas", "Satsumas"};
		JPanel holdingPanel = setupListOfPreviousConfigs(configs);
		//holdingPanel.add();
		//holdingPanel.setSize(100, 1000);
		//holdingPanel.setPreferredSize(new Dimension(100,0));
		holdingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		// Add Scroll pane
		JScrollPane scroll = new JScrollPane(holdingPanel);
		scroll.setOpaque(false);
		pan.add(scroll, BorderLayout.CENTER);
		// Last padding and return!
		pan.setBorder(new javax.swing.border.EmptyBorder(10, 5, 10, 10));
		return pan;
	}
	
	private JPanel setupListOfPreviousConfigs(String[] prevConfig)
	{
		JPanel listPan = new JPanel();
		GridLayout lay = new GridLayout(prevConfig.length, 0);
		lay.setVgap(10);
		listPan.setLayout(lay);
		for(String s : prevConfig)
		{
			JButton button = new JButton(s);
			listPan.add(button);
		}
		
		return listPan;
	}
	
	/**
	 * Creates a JPanel populated with the buttons that we want to use. Note
	 * that this is not safe to run multiple times as it uses member variables
	 * to store references to the buttons themselves.
	 * 
	 * @return the populated JPanel.
	 */
	private JPanel setupButtonPanel()
	{
		JPanel pan = new JPanel();
		GridLayout layout = new GridLayout(3,0);
		layout.setVgap(10);
		pan.setLayout(layout);
		
		// Create buttons
		mNewButton = new JButton("New Configuration");
		mNewButton.addActionListener(aL);
		mOpenButton = new JButton("Open Configuration");
		mOpenButton.addActionListener(aL);
		mGoToWorkspaceButton = new JButton("Go To Workspace");
		mGoToWorkspaceButton.addActionListener(aL);
				
		// Arrange
		pan.add(mNewButton);
		pan.add(mOpenButton);
		pan.add(mGoToWorkspaceButton);
		pan.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 5));
		return pan;
	}
	
	private ActionListener newAction()
	{
		ActionListener AL = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if (source.equals(mNewButton))
				{
					newConfig();
				}
				else if (source.equals(mOpenButton))
				{
					openConfig();
				}
				else if (source.equals(mGoToWorkspaceButton))
				{
					gotoWorkbench();
				}else if (source.equals(mShowEveryTimeButton))
				{
					mPrefs.setShowWelcomeScreen(mShowEveryTimeButton.isSelected());
				}
			}
		};
		return AL;
	}
	
	private void newConfig()
	{
		mParent.newGUIOption(this);
	}
	
	private void gotoWorkbench()
	{
		super.dispose();
	}
	
	private void openConfig()
	{
		mParent.openGUIOption();
		super.dispose();
	}
}