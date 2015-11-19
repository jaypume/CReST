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
/**
 * @author Alex Sheppard
 */

package sim.physical;

import java.util.ArrayList;

public class Software
{
	private ArrayList<String> mSoftware = new ArrayList <String>();
	private ArrayList<Double> mPrice = new ArrayList<Double>();
	
	public Software()
	{
		
	}
	
	/**
	 * Adds the input software name and price to the software installed on the server
	 * 
	 * @param softwareName The software name installed
	 * @param softwarePrice The price/license cost (if at all, otherwise use 0)
	 */
	public void addSoftware(String softwareName, Double softwarePrice)
	{
		mSoftware.add(softwareName);
		mPrice.add(softwarePrice);
	}
	
	/**
	 * Returns a list of software installed on the server
	 * 
	 * @return a list of software installed on the server
	 */
	public String toString()
	{
		String string = "";

		int i;
		for (i = 0; i < mSoftware.size(); i++)
		{
			if (i != mSoftware.size()-1)
			{
				string += mSoftware.get(i) + "- £" + mPrice.get(i) + ", ";
			}
			else
			{
				string += mSoftware.get(i) + "- £" + mPrice.get(i);
			}
		}
		
		if (mSoftware.size() == 0)
		{
			string = "[None]";
		}

		return string;
	}
	
	/**
	 * Calculates the cost of the software on the server
	 *
	 * @return The cost for the software on this server
	 */
	public double getCost()
	{
		double total = 0;
		
		for (int i = 0; i < mPrice.size(); i++)
		{
			total += mPrice.get(i);
		}
		
		return total;
	}
}