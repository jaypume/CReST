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

package builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import builder.prefs.BuilderPreferences;
import builder.prefs.PreferencesWindow;

/**
 * The toolbar that we use in the main window. It could be created twice if you
 * wanted to make multiple toolbars, but this is not recommended.
 * 
 * @author james
 * 
 */
public class MainToolBar extends JToolBar implements ActionListener
{

	private static final long serialVersionUID = 1L;

	public static final String[] BUTTON_NAMES = new String[]
	{ "---Seperator---", "New", "Open", "Save", "Close", "Run",
			"Run Preferences", "Quit", "Rename", "Preferences", "Help",
			"About"/*, "Generate Datacentre"*/ };
	public static final String[] BUTTON_VALUES = new String[]
	{ "!", "new", "open", "save", "close", "run", "runPrefs", "quit", "rename",
			"preferences", "help", "about"/*, "gen"*/ };

	java.util.List<JButton> newButton = new ArrayList<JButton>();
	java.util.List<JButton> openButton = new ArrayList<JButton>();
	java.util.List<JButton> saveButton = new ArrayList<JButton>();
	java.util.List<JButton> closeButton = new ArrayList<JButton>();
	java.util.List<JButton> runButton = new ArrayList<JButton>();
	java.util.List<JButton> runPrefsButton = new ArrayList<JButton>();
	java.util.List<JButton> quitButton = new ArrayList<JButton>();
	java.util.List<JButton> renameButton = new ArrayList<JButton>();
	java.util.List<JButton> preferencesButton = new ArrayList<JButton>();
	java.util.List<JButton> helpButton = new ArrayList<JButton>();
	java.util.List<JButton> aboutButton = new ArrayList<JButton>();
//	java.util.List<JButton> genButton = new ArrayList<JButton>();

	BuilderPreferences mPreferences;
	MainWindow mWindow;

	public static String buttonNamesToValues(String s)
	{
		for (int i = 0; i < BUTTON_NAMES.length; i++)
		{
			if (BUTTON_NAMES[i].equals(s))
			{
				return BUTTON_VALUES[i];
			}
		}
		return "";
	}

	public static String[] buttonNamesToValues(String[] in)
	{
		String[] returns = new String[in.length];
		for (int i = 0; i < in.length; i++)
		{
			returns[i] = buttonNamesToValues(in[i]);
		}
		return returns;
	}

	public static String buttonValuesToNames(String s)
	{
		for (int i = 0; i < BUTTON_NAMES.length; i++)
		{
			if (BUTTON_VALUES[i].equals(s))
			{
				return BUTTON_NAMES[i];
			}
		}
		return "";
	}

	public static String[] buttonValuesToNames(String[] in)
	{
		String[] returns = new String[in.length];
		for (int i = 0; i < in.length; i++)
		{
			returns[i] = buttonValuesToNames(in[i]);
		}
		return returns;
	}

	/**
	 * Populates the toolbar with the buttons, and creates the approrpate
	 * JButton objects.
	 */
	public MainToolBar(BuilderPreferences pPrefs, MainWindow pWindow)
	{
		mPreferences = pPrefs;
		mWindow = pWindow;
		populate();
	}

	public void populate()
	{
		this.removeAll();
		// Get string
		String layout = mPreferences.getToolBarLayout();
		String[] elements = layout.split(";");
		for (String e : elements)
		{
			JButton but = null;

			// Process what this is
			if (e.equals("new"))
			{
				but = createButton("new.png", "New Configuration");
				newButton.add(but);
			} else if (e.equals("open"))
			{
				but = createButton("open.png", "Open Existing Configuration");
				openButton.add(but);
			} else if (e.equals("save"))
			{
				but = createButton("save.png", "Save Configuration");
				saveButton.add(but);
			} else if (e.equals("close"))
			{
				but = createButton("close.png", "Close Configuration");
				closeButton.add(but);
			} else if (e.equals("run"))
			{
				but = createButton("run.png", "Run Simulation");
				runButton.add(but);
			} else if (e.equals("runPrefs"))
			{
				but = createButton("run_prefs.png", "Edit Run Settings");
				runPrefsButton.add(but);
			} else if (e.equals("quit"))
			{
				but = createButton("quit.png", "Quit");
				quitButton.add(but);
			} else if (e.equals("rename"))
			{
				but = createButton("config_rename.png", "Rename Configuration");
				renameButton.add(but);
			} else if (e.equals("preferences"))
			{
				but = createButton("preferences.png", "Editor Preferences");
				preferencesButton.add(but);
			} else if (e.equals("help"))
			{
				but = createButton("help.png", "Help");
				helpButton.add(but);
			} else if (e.equals("about"))
			{
				but = createButton("about.png", "About");
				aboutButton.add(but);
//			} else if (e.equals("gen"))
//			{
//				but = createButton("datacentre_gen.png", "Generate Datacentre");
//				genButton.add(but);
			} else if (e.equals("!"))
			{
				add(createSeperator());
			}
			// Check for Jbutton
			if (but != null)
			{
				add(but);
			}
		}
	}

	private JButton createButton(String imageName, String toolTip)
	{
		JButton but = new JButton();
		but.setIcon(new ImageIcon(new ImageIcon(mPreferences
				.getIconResourcePath() + imageName).getImage()));
		but.addActionListener(this);
		but.setToolTipText(toolTip);
		return but;
	}

	private Component createSeperator()
	{
		return Box.createRigidArea(new Dimension(5, 5));
	}

	/**
	 * Called when the state of the editor has changed, typicly when a new file
	 * has been loaded
	 */
	public void editorUpdated()
	{
		for (JButton b : runButton)
		{
			b.setEnabled(mWindow.isRunOptionOn());
		}
		for (JButton b : saveButton)
		{
			b.setEnabled(mWindow.isLoaded());
		}
		for (JButton b : closeButton)
		{
			b.setEnabled(mWindow.isLoaded());
		}
//		for (JButton b : genButton)
//		{
//			b.setEnabled(mWindow.isLoaded());
//		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Listen to events
		Object source = e.getSource();
		// Test for each source
		if (newButton.contains(source))
		{
			mWindow.newGUIOption(mWindow.getFrame());
		} else if (openButton.contains(source))
		{
			mWindow.openGUIOption();
		} else if (saveButton.contains(source))
		{
			mWindow.saveGUIOption();
		} else if (closeButton.contains(source))
		{
			mWindow.closeGUIOption();
		} else if (runButton.contains(source))
		{
			mWindow.runGUIOption();
		} else if (runPrefsButton.contains(source))
		{
			mWindow.preferencesGUIOption(PreferencesWindow.RUN_PREFS_NAME);
		} else if (quitButton.contains(source))
		{
			mWindow.closeGUIOption();
		} else if (renameButton.contains(source))
		{
			mWindow.renameGUIOption();
		} else if (preferencesButton.contains(source))
		{
			mWindow.preferencesGUIOption();
		} else if (helpButton.contains(source))
		{
			mWindow.helpGUIOption();
		} else if (aboutButton.contains(source))
		{
			mWindow.aboutGUIOption();
//		} else if (genButton.contains(source))
//		{
//			new DCGenWindow(mWindow, mPreferences);
		}
	}

}
