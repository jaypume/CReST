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
package config.physical;

import java.util.*;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

import builder.datacentre.DatacentreListChangedListener;

public class ConfigWorld extends Element
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mNetworkDistance;
	private List<ConfigDatacentre> dcs = new ArrayList<ConfigDatacentre>();
	private List<DatacentreListChangedListener> listeners = new ArrayList<DatacentreListChangedListener>();

	// Defaults
	public static final int DEFAULT_NETWORK_DISTANCE = 1000;

	/**
	 * Constructor creates default values
	 */
	public ConfigWorld()
	{
		mNetworkDistance = DEFAULT_NETWORK_DISTANCE;
	}
	@Override
	public List<Attribute> getAttributes()
	{
		List<Attribute> list = new ArrayList<Attribute>();
		list.add(new Attribute("networkDistance", String
				.valueOf(mNetworkDistance)));
		return list;
	}

	public List<Element> getChildren()
	{
		List<Element> list = new ArrayList<Element>();
		list.addAll(dcs);
		return list;
	}
	public List<?> getContent()
	{
		return getChildren();
	}
	public String getQualifiedName()
	{
		return "world";
	}
	public String getName()
	{
		return "world";
	}
	public Namespace getNamespace()
	{
		return Namespace.NO_NAMESPACE;
	}

	public int getNetworkDistance()
	{
		return mNetworkDistance;
	}

	public void setNetworkDistance(int pNetworkDistance)
	{
		mNetworkDistance = pNetworkDistance;
	}
	
	public void addDatacentre(ConfigDatacentre pDC)
	{
		dcs.add(pDC);
		updatedList();
	}

	public void setDatacentre(ConfigDatacentre pDC, int i)
	{
		dcs.set(i, pDC);
		updatedList();
	}
	
	public void removeDatacentre(ConfigDatacentre pDC)
	{
		dcs.remove(pDC);
		updatedList();
	}
	
	public ConfigDatacentre[] getDCs()
	{
		return dcs.toArray(new ConfigDatacentre[dcs.size()]);
	}

	/**
	 * Gets the names of all the child datacentres.
	 * 
	 * @return array of strings that is the names of all the datacentres.
	 */
	public String[] getDatacentreNames()
    {
    	String[] names = new String[dcs.size()];
    	
    	for(int i=0;i<dcs.size();i++)
    	{
    		names[i] = dcs.get(i).getDatacentreName();
    	}
    	
    	return names;
    }
	// ///////////////////////////// //
	// DATACENTRE LIST LISTENER CODE //
	// ///////////////////////////// //

	/**
	 * Adds a listener to the list, this will be updated whenever a datacentre
	 * (or rather, a ConfigDatacentre object) is removed or added to this world.
	 * 
	 * @param listen
	 */
	public void addDCListListener(DatacentreListChangedListener listen)
	{
		listeners.add(listen);
	}

	/**
	 * Removes a listener from the registered list. 
	 * 
	 * @param listen
	 */
	public void removeDCListListener(DatacentreListChangedListener listen)
	{
		listeners.remove(listen);
	}

	/**
	 * Called internally when the list of Datacenteres is updated, this triggers
	 * events off to all the currently registered listeners.
	 */
	private void updatedList()
	{
		ConfigDatacentre array[] = dcs
				.toArray(new ConfigDatacentre[dcs.size()]);
		for (DatacentreListChangedListener listen : listeners)
		{
			listen.listUpdated(array);
		}
	}
}
