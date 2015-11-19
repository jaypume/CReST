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
package builder.prefs;

import java.awt.*;
import java.util.prefs.*;

import javax.swing.ImageIcon;

public class BuilderPreferences
{
	// Java Preferances API key names
	private static final String CURRENCY_ID = "Currency";
	private static final String SHOW_WELCOME_SCREEN_ID = "ShowWelcomeScreen";
	private static final String JAVA_COMMAND = "javaCommand";
	private static final String JAVA_ARGS = "javaArgs";
	private static final String JAR_LOCATION = "jarLocation";
	private static final String TOOLBAR_LAYOUT = "toolbarLayout";
	
	
	private final Preferences mPrefs;
	
	private String mResourcePath;
	private String mProgramName;

	// Creates the default options file
	public BuilderPreferences()
	{
		// These are effectively static values, we don't want them changing from
		// a user preferences file
		mResourcePath = "resources/";
		mProgramName = "CReST Builder";
		
		// Load preferences
		mPrefs = Preferences.userNodeForPackage(this.getClass());
	}

	public String getResourcePath()
	{
		return mResourcePath;
	}

	public String getImgResourcePath()
	{
		return getResourcePath() + "img/";
	}

	public String getIconResourcePath()
	{
		return getResourcePath() + "img/icons/";
	}

	public Image getProgramIcon()
	{
		return new ImageIcon(getIconResourcePath() + "logo.png").getImage();
	}
	
	public String getProgramName()
	{
		return mProgramName;
	}

	public String getCurrency()
	{
		return mPrefs.get(CURRENCY_ID, "£");
	}
	
	public boolean getShowWelcomeScreen()
	{
		return mPrefs.getBoolean(SHOW_WELCOME_SCREEN_ID, true);
	}
	
	public void setShowWelcomeScreen(boolean value)
	{
		mPrefs.putBoolean(SHOW_WELCOME_SCREEN_ID, value);
	}
	
	public String getToolBarLayout()
	{
		return mPrefs.get(TOOLBAR_LAYOUT, "new;open;save;!;run;!;gen");
	}
	public void setToolBarLayout(String value)
	{
		mPrefs.put(TOOLBAR_LAYOUT, value);
	}
	public String getJavaCommand()
	{
		return mPrefs.get(JAVA_COMMAND, "java");
	}
	
	public void setJavaCommand(String value)
	{
		mPrefs.put(JAVA_COMMAND, value);
	}
	
	public String getJavaArgs()
	{
		return mPrefs.get(JAVA_ARGS, "-Xmx1000m");
	}
	
	public void setJavaArgs(String value)
	{
		mPrefs.put(JAVA_ARGS, value);
	}
	
	public String getJarFileLocation()
	{
		return mPrefs.get(JAR_LOCATION, "dist/CReST-app/CReST.jar");
	}
	
	public void setJarFileLocation(String value)
	{
		mPrefs.put(JAR_LOCATION, value);
	}

	public ImageIcon getImage(String name)
	{
		return new ImageIcon(getIconResourcePath() + name);
	}
	
}
