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
package config;

import java.util.List;

import config.physical.ConfigServerType;
import config.physical.ConfigWorld;

/**
 * This class represents a mutable configuration that the Editor should use
 * @author James Laverack
 *
 */

public class EditorConfiguration
{
	private ConfigWorld confWorld;
	private String name;
	private SettingsManager config;
	private List<ConfigServerType> replacements;

	public EditorConfiguration(String pName, ConfigWorld pWorld, SettingsManager pConfig, List<ConfigServerType> pReplace)
	{
		confWorld = pWorld;
		name = pName;
		config = pConfig;
		replacements = pReplace;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public ConfigWorld getConfWorld()
	{
		return confWorld;
	}
	public SettingsManager getConfig()
	{
		return config;
	}
	public List<ConfigServerType> getReplacements()
	{
		return replacements;
	}
	
}
